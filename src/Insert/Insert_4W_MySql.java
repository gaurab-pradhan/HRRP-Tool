package Insert;

import Runner.*;
import Util.DBUtil;
import Util.PropertiesUtil;
import java.sql.*;

/**
 *
 * @author gpradhan
 */
public class Insert_4W_MySql {

    public static void insertData(String tablename, String path) throws SQLException {
        PropertiesUtil.loadPropertiesFile();
        Statement stmt = null;
        Connection con = DBUtil.getConnectionMySQL();
        path = path.replaceAll("\\\\", "/");
        con.createStatement().execute("DELETE FROM " + tablename);
        stmt = null;
        String esquel = " LOAD DATA LOCAL INFILE '" + path + "' \n"
                + "INTO TABLE " + tablename + " \n"
                + " FIELDS TERMINATED BY \',\' \n"
                + "ENCLOSED BY \'\"'"
                + " LINES TERMINATED BY \'\\n\' "
                + "IGNORE 1 ROWS";

        stmt = con.createStatement();
        stmt.executeUpdate(esquel);

        String update = "UPDATE " + tablename + " SET act_code = REPLACE(REPLACE(act_code, '\\r', ''), '\\n', '')";
        stmt.executeUpdate(update);
        stmt.close();
        con.close();
//        } else {
//            System.out.println("Table Exits Already");
//        }
    }
}
