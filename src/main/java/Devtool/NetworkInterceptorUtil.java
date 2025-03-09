package Devtool;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v121.network.Network;
import org.openqa.selenium.devtools.v121.network.Network.GetResponseBodyResponse;
import org.openqa.selenium.devtools.v121.network.model.Request;
import org.openqa.selenium.devtools.v121.network.model.RequestId;
import org.openqa.selenium.devtools.v121.network.model.Response;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class NetworkInterceptorUtil {

    private DevTools devTools;
    private AtomicReference<RequestId> requestIdRef = new AtomicReference<>();
    private String latestJsonResponse;
    private String latestJsonRequest; // ✅ Store latest request payload

    public NetworkInterceptorUtil(WebDriver driver) {
        this.devTools = ((ChromeDriver) driver).getDevTools();
        this.devTools.createSessionIfThereIsNotOne();
        this.devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
    }

    public void startListening(String targetApi) {
        requestIdRef.set(null);
        latestJsonResponse = null;
        latestJsonRequest = null;

        // ✅ Capture Request Payload
        devTools.addListener(Network.requestWillBeSent(), request -> {
            Request req = request.getRequest();
            if (req.getUrl().toLowerCase().contains(targetApi.toLowerCase())) {
                System.out.println("📌 API Request Sent: " + req.getUrl());
                System.out.println("📌 Request Headers: " + req.getHeaders());
                System.out.println("📌 Request Method: " + req.getMethod());
                if (req.getPostData().isPresent()) {
                    latestJsonRequest = req.getPostData().get();  
                    System.out.println("📌 Request Payload: " + latestJsonRequest);
                }
            }
        });

        // ✅ Capture Response Data
        devTools.addListener(Network.responseReceived(), response -> {
            Response res = response.getResponse();
            if (res.getUrl().toLowerCase().contains(targetApi.toLowerCase())) {
                System.out.println("📌 API Response Received: " + res.getUrl());
                System.out.println("📌 Status Code: " + res.getStatus());
                System.out.println("📌 Response Headers: " + res.getHeaders());
                requestIdRef.set(response.getRequestId());   
            }
        });
    }

    public String getApiResponse() {
        if (requestIdRef.get() != null) {
            GetResponseBodyResponse responseBody = devTools.send(Network.getResponseBody(requestIdRef.get()));
            latestJsonResponse = responseBody.getBody();
            return latestJsonResponse;
        } else {
            System.out.println("❌ No response captured!");
            return null;
        }
    }

    public String getLatestJsonResponse() {
        return latestJsonResponse;
    }

    public String getLatestJsonRequest() {
        return latestJsonRequest;
    }
}
