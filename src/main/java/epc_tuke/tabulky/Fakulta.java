package epc_tuke.tabulky;

public class Fakulta {
    private static int counter;
    private int id;
    private String nazov = "";
    private String skratka = "";

    public Fakulta() {
        id = counter;
        counter++;
    }

    public static int getCounter() {
        return counter;
    }

    public int getId() {
        return id;
    }

    public String getNazov() {
        return nazov;
    }

    public void setNazov(String nazov) {
        this.nazov = nazov;
    }

    public String getSkratka() {
        return skratka;
    }

    public void setSkratka(String skratka) {
        this.skratka = skratka;
    }
}
