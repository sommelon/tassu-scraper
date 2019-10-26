package crepc;

import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.xdevapi.SqlDataResult;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
       ResultSet resultSet = databaseConnection();
      CREPC1scraper crepc1scraper = new CREPC1scraper(resultSet);
     CREPC2scraper crepc2scraper = new CREPC2scraper();
     int crepc1counter = 0;
     int crepc2counter = 0;
       // crepc1scraper.searchForRecordKeyword("Management Tool for Effective  Decision - Business Intelligence ", "ISSN 2048-4186 ");
        //  crepc2scraper.searchForRecordKeyword("Open structured databases' use in destination management ", "ISBN 978-90-828093-5-0 ");
       /* try{
            while (resultSet.next()){
                crepc2scraper.searchForRecordKeyword(resultSet.getString("nazov"), "");
                break;
            }
        }
        catch (SQLException e){
            e.printStackTrace();

        }*/
        try {
            assert resultSet != null;
            while (resultSet.next()) {
                if(crepc2counter > 20){

                    crepc2scraper = new CREPC2scraper();
                    crepc2counter = 0;
                }
                if (resultSet.getString("klucove_slova") == null || resultSet.getString("klucove_slova").equalsIgnoreCase("") || resultSet.getString("klucove_slova").equalsIgnoreCase("---")) {
                    System.out.println("Doing: " + resultSet.getRow());


                    if (Integer.valueOf(resultSet.getString("rok_vydania")) <= 2017) {
                        crepc1counter++;


                        if (resultSet.getString("ISBN") != null) {
                            crepc1scraper.searchForRecordKeyword(resultSet.getString("nazov"), resultSet.getString("ISBN"));
                        } else if (resultSet.getString("ISSN") != null) {
                            crepc1scraper.searchForRecordKeyword(resultSet.getString("nazov"), resultSet.getString("ISSN"));

                        } else {
                            crepc1scraper.searchForRecordKeyword(resultSet.getString("nazov"), "");
                        }
                        System.out.println();
                    } else {
                        crepc2counter++;
                        if (resultSet.getString("ISBN") != null) {
                            crepc2scraper.searchForRecordKeyword(resultSet.getString("nazov"), resultSet.getString("ISBN"));
                        } else if (resultSet.getString("ISSN") != null) {
                            crepc2scraper.searchForRecordKeyword(resultSet.getString("nazov"), resultSet.getString("ISSN"));

                        } else {
                            crepc2scraper.searchForRecordKeyword(resultSet.getString("nazov"), "");
                        }
                        // crepc2scraper.searchForRecordKeyword(resultSet.getString("nazov"), (resultSet.getString("isbn")==null || resultSet.getString("isbn").equalsIgnoreCase(""))? "" : resultSet.getString("isbn"));
                    }
                }
                else{
                    System.out.println("Doing: " + resultSet.getRow() + " (Skipped)");

                }
            }
            } catch(SQLException e){
                e.printStackTrace();
            }



        //  crepc1scraper.searchForRecordKeyword("Monitorovanie a oprava oceľového potrubia používaného na transport plynu a ropy");
        //crepc1scraper.searchForRecordKeyword("Tools for organizational changes managing in companies with high qualified employees");

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
            return stat.executeQuery("SELECT * FROM dielo");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }
}
