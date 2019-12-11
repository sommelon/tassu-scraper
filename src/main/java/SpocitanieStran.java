import databases.FBERG_LF_DB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpocitanieStran {
    public static void main(String[] args) throws SQLException {
        FBERG_LF_DB db = FBERG_LF_DB.getInstance();
        ResultSet rs = db.selectStrany();
        Pattern rozsahStranP = Pattern.compile("([0-9]+) ?- ?([0-9]+)?");
        Matcher m;

        while(rs.next()){
            int dieloId = rs.getInt(1);
            int pocetStran = -1;
            String strany = rs.getString(2);

            if (strany.contains("-")){
                System.out.print("Rozsah - ");
                m = rozsahStranP.matcher(strany);
                if (m.find()) {
                    int stranaOd = Integer.parseInt(m.group(1));
                    int stranaDo = Integer.parseInt(m.group(2));
                    pocetStran = stranaDo - stranaOd;
                    if(pocetStran == 0){
                        pocetStran = 1;
                    }
                }
            }else{
                pocetStran = 1;
            }

            db.updatePocetStran(dieloId,pocetStran);
            System.out.println("Dielo " + dieloId + " ma " + pocetStran + " stran.");
        }
    }
}
