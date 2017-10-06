/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import Bean.Contact;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

/**
 *
 * @author gpradhan
 */
public class ParseJson {

    static InputStream is = null;
    private static Logger log = Logger.getLogger(ParseJson.class.getName());
    

    public static List<Contact> getUpdatedContact(String filterText, String api, String listid) throws Exception {
        List<Contact> list = null;
        String url = Constants.exportURL;
        url = url.replaceAll("REPLACEAPI", api);
        url = url.replaceAll("REPLCAELISTID", listid);
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            list = new ArrayList<Contact>();
            int i = 0;
            log.info("Loading Contact: " + filterText);
            while ((line = reader.readLine()) != null) {
                if (i != 0) {
                    Contact bean = new Contact();
                    sb.append(line + "\n");
                    String[] split = line.split("\",");
                    if (split.length > 8) {
                        String email = split[0].replaceAll("\\[", "").replaceAll("\"", "").replaceAll("\\]", "").replace("null", "");
                        String name = split[1].replaceAll("\\[", "").replaceAll("\"", "").replaceAll("\\]", "").replace("null", "");
                        String position = split[2].replaceAll("\\[", "").replaceAll("\"", "").replaceAll("\\]", "").replace("null", "");
                        String org = split[3].replaceAll("\\[", "").replaceAll("\"", "").replaceAll("\\]", "").replace("null", "");
                        String phone = split[4].replaceAll("\\[", "").replaceAll("\"", "").replaceAll("\\]", "").replace("null", "");
                        String agency_type = split[5].replaceAll("\\[", "").replaceAll("\"", "").replaceAll("\\]", "").replace("null", "");
                        String mailing_list = split[6].replaceAll("\\[", "").replaceAll("\"", "").replaceAll("\\]", "").replace("null", "");
//                        String mailing_list = split[7].replaceAll("\\[", "").replaceAll("\"", "").replaceAll("\\]", "").replace("null", "");
//                        String format = split[8].replaceAll("\\[", "").replaceAll("\"", "").replaceAll("\\]", "").replace("null", "");

                        if (filterText.toLowerCase().equals("kathmandu valley")) {
                            if (mailing_list.toLowerCase().contains("kathmandu") || mailing_list.toLowerCase().contains("bhaktapur") || mailing_list.toLowerCase().contains("lalitpur")) {
                                bean.setEmail(email.trim());
                                bean.setName(name.trim());
                                bean.setPosition(position.trim());
                                bean.setOrg(org.trim());
                                bean.setPhone(phone.trim());
                                bean.setAgency_type(agency_type.trim());
                                bean.setMailing_list(mailing_list.trim());
//                                bean.setFormat(format.trim());

                                list.add(bean);
                            }
                        } else if (mailing_list.toLowerCase().contains(filterText.toLowerCase())) {
                            bean.setEmail(email.trim());
                            bean.setName(name.trim());
                            bean.setPosition(position.trim());
                            bean.setOrg(org.trim());
                            bean.setPhone(phone.trim());
                            bean.setAgency_type(agency_type.trim());
                            bean.setMailing_list(mailing_list.trim());
//                            bean.setFormat(format.trim());

                            list.add(bean);
                        }
                    }
                }
                i++;
            }
            System.out.println("Total number of contact = " + list.size());
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
