package epc_tuke.tabulky;

public class Pracovisko {
    private static int counter;
    private int id;
    private String nazov = "";
    private int fakulta_id;

    public Pracovisko() {
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

    public int getFakulta_id() {
        return fakulta_id;
    }

    public void setFakulta_id(int fakulta_id) {
        this.fakulta_id = fakulta_id;
    }
}
