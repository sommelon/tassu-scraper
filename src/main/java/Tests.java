import org.openqa.selenium.By;
import tabulky.Autor;
import tabulky.Ohlas;

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

        Pattern cisloP = Pattern.compile("[0-9]+");
        Pattern novyRiadokP = Pattern.compile("\n");
        Pattern medzeryP = Pattern.compile(" {2,}");
        Pattern hranateZatvorkyP = Pattern.compile("\\[|\\]");
        Pattern optickyDiskP = Pattern.compile("[0-9] elektronick[ýé](ch)? optický(ch)? disk(y|ov)? \\(CD-ROM\\)");
        Pattern sposobPristupuP = Pattern.compile(" *Spôsob prístupu: *");
        Pattern ISBNP = Pattern.compile("ISBN:? ?([\\- 0-9]+[xX]?)");
//    Pattern ISSNP = Pattern.compile("ISSN:? ?([0-9]{4}( | - |-)?[0-9]{3}[0-9xX])");
        Pattern ISSNP = Pattern.compile("ISSN:? ?([\\- 0-9]+[xX]?)"); //ZLY FORMAT, ale neviem co robit s takymi udajmi, tak ich proste dam do DB tak jak su tam
        //    Pattern rokP = Pattern.compile("(- |\\(| )(19[6-9][0-9]|20[01][0-9])(\\.|\\)),?( -| |\n)");
//    Pattern rok1P = Pattern.compile("(- |\\(| )?(19[6-9][0-9]|20[01][0-9])(\\.|\\))?( -| )?"); //rok vo vseobecnosti na roznych miestach
        Pattern rokNaKonciP = Pattern.compile("[,\\- ]?\\(?(19[6-9][0-9]|20[01][0-9])\\)? ?$"); //rok na konci riadku - mal by byt na konci miesta vydania
        Pattern cisloNaKonciP = Pattern.compile("- [\\(\\[]?[0-9][\\)\\]]? *$"); //cislo na konci riadku po pomlcke nema vyznam '- 78'
        Pattern stranyNeuvedeneP = Pattern.compile("([SPsp]\\.? neuved[^ \\-\n]?)|(neuved[^ \\-\n]? [SPsp]\\.?)");
        Pattern strany1P = Pattern.compile(" [PSps]\\.?\\.? ?([0-9]+(-[0-9]+)?|\\[[0-9]+(-[0-9]+)?\\])"); //strany aj s poznamkou v hranatych zatvorkach
        Pattern strany2P = Pattern.compile(" ([0-9]+(-[0-9]+)?|\\[[0-9]+(-[0-9]+)?\\]) [PSps]\\.?\\.?");// 86 p, [86] p
        Pattern prilohaP = Pattern.compile("( \\[[\\p{L} :\\-]+\\])+"); //[CD-ROM] [USB kluc]
        Pattern vydanieP = Pattern.compile("(- | )?(\\[?[0-9]+\\.?[^-,]+vyd\\.?\\]? ?[^-]*)-");
        Pattern podielP = Pattern.compile("[0-9]{1,3}");
        Pattern autorP = Pattern.compile(" +\\([0-9]{1,3}%?\\)");
        Pattern ohlasP = Pattern.compile("([0-9]{4})  ?\\[([0-9]{1,2})\\] ([^<]+)");
        Pattern znakyNaStranachP = Pattern.compile("^[:.\\-, ]+|[:.\\-, ]+$|\\[\\]");
        Pattern autorOhlasuP = Pattern.compile("\\p{Lu}+ ?, ?\\p{Lu}[^, :]+");
        Pattern etalP = Pattern.compile("(et al\\.|\\[et al\\.\\]):?");
        Pattern nazovOhlasuP = Pattern.compile("^.+In:|^[^.]+");

        String ostatne =
                "                                <b>\n" +
                "                                    <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl02_lZNazov\">Kurzové riziko a možnosti jeho znižovania</span></b>\n" +
                "                                <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl02_LZNazovP\">/</span>\n" +
                "                                \n" +
                "                                <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl02_lUdajZodpovednosti\">Denisa Al-Zabidi a Abed Al-Zabidi</span>\n" +
                "                                <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl02_lZPokrBezUZ\"> - 2007.</span>\n" +
                "                                In: Acta Montanistica Slovaca. Roč. 12, č. 2 (2007), s. 251-257. - ISSN 1335-1788 \n" +
                "                                  \n" +
                "                               \n" +
                "                                      \n" +
                "                                   Spôsob prístupu: <a id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl02_HyperLink1\" href=\"http://actamont.tuke.sk/pdf/2007/s2/3alzabidi.pdf\" target=\"_blank\">http://actamont.tuke.sk/pdf/2007/s2/3alzabidi.pdf...</a>\n" +
                "                                \n" +
                "                                \n" +
                "                                <br>\n" +
                "                                <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl02_lAut\">[AL-ZABIDI, Denisa (50%) - AL-ZABIDI, Abed (50%)]</span>\n" +
                "                                \n";


        Matcher m;

        ostatne = novyRiadokP.matcher(ostatne).replaceAll("");
        ostatne = medzeryP.matcher(ostatne).replaceAll("");
        int i1 = ostatne.indexOf("ZPokrBezUZ") + 12;
        int i2 = ostatne.indexOf("<a");
        ostatne = ostatne.substring(i1, i2);
        ostatne = sposobPristupuP.matcher(ostatne).replaceAll("");

        m = optickyDiskP.matcher(ostatne);
        if (m.find()) {
            ostatne = m.replaceAll("");
        }

        m = ISBNP.matcher(ostatne);
        if (m.find()) {
            ostatne = m.replaceAll("");
        }

        m = ISSNP.matcher(ostatne);
        if (m.find()) {
            ostatne = m.replaceAll("");
        }

        m = prilohaP.matcher(ostatne);
        if (m.find()) {
            ostatne = m.replaceAll("");
        }

        m = stranyNeuvedeneP.matcher(ostatne);
        if (!m.find()) { //ak nenajde nejaku variaciu p neuved tak bude kontrolovat ostatne normalne
            m = strany1P.matcher(ostatne); //najprv sa najde vyraz strany1P kvoli pripadu ked rok nie je oddeleny nicim (okrem medzery) (2016 S. 109-114)
            if (m.find()) {
            } else { //ak podla prveho vyrazu nic nenajde, skusi druhy vyraz
                m = strany2P.matcher(ostatne);
            }
        }
        ostatne = m.replaceAll("");

        m = vydanieP.matcher(ostatne);
        if (m.find()) {
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
//                System.out.println(ANSI_YELLOW + "ostatneArray[0]: " + ostatneArray[0] + ANSI_RESET);
//                System.out.println(ANSI_YELLOW + "ostatneArray[1]: " + ostatneArray[1] + ANSI_RESET);
        } else {
            ostatne = ostatneArray[0];
        }

        System.out.println(ostatne);
    }
}