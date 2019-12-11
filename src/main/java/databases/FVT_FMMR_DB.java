package databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class FVT_FMMR_DB {
    private static FVT_FMMR_DB singleInstance = null;
    private final String url = "jdbc:mysql://localhost:3306/fvt_fmmr?useLegacyDatetimeCode=false&serverTimezone=UTC";
    private final String user  = "root";
    private final String pass  = "root";
    private Connection con = null;

    private FVT_FMMR_DB(){
        openConnection();
    }

    public static FVT_FMMR_DB getInstance(){
        if(singleInstance == null)
            singleInstance = new FVT_FMMR_DB();

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
