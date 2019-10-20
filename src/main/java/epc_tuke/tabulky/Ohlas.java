package epc_tuke.tabulky;

public class Ohlas {
    private static int counter;
    private int id;
    private Integer rok_vydania = null;
    private String nazov = "";
    private String zbierka = "";
    private String strany = "";
    private String ISBN = "";
    private String ISSN = "";
    private String miesto_vydania = "";
    private int dielo_id;
    private int kategoria_ohlasu_id;

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

    public String getStrany() {
        return strany;
    }

    public void setStrany(String strany) {
        this.strany = strany;
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

    public int getDielo_id() {
        return dielo_id;
    }

    public void setDielo_id(int dielo_id) {
        this.dielo_id = dielo_id;
    }

    public int getKategoria_ohlasu_id() {
        return kategoria_ohlasu_id;
    }

    public void setKategoria_ohlasu_id(int kategoria_ohlasu_id) {
        this.kategoria_ohlasu_id = kategoria_ohlasu_id;
    }
}
