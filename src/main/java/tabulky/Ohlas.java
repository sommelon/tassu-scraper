package tabulky;

import java.util.ArrayList;

public class Ohlas {
    private Integer ohlas_id = null;
    private Integer rok_vydania = null;
    private String nazov = null;
    private String strany = null;
    private String ISBN = null;
    private String ISSN = null;
    private String miesto_vydania = null;
    private Integer kategoria_ohlasu_id = null;

    private ArrayList<Autor> autori = new ArrayList<Autor>();

    public Integer getOhlas_id() {
        return ohlas_id;
    }

    public void setOhlas_id(Integer ohlas_id) {
        this.ohlas_id = ohlas_id;
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

    public Integer getKategoria_ohlasu_id() {
        return kategoria_ohlasu_id;
    }

    public void setKategoria_ohlasu_id(Integer kategoria_ohlasu_id) {
        this.kategoria_ohlasu_id = kategoria_ohlasu_id;
    }

    public ArrayList<Autor> getAutori() {
        return autori;
    }

    public void setAutori(ArrayList<Autor> autori) {
        this.autori = autori;
    }

    @Override
    public String toString() {
        return "Ohlas{" +
                "ohlas_id=" + ohlas_id +
                ", rok_vydania=" + rok_vydania +
                ", nazov='" + nazov + '\'' +
                ", strany='" + strany + '\'' +
                ", ISBN='" + ISBN + '\'' +
                ", ISSN='" + ISSN + '\'' +
                ", miesto_vydania='" + miesto_vydania + '\'' +
                ", kategoria_ohlasu_id=" + kategoria_ohlasu_id +
                '}';
    }
}
