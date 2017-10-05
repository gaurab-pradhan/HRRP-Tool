/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import static Util.DBUtil.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import org.apache.log4j.Logger;

/**
 *
 * @author Gaurab.Pradhan
 */
public class STVT_Target {

    private static Logger log = Logger.getLogger(STVT_Target.class.getName());
    String district;

    public void st_vt(String dis) throws SQLException {
       log.info("Inserting Short Training and Vocational Training Data");
        district = dis;
        Connection con = getConnectionSQLite();
        createTable(con, "tbl_st_target");
        createTable(con, "tbl_vt_target");
        insertData(con, "tbl_st_target");
        insertData(con, "tbl_vt_target");
        if (con != null) {
            con.close();
        }
    }

    void createTable(Connection con, String tableName) {
        Statement stmt = null;
        String query = "CREATE TABLE IF NOT EXISTS " + tableName + " (\n"
                + "  `HRRP_DNAME` varchar(14) DEFAULT NULL,\n"
                + "  `HRRP_VCODE` varchar(17) DEFAULT NULL,\n"
                + "  `HRRP_VNAME` varchar(24) DEFAULT NULL,\n"
                + "  `Eligible` int(4) DEFAULT NULL,\n"
                + "  `Target` int(3) DEFAULT NULL,\n"
                + "  `Ward_Count` int(2) DEFAULT NULL,\n"
                + "  `Ward_Target` int(2) DEFAULT NULL\n"
                + ");";
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
        }
    }

    void insertData(Connection con, String tableName) {
        Statement stmt = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Connection mySQL = DBUtil.getConnectionMySQL();
        try {
            String query = "INSERT INTO " + tableName + " (`HRRP_DNAME`, `HRRP_VCODE`, `HRRP_VNAME`, `Eligible`, `Target`, `Ward_Count`, `Ward_Target`)"
                    + " VALUES(?,?,?,?,?,?,?)";

            con.setAutoCommit(false);
            ps = con.prepareStatement(query);
            //delete data from table in SQLITE
            con.createStatement().execute("DELETE FROM " + tableName);

            final int batchSize = 1000;
            int count = 0;
//            Date date = null;

            stmt = mySQL.createStatement();
            String select = "Select * from " + tableName + " Where HRRP_DNAME = '" + district + "'"; // select data from MYSQL database
            if (district.toLowerCase().equals("kathmandu valley")) {
                select = "Select * from " + tableName + " Where HRRP_DNAME = 'Bhaktapur' or HRRP_DNAME = 'Lalitpur' or HRRP_DNAME = 'Kathmandu'";
            }
            if (district.toLowerCase().equals("national")) {
                select = "Select * from " + tableName;
            }
            rs = stmt.executeQuery(select);
            while (rs.next()) {
                ps.setString(1, rs.getString(1));
                ps.setString(2, rs.getString(2));
                ps.setString(3, rs.getString(3));
                ps.setString(4, rs.getString(4));
                ps.setString(5, rs.getString(5));
                ps.setString(6, rs.getString(6));
                ps.setString(7, rs.getString(7));

                ps.addBatch();
                if (++count % batchSize == 0) {
                    ps.executeBatch();
                }
            }
            int[] check = ps.executeBatch(); // insert remaining records
            log.info("Number of Rows inserted: " + check.length + " " + tableName);
            con.commit();
            rs.close();
            stmt.close();
            ps.close();
            mySQL.close();
        } catch (SQLException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
        }
    }
}
