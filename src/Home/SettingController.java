/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Home;

import Util.Crypto;
import Util.PropertiesUtil;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

/**
 * FXML Controller class
 *
 * @author Gaurab.Pradhan
 */
public class SettingController implements Initializable {

    @FXML
    private JFXButton liveUpdate, nasUpdate, mailUpdate;

    @FXML
    private JFXTextField livePassTxt, apikeyTxt, nasDbNameTxt, listidTxt, liveEndTxt, liveDbNameTxt, liveUserTxt, nasEndTxt, nasUserTxt, nasPassTxt;

    @FXML
    private JFXToggleButton editBtn;

    String api, listid, liveEnd, liveDbname, liveUser, livePass, nasEnd, nasDbname, nasUser, nasPass;

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SettingController.class.getName());
    private static final String CONGIF_PROPERTY_FILE = "conf/config.properties";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            api = Crypto.decrypt(PropertiesUtil.getApikey());
            listid = Crypto.decrypt(PropertiesUtil.getListId());

            liveEnd = Crypto.decrypt(PropertiesUtil.getLiveEnd());
            liveDbname = Crypto.decrypt(PropertiesUtil.getLiveDbname());
            liveUser = Crypto.decrypt(PropertiesUtil.getLiveUser());
            livePass = Crypto.decrypt(PropertiesUtil.getLivePass());

            nasEnd = Crypto.decrypt(PropertiesUtil.getNasEnd());
            nasDbname = Crypto.decrypt(PropertiesUtil.getNasDbname());
            nasUser = Crypto.decrypt(PropertiesUtil.getNasUser());
            nasPass = Crypto.decrypt(PropertiesUtil.getNasPass());

            apikeyTxt.setText(api);
            listidTxt.setText(listid);

            liveEndTxt.setText(liveEnd);
            liveDbNameTxt.setText(liveDbname);
            liveUserTxt.setText(liveUser);
            livePassTxt.setText(livePass);

            nasEndTxt.setText(nasEnd);
            nasDbNameTxt.setText(nasDbname);
            nasUserTxt.setText(nasUser);
            nasPassTxt.setText(nasPass);

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
        }
    }

    @FXML
    void editAction(ActionEvent event) {
        if (editBtn.isSelected()) {
            apikeyTxt.setDisable(false);
            listidTxt.setDisable(false);

            liveEndTxt.setDisable(false);
            liveDbNameTxt.setDisable(false);
            liveUserTxt.setDisable(false);
            livePassTxt.setDisable(false);

            nasEndTxt.setDisable(false);
            nasDbNameTxt.setDisable(false);
            nasUserTxt.setDisable(false);
            nasPassTxt.setDisable(false);

            liveUpdate.setDisable(false);
            nasUpdate.setDisable(false);
            mailUpdate.setDisable(false);
        } else {
            apikeyTxt.setDisable(true);
            listidTxt.setDisable(true);

            liveEndTxt.setDisable(true);
            liveDbNameTxt.setDisable(true);
            liveUserTxt.setDisable(true);
            livePassTxt.setDisable(true);

            nasEndTxt.setDisable(true);
            nasDbNameTxt.setDisable(true);
            nasUserTxt.setDisable(true);
            nasPassTxt.setDisable(true);

            liveUpdate.setDisable(true);
            nasUpdate.setDisable(true);
            mailUpdate.setDisable(true);
        }
    }

    @FXML
    void updateBtn(ActionEvent event) throws Exception {
        Properties prop = new Properties();
        OutputStream output = null;
        output = new FileOutputStream(CONGIF_PROPERTY_FILE);
        prop.setProperty("api", Crypto.encrypt(apikeyTxt.getText().trim()));
        prop.setProperty("listId", Crypto.encrypt(listidTxt.getText().trim()));
        prop.setProperty("live1", Crypto.encrypt(liveEndTxt.getText().trim()));
        prop.setProperty("live2", Crypto.encrypt(liveDbNameTxt.getText().trim()));
        prop.setProperty("live3", Crypto.encrypt(liveUserTxt.getText().trim()));
        prop.setProperty("live4", Crypto.encrypt(livePassTxt.getText().trim()));
        prop.setProperty("nas1", Crypto.encrypt(nasEndTxt.getText().trim()));
        prop.setProperty("nas2", Crypto.encrypt(nasDbNameTxt.getText().trim()));
        prop.setProperty("nas3", Crypto.encrypt(nasUserTxt.getText().trim()));
        prop.setProperty("nas4", Crypto.encrypt(nasPassTxt.getText().trim()));

        prop.store(output, null);

        PropertiesUtil.loadPropertiesFile();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucess");
        alert.setContentText("Tool parameter has been changed successfully\n");
        alert.showAndWait();
        apikeyTxt.setDisable(true);
        listidTxt.setDisable(true);

        liveEndTxt.setDisable(true);
        liveDbNameTxt.setDisable(true);
        liveUserTxt.setDisable(true);
        livePassTxt.setDisable(true);

        nasEndTxt.setDisable(true);
        nasDbNameTxt.setDisable(true);
        nasUserTxt.setDisable(true);
        nasPassTxt.setDisable(true);

        liveUpdate.setDisable(true);
        nasUpdate.setDisable(true);
        mailUpdate.setDisable(true);
    }
}
