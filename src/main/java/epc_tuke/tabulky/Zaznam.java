package epc_tuke.tabulky;

public class Zaznam {
    private static int counter;
    private int id;
    private String archivacne_cislo = "";
    private Integer rok_vydania = null;
    private String nazov = "";
    private String podnazov = "";
    private String ISBN = "";
    private String ISSN = "";
    private String miesto_vydania = "";
    private String klucove_slova = "";
    private String odkaz = "";
    private Integer strana_od = null;
    private Integer strana_do = null;

    public Zaznam() {
        id = counter;
        counter++;
    }

    public static int getCounter() {
        return counter;
    }

    public int getId() {
        return id;
    }

    public String getArchivacne_cislo() {
        return archivacne_cislo;
    }

    public void setArchivacne_cislo(String archivacne_cislo) {
        this.archivacne_cislo = archivacne_cislo;
    }

    public Integer getRok_vydania() {
        return rok_vydania;
    }

    public void setRok_vydania(Integer rok_vydania) {
        this.rok_vydania = rok_vydania;
    }

    public String getNazov() {
        return nazov;
    }

    public void setNazov(String nazov) {
        this.nazov = nazov;
    }

    public String getPodnazov() {
        return podnazov;
    }

    public void setPodnazov(String podnazov) {
        this.podnazov = podnazov;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getISSN() {
        return ISSN;
    }

    public void setISSN(String ISSN) {
        this.ISSN = ISSN;
    }

    public String getMiesto_vydania() {
        return miesto_vydania;
    }

    public void setMiesto_vydania(String miesto_vydania) {
        this.miesto_vydania = miesto_vydania;
    }

    public String getKlucove_slova() {
        return klucove_slova;
    }

    public void setKlucove_slova(String klucove_slova) {
        this.klucove_slova = klucove_slova;
    }

    public String getOdkaz() {
        return odkaz;
    }

    public void setOdkaz(String odkaz) {
        this.odkaz = odkaz;
    }

    public Integer getStrana_od() {
        return strana_od;
    }

    public void setStrana_od(Integer strana_od) {
        this.strana_od = strana_od;
    }

    public Integer getStrana_do() {
        return strana_do;
    }

    public void setStrana_do(Integer strana_do) {
        this.strana_do = strana_do;
    }
}