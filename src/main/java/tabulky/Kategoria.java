package tabulky;

public class Kategoria {
    private String kod = "";
    private String popis = "";
    private int skupina_id;

    public String getKod() {
        return kod;
    }

    public void setKod(String kod) {
        this.kod = kod;
    }

    public String getPopis() {
        return popis;
    }

    public void setPopis(String popis) {
        this.popis = popis;
    }

    public int getSkupina_id() {
        return skupina_id;
    }

    public void setSkupina_id(int skupina_id) {
        this.skupina_id = skupina_id;
    }
}
