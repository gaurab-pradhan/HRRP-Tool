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
    private static final String LOG4J_PROPERTIES = "conf/Log4j.properties";
    private static Logger log = Logger.getLogger(PropertiesUtil.class.getName());

    static String template_dp;
    static String template_ta_vdc;
    static String template_ta_ward;
    static String output;
    static String path;

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
