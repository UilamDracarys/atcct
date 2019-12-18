package com.scbpfsdgis.atcct.app;

import android.app.Application;
import android.content.Context;

import com.scbpfsdgis.atcct.data.DatabaseManager;
import com.scbpfsdgis.atcct.data.model.DBHelper;

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
        System.out.println("FC Tools Database Created!");
    }

    public static Context getContext(){
        return context;
    }
}
