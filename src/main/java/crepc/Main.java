package crepc;

import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.xdevapi.SqlDataResult;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
       ResultSet resultSet = databaseConnection();
       CREPC1scraper crepc1scraper = new CREPC1scraper(resultSet);
       CREPC2scraper crepc2scraper = new CREPC2scraper();
      // crepc2scraper.searchForRecordKeyword("Inhibícia korózie oceľových potrubí", "");
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


                if (Integer.valueOf(resultSet.getString("rok_vydania")) <= 2017) {


                    if (resultSet.getString("ISBN") != null) {
                        crepc1scraper.searchForRecordKeyword(resultSet.getString("nazov"), resultSet.getString("ISBN"));
                    } else if (resultSet.getString("ISSN") != null) {
                        crepc1scraper.searchForRecordKeyword(resultSet.getString("nazov"), resultSet.getString("ISSN"));

                    } else {
                        crepc1scraper.searchForRecordKeyword(resultSet.getString("nazov"), "");
                    }
                } else {
                    crepc2scraper.searchForRecordKeyword(resultSet.getString("nazov"), "");
                }
            }
        } catch (SQLException e) {
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
