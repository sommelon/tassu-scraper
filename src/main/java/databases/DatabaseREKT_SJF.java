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

    private PreparedStatement psPodielSelect;
    private PreparedStatement psPodielUpdate;
    private PreparedStatement psAutorSelect;
    private PreparedStatement psAutorUpdate;
    private PreparedStatement psAutorDelete;

    private DatabaseREKT_SJF(){
        openConnection();
    }

    public static DatabaseREKT_SJF getInstance(){
        if(singleInstance == null)
            singleInstance = new DatabaseREKT_SJF();

        return singleInstance;
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

                psPodielUpdate = con.prepareStatement(uPodiel);
                psPodielSelect = con.prepareStatement(sPodiel);
                psAutorSelect = con.prepareStatement(sAutor);
                psAutorUpdate = con.prepareStatement(uAutor);
                psAutorDelete = con.prepareStatement(dAutor);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeConnection(){
        try {
            statement.close();
            psPocetStranToNull.close();
            psUpdatePocetStran.close();
            psPocetEmptyStranToNull.close();

            psPodielUpdate.close();
            psPodielSelect.close();
            psAutorSelect.close();
            psAutorUpdate.close();
            psAutorDelete.close();

            if (con != null)
                con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    public ResultSet selectAutorDielo(){
        String query = "SELECT author_id, epc_id FROM epcs_authors where part is null or part = 0";
        ResultSet rs = null;

        try {
            rs = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }

    private String sPodiel = "select part from epcs_authors where epc_id = ?";

    public ResultSet selectPodiel(int dieloId){
        ResultSet rs = null;

        try {
            psPodielSelect.setInt(1, dieloId);
            rs = psPodielSelect.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }

    private String uPodiel = "update epcs_authors set part = ? where author_id = ? and epc_id = ?";

    public void updatePodiel(int podiel, int autorId, int dieloId){
        try {
            psPodielUpdate.setInt(1, podiel);
            psPodielUpdate.setInt(2, autorId);
            psPodielUpdate.setInt(3, dieloId);

            psPodielUpdate.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet selectAuthors(){
        String query = "SELECT id, name FROM authors group by name";
        ResultSet rs = null;

        try {
            rs = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }

    private String sAutor = "SELECT id FROM authors where name = ? and id != ?";

    public ResultSet selectAutor(String name, int id){
        ResultSet rs = null;

        try {
            psAutorSelect.setString(1, name);
            psAutorSelect.setInt(2, id);

            rs = psAutorSelect.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }

    private String uAutor = "update epcs_authors set author_id = ? where author_id = ?";

    public void updateAuthor_id(int newId, int oldId){
        try {
            psAutorUpdate.setInt(1, newId);
            psAutorUpdate.setInt(2, oldId);

            psAutorUpdate.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String dAutor = "delete from authors where id = ?";

    public void deleteAuthors(int id){
        try {
            psAutorDelete.setInt(1, id);

            psAutorDelete.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
