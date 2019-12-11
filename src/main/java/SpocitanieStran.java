import epc_tuke.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpocitanieStran {
    public static void main(String[] args) throws SQLException {
        Database db = Database.getInstance();
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
                }
            }else{
                pocetStran = 1;
            }

            if (pocetStran > 0 && pocetStran < 1000) {
                //TODO zapisat do nejakej tabulky alebo rovno do star schemy
                System.out.println("Dielo " + dieloId + " ma " + pocetStran + " stran.");

            }
        }
    }
}
