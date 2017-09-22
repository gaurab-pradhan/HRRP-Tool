package DatasetGenerator;

import java.io.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.log4j.Logger;

/**
 *
 * @author gpradhan
 */
public class WriteCSV {

    private static Logger log = Logger.getLogger(WriteCSV.class.getName());

    static void writeToCSV(Connection con, String path, String query, String header, String filename) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;
        DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
        Calendar cal = Calendar.getInstance();
        String todayDate = dateFormat.format(cal.getTime());

        String fname = todayDate + filename;
        BufferedWriter bw = null;
        try {
            File file = new File(path + "/" + fname);
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            bw = new BufferedWriter(fw);
            bw.write(header);
            bw.write("\n");
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (rs.next()) {
                int col = 1;
                String content = "";
                for (int i = 0; i < columnsNumber; i++) {
                    content = content + rs.getString(col) + ",";
                    col++;
                }
                bw.write(content);
                bw.write("\n");
            }
            bw.close();
            System.out.println(path + "/" + fname + " created");
        } catch (IOException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
        }
    }
}
