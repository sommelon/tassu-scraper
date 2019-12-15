package star_schema;

import databases.DatabaseFEI_FU;
import databases.DatabaseStarScheme;
import tabulky.Autor;
import tabulky.Dielo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StarSchemeFEI_FU {
    public static void main(String[] args) throws SQLException {
        DatabaseFEI_FU db = DatabaseFEI_FU.getInstance();
        DatabaseStarScheme dbs = DatabaseStarScheme.getInstance();

        ResultSet rs = db.getDataForStarScheme();
        while (rs.next()){
            Dielo dielo = new Dielo();
            Autor autor = new Autor();

            dielo.setNazov(rs.getString(1));
            dielo.setPodnazov(rs.getString(2));
            String ISBNISSN = rs.getString(3);
            if (ISBNISSN != null) {
                if (ISBNISSN.contains("ISBN")) {
                    dielo.setISBN(ISBNISSN.substring(5));
                } else if (ISBNISSN.contains("ISSN")) {
                    dielo.setISSN(ISBNISSN.substring(5));
                }
            }
            dielo.setVydanie(rs.getString(4));
            dielo.setRok_vydania(rs.getString(5));
            String miestoVydania = null;
            if (rs.getString(6)!=null) {
                miestoVydania = rs.getString(6);
            }
            if (rs.getString(7)!=null) {
                miestoVydania += miestoVydania != null ? " & "+rs.getString(7) : rs.getString(7);
            }
            if (rs.getString(8)!=null) {
                miestoVydania += miestoVydania != null ? " & "+rs.getString(8) : rs.getString(8);
            }

            dielo.setMiesto_vydania(miestoVydania);
            dielo.setArchivacne_cislo(rs.getString(9));
            dielo.setKlucove_slova(db.getKlucoveSlova(rs.getInt(17)));
            ResultSet rsDielo = dbs.insertIntoDiela(dielo);
            ResultSet rsCasVydania = dbs.insertIntoCas(dielo.getRok_vydania());
            ResultSet rsPracovisko = dbs.insetIntoPracovisko(rs.getString(14), rs.getString(15), "");
            ResultSet rsKategoria = dbs.insertIntoKategoria("", rs.getString(16));
            int pocetStranDiela = rs.getInt(10);
            double pocetStranNaAutora = (double) pocetStranDiela*rs.getInt(11)/100;

            autor.setMeno(rs.getString(12));
            autor.setPriezvisko(rs.getString(13));
            System.out.println("------------------------------------------------------------\n");

            System.out.println("Nazov: " + dielo.getNazov());
            System.out.println("Podnazov: " + dielo.getPodnazov());
            System.out.println("ISBN: " + dielo.getISBN());
            System.out.println("ISSN: " + dielo.getISSN());
            System.out.println("Miesto vydania: " + dielo.getMiesto_vydania());
            System.out.println("Vydanie: " + dielo.getVydanie());
            System.out.println("Arch cislo: " + dielo.getArchivacne_cislo());
            System.out.println("Katedra " + rs.getString(14));
            System.out.println("Fakulta: " + rs.getString(15));
            System.out.println("Meno: "+ autor.getMeno() +" Priezvisko: "+ autor.getPriezvisko());
            System.out.println("Pocet stran autora: "+ pocetStranNaAutora);
            System.out.println("------------------------------------------------------------\n");

            ResultSet rsAutor = dbs.insetIntoAutor(autor);

            dbs.insertIntoFact(rsDielo.getInt(1), rsPracovisko.getInt(1), rsAutor.getInt(1), rsKategoria.getInt(1), rsCasVydania.getInt(1), pocetStranNaAutora);
        }
    }
}
