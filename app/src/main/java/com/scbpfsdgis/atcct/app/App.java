package com.scbpfsdgis.atcct.app;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.scbpfsdgis.atcct.data.DatabaseManager;
import com.scbpfsdgis.atcct.data.model.DBHelper;
import com.scbpfsdgis.atcct.data.repo.UpdateHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by William on 12/17/2018.
 */

public class App extends Application{
    private static Context context;
    private static DBHelper dbHelper;

    @Override
    public void onCreate()
    {
        super.onCreate();
        context = this.getApplicationContext();
        dbHelper = new DBHelper();
        DatabaseManager.initializeInstance(dbHelper);

        System.out.println("ATCC Database Created!");

    }

    public static Context getContext(){
        return context;
    }
}
