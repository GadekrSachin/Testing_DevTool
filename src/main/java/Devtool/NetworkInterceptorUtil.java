package Devtool;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v121.network.Network;
import org.openqa.selenium.devtools.v121.network.Network.GetResponseBodyResponse;
import org.openqa.selenium.devtools.v121.network.model.RequestId;
import org.openqa.selenium.devtools.v121.network.model.Response;
import org.openqa.selenium.chrome.ChromeDriver;

public class NetworkInterceptorUtil {

    private DevTools devTools;
    private AtomicReference<RequestId> requestIdRef = new AtomicReference<>();

    // ✅ Constructor to initialize DevTools session
    public NetworkInterceptorUtil(ChromeDriver driver) {
        this.devTools = driver.getDevTools();
        this.devTools.createSessionIfThereIsNotOne();
        this.devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        // Clear cache & cookies
        this.devTools.send(Network.clearBrowserCache());
        this.devTools.send(Network.clearBrowserCookies());
    }

    // ✅ Start Listening to Network Responses
    public void startListening(String targetApi) {
        requestIdRef.set(null);  // Reset request ID

        devTools.addListener(Network.responseReceived(), response -> {
            Response res = response.getResponse();
            if (res != null && res.getUrl().contains(targetApi)) {
                System.out.println("📌 API Response Received: " + res.getUrl());
                System.out.println("📌 Status Code: " + res.getStatus());
                System.out.println("📌 Response Headers: " + res.getHeaders());

                requestIdRef.set(response.getRequestId());  // Store requestId
            }
        });
    }

    // ✅ Capture API Response after an action
    public String getApiResponse() {
        if (requestIdRef.get() != null) {
            GetResponseBodyResponse responseBody = devTools.send(Network.getResponseBody(requestIdRef.get()));
            return responseBody.getBody();
        } else {
            System.out.println("❌ No request ID captured!");
            return null;
        }
    }
}
