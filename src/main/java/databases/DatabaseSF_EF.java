package databases;

import java.sql.*;

public class DatabaseSF_EF {
    private static DatabaseSF_EF singleInstance = null;
    private final String url = "jdbc:mysql://localhost:3306/sf_ef?useLegacyDatetimeCode=false&serverTimezone=UTC";
    private final String user  = "root";
    private final String pass  = "root";
    private Connection con = null;
    private PreparedStatement psGetDataForScheme;
    private Statement statement;

    private DatabaseSF_EF(){
        openConnection();
    }

    public static DatabaseSF_EF getInstance(){
        if(singleInstance == null)
            singleInstance = new DatabaseSF_EF();

        return singleInstance;
    }

    String getDataForSchemeString = "select a.Praca_id, a.Nazov, a.isbn, a.issn, a.miesto_vydania, a.archiv_id, a.rok_vydania, c.Nazov, d.Nazov, e.Meno, e.Priezvisko, a.Kat_EPC, a.Pocet_stran, b.Percent_podiel, a.Vydavatel, a.Podnazov " +
            "from sf_ef.praca a join sf_ef.praca_autor_pracovisko b on a.Praca_ID = b.Praca_ID join sf_ef.pracovisko c on b.Pracovisko_ID = c.Pracovisko_ID join sf_ef.fakulta d on c.Fakulta_ID = d.Fakulta_ID join sf_ef.autor e on b.Autor_ID = e.Autor_ID " +
            "where a.Pocet_stran > 0 and b.Percent_podiel > 0";

    public ResultSet getDataForStarSchema(){

        ResultSet rs = null;
        try {

            rs = psGetDataForScheme.executeQuery();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return rs;
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
                psGetDataForScheme = con.prepareStatement(getDataForSchemeString, Statement.RETURN_GENERATED_KEYS);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public void closeConnection(){
        try {
            statement.close();
            psGetDataForScheme.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getKlucoveSlova(int dieloId){
        String query = "select a.klucove_slovo, a.Praca_ID from sf_ef.klucove_slovo a join sf_ef.praca b on a.Praca_id = b.Praca_ID where a.praca_id = " + dieloId;
        ResultSet rs=null;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            rs = statement.executeQuery(query);
            while (rs.next()){
                stringBuilder.append(rs.getString(1));
                stringBuilder.append(", ");
            }
            if(stringBuilder.length()>2) {
                stringBuilder.substring(0, stringBuilder.length() - 3);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();


    }


    // Pages are in the correct form
}
