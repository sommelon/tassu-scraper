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

        Pattern autorOhlasuP = Pattern.compile("\\p{Lu}+, \\p{Lu}[^, :]+");
        Pattern etalP = Pattern.compile("(et al\\.|\\[et al\\.\\]):?");
        Pattern nazovOhlasuP = Pattern.compile("^((?=In:)|[^.])+"); //Od zaciatku riadku po slovo s velkym pismenom

        String ostatne = "                                <b>\n" +
                "                                    <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl21_lZNazov\">Zem a zemské zdroje</span></b>\n" +
                "                                <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl21_LZNazovP\">/</span>\n" +
                "                                \n" +
                "                                <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl21_lUdajZodpovednosti\">Pavol Rybár, Tibor Sasvári</span>\n" +
                "                                <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl21_lZPokrBezUZ\"> - 1. vyd - Košice : Štroffek, - 1998. - 175 s. - ISBN 80-88896-12-6.</span>\n" +
                "                                \n" +
                "                                  \n" +
                "                               \n" +
                "                                      \n" +
                "                                   <a id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl21_HyperLink1\" target=\"_blank\"></a>\n" +
                "                                \n" +
                "                                \n" +
                "                                <br>\n" +
                "                                <span id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl21_lAut\">[RYBÁR, Pavol (50%) - SASVÁRI, Tibor (50%)]</span>\n" +
                "                                <br> <br> 2005  [4] CEHLÁR, Michal, MIHOK, Jozef Bewertung der Mineralrohstofflagerstatten In: Bewertung der Mineralrohstofflagerstatten 173 s ISBN: 80-8073-482-8 " +
                "<br> <br> 2007  [4] KUDELAS, D., RYBÁR, R.: Výskumno-vývojová činnosť Centra obnoviteľných zdrojov energií (COZE) ako príspevok k využívaniu energie vetra na Slovensku. In: Acta Montanistica Slovaca. Roč. 12, č. mimor. 2 (2007), s. 264-268. ISSN 1335-1788. " +
                "<br> <br> 2007  [4] RYBÁR, R., KUDELAS, D.: Tradičné zdroje energie 1. Košice : ES AMS FBERG TU 2007. 119 s. ISBN 80-8073-799-3. " +
                "<br> <br> 2006  [3] KUDELAS, D., RYBÁR, R., FISCHER, G.: Concept of accumulation system configuration enabling the usage of low-potential wind energy. In: Metalurgija. Vol. 45, no. 4 (2006), p. 299-302. ISSN 0543-5846. " +
                "<br> <br> 2006  [3] KUDELAS, D., RYBÁR, R.: Posúdenie možnosti akumulačného spôsobu využitia veternej energie v centrálnej a južnej časti Košickej kotliny. In: Energetika. Roč. 56, č. 3 (2006), s. 98-101. ISSN 0375-8842. " +
                "<br> <br> 2004  [4] RYBÁR, R., KUDELAS, D., FISCHER, G.: Alternatívne zdroje energie III. In: Veterná energia. Košice: Edičné stredisko/AMS, 2004. S. 99. ISBN 80-8073-144-6. " +
                "<br> <br> 2006  [4] MIHOK, J., RYBÁR, R., CEHLÁR, M. et al: Trhaviny v krízových situáciách. Nitra: SPU, 2006. ISBN 80-8069-661-6. " +
                "<br> <br> 2007  [3] MICHALÍKOVÁ, F. et al.: Vlastnosti hnedouhoľných popolčekov zo spaľovania uhlia v tepelnej elektrárni ENO Nováky. In: Recyklace odpadů 11 - 1: 6.-7.12.2007. Ostrava: VŠB-TU, 2007. p. 241-247. ISBN 978-80-248-1597-8." +
                "<br> <br> 2005  [3] VOKOROKOS, Liberios: Parallel computer system utilization in geographic information systems. In: ICCC 2005. IEEE 3rd International Conference on Computational Cybernetics. Mauritius : April 13-16,2005. Budapest: Tech, 2005. P. 333-338. ISBN 963-7154-37-X.\n";

    }
}