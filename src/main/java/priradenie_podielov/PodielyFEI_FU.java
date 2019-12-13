package priradenie_podielov;

import databases.DatabaseFEI_FU;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PodielyFEI_FU {
    public static void main(String[] args) throws SQLException {
        DatabaseFEI_FU db = DatabaseFEI_FU.getInstance();
        ResultSet rs = db.selectAutorDielo();

        while (rs.next()){
            int autorId = rs.getInt(1);
            int dieloId = rs.getInt(2);

            ResultSet rs2 = db.selectPodiel(dieloId);
            int pocetAutorovBezPodielu = 0;
            int podielDokopy = 0;

            while (rs2.next()){
                int podiel = rs2.getInt(1);
                if (podiel == 0){
                    pocetAutorovBezPodielu++;
                }
                podielDokopy += podiel;
            }

            if (podielDokopy >= 100){ //podiel ostane taky aky bol
                continue;
            }

            int podielAutora = (int) Math.floor((100 - podielDokopy) / pocetAutorovBezPodielu);
            db.updatePodiel(podielAutora, autorId, dieloId);
            System.out.println("Autorovi "+ autorId +" bol prideleny podiel " +podielAutora+ " na diele "+ dieloId);
        }


    }
}
