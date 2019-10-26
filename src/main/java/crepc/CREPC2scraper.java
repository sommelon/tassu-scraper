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
import java.util.concurrent.TimeUnit;

public class CREPC2scraper {
    private ChromeOptions options;
    private WebDriver driver;
    private WebDriverWait wait;
    private ResultSet resultSet;
    private String recordname;
    private String recordNameForSearch;
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
        try {
            con = DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void searchForRecordKeyword(String recordName, String isbn){
        this.recordname = recordName;
        while (recordName.endsWith(" ")){
            recordName = recordname.substring(0, recordName.length()-1);
        }
        while (isbn.endsWith(" ")){
            isbn = isbn.substring(0,isbn.length()-1);
        }
        recordNameForSearch=recordName.replace("'"," ");
        recordNameForSearch=recordNameForSearch.replace("\""," ");
        try {


            driver.get("https://app.crepc.sk/?fn=AdvancedSearchChildO6ST&search=advanced&entity=0&seo=CREP%C4%8C-H%C4%BEadanie");
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='Zadajte text pre hľadanie...']")));
            //recordname = recordName.replace("'", " ");
            driver.findElement(By.xpath("//input[@placeholder='Zadajte text pre hľadanie...']")).clear();
            driver.findElement(By.xpath("//input[@placeholder='Zadajte text pre hľadanie...']")).sendKeys(recordNameForSearch);
            driver.findElement(By.xpath("//input[@placeholder='Zadajte text pre hľadanie...']")).sendKeys(Keys.RETURN);
            ArrayList<String> elementsString = new ArrayList<String>();
            WebDriverWait wait = new WebDriverWait(driver, 10);
            List<WebElement> ord = null;
            int elementsSize = 0;
            driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);
            //driver.findElements(By.xpath("//div[@class='col-sm-12'][contains(.,'" + isbn + "')][contains(.,'" + recordName + "')][contains(.,'Kľúčové slová')]")).get(0).findElements(By.xpath("//a[@class='btn btn-default text-wrap']")).get(i).getText()
            if (isbn.length() > 0) {
                isbn = isbn.substring(isbn.lastIndexOf("N") + 2, isbn.length() - 1);
                while (isbn.endsWith(" ")) {
                    isbn = isbn.substring(0, isbn.length() - 1);
                }
            }

            if (driver.findElements(By.xpath("//div[@class='col-sm-12'][contains(.,'" + isbn + "')][contains(.,\"" + recordName + "\")][contains(.,'Kľúčové slová')]")).size() > 0) {
                if (wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//a[@class='btn btn-default text-wrap']"))).size() > 0) {
                    // ord = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='col-sm-12'][contains(.,'" + isbn + "')][contains(.,'" + recordName + "')][contains(.,'Kľúčové slová')]")));
                    //elementsSize = driver.findElements(By.xpath("//div[@class='col-sm-12'][contains(.,'" + isbn + "')][contains(.,\"" + recordName + "\")][contains(.,'Kľúčové slová')]")).get(0).findElements(By.xpath("//a[@class='btn btn-default text-wrap']")).size();
                    elementsSize = driver.findElements(By.xpath("//div[@class='col-sm-12'][contains(.,'" + isbn + "')][contains(.,\"" + recordName + "\")][contains(.,'Kľúčové slová')]")).get(0).findElements(By.cssSelector(".btn.btn-default.text-wrap")).size();
                    //wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@class='btn btn-default text-wrap']")));
                }
                for (int i = 0; i < elementsSize; i++) {
                    elementsString.add(driver.findElements(By.xpath("//div[@class='col-sm-12'][contains(.,'" + isbn + "')][contains(.,\"" + recordName + "\")][contains(.,'Kľúčové slová')]")).get(0).findElements(By.cssSelector(".btn.btn-default.text-wrap")).get(i).getText());
                }
                StringBuilder sb = new StringBuilder();
                for (String e : elementsString) {
                    sb.append(e + ", ");
                }

      /*  if(driver.findElements(By.xpath("//a[@class='btn btn-default text-wrap']div[@class='col-sm-12'][contains(.,'" + isbn + "')]")).size()>0) {driver.findElements(By.xpath("//div[@class='col-sm-12'][contains(.,'" + isbn + "')][contains(.,\"" + recordName +  "\")][contains(.,'Kľúčové slová')]")).get(0).findElements(By.cssSelector(".btn.btn-default.text-wrap"))
            if (wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//a[@class='btn btn-default text-wrap']"))).size() > 0) {
                ord = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//a[@class='btn btn-default text-wrap']")));
                elementsSize = ord.size();
                //wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@class='btn btn-default text-wrap']")));
            }
            for (int i = 0; i < elementsSize; i++) {
                elementsString.add(wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//a[@class='btn btn-default text-wrap']"))).get(i).getText());
            }
            StringBuilder sb = new StringBuilder();
            for (String e : elementsString) {
                sb.append(e + ", ");
            }

      */



       /* if(sb.toString().endsWith(",")){
            sb.substring(0,sb.length()-1);
        }*/

                // driver.findElements(By.xpath("//[@class='col-sm-12'][contains(.,'" + isbn + "')]"));

                insertToDatabase(sb.toString());
            }
            if (driver.findElements(By.xpath("//div[@class='col-sm-12'][contains(.,'" + isbn + "')][contains(.,\"" + recordName + "\")][contains(.,'Kľúčové slová')]")).size() > 0) {
            insertToDatabaseEmtpy();
            }
            }
        catch (Exception e){
            e.printStackTrace();
        }

    }



    public void insertToDatabase(String keywords) {
        try {
            System.out.println("Keywords: " + keywords);
            ResultSet resultset = databaseConnection();
            while (resultset.next()) {
                if (resultset.getString("nazov").equalsIgnoreCase(recordname)|| resultset.getString("nazov").equalsIgnoreCase(recordname + " ")){
//                    StringBuilder sb = new StringBuilder();
//                    for (String s : wordList)
//                    {
//                        sb.append(s);
//                        sb.append(", ");
//                    }
                    dieloId = resultset.getInt("zaznam_id");
                    String query = "insert into dielo.klucove_slova values (?) where zaznam_id = " + dieloId;
                    String sql = "UPDATE dielo SET klucove_slova = ? WHERE zaznam_id= ?";

                    try {

                        PreparedStatement ps = con.prepareStatement(sql);
                              ps.setString(1, keywords);
                              ps.setInt(2, dieloId);

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
    public void insertToDatabaseEmtpy() {
        try {
            ResultSet resultset = databaseConnection();
            while (resultset.next()) {
                if (resultset.getString("nazov").equalsIgnoreCase(recordname)|| resultset.getString("nazov").equalsIgnoreCase(recordname + " ")){

                    dieloId = resultset.getInt("zaznam_id");
                    String query = "insert into dielo.klucove_slova values (?) where zaznam_id = " + dieloId;
                    String sql = "UPDATE dielo " +
                            "SET klucove_slova = " + "'" +"---" + "'" + " WHERE zaznam_id=" + dieloId;
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
                        break;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
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
