import databases.FBERG_LF_DB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class RozdelenieKlucovychSlov {
    public static void main(String[] args) throws SQLException {
        FBERG_LF_DB db = FBERG_LF_DB.getInstance();
        ResultSet rs = db.selectKlucove_slova();
        Pattern znakyNaStranachP = Pattern.compile("^ +| $");

        while (rs.next()){
            int dieloID = rs.getInt(1);
            String klucoveSlova = rs.getString(2);

            klucoveSlova = klucoveSlova.replaceAll(",$", "");
            String[] klucove_slovo = klucoveSlova.split(",");

            ArrayList<Integer> generatedKeys = new ArrayList<Integer>();
            for (String s : klucove_slovo) {
                s = znakyNaStranachP.matcher(s).replaceAll("");
                if (s.equals(""))
                    continue;
                ResultSet generatedKey = db.insertIntoKlucove_slova(s);
                if (generatedKey != null){
                    if (generatedKey.next()){
                        generatedKeys.add(generatedKey.getInt(1));
                    }
                }
            }

            for (Integer generatedKey : generatedKeys) {
                db.insertIntoDieloKlucoveSlova(dieloID, generatedKey);
            }
        }
    }
}