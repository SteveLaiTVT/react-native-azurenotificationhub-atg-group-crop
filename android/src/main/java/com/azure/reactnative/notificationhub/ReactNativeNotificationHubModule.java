package com.azure.reactnative.notificationhub;

import static com.azure.reactnative.notificationhub.ReactNativeConstants.*;
import static com.azure.reactnative.notificationhub.ReactNativeUtil.convertBundleToMap;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import com.azure.reactnative.notificationhub.listener.CustomListener;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.microsoft.windowsazure.messaging.notificationhubs.NotificationListener;
import com.microsoft.windowsazure.messaging.notificationhubs.NotificationHub;

public class ReactNativeNotificationHubModule extends ReactContextBaseJavaModule {

    private final NotificationListener notificationListener;
    private final Application application;

    public ReactNativeNotificationHubModule(ReactApplicationContext reactContext) {
        super(reactContext);
        application = (Application)reactContext.getApplication();
        notificationListener = new CustomListener();
    }


    @Override
    public String getName() {
        return AZURE_NOTIFICATION_HUB_NAME;
    }

    @ReactMethod
    public void getInitialNotification(Promise promise) {
        Activity activity = getCurrentActivity();
        if (activity == null) {
            promise.reject(ERROR_GET_INIT_NOTIFICATION, ERROR_ACTIVITY_IS_NULL);
            return;
        }
        Intent intent = activity.getIntent();
        if (intent != null && intent.getAction() != null) {
            if (intent.getExtras() == null) {
                // In certain cases while app cold launches, i.getExtras() returns null.
                // Adding the check to make sure app won't crash,
                // and still successfully launches from notification
                promise.reject(ERROR_GET_INIT_NOTIFICATION, ERROR_INTENT_EXTRAS_IS_NULL);
            } else {
                promise.resolve(convertBundleToMap(intent.getExtras()));
            }
        } else {
            promise.reject(ERROR_GET_INIT_NOTIFICATION, ERROR_ACTIVITY_INTENT_IS_NULL);
        }
    }

    @ReactMethod
    public void register(ReadableMap config, Promise promise) {
        ReactNativeNotificationHubUtil notificationHubUtil = ReactNativeNotificationHubUtil.getInstance();
        String connectionString = config.getString(KEY_REGISTRATION_CONNECTIONSTRING);
        if (connectionString == null) {
            promise.reject(ERROR_INVALID_ARGUMENTS, ERROR_INVALID_CONNECTION_STRING);
            return;
        }

        String hubName = config.getString(KEY_REGISTRATION_HUBNAME);
        if (hubName == null) {
            promise.reject(ERROR_INVALID_ARGUMENTS, ERROR_INVALID_HUBNAME);
            return;
        }

        String senderID = config.getString(KEY_REGISTRATION_SENDERID);
        if (senderID == null) {
            promise.reject(ERROR_INVALID_ARGUMENTS, ERROR_INVALID_SENDER_ID);
            return;
        }

        if (application != null) {
            NotificationHub.setListener(notificationListener);
            NotificationHub.start(application, hubName, connectionString);
        }

        WritableMap res = Arguments.createMap();
        res.putString(KEY_PROMISE_RESOLVE_UUID, NotificationHub.getPushChannel());
        promise.resolve(res);
    }

    public void unregister(Promise promise) {
        promise.resolve(null);
    }

    @ReactMethod
    public void getUUID(Promise promise) {
        String uuid = NotificationHub.getPushChannel();
        if (uuid != null) {
            promise.resolve(uuid);
        }  else {
            promise.reject(ERROR_GET_UUID, ERROR_NO_UUID_SET);
        }
    }
}
