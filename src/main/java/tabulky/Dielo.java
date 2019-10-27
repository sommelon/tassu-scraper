package tabulky;

public class Dielo {
    private Integer dielo_id = null;
    private String archivacne_cislo = null;
    private Integer rok_vydania = null;
    private String nazov = null;
    private String podnazov = null;
    private String vydanie = null;
    private String ISBN = null;
    private String ISSN = null;
    private String miesto_vydania = null;
    private String klucove_slova = null;
    private String odkaz = null;
    private String strany = null;
    private String priloha = null;
    private Integer kategoria_id = null;

    public Integer getDielo_id() {
        return dielo_id;
    }

    public void setDielo_id(Integer dielo_id) {
        this.dielo_id = dielo_id;
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

    public String getVydanie() {
        return vydanie;
    }

    public void setVydanie(String vydanie) {
        this.vydanie = vydanie;
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

    public String getStrany() {
        return strany;
    }

    public void setStrany(String strany) {
        this.strany = strany;
    }

    public String getPriloha() {
        return priloha;
    }

    public void setPriloha(String priloha) {
        this.priloha = priloha;
    }

    public Integer getKategoria_id() {
        return kategoria_id;
    }

    public void setKategoria_id(Integer kategoria_id) {
        this.kategoria_id = kategoria_id;
    }

    @Override
    public String toString() {
        return "Dielo{" +
                "dielo_id=" + dielo_id +
                ", archivacne_cislo='" + archivacne_cislo + '\'' +
                ", rok_vydania=" + rok_vydania +
                ", nazov='" + nazov + '\'' +
                ", podnazov='" + podnazov + '\'' +
                ", vydanie='" + vydanie + '\'' +
                ", ISBN='" + ISBN + '\'' +
                ", ISSN='" + ISSN + '\'' +
                ", miesto_vydania='" + miesto_vydania + '\'' +
                ", klucove_slova='" + klucove_slova + '\'' +
                ", odkaz='" + odkaz + '\'' +
                ", strany='" + strany + '\'' +
                ", priloha='" + priloha + '\'' +
                ", kategoria_id=" + kategoria_id +
                '}';
    }
}