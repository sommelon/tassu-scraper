package star_schema;

import java.sql.ResultSet;
import java.sql.SQLException;

import databases.DatabaseFBERG_LF;
import databases.DatabaseStarScheme;
import tabulky.Autor;
import tabulky.Dielo;

public class StarSchemeFBERG_LF {
    public static void main(String[] args) {
        DatabaseFBERG_LF db = DatabaseFBERG_LF.getInstance();
        DatabaseStarScheme databaseStarScheme = DatabaseStarScheme.getInstance();

        ResultSet rs = db.getDataForStarSchema();
        try {
            while (rs.next()) {
                double pocetStranNaAutora = (double) rs.getInt(16)/rs.getInt(17);
                System.out.println("------------------------------------------------------------\n");

                System.out.println("Nazov: " + rs.getString(2));
                System.out.println("Podnazov: " + rs.getString(18));
                System.out.println("ISBN: " + rs.getString(3));
                System.out.println("ISSN: " + rs.getString(4));
                System.out.println("Miesto vydania: " + rs.getString(5));
                System.out.println("Vydanie: " + rs.getString(6));
                System.out.println("Arch cislo: " + rs.getString(7));
                System.out.println("Rok vydania: " + rs.getString(8));
                System.out.println("Katedra " + rs.getString(9));
                System.out.println("Fakulta: " + rs.getString(10));
                System.out.println("Meno: " + rs.getString(12) + " Priezvisko: " + rs.getString(13));
                System.out.println("Pocet stran autora: "+ pocetStranNaAutora);
                System.out.println("\n------------------------------------------------------------\n\n");


                Dielo dielo = new Dielo();
                dielo.setNazov(rs.getString(2));
                dielo.setPodnazov(rs.getString(18));
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

                ResultSet rsPracovisko = databaseStarScheme.insetIntoPracivosko(rs.getString(9),rs.getString(10), rs.getString(11));
                ResultSet rsCas = databaseStarScheme.insertIntoCas(rs.getString(8));
                ResultSet rsKategoria = databaseStarScheme.insertIntoKategoria(rs.getString(14),rs.getString(15));

                databaseStarScheme.insertIntoFact(rsDielo.getInt(1),rsPracovisko.getInt(1),rsAutor.getInt(1),rsKategoria.getInt(1),rsCas.getInt(1), pocetStranNaAutora);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
