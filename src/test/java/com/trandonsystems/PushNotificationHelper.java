package com.trandonsystems;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.trandonsystems.britebin.services.SmsServices;

public class PushNotificationHelper {
	
    public final static String FCM_AUTH_KEY = "AAAAD9UvohM:APA91bGppLoHeyMTywSHcVFVevBoX7inAshjECAH723w-rvRCKKBwGHFwa88AgxmqtvZBTylrhhweMYVFxsG1kX0avmtrv9Io8xoVP16gdECWL_K2FxVOLj3-CL6A10NfvEFvTPdX67A";
    public final static String FCM_API_URL = "https://fcm.googleapis.com/fcm/send";
	
	static Logger log = Logger.getLogger(SmsServices.class);	
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String sendPushNotification(String deviceToken) throws IOException {
        String result = "";
        URL url = new URL(FCM_AUTH_KEY);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "key=" + FCM_API_URL);
        conn.setRequestProperty("Content-Type", "application/json");

        JsonObject json = new JsonObject();

        json.addProperty("to", deviceToken.trim());
        JsonObject info = new JsonObject();
        info.addProperty("title", "BriteBin"); // Notification title
        info.addProperty("body", "Hello, from BriteBin"); // Notification body
        json.add("notification", info);
        
        System.out.println("Json Object: " + gson.toJson(json));
        
        try {
            OutputStreamWriter wr = new OutputStreamWriter(
                    conn.getOutputStream());
            wr.write(json.toString());
            wr.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }
            result = "SUCCESS";

            System.out.println("GCM Notification is sent successfully");
        } catch (Exception e) {
            e.printStackTrace();
            result = "FAILURE";
        }

        return result;
    }
    
//    public static Response pushNotificationHttp(String deviceToken) {
//    	try {
//    	} catch (Exception ex) {
//    		
//    	}
//    }
}
