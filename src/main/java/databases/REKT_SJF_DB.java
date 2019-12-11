package databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class REKT_SJF_DB {
    private static REKT_SJF_DB singleInstance = null;
    private final String url = "jdbc:mysql://localhost:3306/rekt_sjf?useLegacyDatetimeCode=false&serverTimezone=UTC";
    private final String user  = "root";
    private final String pass  = "root";
    private Connection con = null;

    private REKT_SJF_DB(){
        openConnection();
    }

    public static REKT_SJF_DB getInstance(){
        if(singleInstance == null)
            singleInstance = new REKT_SJF_DB();

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
}
