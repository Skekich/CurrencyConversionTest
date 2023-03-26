import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import org.json.JSONObject;
public class ConversionTest {
    private WebDriver driver;

    @BeforeClass
    public void setUp(){
        try {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
            driver.manage().window().maximize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public void tearDown(){
        try {
            driver.quit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void postRequestTest() {
        String url = "https://www.rba.hr/alati/tecajni-kalkulator?p_p_id=tecajKalkulator_WAR_calculatorsportlet&p_p_lifecycle=2&p_p_state=normal&p_p_mode=view&p_p_resource_id=calculateExchangeRate&p_p_cacheability=cacheLevelPage&p_p_col_id=column-4&p_p_col_count=2";
        String requestBody = "source=FIRST" +
                "&currency1Id=840" +
                "&currency1Ammount=40" +
                "&currency2Id=826" +
                "&type=0";

        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // Set the request method and headers
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Send the request body
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(requestBody);
            wr.flush();
            wr.close();

            // Read the response body
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder responseBody = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                responseBody.append(inputLine);
            }
            in.close();

            // Parse the response JSON
            JSONObject jsonObject = new JSONObject(responseBody.toString());
            JSONObject formObject = jsonObject.getJSONObject("form");
            double jsonValue = formObject.getDouble("currency2Ammount");

            double rate = formObject.getDouble("exchangeRate");
            double amount = formObject.getDouble("currency1Ammount");

            System.out.println("kupnja GBP: tečaj je 1 USD = " + RoundNumber(formObject.getDouble("exchangeRate"), 2) + " GBP, za " + amount + " USD dobijem " + RoundNumber(jsonValue, 2) + " GBP");

            Assert.assertEquals(jsonValue, RoundNumber(ExpectedValue(rate, amount), 5));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Helpers
    private double ExpectedValue(double amount, double rate) {
        return amount * rate;
    }

    private double RoundNumber(double currentNumber, int roundFactor){
        BigDecimal bd = new BigDecimal(currentNumber);
        bd = bd.setScale(roundFactor, RoundingMode.DOWN);
        return bd.doubleValue();
    }
}
