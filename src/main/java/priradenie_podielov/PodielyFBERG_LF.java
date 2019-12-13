package priradenie_podielov;

import databases.DatabaseFBERG_LF;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PodielyFBERG_LF {
    public static void main(String[] args) throws SQLException {
        DatabaseFBERG_LF db = DatabaseFBERG_LF.getInstance();
        ResultSet rs = db.selectAutorDieloPracovisko(); //select len tych co nemaju podiel

        while (rs.next()){
            int autorId = rs.getInt(1);
            int dieloId = rs.getInt(2);
            int pracoviskoId = rs.getInt(3);

            ResultSet rs2 = db.selectPodiel(dieloId, pracoviskoId);
            int pocetAutorovBezPodielu = 0;
            int podielDokopy = 0;

            //pocitanie podielu a autorov bez podielu pre dane dielo v danom pracovisku
            while (rs2.next()){
                int podiel = rs2.getInt(1);
                if (podiel == 0){
                    pocetAutorovBezPodielu++;
                }
                podielDokopy += podiel;
            }

            if (podielDokopy >= 100){ //podiel zostane taky aky bol
                continue;
            }

            int podielAutora = (int) Math.floor((100 - podielDokopy) / pocetAutorovBezPodielu);
            db.updatePodiel(podielAutora, autorId, dieloId, pracoviskoId);
            System.out.println("Autorovi " + autorId + " bol prideleny podiel " + podielAutora + " na diele " + dieloId);
            System.out.println("Podiel dokopy: "+ podielDokopy +". Pocet autorov bez podielu: "+ pocetAutorovBezPodielu);
        }
    }
}
