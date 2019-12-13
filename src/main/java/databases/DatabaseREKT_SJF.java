package databases;

import java.sql.*;

public class DatabaseREKT_SJF {
    private static DatabaseREKT_SJF singleInstance = null;
    private final String url = "jdbc:mysql://localhost:3306/rekt_sjf?useLegacyDatetimeCode=false&serverTimezone=UTC";
    private final String user  = "root";
    private final String pass  = "root";
    private Connection con = null;
    private Statement statement;
    private PreparedStatement psUpdatePocetStran;
    private PreparedStatement psPocetStranToNull;
    private PreparedStatement psPocetEmptyStranToNull;

    private DatabaseREKT_SJF(){
        openConnection();
    }

    public static DatabaseREKT_SJF getInstance(){
        if(singleInstance == null)
            singleInstance = new DatabaseREKT_SJF();

        return singleInstance;
    }
    //TODO Convert pages to correct form
    private String updatePocetStran = "UPDATE epcs set numberOfPages = ? where id = ?";
    public void updatePocetStran(int dielo_id, int pocetStran){
        try {
            if(pocetStran <= 0 || pocetStran > 1000){
                psUpdatePocetStran.setNull(1, Types.INTEGER);
            }else{
                psUpdatePocetStran.setInt(1, pocetStran);
            }
            psUpdatePocetStran.setInt(2,dielo_id);
            psUpdatePocetStran.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public ResultSet selectStrany(){
        String query = "select id, numberOfPages from epcs where numberOfPages is not null";
        ResultSet rs = null;

        try {
            rs = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }
    private String pocetStranToNull = "UPDATE epcs set numberOfPages = REPLACE(numberOfPages,'null',null) where numberOfPages like 'null'";
    private String pocetEmptyStranToNull = "UPDATE epcs set numberOfPages = REPLACE(numberOfPages,'',null) where numberOfPages like ''";
    public void convertToNull(){
       try {
           psPocetEmptyStranToNull.executeUpdate();
           psPocetStranToNull.executeUpdate();
       } catch (SQLException e) {
           e.printStackTrace();
       }

    }

    private void openConnection() {
        if (con == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {
                con = DriverManager.getConnection(url, user, pass);
                statement = con.createStatement();
                psPocetStranToNull = con.prepareStatement(pocetStranToNull);
                psUpdatePocetStran = con.prepareStatement(updatePocetStran);
                psPocetEmptyStranToNull = con.prepareStatement(pocetEmptyStranToNull);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}
