package epc_tuke;

import epc_tuke.tabulky.*;

import java.sql.*;
import java.util.List;

public class Inserter {
    private Dielo dielo;
    private List<Autor> autori;
    private List<Integer> podiely;
    private List<Ohlas> ohlasy;
    private Fakulta fakulta;
    private Pracovisko pracovisko;

    private static final String url = "jdbc:mysql://localhost:3306/tassu?useLegacyDatetimeCode=false&serverTimezone=UTC";
    private static final String user  = "root";
    private static final String pass  = "qwer";

    private static Connection con = null;

    //TODO prerobit, aby to bolo flexibilnejsie. Cudzie kluce by uz mali byt v objektoch a nemali by sa nastavovat v tychto metodach.
    //ohlasy a podiely mozu byt null
    public Inserter(Dielo dielo, List<Autor> autori, List<Integer> podiely, List<Ohlas> ohlasy/*, Fakulta fakulta, Pracovisko pracovisko*/){
        this.dielo = dielo;
        this.autori = autori;
        this.podiely = podiely;
        this.ohlasy = ohlasy;
//        this.fakulta = fakulta;
//        this.pracovisko = pracovisko;


        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            con = DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getCon() {
        return con;
    }

    public static void closeConnection(){
        try {
            if (con != null)
                con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertIntoDielo() {
        //todo zmenit zaznam na dielo v DB
        String query = "insert into diela (dielo_id, nazov, archivacne_cislo, rok_vydania, ISBN, miesto_vydania, klucove_slova, odkaz, ISSN, podnazov, strany, kategoria_id) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, dielo.getId());
            ps.setString(2, dielo.getNazov());
            ps.setString(3, dielo.getArchivacne_cislo());
            ps.setInt(4, dielo.getRok_vydania());
            ps.setString(5, dielo.getISBN());
            ps.setString(6, dielo.getMiesto_vydania());
            ps.setString(7, dielo.getKlucove_slova());
            ps.setString(8, dielo.getOdkaz());
            ps.setString(9, dielo.getISSN());
            ps.setString(10, dielo.getPodnazov());
            ps.setString(11, dielo.getStrany());
            ps.setInt(12, -1);

            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void insertIntoAutor(Autor autor){
        String query = "insert into autori (autor_id, meno, priezvisko) values (?,?,?)";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, autor.getId());
            ps.setString(2, autor.getMeno());
            ps.setString(3, autor.getPriezvisko());

            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        //TODO: close PreparedStatement in a finally block
//        finally {
//            ps.close();
//        }
    }

    public void insertIntoAutorZaznamPracovisko(Integer autor_id, Integer dielo_id, Integer pracovisko_id, Integer percentualny_podiel){
        String query = "insert into autor_zaznam_pracovisko (autor_id, dielo_id, pracovisko_id, percentualny_podiel) values (?,?,?,?)";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, autor_id);
            ps.setInt(2, dielo_id);
            ps.setInt(3, pracovisko_id);
            if (percentualny_podiel != null)
                ps.setInt(4, percentualny_podiel);
            else
                ps.setNull(4, Types.INTEGER);

            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void insertIntoOhlas(){
        String query = "insert into ohlasy (ohlas_id, nazov, dielo_id) values (?,?,?)";
        for (Ohlas ohlas : ohlasy) {
            try {
                PreparedStatement ps = con.prepareStatement(query);
                ps.setInt(1, ohlas.getId());
                ps.setString(2, ohlas.getNazov());
                ps.setInt(3, dielo.getId());

                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }
}
