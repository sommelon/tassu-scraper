package databases;

import tabulky.Autor;
import tabulky.Dielo;

import java.sql.*;

public class DatabaseStarScheme {
    private static DatabaseStarScheme singleInstance = null;
    private final String url = "jdbc:mysql://localhost:3306/star_schema?useLegacyDatetimeCode=false&serverTimezone=UTC";
    private final String user = "root";
    private final String pass = "root";
    private Connection con = null;
    private PreparedStatement psDielo;
    private PreparedStatement psAutor;
    private PreparedStatement psKategoria;
    private PreparedStatement psCas;
    private PreparedStatement psPracovisko;
    private PreparedStatement psFactTable;
    private PreparedStatement psSelectPracovisko;
    private PreparedStatement psSelectKategoria;
    private PreparedStatement psSelectAutor;
    private PreparedStatement psSelectCas;
    private Statement statement;

    private DatabaseStarScheme() {
        openConnection();

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
                psAutor = con.prepareStatement(iAutor, Statement.RETURN_GENERATED_KEYS);
                psPracovisko = con.prepareStatement(iPracovisko, Statement.RETURN_GENERATED_KEYS);
                psKategoria = con.prepareStatement(iKategoria, Statement.RETURN_GENERATED_KEYS);
                psCas = con.prepareStatement(iCas, Statement.RETURN_GENERATED_KEYS);
                psFactTable = con.prepareStatement(iFact);
                psSelectPracovisko = con.prepareStatement(selectPracovisko, Statement.RETURN_GENERATED_KEYS);
                psSelectKategoria = con.prepareStatement(selectKategoria, Statement.RETURN_GENERATED_KEYS);
                psSelectAutor = con.prepareStatement(selectAutor, Statement.RETURN_GENERATED_KEYS);
                psSelectCas = con.prepareStatement(selectCas,Statement.RETURN_GENERATED_KEYS);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeConnection() {
        try {
            statement.close();

            if (con != null)
                con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private String iDielo = "insert into dielo " +
            "(archivacne_cislo, rok_vydania, nazov,ISBN, ISSN, miesto_vydania, vydanie, klucove_slova) " +
            "values (?,?,?,?,?,?,?,?)";

    public ResultSet insertIntoDiela(Dielo dielo) {
        ResultSet rs = null;
        try {

            psDielo.setString(1, dielo.getArchivacne_cislo());
            psDielo.setString(2, dielo.getRok_vydania());
            psDielo.setString(3, dielo.getNazov());
            psDielo.setString(4, dielo.getISBN());
            psDielo.setString(5, dielo.getISSN());
            psDielo.setString(6, dielo.getMiesto_vydania());
            psDielo.setString(7, dielo.getVydanie());
            psDielo.setString(8,dielo.getKlucove_slova());


            psDielo.executeUpdate();


            rs = psDielo.getGeneratedKeys();
            rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return rs;
    }

    private String selectAutor = "select autorid from autor where meno = ? AND priezvisko = ?";
    private String iAutor = "insert into autor " +
            "(meno, priezvisko) " +
            "values (?,?)";

    public ResultSet insetIntoAutor(Autor autor) {
        ResultSet rs = null;

        try {
            psSelectAutor.setString(1, autor.getMeno());
            psSelectAutor.setString(2, autor.getPriezvisko());
            ResultSet rsSelectAutor = psSelectAutor.executeQuery();
            if (!rsSelectAutor.next()) {
                psAutor.setString(1, autor.getMeno());
                psAutor.setString(2, autor.getPriezvisko());


                psAutor.executeUpdate();
                rs = psAutor.getGeneratedKeys();
                rs.next();
            }else{
                return rsSelectAutor;
            }
            } catch(SQLException e){
                e.printStackTrace();
            } catch(NullPointerException e){
                e.printStackTrace();
            }

            return rs;
        }


//    INSERT INTO `table` (`value1`, `value2`)
//    SELECT 'stuff for value1', 'stuff for value2' FROM DUAL
//    WHERE NOT EXISTS (SELECT * FROM `table`
//            WHERE `value1`='stuff for value1' AND `value2`='stuff for value2' LIMIT 1)


        //   private String iPracovisko =  "insert into pracovisko (nazov, nazov_fakulty, skratka_fakulty) values (?,?,?) select nazov from dual where not exists (select * from pracovisko where nazov = ? LIMIT 1)";


        //
        private String selectPracovisko = "select pracoviskoid , nazov from pracovisko where nazov = ?";
        private String iPracovisko = "insert into pracovisko " +
                "(nazov, nazov_fakulty, skratka_fakulty) " +
                "values (?,?, ?)";

        //private String iPracovisko = "insert ignore into pracovisko set nazov = ?, nazov_fakulty = ?, skratka_fakulty = ?";
//    private String iPracovisko = "IF NOT EXISTS (SELECT * FROM pracovisko WHERE nazov = ?)" +
//        "    insert into pracovisko (nazov, nazov_fakulty, skratka_fakulty) values (?,?, ?)";
        public ResultSet insetIntopracivosko (String nazov, String nazov_fakulty, String skartka_fakulty){
            ResultSet rs = null;

            try {
                psSelectPracovisko.setString(1, nazov);
                ResultSet rsSelectPracovisko = psSelectPracovisko.executeQuery();
                if (!rsSelectPracovisko.next()) {
                    psPracovisko.setString(1, nazov);
                    // psPracovisko.setString(2,nazov);
                    psPracovisko.setString(2, nazov_fakulty);
                    psPracovisko.setString(3, skartka_fakulty);
                    //  psPracovisko.setString(4, nazov);


                    psPracovisko.executeUpdate();
                    rs = psPracovisko.getGeneratedKeys();
                    rs.next();
                } else {
                    return rsSelectPracovisko;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();

            }

            return rs;
        }

        private String selectKategoria = "select kategoriaId, skratka from kategoria where skratka = ?";

        private String iKategoria = "insert into kategoria " +
                "(popis, skratka) " +
                "values (?,?)";

        public ResultSet insertIntoKategoria (String popis, String skratka){
            ResultSet rs = null;
            try {
                psSelectKategoria.setString(1, skratka);
                ResultSet rsSelectKategoria = psSelectKategoria.executeQuery();

                if (!rsSelectKategoria.next()) {

                    psKategoria.setString(1, popis);
                    psKategoria.setString(2, skratka);


                    psKategoria.executeUpdate();
                    rs = psKategoria.getGeneratedKeys();
                    rs.next();
                } else {
                    return rsSelectKategoria;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            return rs;
        }

        private String selectCas = "select casvydaniaid from casvydania where rok = ?";
        private String iCas = "insert into casvydania " +
                "(rok) " +
                "values (?)";

        public ResultSet insertIntoCas (String rok){
            ResultSet rs = null;
            try {
                psSelectCas.setString(1, rok);
                ResultSet rsSelectCas = psSelectCas.executeQuery();

                if (!rsSelectCas.next()) {
                    psCas.setString(1, rok);


                    psCas.executeUpdate();
                    rs = psCas.getGeneratedKeys();
                    rs.next();
                }
                else{
                    return rsSelectCas;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            return rs;
        }

        private String iFact = "insert into fact " +
                "(dieloId,pracoviskoId,autorId, kategoriaId, casVydaniaId, pocetStran) " +
                "values (?,?,?,?,?,?)";

        public ResultSet insertIntoFact ( long dieloId, long pracoviskoId, long autorId, long kategoriaId,
        long casVydaniaId, long pocetStran){
            ResultSet rs = null;
            try {
                psFactTable.setInt(1, (int) dieloId);
                psFactTable.setInt(2, (int) pracoviskoId);
                psFactTable.setInt(3, (int) autorId);
                psFactTable.setInt(4, (int) kategoriaId);
                psFactTable.setInt(5, (int) casVydaniaId);
                psFactTable.setInt(6, (int) pocetStran);


                psFactTable.executeUpdate();
                // rs = psFactTable.getGeneratedKeys();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            return rs;
        }


        public static DatabaseStarScheme getInstance () {
            if (singleInstance == null)
                singleInstance = new DatabaseStarScheme();

            return singleInstance;
        }

    }
