package Runner;

import Home.HomeController;
import Home.LocationController;
import Login.*;
import Util.*;
import java.io.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import java.util.HashMap;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

/**
 *
 * @author gpradhan
 */
public class Runner extends Application {

    String title = "HRRP TOOL";
    private static Logger log = Logger.getLogger(Runner.class.getName());
    private Stage stage;
    static HashMap<String, String> hmap;
    static Connection con = null;
    static String location = null;

    public static void setLocation(String location) {
        Runner.location = location;
    }

    public void init() {
        PropertiesUtil.loadLog4j();
        log.info("~~~~~~~~~~~> LOG PROPERTIES FILE LOADED <~~~~~~~~~~\n\n");
        log.info("~~~~~~~~~~~> LOADING APPLICATION PROPERTIES FILE.........");
        PropertiesUtil.loadPropertiesFile();
        log.info("~~~~~~~~~~~> APPLICATION PROPERTIES FILE LOADED <~~~~~~~~~~\n\n");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        init();
        stage = primaryStage;
        stage.setTitle(title);
        stage.getIcons().add(new Image(Constants.IMAGE));
        gotoHome(); // this is first scene
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        hmap = new HashMap<String, String>();
        hmap.put("32ff00f215", "National");
        hmap.put("e6267bf447", "Dhading");
        hmap.put("5c68675513", "Dolakha");
        hmap.put("290636ad82", "Gorkha");
        hmap.put("75122f4a77", "Kavrepalanchok");
        hmap.put("ab1f50bca0", "Makwanpur");
        hmap.put("b6fbd2a31d", "Nuwakot");
        hmap.put("0facae5d50", "Okhaldhunga");
        hmap.put("29e91b4476", "Ramechhap");
        hmap.put("1014355c21", "Rasuwa");
        hmap.put("e25b8f181d", "Sindhuli");
        hmap.put("e7c46e2586", "Sindhupalchok");
        hmap.put("1bc27fbd1e", "Kathmandu Valley");
        DBUtil.main(args);
        location = DBUtil.getLocation();
        launch(args);
    }

    public void gotoHome() throws Exception {
        String district = hmap.get(location);
        if (location == null) {
            LocationController loc = (LocationController) replaceSceneContent(Constants.LOCATION);
            loc.setApp(this, stage);
        } else {
            try {
                new Thread(new Runnable() {
                    public void run() {
                        Connection con = DBUtil.getConnectionSQLite();
                        try {
                            Statement stmt = con.createStatement();
                            ResultSet rs = null;
                            rs = stmt.executeQuery("SELECT count(*) FROM tbl_st_target");
                            int count = 0;
                            if (rs.next()) {
                                count = rs.getInt(1);
                            }
                            if (count == 0) {
                                stmt.executeUpdate("DELETE from tbl_vt_target");
                                STVT_Target stvt = new STVT_Target();
                                stvt.st_vt(district);
                            }
                            rs.close();
                            stmt.close();
                            con.close();
                        } catch (Exception ex) {
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            ex.printStackTrace(pw);
                            String exceptionText = sw.toString();
                            log.error(exceptionText);
                        }
                    }
                }).start();
                HomeController home = (HomeController) replaceSceneContent(Constants.HOME);
                home.setApp(this, stage, district);
            } catch (Exception ex) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                String exceptionText = sw.toString();
                log.error(exceptionText);
            }
        }
    }

    public void gotoLogin(Stage stage) throws Exception {
        this.stage = stage;
        LoginController login = (LoginController) replaceSceneContent(Constants.LOGIN);
        login.setApp(this, stage);
    }

    public Node replaceSceneContent(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        InputStream in = Runner.class.getResourceAsStream(fxml);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(Runner.class.getResource(fxml));
        BorderPane page;
        try {
            page = (BorderPane) loader.load(in);
        } finally {
            in.close();
        }
        Scene scene = new Scene(page, Color.TRANSPARENT);
        stage.setScene(scene);
        return (Node) loader.getController();
    }
}
