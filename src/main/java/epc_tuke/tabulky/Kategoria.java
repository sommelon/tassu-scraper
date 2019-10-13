package epc_tuke.tabulky;

public class Kategoria {
    private static int counter;
    private int id;
    private String kod = "";
    private String popis = "";
    private int skupina_id;

    public Kategoria() {
        id = counter;
        counter++;
    }

    public int getCounter() {
        return counter;
    }

    public int getId() {
        return id;
    }

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
