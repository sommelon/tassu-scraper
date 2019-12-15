import databases.DatabaseStarScheme;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OpravaMiestaVydania {
    public static void main(String[] args) throws SQLException {
        DatabaseStarScheme db = DatabaseStarScheme.getInstance();
        ResultSet rs = db.selectMiesto();
        while (rs.next()){
            String m = rs.getString(2);
            if (m.equals("null & null")){
                db.updateMiesto(null, rs.getInt(1));
            }else{
                db.updateMiesto(m.replaceFirst(" & null", ""), rs.getInt(1));
            }
        }
    }
}
