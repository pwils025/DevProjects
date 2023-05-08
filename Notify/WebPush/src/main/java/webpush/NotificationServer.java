package webpush;

import static spark.Spark.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jose4j.lang.JoseException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;



public class NotificationServer {
    private static final String VAPID_PUBLIC_KEY = "BFLzIRdSZPx2JSojb9QN6f06dibyfmyjtreZhJR-uvhO6eMMskKl1BAn7agVrsk9Yycv-S2bAoc062gGrCMwgFw";
    private static final String VAPID_PRIVATE_KEY = "6XsRTr7T1AEbB0QGYWQLkA-09Kh9cvbkEEa6Je0qJjE";
    private static final List<Subscription> subscriptions = new ArrayList<>();
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        port(8080);
        get("/test", (req, res) -> {
            System.out.println("HELLO THERE");
            return "Thanks";
        });

        post("/subscribe", (req, res) -> {
            JsonObject subscription = gson.fromJson(req.body(), JsonObject.class);

            String endpoint = subscription.get("endpoint").getAsString();
            String auth = subscription.get("keys").getAsJsonObject().get("auth").getAsString();
            String key = subscription.get("keys").getAsJsonObject().get("p256dh").getAsString();
            Subscription sub = new Subscription(endpoint,new Subscription.Keys(key,auth));

            System.out.println("Subscription:" + subscription.toString());
            subscriptions.add(sub);
            return "Subscribed";
        });

        post("/send", (req, res) -> {
            String title = "Test notification";
            String message = req.queryParams("message");

            PushService pushService = new PushService();
            try {
                pushService.setPublicKey(VAPID_PUBLIC_KEY);
                pushService.setPrivateKey(VAPID_PRIVATE_KEY);
            }catch(NoSuchAlgorithmException nae){
                nae.printStackTrace();
            }catch(NoSuchProviderException pae){
                pae.printStackTrace();
            }catch(InvalidKeySpecException iae){
                iae.printStackTrace();
            }
            catch(Exception ex){
                ex.printStackTrace();
            }

            JsonObject payload = new JsonObject();
            payload.addProperty("title", title);
            payload.addProperty("body", message);

            for (Subscription sub : subscriptions) {
                try {
                    pushService.send(new Notification(sub, gson.toJson(payload)));
                } catch (GeneralSecurityException | IOException | JoseException e) {
                    e.printStackTrace();
                }
            }

            return "Notification sent";
        });

    }
}
