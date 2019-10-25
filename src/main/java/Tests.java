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
        Pattern optickyDiskP = Pattern.compile("[0-9] elektronick[ýé](ch)? optický(ch)? disk(y|ov)? \\(CD-ROM\\)");
        Pattern sposobPristupuP = Pattern.compile(" *Spôsob prístupu: *");
        Pattern ISBNP = Pattern.compile("ISBN:? ?([\\- 0-9]+X?)");
        Pattern ISSNP = Pattern.compile("ISSN:? ?([0-9]{4}-[0-9]{3}[0-9xX])");
//    Pattern rokP = Pattern.compile("(- |\\(| )(19[6-9][0-9]|20[01][0-9])(\\.|\\)),?( -| |\n)");
        Pattern rok1P = Pattern.compile("(- |\\(| )?(19[6-9][0-9]|20[01][0-9])(\\.|\\))?( -| )?"); //rok vo vseobecnosti na roznych miestach
        Pattern rokNaKonciP = Pattern.compile("[,\\- ]?\\(?(19[6-9][0-9]|20[01][0-9])\\)? ?$"); //rok na konci riadku - mal by byt na konci miesta vydania
        Pattern stranyNeuvedeneP = Pattern.compile("([SPsp]\\.? neuved[^ \\-\n]?)|(neuved[^ \\-\n]? [SPsp]\\.?)");
        Pattern strany1P = Pattern.compile("(\\[[^\\]]+\\] )?[PSps]\\.? ?([0-9]+(-[0-9]+)?|\\[[0-9]+(-[0-9]+)?\\])( \\[[^\\]]+\\])*"); //strany aj s poznamkou v hranatych zatvorkach
        Pattern strany2P = Pattern.compile("(\\[[^\\]]+\\] )?([0-9]+(-[0-9]+)?|\\[[0-9]+(-[0-9]+)?\\]) [PSps]\\.?( \\[[^\\]]+\\])*");// 86 p [CD-ROM]; [86] p; 86 p
        Pattern vydanieP = Pattern.compile("(- | )?(\\[?[0-9]+\\.?[^-,]+vyd\\.?\\]? ?[^-]*)-");
        Pattern podielP = Pattern.compile("[0-9]{1,3}");
        Pattern autorP = Pattern.compile(" +\\([0-9]{1,3}%?\\)");
        Pattern ohlasP = Pattern.compile("([0-9]{4})  ?\\[([0-9]{1,2})\\] ([^<]+)");
        Pattern znakyNaStranachP = Pattern.compile("^[:.\\-, ]+|[:.\\-, ]+$|\\[\\]");

        //TODO - Košice : TU-FBERG - 2000. - 78, [2] s.. - ISBN 80-7099-634-X. WATAFAK????????????????????

        //TODO strana 8 In: Environmentální vzdělávání. - Ostrava : VŠB-TU
        //	Priloha: Rijeka : InTech  264 p.. - ISBN 9789533075020
        String ostatne = "<p style=\"font-size: 11px\">\n" +
                "                                <b>\n" +
                "                                    <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl23_lZNazov\">TeZEK - Technológie znalostnej ekonomiky</span></b>\n" +
                "                                <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl23_LZNazovP\">/</span>\n" +
                "                                \n" +
                "                                <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl23_lUdajZodpovednosti\">Jozef Bucko ... [et al.]</span>\n" +
                "                                <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl23_lZPokrBezUZ\"> - Košice : TU, - 2005. - 1 elektronický optický disk (CD-ROM). - ISBN 80-8073-319-8.</span>\n" +
                "                                \n" +
                "                                  \n" +
                "                               \n" +
                "                                      \n" +
                "                                   <a id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl23_HyperLink1\" target=\"_blank\"></a>\n" +
                "                                \n" +
                "                                \n" +
                "                                <br>\n" +
                "                                <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl23_lAut\">[BUCKO, Jozef - DELINA, Radoslav - GROHOĽ, Milan - HOROVČÁK, Pavel - LAVRIN, Anton - LEVICKÝ, Dušan - MIHÓK, Peter - ŠEVEC, Sergej - VODZINSKÝ, Vladimír - GRAJCÁROVÁ, Ľ. - ČIŽMÁRIKOVÁ, K. - ČIŽMÁRIK, M.]</span>\n" +
                "                                \n" +
                "                            </p>";


        Matcher m;

        ostatne = ostatne.replaceAll("\n", " ");
        ostatne = ostatne.replaceAll(" {2,}", " ");
        int i1 = ostatne.indexOf("ZPokrBezUZ")+12;
        int i2 = ostatne.indexOf("<a");
        ostatne = ostatne.substring(i1, i2);
        ostatne = sposobPristupuP.matcher(ostatne).replaceAll("");

        m = optickyDiskP.matcher(ostatne);
        if (m.find()) {
//            dielo.setPriloha(m.group(0));
            ostatne = m.replaceAll("");
        }

        String[] ostatneArray = ostatne.split("</span>");
        if (ostatneArray.length == 2) {
            if (ostatneArray[1].length() <= 2) { //ked je za </span> nic nie je, pracujeme iba s castou pred </span> (cislo 2 je tam keby nahodou bolo za spanom napr. "2 s")
                ostatne = ostatneArray[0];
            } else {
                if (ostatneArray[0].length() > 10) { //ak je pred </span> text dlhsi ako 10 pismen
                    //je pripad kedy tu priloha nie je, ale su tu tie iste informacie ako za </span>
                    ostatneArray[0] = rok1P.matcher(ostatneArray[0]).replaceAll(""); //rok1P pattern vymaze aj pomlcky okolo roku
//                ostatneArray[0] = znakyNaStranachP.matcher(ostatneArray[0]).replaceAll("");
                    System.out.println("ostatneArray[0]: "+ ostatneArray[0]);
                }
                ostatne = ostatneArray[1];
                System.out.println("ostatneArray[1]: "+ ostatneArray[1]);
            }
        } else {
            ostatne = ostatneArray[0];
        }
        ostatne = znakyNaStranachP.matcher(ostatne).replaceAll("");

        m = ISBNP.matcher(ostatne);
        if (m.find()) {
//            dielo.setISBN(m.group(1));
            ostatne = m.replaceAll("");
        }

        m = ISSNP.matcher(ostatne);
        if (m.find()) {
//            dielo.setISSN(m.group(1));
            ostatne = m.replaceAll("");
        }

        m = stranyNeuvedeneP.matcher(ostatne);
        if (!m.find()) { //ak nenajde nejaku variaciu p neuved tak bude kontrolovat ostatne normalne
            m = strany1P.matcher(ostatne); //najprv sa najde vyraz strany1P kvoli pripadu ked rok nie je oddeleny nicim (okrem medzery) (2016 S. 109-114)
            if (m.find()) {
//                dielo.setStrany(m.group(0));
            } else { //ak podla prveho vyrazu nic nenajde, skusi druhy vyraz
                m = strany2P.matcher(ostatne);
                if (m.find()) {
//                    dielo.setStrany(m.group(0));
                }
            }
        }
        ostatne = m.replaceAll("");

        m = vydanieP.matcher(ostatne);
        if (m.find()) {
//            dielo.setVydanie(m.group(2).replaceAll("\\[|\\]", ""));
            ostatne = m.replaceAll("");
        }

//        Pattern miesto_vydania1P = Pattern.compile("In:(- )?[A-Z][^:\n]+: [a-zA-Z ,]+(, -| -)");
//        m = miesto_vydania1P.matcher(ostatne);
//        if (m.find()) {
//            String miesto_vydania = m.group(0);
//            dielo.setMiesto_vydania(miesto_vydania);
//            ostatne = m.replaceAll("");
//        }

        //TODO zacykluje sa
        m = znakyNaStranachP.matcher(ostatne);
        while (m.find()) {
            ostatne = m.replaceAll("");
            ostatne = rokNaKonciP.matcher(ostatne).replaceAll("");
            m = znakyNaStranachP.matcher(ostatne);
        }

//        dielo.setMiesto_vydania(ostatne);
        System.out.println(ostatne);
    }
}