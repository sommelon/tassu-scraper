import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tests {
    public static void main(String[] args) {

//        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("window-size=1920x1080");
//        options.addArguments("disable-infobars");
//        WebDriver driver = new ChromeDriver(options);
//        WebDriverWait wait = new WebDriverWait(driver, 15);
//        driver.get("https://epc.lib.tuke.sk/PrehladPubl.aspx");
//
//        driver.findElement(By.id("ctl00_ContentPlaceHolderMain_ddlKrit1")).click();
//        {
//            WebElement dropdown = driver.findElement(By.id("ctl00_ContentPlaceHolderMain_ddlKrit1"));
//            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//option[. = 'Pracovisko']")));
//            dropdown.findElement(By.xpath("//option[. = 'Pracovisko']")).click();
//        }
//        wait.until(ExpectedConditions.elementToBeClickable(By.id("ctl00_ContentPlaceHolderMain_ddlStredisko")));
//
//        WebElement select = driver.findElement(By.id("ctl00_ContentPlaceHolderMain_ddlStredisko"));
//        for(WebElement e : select.findElements(By.xpath("option"))){
//            System.out.println(e.getText());
//        }

        String ostatne = "<p style=\"font-size: 11px\">\n" +
                "                                <b>\n" +
                "                                    <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl19_lZNazov\">Uskladňovanie tekutých odpadov v horninových štruktúrach</span></b>\n" +
                "                                <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl19_LZNazovP\">/</span>\n" +
                "                                \n" +
                "                                <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl19_lUdajZodpovednosti\">Daniela Marasová ... [et al.]</span>\n" +
                "                                <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl19_lZPokrBezUZ\"> - [1. vyd.] - Košice : TU, FBERG, - 1997. - 114 s. - ISBN 80-88896-11-8.</span>\n" +
                "                                \n" +
                "                                  \n" +
                "                               \n" +
                "                                      \n" +
                "                                   <a id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl19_HyperLink1\" target=\"_blank\"></a>\n" +
                "                                \n" +
                "                                \n" +
                "                                <br>\n" +
                "                                <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl19_lAut\">[MARASOVÁ, Daniela (25%) - PINKA, Ján (25%) - BUJOK, Petr (25%) - KRIŠTÍN, Štefan (25%)]</span>\n" +
                "                                \n" +
                "                            </p>";


        Matcher m;

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
            System.out.println(ISBN);
        }
        ostatne = m.replaceAll("");

        Pattern ISSNP = Pattern.compile("ISSN:? ?([0-9]{4}-[0-9]{3}[0-9xX])");
        m = ISSNP.matcher(ostatne);
        if (m.find()) {
            String ISSN = m.group(1);
            System.out.println(ISSN);
        }
        ostatne = m.replaceAll("");

        Pattern stranyP = Pattern.compile("[PSps]\\. ?[0-9]+(-[0-9]+)?"); //najprv sa najde tento vyraz kvoli pripadu ked rok nie je oddeleny nicim okrem medzery (2016 S. 109-114)
        m = stranyP.matcher(ostatne);
        if (m.find()) {
            String strany = m.group(0);
            System.out.println(strany);
        } else { //ak sa nenajde ten prvy vzor, tak hlada dalsi vzor
            stranyP = Pattern.compile("[0-9]+(-[0-9]+)? [PSps]\\.");
            m = stranyP.matcher(ostatne);
            if (m.find()) {
                String strany = m.group(0);
                System.out.println(strany);
            }
        }
        ostatne = m.replaceAll("");

        Pattern vydanieP = Pattern.compile("- ?(\\[?[0-9]\\.[^-,]+vyd\\.?\\]? ?[^-]*)-");
        m = vydanieP.matcher(ostatne);
        if (m.find()) {
            String vydanie = m.group(1);
            System.out.println(vydanie);
        }
        ostatne = m.replaceAll("");
//        ostatne = ostatne.replaceAll("[. \\-,]{2,}", " ").trim();
        System.out.println(ostatne);

//        String[] ohlasyNespracovane = ostatne.split("<br> <br> ");
//        List<String> ohlasyList = Arrays.asList(ohlasyNespracovane);
//        List<String> ohlasyList = new ArrayList<String>();
//
//        Pattern ohlasyP = Pattern.compile("[0-9]{4}  ?\\[[0-9]{1,2}\\][^<]+");
//        m = ohlasyP.matcher(ostatne);
//        while (m.find()){
//            ohlasyList.add(m.group(0).trim());
//        }
//        for (String s1 : ohlasyList) {
//            System.out.println("s = '" + s1 + "'");
//        }
    }
}