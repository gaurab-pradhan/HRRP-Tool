/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Home;

import Login.LoginController;
import Runner.Runner;
import Util.Constants;
import Util.DBUtil;
import Util.STVT_Target;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author gpradhan
 */
public class LocationController extends BorderPane {

    private static Logger log = Logger.getLogger(LocationController.class.getName());
    Runner run;
    Stage stage;

    @FXML
    private ComboBox<String> disCombo;

    public void setApp(Runner run, Stage stage) throws Exception {
        this.run = run;
        this.stage = stage;
        fillCombo();
        log.info("LocationController Loaded");
//        run.gotoHome();
    }

    @FXML
    void disComboAction(ActionEvent event) throws Exception {
        String location = "";
        if (!disCombo.getSelectionModel().isEmpty()) {
            location = disCombo.getSelectionModel().getSelectedItem();
            String dis = location;
            if (location.toLowerCase().equals("national")) {
                location = "32ff00f215";
            } else if (location.toLowerCase().equals("dhading")) {
                location = "e6267bf447";
            } else if (location.toLowerCase().equals("dolakha")) {
                location = "5c68675513";
            } else if (location.toLowerCase().equals("gorkha")) {
                location = "290636ad82";
            } else if (location.toLowerCase().equals("kavrepalanchok")) {
                location = "75122f4a77";
            } else if (location.toLowerCase().equals("makwanpur")) {
                location = "ab1f50bca0";
            } else if (location.toLowerCase().equals("nuwakot")) {
                location = "b6fbd2a31d";
            } else if (location.toLowerCase().equals("okhaldhunga")) {
                location = "0facae5d50";
            } else if (location.toLowerCase().equals("ramechhap")) {
                location = "29e91b4476";
            } else if (location.toLowerCase().equals("rasuwa")) {
                location = "1014355c21";
            } else if (location.toLowerCase().equals("sindhuli")) {
                location = "e25b8f181d";
            } else if (location.toLowerCase().equals("sindhupalchok")) {
                location = "e7c46e2586";
            } else if (location.toLowerCase().equals("kathmandu valley")) {
                location = "1bc27fbd1e";
            }

            int row = DBUtil.insertLoc(location);
            run.setLocation(location);
            Thread t1 = new Thread(new Runnable() {
                public void run() {
                    STVT_Target stvt = new STVT_Target();
                    try {
                        stvt.st_vt(dis);
                    } catch (Exception ex) {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        ex.printStackTrace(pw);
                        String exceptionText = sw.toString();
                        log.error(exceptionText);
                    } 
                }
            });
            t1.start();
            t1.join();
            if (dis.toLowerCase().equals("national")) {
                DBUtil.createUsertbl();
                Runner r = new Runner();
                r.gotoLogin(stage);
            } else {
                this.run.gotoHome();
            }

        }
    }

    private void fillCombo() {
        String[] orgType = {"National", "Dhading", "Dolakha", "Gorkha", "Kavrepalanchok", "Makwanpur", "Nuwakot", "Okhaldhunga", "Ramechhap", "Rasuwa", "Sindhuli", "Sindhupalchok", "Kathmandu Valley"};
        ObservableList obList = FXCollections.observableArrayList();
        for (int i = 0; i < orgType.length; i++) {
            obList.add(orgType[i]);
        }
        disCombo.setItems(obList);
    }
}
