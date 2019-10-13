package epc_tuke;

import epc_tuke.tabulky.Pracovisko;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {

    public static void main(String[] args) {
//        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
//        ChromeOptions options = new ChromeOptions();
////        options.addArguments("headless");
//        options.addArguments("window-size=1920x1080");
//        options.addArguments("disable-infobars");
//        WebDriver driver = new ChromeDriver(options);
//        WebDriverWait wait = new WebDriverWait(driver, 15);
//
//        driver.get("https://epc.lib.tuke.sk/PrehladPubl.aspx");
//        driver.findElement(By.id("ctl00_ContentPlaceHolderMain_ddlKrit1")).click();
//        {
//            WebElement dropdown = driver.findElement(By.id("ctl00_ContentPlaceHolderMain_ddlKrit1"));
//            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//option[. = 'Pracovisko']")));
//            dropdown.findElement(By.xpath("//option[. = 'Pracovisko']")).click();
//        }
//        wait.until(ExpectedConditions.elementToBeClickable(By.id("ctl00_ContentPlaceHolderMain_ddlStredisko")));
//
//        //ulozenie atributov value stredisk FBERG do zoznamu
//        driver.findElement(By.id("ctl00_ContentPlaceHolderMain_ddlStredisko")).click();
//        List<WebElement> strediskaFBERG = driver.findElements(By.xpath("//*[@id=\"ctl00_ContentPlaceHolderMain_ddlStredisko\"]/option[position()>1]"));
//        List<String> strediskaFBERGValues = new ArrayList<String>();
//        for (WebElement webElement : strediskaFBERG) {
//            strediskaFBERGValues.add(webElement.getAttribute("value"));
//        }
//
//        //ulozenie atributov value stredisk LF do zoznamu
//        driver.findElement(By.id("ctl00_ContentPlaceHolderMain_ddlFakulta")).click();
//        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"ctl00_ContentPlaceHolderMain_ddlFakulta\"]/option[@value='09']")));
//        driver.findElement(By.xpath("//*[@id=\"ctl00_ContentPlaceHolderMain_ddlFakulta\"]/option[@value='09']")).click();
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        //wait.until(ExpectedConditions.elementToBeClickable(By.id("ctl00_ContentPlaceHolderMain_ddlStredisko")));
//        driver.findElement(By.id("ctl00_ContentPlaceHolderMain_ddlStredisko")).click();
//        List<WebElement> strediskaLF = driver.findElements(By.xpath("//*[@id=\"ctl00_ContentPlaceHolderMain_ddlStredisko\"]/option[position()>1]"));
//        List<String> strediskaLFValues = new ArrayList<String>();
//        for (WebElement webElement : strediskaLF) {
//            strediskaLFValues.add(webElement.getAttribute("value"));
//        }
//
//        driver.quit();

        //spustenie scrapera pre vsetky strediska
//        for (String stredisko : strediskaFBERGValues) {
//            PracoviskoScraper pracoviskoScraper = new PracoviskoScraper();
//            pracoviskoScraper.init("01", stredisko);
//            pracoviskoScraper.scrape();
//            pracoviskoScraper.getDriver().quit();
//            zaznamy.put(stredisko, pracoviskoScraper.getData());
//        }

//        for (String stredisko : strediskaLFValues) {
//            PracoviskoScraper pracoviskoScraper = new PracoviskoScraper();
//            pracoviskoScraper.init("09", stredisko);
//            pracoviskoScraper.scrape();
//            pracoviskoScraper.getDriver().quit();
//            zaznamy.put(stredisko, pracoviskoScraper.getData());
//        }

        PracoviskoScraper pracoviskoScraper = new PracoviskoScraper();
        pracoviskoScraper.init("01", "101001");
        pracoviskoScraper.scrape();

        Inserter.closeConnection();
    }
}
