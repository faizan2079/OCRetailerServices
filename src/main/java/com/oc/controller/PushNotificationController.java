/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.controller;

//import com.google.android.gcm.server.Message;
//import com.google.android.gcm.server.Result;
//import com.google.android.gcm.server.Sender;
import com.google.gson.Gson;
import com.oc.services.entity.Message;

import java.util.ArrayList;
import java.util.Objects;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.Message;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 *
 * @author DELL PRECISION M6800
 */
public class PushNotificationController {

    private String API_TOKEN = null;
    private String URL_FCM = null;

    public PushNotificationController(){
        
    }
            

    public PushNotificationController(String apitoken, String urlFCM) {
        API_TOKEN = apitoken;
        URL_FCM=urlFCM;
        
    }

    public String getAPI_TOKEN() {
        return API_TOKEN;
    }

    public void setAPI_TOKEN(String API_TOKEN) {
        this.API_TOKEN = API_TOKEN;
    }

    public Boolean sendNotification(Message message) {

        try {

            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(URL_FCM);
            post.setHeader("Content-type", "application/json");
            post.setHeader("Authorization", API_TOKEN);

            Gson gson = new Gson();
            String noti = gson.toJson(message, Message.class);

            post.setEntity(new StringEntity(noti, "UTF-8"));
            HttpResponse response = client.execute(post);

            return Objects.equals(response.getStatusLine().getReasonPhrase(), "OK");

        } catch (Exception e) {
            Logger.getLogger(PushNotificationController.class.getName()).log(Level.SEVERE, null, e);

        }

        return false;

    }

    public ArrayList<Message> sendBulkNotification(ArrayList<Message> lstMessages) {
        ArrayList<Message> lstFailedMessages = new ArrayList<>();
        try {
            for (Message message : lstMessages) {
                HttpClient client = HttpClientBuilder.create().build();
                HttpPost post = new HttpPost("https://fcm.googleapis.com/fcm/send");
                post.setHeader("Content-type", "application/json");
                post.setHeader("Authorization", API_TOKEN);

                Gson gson = new Gson();
                String noti = gson.toJson(message, Message.class);

                post.setEntity(new StringEntity(noti, "UTF-8"));
                HttpResponse response = client.execute(post);
                if (!Objects.equals(response.getStatusLine().getReasonPhrase(), "OK")) {
                    lstFailedMessages.add(message);
                }
            }

        } catch (Exception e) {
            Logger.getLogger(PushNotificationController.class.getName()).log(Level.SEVERE, null, e);

        }

        return lstFailedMessages;

    }

}
