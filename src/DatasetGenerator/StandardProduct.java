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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javafx.scene.control.TextArea;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *
 * @author gpradhan
 */
public class StandardProduct {

    static String output = PropertiesUtil.getOutput() + "\\TA_Map_Data";
    static String template = null;
    static String tableName = "tbl_hrrp_4w";
    private static Logger log = Logger.getLogger(StandardProduct.class.getName());

    public static void main(String[] args) {

//        standardWard();
    }

    public static void standardWard(TextArea logText) {
        template = PropertiesUtil.getTemplate_ta_ward();
        File file = new File(output + "\\ward");
        String path = null;
        if (!file.exists()) {
            if (file.mkdirs()) {
                logText.appendText("Directory is created!\n");
                path = file.getAbsolutePath();
            } else {
                logText.appendText("Failed to create directory!\n");
            }
        } else {
            path = file.getAbsolutePath();
        }
        try {
            if (path != null) {
                if (template != null) {
                    InputStream in = new FileInputStream(template);
                    Workbook workbook = WorkbookFactory.create(in);
                    Sheet sheet = workbook.getSheet("00_Connect");
                    workbook.setForceFormulaRecalculation(true);
                    sheet = null;
                    PropertiesUtil.loadPropertiesFile();
                    Connection con = DBUtil.getConnectionSQLite();

//                    String headerST_VT = "District,VDC,Ward,HRRP_WARD_CODE,Target_Code,Planned,Reached,Per_Ach";
//                    String header = "District,VDC,Ward,HRRP_WARD_CODE";
                    //Community 
                    String queryCR = "SELECT district,vdc,ward,HRRP_Ward_Code \n"
                            + "FROM " + tableName + " \n"
                            + "where HRRP_Ward_Code  != '#N/A' and (act_code = 'TA CR 002' or act_code = 'TA CR 003' or act_code = 'TA HR 002' or act_code = 'TA HR 003') \n"
                            + "GROUP BY HRRP_Ward_Code";
                    sheet = workbook.getSheet("3_Orinetation");
                    workbook.setForceFormulaRecalculation(true);
                    writeToExcel(con, queryCR, sheet, path, workbook);
//                    WriteCSV.writeToCSV(con, path, queryCR, header, "_CH_Orientation.csv");
                    //Door to Door
                    String queryDtD = "SELECT district,vdc,ward,HRRP_Ward_Code \n"
                            + "FROM " + tableName + " \n"
                            + "where HRRP_Ward_Code != '#N/A' and (act_code = 'TA DD 002') \n"
                            + "GROUP BY HRRP_Ward_Code";
                    sheet = workbook.getSheet("2_D2D");
                    workbook.setForceFormulaRecalculation(true);
                    writeToExcel(con, queryDtD, sheet, path, workbook);
//                    WriteCSV.writeToCSV(con, path, queryDtD, header, "_DtD.csv");

                    //Demo Const
                    String queryDC = "SELECT district,vdc,ward,HRRP_Ward_Code \n"
                            + "FROM " + tableName + " \n"
                            + "where HRRP_Ward_Code != '#N/A' and (act_code = 'TA DC 001' OR act_code = 'TA DC 003') \n"
                            + "GROUP BY HRRP_Ward_Code";
                    sheet = workbook.getSheet("1_Demo");
                    workbook.setForceFormulaRecalculation(true);
                    writeToExcel(con, queryDC, sheet, path, workbook);
//                    WriteCSV.writeToCSV(con, path, queryDC, header, "_Demo.csv");

                    //Help Desk
                    String queryHD = "SELECT district,vdc,ward,HRRP_Ward_Code \n"
                            + "FROM " + tableName + " \n"
                            + "where HRRP_Ward_Code != '#N/A' and act_code = 'TA HD 001'\n"
                            + "GROUP BY HRRP_Ward_Code";
                    sheet = workbook.getSheet("4_Help");
                    workbook.setForceFormulaRecalculation(true);
                    writeToExcel(con, queryHD, sheet, path, workbook);
//                    WriteCSV.writeToCSV(con, path, queryHD, header, "_Help.csv");

                    //ST
                    String queryST = "SELECT a.district,a.vdc,a.ward,a.HRRP_Ward_Code,b.Ward_target,sum(a.total_planned) as Planned,sum(a.total_reached) as Reached,ROUND(MAX(sum(a.total_planned),sum(a.total_reached))*1.0/b.Ward_target,2) as per_Ach \n"
                            + "FROM " + tableName + " as a \n"
                            + "INNER JOIN tbl_st_target as b ON a.HRRP_VDC_code = b.HRRP_VCODE WHERE (a.act_code = 'TR ST 001' OR a.act_code = 'TR ST 002') AND HRRP_Ward_Code != '#N/A' \n"
                            + "GROUP BY a.HRRP_Ward_Code";
                    sheet = workbook.getSheet("5_ST");
                    workbook.setForceFormulaRecalculation(true);
                    writeToExcel(con, queryST, sheet, path, workbook);
//                    WriteCSV.writeToCSV(con, path, queryST, headerST_VT, "_ST.csv");

                    //VT
                    String queryVT = "SELECT a.district,a.vdc,a.ward,a.HRRP_Ward_Code,b.Ward_target,sum(a.total_planned) as Planned,sum(a.total_reached) as Reached,ROUND(MAX(sum(a.total_planned),sum(a.total_reached))*1.0/b.Ward_target,2) as per_Ach \n"
                            + "FROM " + tableName + " as a \n"
                            + "INNER JOIN tbl_vt_target as b ON a.HRRP_VDC_code = b.HRRP_VCODE \n"
                            + "WHERE (a.act_code = 'TR VT 001' OR a.act_code = 'TR VT 002') AND HRRP_Ward_Code != '#N/A' \n"
                            + "GROUP BY a.HRRP_Ward_Code";
                    sheet = workbook.getSheet("6_VT");
                    workbook.setForceFormulaRecalculation(true);
                    writeToExcel(con, queryVT, sheet, path, workbook);
//                    WriteCSV.writeToCSV(con, path, queryVT, headerST_VT, "_VT.csv");

                    //Reconstruction coordination committee
                    String queryRC = "SELECT district,vdc,ward,HRRP_Ward_Code \n"
                            + "FROM " + tableName + " \n"
                            + "where HRRP_Ward_Code != '#N/A' and act_code = 'TA RC 001'\n"
                            + "GROUP BY HRRP_Ward_Code";
                    sheet = workbook.getSheet("7_RC");
                    workbook.setForceFormulaRecalculation(true);
                    writeToExcel(con, queryRC, sheet, path, workbook);

                    con.close();

                    DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
                    Calendar cal = Calendar.getInstance();
                    String todayDate = dateFormat.format(cal.getTime());

                    String fname = "TA-MAP-Ward-Data-" + todayDate;
                    FileOutputStream fos = new FileOutputStream(path + "/" + fname + ".xlsx");
                    workbook.write(fos);
                    System.out.println(path + "/" + fname + ".xlsx created");
                    in.close();
                    fos.close();
                    workbook.close();
                }
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

    public static void standardVDC(TextArea logText) {
        template = PropertiesUtil.getTemplate_ta_vdc();
        File file = new File(output + "\\vdc");
        String path = null;
        if (!file.exists()) {
            if (file.mkdirs()) {
                logText.appendText("Directory is created!\n");
                path = file.getAbsolutePath();
            } else {
                logText.appendText("Failed to create directory!\n");
            }
        } else {
            path = file.getAbsolutePath();
        }
        try {
            if (path != null) {
                if (template != null) {
                    InputStream in = new FileInputStream(template);
                    Workbook workbook = WorkbookFactory.create(in);
                    Sheet sheet = workbook.getSheet("00_Connect");
                    workbook.setForceFormulaRecalculation(true);
                    sheet = null;
                    PropertiesUtil.loadPropertiesFile();
                    Connection con = DBUtil.getConnectionSQLite();
//                    String fname = null;
//                    String headerST_VT = "District,HRRP_VDC_CODE,VDC,Target_Code,Planned,Reached,Per_Ach";
//                    String header = "District,HRRP_VDC_CODE,VDC";

                    //Community 
                    String queryCR = "SELECT district,HRRP_VDC_Code,vdc\n"
                            + "FROM " + tableName + " \n"
                            + "where (act_code = 'TA CR 002' or act_code = 'TA CR 003' or act_code = 'TA HR 002' or act_code = 'TA HR 003') \n"
                            + "GROUP BY HRRP_VDC_Code";
                    sheet = workbook.getSheet("3_Orinetation");
                    workbook.setForceFormulaRecalculation(true);
                    writeToExcel(con, queryCR, sheet, path, workbook);
//                    WriteCSV.writeToCSV(con, path, queryCR, header, "_CH_Orientation.csv");
                    //Door to Door
                    String queryDtD = "SELECT district,HRRP_VDC_Code,vdc \n"
                            + "FROM " + tableName + " \n"
                            + "where (act_code = 'TA DD 002') \n"
                            + "GROUP BY HRRP_VDC_Code";
                    sheet = workbook.getSheet("2_D2D");
                    workbook.setForceFormulaRecalculation(true);
                    writeToExcel(con, queryDtD, sheet, path, workbook);
//                    WriteCSV.writeToCSV(con, path, queryDtD, header, "_DtD.csv");

                    //Demo Const
                    String queryDC = "SELECT district,HRRP_VDC_Code,vdc\n"
                            + "FROM " + tableName + " \n"
                            + "where act_code = 'TA DC 001' OR act_code = 'TA DC 003' \n"
                            + "GROUP BY HRRP_VDC_Code";
                    sheet = workbook.getSheet("1_Demo");
                    workbook.setForceFormulaRecalculation(true);
                    writeToExcel(con, queryDC, sheet, path, workbook);
//                    WriteCSV.writeToCSV(con, path, queryDC, header, "_Demo.csv");

                    //Help Desk
                    String queryHD = "SELECT district,HRRP_VDC_Code,vdc \n"
                            + "FROM " + tableName + " \n"
                            + "where act_code = 'TA HD 001'\n"
                            + "GROUP BY HRRP_VDC_Code";
                    sheet = workbook.getSheet("4_Help");
                    workbook.setForceFormulaRecalculation(true);
                    writeToExcel(con, queryHD, sheet, path, workbook);
//                    WriteCSV.writeToCSV(con, path, queryHD, header, "_Help.csv");

                    //ST
                    String queryST = "SELECT a.district,a.HRRP_VDC_Code,a.vdc,b.Target,sum(a.total_planned) as Planned,sum(a.total_reached) as Reached,ROUND(MAX(sum(a.total_planned),sum(a.total_reached))*1.0/b.Target,2) as per_Ach \n"
                            + "FROM " + tableName + " as a \n"
                            + "INNER JOIN tbl_st_target as b ON a.HRRP_VDC_Code = b.HRRP_VCODE WHERE (a.act_code = 'TR ST 001' OR a.act_code = 'TR ST 002') \n"
                            + "GROUP BY a.HRRP_VDC_Code";
                    sheet = workbook.getSheet("5_ST");
                    workbook.setForceFormulaRecalculation(true);
                    writeToExcel(con, queryST, sheet, path, workbook);
//                    WriteCSV.writeToCSV(con, path, queryST, headerST_VT, "_ST.csv");

                    //VT
                    String queryVT = "SELECT a.district,a.HRRP_VDC_Code,a.vdc,b.Target,sum(a.total_planned) as Planned,sum(a.total_reached) as Reached,ROUND(MAX(sum(a.total_planned),sum(a.total_reached))*1.0/b.Target,2) as per_Ach \n"
                            + "FROM " + tableName + " as a \n"
                            + "INNER JOIN tbl_vt_target as b ON a.HRRP_VDC_Code = b.HRRP_VCODE \n"
                            + "WHERE (a.act_code = 'TR VT 001' OR a.act_code = 'TR VT 002') \n"
                            + "GROUP BY a.HRRP_VDC_Code";
                    sheet = workbook.getSheet("6_VT");
                    workbook.setForceFormulaRecalculation(true);
                    writeToExcel(con, queryVT, sheet, path, workbook);
//                    WriteCSV.writeToCSV(con, path, queryVT, headerST_VT, "_VT.csv");

                    //Reconstruction coordination committee
                    String queryRC = "SELECT district,HRRP_VDC_Code,vdc \n"
                            + "FROM " + tableName + " \n"
                            + "where act_code = 'TA RC 001'\n"
                            + "GROUP BY HRRP_VDC_Code";
                    sheet = workbook.getSheet("7_RC");
                    workbook.setForceFormulaRecalculation(true);
                    writeToExcel(con, queryRC, sheet, path, workbook);

                    con.close();

                    DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
                    Calendar cal = Calendar.getInstance();
                    String todayDate = dateFormat.format(cal.getTime());

                    String fname = "TA-MAP-VDC-Data-" + todayDate;
                    FileOutputStream fos = new FileOutputStream(path + "/" + fname + ".xlsx");
                    workbook.write(fos);
                    System.out.println(path + "/" + fname + ".xlsx created");
                    in.close();
                    fos.close();
                    workbook.close();
                } else {
                    System.out.println("Unable to create directory!");
                }
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
        }
    }

    private static void writeToExcel(Connection con, String query, Sheet sheet, String path, Workbook wb) {
        Statement stmt = null;
        ResultSet rs = null;
        if (con != null) {
            try {
                stmt = con.createStatement();
                rs = stmt.executeQuery(query);
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnsNumber = rsmd.getColumnCount();
                int rowIndex = 1;
//                DataFormat format = wb.createDataFormat();

                while (rs.next()) {
                    Row row = sheet.getRow(rowIndex);
//                    CellStyle style = null;
                    Cell cell = null;
                    if (row == null) {
                        row = sheet.createRow(rowIndex);
                    }
//                     style = wb.createCellStyle();
                    for (int i = 0; i < columnsNumber; i++) {
                        String data = rs.getString(i + 1);
                        boolean flag = isNumeric(data);
                        cell = row.createCell(i);
                        row.createCell(i).setCellValue(data);

                        if (flag) {
//                            style.setDataFormat(format.getFormat("0.0"));
//                            cell.setCellStyle(style);
                            if (data.contains(".")) {
                                cell.setCellValue(Float.parseFloat(data));
                            } else {
                                cell.setCellValue(Integer.parseInt(data));
                            }
                        } else {
                            cell.setCellValue(data);
                        }
                    }
                    rowIndex++;
                }
                rs.close();
                stmt.close();
            } catch (SQLException ex) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                String exceptionText = sw.toString();
                log.error(exceptionText);
            }
        }

    }

    private static boolean isNumeric(String data) {
        return data.matches("(\\d+(?:\\.\\d+)?)");
    }
}
