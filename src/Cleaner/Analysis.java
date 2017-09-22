package Cleaner;

import Bean.*;
import Util.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import javafx.scene.control.TextArea;
import org.apache.log4j.Logger;

/**
 *
 * @author gpradhan
 */
public class Analysis {

    private static Logger log = Logger.getLogger(Analysis.class.getName());
    static Statement stmt = null;
    static ResultSet rs = null;

    static int pre_count = 0;
    static int new_count = 0;

    static int pre_planned = 0; //sum of planned (previous)
    static int new_planned = 0; //sum of planned (new)

    static int pre_reached = 0; //sum of reached (previous)
    static int new_reached = 0; //sum of reached (new)

    static String overview = "";

    static String path = null;
    TextArea log_txt;

    public void check(String district, TextArea log_txt) throws Exception {
        overview = "";
//        this.log_txt.setText("");
        this.log_txt = log_txt;
        Connection con = DBUtil.getConnectionSQLite();
        stmt = con.createStatement();

        //Compare total number of Rows from previous and new round of 4w
        String prev_count = "SELECT count(*) as count FROM tbl_hrrp_4w"; // where district = '" + district + "'";
        String now_count = "SELECT count(*) as count FROM temp_hrrp_4w"; // where district = '" + district + "'";
        rs = stmt.executeQuery(prev_count);
        pre_count = rowCount();

        rs = stmt.executeQuery(now_count);
        new_count = rowCount();

        //Compare total planned and reaced from previous and new round of 4w
        String prev_sum = "SELECT sum(total_planned) as tp, sum(total_reached) as tr FROM tbl_hrrp_4w";// where district = '" + district + "'";
        String now_sum = "SELECT sum(total_planned) as tp, sum(total_reached) as tr FROM temp_hrrp_4w";// where district = '" + district + "'";
        rs = stmt.executeQuery(prev_sum);
        int[] pre_sum = sumTotalPlanReach();
        pre_planned = pre_sum[0];
        pre_reached = pre_sum[1];

        rs = stmt.executeQuery(now_sum);
        int[] new_sum = sumTotalPlanReach();
        new_planned = new_sum[0];
        new_reached = new_sum[1];

        display(district);
        mkdir();
        String fname = district + "_Report.csv";
        if(district.toLowerCase().equals("kathmandu valley")){
            fname = "KtmValley_Report.csv";
        }
        BufferedWriter bw = null;
        try {
            File file = new File(path + "/" + fname);
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            bw = new BufferedWriter(fw);
            bw.write(overview);
            bw.write("\n");
            bw.write("PO,Act_Type,Act_Sub_Type,Act_Name,P_Planned,N_Planned,P_Diff,P_Reached,N_Reached,R_Diff");
            bw.write("\n");
            //Compare sum of activity planned and reached po wise from previous and new round of 4w
            String query1 = "SELECT po,act_type,act_sub_type,act_name "
                    + "FROM tbl_hrrp_4w "
                    + "Group by po,act_type,act_sub_type,act_name "
                    + "UNION "
                    + "SELECT po,act_type,act_sub_type,act_name "
                    + "FROM temp_hrrp_4w "
                    + "Group by po,act_type,act_sub_type,act_name ORDER BY po ASC";
            List<ActType_PO> mPairs = new ArrayList<ActType_PO>();
            rs = stmt.executeQuery(query1);
            mPairs = getListPo_ActType();
            for (int i = 0; i < mPairs.size(); i++) {
                String prev_query = "SELECT sum(total_planned) as tp, sum(total_reached) as tr "
                        + "FROM tbl_hrrp_4w "
                        + "Where po= '" + mPairs.get(i).getPo() + "' "
                        + "and act_type = '" + mPairs.get(i).getAct_type() + "' "
                        + "and act_sub_type = '" + mPairs.get(i).getAct_sub_type() + "' "
                        + "and act_name ='" + mPairs.get(i).getAct_name() + "'";
                String new_query = "SELECT sum(total_planned) as tp, sum(total_reached) as tr "
                        + "FROM temp_hrrp_4w "
                        + "Where po= '" + mPairs.get(i).getPo() + "' "
                        + "and act_type = '" + mPairs.get(i).getAct_type() + "'"
                        + " and act_sub_type = '" + mPairs.get(i).getAct_sub_type() + "' "
                        + "and act_name ='" + mPairs.get(i).getAct_name() + "'";

                int prev_po_act_panned = 0;
                int prev_po_act_reached = 0;

                int new_po_act_planned = 0;
                int new_po_act_reached = 0;

                rs = stmt.executeQuery(prev_query);
                int[] pre_sum_act = sumTotalPlanReach();
                prev_po_act_panned = pre_sum_act[0];
                prev_po_act_reached = pre_sum_act[1];

                rs = stmt.executeQuery(new_query);
                int[] new_sum_act = sumTotalPlanReach();
                new_po_act_planned = new_sum_act[0];
                new_po_act_reached = new_sum_act[1];

                int planned_Diff = new_po_act_planned - prev_po_act_panned;
                int reached_diff = new_po_act_reached - prev_po_act_reached;

                bw.write(mPairs.get(i).getPo() + "," + "\"" + mPairs.get(i).getAct_type() + "\"" + "," + "\"" + mPairs.get(i).getAct_sub_type() + "\"" + "," + "\"" + mPairs.get(i).getAct_name() + "\"" + "," + prev_po_act_panned + "," + new_po_act_planned + "," + planned_Diff + "," + prev_po_act_reached + "," + new_po_act_reached + "," + reached_diff);
                bw.write("\n");
            }
            bw.write("\n");
            bw.write("Note: P_Planned: Previous Planned; P_Reached: Previous Reached;N_Planned: New Planned;N_Reached: New Reached");
            bw.close();
            System.out.println(path + "/" + fname + " created");
            log_txt.appendText("INFO: Report Created: " + path + "/" + fname + "\n");
        } catch (IOException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
        }
        if (stmt != null) {
            stmt.close();
        }
        if (rs != null) {
            rs.close();
        }

        if (con != null) {
            con.close();
        }

    }

    public void display(String district) {
        log.info("~~~~~~~~~~> " + district.toUpperCase());
        log.info("~~~~~~~~~~> Total Row Count (Previous): " + pre_count);
        log_txt.appendText("INFO: Total Row Count (Previous): " + pre_count + "\n");
        log.info("~~~~~~~~~~> Total Row Count (Now): " + new_count);
        log_txt.appendText("INFO: Total Row Count (Now): " + new_count + "\n");
        int diff_row = new_count - pre_count;
        String msg = "";
        if (diff_row > 0) {
            log.info("~~~~~~~~~~> " + diff_row + " row(s) was added");
            log_txt.appendText("INFO: " + diff_row + " row(s) was added \n");
            msg = Math.abs(diff_row) + " row(s) was added";
        } else if (diff_row < 0) {
            log.info("~~~~~~~~~~> " + Math.abs(diff_row) + " row(s) was deleted");
            log_txt.appendText("INFO: " + Math.abs(diff_row) + " row(s) was deleted \n");
            msg = Math.abs(diff_row) + " row(s) was deleted";
        } else {
            log.info("~~~~~~~~~~> No changes");
            log_txt.appendText("INFO: No changes in row count \n");
            msg = "No changes";
        }
        log.info("~~~~~~~~~> Total Planned (Previous): " + pre_planned);
        log_txt.appendText("INFO: Total Planned (Previous): " + pre_planned + "\n");
        log.info("~~~~~~~~~> Total Planned (Now): " + new_planned);
        log_txt.appendText("INFO: Total Planned (Now): " + new_planned + "\n");

        log.info("~~~~~~~~~> Total Reached (Previous): " + pre_reached);
        log_txt.appendText("INFO: Total Reached (Previous): " + pre_reached + "\n");
        log.info("~~~~~~~~~> Total Reached (Now): " + new_reached + "\n");
        log_txt.appendText("INFO: Total Reached (Now): " + new_reached + "\n");

        overview = overview + district.toUpperCase() + "\nTotal Row Count (Previous): " + pre_count + "\nTotal Row Count (Now): " + new_count + "\n" + msg + "\nTotal Planned (Previous): " + pre_planned + "\nTotal Planned (Now): " + new_planned + "\nTotal Reached (Previous): " + pre_reached + "\nTotal Reached (Now): " + new_reached;
    }

    private static int rowCount() throws SQLException {
        int count = 0;
        try {
            while (rs.next()) {
                count = rs.getInt("count");
            }
        } catch (SQLException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
        }
        rs.close();
        return count;
    }

    private static int[] sumTotalPlanReach() throws SQLException {
        int[] sum = new int[2];
        try {
            while (rs.next()) {
                sum[0] = rs.getInt("tp");
                sum[1] = rs.getInt("tr");
            }
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
        }
        rs.close();
        return sum;
    }

    private static List<ActType_PO> getListPo_ActType() throws SQLException {
        List<ActType_PO> mPairs = new ArrayList<ActType_PO>();
        try {
            while (rs.next()) {
                ActType_PO bean = new ActType_PO();
                bean.setPo(rs.getString(1));
                bean.setAct_type(rs.getString(2));
                bean.setAct_sub_type(rs.getString(3));
                bean.setAct_name(rs.getString(4));
                mPairs.add(bean);
            }
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
        }
        rs.close();
        return mPairs;
    }

    private static void mkdir() {
        File file = new File("Report/");
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
    }
}
