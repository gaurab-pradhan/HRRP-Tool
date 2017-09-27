/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Login;

import Home.HomeController;
import Runner.Runner;
import Util.Constants;
import Util.DBUtil;
import com.jfoenix.controls.*;
import com.jfoenix.controls.JFXTextField;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author Gaurab.Pradhan
 */
public class LoginController extends BorderPane {

    private static Logger log = Logger.getLogger(LoginController.class.getName());
    Runner run;
    Stage stage;

    @FXML
    private JFXButton loginBtn;

    @FXML
    private JFXTextField uname;

    @FXML
    private JFXPasswordField pass;

    public void setApp(Runner run, Stage stage) throws Exception {
        this.run = run;
        this.stage = stage;
        stage.setResizable(false);
        log.info("LoginController Loaded");
    }

    @FXML
    public void onEnter(ActionEvent ae) throws Exception {
        loginCheck();
    }

    @FXML
    void checkAction(ActionEvent event) throws Exception {
        loginCheck();
    }

    private void loginCheck() throws Exception {
        Connection con = DBUtil.getConnectionSQLite();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM user WHERE uname = '" + uname.getText() + "' and password = '" + pass.getText() + "'");
            if (rs.next()) {
                 this.run.gotoHome();
//                Runner r = new Runner();
//                HomeController home = (HomeController) r.replaceSceneContent(Constants.HOME);
//                home.setApp(run, stage, "National");
                rs.close();
                stmt.close();
                con.close();
            } else {
                uname.clear();
                pass.clear();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setContentText("Invalid Username or Password !!!");
                alert.showAndWait();

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
        }
    }
}
