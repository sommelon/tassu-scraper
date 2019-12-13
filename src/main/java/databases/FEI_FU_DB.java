package databases;

import star_schema.FEI_FU;

import java.sql.*;

public class FEI_FU_DB {
    private static FEI_FU_DB singleInstance = null;
    private final String url = "jdbc:mysql://localhost:3306/fei_fu?useLegacyDatetimeCode=false&serverTimezone=UTC";
    private final String user  = "root";
    private final String pass  = "root";
    private Connection con = null;

    private FEI_FU_DB(){
        openConnection();
    }

    public static FEI_FU_DB getInstance(){
        if(singleInstance == null)
            singleInstance = new FEI_FU_DB();

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
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
