package epc_tuke;

import epc_tuke.tabulky.Autor;
import epc_tuke.tabulky.Dielo;
import epc_tuke.tabulky.Ohlas;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PracoviskoScraper{
    private ChromeOptions options;
    private WebDriver driver;
    private WebDriverWait wait;
    private String pracoviskoValue;
    private String strediskoValue;
    private int currentPage = 0;

    public PracoviskoScraper() {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        options = new ChromeOptions();
//        options.addArguments("headless");
        options.addArguments("window-size=1920x1080");
        options.addArguments("disable-infobars");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 15);
    }

    //Inicializacia - Nacita stranku, vyhlada konkretnu fakultu a pracovisko a pocka, kym sa nacitaju vysledky.
    public void init(String pracoviskoValue, String strediskoValue){
        this.pracoviskoValue = pracoviskoValue;
        this.strediskoValue = strediskoValue;

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
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"ctl00_ContentPlaceHolderMain_ddlFakulta\"]/option[@value='"+pracoviskoValue+"']")));
            driver.findElement(By.xpath("//*[@id=\"ctl00_ContentPlaceHolderMain_ddlFakulta\"]/option[@value='"+pracoviskoValue+"']")).click();

            //najde a klikne na stredisko
            driver.findElement(By.id("ctl00_ContentPlaceHolderMain_ddlStredisko")).click();
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"ctl00_ContentPlaceHolderMain_ddlStredisko\"]/option[@value='"+strediskoValue+"']")));
            driver.findElement(By.xpath("//*[@id=\"ctl00_ContentPlaceHolderMain_ddlStredisko\"]/option[@value='"+strediskoValue+"']")).click();
        }

        checkCheckboxesAndSearch();

        //pocka sa, kym sa nacita tabulka so zaznamami
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("ctl00_ContentPlaceHolderMain_gvVystupyByFilter")));
        }catch (StaleElementReferenceException e){
            System.err.println("Nepodarilo sa nacitat vysledky pre dane pracovisko.");
            if (driver.findElement(By.id("ctl00_ContentPlaceHolderMain_lPocetNajdenychZaznamov")).getText().equals("Počet nájdených záznamov : 0"))
                System.err.println("Dovod: Ziadne zaznamy pre dane pracovisko.");
        }
    }

    //Vytiahne vsetky zaznamy na jednej strane okrem prveho
    public void scrape(){
        if (pracoviskoValue == null || strediskoValue == null) {
            System.err.println("Najprv treba zavolat init() s parametrami.");
            return;
        }

        List<WebElement> zaznamyNaStrane = driver.findElements(
                By.xpath("/html/body/form/div[3]/div[2]/div/div/div/table[@id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter\"]/tbody/tr[position()>1 and position()<last()]"));
        for (WebElement riadokTabulky : zaznamyNaStrane) {
            exctractDataFromTableRow(riadokTabulky);
        }

        nextPage();
    }

    private void exctractDataFromTableRow(WebElement riadokTabulky){
        Matcher m;
        Dielo dielo = new Dielo();
        dielo.setArchivacne_cislo(riadokTabulky.findElement(By.xpath("td[2]/span")).getText());
        dielo.setRok_vydania(Integer.parseInt(riadokTabulky.findElement(By.xpath("td[4]/span")).getText()));
        dielo.setNazov(riadokTabulky.findElement(By.xpath("td[5]/p/b/span")).getText());
        dielo.setPodnazov(riadokTabulky.findElement(By.xpath("td[5]/p/span[1]")).getText().replaceFirst("/$", "")); //replace kvoli vymazaniu lomitka na konci
        String odkaz = riadokTabulky.findElement(By.xpath("td[5]/p/a[1]")).getAttribute("href");
        dielo.setOdkaz(odkaz);

        String ostatne = riadokTabulky.findElement(By.xpath("td[5]/p")).getText();
        //V zaznamoch, kde su hypertextove odkazy, je len rok medzi spanmi a ostatne informacie su za spanom a pred <a
        ostatne = ostatne.replaceAll("\n", " ");
        ostatne = ostatne.replaceAll(" {2,}", " ");
        int i1 = ostatne.indexOf("ZPokrBezUZ")+12;
        int i2 = ostatne.indexOf("<a");
        ostatne = ostatne.substring(i1, i2);
        String[] ostatneArray = ostatne.split("</span>"); //rozdelenie stringu
        if (ostatneArray[0].length() > ostatneArray[1].length()){ //ak nema string ziadny text za </span>
            ostatne = ostatneArray[0];
        } else { //ak ma string nejaky text za </span>
            ostatne = ostatneArray[1];
            ostatne = ostatne.replaceAll(" *Spôsob prístupu: *", "");
        }
        ostatne = ostatne.replaceAll("^ +| +$", ""); //odstranenie medzier na zaciatku a na konci

        Pattern ISBNP = Pattern.compile("ISBN:? ?([\\- 0-9]+)");
        m = ISBNP.matcher(ostatne);
        if (m.find()) {
            String ISBN = m.group(1);
            dielo.setISBN(ISBN);
        }
        ostatne = m.replaceAll("");

        Pattern ISSNP = Pattern.compile("ISSN:? ?([0-9]{4}-[0-9]{3}[0-9xX])");
        m = ISSNP.matcher(ostatne);
        if (m.find()) {
            String ISSN = m.group(1);
            dielo.setISSN(ISSN);
        }
        ostatne = m.replaceAll("");

        Pattern stranyP = Pattern.compile("[PSps]\\. ?[0-9]+(-[0-9]+)?"); //najprv sa najde tento vyraz kvoli pripadu ked rok nie je oddeleny nicim okrem medzery (2016 S. 109-114)
        m = stranyP.matcher(ostatne);
        if (m.find()) {
            String strany = m.group(0);
            dielo.setStrany(strany);
        } else { //ak sa nenajde ten prvy vzor, tak hlada dalsi vzor
            stranyP = Pattern.compile("[0-9]+(-[0-9]+)? [PSps]\\.");
            m = stranyP.matcher(ostatne);
            if (m.find()) {
                String strany = m.group(0);
                dielo.setStrany(strany);
            }
        }
        ostatne = m.replaceAll("");

        Pattern vydanieP = Pattern.compile("- ?(\\[?[0-9]\\.[^-,]+vyd\\.?\\]? ?[^-]*)-");
        m = vydanieP.matcher(ostatne);
        if (m.find()) {
            String vydanie = m.group(1);
            dielo.setVydanie(vydanie);
        }
        ostatne = m.replaceAll("");

        //k - Košice : TU, FBERG - 2001
//        Pattern miesto_vydaniaP = Pattern.compile("- [A-Z][^:\n]+ : [a-zA-Z ,]+(, -| -)");
//        m = miesto_vydaniaP.matcher(ostatne);
//        if (m.find()) {
//            String miesto_vydania = m.group(0);
//            dielo.setMiesto_vydania(miesto_vydania);
//        }
//        ostatne = m.replaceAll("");

        String autoryNespracovane = riadokTabulky.findElement(By.xpath("td[5]/p/span[4]")).getText();
        //odstranenie hranatych zatvoriek na zaciatku a na konci
        autoryNespracovane = autoryNespracovane.replaceAll("\\[|\\]", "");
        String[] autoryRozdelene = autoryNespracovane.split(" - ");

        Pattern podielP = Pattern.compile("[0-9]{1,3}");
        Pattern autorP = Pattern.compile(" +\\([0-9]{1,3}%?\\)");

        //rozdelenie autorov a podielov
        ArrayList<Autor> autori = new ArrayList<Autor>();
        ArrayList<Integer> podiely = new ArrayList<Integer>();
        for (String autorAPodiel : autoryRozdelene) {
            m = podielP.matcher(autorAPodiel);
            if(m.find())
                podiely.add(Integer.parseInt(m.group(0)));
            else
                podiely.add(null);

            m = autorP.matcher(autorAPodiel);
            //odstrani podiel a rozdeli meno a priezvisko podla ciarky
            String[] priezviskoAMeno = m.replaceFirst("").split(", ");
            Autor autor = new Autor();
            try {
                autor.setPriezvisko(priezviskoAMeno[0]);
                autor.setMeno(priezviskoAMeno[1]);
            }catch (ArrayIndexOutOfBoundsException e){ //TODO: Stava sa to pri mene Phan Huy Nam. Nie je ciarka medzi menom a priezviskom. Osetrit.
                System.out.println(priezviskoAMeno[0]);
            }
            autori.add(autor);
        }

        Pattern ohlasP = Pattern.compile("([0-9]{4})  ?\\[([0-9]{1,2})\\] ([^<]+)");
        m = ohlasP.matcher(riadokTabulky.findElement(By.xpath("td[5]/p")).getAttribute("innerHTML"));

        List<String> ohlasyNespracovane = new ArrayList<String>();
        ArrayList<Ohlas> ohlasy = new ArrayList<Ohlas>();
        while (m.find()){
            Ohlas ohlas = new Ohlas();
            ohlas.setRok_vydania(Integer.parseInt(m.group(1)));
            ohlas.setKategoria_ohlasu_id(Integer.parseInt(m.group(2)));
        }


        for (String ohlasCely : ohlasyNespracovane) {
            Ohlas ohlas = new Ohlas();
            //TODO Zparsovat ohlas. Zatial sa dava cely do nazvu.
            ohlas.setNazov(ohlasCely);
            ohlasy.add(ohlas);
        }

//        Inserter inserter = new Inserter(dielo, autori, podiely, ohlasy);
    }

    private void nextPage() {
        currentPage = Integer.parseInt(driver.findElement(
                By.xpath("//*[@id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter\"]/tbody/tr[last()]/td/table/tbody/tr/td/span")).getText());
        int nextPage = currentPage+1;

        try {
            WebElement nextPageElement = driver.findElement(By.linkText(Integer.toString(nextPage)));
            nextPageElement.click();
            //pocka sa kym text na XPath adrese bude mat hodnotu dalsej strany
            wait.until(ExpectedConditions.textToBe(
                    By.xpath("//*[@id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter\"]/tbody/tr[last()]/td/table/tbody/tr/td/span"), Integer.toString(nextPage)));

            System.out.println("Strana " + nextPage);
            scrape();
        } catch (NoSuchElementException e){
            System.err.println("Dalsia strana neexistuje.");
        }
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
}
