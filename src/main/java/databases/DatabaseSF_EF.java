package databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseSF_EF {
    private static DatabaseSF_EF singleInstance = null;
    private final String url = "jdbc:mysql://localhost:3306/sf_ef?useLegacyDatetimeCode=false&serverTimezone=UTC";
    private final String user  = "root";
    private final String pass  = "root";
    private Connection con = null;

    private DatabaseSF_EF(){
        openConnection();
    }

    public static DatabaseSF_EF getInstance(){
        if(singleInstance == null)
            singleInstance = new DatabaseSF_EF();

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
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Pages are in the correct form
}
