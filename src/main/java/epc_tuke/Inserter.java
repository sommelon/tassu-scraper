package epc_tuke;

import epc_tuke.tabulky.*;

import java.sql.*;
import java.util.List;

public class Inserter {
    private Zaznam zaznam;
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
    public Inserter(Zaznam zaznam, List<Autor> autori, List<Integer> podiely, List<Ohlas> ohlasy/*, Fakulta fakulta, Pracovisko pracovisko*/){
        this.zaznam = zaznam;
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

    public void insertIntoZaznam() {
        String query = "insert into zaznamy (zaznam_id, nazov, archivacne_cislo, rok_vydania, ISBN, miesto_vydania, klucove_slova, odkaz, ISSN, podnazov, strana_od, strana_do, kategoria_id) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, zaznam.getId());
            ps.setString(2, zaznam.getNazov());
            ps.setString(3, zaznam.getArchivacne_cislo());
            ps.setInt(4, zaznam.getRok_vydania());
            ps.setString(5, zaznam.getISBN());
            ps.setString(6, zaznam.getMiesto_vydania());
            ps.setString(7, zaznam.getKlucove_slova());
            ps.setString(8, zaznam.getOdkaz());
            ps.setString(9, zaznam.getISSN());
            ps.setString(10, zaznam.getPodnazov());
            if (zaznam.getStrana_od() != null)
                ps.setInt(11, zaznam.getStrana_od());
            else
                ps.setNull(11, Types.INTEGER);
            if (zaznam.getStrana_do() != null)
                ps.setInt(12, zaznam.getStrana_do());
            else
                ps.setNull(12, Types.INTEGER);
            ps.setInt(13, -1);

            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertIntoAutor(){
        String query = "insert into autori (autor_id, meno, priezvisko) values (?,?,?)";
        for (Autor autor : autori) {
            try {
                PreparedStatement ps = con.prepareStatement(query);
                ps.setInt(1, autor.getId());
                ps.setString(2, autor.getMeno());
                ps.setString(3, autor.getPriezvisko());

                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void insertIntoAutorZaznamPracovisko(){
        String query = "insert into autor_zaznam_pracovisko (autor_id, záznam_id, pracovisko_id, percentualny_podiel) values (?,?,?,?)";
        for (int i = 0; i < autori.size(); i++) {
            try {
                PreparedStatement ps = con.prepareStatement(query);
                ps.setInt(1, autori.get(i).getId());
                ps.setInt(2, zaznam.getId());
                ps.setInt(3, -1);
                if (podiely.get(i) != null)
                    ps.setInt(4, podiely.get(i));
                else
                    ps.setNull(4, Types.INTEGER);

                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void insertIntoOhlas(){
        String query = "insert into ohlasy (ohlas_id, nazov, zaznam_id) values (?,?,?)";
        for (Ohlas ohlas : ohlasy) {
            try {
                PreparedStatement ps = con.prepareStatement(query);
                ps.setInt(1, ohlas.getId());
                ps.setString(2, ohlas.getNazov());
                ps.setInt(3, zaznam.getId());

                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
