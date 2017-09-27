/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Home;

import Runner.Runner;
import Util.Constants;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

/**
 *
 * @author gpradhan
 */
public class HomeController extends BorderPane {

    private static Logger log = Logger.getLogger(HomeController.class.getName());
    Runner run;
    public static Stage stage;
    static public String dis;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private BorderPane root;

    public static BorderPane rootP;
    public static JFXDrawer drawerP;
    public static JFXHamburger hamburgerP;

    public void setApp(Runner run, Stage stage, String dis) {
        this.run = run;
        this.stage = stage;
        this.dis = dis;
        setting();
    }

    private void setting() {
        rootP = root;

        try {
            if (dis.toLowerCase().equals("national")) {
                VBox box = FXMLLoader.load(getClass().getResource(Constants.SIDE));
                drawer.setSidePane(box);
            } else {
                VBox box = FXMLLoader.load(getClass().getResource(Constants.SIDE_DIS));
                drawer.setSidePane(box);
            }
            BorderPane box1 = FXMLLoader.load(getClass().getResource(Constants.DASHBOARD));
            root.setCenter(box1);
        } catch (IOException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
        }
        drawerP = drawer;
        hamburgerP = hamburger;
        HamburgerBackArrowBasicTransition transition = new HamburgerBackArrowBasicTransition(hamburger);
        transition.setRate(-1);
        hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            transition.setRate(transition.getRate() * -1);
//            transition.play();

            if (drawer.isShown()) {
                drawer.close();
            } else {
                drawer.open();
            }
        });
    }
}
