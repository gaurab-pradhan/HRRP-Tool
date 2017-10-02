/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import au.com.bytecode.opencsv.CSVReader;
import java.sql.Statement;
import javafx.scene.control.TextArea;

/**
 *
 * @author Gaurab.Pradhan
 */
public class CSVLoader {

    private Connection connection;
    private char seprator;

    /**
     * Public constructor to build CSVLoader object with Connection details. The
     * connection is closed on success or failure.
     *
     * @param connection
     */
    public CSVLoader(Connection connection) {
        this.connection = connection;
        //Set default separator
        this.seprator = ',';
    }

    /**
     * Parse CSV file using OpenCSV library and load in given database table.
     *
     * @param csvFile Input CSV file
     * @param tableName Database table name to import data
     * @param truncateBeforeLoad Truncate the table before inserting new
     * records.
     * @throws Exception
     */
    public void loadCSV(String csvFile, String tableName, boolean truncateBeforeLoad, TextArea logText, int indexSize) throws Exception {
        CSVReader csvReader = null;
        if (null == this.connection) {
            throw new Exception("Not a valid connection.");
        }
        try {

            csvReader = new CSVReader(new FileReader(csvFile), this.seprator);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error occured while executing file. "
                    + e.getMessage());
        }

        String[] headerRow = csvReader.readNext();

        if (null == headerRow) {
            throw new FileNotFoundException(
                    "No columns defined in given CSV file."
                    + "Please check the CSV file format.");
        }

        String questionmarks = StringUtils.repeat("?,", headerRow.length);
        questionmarks = (String) questionmarks.subSequence(0, questionmarks
                .length() - 1);

//        String query = SQL_INSERT.replaceFirst(TABLE_REGEX, tableName);
//        query = query
//                .replaceFirst(KEYS_REGEX, StringUtils.join(headerRow, ","));
//        query = query.replaceFirst(VALUES_REGEX, questionmarks);
        String query = "INSERT INTO " + tableName + " (`sn`, `district`, `vdc`, `ward`, `po`, `impl_partner`, `FundingOrg`, "
                + "`act_type`, `act_sub_type`, `act_name`, `act_detail`, `units`, "
                + "`fund_status`, `act_status`, `total_planned`, `total_reached`, "
                + "`start_date`, `end_date`, `contact_name`, `contact_number`, `email`, "
                + "`comments`, `HRRP_VDC_Code`, `HRRP_Ward_Code`, `act_code`,`round`) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        if (indexSize == 25) {
            query = "INSERT INTO " + tableName + " (`sn`, `district`, `vdc`, `ward`, `po`, `impl_partner`, `FundingOrg`, "
                    + "`act_type`, `act_sub_type`, `act_name`, `act_detail`, `units`, "
                    + "`fund_status`, `act_status`, `total_planned`, `total_reached`, "
                    + "`start_date`, `end_date`, `contact_name`, `contact_number`, `email`, "
                    + "`comments`, `HRRP_VDC_Code`, `HRRP_Ward_Code`, `act_code`) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        }
        System.out.println("Query: " + query);

        String[] nextLine;
        Connection con = null;
        PreparedStatement ps = null;
        Statement stmt = null;
        try {
            con = this.connection;
            con.setAutoCommit(false);
            ps = con.prepareStatement(query);
            if (truncateBeforeLoad) {
                //delete data from table before loading csv
                con.createStatement().execute("DELETE FROM " + tableName);
            }

            final int batchSize = 1000;
            int count = 0;
            Date date = null;
            while ((nextLine = csvReader.readNext()) != null) {

                if (null != nextLine) {
                    int index = 1;

                    for (String string : nextLine) {
                        if (indexSize >= index) {
//                        date = DateUtil.convertToDate(string);
                            if (null != date) {
                                ps.setDate(index++, new java.sql.Date(date.getTime()));
                            } else {
                                ps.setString(index++, string);
                            }
                        }
                    }
                    ps.addBatch();
                }
                if (++count % batchSize == 0) {
                    ps.executeBatch();
                }
            }
            ps.executeBatch(); // insert remaining records
            con.commit();
        } catch (Exception e) {
            logText.appendText("ERROR: Data might in correct format or some column is missing!!!!\n");
            con.rollback();
            e.printStackTrace();
            if (e.toString().contains("temp_hrrp_4w")) {
                DBUtil.createTemptbl();
                loadCSV(csvFile, tableName, truncateBeforeLoad, logText, indexSize);
            }
            throw new Exception(
                    "Error occured while loading data from file to database."
                    + e.getMessage());

        } finally {
            if (null != ps) {
                ps.close();
            }
            if (null != con) {
                con.close();
            }

            csvReader.close();
        }
    }

    public char getSeprator() {
        return seprator;
    }

    public void setSeprator(char seprator) {
        this.seprator = seprator;
    }

}
