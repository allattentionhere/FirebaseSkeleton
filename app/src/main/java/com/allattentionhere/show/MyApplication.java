package com.allattentionhere.show;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

/**
 * Created by krupenghetiya on 10/02/17.
 */

public class MyApplication extends Application {

    private static FirebaseDatabase firebaseDatabase;
    private static Gson mGson;


    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        mGson=new Gson();
    }


    public static FirebaseDatabase getFirebaseDatabase() {
        if (firebaseDatabase != null) {
            return firebaseDatabase;
        } else {
            firebaseDatabase = FirebaseDatabase.getInstance();
            return firebaseDatabase;
        }
    }

    public static Gson getGson() {
        return mGson;
    }
}
