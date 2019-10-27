package epc_tuke;

import tabulky.*;

import java.sql.*;
import java.util.Hashtable;

public class Database {
    private static Database singleInstance = null;
    private final String url = "jdbc:mysql://localhost:3306/tassu?useLegacyDatetimeCode=false&serverTimezone=UTC";
    private final String user  = "root";
    private final String pass  = "qwer";
    private Connection con = null;

    private String sDielo = "select dielo_id from diela where archivacne_cislo = ?";
    private String iDielo = "insert into diela " +
            "(archivacne_cislo, rok_vydania, nazov, podnazov,ISBN, ISSN, miesto_vydania, klucove_slova, odkaz, strany, priloha, kategoria_id) " +
            "values (?,?,?,?,?,?,?,?,?,?,?,?)";
    private String iDieloOhlas = "insert into dielo_ohlas (dielo_id, ohlas_id) values (?,?)";
    private String iAutor = "insert into autori (meno, priezvisko) values (?,?)";
    private String sAutor = "select autor_id from autori where meno = ? and priezvisko = ?";
    private String iOhlas = "insert into ohlasy (rok_vydania, nazov, ISBN, ISSN, miesto_vydania, strany, kategorie_ohlasov_id) values (?,?,?,?,?,?,?)";
    private String iAutorOhlas = "insert into autor_ohlas (autor_id, ohlas_id) values (?,?)";
    private String sOhlas = "select ohlas_id from ohlasy where nazov = ? and rok_vydania = ?";
    private String sAutorIdAPodiel = "select autor_id, percentualny_podiel from autor_dielo_pracovisko where dielo_id = ?";
    private String iAutorDieloPracovisko = "insert into autor_dielo_pracovisko (autor_id, dielo_id, pracovisko_id, percentualny_podiel) values (?,?,?,?)";

    private Statement stKategorie;
    private Statement stPracoviska;
    private PreparedStatement psDielo;
    private PreparedStatement psDieloOhlas;
    private PreparedStatement psDieloSelect;
    private PreparedStatement psAutorSelect;
    private PreparedStatement psAutorInsert;
    private PreparedStatement psOhlas;
    private PreparedStatement psOhlasSelect;
    private PreparedStatement psAutorOhlas;
    private PreparedStatement psAutorIdAPodielByDielo;
    private PreparedStatement psAutorDieloPracovisko;

    private Hashtable<String, Integer> kategorie = new Hashtable<String, Integer>();
    private Hashtable<String, Integer> pracoviska = new Hashtable<String, Integer>();

    private Database(){
        openConnection();
        selectKategorie();
        selectPracoviska();

        System.out.println("Kategorie:");
        for (String s : kategorie.keySet()) {
            System.out.print(s+", ");
        }
    }

    public static Database getInstance(){
        if(singleInstance == null)
            singleInstance = new Database();

        return singleInstance;
    }

    public void openConnection() {
        if (con == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {
                con = DriverManager.getConnection(url, user, pass);
                stKategorie = con.createStatement();
                stPracoviska = con.createStatement();
                psDielo = con.prepareStatement(iDielo, Statement.RETURN_GENERATED_KEYS);
                psDieloOhlas = con.prepareStatement(iDieloOhlas, Statement.RETURN_GENERATED_KEYS);
                psDieloSelect = con.prepareStatement(sDielo);
                psAutorSelect = con.prepareStatement(sAutor);
                psAutorInsert = con.prepareStatement(iAutor, Statement.RETURN_GENERATED_KEYS);
                psOhlas = con.prepareStatement(iOhlas, Statement.RETURN_GENERATED_KEYS);
                psAutorOhlas = con.prepareStatement(iAutorOhlas);
                psOhlasSelect = con.prepareStatement(sOhlas);
                psAutorIdAPodielByDielo = con.prepareStatement(sAutorIdAPodiel);
                psAutorDieloPracovisko = con.prepareStatement(iAutorDieloPracovisko);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection getCon() {
        return con;
    }

    public void closeConnection(){
        try {
            stKategorie.close();
            stPracoviska.close();
            psDielo.close();
            psDieloOhlas.close();
            psDieloSelect.close();
            psAutorInsert.close();
            psAutorSelect.close();
            psOhlas.close();
            psAutorOhlas.close();
            psOhlasSelect.close();
            psAutorIdAPodielByDielo.close();
            psAutorDieloPracovisko.close();
            if (con != null)
                con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet selectDielo(String archivacneCislo){
        ResultSet rs = null;
        try {
            psDieloSelect.setString(1, archivacneCislo);
            rs = psDieloSelect.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public ResultSet insertIntoDiela(Dielo dielo) {
        ResultSet rs = null;
        try {
            psDielo.setString(1, dielo.getArchivacne_cislo());
            psDielo.setInt(2, dielo.getRok_vydania());
            psDielo.setString(3, dielo.getNazov());
            psDielo.setString(4, dielo.getPodnazov());
            psDielo.setString(5, dielo.getISBN());
            psDielo.setString(6, dielo.getISSN());
            psDielo.setString(7, dielo.getMiesto_vydania());
            psDielo.setString(8, dielo.getKlucove_slova());
            psDielo.setString(9, dielo.getOdkaz());
            psDielo.setString(10, dielo.getStrany());
            psDielo.setString(11, dielo.getPriloha());
            psDielo.setInt(12, dielo.getKategoria_id());

            psDielo.executeUpdate();
            rs = psDielo.getGeneratedKeys();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        return rs;
    }

    public void insertIntoDieloOhlas(Integer dielo_id, Integer ohlas_id){
        try {
            psDieloOhlas.setInt(1, dielo_id);
            psDieloOhlas.setInt(2, ohlas_id);

            psDieloOhlas.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet insertIntoAutori(Autor autor){
        ResultSet rs = null;
        try {
            psAutorSelect.setString(1, autor.getMeno());
            psAutorSelect.setString(2, autor.getPriezvisko());
            rs = psAutorSelect.executeQuery();
            if (rs.next()) { //ak v tabulke uz autor je, tak vrati jeho ID
                rs.beforeFirst();
                return rs;
            }
            psAutorInsert.setString(1, autor.getMeno());
            psAutorInsert.setString(2, autor.getPriezvisko());

            psAutorInsert.executeUpdate();
            rs = psAutorInsert.getGeneratedKeys();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return rs;
    }

    public void insertIntoAutorDieloPracovisko(Integer autor_id, Integer dielo_id, Integer pracovisko_id, Integer percentualny_podiel){
        try {
            psAutorDieloPracovisko.setInt(1, autor_id);
            psAutorDieloPracovisko.setInt(2, dielo_id);
            psAutorDieloPracovisko.setInt(3, pracovisko_id);
            if (percentualny_podiel != null && percentualny_podiel != 0)
                psAutorDieloPracovisko.setInt(4, percentualny_podiel);
            else
                psAutorDieloPracovisko.setNull(4, Types.INTEGER);

            psAutorDieloPracovisko.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public ResultSet selectAutorIdAPodielByDielo(Integer dielo_id){
        ResultSet rs = null;

        try {
            psAutorIdAPodielByDielo.setInt(1, dielo_id);
            rs = psAutorIdAPodielByDielo.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }

    public ResultSet insertIntoOhlasy(Ohlas ohlas){
        ResultSet rs = null;

        try {
            psOhlas.setInt(1, ohlas.getRok_vydania());
            psOhlas.setString(2, ohlas.getNazov());
            psOhlas.setString(3, ohlas.getISBN());
            psOhlas.setString(4, ohlas.getISSN());
            psOhlas.setString(5, ohlas.getMiesto_vydania());
            psOhlas.setString(6, ohlas.getStrany());
            psOhlas.setInt(7, ohlas.getKategoria_ohlasu_id());

            psOhlas.executeUpdate();
            rs = psOhlas.getGeneratedKeys();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public void insertIntoAutorOhlas(Integer autor_id, Integer ohlas_id){
        try {
            psAutorOhlas.setInt(1, autor_id);
            psAutorOhlas.setInt(2, ohlas_id);

            psAutorOhlas.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet selectOhlas(String nazov, Integer rok){
        ResultSet rs = null;
        try {
            psOhlasSelect.setString(1, nazov);
            psOhlasSelect.setInt(2, rok);
            rs = psOhlasSelect.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    private void selectKategorie(){
        String query = "select kod, kategoria_id from kategorie";
        ResultSet rs;

        try {
            rs = stKategorie.executeQuery(query);
            while(rs.next()){
                kategorie.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectPracoviska(){
        String query = "select nazov, pracovisko_id from pracoviska";
        ResultSet rs;

        try {
            rs = stPracoviska.executeQuery(query);
            while(rs.next()){
                pracoviska.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Hashtable<String, Integer> getKategorie() {
        return kategorie;
    }

    public Hashtable<String, Integer> getPracoviska() {
        return pracoviska;
    }
}
