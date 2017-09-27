/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DatasetGenerator;

import Home.HomeController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author Gaurab.Pradhan
 */
public class GeneratorController implements Initializable {

    Stage stage;
    String path = null;
    String tablename = "tbl_hrrp_4w";
    String district;
    private static Logger log = Logger.getLogger(GeneratorController.class.getName());

    @FXML
    private ComboBox<String> optionCombo;

    @FXML
    Label dpLbl;

    @FXML
    TextArea logText;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        stage = Home.HomeController.stage;
        district = HomeController.dis;
        if (district.toLowerCase().equals("national")) {
            dpLbl.setVisible(true);
        } else {
            dpLbl.setVisible(false);
        }
        fillCombo();
        logText.setText("");
    }

    @FXML
    void optionComboAction(ActionEvent event) {
        int choice;
        if (!optionCombo.getSelectionModel().isEmpty()) {
            String temp = optionCombo.getSelectionModel().getSelectedItem();
            choice = Integer.parseInt(temp);
            if (district.toLowerCase().equals("national")) {
                switch (choice) {
                    case 1:
                        logText.appendText("Processing : Operational Presence " + tablename + "\n");
                        OperationalPresence.main(logText);
                        break;
                    case 2:
                        logText.appendText("Processing : Standard Product(VDC Level Data) " + tablename + "\n");
                        StandardProduct.standardVDC(logText);
                        break;
                    case 3:
                        logText.appendText("Processing : Standard Product(Ward Level Data) " + tablename + "\n");
                        StandardProduct.standardWard(logText);
                        break;

                    case 4:
                        logText.appendText("Processing : District Profile " + tablename + "\n");
                        DistrictProfile.main(logText);
                        break;
                    default:
                        System.out.println("You did not enter a valid choice.");
                        break;
                }
            } else {
                switch (choice) {
                    case 1:
                        logText.appendText("Processing : Operational Presence " + tablename + "\n");
                        OperationalPresence.main(logText);
                        break;
                    case 2:
                        logText.appendText("Processing : Standard Product(VDC Level Data) " + tablename + "\n");
                        StandardProduct.standardVDC(logText);
                        break;
                    case 3:
                        logText.appendText("Processing : Standard Product(Ward Level Data) " + tablename + "\n");
                        StandardProduct.standardWard(logText);
                        break;

                    default:
                        System.out.println("You did not enter a valid choice.");
                        break;
                }
            }
        }

    }

    private void fillCombo() {
        String[] orgType = {"1", "2", "3", "4"};
        ObservableList obList = FXCollections.observableArrayList();
        if (district.toLowerCase().equals("national")) {
            for (int i = 0; i < orgType.length; i++) {
                obList.add(orgType[i]);
            }
        } else {
            for (int i = 0; i < orgType.length - 1; i++) {
                obList.add(orgType[i]);
            }
        }

        optionCombo.setItems(obList);
    }
}
