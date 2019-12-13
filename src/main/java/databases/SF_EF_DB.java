package databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SF_EF_DB {
    private static SF_EF_DB singleInstance = null;
    private final String url = "jdbc:mysql://localhost:3306/sf_ef?useLegacyDatetimeCode=false&serverTimezone=UTC";
    private final String user  = "root";
    private final String pass  = "root";
    private Connection con = null;

    private SF_EF_DB(){
        openConnection();
    }

    public static SF_EF_DB getInstance(){
        if(singleInstance == null)
            singleInstance = new SF_EF_DB();

        return singleInstance;
    }
    // Pages are in the correct form

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
