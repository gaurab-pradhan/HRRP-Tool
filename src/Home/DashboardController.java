package Home;

import Util.DBUtil;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.apache.log4j.Logger;

/**
 *
 * @author gpradhan
 */
public class DashboardController extends BorderPane implements Initializable {

    private static Logger log = Logger.getLogger(DashboardController.class.getName());
    @FXML
    Label dis, rowLbl, planLbl, reachLbl, poLbl, roundLbl, lbl;

    @FXML
    private Button refreshBtn;

    String district;
    String tableName = "tbl_hrrp_4w";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        district = HomeController.dis;
        dis.setText(district);
        fillDashboard();
    }

    private void fillDashboard() {
        ResultSet rs = null;
        Statement stmt = null;
        Connection con = null;
        try {
            con = DBUtil.getConnectionSQLite();
            stmt = con.createStatement();

            String countQ = "SELECT COUNT(*) FROM tbl_hrrp_4w WHERE district = '" + district + "'";
            if (district.toLowerCase().equals("kathmandu valley")) {
                countQ = "SELECT COUNT(*) FROM tbl_hrrp_4w WHERE district = 'Kathmandu' or district = 'Lalitpur' or district = 'Bhaktapur'";
            }
            if (district.toLowerCase().equals("national")) {
                countQ = "SELECT COUNT(*) FROM tbl_hrrp_4w";
            }
            rs = stmt.executeQuery(countQ);

            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            rowLbl.setText(String.valueOf(count));

            String poQ = "SELECT COUNT(DISTINCT(po)) FROM tbl_hrrp_4w WHERE act_code != 'OT OT 001' and district = '" + district + "'";
            if (district.toLowerCase().equals("kathmandu valley")) {
                poQ = "SELECT COUNT(DISTINCT(po)) FROM tbl_hrrp_4w WHERE act_code != 'OT OT 001' and ( district = 'Kathmandu' or district = 'Lalitpur' or district = 'Bhaktapur')";
            }
            if (district.toLowerCase().equals("national")) {
                poQ = "SELECT COUNT(DISTINCT(po)) FROM tbl_hrrp_4w";
            }
            rs = stmt.executeQuery(poQ);

            int poCount = 0;
            if (rs.next()) {
                poCount = rs.getInt(1);
            }
            poLbl.setText(String.valueOf(poCount));

            String sumQ = "SELECT SUM(total_planned) as plan, SUM(total_reached) as reach FROM tbl_hrrp_4w WHERE district = '" + district + "'";
            if (district.toLowerCase().equals("kathmandu valley")) {
                sumQ = "SELECT SUM(total_planned) as plan, SUM(total_reached) as reach FROM tbl_hrrp_4w WHERE district = 'Kathmandu' or district = 'Lalitpur' or district = 'Bhaktapur'";
            }
            if (district.toLowerCase().equals("national")) {
                sumQ = "SELECT SUM(total_planned) as plan, SUM(total_reached) as reach FROM tbl_hrrp_4w";
            }
            rs = stmt.executeQuery(sumQ);
            float plan = 0;
            float reach = 0;
            if (rs.next()) {
                plan = rs.getFloat(1);
                reach = rs.getFloat(2);
            }
            planLbl.setText(String.valueOf(plan));
            reachLbl.setText(String.valueOf(reach));

            String round = "SELECT round FROM tbl_hrrp_4w LIMIT 1";
            rs = stmt.executeQuery(round);
            if (rs.next()) {
                roundLbl.setText(rs.getString(1));
            }
            rs.close();
            stmt.close();;
            con.close();
        } catch (SQLException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
        }
    }

    @FXML
    void refreshAction(ActionEvent event) throws SQLException {
        Connection consqlite = DBUtil.getConnectionSQLite();
        Connection conmysql = DBUtil.getConnectionMySQL();
        Statement stmt = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            String query = "INSERT INTO " + tableName + " (`sn`, `district`, `vdc`, `ward`, `po`, `impl_partner`, `FundingOrg`, "
                    + "`act_type`, `act_sub_type`, `act_name`, `act_detail`, `units`, "
                    + "`fund_status`, `act_status`, `total_planned`, `total_reached`, "
                    + "`start_date`, `end_date`, `contact_name`, `contact_number`, `email`, "
                    + "`comments`, `HRRP_VDC_Code`, `HRRP_Ward_Code`, `act_code`,`round`) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            consqlite.setAutoCommit(false);
            ps = consqlite.prepareStatement(query);
            //delete data from table in SQLITE
            consqlite.createStatement().execute("DELETE FROM " + tableName);

            final int batchSize = 1000;
            int count = 0;
//            Date date = null;

            stmt = conmysql.createStatement();
            String select = "Select * from " + tableName + " where district =  '" + district + "'"; // select data from MYSQL database
            if (district.toLowerCase().equals("kathmandu valley")) {
                select = "Select * from " + tableName + " where district = 'Kathmandu' or district = 'Lalitpur' or district = 'Bhaktapur'";
            }
            if (district.toLowerCase().equals("national")) {
                select = "Select * from " + tableName;
            }
            rs = stmt.executeQuery(select);
            int i = 1;
            while (rs.next()) {
                ps.setInt(1, i);
                ps.setString(2, rs.getString(2));
                ps.setString(3, rs.getString(3));
                ps.setString(4, rs.getString(4));
                ps.setString(5, rs.getString(5).toUpperCase());
                ps.setString(6, rs.getString(6).toUpperCase());
                ps.setString(7, rs.getString(7).toUpperCase());
                ps.setString(8, rs.getString(8));
                ps.setString(9, rs.getString(9));
                ps.setString(10, rs.getString(10));
                ps.setString(11, rs.getString(11));
                ps.setString(12, rs.getString(12));
                ps.setString(13, rs.getString(13));
                ps.setString(14, rs.getString(14));
                ps.setString(15, rs.getString(15));
                ps.setString(16, rs.getString(16));
                ps.setString(17, rs.getString(17));
                ps.setString(18, rs.getString(18));
                ps.setString(19, rs.getString(19));
                ps.setString(20, rs.getString(20));
                ps.setString(21, rs.getString(21));
                ps.setString(22, rs.getString(22));
                ps.setString(23, rs.getString(23));
                ps.setString(24, rs.getString(24));
                ps.setString(25, rs.getString(25));
                ps.setString(26, rs.getString(26));
                i++;
                ps.addBatch();
                if (++count % batchSize == 0) {
                    ps.executeBatch();
                }
            }
            ps.executeBatch(); // insert remaining records
            consqlite.commit();
            rs.close();
            stmt.close();
            ps.close();
            consqlite.close();
            conmysql.close();
        } catch (SQLException ex) {
            consqlite.rollback();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
        }
        fillDashboard();
    }
}
