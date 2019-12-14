package star_schema;

import java.sql.ResultSet;
import java.sql.SQLException;

import databases.DatabaseFBERG_LF;
import databases.DatabaseStarScheme;
import tabulky.Autor;
import tabulky.Dielo;

import javax.xml.transform.Result;

public class StarSchemeFBERG_LF {
    public static void main(String[] args) {
        DatabaseFBERG_LF db = DatabaseFBERG_LF.getInstance();
        DatabaseStarScheme databaseStarScheme = DatabaseStarScheme.getInstance();

        ResultSet rs = db.getDataForStarSchema();
        try {
            while (rs.next()) {
                System.out.println("------------------------------------------------------------\n\n");

                System.out.println("Nazov: " + rs.getString(2));
                System.out.println("ISBN: " + rs.getString(3));
                System.out.println("ISSN: " + rs.getString(4));
                System.out.println("Miesto vydania: " + rs.getString(5));
                System.out.println("vydanie: " + rs.getString(6));
                System.out.println("Arch cislo: " + rs.getString(7));
                System.out.println("Katedra " + rs.getString(9));
                System.out.println("Fakulta: " + rs.getString(10));
                System.out.println("Meno: " + rs.getString(12) + " Priezvisko: " + rs.getString(13));
                System.out.println("------------------------------------------------------------\n\n");


                Dielo dielo = new Dielo();
                dielo.setNazov(rs.getString(2));
                dielo.setISBN(rs.getString(3));
                dielo.setISSN(rs.getString(4));
                dielo.setMiesto_vydania(rs.getString(5));
                dielo.setVydanie(rs.getString(6));
                dielo.setRok_vydania(rs.getString(8));
                dielo.setArchivacne_cislo(rs.getString(7));
                dielo.setKlucove_slova(db.getKlucoveSlova(rs.getInt(1)));
                ResultSet rsDielo = databaseStarScheme.insertIntoDiela(dielo);


                Autor autor = new Autor();
                autor.setMeno(rs.getString(12));
                autor.setPriezvisko(rs.getString(13));
                ResultSet rsAutor = databaseStarScheme.insetIntoAutor(autor);

                ResultSet rsPracovisko = databaseStarScheme.insetIntopracivosko(rs.getString(9),rs.getString(10), rs.getString(11));
                ResultSet rsCas = databaseStarScheme.insertIntoCas(rs.getString(8));
                ResultSet rsKategoria = databaseStarScheme.insertIntoKategoria(rs.getString(14),rs.getString(15));

                databaseStarScheme.insertIntoFact(rsDielo.getLong(1),rsPracovisko.getLong(1),rsAutor.getLong(1),rsKategoria.getLong(1),rsCas.getLong(1), isNumeric(rs.getString(16))?Long.valueOf(rs.getString(16)):1L);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
