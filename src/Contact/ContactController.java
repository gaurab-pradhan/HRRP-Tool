package Contact;

import Bean.Contact;
import Home.HomeController;
import Util.*;
import javafx.scene.layout.BorderPane;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.input.KeyEvent;
import java.io.*;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * FXML Controller class
 *
 * @author gpradhan
 */
public class ContactController extends BorderPane {

    @FXML
    private TableColumn<Contact, String> emailCol, nameCol, orgCol, desigCol, phoneCol, agencyCol, snCol, mailingListCol;

    @FXML
    TableView<Contact> tableView;
    @FXML
    private JFXTextField search, email, fname, position, org, phone, searchTbl;

    @FXML
    private JFXButton subBtn, resetBtn, searchBtn, reloadBtn;

    @FXML
    private Label errorLabel;

    @FXML
    private ComboBox agencyType;

    @FXML
    JFXToggleButton editBtn;

    @FXML
    private JFXCheckBox dha, dol, gor, ktm, kav, mak, nuw, okh, ram, ras, sin, sp, lp, bk;

    @FXML
    ProgressIndicator progressBar;

    private static Logger log = Logger.getLogger(ContactController.class.getName());
    String listId = PropertiesUtil.getListId();
    String api = PropertiesUtil.getApikey();
    String url = Constants.url;
    List<Contact> mainList;
    ObservableList<Contact> masterList;
    String dis;

    @FXML
    void subscribeAction(ActionEvent event) throws Exception {
        processData();
    }

    @FXML
    void searchAction(ActionEvent event) {
        searchCheck();
    }

    @FXML
    void resetAction(ActionEvent event) {
        resetForm();
    }

    private void processData() throws Exception {
        boolean flag = false;
        String emailTxt = email.getText().trim();
        String fnameTxt = fname.getText().trim();
//        String lnameTxt = lname.getText().trim();
        String posTxt = position.getText().trim();
        String orgTxt = org.getText().trim();
        String phoneTxt = phone.getText().trim();
        String agency_type = null;
        if (!agencyType.getSelectionModel().isEmpty()) {
            agency_type = agencyType.getSelectionModel().getSelectedItem().toString();
        }

        if ((!emailTxt.isEmpty() && !fnameTxt.isEmpty()
                && !posTxt.isEmpty() && !orgTxt.isEmpty() && agency_type != null)
                && (dha.isSelected() || dol.isSelected() || gor.isSelected() || ktm.isSelected() || bk.isSelected() || lp.isSelected()
                || kav.isSelected() || mak.isSelected() || nuw.isSelected() || okh.isSelected()
                || ram.isSelected() || ras.isSelected() || sin.isSelected() || sp.isSelected())) {
            boolean emailCheck = isValidEmail(emailTxt);

            if (!emailCheck) {
                flag = false;
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setContentText("Invalid Email address !!!");
                alert.showAndWait();
            } else {
                flag = true;
            }

            if (flag) {
                try {
                    String name = "anything";
                    String password = api;     //Mailchimp API key
                    String authString = name + ":" + password;

                    byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
                    String authStringEnc = new String(authEncBytes);

                    URL urlConnector = new URL(url);
                    HttpURLConnection httpConnection = (HttpURLConnection) urlConnector.openConnection();
                    httpConnection.setRequestProperty("X-HTTP-Method-Override", "PUT");
                    httpConnection.setRequestMethod("POST");
                    httpConnection.setDoOutput(true);
                    httpConnection.setDoInput(true);
                    httpConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    httpConnection.setRequestProperty("Accept", "application/json");
                    httpConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
                    String input = "{\n"
                            + "    \"email_address\": \"" + emailTxt + "\",\n"
                            + "    \"status_if_new\": \"subscribed\",\n"
                            + "    \"merge_fields\": {\n"
                            + "        \"NAME\": \"" + fnameTxt + "\",\n"
                            + "        \"POSITION\": \"" + posTxt + "\",\n"
                            + "        \"ORG\": \"" + orgTxt + "\",\n"
                            + "        \"PHONE\": \"" + phoneTxt + "\",\n"
                            + "        \"AGENCY\": \"" + agency_type + "\"\n"
                            + "    },\n"
                            + "    \"interests\": {\"" + Constants.national + "\":true," //National
                            + "    \"" + Constants.dhading + "\":" + dha.isSelected() + "," // Dhading
                            + "    \"" + Constants.dolakha + "\":" + dol.isSelected() + "," // Dolakha
                            + "    \"" + Constants.gorkha + "\":" + gor.isSelected() + "," // Gorkha
                            + "    \"" + Constants.kavre + "\":" + kav.isSelected() + "," // Kavre
                            + "    \"" + Constants.makwanpur + "\":" + mak.isSelected() + "," // Makwanpur
                            + "    \"" + Constants.nuwakot + "\":" + nuw.isSelected() + "," // Nuwakot
                            + "    \"" + Constants.okhaldhunga + "\":" + okh.isSelected() + "," // Okhaldhunga
                            + "    \"" + Constants.ramechhap + "\":" + ram.isSelected() + "," // Ramechhap
                            + "    \"" + Constants.rasuwa + "\":" + ras.isSelected() + "," // Rasuwa
                            + "    \"" + Constants.sindhuli + "\":" + sin.isSelected() + "," // Sindhuli
                            + "    \"" + Constants.sindhupalchok + "\":" + sp.isSelected() + "," // Sindhupalchok
                            + "    \"" + Constants.bhaktapur + "\":" + bk.isSelected() + "," // Bhaktapur
                            + "    \"" + Constants.lalitpur + "\":" + lp.isSelected() + "," // lalitpur
                            + "    \"" + Constants.kathmandu + "\":" + ktm.isSelected() + "}\n" // Ktm
                            + "}";
//                    System.out.println(input);
                    OutputStream os = httpConnection.getOutputStream();
                    os.write(input.getBytes("UTF-8"));
                    os.flush();

                    int code = httpConnection.getResponseCode();
                    if (code == 200) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                        alert.setTitle("Error Dialog");
                        alert.setContentText(emailTxt + " has been successfully subscribed or updated." + code);
                        alert.showAndWait();
                        resetForm();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error Dialog");
                        alert.setContentText("ERROR: " + code);
                        alert.showAndWait();
                        resetForm();
                    }

                } catch (MalformedURLException ex) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    ex.printStackTrace(pw);
                    String exceptionText = sw.toString();
                    log.error(exceptionText);
                } catch (IOException ex) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    ex.printStackTrace(pw);
                    String exceptionText = sw.toString();
                    log.error(exceptionText);
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("Some fields are missing !!!");
            alert.showAndWait();
        }
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

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return email.matches(EMAIL_PATTERN);
    }

    @FXML
    private void editBtnClicked(ActionEvent event) {
        if (editBtn.isSelected()) {
            email.setText(search.getText().trim());
            email.setDisable(false);
            fname.setDisable(false);
            position.setDisable(false);
            org.setDisable(false);
            phone.setDisable(false);
            agencyType.setDisable(false);
            subBtn.setDisable(false);
            dha.setDisable(false);
            dol.setDisable(false);
            gor.setDisable(false);
            ktm.setDisable(false);
            bk.setDisable(false);
            lp.setDisable(false);
            kav.setDisable(false);
            mak.setDisable(false);
            nuw.setDisable(false);
            okh.setDisable(false);
            ram.setDisable(false);
            ras.setDisable(false);
            sin.setDisable(false);
            sp.setDisable(false);
        } else {
            email.setDisable(true);
            fname.setDisable(true);
            position.setDisable(true);
            org.setDisable(true);
            phone.setDisable(true);
            agencyType.setDisable(true);
            subBtn.setDisable(true);
            dha.setDisable(true);
            dol.setDisable(true);
            gor.setDisable(true);
            ktm.setDisable(true);
            bk.setDisable(true);
            lp.setDisable(true);
            kav.setDisable(true);
            mak.setDisable(true);
            nuw.setDisable(true);
            okh.setDisable(true);
            ram.setDisable(true);
            ras.setDisable(true);
            sin.setDisable(true);
            sp.setDisable(true);
        }
    }

    public void initialize() throws Exception {
        dis = HomeController.dis;
        fillCombo();
        api = Crypto.decrypt(api);
        listId = Crypto.decrypt(listId);
        url = url.replaceAll("REPLCAELISTID", listId);
        new Thread(new Runnable() {
            public void run() {
                try {
                    masterList = FXCollections.observableArrayList();
                    mainList = new ArrayList<Contact>();
                    mainList = ParseJson.getUpdatedContact(dis, api, listId);
                } catch (Exception ex) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    ex.printStackTrace(pw);
                    String exceptionText = sw.toString();
                    log.error(exceptionText);
                }
                log.info(mainList.size());
                loadTable();
            }
        }).start();
    }

    @FXML
    public void onEnter(ActionEvent ae) {
        searchCheck();
    }

    private void searchCheck() {
        String searchTxt = search.getText().trim();
        if (!searchTxt.isEmpty()) {
            boolean emailCheck = isValidEmail(searchTxt);
            if (emailCheck) {
                try {
                    String hashEmail = HashIt.MD5(searchTxt.toLowerCase().trim());

                    url = (Constants.searchURL.replaceAll("REPLCAELISTID", listId)) + hashEmail;
                    String name = "anything";
                    String password = api;     //Mailchimp API key
                    String authString = name + ":" + password;

                    byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
                    String authStringEnc = new String(authEncBytes);

                    URL urlConnector = new URL(url);
                    HttpURLConnection httpConnection = (HttpURLConnection) urlConnector.openConnection();
                    httpConnection.setRequestMethod("GET");
                    httpConnection.setDoOutput(true);
                    httpConnection.setDoInput(true);
                    httpConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    httpConnection.setRequestProperty("Accept", "application/json");
                    httpConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
                    int code = httpConnection.getResponseCode();
                    if (code == 200) {
                        InputStream is = httpConnection.getInputStream();
                        StringBuilder sb = new StringBuilder();
                        BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            sb.append(line + "\n");
                            System.out.println(line);
                        }
                        br.close();
                        is.close();
                        JSONParser parser = new JSONParser();
                        Object obj = parser.parse(sb.toString());
                        JSONObject jsonObject = (JSONObject) obj;
                        String status = (String) jsonObject.get("status");
                        if (status.toLowerCase().equals("subscribed")) {
                            email.setText((String) jsonObject.get("email_address"));

                            Object nestObj = parser.parse(jsonObject.get("merge_fields").toString());
                            JSONObject nestJsonObject = (JSONObject) nestObj;

                            fname.setText((String) nestJsonObject.get("FNAME"));
//                            lname.setText((String) nestJsonObject.get("LNAME"));
                            position.setText((String) nestJsonObject.get("POSITION"));
                            org.setText((String) nestJsonObject.get("ORG"));
                            phone.setText((String) nestJsonObject.get("CNUMBER"));
                            agencyType.getSelectionModel().select((String) nestJsonObject.get("AGENCYTYPE"));

                            nestObj = parser.parse(jsonObject.get("interests").toString());
                            nestJsonObject = (JSONObject) nestObj;

                            boolean national = ((boolean) nestJsonObject.get(Constants.national));
                            boolean dhading = ((boolean) nestJsonObject.get(Constants.dhading));
                            boolean dolakha = ((boolean) nestJsonObject.get(Constants.dolakha));
                            boolean gorkha = ((boolean) nestJsonObject.get(Constants.gorkha));
                            boolean kavrepalanchok = ((boolean) nestJsonObject.get(Constants.kavre));
                            boolean makwanpur = ((boolean) nestJsonObject.get(Constants.makwanpur));
                            boolean nuwakot = ((boolean) nestJsonObject.get(Constants.nuwakot));
                            boolean okhaldhunga = ((boolean) nestJsonObject.get(Constants.okhaldhunga));
                            boolean ramechhap = ((boolean) nestJsonObject.get(Constants.ramechhap));
                            boolean rasuwa = ((boolean) nestJsonObject.get(Constants.rasuwa));
                            boolean sindhuli = ((boolean) nestJsonObject.get(Constants.sindhuli));
                            boolean sindhupalchok = ((boolean) nestJsonObject.get(Constants.sindhupalchok));
                            boolean kath = ((boolean) nestJsonObject.get(Constants.kathmandu));
                            boolean bkt = ((boolean) nestJsonObject.get(Constants.bhaktapur));
                            boolean lalit = ((boolean) nestJsonObject.get(Constants.lalitpur));
                            dha.setSelected(dhading);
                            dol.setSelected(dolakha);
                            gor.setSelected(gorkha);
                            kav.setSelected(kavrepalanchok);
                            mak.setSelected(makwanpur);
                            nuw.setSelected(nuwakot);
                            okh.setSelected(okhaldhunga);
                            ram.setSelected(ramechhap);
                            ras.setSelected(rasuwa);
                            sin.setSelected(sindhuli);
                            sp.setSelected(sindhupalchok);
                            ktm.setSelected(kath);
                            bk.setSelected(bkt);
                            lp.setSelected(lalit);
                            editBtn.setDisable(false);
                        } else {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Information");
                            alert.setContentText("Code: " + code + " This Email address was unsubscribed from our mailing list.\n If you really want to subscribe this contact again, please send request to im.national@hrrpnepal.org");
                            alert.showAndWait();
                        }
                    } else if (code == 404) {
                        editBtn.setDisable(false);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Information");
                        alert.setContentText("Code: " + code + " This Email address is not in our mailing list");
                        alert.showAndWait();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Error Code");
                        alert.setContentText("ERROR CODE: " + String.valueOf(code));
                        alert.showAndWait();
                        log.info("ERROR CODE: " + String.valueOf(code) + " " + searchTxt);
                    }
                } catch (MalformedURLException ex) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    ex.printStackTrace(pw);
                    String exceptionText = sw.toString();
                    log.error(exceptionText);
                } catch (IOException ex) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    ex.printStackTrace(pw);
                    String exceptionText = sw.toString();
                    log.error(exceptionText);
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Dialog");
                    alert.setContentText("Check your internet connection !!!");
                    alert.showAndWait();
                } catch (NoSuchAlgorithmException ex) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    ex.printStackTrace(pw);
                    String exceptionText = sw.toString();
                    log.error(exceptionText);
                } catch (ParseException ex) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    ex.printStackTrace(pw);
                    String exceptionText = sw.toString();
                    log.error(exceptionText);
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setContentText("Invalid Email address !!!");
                alert.showAndWait();
            }
        }
    }

    private void resetForm() {
        search.clear();
        email.clear();
        fname.clear();
        position.clear();
        org.clear();
        phone.clear();
        agencyType.getItems().clear();
        dha.setSelected(false);
        dol.setSelected(false);
        gor.setSelected(false);
        ktm.setSelected(false);
        kav.setSelected(false);
        mak.setSelected(false);
        nuw.setSelected(false);
        okh.setSelected(false);
        ram.setSelected(false);
        ras.setSelected(false);
        sin.setSelected(false);
        sp.setSelected(false);
        lp.setSelected(false);
        bk.setSelected(false);
        fillCombo();
    }

    private void fillCombo() {
        String[] orgType = {"NRA Central", "NRA District", "MoFALD CLPIU & DLPIU", "MoUD CLPIU", "MoUD DLPIU Engineers", "District Government", "Other Government Agencies", "Donor", "Partner Organizations", "Private Sectors", "Journalist/Media", "Others"};
        ObservableList obList = FXCollections.observableArrayList();
        for (int i = 0; i < orgType.length; i++) {
            obList.add(orgType[i]);
        }
        agencyType.setItems(obList);
    }

    private void loadTable() {
        masterList = FXCollections.observableList(mainList);
        tableView.setItems(masterList);
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        orgCol.setCellValueFactory(new PropertyValueFactory<>("org"));
        desigCol.setCellValueFactory(new PropertyValueFactory<>("position"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        agencyCol.setCellValueFactory(new PropertyValueFactory<>("agency_type"));
        mailingListCol.setCellValueFactory(new PropertyValueFactory<>("mailing_list"));
        snCol.setCellValueFactory(column -> new ReadOnlyObjectWrapper(tableView.getItems().indexOf(column.getValue()) + 1));
    }

    @FXML
    public void filterAction(KeyEvent evt) {
        FilteredList<Contact> filteredData = new FilteredList<>(masterList, p -> true);
        searchTbl.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(dataBean -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (dataBean.getAgency_type().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (dataBean.getEmail().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (dataBean.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (dataBean.getOrg().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (dataBean.getPosition().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (dataBean.getPhone().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
        SortedList<Contact> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        tableView.setItems(sortedData);
    }

    @FXML
    void reloadAction(ActionEvent event) throws Exception {
        log.info("Reloaded");
        masterList = FXCollections.observableArrayList();
        mainList = new ArrayList<Contact>();
        mainList = ParseJson.getUpdatedContact(dis, api, listId);
        log.info("Total Contacts: " + mainList.size());
        loadTable();
    }

    @FXML
    void downloadAction(ActionEvent event) {
        DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
        Calendar cal = Calendar.getInstance();
        String todayDate = dateFormat.format(cal.getTime());
        String path = "Output/";
        String fname = todayDate + "_ContactList_" + dis + ".csv";
        File f = new File(path);
        if (!f.exists()) {
            if (f.mkdirs()) {
                path = f.getAbsolutePath();
                System.out.println("Directory is created!");
                log.info(path + " Directory is created");
            } else {
                System.out.println("Failed to create directory!");
                log.info("Failed to create directory!");
            }
        } else {
            path = f.getAbsolutePath();
        }
        BufferedWriter bw = null;
        try {
            File file = new File(path + "/" + fname);
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            bw = new BufferedWriter(fw);
            bw.write("SN.,Name,Designation,Organisation,Agency Type,Contact Number,Email ID,MailingList");
            bw.write("\n");
            for (int i = 0; i < mainList.size(); i++) {
                String content = i + 1 + "," + "\"" + mainList.get(i).getName() + "\"" + "," + "\"" + mainList.get(i).getPosition() + "\"" + ","
                        + "\"" + mainList.get(i).getOrg() + "\"" + "," + "\"" + mainList.get(i).getAgency_type() + "\"" + ","
                        + "\"" + mainList.get(i).getPhone() + "\"" + "," + mainList.get(i).getEmail() + "," + "\"" + mainList.get(i).getMailing_list() + "\"";
                bw.write(content);
                bw.write("\n");
            }
            bw.close();
            log.info(path + "/" + fname + " created");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Completed");
            alert.setContentText(path + "/" + fname + " created");
            alert.showAndWait();
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);

        }
    }

    @FXML
    void unsubscribeAction(ActionEvent event) {
        try {
            String name = "anything";
            String password = api;     //Mailchimp API key
            String authString = name + ":" + password;

            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            String authStringEnc = new String(authEncBytes);

            URL urlConnector = new URL(url);
            HttpURLConnection httpConnection = (HttpURLConnection) urlConnector.openConnection();
            httpConnection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            httpConnection.setRequestMethod("POST");;
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);
            httpConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpConnection.setRequestProperty("Accept", "application/json");
            httpConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            String input = "{\n"
                    + "    \"status\": \"unsubscribed\"\n"
                    + "}";

            OutputStream os = httpConnection.getOutputStream();
            os.write(input.getBytes("UTF-8"));
            os.flush();
            int code = httpConnection.getResponseCode();
            if (code == 200) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                        alert.setTitle("Error Dialog");
                alert.setContentText(search.getText() + " has been successfully unsubscribed." + code);
                alert.showAndWait();
                resetForm();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setContentText("ERROR: " + code);
                alert.showAndWait();
                resetForm();
            }

        } catch (MalformedURLException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
        } catch (IOException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
        }
    }
}
