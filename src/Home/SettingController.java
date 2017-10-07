/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Home;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;

/**
 * FXML Controller class
 *
 * @author Gaurab.Pradhan
 */
public class SettingController implements Initializable {

    @FXML
    private JFXButton liveUpdate, nasUpdate, mailUpdate;

    @FXML
    private JFXTextField livePass, apikey, nasDbName, listid, liveEnd, liveDbName, liveUser, nasEnd, nasUser, nasPass;

    @FXML
    private JFXToggleButton liveEdit, mailEdit, nasEdit;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}
