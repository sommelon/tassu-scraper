package epc_tuke;

import tabulky.*;

import java.sql.*;
import java.util.List;

public class Inserter {
    private static final String url = "jdbc:mysql://localhost:3306/tassu?useLegacyDatetimeCode=false&serverTimezone=UTC";
    private static final String user  = "root";
    private static final String pass  = "qwer";
    private static Connection con = null;

    //ohlasy a podiely mozu byt null
    private Inserter(){
    }

    public static void openConnection() {
        if (con == null) {
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
    //TODO zmenit parameterIndex vo vsetkych metodach
    public static void insertIntoDielo(Dielo dielo) {

        String query = "insert into diela (nazov, archivacne_cislo, rok_vydania, ISBN, miesto_vydania, klucove_slova, odkaz, ISSN, podnazov, strany, kategoria_id) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement ps = con.prepareStatement(query);
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

    public static void insertIntoAutor(List<Autor> autori){
        String query = "insert into autori (meno, priezvisko) values (?,?,?)";
        for (Autor autor : autori) {
            try {
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(2, autor.getMeno());
                ps.setString(3, autor.getPriezvisko());

                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public static void insertIntoAutorZaznamPracovisko(Integer autor_id, Integer dielo_id, Integer pracovisko_id, Integer percentualny_podiel){
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

    public static void insertIntoOhlas(List<Ohlas> ohlasy, Integer dielo_id){
        String query = "insert into ohlasy (ohlas_id, nazov, dielo_id) values (?,?,?)";
        for (Ohlas ohlas : ohlasy) {
            try {
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(2, ohlas.getNazov());
                ps.setInt(3, dielo_id);

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
