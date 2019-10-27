package crepc;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebDriver;

import java.sql.*;


import java.util.ArrayList;
import java.util.Arrays;


public class CREPC1scraper {
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

    public CREPC1scraper(ResultSet resultset) {
        this.resultSet = resultset;
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        options = new ChromeOptions();
//        options.addArguments("headless");
        options.addArguments("window-size=1920x1080");
        options.addArguments("disable-infobars");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 15);
    }

    public void searchForRecordKeyword(String recordName, String isbn) {
        this.recordname = recordName;
        while (recordName.endsWith(" ")){
            recordName = recordname.substring(0, recordName.length()-1);
        }
        while (isbn.endsWith(" ")){
            isbn = isbn.substring(0,isbn.length()-1);
        }
        String recordNameForSearch;
        recordNameForSearch=recordName.replace("'"," ");
        recordNameForSearch=recordNameForSearch.replace("\""," ");
        try {


        driver.get("http://www.crepc.sk/portal?fn=SearchForm");
        driver.findElement(By.xpath("//*[@id=\"googleedit\"]")).clear();
        driver.findElement(By.xpath("//*[@id=\"googleedit\"]")).sendKeys(recordNameForSearch);
        driver.findElement(By.xpath("//*[@id=\"googleedit\"]")).sendKeys(Keys.RETURN);
        //wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"divChck_1\"]/div[3]/a")));
            if(driver.findElements(By.xpath("//*[contains(@id, 'divChck')]")).size()==0) {
                insertToDatabaseEmtpy();
            }

            if(driver.findElements(By.xpath("//*[contains(@id, 'divChck')]")).size()==1) {
            String pageRecordName = driver.findElement(By.xpath("//*[@id=\"divChck_1\"]/div[2]/b")).getText();
            while (pageRecordName.endsWith(" ")){
                recordName = recordname.substring(0, recordName.length()-1);
            }
            if (pageRecordName.equalsIgnoreCase(recordName)) {
                driver.findElement(By.xpath("//*[@id=\"divChck_1\"]/div[3]/a")).click();
                //*[@id="divChck_1"]/div[3]/a
                //wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"divChck_1\"]/div[3]/a")));
                keyWordsFull = driver.findElement(By.xpath("//*[@id=\"wrap\"]/div/div[7]/label")).getText();
                System.out.println(keyWordsFull);
                parseKeywords(keyWordsFull);
                insertToDatabase();

            }


        }
        //*[@id="divChck_2"]/div[2]/text()[2]
        if(driver.findElements(By.xpath("//*[contains(@id, 'divChck')]")).size()>1) {
            String xpath = "//*[contains('" + isbn + "')]";
            if (driver.findElements(By.xpath("//div[@class='mid'][contains(.,'" + isbn + "') ]")).size() > 0) {
                if(driver.findElements(By.xpath("//div[@class='mid' and contains(.,'" + isbn + "') and contains (.,\"" + recordName + "\")]")).size()>0) {
                    if (driver.findElement(By.xpath("//div[@class='mid' and contains(.,'" + isbn + "')and contains(.,\"" + recordName + "\")]")).getText().contains(isbn)) {
                        WebElement childElement = driver.findElement(By.xpath("//div[@class='mid' and contains(.,'" + isbn + "')and contains(.,\"" + recordName + "\")]"));
                        WebElement parent = (WebElement) ((JavascriptExecutor) driver)
                                .executeScript("return arguments[0].parentNode;", childElement);
                        String numberOfRecord = parent.getText().substring(0, parent.getText().indexOf("\n"));
                        driver.findElement(By.xpath("//*[@id=\"divChck_" + numberOfRecord + "\"]/div[3]/a")).click();
                        //*[@id="divChck_1"]/div[3]/a
                        //wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"divChck_1\"]/div[3]/a")));
                        keyWordsFull = driver.findElement(By.xpath("//*[@id=\"wrap\"]/div/div[7]/label")).getText();
                        System.out.println(keyWordsFull);
                        parseKeywords(keyWordsFull);
                        insertToDatabase();

                    }
                }


            }
        }


            }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<String> parseKeywords(String keywords) {
        String tok [];
        if (keywords.contains("\n")) {
            tok = keywords.split("\n", 20);
        }
        else {
            tok = keywords.split("  ", 20);
        }
        wordList = new ArrayList<String>(tok.length);
        wordList.addAll(Arrays.asList(tok));
        wordList.remove(0);
        if (keywords.contains("- ")) {
            for (String e : wordList) {
                String substring = e.substring(e.startsWith("-") ? 2 : 0, e.contains("\n") ? e.lastIndexOf("\n") : e.length());
                wordList.set(wordList.indexOf(e), substring);
                System.out.println(substring);
            }
        }
        else {
            for (String e : wordList) {
                String substring = e.substring(e.startsWith(" ") ? 1 : 0, e.contains(",") ? e.lastIndexOf(",") : e.length());
                wordList.set(wordList.indexOf(e), substring);
                System.out.println(substring);
            }
        }
        return wordList;

    }

    public void insertToDatabase() {
        try {
            ResultSet resultset = databaseConnection();
            while (resultset.next()) {
                if (resultset.getString("nazov").equalsIgnoreCase(recordname)|| resultset.getString("nazov").equalsIgnoreCase(recordname + " ")){
                    StringBuilder sb = new StringBuilder();
                    for (String s : wordList)
                    {
                        sb.append(s);
                        sb.append(", ");
                    }
                    dieloId = resultset.getInt("dielo_id");
                    String query = "insert into diela.klucove_slova values (?) where dielo_id = " + dieloId;
                    String sql = "UPDATE diela " +
                            "SET klucove_slova = " + "'" + sb.toString() + "'" + " WHERE dielo_id=" + dieloId;
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
    public void insertToDatabaseEmtpy() {
        try {
            ResultSet resultset = databaseConnection();
            while (resultset.next()) {
                if (resultset.getString("nazov").equalsIgnoreCase(recordname)|| resultset.getString("nazov").equalsIgnoreCase(recordname + " ")){

                    dieloId = resultset.getInt("dielo_id");
                    String query = "insert into diela.klucove_slova values (?) where dielo_id = " + dieloId;
                    String sql = "UPDATE diela " +
                            "SET klucove_slova = " + "'" +"---" + "'" + " WHERE dielo_id=" + dieloId;
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
            return stat.executeQuery("SELECT * FROM diela");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }


    //TODO select diela z databazy
    //  pozriet na rok vydania a podla toho vybrat na ktorej verzii CREPCu hladat
    //  najst dielo - hlaska ked sa nenasli ziadne vysledky alebo ked sa naslo viac vysledkov
    //  vybrat klucove slova a pracoviska a pozicie autorov podla datumu
}
