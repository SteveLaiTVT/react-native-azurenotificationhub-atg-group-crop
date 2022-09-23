package com.azure.reactnative.notificationhub.listener;

import android.content.Context;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.microsoft.windowsazure.messaging.notificationhubs.NotificationListener;

import java.util.HashMap;
import java.util.Map;

public class CustomListener implements NotificationListener {
    private static final String TAG = "CustomNotification";

    @Override
    public void onPushNotificationReceived(Context context, RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        String title = "";
        String body = "";
        Map<String, String> data = new HashMap<>();
        if (notification != null) {
            title = notification.getTitle();
            body = notification.getBody();
            data = remoteMessage.getData();
        }

        Log.d(TAG, "Message Notification Title:" + title);
        Log.d(TAG, "Message Notification Body:" + body);

        for (Map.Entry<String, String> entry : data.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            Log.d(TAG, "Message Notification Data: " + key + " : " + value);
        }
    }
}
