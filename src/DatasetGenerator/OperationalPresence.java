package DatasetGenerator;

import Util.DBUtil;
import Util.PropertiesUtil;
import java.io.*;
import java.sql.*;
import javafx.scene.control.TextArea;
import org.apache.log4j.Logger;

/**
 *
 * @author gpradhan
 */
public class OperationalPresence {

//    static String template = PropertiesUtil.getTemplate_Tab();
    static String output = PropertiesUtil.getOutput();
    static String tableName = "tbl_hrrp_4w";
    private static Logger log = Logger.getLogger(OperationalPresence.class.getName());

    public static void main(TextArea logText) {

        File file = new File(output);
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
                PropertiesUtil.loadPropertiesFile();
                Connection con = DBUtil.getConnectionSQLite();
                String query = "SELECT district ,HRRP_VDC_CODE,vdc, COUNT(distinct(po)) count_po FROM " + tableName + " where act_type != 'Other' GROUP by district,HRRP_VDC_CODE,vdc ORDER BY district ASC";
                String fname = "_OperationalPresence.csv";
                String header = "District,HRRP_VDC_CODE,VDC, PO_COUNT";
                WriteCSV.writeToCSV(con, path, query, header, fname,logText);
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
}
