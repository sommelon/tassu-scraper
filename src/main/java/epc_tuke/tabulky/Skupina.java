package epc_tuke.tabulky;

public class Skupina {
    private static int counter;
    private int id;
    private String kod = "";
    private String popis = "";

    public Skupina() {
        id = counter;
        counter++;
    }

    public static int getCounter() {
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
}
