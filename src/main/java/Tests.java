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
                "                                    <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl12_lZNazov\">Fractional order systems</span></b>\n" +
                "                                <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl12_LZNazovP\">Modeling and Control Applications : World Scientific Series on Nonlinear Science, Series A - Vol. 72/</span>\n" +
                "                                \n" +
                "                                <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl12_lUdajZodpovednosti\">Riccardo Caponetto ... [et al.]</span>\n" +
                "                                <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl12_lZPokrBezUZ\"> - Singapore : World Scientific Publishing - 2010. - 178 p. - ISBN 978-981-4304-19-1.</span>\n" +
                "                                \n" +
                "                                  \n" +
                "                               \n" +
                "                                      \n" +
                "                                   Spôsob prístupu: <a id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl12_HyperLink1\" href=\"http://www.worldscibooks.com/chaos/7709.html\" target=\"_blank\">http://www.worldscibooks.com/chaos/7709.html...</a>\n" +
                "                                \n" +
                "                                \n" +
                "                                <br>\n" +
                "                                <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl12_lAut\">[CAPONETTO, Riccardo (33%) - DONGOLA, Giovanni (17%) - FORTUNA, Luigi (17%) - PETRÁŠ, Ivo (33%)]</span>\n" +
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

        Pattern p = Pattern.compile("(- |\\(| )(19[6-9][0-9]|20[01][0-9])(\\.|\\)),?( | -|\n)");
        m = p.matcher(ostatne);
        if (m.find()) {
            System.out.println(m.group(0));
            ostatne = m.replaceAll("");
        }

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

        ostatne = ostatne.replaceAll("^[.\\-, ]{2,}|[.\\-, ]{2,}$", "");

        Pattern miesto_vydaniaP = Pattern.compile("- [A-Z][^:\n]+ : [a-zA-Z ,]+(, -| -)");
        m = miesto_vydaniaP.matcher(ostatne);
        if (m.find()) {
            String miesto_vydania = m.group(0);
            System.out.println(miesto_vydania);
        }
        ostatne = m.replaceAll("");


        System.out.println(ostatne);
    }
}