package star_schema;

import databases.DatabaseFVT_FMMR;
import databases.DatabaseStarScheme;
import tabulky.Autor;
import tabulky.Dielo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StarSchemeFVT_FMMR {
    public static void main(String[] args) throws SQLException {
        DatabaseFVT_FMMR db = DatabaseFVT_FMMR.getInstance();
        DatabaseStarScheme dbs = DatabaseStarScheme.getInstance();

        ResultSet rs = db.getDataForStarScheme();
        while (rs.next()){
            Dielo dielo = new Dielo();
            Autor autor = new Autor();

            dielo.setNazov(rs.getString(2));
            dielo.setISBN(rs.getString(3));
            dielo.setISSN(rs.getString(4));
            dielo.setMiesto_vydania(rs.getString(6));
            dielo.setArchivacne_cislo(rs.getString(7));
            dielo.setKlucove_slova(db.getKlucoveSlova(rs.getInt(1)));
            ResultSet rsDielo = dbs.insertIntoDiela(dielo);
            ResultSet rsCasVydania = dbs.insertIntoCas(rs.getString(5));
            ResultSet rsPracovisko = dbs.insetIntoPracovisko(rs.getString(13), rs.getString(14), rs.getString(15));
            ResultSet rsKategoria = dbs.insertIntoKategoria("", rs.getString(8));
            int pocetStranDiela = Integer.parseInt(rs.getString(11));
            double pocetStranNaAutora = (double) pocetStranDiela*rs.getInt(12)/100;

            System.out.println("------------------------------------------------------------\n");

            System.out.println("Nazov: " + dielo.getNazov());
            System.out.println("Podnazov: " + dielo.getPodnazov());
            System.out.println("ISBN: " + dielo.getISBN());
            System.out.println("ISSN: " + dielo.getISSN());
            System.out.println("Miesto vydania: " + dielo.getMiesto_vydania());
            System.out.println("Vydanie: " + dielo.getVydanie());
            System.out.println("Arch cislo: " + dielo.getArchivacne_cislo());
            System.out.println("Katedra " + rs.getString(13));
            System.out.println("Fakulta: " + rs.getString(14));

            autor.setMeno(rs.getString(9));
            autor.setPriezvisko(rs.getString(10));
            ResultSet rsAutor = dbs.insetIntoAutor(autor);

            try{
                dbs.insertIntoFact(rsDielo.getInt(1), rsPracovisko.getInt(1), rsAutor.getInt(1), rsKategoria.getInt(1), rsCasVydania.getInt(1), pocetStranNaAutora);
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }
}
