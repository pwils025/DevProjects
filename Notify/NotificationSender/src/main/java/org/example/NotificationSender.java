package org.example;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotificationSender {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java NotificationSender <message>");
            System.exit(1);
        }

        String message = args[0];
        try {
            sendNotification(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendNotification(String message) throws IOException {
        URL url = new URL("http://localhost:8080/send");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String postData = "message=" + message;
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(postData.getBytes());
        }

        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);
        System.out.println("Response Message: " + connection.getResponseMessage());
    }
}
