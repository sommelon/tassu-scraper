import epc_tuke.Inserter;
import epc_tuke.tabulky.Autor;
import epc_tuke.tabulky.Ohlas;
import epc_tuke.tabulky.Zaznam;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
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

        //TODO tento zaznam je trocha inak reprezentovany v DOM. Jedna cast nie je medzi span tagmi
        String ostatne = "Sledovanie vplyvu posypových solí na procesy v aktivačnej nádrži / Eliška Horniaková, Milan Búgel, Tomáš Bakalár - 2010. In: Chemické listy. Vol. 104, no. 4 (2010), p. 257-260. - ISSN 0009-2770 Spôsob prístupu: http://www.chemicke-listy.cz/docs/full/2010_04_257-260.pdf...\n" +
                "[HORNIAKOVÁ, Eliška (50%) - BÚGEL, Milan (30%) - BAKALÁR, Tomáš (20%)]\n";
    }
}