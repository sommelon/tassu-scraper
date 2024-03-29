package databases;

import tabulky.Autor;
import tabulky.Dielo;

import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private PreparedStatement psSelectDielo;

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
                psSelectDielo = con.prepareStatement(selectDielo,Statement.RETURN_GENERATED_KEYS);

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

    private String selectDielo = "select dieloId from dielo where nazov = ?";

    private String iDielo = "insert into dielo " +
            "(archivacne_cislo, rok_vydania, nazov,ISBN, ISSN, miesto_vydania, vydanie, klucove_slova, podnazov) " +
            "values (?,?,?,?,?,?,?,?,?)";

    public ResultSet insertIntoDiela(Dielo dielo) {
        ResultSet rs = null;
        try {
            psSelectDielo.setString(1, dielo.getNazov());
            ResultSet rsSelectDielo = psSelectDielo.executeQuery();
            if (!rsSelectDielo.next()) {
                psDielo.setString(1, dielo.getArchivacne_cislo());
                psDielo.setString(2, dielo.getRok_vydania());
                psDielo.setString(3, dielo.getNazov());
                if (dielo.getISBN() == null) {
                    psDielo.setNull(4, Types.VARCHAR);
                }else{
                    psDielo.setString(4, dielo.getISBN());
                }
                if (dielo.getISSN() == null) {
                    psDielo.setNull(5, Types.VARCHAR);
                }else{
                    psDielo.setString(5, dielo.getISSN());
                }
                if (dielo.getMiesto_vydania() == null) {
                    psDielo.setNull(6, Types.VARCHAR);
                }else {
                    psDielo.setString(6, dielo.getMiesto_vydania());
                }
                if (dielo.getVydanie() == null) {
                    psDielo.setNull(7, Types.VARCHAR);
                }else{
                    psDielo.setString(7, dielo.getVydanie());
                }
                if (dielo.getKlucove_slova() == null){
                    psDielo.setNull(8, Types.VARCHAR);
                }else {
                    psDielo.setString(8, dielo.getKlucove_slova());
                }
                if (dielo.getPodnazov() == null){
                    psDielo.setNull(9, Types.VARCHAR);
                }else {
                    psDielo.setString(9, dielo.getPodnazov());
                }


                psDielo.executeUpdate();


                rs = psDielo.getGeneratedKeys();
                rs.next();
            }
            else {
                return rsSelectDielo;
            }
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
        public ResultSet insetIntoPracovisko(String nazov, String nazov_fakulty, String skartka_fakulty){
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

        Pattern rokP = Pattern.compile("[0-9]{4}");
        public ResultSet insertIntoCas (String rok){
            int rokInt;
            if (rok.contains("-")) {
                Matcher m = rokP.matcher(rok);
                m.find();
                rokInt = Integer.parseInt(m.group(0));
            }else {
                rokInt = Integer.parseInt(rok);
            }

            ResultSet rs = null;
            try {
                psSelectCas.setInt(1, rokInt);
                ResultSet rsSelectCas = psSelectCas.executeQuery();

                if (!rsSelectCas.next()) {
                    psCas.setInt(1, rokInt);

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

        public ResultSet insertIntoFact ( int dieloId, int pracoviskoId, int autorId, int kategoriaId,
        int casVydaniaId, double pocetStran){
            ResultSet rs = null;
            try {
                psFactTable.setInt(1, dieloId);
                psFactTable.setInt(2, pracoviskoId);
                psFactTable.setInt(3, autorId);
                psFactTable.setInt(4, kategoriaId);
                psFactTable.setInt(5, casVydaniaId);
                psFactTable.setDouble(6, pocetStran);


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
