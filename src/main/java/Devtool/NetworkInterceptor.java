package Devtool;

 
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions; 

 


public class NetworkInterceptor {

    private static final By EMAIL_INPUT = By.xpath("//input[@inputmode='email']");
    private static final By PASSWORD_INPUT = By.xpath("//input[@name='password']");
    private static final By SIGN_IN_BUTTON = By.xpath("(//button[@type='submit'])[1]");
    private static final By ADD_BUTTON = By.xpath("//i[@class='ri-add-line']");
    private static final String TARGET_API = "retrieveServices?"; 

    
    public static void main(String[] args) throws InterruptedException {
        // Set up WebDriver
//        System.setProperty("webdriver.chrome.driver", "C:\\Users\\SHUBH\\Driver\\Testing browser\\chromedriver-122.exe");
//        System.setProperty("webdriver.chrome.driver", "C:\\Users\\SHUBH\\Driver\\Testing browser\\chromedriver-121.exe");
    	System.setProperty("webdriver.chrome.driver", "src/test/resources/Driver/chromedriver-121.exe");

    	
        ChromeOptions options = new ChromeOptions();
//        options.setBinary("C:\\Users\\SHUBH\\Driver\\Testing browser\\chrome-win64(121)\\chrome-win64\\chrome.exe");

//        user chrome browser 121 version
        options.setBinary("C:\\Users\\SHUBH\\Driver\\Testing browser\\chrome-win64(121)\\chrome-win64\\chrome.exe");

//        options.setBinary("C:\\Users\\SHUBH\\Driver\\Testing browser\\chrome-win64-122\\chrome.exe");
        WebDriver driver = new ChromeDriver(options);

        // Start DevTools session
         
        NetworkInterceptorUtil networkUtil = new NetworkInterceptorUtil(driver);

        // ✅ Open Website & Perform Login
        driver.get("https://go-manage-bo.web.app/");
        driver.findElement(EMAIL_INPUT).sendKeys("dev66.scriptus@gmail.com");
        driver.findElement(PASSWORD_INPUT).sendKeys("Test@1234");
        driver.findElement(SIGN_IN_BUTTON).click();
        Thread.sleep(8000); 
        
        networkUtil.startListening(TARGET_API);  
        
        System.out.println("🚀 Clicking Add Button...");
        
        driver.findElement(ADD_BUTTON).click();          
        Thread.sleep(5000);
        String jsonRequest = networkUtil.getLatestJsonRequest();
        String jsonResponse = networkUtil.getLatestJsonResponse();

        if (jsonRequest != null) {
            System.out.println("📌 Captured API Request Payload: " + jsonRequest);
        } else {
            System.out.println("❌ No API request payload captured!");
        }

        if (jsonResponse != null) {
            System.out.println("📌 API JSON Response: " + jsonResponse);
        } else {
            System.out.println("❌ No API response captured!");
        }

        driver.quit();
    }
}