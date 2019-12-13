package databases;

import java.sql.*;

public class FEI_FU_DB {
    private static FEI_FU_DB singleInstance = null;
    private final String url = "jdbc:mysql://localhost:3306/fei_fu?useLegacyDatetimeCode=false&serverTimezone=UTC";
    private final String user  = "root";
    private final String pass  = "root";
    private Connection con = null;

    private Statement statement;
    private PreparedStatement psPodielSelect;
    private PreparedStatement psPodielUpdate;

    private FEI_FU_DB(){
        openConnection();
    }

    public static FEI_FU_DB getInstance(){
        if(singleInstance == null)
            singleInstance = new FEI_FU_DB();

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
                psPodielUpdate = con.prepareStatement(uPodiel);
                psPodielSelect = con.prepareStatement(sPodiel);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeConnection(){
        try {
            statement.close();

            psPodielUpdate.close();
            psPodielSelect.close();

            if (con != null)
                con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet selectAutorDielo(){
        String query = "select id_autor, id_publication from author_publication where contribution is null";
        ResultSet rs = null;

        try {
            rs = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }

    private String sPodiel = "select contribution from author_publication where id_publication = ?";

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

    private String uPodiel = "update author_publication set contribution = ? where autor_id = ? and dielo_id = ?";

    public void updatePodiel(int podiel, int autorId, int dieloId){
        try {
            psPodielUpdate.setInt(1, podiel);
            psPodielUpdate.setInt(2, autorId);
            psPodielUpdate.setInt(3, dieloId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
