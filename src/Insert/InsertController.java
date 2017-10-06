package Insert;

import Home.DashboardController;
import Util.DBUtil;
import com.jfoenix.controls.*;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author gpradhan
 */
public class InsertController implements Initializable {

    private static Logger log = Logger.getLogger(InsertController.class.getName());
    @FXML
    private JFXButton insertBtn, browseFile;

    @FXML
    private TextArea log_txt;

    @FXML
    private ComboBox<String> combo;

    @FXML
    private Label lbl1, lbl2;

    @FXML
    private TextField roundTxt;

    Stage stage;
    String path = null;
    String tablename = "tbl_hrrp_4w";

    @FXML
    void browseFileAction(ActionEvent event) {
        log_txt.setText("");
        insertBtn.setDisable(true);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV", "*.csv")
        );
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            path = file.getAbsolutePath();
            log_txt.appendText("INFO: File Loaded: " + path + "\n");
            insertBtn.setDisable(false);
        }
    }

    @FXML
    void insertAction(ActionEvent event) {
        try {
            log_txt.appendText("INFO: Preparing data to insert into database.\n");
            if (combo.getSelectionModel().getSelectedItem().toLowerCase().equals("nas")) {
                if (!roundTxt.getText().trim().isEmpty()) {
                    Connection con = DBUtil.getConnectionNAS();
                    String tableName = "tbl_hrrp_4w_r" + roundTxt.getText().trim();
                    DBUtil.createHRRP_4wTbl_nas(con, tableName);
//                    CSVLoader loader = new CSVLoader(con);
//                    loader.loadCSV(path, tablename, true, log_txt, 25);
                    Insert_4W_MySql.insertData(con, tableName, path);
                    displayResult(con, tableName);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Dialog");
                    alert.setContentText("4W Round missing");
                    alert.showAndWait();
                }
            } else {
                Connection con = DBUtil.getConnectionMySQL();
                Insert_4W_MySql.insertData(con, tablename, path);
                displayResult(con, tablename);
            }
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        stage = Home.HomeController.stage;
        browseFile.setDisable(true);
        insertBtn.setDisable(true);
        lbl1.setVisible(false);
        lbl2.setVisible(false);
        roundTxt.setVisible(false);
        fillCombo();

    }

    @FXML
    void comboAction(ActionEvent event) throws Exception {
        String location = "";
        if (!combo.getSelectionModel().isEmpty()) {
            browseFile.setDisable(false);
            location = combo.getSelectionModel().getSelectedItem();
            if (location.toLowerCase().equals("nas")) {
                lbl1.setVisible(true);
                lbl2.setVisible(true);
                roundTxt.setVisible(true);
            } else {
                lbl1.setVisible(false);
                lbl2.setVisible(false);
                roundTxt.setVisible(false);
            }
        }
    }

    private void displayResult(Connection con, String tbl) {
        ResultSet rs = null;
        Statement stmt = null;
        try {
            stmt = con.createStatement();

            String countQ = "SELECT COUNT(*) FROM " + tbl;
            rs = stmt.executeQuery(countQ);

            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            if (count > 0) {
                log_txt.appendText("INFO: Total Rows Count: " + count + "\n");

                String sumQ = "SELECT SUM(total_planned) as plan, SUM(total_reached) as reach FROM " + tbl;
                rs = stmt.executeQuery(sumQ);
                float plan = 0;
                float reach = 0;
                if (rs.next()) {
                    plan = rs.getFloat(1);
                    reach = rs.getFloat(2);
                }
                log_txt.appendText("INFO: Sum of Planned: " + plan + "\n");
                log_txt.appendText("INFO: Sum of Reached: " + reach + "\n");
                log_txt.appendText("INFO: Data inserted into:" + tbl + "\n");
                log_txt.appendText("INFO: Operation completed successfully\n");
            } else {
                log_txt.appendText("ERROR: Check your data and try again\n");
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

    private void fillCombo() {
        String[] orgType = {"Live Server", "NAS"};
        ObservableList obList = FXCollections.observableArrayList();
        for (int i = 0; i < orgType.length; i++) {
            obList.add(orgType[i]);
        }
        combo.setItems(obList);
    }

    @FXML
    void txtKeyTyped(KeyEvent evt) {
        if (evt.getEventType() == KeyEvent.KEY_TYPED) {
            String value = evt.getCharacter();
            char vChar = value.charAt(0);
            if (!(Character.isDigit(vChar)) || (vChar == '\b') || (vChar == ' ')) {
                evt.consume();
            }
        }
    }
}
