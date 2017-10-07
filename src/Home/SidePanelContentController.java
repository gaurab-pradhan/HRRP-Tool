package Home;

import Util.Constants;
import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

public class SidePanelContentController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void changeScreen(ActionEvent event) throws IOException {
        JFXButton btn = (JFXButton) event.getSource();
        BorderPane box = null;
        switch (btn.getText()) {
            case "HOME":
                box = FXMLLoader.load(getClass().getResource(Constants.DASHBOARD));
                HomeController.rootP.setCenter(box);

                break;
            case "CONTACT":
                box = FXMLLoader.load(getClass().getResource(Constants.CONTACT));
                HomeController.rootP.setCenter(box);
                break;
            case "4W CHECK":
                if (HomeController.dis.toLowerCase().equals("national")) {
                    box = FXMLLoader.load(getClass().getResource(Constants.CLEAN_NAT));
                    HomeController.rootP.setCenter(box);
                } else {
                    box = FXMLLoader.load(getClass().getResource(Constants.CLEAN));
                    HomeController.rootP.setCenter(box);
                }
                break;

            case "INSERT CLEAN 4W":
                box = FXMLLoader.load(getClass().getResource(Constants.INSERT));
                HomeController.rootP.setCenter(box);
                break;

            case "DATASET GENERATOR":
                box = FXMLLoader.load(getClass().getResource(Constants.GENERATOR));
                HomeController.rootP.setCenter(box);
                break;
                
            case "SETTING":
                box = FXMLLoader.load(getClass().getResource(Constants.SETTING));
                HomeController.rootP.setCenter(box);
                break;
        }
    }

    @FXML
    private void exit(ActionEvent event) {
        System.exit(0);
    }

}
