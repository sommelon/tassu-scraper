package priradenie_podielov;

import databases.DatabaseREKT_SJF;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PodielyREKT_SJF {
    public static void main(String[] args) throws SQLException {
        DatabaseREKT_SJF db = DatabaseREKT_SJF.getInstance();
        ResultSet rs = db.selectAutorDielo();

        //TODO v DB je vela duplicitnych autorov

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
            System.out.println("Podiel dokopy: "+ podielDokopy +". Pocet autorov bez podielu: "+ pocetAutorovBezPodielu);
        }
    }
}
