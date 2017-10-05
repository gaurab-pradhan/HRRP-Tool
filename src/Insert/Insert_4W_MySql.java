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
//        path = PropertiesUtil.getDatasetPath();
//         tablename = "tbl_hrrp_4w";
//        String path = PropertiesUtil.getDatasetPath();
//        DatabaseMetaData metadata = con.getMetaData();
//        ResultSet resultSet;
//        resultSet = metadata.getTables(null, null, tablename, null);
//        if (!resultSet.next()) {
        // next() checks if the next table exists ...
//            String query = "CREATE TABLE IF NOT EXISTS " + tablename + " (\n"
//                    + "  `sn` int(10) DEFAULT NULL,\n"
//                    + "  `district` varchar(50) DEFAULT NULL,\n"
//                    + "  `vdc` varchar(50) DEFAULT NULL,\n"
//                    + "  `ward` varchar(100) DEFAULT NULL,\n"
//                    + "  `po` varchar(100) DEFAULT NULL,\n"
//                    + "  `impl_partner` varchar(100) DEFAULT NULL,\n"
//                    + "  `FundingOrg` varchar(100) DEFAULT NULL,\n"
//                    + "  `act_type` varchar(100) DEFAULT NULL,\n"
//                    + "  `act_sub_type` varchar(100) DEFAULT NULL,\n"
//                    + "  `act_name` varchar(100) DEFAULT NULL,\n"
//                    + "  `act_detail` varchar(1000) DEFAULT NULL,\n"
//                    + "  `units` varchar(50) DEFAULT NULL,\n"
//                    + "  `fund_status` varchar(3) DEFAULT NULL,\n"
//                    + "  `act_status` varchar(15) DEFAULT NULL,\n"
//                    + "  `total_planned` varchar(10) DEFAULT NULL,\n"
//                    + "  `total_reached` varchar(10) DEFAULT NULL,\n"
//                    + "  `start_date` varchar(10) DEFAULT NULL,\n"
//                    + "  `end_date` varchar(10) DEFAULT NULL,\n"
//                    + "  `contact_name` varchar(50) DEFAULT NULL,\n"
//                    + "  `contact_number` varchar(50) DEFAULT NULL,\n"
//                    + "  `email` varchar(100) DEFAULT NULL,\n"
//                    + "  `comments` varchar(1000) DEFAULT NULL,\n"
//                    + "  `HRRP_VDC_Code` varchar(50) DEFAULT NULL,\n"
//                    + "  `HRRP_Ward_Code` varchar(50) DEFAULT NULL,\n"
//                    + "  `act_code` varchar(50) DEFAULT NULL\n"
//                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
//            stmt = con.createStatement();
//            stmt.executeUpdate(query);
//        path = "D:/Gaurab/03_Programming/170910_4W_National_R40.csv";

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
