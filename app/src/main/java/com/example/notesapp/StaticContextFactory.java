package com.example.notesapp;

import android.app.Application;
import android.content.Context;

public class StaticContextFactory extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        StaticContextFactory.context = getApplicationContext();
    }

    public static Context getAppContext(){
        return StaticContextFactory.context;
    }
}
