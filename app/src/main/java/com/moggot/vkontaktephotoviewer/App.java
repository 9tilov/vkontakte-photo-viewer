package com.moggot.vkontaktephotoviewer;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

/**
 * Created by toor on 25.04.17.
 */

public class App extends Application {

    private static final String LOG_TAG = "App";

    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
//            Log.v(LOG_TAG, "access_token = " + newToken.accessToken);
//            Log.v(LOG_TAG, "userid = " + newToken.userId);
            if (newToken == null) {
                Toast.makeText(App.this, "AccessToken invalidated", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(App.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(this);
    }
}
