import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

public class EPCTUKEScraper {
    private static ChromeOptions options;
    private static WebDriver driver;
    private static WebDriverWait wait;
    private static String poradiePredoslehoPrvehoZaznamu;

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver",
                "chromedriver.exe");
        options = new ChromeOptions();
//        options.addArguments("headless");
        options.addArguments("window-size=1920x1080");
        options.addArguments("disable-infobars");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 15);

        driver.get("https://epc.lib.tuke.sk/PrehladPubl.aspx");

        driver.findElement(By.id("ctl00_ContentPlaceHolderMain_Image1")).click();

        //01 = FBERG, 09 = LF
        loadPracovisko("01", "Celá fakulta");

        scrape();

        driver.quit();
    }

    private static void scrape(){
        poradiePredoslehoPrvehoZaznamu = driver.findElement(By.xpath("//*[@id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter\"]/tbody/tr[2]/td[1]/p")).getText();

        List<WebElement> zaznamyStrana = driver.findElements(By.xpath("//*[@id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter\"]/tbody/tr/td[5]"));
        for (WebElement webElement : zaznamyStrana) {
            System.out.println(webElement.getText());
        }

        List<List<WebElement>> zaznamyVsetky = new ArrayList<List<WebElement>>();
        zaznamyVsetky.add(zaznamyStrana);

        nextPage();
    }

    private static void nextPage() {
        int currentPage = Integer.parseInt(driver.findElement(By.xpath("//*[@id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter\"]/tbody/tr[last()]/td/table/tbody/tr/td/span")).getText());
        int nextPage = currentPage+1;

        try {
            WebElement nextPageElement = driver.findElement(By.xpath("//*[@id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter\"]/tbody/tr[last()]/td/table/tbody/tr/td/a[normalize-space(text())='" + nextPage + "']\n"));
            nextPageElement.click();
            String poradieDalsiehoPrvehoZaznamu = Integer.parseInt(poradiePredoslehoPrvehoZaznamu.substring(0, poradiePredoslehoPrvehoZaznamu.length() - 1)) + 30 +".";
            wait.until(ExpectedConditions.textToBe(By.xpath("//*[@id=\"ctl00_ContentPlaceHolderMain_gvVystupyByFilter\"]/tbody/tr[2]/td[1]/p"), poradieDalsiehoPrvehoZaznamu));

            System.out.println("Strana " + nextPage);
            scrape();
        } catch (NoSuchElementException e){
            System.err.println("Dalsia strana neexistuje.");
            e.printStackTrace();
        }
    }

    private static void loadPracovisko(String pracoviskoValue, String strediskoValue){
        WebDriverWait wait = new WebDriverWait(driver, 10);

        //Z dropdownu sa zvoli Pracovisko
        driver.findElement(By.id("ctl00_ContentPlaceHolderMain_ddlKrit1")).click();
        {
            WebElement dropdown = driver.findElement(By.id("ctl00_ContentPlaceHolderMain_ddlKrit1"));
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//option[. = 'Pracovisko']")));
            dropdown.findElement(By.xpath("//option[. = 'Pracovisko']")).click();

            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("ctl00_ContentPlaceHolderMain_ddlFakulta")));
            dropdown = driver.findElement(By.id("ctl00_ContentPlaceHolderMain_ddlFakulta"));
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"ctl00_ContentPlaceHolderMain_ddlFakulta\"]/option[@value='"+pracoviskoValue+"']")));
            dropdown.findElement(By.xpath("//*[@id=\"ctl00_ContentPlaceHolderMain_ddlFakulta\"]/option[@value='"+pracoviskoValue+"']")).click();

            dropdown = driver.findElement(By.id("ctl00_ContentPlaceHolderMain_ddlStredisko"));
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"ctl00_ContentPlaceHolderMain_ddlStredisko\"]/option[@value='"+strediskoValue+"']")));
            dropdown.findElement(By.xpath("//*[@id=\"ctl00_ContentPlaceHolderMain_ddlStredisko\"]/option[@value='"+strediskoValue+"']")).click();
        }

        checkCheckboxesAndSearch();

        //pocka sa, kym sa nacita tabulka so zaznamami
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("ctl00_ContentPlaceHolderMain_gvVystupyByFilter")));
    }

    private static void checkCheckboxesAndSearch(){
        WebDriverWait wait = new WebDriverWait(driver, 10);

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
