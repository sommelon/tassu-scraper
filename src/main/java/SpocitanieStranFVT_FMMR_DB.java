import databases.FVT_FMMR_DB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpocitanieStranFVT_FMMR_DB {


        public static void main(String[] args) throws SQLException {

            FVT_FMMR_DB fvt_fmmr_db = FVT_FMMR_DB.getInstance();
            ResultSet rs = fvt_fmmr_db.selectStrany();
            Pattern rozsahStranP = Pattern.compile("([0-9]+) ?- ?([0-9]+)?");
            Matcher m;
            fvt_fmmr_db.convertToNull();
            while (rs.next()) {
                int dieloId = rs.getInt(1);
                int pocetStran = -1;
                String strany = rs.getString(2);

                if (strany.contains("-")) {
                    System.out.print("Rozsah - ");
                    m = rozsahStranP.matcher(strany);
                    if (m.find()) {
                        if(isNumeric(m.group(1))&& isNumeric(m.group(2))) {
                            int stranaOd = Integer.parseInt(m.group(1));
                            int stranaDo = Integer.parseInt(m.group(2));
                            pocetStran = stranaDo - stranaOd;
                            if (pocetStran == 0) {
                                pocetStran = 1;
                            }
                        }
                    }
                } else {
                    pocetStran = 1;
                }

                fvt_fmmr_db.updatePocetStran(dieloId, pocetStran);
                System.out.println("Dielo " + dieloId + " ma " + pocetStran + " stran.");

            }
        }
        public static boolean isNumeric(String str) {
            if(str !=null) {
                try {
                    Double.parseDouble(str);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            return false;
        }
    }


