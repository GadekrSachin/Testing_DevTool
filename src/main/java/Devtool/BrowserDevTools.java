package Devtool;
import org.openqa.selenium.bidi.log.LogEntry;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
 
public class BrowserDevTools {

	

 
	    public static void main(String[] args) {
	        // Set up FirefoxOptions
	        FirefoxOptions options = new FirefoxOptions();
 	        options.addPreference("devtools.debugger.remote-enabled", true);
	        options.addPreference("devtools.chrome.enabled", true);
	        options.addPreference("browser.tabs.remote.autostart", false);
	        options.addPreference("remote.active", true);
	        options.addPreference("network.http.use-cache", false);

	        // Launch Firefox with debugging enabled
	        FirefoxDriver driver = new FirefoxDriver(options);
	        driver.get("https://www.mozilla.org");	
	          System.out.println("data ");
	        driver.quit();
	    }
	}

 