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
        Pattern ISBNP = Pattern.compile("ISBN:? ?([\\- 0-9]+X?)");
        Pattern ISSNP = Pattern.compile("ISSN:? ?([0-9]{4}-[0-9]{3}[0-9xX])");
//    Pattern rokP = Pattern.compile("(- |\\(| )(19[6-9][0-9]|20[01][0-9])(\\.|\\)),?( -| |\n)");
        Pattern rok1P = Pattern.compile("(- |\\(| )(19[6-9][0-9]|20[01][0-9])(\\.|\\))?( -| )?"); //rok vo vseobecnosti na roznych miestach
        Pattern rokNaKonciP = Pattern.compile("[,\\-]? \\(?(19[6-9][0-9]|20[01][0-9])\\)? ?$"); //rok na konci riadku - mal by byt na konci miesta vydania
        Pattern stranyNeuvedeneP = Pattern.compile("([SPsp]\\.? neuved[^ \\-\n]?)|(neuved[^ \\-\n]? [SPsp]\\.?)");
        Pattern strany1P = Pattern.compile("(\\[[^\\]]+\\] )?[PSps]\\.? ?([0-9]+(-[0-9]+)?|\\[[0-9]+(-[0-9]+)?\\])( \\[[^\\]]+\\])*"); //strany aj s poznamkou v hranatych zatvorkach
        Pattern strany2P = Pattern.compile("(\\[[^\\]]+\\] )?([0-9]+(-[0-9]+)?|\\[[0-9]+(-[0-9]+)?\\]) [PSps]\\.?( \\[[^\\]]+\\])*");// 86 p [CD-ROM]; [86] p; 86 p
        Pattern vydanieP = Pattern.compile("(- | )?(\\[?[0-9]+\\.[^-,]+vyd\\.?\\]? ?[^-]*)-");
        Pattern podielP = Pattern.compile("[0-9]{1,3}");
        Pattern autorP = Pattern.compile(" +\\([0-9]{1,3}%?\\)");
        Pattern ohlasP = Pattern.compile("([0-9]{4})  ?\\[([0-9]{1,2})\\] ([^<]+)");
        Pattern znakyNaStranachP = Pattern.compile("^[:.\\-, ]+|[:.\\-, ]+$");

        //TODO - Košice : TU-FBERG - 2000. - 78, [2] s.. - ISBN 80-7099-634-X. WATAFAK????????????????????

        //TODO strana 8 In: Environmentální vzdělávání. - Ostrava : VŠB-TU
        //	Priloha: Rijeka : InTech  264 p.. - ISBN 9789533075020
        String ostatne = "<p style=\"font-size: 11px\">\n" +
                "                                <b>\n" +
                "                                    <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl27_lZNazov\">Ochrana ovzduší</span></b>\n" +
                "                                <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl27_LZNazovP\">/</span>\n" +
                "                                \n" +
                "                                <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl27_lUdajZodpovednosti\">Jozef Mačala, Vladimír Smrž</span>\n" +
                "                                <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl27_lZPokrBezUZ\"> - 2006.</span>\n" +
                "                                In: Environmentální vzdělávání. - Ostrava : VŠB-TU, 2006 P. 229-283. - ISBN 8024811138 \n" +
                "                                  \n" +
                "                               \n" +
                "                                      \n" +
                "                                   <a id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl27_HyperLink1\" target=\"_blank\"></a>\n" +
                "                                \n" +
                "                                \n" +
                "                                <br>\n" +
                "                                <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl27_lAut\">[MAČALA, Jozef - SMRŽ, Vladimír]</span>\n" +
                "                                \n" +
                "                            </p>";


        Matcher m;

        ostatne = ostatne.replaceAll("\n", " "); //TODO vsetky replaceAll na Stringoch sa daju zrychlit keby sa regex skompiloval pri instanciacii a zavolal replaceAll na matcheri namiesto Stringu
        ostatne = ostatne.replaceAll(" {2,}", " ");
        int i1 = ostatne.indexOf("ZPokrBezUZ")+12;
        int i2 = ostatne.indexOf("<a");
        ostatne = ostatne.substring(i1, i2);
        String[] ostatneArray = ostatne.split("</span>");
        if (ostatneArray[1].length() <= " Spôsob prístupu: ".length()){ //ked je za </span> len text " Spôsob prístupu: " alebo nejaky kratsi text, tak je to nepodstatne
            ostatne = ostatneArray[0];
        } else { //Ak je za </span> dlhsi text ako " Spôsob prístupu: ", su tam podstatne informacie
            if (ostatneArray.length == 2 && ostatneArray[0].length() > 10) { //ak je pred </span> text dlhsi ako 10 pismen, je tam aj priloha
                ostatneArray[0] = rok1P.matcher(ostatneArray[0]).replaceAll(""); //odstrani sa rok a mala by ostat iba priloha
                ostatneArray[0] = znakyNaStranachP.matcher(ostatneArray[0]).replaceAll("");
                System.out.println("\tPriloha: " + ostatneArray[0]);
//                dielo.setPriloha(ostatneArray[0]);
            }
            ostatne = ostatneArray[1];
            ostatne = ostatne.replaceAll(" *Spôsob prístupu: *", "");
        }
        m = znakyNaStranachP.matcher(ostatne);
        ostatne = m.replaceAll(""); //odstranenie medzier na zaciatku a na konci

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
        if (!m.find()) {
            m = strany1P.matcher(ostatne); //najprv sa najde vyraz strany1P kvoli pripadu ked rok nie je oddeleny nicim (okrem medzery) (2016 S. 109-114)
            if (m.find()) {
//                dielo.setStrany(m.group(0));
                System.out.println("Naslo sa "+m.group(0));
            } else { //ak podla prveho vyrazu nic nenajde, skusi druhy vyraz
                m = strany2P.matcher(ostatne);
                if (m.find()) {
//                    dielo.setStrany(m.group(0));
                    System.out.println("Naslo sa "+m.group(0));
                }
            }
        }
        ostatne = m.replaceAll("");

        m = vydanieP.matcher(ostatne);
        if (m.find()) {
//            dielo.setVydanie(m.group(2).replaceAll("\\[|\\]", ""));
//            System.out.println("\t\t\t"+dielo.getVydanie());
            ostatne = m.replaceAll("");
        }

        ostatne = znakyNaStranachP.matcher(ostatne).replaceAll("");
        ostatne = rokNaKonciP.matcher(ostatne).replaceAll("");
        Pattern miesto_vydania1P = Pattern.compile("In:(- )?[A-Z][^:\n]+: [a-zA-Z ,]+(, -| -)");
        m = miesto_vydania1P.matcher(ostatne);
        if (m.find()) {
            String miesto_vydania = m.group(0);
//            dielo.setMiesto_vydania(miesto_vydania);
            ostatne = m.replaceAll("");
        }
        ostatne = znakyNaStranachP.matcher(ostatne).replaceAll("");
        System.out.println(ostatne);

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