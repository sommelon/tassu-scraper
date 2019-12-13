package databases;

import java.sql.*;

public class FVT_FMMR_DB {
    private static FVT_FMMR_DB singleInstance = null;
    private final String url = "jdbc:mysql://localhost:3306/fvt_fmmr?useLegacyDatetimeCode=false&serverTimezone=UTC";
    private final String user  = "root";
    private final String pass  = "root";
    private Connection con = null;
    private Statement statement;
    private PreparedStatement psUpdatePocetStran;
    private PreparedStatement psPocetStranToNull;
    private PreparedStatement psPocetEmptyStranToNull;

    private FVT_FMMR_DB(){
        openConnection();
    }

    public static FVT_FMMR_DB getInstance(){
        if(singleInstance == null)
            singleInstance = new FVT_FMMR_DB();

        return singleInstance;
    }
    private String updatePocetStran = "UPDATE zaznam set pocet_stran = ? where idZaznam = ?";
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
        String query = "select idZaznam, pocet_stran from zaznam where pocet_stran is not null";
        ResultSet rs = null;

        try {
            rs = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }
    private String pocetStranToNull = "UPDATE zaznam set pocet_stran = REPLACE(pocet_stran,'null',null) where pocet_stran like 'null'";
    private String pocetEmptyStranToNull = "UPDATE zaznam set pocet_stran = REPLACE(pocet_stran,'',null) where pocet_stran like ''";
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
