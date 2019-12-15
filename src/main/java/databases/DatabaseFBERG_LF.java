package databases;

import crepc.CREPC2scraper;
import tabulky.*;

import java.sql.*;
import java.util.Hashtable;

public class DatabaseFBERG_LF {
    private static DatabaseFBERG_LF singleInstance = null;
    private final String url = "jdbc:mysql://localhost:3306/fberg_lf?useLegacyDatetimeCode=false&serverTimezone=UTC";
    private final String user  = "root";
    private final String pass  = "root";
    private Connection con = null;

    private Statement statement;
    private PreparedStatement psDielo;
    private PreparedStatement psDieloOhlas;
    private PreparedStatement psDieloSelect;
    private PreparedStatement psAutorSelect;
    private PreparedStatement psAutorInsert;
    private PreparedStatement psOhlas;
    private PreparedStatement psOhlasSelect;
    private PreparedStatement psAutorOhlas;
    private PreparedStatement psAutorIdAPodielAPracoviskoByDielo;
    private PreparedStatement psAutorDieloPracovisko;

    private PreparedStatement psKlucoveSlova;
    private PreparedStatement psKlucoveSlovoId;
    private PreparedStatement psDieloKlucoveSlovo;
    private PreparedStatement psUpdatePocetStran;

    private PreparedStatement psPodielSelect;
    private PreparedStatement psPodielUpdate;

    private Hashtable<String, Integer> kategorie = new Hashtable<String, Integer>();
    private Hashtable<String, Integer> pracoviska = new Hashtable<String, Integer>();
    private PreparedStatement psGetDataForScheme;

    private DatabaseFBERG_LF(){
        openConnection();
        selectKategorie();
        selectPracoviska();

//        System.out.println("Kategorie:");
//        for (String s : kategorie.keySet()) {
//            System.out.print(s+", ");
//        }
    }

    public static DatabaseFBERG_LF getInstance(){
        if(singleInstance == null)
            singleInstance = new DatabaseFBERG_LF();

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
                psDielo = con.prepareStatement(iDielo, Statement.RETURN_GENERATED_KEYS);
                psDieloOhlas = con.prepareStatement(iDieloOhlas, Statement.RETURN_GENERATED_KEYS);
                psDieloSelect = con.prepareStatement(sDielo);
                psAutorSelect = con.prepareStatement(sAutor);
                psAutorInsert = con.prepareStatement(iAutor, Statement.RETURN_GENERATED_KEYS);
                psOhlas = con.prepareStatement(iOhlas, Statement.RETURN_GENERATED_KEYS);
                psAutorOhlas = con.prepareStatement(iAutorOhlas);
                psOhlasSelect = con.prepareStatement(sOhlas);
                psAutorIdAPodielAPracoviskoByDielo = con.prepareStatement(sAutorIdAPodielAPracovisko);
                psAutorDieloPracovisko = con.prepareStatement(iAutorDieloPracovisko);

                psPodielUpdate = con.prepareStatement(uPodiel);
                psPodielSelect = con.prepareStatement(sPodiel);
                psKlucoveSlova = con.prepareStatement(iKlucoveSlova, Statement.RETURN_GENERATED_KEYS);
                psKlucoveSlovoId = con.prepareStatement(klucoveSlovoId);
                psDieloKlucoveSlovo = con.prepareStatement(iDieloKlucoveSlovo);
                psUpdatePocetStran = con.prepareStatement(updatePocetStran);
                psGetDataForScheme = con.prepareStatement(getDataForSchemeString, Statement.RETURN_GENERATED_KEYS);
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
            statement.close();
            psDielo.close();
            psDieloOhlas.close();
            psDieloSelect.close();
            psAutorInsert.close();
            psAutorSelect.close();
            psOhlas.close();
            psAutorOhlas.close();
            psOhlasSelect.close();
            psAutorIdAPodielAPracoviskoByDielo.close();
            psAutorDieloPracovisko.close();

            psPodielUpdate.close();
            psPodielSelect.close();
            psKlucoveSlova.close();
            psKlucoveSlovoId.close();
            psDieloKlucoveSlovo.close();
            psUpdatePocetStran.close();
            psGetDataForScheme.close();
            if (con != null)
                con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String sDielo = "select dielo_id from diela where archivacne_cislo = ?";

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
    String getDataForSchemeString = "select a.dielo_id, a.nazov, a.isbn, a.issn, a.miesto_vydania, a.vydanie, a.archivacne_cislo , a.rok_vydania , c.nazov, d.nazov, d.skratka , e.meno, e.priezvisko, f.popis, f.kod, a.strany, b.percentualny_podiel, a.podnazov " +
            "from fberg_lf.diela a join fberg_lf.autor_dielo_pracovisko b on a.dielo_id = b.dielo_id join  fberg_lf.pracoviska c on b.pracovisko_id = c.pracovisko_id join fberg_lf.fakulty d on c.fakulta_id = d.fakulta_id  join  fberg_lf.autori e on b.autor_id = e.autor_id join fberg_lf.kategorie f on a.kategoria_id = f.kategoria_id " +
            "where a.strany > 0 and b.percentualny_podiel > 0";

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


    private String iDielo = "insert into diela " +
            "(archivacne_cislo, rok_vydania, nazov, podnazov,ISBN, ISSN, miesto_vydania, odkaz, strany, vydanie, kategoria_id) " +
            "values (?,?,?,?,?,?,?,?,?,?,?)";

    public ResultSet insertIntoDiela(Dielo dielo) {
        ResultSet rs = null;
        try {
            psDielo.setString(1, dielo.getArchivacne_cislo());
            psDielo.setString(2, dielo.getRok_vydania());
            psDielo.setString(3, dielo.getNazov());
            psDielo.setString(4, dielo.getPodnazov());
            psDielo.setString(5, dielo.getISBN());
            psDielo.setString(6, dielo.getISSN());
            psDielo.setString(7, dielo.getMiesto_vydania());
            psDielo.setString(8, dielo.getOdkaz());
            psDielo.setString(9, dielo.getStrany());
            psDielo.setString(10, dielo.getVydanie());
            psDielo.setInt(11, dielo.getKategoria_id());

            psDielo.executeUpdate();
            rs = psDielo.getGeneratedKeys();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        return rs;
    }



    private String iDieloOhlas = "insert into dielo_ohlas (dielo_id, ohlas_id) values (?,?)";

    public void insertIntoDieloOhlas(Integer dielo_id, Integer ohlas_id){
        try {
            psDieloOhlas.setInt(1, dielo_id);
            psDieloOhlas.setInt(2, ohlas_id);

            psDieloOhlas.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String iAutor = "insert into autori (meno, priezvisko) values (?,?)";
    private String sAutor = "select autor_id from autori where meno = ? and priezvisko = ?";

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

    private String iAutorDieloPracovisko = "insert into autor_dielo_pracovisko (autor_id, dielo_id, pracovisko_id, percentualny_podiel) values (?,?,?,?)";

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

    private String sAutorIdAPodielAPracovisko = "select autor_id, percentualny_podiel, pracovisko_id from autor_dielo_pracovisko where dielo_id = ?";

    public ResultSet selectAutorIdAPodielAPracoviskoByDielo(Integer dielo_id){
        ResultSet rs = null;

        try {
            psAutorIdAPodielAPracoviskoByDielo.setInt(1, dielo_id);
            rs = psAutorIdAPodielAPracoviskoByDielo.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }

    private String iOhlas = "insert into ohlasy (rok_vydania, nazov, ISBN, ISSN, miesto_vydania, strany, kategorie_ohlasov_id) values (?,?,?,?,?,?,?)";

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

    private String iAutorOhlas = "insert into autor_ohlas (autor_id, ohlas_id) values (?,?)";

    public void insertIntoAutorOhlas(Integer autor_id, Integer ohlas_id){
        try {
            psAutorOhlas.setInt(1, autor_id);
            psAutorOhlas.setInt(2, ohlas_id);

            psAutorOhlas.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String updatePocetStran = "UPDATE diela set strany = ? where dielo_id = ?";
    public void updatePocetStran(int dielo_id, int pocetStran){
        try {
            if(pocetStran <= 0 || pocetStran > 1000){
                psUpdatePocetStran.setNull(1,Types.INTEGER);
            }else{
                psUpdatePocetStran.setInt(1, pocetStran);
            }
            psUpdatePocetStran.setInt(2,dielo_id);
            psUpdatePocetStran.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String sOhlas = "select ohlas_id from ohlasy where nazov = ? and rok_vydania = ?";

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
            rs = statement.executeQuery(query);
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
            rs = statement.executeQuery(query);
            while(rs.next()){
                pracoviska.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String iKlucoveSlova = "insert into klucove_slova (klucove_slovo) values (?)";

    public ResultSet insertIntoKlucove_slova(String klucove_slovo){
        ResultSet rs = null;

        try {
            psKlucoveSlovoId.setString(1, klucove_slovo);
            rs = psKlucoveSlovoId.executeQuery();
            if (rs.next()) { //ak existuje take klucove slovo, vrati ID toho klucoveho slova
                rs.beforeFirst();
                return rs;
            }

            psKlucoveSlova.setString(1, klucove_slovo);
            psKlucoveSlova.executeUpdate();
            rs = psKlucoveSlova.getGeneratedKeys();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }

    private String iDieloKlucoveSlovo = "insert into dielo_klucove_slovo (dielo_id, klucove_slovo_id) values (?,?)";

    public void insertIntoDieloKlucoveSlova(Integer dielo_id, Integer klucove_slovo_id){
        try {
            psDieloKlucoveSlovo.setInt(1, dielo_id);
            psDieloKlucoveSlovo.setInt(2, klucove_slovo_id);

            psDieloKlucoveSlovo.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String klucoveSlovoId = "select klucove_slovo_id from klucove_slova where klucove_slovo = ?";

    public ResultSet selectKlucove_slova(){
        String query = "select dielo_id, klucove_slova from diela where klucove_slova is not null";
        ResultSet rs = null;

        try {
            rs = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }

    public String getKlucoveSlova(int dieloId){
        String query = "select a.dielo_id, b.klucove_slovo from fberg_lf.dielo_klucove_slovo a join fberg_lf.klucove_slova b on a.klucove_slovo_id = b.klucove_slovo_id where dielo_id = " +dieloId;
        ResultSet rs;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            rs = statement.executeQuery(query);
            while (rs.next()){
                stringBuilder.append(rs.getString(2));
                stringBuilder.append(", ");
            }
            if(stringBuilder.length()>2) {
                stringBuilder.substring(0, stringBuilder.length() - 2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public ResultSet selectAutorDieloPracovisko(){
        String query = "select autor_id, dielo_id, pracovisko_id from autor_dielo_pracovisko where percentualny_podiel is null";
        ResultSet rs = null;

        try {
            rs = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }

    private String sPodiel = "select percentualny_podiel from autor_dielo_pracovisko where dielo_id = ? and pracovisko_id = ?";

    public ResultSet selectPodiel(int dieloId, int pracoviskoId){
        ResultSet rs = null;

        try {
            psPodielSelect.setInt(1, dieloId);
            psPodielSelect.setInt(2, pracoviskoId);
            rs = psPodielSelect.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }

    private String uPodiel = "update autor_dielo_pracovisko set percentualny_podiel = ? where autor_id = ? and dielo_id = ? and pracovisko_id = ?";

    public int updatePodiel(int podiel, int autorId, int dieloId, int pracoviskoId){
        int rows = -1;
        try {
            psPodielUpdate.setInt(1, podiel);
            psPodielUpdate.setInt(2, autorId);
            psPodielUpdate.setInt(3, dieloId);
            psPodielUpdate.setInt(4, pracoviskoId);
            rows = psPodielUpdate.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    public ResultSet selectStrany(){
        String query = "select dielo_id, strany from diela where strany is not null";
        ResultSet rs = null;

        try {
            rs = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }

    public Hashtable<String, Integer> getKategorie() {
        return kategorie;
    }

    public Hashtable<String, Integer> getPracoviska() {
        return pracoviska;
    }
}
