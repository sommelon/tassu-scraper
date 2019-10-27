package epc_tuke;
import tabulky.Autor;
import tabulky.Dielo;
import tabulky.Ohlas;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZaznamyScraper {
    private ChromeOptions options;
    private WebDriver driver;
    private WebDriverWait wait;
    private int currentPage = 1;
    private int maxPages = 0;
    private Integer pracoviskoID;
    private Database db = Database.getInstance();

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";

    private Pattern cisloP = Pattern.compile("[0-9]+");
    private Pattern novyRiadokP = Pattern.compile("\n");
    private Pattern medzeryP = Pattern.compile(" {2,}");
    private Pattern hranateZatvorkyP = Pattern.compile("\\[|\\]");
    private Pattern optickyDiskP = Pattern.compile("[0-9] elektronick[ýé](ch)? optický(ch)? disk(y|ov)? \\(CD-ROM\\)");
    private Pattern sposobPristupuP = Pattern.compile(" *Spôsob prístupu: *");
    private Pattern ISBNP = Pattern.compile("ISBN:? ?([\\- 0-9]+X?)");
    private Pattern ISSNP = Pattern.compile("ISSN:? ?([0-9]{4}-[0-9]{3}[0-9xX])");
//    private Pattern rokP = Pattern.compile("(- |\\(| )(19[6-9][0-9]|20[01][0-9])(\\.|\\)),?( -| |\n)");
//    private Pattern rok1P = Pattern.compile("(- |\\(| )?(19[6-9][0-9]|20[01][0-9])(\\.|\\))?( -| )?"); //rok vo vseobecnosti na roznych miestach
    private Pattern rokNaKonciP = Pattern.compile("[,\\- ]?\\(?(19[6-9][0-9]|20[01][0-9])\\)? ?$"); //rok na konci riadku - mal by byt na konci miesta vydania
    private Pattern cisloNaKonciP = Pattern.compile("- [\\(\\[]?[0-9][\\)\\]]? *$"); //rok na konci riadku - mal by byt na konci miesta vydania
    private Pattern stranyNeuvedeneP = Pattern.compile("([SPsp]\\.? neuved[^ \\-\n]?)|(neuved[^ \\-\n]? [SPsp]\\.?)");
    private Pattern strany1P = Pattern.compile("(\\[[^\\]]+\\] )?[PSps]\\.?\\.? ?([0-9]+(-[0-9]+)?|\\[[0-9]+(-[0-9]+)?\\])( \\[[^\\]]+\\])*"); //strany aj s poznamkou v hranatych zatvorkach
    private Pattern strany2P = Pattern.compile("(\\[[^\\]]+\\] )?([0-9]+(-[0-9]+)?|\\[[0-9]+(-[0-9]+)?\\]) [PSps]\\.?\\.?( \\[[^\\]]+\\])*");// 86 p [CD-ROM]; [86] p; 86 p
    private Pattern vydanieP = Pattern.compile("(- | )?(\\[?[0-9]+\\.?[^-,]+vyd\\.?\\]? ?[^-]*)-");
    private Pattern podielP = Pattern.compile("[0-9]{1,3}");
    private Pattern autorP = Pattern.compile(" +\\([0-9]{1,3}%?\\)");
    private Pattern ohlasP = Pattern.compile("([0-9]{4})  ?\\[([0-9]{1,2})\\] ([^<]+)");
    private Pattern znakyNaStranachP = Pattern.compile("^[:.\\-, ]+|[:.\\-, ]+$|\\[\\]");
    private Pattern autorOhlasuP = Pattern.compile("\\p{Lu}+, \\p{Lu}[^, :]+");
    private Pattern etalP = Pattern.compile("(et al\\.|\\[et al\\.\\]):?");
    private Pattern nazovOhlasuP = Pattern.compile("^((?=In:)|[^.])+"); //od zaciatku riadku po In: alebo po bodku - pravdepodobne to niekde matchne zle

    public ZaznamyScraper() {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        options = new ChromeOptions();
//        options.addArguments("headless");
        options.addArguments("window-size=1920x1080");
        options.addArguments("disable-infobars");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 30);
    }

    //Inicializacia - Nacita stranku, vyhlada konkretnu fakultu a pracovisko a pocka, kym sa nacitaju vysledky.
    public void vybratPracovisko(String fakultaValue, String strediskoName){
        pracoviskoID = db.getPracoviska().get(strediskoName);
        System.out.println(ANSI_BLUE+ "Fakulta: "+ fakultaValue + ", stredisko: "+ strediskoName +ANSI_RESET);
        driver.get("https://epc.lib.tuke.sk/PrehladPubl.aspx");
        //kliknutie na tu sipku, ktora otvori dalsie moznosti
        driver.findElement(By.id("ctl00_ContentPlaceHolderMain_Image1")).click();

        //Z dropdownu sa zvoli Pracovisko
        driver.findElement(By.id("ctl00_ContentPlaceHolderMain_ddlKrit1")).click();
        {
            //najde a klikne na Pracovisko
            WebElement dropdown = driver.findElement(By.id("ctl00_ContentPlaceHolderMain_ddlKrit1"));
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//option[. = 'Pracovisko']")));
            dropdown.findElement(By.xpath("option[. = 'Pracovisko']")).click();

            //najde a klikne na fakultu
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("ctl00_ContentPlaceHolderMain_ddlFakulta")));
            driver.findElement(By.id("ctl00_ContentPlaceHolderMain_ddlFakulta")).click();
            WebElement fakultaOption = driver.findElement(
                    By.xpath("//*[@id=\"ctl00_ContentPlaceHolderMain_ddlFakulta\"]/option[@value='" + fakultaValue + "']"));
            wait.until(ExpectedConditions.elementToBeClickable(fakultaOption));
            fakultaOption.click();

            //najde a klikne na stredisko
            wait.until(ExpectedConditions.elementToBeClickable(By.id("ctl00_ContentPlaceHolderMain_ddlStredisko")));
            driver.findElement(By.id("ctl00_ContentPlaceHolderMain_ddlStredisko")).click();
            if (fakultaValue.equals("09")) {
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//*[@id=\"ctl00_ContentPlaceHolderMain_ddlStredisko\"]/option[@value='109001']")));
            } else if (fakultaValue.equals("01")){
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//*[@id=\"ctl00_ContentPlaceHolderMain_ddlStredisko\"]/option[@value='101001']")));
            }
            WebElement strediskoOption = driver.findElement(
                    By.xpath("//*[@id=\"ctl00_ContentPlaceHolderMain_ddlStredisko\"]/option[text()[contains(.,'"+strediskoName+"')]]"));
            wait.until(ExpectedConditions.elementToBeClickable(strediskoOption));
            strediskoOption.click();
        }

        checkCheckboxesAndSearch();

        //pocka sa, kym sa nacita tabulka so zaznamami
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("ctl00_ContentPlaceHolderMain_lPocetNajdenychZaznamov")));
        }catch (TimeoutException e){
            System.err.println("Nepodarilo sa nacitat vysledky pre dane pracovisko.");
        }

        String pocetZaznamov = driver.findElement(By.id("ctl00_ContentPlaceHolderMain_lPocetNajdenychZaznamov")).getText();
        Matcher m = cisloP.matcher(pocetZaznamov);
        if(m.find())
            maxPages = (int) Math.ceil((double) Integer.parseInt(m.group(0))/30);
    }

    //Vytiahne vsetky zaznamy na jednej strane okrem prveho
    public void scrape(){
        if (maxPages == 0) {
            System.out.println(ANSI_RED+ "Ziadne zaznamy pre dane pracovisko." +ANSI_RESET);
            return;
        }

        List<WebElement> zaznamyNaStrane = driver.findElements(
                By.xpath("/html/body/form/div[3]/div[2]/div/div/div/table[@id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter\"]/tbody/tr[position()>1 and position()<last()]"));
        for (WebElement riadokTabulky : zaznamyNaStrane) {
            try {
                exctractDataFromTableRow(riadokTabulky);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        nextPage();
    }

    //Prepne na dalsiu stranu
    private void nextPage() {
        try {
            goToPage(currentPage+1);
            scrape();
        } catch (NoSuchPageException e){
            System.out.println(ANSI_RED+ "Strana "+ currentPage+1 +" neexistuje." +ANSI_RESET);
        }
    }

    //Prepne na specificku stranu
    public void goToPage(int page){
        if (page < 1 || page > maxPages)
            throw new NoSuchPageException();

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("__doPostBack('ctl00$ContentPlaceHolderMain$gvVystupyByFilter','Page$"+page+"')");
        try {
            wait.until(ExpectedConditions.textToBe(
                    By.xpath("//*[@id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter\"]/tbody/tr[last()]/td/table/tbody/tr/td/span"), Integer.toString(page)));
        }catch (StaleElementReferenceException e){
            System.err.println("Nova strana "+ page +" sa nestihla nacitat do stanoveneho casu.");
        }
        currentPage = page;
        System.out.println(ANSI_BLUE+ "Strana " + currentPage +ANSI_RESET);
    }

    //Parsuje data z WebElementu, ktory reprezentuje riadok tabulky
    private void exctractDataFromTableRow(WebElement riadokTabulky) throws SQLException {
        Matcher m;
        ResultSet rs;
        Dielo dielo = new Dielo();
        dielo.setArchivacne_cislo(riadokTabulky.findElement(By.xpath("td[2]/span")).getText());
        rs = db.selectDielo(dielo.getArchivacne_cislo());
        if (!rs.next()) { //ak dielo este nie je v tabulke, vytiahnu sa ostatne data
            String kategoria = riadokTabulky.findElement(By.xpath("td[3]/span")).getText();
            Integer kategoria_id = db.getKategorie().get(kategoria);
            if (kategoria_id == null)
                System.err.println("Kategoria sa nenasla.");
            dielo.setKategoria_id(kategoria_id);
            dielo.setRok_vydania(Integer.parseInt(riadokTabulky.findElement(By.xpath("td[4]/span")).getText()));
            dielo.setNazov(riadokTabulky.findElement(By.xpath("td[5]/p/b/span")).getText());
            String podnazov = riadokTabulky.findElement(By.xpath("td[5]/p/span[1]")).getText();
            dielo.setPodnazov(podnazov.substring(0, podnazov.length()-1)); //substring kvoli vymazaniu lomitka na konci
            String odkaz = riadokTabulky.findElement(By.xpath("td[5]/p/a[1]")).getAttribute("href");
            dielo.setOdkaz(odkaz);

            String ostatne = riadokTabulky.findElement(By.xpath("td[5]/p")).getAttribute("innerHTML");
            //V zaznamoch, kde su hypertextove odkazy, je len rok medzi spanmi a ostatne informacie su za spanom a pred <a
            ostatne = novyRiadokP.matcher(ostatne).replaceAll("");
            ostatne = medzeryP.matcher(ostatne).replaceAll("");
            int i1 = ostatne.indexOf("ZPokrBezUZ") + 12;
            int i2 = ostatne.indexOf("<a");
            ostatne = ostatne.substring(i1, i2);
            ostatne = sposobPristupuP.matcher(ostatne).replaceAll("");

            m = optickyDiskP.matcher(ostatne);
            if (m.find()) {
                dielo.setPriloha(m.group(0));
                ostatne = m.replaceAll("");
            }

            m = ISBNP.matcher(ostatne);
            if (m.find()) {
                dielo.setISBN(m.group(1));
                ostatne = m.replaceAll("");
            }

            m = ISSNP.matcher(ostatne);
            if (m.find()) {
                dielo.setISSN(m.group(1));
                ostatne = m.replaceAll("");
            }

            m = stranyNeuvedeneP.matcher(ostatne);
            if (!m.find()) { //ak nenajde nejaku variaciu p neuved tak bude kontrolovat ostatne normalne
                m = strany1P.matcher(ostatne); //najprv sa najde vyraz strany1P kvoli pripadu ked rok nie je oddeleny nicim (okrem medzery) (2016 S. 109-114)
                if (m.find()) {
                    dielo.setStrany(m.group(0));
                } else { //ak podla prveho vyrazu nic nenajde, skusi druhy vyraz
                    m = strany2P.matcher(ostatne);
                    if (m.find()) {
                        dielo.setStrany(m.group(0));
                    }
                }
            }
            ostatne = m.replaceAll("");

            m = vydanieP.matcher(ostatne);
            if (m.find()) {
                dielo.setVydanie(hranateZatvorkyP.matcher(m.group(2)).replaceAll(""));
                ostatne = m.replaceAll("");
            }

            //---------------------------Miesto vydania---------------------------------
            String[] ostatneArray = ostatne.split("</span>");
            if (ostatneArray.length == 2) {
                if (ostatneArray[1].length() <= ostatneArray[0].length()) { //ked je za </span> nic nie je, pracujeme iba s castou pred </span> (cislo 2 je tam keby nahodou bolo za spanom napr. "2 s")
                    ostatne = ostatneArray[0];
                } else {
                    ostatne = ostatneArray[1];
                }
                System.out.println(ANSI_YELLOW + "ostatneArray[0]: " + ostatneArray[0] + ANSI_RESET);
                System.out.println(ANSI_YELLOW + "ostatneArray[1]: " + ostatneArray[1] + ANSI_RESET);
            } else {
                ostatne = ostatneArray[0];
            }

//        Pattern miesto_vydania1P = Pattern.compile("In:(- )?[A-Z][^:\n]+: [a-zA-Z ,]+(, -| -)");
//        m = miesto_vydania1P.matcher(ostatne);
//        if (m.find()) {
//            String miesto_vydania = m.group(0);
//            dielo.setMiesto_vydania(miesto_vydania);
//            ostatne = m.replaceAll("");
//        }

            ostatne = odstranitZnakyARokyNaStranach(ostatne);

            dielo.setMiesto_vydania(ostatne);
            System.out.println(dielo);
            if (dielo.getPriloha() != null)
                System.out.println(ANSI_YELLOW + "\tPriloha: " + dielo.getPriloha() + ANSI_RESET);

            //---------------------------Ohlasy---------------------------------
            m = ohlasP.matcher(riadokTabulky.findElement(By.xpath("td[5]/p")).getAttribute("innerHTML"));

            ArrayList<Ohlas> ohlasy = spracovanieOhlasov(m);

            //---------------------------Autori---------------------------------
            String autoriNespracovane = riadokTabulky.findElement(By.xpath("td[5]/p/span[4]")).getText();
            //odstranenie hranatych zatvoriek na zaciatku a na konci
            autoriNespracovane = hranateZatvorkyP.matcher(autoriNespracovane).replaceAll("");
            String[] autoriRozdelene = autoriNespracovane.split(" - ");

            //rozdelenie autorov a podielov
            ArrayList<Autor> autori = new ArrayList<Autor>();
            ArrayList<Integer> podiely = new ArrayList<Integer>();
            for (String autorAPodiel : autoriRozdelene) {
                m = podielP.matcher(autorAPodiel);
                if (m.find())
                    podiely.add(Integer.parseInt(m.group(0)));
                else
                    podiely.add(null);

                m = autorP.matcher(autorAPodiel);
                //odstrani podiel a rozdeli meno a priezvisko podla ciarky
                String[] priezviskoAMeno = m.replaceFirst("").split(", ");
                Autor autor = new Autor();
                if (priezviskoAMeno.length == 2) {
                    autor.setPriezvisko(priezviskoAMeno[0]);
                    autor.setMeno(priezviskoAMeno[1]);
                } else { //ak nie je meno a priezvisko oddelene ciarkou
                    String priezvisko = priezviskoAMeno[0].substring(0, priezviskoAMeno[0].indexOf(" ")); //priezvisko po prvu medzeru
                    String meno = priezviskoAMeno[0].substring(priezviskoAMeno[0].indexOf(" ") + 1);
                    autor.setPriezvisko(priezvisko);
                    autor.setMeno(meno);
                }
                autori.add(autor);
            }

            nahratDoDB(dielo, autori, ohlasy, podiely);

        } else { //ak dielo uz je v tabulke, urobi sa zaznam s novym pracoviskom v tabulke autor_dielo_pracovisko
            int dieloID = rs.getInt(1);
            rs = db.selectAutorIdAPodielByDielo(dieloID);
            ArrayList<Integer> autorIDs = new ArrayList<Integer>();
            ArrayList<Integer> podiely = new ArrayList<Integer>();
            while (rs.next()){
                autorIDs.add(rs.getInt(1));
                podiely.add(rs.getInt(2));
            }

            for (int i = 0; i < autorIDs.size(); i++) {
                db.insertIntoAutorDieloPracovisko(autorIDs.get(i), dieloID, pracoviskoID, podiely.get(i));
            }
        }
    }

    private void nahratDoDB(Dielo dielo, ArrayList<Autor> autori, ArrayList<Ohlas> ohlasy, ArrayList<Integer> podiely) throws SQLException {
        ResultSet rs;
        rs = db.insertIntoDiela(dielo);
        dielo.setDielo_id(rs.getInt(1));

        for (Autor autor : autori) {
            rs = db.insertIntoAutori(autor);
            if(rs.next())
                autor.setAutor_id(rs.getInt(1));
        }

        for (int i = 0; i < autori.size(); i++) {
            db.insertIntoAutorDieloPracovisko(autori.get(i).getAutor_id(), dielo.getDielo_id(), pracoviskoID, podiely.get(i));
        }

        for (Ohlas ohlas : ohlasy) {
            rs = db.selectOhlas(ohlas.getNazov());
            if (!rs.next()) { //ak ohlas este nie je v databaze, vlozi sa tam
                rs = db.insertIntoOhlasy(ohlas);
                ohlas.setOhlas_id(rs.getInt(1));

                for (Autor autor : ohlas.getAutori()) {
                    rs = db.insertIntoAutori(autor);
                    if(rs.next())
                        autor.setAutor_id(rs.getInt(1));
                }

                for (int i = 0; i < ohlas.getAutori().size(); i++) {
                    db.insertIntoAutorOhlas(ohlas.getAutori().get(i).getAutor_id(), ohlas.getOhlas_id());
                }
            }else {
                ohlas.setOhlas_id(rs.getInt(1));
            }

            db.insertIntoDieloOhlas(dielo.getDielo_id(), ohlas.getOhlas_id());
        }
    }

    private ArrayList<Ohlas> spracovanieOhlasov(Matcher m){
        ArrayList<Ohlas> ohlasy = new ArrayList<Ohlas>();
        String ostatne;
        Matcher mo;
        while (m.find()) {
            Ohlas ohlas = new Ohlas();
            ohlas.setRok_vydania(Integer.parseInt(m.group(1)));
            ohlas.setKategoria_ohlasu_id(Integer.parseInt(m.group(2)));

            ostatne = m.group(3);
            mo = ISBNP.matcher(ostatne);
            if (mo.find()) {
                ohlas.setISBN(mo.group(1));
                ostatne = mo.replaceAll("");
            }

            mo = ISSNP.matcher(ostatne);
            if (mo.find()) {
                ohlas.setISSN(mo.group(1));
                ostatne = mo.replaceAll("");
            }

            mo = stranyNeuvedeneP.matcher(ostatne);
            if (!mo.find()) { //ak nenajde nejaku variaciu p neuved tak bude kontrolovat ostatne normalne
                mo = strany1P.matcher(ostatne); //najprv sa najde vyraz strany1P kvoli pripadu ked rok nie je oddeleny nicim (okrem medzery) (2016 S. 109-114)
                if (mo.find()) {
                    ohlas.setStrany(mo.group(0));
                } else { //ak podla prveho vyrazu nic nenajde, skusi druhy vyraz
                    mo = strany2P.matcher(ostatne);
                    if (mo.find()) {
                        ohlas.setStrany(mo.group(0));
                    }
                }
            }
            ostatne = mo.replaceAll("");
            ostatne = znakyNaStranachP.matcher(ostatne).replaceAll("");
            ostatne = rokNaKonciP.matcher(ostatne).replaceAll("");
            ostatne = znakyNaStranachP.matcher(ostatne).replaceAll("");

            mo = autorOhlasuP.matcher(ostatne);
            while (mo.find()){
                String[] priezviskoAMeno = mo.group(0).split(", ");
                Autor autor = new Autor();
                if (priezviskoAMeno.length == 2) {
                    autor.setPriezvisko(priezviskoAMeno[0]);
                    autor.setMeno(priezviskoAMeno[1]);
                } else { //ak nie je meno a priezvisko oddelene ciarkou
                    String priezvisko = priezviskoAMeno[0].substring(0, priezviskoAMeno[0].indexOf(" ")); //priezvisko po prvu medzeru
                    String meno = priezviskoAMeno[0].substring(priezviskoAMeno[0].indexOf(" ") + 1);
                    autor.setPriezvisko(priezvisko);
                    autor.setMeno(meno);
                }
                ohlas.getAutori().add(autor);
            }

            ostatne = mo.replaceAll("");
            ostatne = etalP.matcher(ostatne).replaceAll("");
            ostatne = znakyNaStranachP.matcher(ostatne).replaceAll("");

            mo = nazovOhlasuP.matcher(ostatne);
            if (mo.find()){
                ohlas.setNazov(mo.group(0));
                ostatne = mo.replaceAll("");
            } else {
                System.err.println("Nazov ohlasu sa nenasiel.");
            }

            ostatne = odstranitZnakyARokyNaStranach(ostatne);

            ohlas.setMiesto_vydania(ostatne);

            ohlasy.add(ohlas);
            System.out.println(ohlas);
        }

        return ohlasy;
    }

    private String odstranitZnakyARokyNaStranach (String s){
        Matcher m;
        m = znakyNaStranachP.matcher(s);
        while (m.find()) {
            s = m.replaceAll("");
            s = rokNaKonciP.matcher(s).replaceAll("");
            s = cisloNaKonciP.matcher(s).replaceAll("");
            m = znakyNaStranachP.matcher(s);
        }
        return s;
    }

    private void checkCheckboxesAndSearch(){
        //Pocka sa, kym sa v DOMe objavia checkboxy a bude sa na nich dat kliknut
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("ctl00_ContentPlaceHolderMain_chbOhlasy")));
        wait.until(ExpectedConditions.elementToBeClickable(By.id("ctl00_ContentPlaceHolderMain_chbOhlasy")));
        wait.until(ExpectedConditions.elementToBeClickable(By.id("ctl00_ContentPlaceHolderMain_chbAjPercentualnePodiely")));
        //klikne na checkboxy
        driver.findElement(By.id("ctl00_ContentPlaceHolderMain_chbOhlasy")).click();
        driver.findElement(By.id("ctl00_ContentPlaceHolderMain_chbAjPercentualnePodiely")).click();
        //klikne na tlacidlo a zacne sa hladat
        driver.findElement(By.id("ctl00_ContentPlaceHolderMain_btnHladaj")).click();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public WebDriver getDriver() {
        return driver;
    }
}
