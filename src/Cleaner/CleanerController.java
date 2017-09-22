package Cleaner;

import Home.HomeController;
import Util.*;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author gpradhan
 */
public class CleanerController extends BorderPane implements Initializable {

    private static Logger log = Logger.getLogger(CleanerController.class.getName());

    Stage stage;

    @FXML
    TextField rowCount;

    @FXML
    private TextArea log_txt;

    @FXML
    private Button browseFile, checkBtn;

    String path = null;
    String tablename = "temp_hrrp_4w";

    @FXML
    private void browseFileAction(ActionEvent event) {
        log_txt.setText("");
        System.out.println("i am browse file");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV", "*.csv")
        );
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            path = file.getAbsolutePath();
            log_txt.appendText("INFO: File Loaded: " + path + "\n");
            rowCount.setDisable(false);
        }
    }

    @FXML
    void checkAction(ActionEvent event) throws SQLException {
        readCSV();
        boolean flag = rowCountCheck();
        if (flag) {
            log_txt.appendText("INFO: First Level Check Started. \n");
            boolean check1 = dataCheck1(); // will check HRRP_VDC_CODE,ACT_CODE,ACT_STATUS
            if (check1) {
                log_txt.appendText("INFO: Second Level Check Started. \n");
                dataCheck2(); // will check compare with previous 4w and identify probable dupicates
            } else {
                log_txt.appendText("INFO: Unable to start second level of check. \n");
                log_txt.appendText("INFO: Please address above mentioned issues and try again. \n");
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        stage = Home.HomeController.stage;
        checkBtn.setDisable(true);
        rowCount.setDisable(true);
    }

    private void readCSV() throws SQLException {
        try {
            CSVLoader loader = new CSVLoader(DBUtil.getConnectionSQLite());
            loader.loadCSV(path, tablename, true);
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
        }
    }

    @FXML
    void txtKeyTyped(KeyEvent evt) {
        if (evt.getEventType() == KeyEvent.KEY_TYPED) {
            String value = evt.getCharacter();
            char vChar = value.charAt(0);
            checkBtn.setDisable(false);
            if (!(Character.isDigit(vChar)) || (vChar == '\b') || (vChar == ' ')) {
                evt.consume();
                checkBtn.setDisable(true);
            }
        }
    }

    private boolean rowCountCheck() {
        boolean check = false;
        try {
            ResultSet rs = null;
            Statement stmt = null;
            Connection con = DBUtil.getConnectionSQLite();
            if (con != null) {
                stmt = con.createStatement();
                rs = stmt.executeQuery("SELECT COUNT(*) as count FROM " + tablename);
                int count = 0;
                if (rs.next()) {
                    count = rs.getInt("count");
                }
                rs.close();
                stmt.close();
                if (count == Integer.parseInt(rowCount.getText().trim())) {
                    log.info("Row Count Matched");
                    log_txt.appendText("INFO: Row Count Matched. \n");
                    check = true;
                } else {
                    String delTbl = "DROP TABLE IF EXISTS " + tablename;
                    stmt = con.createStatement();
                    stmt.executeUpdate(delTbl);
                    stmt = null;

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Dialog");
                    alert.setContentText("Row Count Did not matched. Please try again");
                    alert.showAndWait();
                }
                con.close();
            }
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
        }
        return check;
    }

    private boolean dataCheck1() {
        boolean check = false;
        boolean vdcError = false; // no error
        boolean actError = false; // no error
        boolean comError = false; // no error
        boolean ongoingError = false; // no error
        boolean planError = false; // no error
        ResultSet rs = null;
        Statement stmt = null;
        Connection con = null;
        try {
            con = DBUtil.getConnectionSQLite();
            stmt = con.createStatement();

            String vdcCodeCheck = "SELECT sn as RowNumber FROM " + tablename + " WHERE HRRP_VDC_Code = '#N/A' or  HRRP_VDC_Code = ''";
            rs = stmt.executeQuery(vdcCodeCheck);
            int i = 0;
            String sn = "";
            while (rs.next()) {
                vdcError = true; // has some error
                if (i == 0) {
                    log_txt.appendText("ERROR: District or VDC mismatch. \n");
                }
                i++;
                sn = sn + String.valueOf(rs.getInt(1)) + ", ";
            }
            if (vdcError) {
                sn = sn.trim();
                if (sn.endsWith(",")) {
                    int index = sn.lastIndexOf(",");
                    sn = sn.substring(0, index);
                }
                log_txt.appendText("ERROR: Please check District or VDC column in following rows: " + sn + "\n");
            }
            i = 0;
            sn = "";
            String actCodeCheck = "SELECT sn as RowNumber FROM " + tablename + " WHERE act_code = '#N/A' or act_code = ''";
            rs = stmt.executeQuery(actCodeCheck);
            while (rs.next()) {
                actError = true; // has some error
                if (i == 0) {
                    log_txt.appendText("ERROR: Activity Type, Activity Sub Type or Activity Name mismatch \n");
                }
                i++;
                sn = sn + String.valueOf(rs.getInt(1)) + ", ";
            }
            if (actError) {
                sn = sn.trim();
                if (sn.endsWith(",")) {
                    int index = sn.lastIndexOf(",");
                    sn = sn.substring(0, index);
                }
                log_txt.appendText("ERROR: Please check Activities column in following rows: " + sn + "\n");
            }
            i = 0;
            sn = "";
            String completedCheck = "SELECT sn FROM " + tablename + " where act_status LIKE '%completed%' and (total_planned <= 0 or total_reached <= 0)";
            rs = stmt.executeQuery(completedCheck);
            while (rs.next()) {
                comError = true; // has some error
                if (i == 0) {
                    log_txt.appendText("ERROR: Total Planned or Total Reached figure is 0 for Completed activity \n");
                }
                i++;
                sn = sn + String.valueOf(rs.getInt(1)) + ", ";
            }
            if (comError) {
                sn = sn.trim();
                if (sn.endsWith(",")) {
                    int index = sn.lastIndexOf(",");
                    sn = sn.substring(0, index);
                }
                log_txt.appendText("ERROR: Activity Status is completed but Total Planned or Total Reached figure is 0 in following rows: " + sn + "\n");
            }
            i = 0;
            sn = "";
            String ongoingCheck = "SELECT sn FROM " + tablename + " where act_status LIKE '%ongoing%' and (total_planned <= 0 )";
            rs = stmt.executeQuery(ongoingCheck);
            while (rs.next()) {
                ongoingError = true; // has some error
                if (i == 0) {
                    log_txt.appendText("ERROR: Total Planned figure is 0 for Ongoing activity\n");
                }
                i++;
                sn = sn + String.valueOf(rs.getInt(1)) + ", ";
            }
            if (ongoingError) {
                sn = sn.trim();
                if (sn.endsWith(",")) {
                    int index = sn.lastIndexOf(",");
                    sn = sn.substring(0, index);
                }
                log_txt.appendText("ERROR: Activity Status is Ongoing but Total Planned figure is 0 in following rows: " + sn + "\n");
            }
            i = 0;
            sn = "";
            String planCheck = "SELECT sn FROM " + tablename + " where act_status LIKE '%planned%' and (total_reached >0 )";
            rs = stmt.executeQuery(planCheck);
            while (rs.next()) {
                planError = true; // has some error
                if (i == 0) {
                    log_txt.appendText("ERROR: Total Reached figure greater than 0 for Planned activity \n");
                }
                i++;
                sn = sn + String.valueOf(rs.getInt(1)) + ", ";
            }
            if (planError) {
                sn = sn.trim();
                if (sn.endsWith(",")) {
                    int index = sn.lastIndexOf(",");
                    sn = sn.substring(0, index);
                }
                log_txt.appendText("ERROR: Activity Status is planned but Total Reached figure is greater than 0 in following rows: " + sn + "\n");
            }
            rs.close();
            stmt.close();
            if (con != null) {
                con.close();
            }
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
        }
        if (!vdcError && !actError && !comError && !ongoingError && !planError) {
            check = true;
        }
        return check;
    }

    private void dataCheck2() {
        String district = HomeController.dis;
        try {
            Analysis ana = new Analysis();
            ana.check(district, log_txt);
            log_txt.appendText("INFO: 4w Check completed successfully \n");
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
        }
    }
}
