package Util;

import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Gaurab Pradhan
 */
public class PropertiesUtil {

    private static final String APPLICATION_PROPERTY_FILE = "conf/application.properties";
    private static final String CONGIF_PROPERTY_FILE = "conf/config.properties";
    private static final String LOG4J_PROPERTIES = "conf/Log4j.properties";
    private static Logger log = Logger.getLogger(PropertiesUtil.class.getName());

    static String template_dp;
    static String template_ta_vdc;
    static String template_ta_ward;
    static String output;
    static String path;

    static String apikey;
    static String listId;

    static String liveEnd;
    static String liveDbname;
    static String liveUser;
    static String livePass;

    static String nasEnd;
    static String nasDbname;
    static String nasUser;
    static String nasPass;

    public static String getApikey() {
        return apikey;
    }

    public static void setApikey(String apikey) {
        PropertiesUtil.apikey = apikey;
    }

    public static String getListId() {
        return listId;
    }

    public static void setListId(String listId) {
        PropertiesUtil.listId = listId;
    }

    public static String getLiveEnd() {
        return liveEnd;
    }

    public static void setLiveEnd(String liveEnd) {
        PropertiesUtil.liveEnd = liveEnd;
    }

    public static String getLiveDbname() {
        return liveDbname;
    }

    public static void setLiveDbname(String liveDbname) {
        PropertiesUtil.liveDbname = liveDbname;
    }

    public static String getLiveUser() {
        return liveUser;
    }

    public static void setLiveUser(String liveUser) {
        PropertiesUtil.liveUser = liveUser;
    }

    public static String getLivePass() {
        return livePass;
    }

    public static void setLivePass(String livePass) {
        PropertiesUtil.livePass = livePass;
    }

    public static String getNasEnd() {
        return nasEnd;
    }

    public static void setNasEnd(String nasEnd) {
        PropertiesUtil.nasEnd = nasEnd;
    }

    public static String getNasDbname() {
        return nasDbname;
    }

    public static void setNasDbname(String nasDbname) {
        PropertiesUtil.nasDbname = nasDbname;
    }

    public static String getNasUser() {
        return nasUser;
    }

    public static void setNasUser(String nasUser) {
        PropertiesUtil.nasUser = nasUser;
    }

    public static String getNasPass() {
        return nasPass;
    }

    public static void setNasPass(String nasPass) {
        PropertiesUtil.nasPass = nasPass;
    }

    public static String getPath() {
        return path;
    }

    public static void setPath(String path) {
        PropertiesUtil.path = path;
    }

    public static String getOutput() {
        return output;
    }

    public static void setOutput(String output) {
        PropertiesUtil.output = output;
    }

    public static String getTemplate_dp() {
        return template_dp;
    }

    public static void setTemplate_dp(String template_dp) {
        PropertiesUtil.template_dp = template_dp;
    }

    public static String getTemplate_ta_vdc() {
        return template_ta_vdc;
    }

    public static void setTemplate_ta_vdc(String template_ta_vdc) {
        PropertiesUtil.template_ta_vdc = template_ta_vdc;
    }

    public static String getTemplate_ta_ward() {
        return template_ta_ward;
    }

    public static void setTemplate_ta_ward(String template_ta_ward) {
        PropertiesUtil.template_ta_ward = template_ta_ward;
    }

    public static void loadPropertiesFile() {
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(APPLICATION_PROPERTY_FILE));
            template_dp = prop.getProperty("template_DP");
            template_ta_vdc = prop.getProperty("template_ta_vdc");
            template_ta_ward = prop.getProperty("template_ta_ward");
            output = prop.getProperty("output");
            path = prop.getProperty("path");

            prop.load(new FileInputStream(CONGIF_PROPERTY_FILE));
            apikey = prop.getProperty("api");;
            listId = prop.getProperty("listId");

            liveEnd = prop.getProperty("live1");
            liveDbname = prop.getProperty("live2");
            liveUser = prop.getProperty("live3");
            livePass = prop.getProperty("live4");

            nasEnd = prop.getProperty("nas1");
            nasDbname = prop.getProperty("nas2");
            nasUser = prop.getProperty("nas3");
            nasPass = prop.getProperty("nas4");

        } catch (IOException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();
            log.error(exceptionText);
        }
    }

    public static void loadLog4j() {

        PropertyConfigurator.configure(LOG4J_PROPERTIES);
        log.info("-------------> LOG4J FILE IS LOADED.");
    }
}
