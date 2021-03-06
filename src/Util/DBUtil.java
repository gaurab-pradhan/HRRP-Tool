package Util;

import java.io.*;
import java.security.GeneralSecurityException;
import java.sql.*;
import javafx.scene.control.Alert;
import org.apache.log4j.*;

/**
 *
 * @author Gaurab.Pradhan
 */
public class DBUtil {

    private static String JDBC_CONNECTION_URL = "jdbc:sqlite:conf/hrrp_4w.db";
    private static Logger log = Logger.getLogger(DBUtil.class.getName());
    static Connection con = null;

    public static Connection getConnectionMySQL() throws GeneralSecurityException, IOException {
        String dbURL = "jdbc:mysql://" + Crypto.decrypt(PropertiesUtil.getLiveEnd()) + ":3306/";
        String dbName = Crypto.decrypt(PropertiesUtil.getLiveDbname());
        String username = Crypto.decrypt(PropertiesUtil.getLiveUser());
        String password = Crypto.decrypt(PropertiesUtil.getLivePass());
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(dbURL + dbName, username, password);
        } catch (ClassNotFoundException e) {
            System.out.println("MySql database driver class not found." + e);
            log.info("MySql database driver class not found." + e);
        } catch (SQLException e) {
            System.out.println("Unable to establish connection to MySql database." + e);
            log.info("Unable to establish connection to MySql database." + e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("Unable to establish connection to MySql database.com.mysql.jdbc.exceptions.jdbc4.CommunicationsException: Communications link failure\n");
            alert.showAndWait();
        }
        return connection;
    }

    public static Connection getConnectionNAS() throws GeneralSecurityException, IOException {
        Connection connection = null;
        String dbURLNAS = "jdbc:mysql://" + Crypto.decrypt(PropertiesUtil.getNasEnd()) + ":3306/";
        String dbNameNAS = Crypto.decrypt(PropertiesUtil.getNasDbname());
        String usernameNAS = Crypto.decrypt(PropertiesUtil.getNasUser());
        String passwordNAS = Crypto.decrypt(PropertiesUtil.getNasPass());
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(dbURLNAS + dbNameNAS, usernameNAS, passwordNAS);
        } catch (ClassNotFoundException e) {
            System.out.println("MySql database driver class not found." + e);
            log.info("MySql database driver class not found." + e);
        } catch (SQLException e) {
            System.out.println("Unable to establish connection to MySql database." + e);
            log.info("Unable to establish connection to MySql database." + e);
        }
        return connection;
    }

    public static Connection getConnectionSQLite() {
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(JDBC_CONNECTION_URL);

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
        }
        return connection;
    }

    public static void main(String[] args) throws Exception {
        con = getConnectionSQLite();
        createLocTbl();
        createHRRP_4wTbl();
        con.close();
//        createUsertbl();
    }

    private static void createLocTbl() {
        Statement stmt = null;
        try {
            String sql = "CREATE TABLE IF NOT EXISTS location (\n"
                    + "	loc text NOT NULL\n"
                    + ");";
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                }
            }
        }
    }

    public static String getLocation() {
        con = getConnectionSQLite();
        String location = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM location;");
            while (rs.next()) {
                location = rs.getString(1);
            }

        } catch (SQLException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                }
            }
            if (rs != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                }
            }
            if (con != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                }
            }
        }
        return location;
    }

    public static int insertLoc(String location) {
        int row = -1;
        con = getConnectionSQLite();
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            String sql = "INSERT INTO location (loc) "
                    + "VALUES ('" + location + "');";
            row = stmt.executeUpdate(sql);

        } catch (SQLException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
            ex.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                }
            }
            if (con != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                }
            }
        }
        return row;
    }

    private static void createHRRP_4wTbl() {
        Statement stmt = null;
        try {
            String sql = "CREATE TABLE IF NOT EXISTS tbl_hrrp_4w (\n"
                    + "`sn` int(10) DEFAULT NULL,\n"
                    + "`district` varchar(50) DEFAULT NULL,\n"
                    + "`vdc` varchar(50) DEFAULT NULL,\n"
                    + "`ward` varchar(100) DEFAULT NULL,\n"
                    + "`po` varchar(100) DEFAULT NULL,\n"
                    + "`impl_partner` varchar(100) DEFAULT NULL,\n"
                    + "`FundingOrg` varchar(100) DEFAULT NULL,\n"
                    + "`act_type` varchar(100) DEFAULT NULL,\n"
                    + "`act_sub_type` varchar(100) DEFAULT NULL,\n"
                    + "`act_name` varchar(100) DEFAULT NULL,\n"
                    + "`act_detail` varchar(1000) DEFAULT NULL,\n"
                    + "`units` varchar(50) DEFAULT NULL,\n"
                    + "`fund_status` varchar(3) DEFAULT NULL,\n"
                    + "`act_status` varchar(15) DEFAULT NULL,\n"
                    + "`total_planned` varchar(10) DEFAULT NULL,\n"
                    + "`total_reached` varchar(10) DEFAULT NULL,\n"
                    + "`start_date` varchar(10) DEFAULT NULL,\n"
                    + "`end_date` varchar(10) DEFAULT NULL,\n"
                    + "`contact_name` varchar(50) DEFAULT NULL,\n"
                    + "`contact_number` varchar(50) DEFAULT NULL,\n"
                    + "`email` varchar(100) DEFAULT NULL,\n"
                    + "`comments` varchar(1000) DEFAULT NULL,\n"
                    + "`HRRP_VDC_Code` varchar(50) DEFAULT NULL,\n"
                    + "`HRRP_Ward_Code` varchar(50) DEFAULT NULL,\n"
                    + "`act_code` varchar(50) DEFAULT NULL,\n"
                    + "`round` varchar(50) DEFAULT NULL\n"
                    + ");";
            stmt = con.createStatement();
            stmt.executeUpdate(sql);

            sql = "CREATE TABLE IF NOT EXISTS temp_hrrp_4w (\n"
                    + "`sn` int(10) DEFAULT NULL,\n"
                    + "`district` varchar(50) DEFAULT NULL,\n"
                    + "`vdc` varchar(50) DEFAULT NULL,\n"
                    + "`ward` varchar(100) DEFAULT NULL,\n"
                    + "`po` varchar(100) DEFAULT NULL,\n"
                    + "`impl_partner` varchar(100) DEFAULT NULL,\n"
                    + "`FundingOrg` varchar(100) DEFAULT NULL,\n"
                    + "`act_type` varchar(100) DEFAULT NULL,\n"
                    + "`act_sub_type` varchar(100) DEFAULT NULL,\n"
                    + "`act_name` varchar(100) DEFAULT NULL,\n"
                    + "`act_detail` varchar(1000) DEFAULT NULL,\n"
                    + "`units` varchar(50) DEFAULT NULL,\n"
                    + "`fund_status` varchar(3) DEFAULT NULL,\n"
                    + "`act_status` varchar(15) DEFAULT NULL,\n"
                    + "`total_planned` varchar(10) DEFAULT NULL,\n"
                    + "`total_reached` varchar(10) DEFAULT NULL,\n"
                    + "`start_date` varchar(10) DEFAULT NULL,\n"
                    + "`end_date` varchar(10) DEFAULT NULL,\n"
                    + "`contact_name` varchar(50) DEFAULT NULL,\n"
                    + "`contact_number` varchar(50) DEFAULT NULL,\n"
                    + "`email` varchar(100) DEFAULT NULL,\n"
                    + "`comments` varchar(1000) DEFAULT NULL,\n"
                    + "`HRRP_VDC_Code` varchar(50) DEFAULT NULL,\n"
                    + "`HRRP_Ward_Code` varchar(50) DEFAULT NULL,\n"
                    + "`act_code` varchar(50) DEFAULT NULL\n"
                    + ");";
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                }
            }
            if (con != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                }
            }
        }
    }

    public static void createTemptbl() {
        Statement stmt = null;
        try {
            String sql = "CREATE TABLE IF NOT EXISTS temp_hrrp_4w (\n"
                    + "`sn` int(10) DEFAULT NULL,\n"
                    + "`district` varchar(50) DEFAULT NULL,\n"
                    + "`vdc` varchar(50) DEFAULT NULL,\n"
                    + "`ward` varchar(100) DEFAULT NULL,\n"
                    + "`po` varchar(100) DEFAULT NULL,\n"
                    + "`impl_partner` varchar(100) DEFAULT NULL,\n"
                    + "`FundingOrg` varchar(100) DEFAULT NULL,\n"
                    + "`act_type` varchar(100) DEFAULT NULL,\n"
                    + "`act_sub_type` varchar(100) DEFAULT NULL,\n"
                    + "`act_name` varchar(100) DEFAULT NULL,\n"
                    + "`act_detail` varchar(1000) DEFAULT NULL,\n"
                    + "`units` varchar(50) DEFAULT NULL,\n"
                    + "`fund_status` varchar(3) DEFAULT NULL,\n"
                    + "`act_status` varchar(15) DEFAULT NULL,\n"
                    + "`total_planned` varchar(10) DEFAULT NULL,\n"
                    + "`total_reached` varchar(10) DEFAULT NULL,\n"
                    + "`start_date` varchar(10) DEFAULT NULL,\n"
                    + "`end_date` varchar(10) DEFAULT NULL,\n"
                    + "`contact_name` varchar(50) DEFAULT NULL,\n"
                    + "`contact_number` varchar(50) DEFAULT NULL,\n"
                    + "`email` varchar(100) DEFAULT NULL,\n"
                    + "`comments` varchar(1000) DEFAULT NULL,\n"
                    + "`HRRP_VDC_Code` varchar(50) DEFAULT NULL,\n"
                    + "`HRRP_Ward_Code` varchar(50) DEFAULT NULL,\n"
                    + "`act_code` varchar(50) DEFAULT NULL\n"
                    + ");";
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                }
            }
            if (con != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                }
            }
        }
    }

    public static void createUsertbl() {
        Statement stmt = null;
        try {
            String sql = "CREATE TABLE IF NOT EXISTS `user` (\n"
                    + "  `id` int(11) NOT NULL,\n"
                    + "  `uname` varchar(100) NOT NULL,\n"
                    + "  `password` varchar(100) NOT NULL,\n"
                    + "  `type` varchar(50) NOT NULL\n"
                    + ");";
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
            stmt.executeUpdate("INSERT INTO `user` (`id`, `uname`, `password`, `type`) VALUES\n"
                    + "(1, 'im.national@hrrpnepal.org', 'HRNepalRP20!7', 'Super Admin'),\n"
                    + "(2, 'national@hrrpnepal.org', 'HRNepalRP20!7', 'National');");
        } catch (SQLException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                }
            }
        }
    }

    public static void createHRRP_4wTbl_nas(Connection con, String tbaleName) {
        Statement stmt = null;
        try {
            String sql = "CREATE TABLE IF NOT EXISTS " + tbaleName + " (\n"
                    + "`sn` int(10) DEFAULT NULL,\n"
                    + "`district` varchar(50) DEFAULT NULL,\n"
                    + "`vdc` varchar(50) DEFAULT NULL,\n"
                    + "`ward` varchar(100) DEFAULT NULL,\n"
                    + "`po` varchar(100) DEFAULT NULL,\n"
                    + "`impl_partner` varchar(100) DEFAULT NULL,\n"
                    + "`FundingOrg` varchar(100) DEFAULT NULL,\n"
                    + "`act_type` varchar(100) DEFAULT NULL,\n"
                    + "`act_sub_type` varchar(100) DEFAULT NULL,\n"
                    + "`act_name` varchar(100) DEFAULT NULL,\n"
                    + "`act_detail` varchar(1000) DEFAULT NULL,\n"
                    + "`units` varchar(50) DEFAULT NULL,\n"
                    + "`fund_status` varchar(3) DEFAULT NULL,\n"
                    + "`act_status` varchar(15) DEFAULT NULL,\n"
                    + "`total_planned` varchar(10) DEFAULT NULL,\n"
                    + "`total_reached` varchar(10) DEFAULT NULL,\n"
                    + "`start_date` varchar(10) DEFAULT NULL,\n"
                    + "`end_date` varchar(10) DEFAULT NULL,\n"
                    + "`contact_name` varchar(50) DEFAULT NULL,\n"
                    + "`contact_number` varchar(50) DEFAULT NULL,\n"
                    + "`email` varchar(100) DEFAULT NULL,\n"
                    + "`comments` varchar(1000) DEFAULT NULL,\n"
                    + "`HRRP_VDC_Code` varchar(50) DEFAULT NULL,\n"
                    + "`HRRP_Ward_Code` varchar(50) DEFAULT NULL,\n"
                    + "`act_code` varchar(50) DEFAULT NULL\n"
                    + ");";
            stmt = con.createStatement();
            stmt.executeUpdate(sql);

            sql = "CREATE TABLE IF NOT EXISTS temp_hrrp_4w (\n"
                    + "`sn` int(10) DEFAULT NULL,\n"
                    + "`district` varchar(50) DEFAULT NULL,\n"
                    + "`vdc` varchar(50) DEFAULT NULL,\n"
                    + "`ward` varchar(100) DEFAULT NULL,\n"
                    + "`po` varchar(100) DEFAULT NULL,\n"
                    + "`impl_partner` varchar(100) DEFAULT NULL,\n"
                    + "`FundingOrg` varchar(100) DEFAULT NULL,\n"
                    + "`act_type` varchar(100) DEFAULT NULL,\n"
                    + "`act_sub_type` varchar(100) DEFAULT NULL,\n"
                    + "`act_name` varchar(100) DEFAULT NULL,\n"
                    + "`act_detail` varchar(1000) DEFAULT NULL,\n"
                    + "`units` varchar(50) DEFAULT NULL,\n"
                    + "`fund_status` varchar(3) DEFAULT NULL,\n"
                    + "`act_status` varchar(15) DEFAULT NULL,\n"
                    + "`total_planned` varchar(10) DEFAULT NULL,\n"
                    + "`total_reached` varchar(10) DEFAULT NULL,\n"
                    + "`start_date` varchar(10) DEFAULT NULL,\n"
                    + "`end_date` varchar(10) DEFAULT NULL,\n"
                    + "`contact_name` varchar(50) DEFAULT NULL,\n"
                    + "`contact_number` varchar(50) DEFAULT NULL,\n"
                    + "`email` varchar(100) DEFAULT NULL,\n"
                    + "`comments` varchar(1000) DEFAULT NULL,\n"
                    + "`HRRP_VDC_Code` varchar(50) DEFAULT NULL,\n"
                    + "`HRRP_Ward_Code` varchar(50) DEFAULT NULL,\n"
                    + "`act_code` varchar(50) DEFAULT NULL\n"
                    + ");";
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                }
            }
            if (con != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                }
            }
        }
    }
}
