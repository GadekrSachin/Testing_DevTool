package Devtool;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v121.network.Network;
import org.openqa.selenium.devtools.v121.network.Network.GetResponseBodyResponse;
import org.openqa.selenium.devtools.v121.network.model.RequestId;
import org.openqa.selenium.devtools.v121.network.model.Response; 

 


public class NetworkInterceptor {

    private static final By EMAIL_INPUT = By.xpath("//input[@inputmode='email']");
    private static final By PASSWORD_INPUT = By.xpath("//input[@name='password']");
    private static final By SIGN_IN_BUTTON = By.xpath("(//button[@type='submit'])[1]");
    private static final By ADD_BUTTON = By.xpath("//i[@class='ri-add-line']");
    private static final String TARGET_API = "retrieveStaff"; // The API endpoint you want to capture

    public static void main(String[] args) throws InterruptedException {
        // Set up WebDriver
//        System.setProperty("webdriver.chrome.driver", "C:\\Users\\SHUBH\\Driver\\Testing browser\\chromedriver-122.exe");
//        System.setProperty("webdriver.chrome.driver", "C:\\Users\\SHUBH\\Driver\\Testing browser\\chromedriver-121.exe");
    	System.setProperty("webdriver.chrome.driver", "src/test/resources/Driver/chromedriver-121.exe");

    	
        ChromeOptions options = new ChromeOptions();
//        options.setBinary("C:\\Users\\SHUBH\\Driver\\Testing browser\\chrome-win64(121)\\chrome-win64\\chrome.exe");

//        user chrome browser 121 version
        options.setBinary("src/test/resources/Driver/chrome-win64/chrome.exe");

//        options.setBinary("C:\\Users\\SHUBH\\Driver\\Testing browser\\chrome-win64-122\\chrome.exe");
        WebDriver driver = new ChromeDriver(options);

        // Start DevTools session
        DevTools devTools = ((ChromeDriver) driver).getDevTools();
        devTools.createSessionIfThereIsNotOne();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
 
        devTools.send(Network.clearBrowserCache());
        devTools.send(Network.clearBrowserCookies());

         AtomicReference<RequestId> requestIdRef = new AtomicReference<>();

         devTools.addListener(Network.responseReceived(), response -> {
            Response res = response.getResponse(); 
        	System.out.println("📌 Raw JSON Response: " + res);

            
            if (res != null) {
                String responseUrl = res.getUrl();
                
                if (responseUrl.contains(TARGET_API)) {  // ✅ Capture only 'retrieveStaff' API
                    System.out.println("📌 API Response Received: " + responseUrl);
                    System.out.println("📌 Status Code: " + res.getStatus());
                    System.out.println("📌 Response Headers: " + res.getHeaders());

                    requestIdRef.set(response.getRequestId());
                }
            }
        });
        

        // ✅ Open Website & Perform Login
        driver.get("https://go-manage-bo.web.app/");
        driver.findElement(EMAIL_INPUT).sendKeys("dev66.scriptus@gmail.com");
        driver.findElement(PASSWORD_INPUT).sendKeys("Test@1234");
        driver.findElement(SIGN_IN_BUTTON).click();
        Thread.sleep(5000); // Wait for login to complete

        // ✅ Click ADD button to trigger API call
        System.out.println("🚀 Clicking Add Button...");
        driver.findElement(ADD_BUTTON).click();
        if (requestIdRef.get() != null) {
            GetResponseBodyResponse responseBody = devTools.send(Network.getResponseBody(requestIdRef.get()));
            String jsonResponse = responseBody.getBody();

            // Print raw JSON response
            System.out.println("📌 Raw JSON Response: " + jsonResponse);

            // Parse JSON using Gson
//            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
//            System.out.println("✅ Parsed JSON Response: " + jsonObject);
//		
//            
        } else {
            System.out.println("❌ No request ID captured!");
        }
        // ✅ Wait for response after clicking the button
        Thread.sleep(5000);

        driver.quit();
    }
}
