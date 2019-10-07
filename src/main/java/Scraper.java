import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.*;

import java.io.IOException;

public class Scraper {

    public static final String baseURL = "https://epc.lib.tuke.sk/PrehladPubl.aspx";

    public static void main(String[] args) {

        final WebClient client = new WebClient(BrowserVersion.CHROME);
//        client.setAjaxController(new NicelyResynchronizingAjaxController());

        try {
            HtmlPage page = client.getPage(baseURL);

            HtmlSelect select = (HtmlSelect) page.getElementById("ctl00_ContentPlaceHolderMain_ddlKrit1");
            HtmlOption option = select.getOptionByValue("Pracovisko");
            page = select.setSelectedAttribute(option, true);

            client.waitForBackgroundJavaScriptStartingBefore(1000);

//            HtmlCheckBoxInput chbOhlasy = (HtmlCheckBoxInput) page.getElementById("ctl00_ContentPlaceHolderMain_chbOhlasy");
//            page = (HtmlPage) chbOhlasy.setChecked(true);
//            HtmlCheckBoxInput chbAjPercentualnePodiely = (HtmlCheckBoxInput) page.getElementById("ctl00_ContentPlaceHolderMain_chbAjPercentualnePodiely");
//            page = (HtmlPage) chbAjPercentualnePodiely.setChecked(true);

            HtmlSubmitInput btnHladaj = (HtmlSubmitInput) page.getElementById("ctl00_ContentPlaceHolderMain_btnHladaj");
            page = btnHladaj.click();


            client.waitForBackgroundJavaScriptStartingBefore(1000);
//            client.waitForBackgroundJavaScript(10000);

//            WebResponse response = page.getWebResponse();
//            String content = response.getContentAsString();
//            System.out.println(content);

            for (int i = 0; i < 20; i++) {
                if (page.getElementById("ctl00_ContentPlaceHolderMain_gvVystupyByFilter_ctl02_lZNazov") != null) {
                    break;
                }
                synchronized (page) {
                    try {
                        page.wait(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (i == 19)
                    System.out.println("Time out.");
            }

            System.out.println(page.asXml());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
