package star_schema;

import databases.DatabaseSF_EF;
import databases.DatabaseStarScheme;
import tabulky.Autor;
import tabulky.Dielo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StarSchemeSF_EF {
    public static void main(String[] args) {
        DatabaseSF_EF databaseSF_ef = DatabaseSF_EF.getInstance();
        DatabaseStarScheme databaseStarScheme = DatabaseStarScheme.getInstance();
        ResultSet rs = databaseSF_ef.getDataForStarSchema();
        try {
            while (rs.next()) {
                double pocetStranNaAutora = (double) rs.getInt(13)*rs.getInt(14)/100;

                String miestoVydania = null;
                if (rs.getString(5)!=null) {
                    miestoVydania = rs.getString(5);
                }
                if (rs.getString(15)!=null) {
                    miestoVydania += miestoVydania != null ? " & "+rs.getString(15) : rs.getString(15);
                }

                System.out.println("------------------------------------------------------------\n");

                System.out.println("Nazov: " + rs.getString(2));
                System.out.println("Podnazov: " + rs.getString(16));
                System.out.println("ISBN: " + rs.getString(3));
                System.out.println("ISSN: " + rs.getString(4));
                System.out.println("Miesto vydania: " + miestoVydania);
                System.out.println("Arch cislo: " + rs.getString(6));
                System.out.println("Rok vydania: " + rs.getString(7));
                System.out.println("Katedra " + rs.getString(8));
                System.out.println("Fakulta: " + rs.getString(9));
                System.out.println("Meno: " + rs.getString(10) + " Priezvisko: " + rs.getString(11));
                System.out.println("Pocet stran autora: "+ pocetStranNaAutora);
                System.out.println("\n------------------------------------------------------------\n\n");

                Dielo dielo = new Dielo();
                dielo.setNazov(rs.getString(2));
                dielo.setPodnazov(rs.getString(16));
                dielo.setISBN(rs.getString(3));
                dielo.setISSN(rs.getString(4));
                dielo.setMiesto_vydania(miestoVydania);
                dielo.setArchivacne_cislo(rs.getString(6));
                dielo.setRok_vydania(rs.getString(7));
                dielo.setKlucove_slova(databaseSF_ef.getKlucoveSlova(rs.getInt(1)));
                ResultSet rsDielo = databaseStarScheme.insertIntoDiela(dielo);

                Autor autor = new Autor();
                autor.setMeno(rs.getString(10));
                autor.setPriezvisko(rs.getString(11));
                ResultSet rsAutor = databaseStarScheme.insetIntoAutor(autor);

                ResultSet rsPracovisko = databaseStarScheme.insetIntoPracovisko(rs.getString(8), rs.getString(9), rs.getString(9).contains("Stavebná fakulta") ? "SF" : "EF");
                ResultSet rsCas = databaseStarScheme.insertIntoCas(rs.getString(7));
                ResultSet rsKategoria = databaseStarScheme.insertIntoKategoria(katPopis(rs.getString(12)), rs.getString(12));

                databaseStarScheme.insertIntoFact(rsDielo.getInt(1), rsPracovisko.getInt(1), rsAutor.getInt(1), rsKategoria.getInt(1), rsCas.getInt(1), pocetStranNaAutora);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static String katPopis(String skratka) {
        if ("AAA".equals(skratka)) {
            return "Vedecké monografie vydané v zahraničných vydavateľstvách";
        } else if ("AAB".equals(skratka)) {
            return "Vedecké monografie vydané v domácich vydavateľstvách";
        } else if ("ABA".equals(skratka)) {
            return "Štúdie v časopisoch a zborníkoch charakteru vedeckej monografie vydané v zahraničných vydavateľstvách";
        } else if ("ABB".equals(skratka)) {
            return "Štúdie v časopisoch a zborníkoch charakteru vedeckej monografie vydané v domácich vydavateľstvách";
        } else if ("ABC".equals(skratka)) {
            return "Kapitoly vo vedeckých monografiách vydané v zahraničných vydavateľstvách";
        } else if ("ABD".equals(skratka)) {
            return "Kapitoly vo vedeckých monografiách vydané v domácich vydavateľstvách";
        } else if ("ACA".equals(skratka)) {
            return "Vysokoškolské učebnice vydané v zahraničných vydavateľstvách";
        } else if ("ACB".equals(skratka)) {
            return "Vysokoškolské učebnice vydané v domácich vydavateľstvách";
        } else if ("BAA".equals(skratka)) {
            return "Odborné monografie vydané v zahraničných vydavateľstvách";
        } else if ("BAB".equals(skratka)) {
            return "Odborné monografie vydané v domácich vydavateľstvách";
        } else if ("EAJ".equals(skratka)) {
            return "Odborné preklady publikácií - knižné";
        } else if ("FAI".equals(skratka)) {
            return "Redakčné a zostavovateľské práce knižného charakteru (bibliografie, encyklopédie, katalógy, slovníky, zborníky...)";
        } else if ("ADC".equals(skratka)) {
            return "Vedecké práce v zahraničných karentovaných časopisoch";
        } else if ("ADD".equals(skratka)) {
            return "Vedecké práce v domácich karentovaných časopisoch";
        } else if ("AEG".equals(skratka)) {
            return "Stručné oznámenia, abstrakty vedeckých prác v zahraničných karentovaných časopisoch";
        } else if ("AEH".equals(skratka)) {
            return "Stručné oznámenia, abstrakty vedeckých prác v domácich karentovaných časopisoch";
        } else if ("AGJ".equals(skratka)) {
            return "Autorské osvedčenia, patenty, objavy";
        } else if ("BDC".equals(skratka)) {
            return "Odborné práce v zahraničných karentovaných časopisoch";
        } else if ("ACD".equals(skratka)) {
            return "Kapitoly vo vysokoškolských učebniciach vydaných v domácich vydavateľstvách";
        } else if ("ADE".equals(skratka)) {
            return "Vedecké práce v zahraničných nekarentovaných časopisoch";
        } else if ("ADF".equals(skratka)) {
            return "Vedecké práce v domácich nekarentovaných časopisoch";
        } else if ("AEC".equals(skratka)) {
            return "Vedecké práce v zahraničných recenzovaných vedeckých zborníkoch, monografiách";
        } else if ("AED".equals(skratka)) {
            return "Vedecké práce v domácich recenzovaných vedeckých zborníkoch, monografiách";
        } else if ("AFA".equals(skratka)) {
            return "Publikované pozvané príspevky na zahraničných vedeckých konferenciách";
        } else if ("AFB".equals(skratka)) {
            return "Publikované pozvané príspevky na domácich vedeckých konferenciách";
        } else if ("AFC".equals(skratka)) {
            return "Publikované príspevky na zahraničných vedeckých konferenciách";
        } else if ("AFD".equals(skratka)) {
            return "Publikované príspevky na domácich vedeckých konferenciách";
        } else if ("AFE".equals(skratka)) {
            return "Abstrakty pozvaných príspevkov zo zahraničných konferencií";
        } else if ("AFF".equals(skratka)) {
            return "Abstrakty pozvaných referátov z domácich konferencií";
        } else if ("AFG".equals(skratka)) {
            return "Abstrakty príspevkov zo zahraničných konferencií";
        } else if ("AFH".equals(skratka)) {
            return "Abstrakty príspevkov z domácich konferencií";
        } else if ("BBA".equals(skratka)) {
            return "Kapitoly v odborných monografiách vydané v zahraničných vydavateľstvách";
        } else if ("BBB".equals(skratka)) {
            return "Kapitoly v odborných monografiách vydané v domácich vydavateľstvách";
        } else if ("BDB".equals(skratka)) {
            return "Heslá v odborných terminologických slovníkoch a encyklopédiách vydaných v domácich vydavateľstvách";
        } else if ("BDE".equals(skratka)) {
            return "Odborné práce v zahraničných nekarentovaných časopisoch";
        } else if ("BDF".equals(skratka)) {
            return "Odborné práce v domácich nekarentovaných časopisoch";
        } else if ("BEC".equals(skratka)) {
            return "Odborné práce v zahraničných recenzovaných zborníkoch (konferenčných aj nekonferenčných)";
        } else if ("BED".equals(skratka)) {
            return "Odborné práce v domácich recenzovaných zborníkoch (konferenčných aj nekonferenčných)";
        } else if ("BFA".equals(skratka)) {
            return "Abstrakty odborných prác zo zahraničných podujatí (konferencie...)";
        } else if ("BFB".equals(skratka)) {
            return "Abstrakty odborných prác z domácich podujatí (konferencie...)";
        } else if ("ADM".equals(skratka)) {
            return "Vedecké práce v zahraničných časopisoch registrovaných v databázach Web of Science alebo SCOPUS";
        } else if ("ADN".equals(skratka)) {
            return "Vedecké práce v domácich časopisoch registrovaných v databázach Web of Science alebo SCOPUS";
        } else if ("AEE".equals(skratka)) {
            return "Vedecké práce v zahraničných nerecenzovaných vedeckých zborníkoch, monografiách";
        } else if ("AEF".equals(skratka)) {
            return "Vedecké práce v domácich nerecenzovaných vedeckých zborníkoch, monografiách";
        } else if ("AFI".equals(skratka)) {
            return "Preprinty vedeckých prác vydané v zahraničných vydavateľstvách";
        } else if ("AFJ".equals(skratka)) {
            return "Preprinty vedeckých prác vydané v domácich vydavateľstvách";
        } else if ("AFK".equals(skratka)) {
            return "Postery zo zahraničných konferencií";
        } else if ("AFL".equals(skratka)) {
            return "Postery z domácich konferencií";
        } else if ("AGI".equals(skratka)) {
            return "Správy o vyriešených vedeckovýskumných úlohách";
        } else if ("BEE".equals(skratka)) {
            return "Odborné práce v zahraničných nerecenzovaných zborníkoch (konferenčných aj nekonferenčných)";
        } else if ("BEF".equals(skratka)) {
            return "Odborné práce v domácich nerecenzovaných zborníkoch (konferenčných aj nekonferenčných)";
        } else if ("CAH".equals(skratka)) {
            return "Audiovizuálne diela (videokazeta,film,CD-ROM,DVD) nakrútené v domácej produkcii";
        } else if ("DAI".equals(skratka)) {
            return "Dizertačné a habilitačné práce";
        } else if ("EDI".equals(skratka)) {
            return "Recenzie v časopisoch a zborníkoch";
        } else if ("EDJ".equals(skratka)) {
            return "Prehľadové práce, odborné preklady v časopisoch a zborníkoch";
        } else if ("GAI".equals(skratka)) {
            return "Výskumné štúdie a priebežné správy";
        } else if ("GHG".equals(skratka)) {
            return "Práce zverejnené na internete";
        } else if ("GII".equals(skratka)) {
            return "Rôzne publikácie a dokumenty, ktoré nemožno zaradiť do žiadnej z predchádzajúcich kategórií";
        }
        return "No details given";
    }

    public static boolean isNumeric(String str) {
        if (str != null) {
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