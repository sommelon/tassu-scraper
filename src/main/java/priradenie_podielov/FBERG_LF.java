package priradenie_podielov;

import epc_tuke.Database;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FBERG_LF {
    public static void main(String[] args) throws SQLException {
        Database db = Database.getInstance();
        ResultSet rs = db.selectAutorDieloPracovisko();

        while (rs.next()){
            int autorId = rs.getInt(1);
            int dieloId = rs.getInt(2);
            int pracoviskoId = rs.getInt(3);

            ResultSet rs2 = db.selectPodiel(dieloId, pracoviskoId);
            int pocetAutorovBezPodielu = 0;
            int podielDokopy = 0;

            while (rs2.next()){
                int podiel = rs2.getInt(1);
                if (podiel == 0){
                    pocetAutorovBezPodielu++;
                }
                podielDokopy += podiel;
            }

            if (pocetAutorovBezPodielu == 1){ //ak je len jeden autor, ktory nema podiel, dopocita sa a updatne v DB
                int podielAutora = 100 - podielDokopy;
                db.updatePodiel(podielAutora, autorId, dieloId, pracoviskoId);
                System.out.println("Autorovi "+ autorId +" bol prideleny podiel " +podielAutora+ " na diele "+ dieloId);
            } else {
                int podielAutora = (int) Math.floor((100 - podielDokopy) / pocetAutorovBezPodielu);
                db.updatePodiel(podielAutora, autorId, dieloId, pracoviskoId);
                System.out.println("Pre dielo "+ dieloId +" je viac autorov bez podielu. " +
                        "Autorovi "+ autorId +" bol priradeny podiel "+ podielAutora);
            }
        }
    }
}
