package databases;

import java.sql.*;

public class DatabaseFEI_FU {
    private static DatabaseFEI_FU singleInstance = null;
    private final String url = "jdbc:mysql://localhost:3306/fei_fu?useLegacyDatetimeCode=false&serverTimezone=UTC";
    private final String user  = "root";
    private final String pass  = "root";
    private Connection con = null;

    private Statement statement;
    private PreparedStatement psPodielSelect;
    private PreparedStatement psPodielUpdate;
    private PreparedStatement psGetDataForScheme;
    private PreparedStatement psKluc;

    private DatabaseFEI_FU(){
        openConnection();
    }

    public static DatabaseFEI_FU getInstance(){
        if(singleInstance == null)
            singleInstance = new DatabaseFEI_FU();

        return singleInstance;
    }
    //Pages are in the correct form

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
                psPodielUpdate = con.prepareStatement(uPodiel);
                psPodielSelect = con.prepareStatement(sPodiel);
                psGetDataForScheme = con.prepareStatement(sDataForStarScheme, Statement.RETURN_GENERATED_KEYS);
                psKluc = con.prepareStatement(sKluc);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeConnection(){
        try {
            statement.close();

            psPodielUpdate.close();
            psPodielSelect.close();

            if (con != null)
                con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet selectAutorDielo(){
        String query = "select id_author, id_publication from author_publication where contribution is null";
        ResultSet rs = null;

        try {
            rs = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }

    private String sPodiel = "select contribution from author_publication where id_publication = ?";

    public ResultSet selectPodiel(int dieloId){
        ResultSet rs = null;

        try {
            psPodielSelect.setInt(1, dieloId);
            rs = psPodielSelect.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }

    private String uPodiel = "update author_publication set contribution = ? where id_author = ? and id_publication = ?";

    public void updatePodiel(int podiel, int autorId, int dieloId){
        try {
            psPodielUpdate.setInt(1, podiel);
            psPodielUpdate.setInt(2, autorId);
            psPodielUpdate.setInt(3, dieloId);

            psPodielUpdate.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String sDataForStarScheme = "select p.title, p.subtitle, p.ISBNISSN, p.edition, p.year, p.journal, p.place, p.publisher, p.arch_num, p.pages, ap.contribution, a.first_name, a.last_name, d.name, f.name, p.category_EPC, p.id_publication " +
            "from fei_fu.publication p " +
            "join fei_fu.author_publication ap " +
            "on p.id_publication = ap.id_publication " +
            "join fei_fu.author a " +
            "on a.id_author = ap.id_author " +
            "join fei_fu.department_publication dp " +
            "on p.id_publication = dp.id_publication " +
            "join fei_fu.department d " +
            "on dp.id_department = d.id_department " +
            "join fei_fu.faculty f " +
            "on d.id_faculty = f.id_faculty " +
            "where p.pages > 0 and ap.contribution > 0";

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

    String sKluc = "select k.value from fei_fu.keyword k " +
            "join fei_fu.publication_keyword pk on pk.id_keyword = k.id_keyword " +
            "where pk.id_publication = ?";
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
