package com.scbpfsdgis.atcct;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.scbpfsdgis.atcct.data.model.DBHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState);
        setContentView(R.layout.activity_main);
        DBHelper dbHelper = new DBHelper();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
    }

}
