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

        /*final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

        Map<String,Object> defaultValue = new HashMap<>();
        defaultValue.put(UpdateHelper.KEY_UPDATE_ENABLE, false);
        defaultValue.put(UpdateHelper.KEY_UPDATE_URL, "https://drive.google.com/open?id=1JoEzGtFptQg86qa8fuyA_Nje5fiKUOHX");
        defaultValue.put(UpdateHelper.KEY_UPDATE_VERSION, "2.1.0");

        remoteConfig.setDefaults(defaultValue);
        remoteConfig.fetch(5)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            remoteConfig.activateFetched();
                        }
                    }
                });*/

        context = this.getApplicationContext();
        dbHelper = new DBHelper();
        DatabaseManager.initializeInstance(dbHelper);
        System.out.println("FC Tools Database Created!");
    }

    public static Context getContext(){
        return context;
    }
}
