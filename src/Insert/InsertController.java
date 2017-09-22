package Insert;

import Util.CSVLoader;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
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

    Stage stage;
    String path = null;
    String tablename = "tbl_hrrp_4w";

    @FXML
    void browseFileAction(ActionEvent event) {
        log_txt.setText("");
        insertBtn.setDisable(true);
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
            insertBtn.setDisable(false);
        }
    }

    @FXML
    void insertAction(ActionEvent event) throws SQLException {
        try {
            CSVLoader loader = new CSVLoader(DBUtil.getConnectionMySQL());
            loader.loadCSV(path, tablename, true);
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
        }
        log_txt.appendText("INFO: Preparing data to insert into database.");
        displayResult();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        stage = Home.HomeController.stage;
        insertBtn.setDisable(true);
    }

    private void displayResult() {
        ResultSet rs = null;
        Statement stmt = null;
        Connection con = null;
        try {
            con = DBUtil.getConnectionMySQL();
            stmt = con.createStatement();

            String countQ = "SELECT COUNT(*) FROM " + tablename;
            rs = stmt.executeQuery(countQ);

            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            log_txt.appendText("INFO: Total Rows Count: " + count + "\n");

            String sumQ = "SELECT SUM(total_planned) as plan, SUM(total_reached) as reach FROM " + tablename;
            rs = stmt.executeQuery(sumQ);
            float plan = 0;
            float reach = 0;
            if (rs.next()) {
                plan = rs.getFloat(1);
                reach = rs.getFloat(2);
            }
            log_txt.appendText("INFO: Sum of Planned: " + plan + "\n");
            log_txt.appendText("INFO: Sum of Reached: " + reach + "\n");
            log_txt.appendText("INFO: Operation completed successfully\n");
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

}
