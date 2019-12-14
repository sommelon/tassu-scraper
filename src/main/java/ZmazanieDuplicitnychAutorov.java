import databases.DatabaseREKT_SJF;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ZmazanieDuplicitnychAutorov {
    public static void main(String[] args) throws SQLException {
        DatabaseREKT_SJF db = DatabaseREKT_SJF.getInstance();
        ResultSet rs = db.selectAuthors();
        ArrayList<Integer> oldIds = new ArrayList<Integer>();

        while (rs.next()){
            int autorId = rs.getInt(1);
            String meno = rs.getString(2);

            ResultSet rs2 = db.selectAutor(meno, autorId);
            boolean duplicate = false;
            while (rs2.next()){
                if (!duplicate) {
                    duplicate = true;
                    System.out.println("Duplicitny autor: " + meno);
                }
                int oldId = rs2.getInt(1);
                oldIds.add(oldId);
                db.updateAuthor_id(autorId, oldId);
            }
        }

        for (Integer oldId : oldIds) {
            db.deleteAuthors(oldId);
            System.out.println("Vymazany autor s ID "+ oldId);
        }

        db.closeConnection();
    }
}
