package epc_tuke;

import databases.DatabaseFBERG_LF;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        DatabaseFBERG_LF db = DatabaseFBERG_LF.getInstance();

        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("headless");
        options.addArguments("window-size=1920x1080");
        options.addArguments("disable-infobars");
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, 15);

        driver.get("https://epc.lib.tuke.sk/PrehladPubl.aspx");
        driver.findElement(By.id("ctl00_ContentPlaceHolderMain_ddlKrit1")).click();
        {
            WebElement dropdown = driver.findElement(By.id("ctl00_ContentPlaceHolderMain_ddlKrit1"));
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//option[. = 'Pracovisko']")));
            dropdown.findElement(By.xpath("//option[. = 'Pracovisko']")).click();
        }
        wait.until(ExpectedConditions.elementToBeClickable(By.id("ctl00_ContentPlaceHolderMain_ddlStredisko")));

        //ulozenie atributov value stredisk FBERG do zoznamu
        driver.findElement(By.id("ctl00_ContentPlaceHolderMain_ddlStredisko")).click();
        List<WebElement> strediskaFBERG = driver.findElements(By.xpath("//*[@id=\"ctl00_ContentPlaceHolderMain_ddlStredisko\"]/option[position()>1]"));
        List<String> strediskaFBERGNazov = new ArrayList<String>();
        for (WebElement webElement : strediskaFBERG) {
            strediskaFBERGNazov.add(webElement.getText().substring(7));
        }

        //ulozenie atributov value stredisk LF do zoznamu
        driver.findElement(By.id("ctl00_ContentPlaceHolderMain_ddlFakulta")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"ctl00_ContentPlaceHolderMain_ddlFakulta\"]/option[@value='09']")));
        driver.findElement(By.xpath("//*[@id=\"ctl00_ContentPlaceHolderMain_ddlFakulta\"]/option[@value='09']")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[@id=\"ctl00_ContentPlaceHolderMain_ddlStredisko\"]/option[@value='109001']")));
        driver.findElement(By.id("ctl00_ContentPlaceHolderMain_ddlStredisko")).click();
        List<WebElement> strediskaLF = driver.findElements(By.xpath("//*[@id=\"ctl00_ContentPlaceHolderMain_ddlStredisko\"]/option[position()>1]"));
        List<String> strediskaLFNazov = new ArrayList<String>();
        for (WebElement webElement : strediskaLF) {
            strediskaLFNazov.add(webElement.getText().substring(7));
        }

        driver.quit();

//        spustenie scrapera pre vsetky strediska

        //toto je len na to, aby som vedel zacat odkial chcem
//        {
//            strediskaLFNazov.subList(0, 6).clear();
//
//            ZaznamyScraper z = new ZaznamyScraper();
//            z.vybratPracovisko("09", strediskaLFNazov.get(0));
////            z.goToPage(54);
//            z.scrape();
//            z.getDriver().close();
//            strediskaLFNazov.remove(0);
//        }

        for (String stredisko : strediskaLFNazov) {
            ZaznamyScraper zaznamyScraper = new ZaznamyScraper();
            zaznamyScraper.vybratPracovisko("09", stredisko);
            zaznamyScraper.scrape();
            zaznamyScraper.getDriver().close();
        }

        for (String stredisko : strediskaFBERGNazov) {
            ZaznamyScraper zaznamyScraper = new ZaznamyScraper();
            zaznamyScraper.vybratPracovisko("01", stredisko);
            zaznamyScraper.scrape();
            zaznamyScraper.getDriver().close();
        }

        db.closeConnection();
    }
}


