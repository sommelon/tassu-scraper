package crepc;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CREPC2scraper {
    private ChromeOptions options;
    private WebDriver driver;
    private WebDriverWait wait;
    private ResultSet resultSet;
    private String recordname;
    private static final String url = "jdbc:mysql://localhost:3306/tssu?useLegacyDatetimeCode=false&serverTimezone=UTC";
    private static final String user = "root";
    private static final String pass = "root";
    private static Connection con = null;
    private int dieloId;
    private String keyWordsFull;
    private ArrayList<String> wordList;
    public CREPC2scraper(){
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        options = new ChromeOptions();
//        options.addArguments("headless");
        options.addArguments("window-size=1920x1080");
        options.addArguments("disable-infobars");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 15);
    }
    public void searchForRecordKeyword(String recordName, String isbn){
        this.recordname = recordName;
        if(recordName.endsWith(" ")){
            recordName = recordname.substring(0, recordName.length()-1);
        }
        driver.get("https://app.crepc.sk/?fn=AdvancedSearchChildO6ST&search=advanced&entity=0&seo=CREP%C4%8C-H%C4%BEadanie");
//        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"B7T\"]")));

        driver.findElement(By.xpath("//input[@placeholder='Zadajte text pre hľadanie...']")).clear();
        driver.findElement(By.xpath("//input[@placeholder='Zadajte text pre hľadanie...']")).sendKeys(recordName);
        driver.findElement(By.xpath("//input[@placeholder='Zadajte text pre hľadanie...']")).sendKeys(Keys.RETURN);
        ArrayList<String> elementsString = new ArrayList<String>();
        WebDriverWait wait = new WebDriverWait(driver, 10);
        List<WebElement> ord = null;
        int elementsSize = 0;
        if(driver.findElements(By.xpath("//a[@class='btn btn-default text-wrap']")).size()>0) {
            ord = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//a[@class='btn btn-default text-wrap']")));
            elementsSize = ord.size();
            //wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@class='btn btn-default text-wrap']")));
        }
        for(int i = 0; i < elementsSize; i++){
    elementsString.add(driver.findElements(By.xpath("//a[@class='btn btn-default text-wrap']")).get(i).getText());
}
        StringBuilder sb = new StringBuilder();
        for(String e: elementsString){
            sb.append(e +", ");
        }
        if(sb.toString().endsWith(",")){
            sb.substring(0,sb.length()-1);
        }

        insertToDatabase(sb.toString());

    }



    public void insertToDatabase(String keywords) {
        try {
            System.out.println("Keywords: " + keywords);
            ResultSet resultset = databaseConnection();
            while (resultset.next()) {
                if (resultset.getString("nazov").equalsIgnoreCase(recordname + " ")) {
//                    StringBuilder sb = new StringBuilder();
//                    for (String s : wordList)
//                    {
//                        sb.append(s);
//                        sb.append(", ");
//                    }
                    dieloId = resultset.getInt("zaznam_id");
                    String query = "insert into dielo.klucove_slova values (?) where zaznam_id = " + dieloId;
                    String sql = "UPDATE dielo " +
                            "SET klucove_slova = " + "'" + keywords + "'" + " WHERE zaznam_id=" + dieloId;
                    try {
                        try {
                            con = DriverManager.getConnection(url, user, pass);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        PreparedStatement ps = con.prepareStatement(sql);
                        //       ps.setString(1, keyWordsFull);

                        ps.executeUpdate();
                        ps.close();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static ResultSet databaseConnection() {
        Connection con = null;


        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String uName = "root";
            String uPass = "root";
            String host = "jdbc:mysql://localhost:3306/tssu?autoReconnect=true&useSSL=false&serverTimeZone=Europe/Berlin";
            con = DriverManager.getConnection(host, uName, uPass);

            Statement stat = con.createStatement();
            return stat.executeQuery("SELECT * FROM dielo");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }

}
