package DatasetGenerator;

import Util.DBUtil;
import Util.PropertiesUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *
 * @author Gaurab.Pradhan
 */
public class DistrictProfile {

    static String template = PropertiesUtil.getTemplate_dp();
    static String output = PropertiesUtil.getOutput() + "\\DP";
    static String tableName = "tbl_hrrp_4w";
    private static Logger log = Logger.getLogger(DistrictProfile.class.getName());

    public static void main() {
        Statement stmt = null;

        File file = new File(output);
        String path = null;
        if (!file.exists()) {
            if (file.mkdirs()) {
                System.out.println("Directory is created!");
                path = file.getAbsolutePath();
            } else {
                System.out.println("Failed to create directory!");
            }
        } else {
            path = file.getAbsolutePath();
        }
        try {
            if (path != null) {
                PropertiesUtil.loadPropertiesFile();
                Connection con = DBUtil.getConnectionMySQL();
                writeToExcel(con, path);
                con.close();
            } else {
                System.out.println("Unable to create directory!");
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
        }
    }

    private static void writeToExcel(Connection con, String outputPath) throws Exception {
        Statement stmt = null;

        if (con != null) {
            if (template != null) {
                InputStream file = new FileInputStream(template);
                Workbook workbook = WorkbookFactory.create(file);
//                int rowIndex = 1;
                System.out.println("Generating dataset for DP");
                String query = null;
                query = "select district,po,group_concat(distinct impl_partner separator ',') AS ip "
                        + "from " + tableName + "\n"
                        + "Where act_code != 'OT OT 001' \n"
                        + "GROUP BY district,po";
                stmt = con.createStatement();

                writePOIP(stmt, query, workbook);

                writeSTVT(stmt, workbook);
                stmt.close();

                //ST-VT
//                sheet = workbook.getSheet("ST_VT");
//                rowIndex = 1;
                //Writing into excel file
                DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
                Calendar cal = Calendar.getInstance();
                String todayDate = dateFormat.format(cal.getTime());

                String fname = "DP-Data-" + todayDate;
                FileOutputStream fos = new FileOutputStream(outputPath + "/" + fname + ".xlsx");
                workbook.write(fos);
                System.out.println(outputPath + "/" + fname + ".xlsx created");
                fos.close();
                workbook.close();
            } else {
                System.out.println("Unable to find template for DP");
            }
        } else {
            System.out.println("DB connection is Null");
        }
    }

    private static void writePOIP(Statement stmt, String query, Workbook workbook) throws SQLException {
        org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheet("PO_IP");
        ResultSet rs = null;
        rs = stmt.executeQuery(query);
        int rowIndex = 1;
        int i = 0;
        CellStyle cellStyle1 = null;
        CellStyle cellStyle2 = null;
        while (rs.next()) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                row = sheet.createRow(rowIndex);
            }
            row.createCell(0).setCellValue(rs.getString(1));
            String po = rs.getString(2).trim();
            row.createCell(1).setCellValue(po.toUpperCase());
            String ip = rs.getString(3);
            if (ip.toLowerCase().contains(po.toLowerCase())) {
                ip = ip.replaceAll(po, "");
            }
            if (ip.startsWith(",")) {
                ip = ip.replaceFirst(",", "");
            }
            if (ip.endsWith(",")) {
                ip = ip.substring(0, ip.lastIndexOf(","));
            }
            ip = ip.replaceAll(",,", "");
            row.createCell(2).setCellValue(ip.toUpperCase());
            int r = row.getRowNum();
            r++;

            if (i == 0) {
                cellStyle1 = row.getCell(4).getCellStyle();
                cellStyle2 = row.getCell(5).getCellStyle();
                row.getCell(4).setCellFormula("B" + r);
                row.getCell(5).setCellFormula("C" + r);
            } else {
                row.createCell(4).setCellFormula("B" + r);
                row.createCell(5).setCellFormula("C" + r);
                row.getCell(4).setCellStyle(cellStyle1);
                row.getCell(5).setCellStyle(cellStyle2);
            }
            i++;
            rowIndex++;
        }
    }

    private static void writeSTVT(Statement stmt, Workbook workbook) throws SQLException {
        org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheet("ST_VT");
        Iterator<Row> iterator = sheet.iterator();
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        while (iterator.hasNext()) {
            Row row = iterator.next();
            int rowIndex = row.getRowNum();
            if (rowIndex > 0) {
                String dis = row.getCell(0).getStringCellValue();
                String query1 = "SELECT district,SUM(total_reached) as reach "
                        + "FROM " + tableName + " \n"
                        + "WHERE district = '" + dis + "' and (act_code = 'TR ST 001' OR act_code = 'TR ST 002') \n"
                        + "GROUP BY district";
                rs1 = stmt.executeQuery(query1);
                if (rs1.next()) {
                    int a = rowIndex + 1;
                    String formula = ("B" + a) + "-" + ("C") + a;
                    row.createCell(2).setCellValue(Integer.parseInt(rs1.getString(2)));
                    row.createCell(3).setCellFormula(formula);
                }
                String query2 = "SELECT district,SUM(total_reached) as reach "
                        + "FROM " + tableName + " \n"
                        + "WHERE district = '" + dis + "' and (act_code = 'TR VT 001' OR act_code = 'TR VT 002') \n"
                        + "GROUP BY district";
                rs2 = stmt.executeQuery(query2);
                if (rs2.next()) {
                    int a = rowIndex + 1;
                    String formula = ("G" + a) + "-" + ("H") + a;
                    row.createCell(7).setCellValue(Integer.parseInt(rs2.getString(2)));
                    row.createCell(8).setCellFormula(formula);
                }
            }
        }
    }
}
