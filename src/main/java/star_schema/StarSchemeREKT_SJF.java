package star_schema;

import databases.DatabaseREKT_SJF;
import databases.DatabaseStarScheme;
import tabulky.Autor;
import tabulky.Dielo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StarSchemeREKT_SJF {
    public static void main(String[] args) throws SQLException {
        DatabaseREKT_SJF db = DatabaseREKT_SJF.getInstance();
        DatabaseStarScheme dbs = DatabaseStarScheme.getInstance();

        ResultSet rs = db.getDataForStarScheme();
        while (rs.next()){
            Dielo dielo = new Dielo();
            Autor autor = new Autor();

            dielo.setNazov(rs.getString(1));
            dielo.setISBN(rs.getString(2));
            dielo.setISSN(rs.getString(3));
            dielo.setMiesto_vydania(rs.getString(4));
            dielo.setVydanie(rs.getString(13));
            dielo.setArchivacne_cislo(rs.getString(5));
            dielo.setKlucove_slova(db.getKlucoveSlova(rs.getInt(12)));
            ResultSet rsDielo = dbs.insertIntoDiela(dielo);
            ResultSet rsCasVydania = dbs.insertIntoCas(rs.getString(6));
            ResultSet rsPracovisko = dbs.insetIntoPracovisko(rs.getString(7), "", "");
            ResultSet rsKategoria = dbs.insertIntoKategoria("", rs.getString(8));
            int pocetStranDiela = Integer.parseInt(rs.getString(9));
            double pocetStranNaAutora = (double) pocetStranDiela*rs.getInt(11)/100;

            System.out.println("------------------------------------------------------------\n");

            System.out.println("Nazov: " + dielo.getNazov());
            System.out.println("Podnazov: " + dielo.getPodnazov());
            System.out.println("ISBN: " + dielo.getISBN());
            System.out.println("ISSN: " + dielo.getISSN());
            System.out.println("Miesto vydania: " + dielo.getMiesto_vydania());
            System.out.println("Vydanie: " + dielo.getVydanie());
            System.out.println("Arch cislo: " + dielo.getArchivacne_cislo());
            System.out.println("Katedra " + rs.getString(7));
            System.out.println("Fakulta: " + "DOPLNIT");

            String[] menoAPriezvisko = rs.getString(10).split(",");
            autor.setPriezvisko(menoAPriezvisko[0]);

            if (menoAPriezvisko.length > 1) {
                autor.setMeno(menoAPriezvisko[1]);
                System.out.print("Meno: "+ menoAPriezvisko[1]);
            }
            System.out.println(" Priezvisko: "+ menoAPriezvisko[0]);
            System.out.println("Pocet stran autora: "+ pocetStranNaAutora);
            System.out.println("------------------------------------------------------------\n");

            ResultSet rsAutor = dbs.insetIntoAutor(autor);

            dbs.insertIntoFact(rsDielo.getInt(1), rsPracovisko.getInt(1), rsAutor.getInt(1), rsKategoria.getInt(1), rsCasVydania.getInt(1), pocetStranNaAutora);
        }
    }
}
