package crepc;

import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.xdevapi.SqlDataResult;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
        ResultSet resultSet = databaseConnection();
        CREPC1scraper crepc1scraper = new CREPC1scraper(resultSet);
        int crepc2counter = 0;

        try {
            assert resultSet != null;
            while (resultSet.next()) {

                if (resultSet.getString("klucove_slova") == null || resultSet.getString("klucove_slova").equalsIgnoreCase("") || resultSet.getString("klucove_slova").equalsIgnoreCase("---")) {
                    System.out.println("Doing 2017: " + resultSet.getRow());


                    if (Integer.valueOf(resultSet.getString("rok_vydania")) <= 2017) {
                        if (resultSet.getString("ISBN") != null) {
                            crepc1scraper.searchForRecordKeyword(resultSet.getString("nazov"), resultSet.getString("ISBN"));
                        } else if (resultSet.getString("ISSN") != null) {
                            crepc1scraper.searchForRecordKeyword(resultSet.getString("nazov"), resultSet.getString("ISSN"));

                        } else {
                            crepc1scraper.searchForRecordKeyword(resultSet.getString("nazov"), "");
                        }
                        System.out.println();
                    }
                } else {
                    System.out.println("Doing 2017: " + resultSet.getRow() + " (Skipped)");

                }
            }
            resultSet = databaseConnection();
            CREPC2scraper crepc2scraper = new CREPC2scraper();

            while (resultSet.next()) {
                if (crepc2counter > 20) {
                    crepc2scraper.close();
                    crepc2scraper = new CREPC2scraper();
                    crepc2counter = 0;
                }
                if (resultSet.getString("klucove_slova") == null || resultSet.getString("klucove_slova").equalsIgnoreCase("") || resultSet.getString("klucove_slova").equalsIgnoreCase("---")) {
                    System.out.println("Doing 2018: " + resultSet.getRow());


                    if (Integer.valueOf(resultSet.getString("rok_vydania")) > 2017) {
                        crepc2counter++;
                        if (resultSet.getString("ISBN") != null) {
                            crepc2scraper.searchForRecordKeyword(resultSet.getString("nazov"), resultSet.getString("ISBN"));
                        } else if (resultSet.getString("ISSN") != null) {
                            crepc2scraper.searchForRecordKeyword(resultSet.getString("nazov"), resultSet.getString("ISSN"));

                        } else {
                            crepc2scraper.searchForRecordKeyword(resultSet.getString("nazov"), "");
                        }
                    }
                } else {
                    System.out.println("Doing 2018: " + resultSet.getRow() + " (Skipped)");

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static ResultSet databaseConnection() {
        Connection con = null;


        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String uName = "root";
            String uPass = "root";
            String host = "jdbc:mysql://localhost:3306/tssu?autoReconnect=true&useSSL=false&serverTimeZone=Europe/Berlin";
            con = DriverManager.getConnection(host, uName, uPass);

            Statement stat = con.createStatement();
            return stat.executeQuery("SELECT * FROM diela");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }
}
