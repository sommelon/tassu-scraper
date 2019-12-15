package databases;

import java.sql.*;

public class DatabaseFVT_FMMR {
    private static DatabaseFVT_FMMR singleInstance = null;
    private final String url = "jdbc:mysql://localhost:3306/fvt_fmmr?useLegacyDatetimeCode=false&serverTimezone=UTC";
    private final String user  = "root";
    private final String pass  = "root";
    private Connection con = null;
    private Statement statement;
    private PreparedStatement psUpdatePocetStran;
    private PreparedStatement psPocetStranToNull;
    private PreparedStatement psPocetEmptyStranToNull;
    private PreparedStatement psGetDataForScheme;
    private PreparedStatement psKluc;

    private DatabaseFVT_FMMR(){
        openConnection();
    }

    public static DatabaseFVT_FMMR getInstance(){
        if(singleInstance == null)
            singleInstance = new DatabaseFVT_FMMR();

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
                psPocetStranToNull = con.prepareStatement(pocetStranToNull);
                psUpdatePocetStran = con.prepareStatement(updatePocetStran);
                psPocetEmptyStranToNull = con.prepareStatement(pocetEmptyStranToNull);
                psGetDataForScheme = con.prepareStatement(sDataForStarScheme, Statement.RETURN_GENERATED_KEYS);
                psKluc = con.prepareStatement(sKluc);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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

    private String sDataForStarScheme = "select z.idZaznam, z.Nazov, z.ISBN, z.ISSN, r.Roky, m.Mesto, z.arch_cislo, ke.katepc, a.Meno, a.Priezvisko, z.pocet_stran, p.percenta, k.Katedra, f.Fakulta, f.Skratka " +
            "from fvt_fmmr.zaznam z " +
            "join fvt_fmmr.mesta m on m.idMesta = z.fkmesta " +
            "join fvt_fmmr.kat_epc ke on ke.idkatepc = z.fkkat_epc " +
            "join fvt_fmmr.zaznam_has_autor zha on zha.fkzaznam = z.idZaznam " +
            "join fvt_fmmr.percenta p on p.idpercenta = zha.fkpercenta " +
            "join fvt_fmmr.autor a on a.idAutor = zha.fkautor " +
            "join fvt_fmmr.roky r on r.idRoky = z.fkroky " +
            "join fvt_fmmr.katedra_has_zaznam khz on khz.Zaznam_idZaznam = z.idZaznam " +
            "join fvt_fmmr.katedra k on khz.Katedra_idKatedra = k.idKatedra " +
            "join fvt_fmmr.fakulta f on k.Fakulta_idFakulta = f.idFakulta " +
            "where z.pocet_stran > 0 and p.percenta > 0";

    public ResultSet getDataForStarScheme(){

        ResultSet rs = null;
        try {
            rs = psGetDataForScheme.executeQuery();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return rs;
    }

    String sKluc = "select k.kluc_slova from fvt_fmmr.klucove_slova k " +
            "join fvt_fmmr.zaznam_has_klucove_slova zk on zk.fkklucoveslova = k.idkluc_slova " +
            "where zk.fkzaznam = ?";
    public String getKlucoveSlova(int dieloId){
        ResultSet rs;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            psKluc.setInt(1, dieloId);
            rs = psKluc.executeQuery();
            while (rs.next()){
                stringBuilder.append(rs.getString(1));
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
}
