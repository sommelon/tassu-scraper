package epc_tuke.tabulky;

public class Ohlas {
    private static int counter;
    private int id;
    private Integer rok_vydania = null;
    private String nazov = "";
    private String zbierka = "";
    private Integer strana_od = null;
    private Integer strana_do = null;
    private String ISBN = "";
    private String ISSN = "";
    private String miesto_vydania = "";
    private int zaznam_id;

    public Ohlas() {
        id = counter;
        counter++;
    }

    public static int getCounter() {
        return counter;
    }

    public int getId() {
        return id;
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

    public String getZbierka() {
        return zbierka;
    }

    public void setZbierka(String zbierka) {
        this.zbierka = zbierka;
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

    public int getZaznam_id() {
        return zaznam_id;
    }

    public void setZaznam_id(int zaznam_id) {
        this.zaznam_id = zaznam_id;
    }
}
