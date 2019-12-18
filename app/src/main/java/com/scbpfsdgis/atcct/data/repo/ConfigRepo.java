package com.scbpfsdgis.atcct.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.scbpfsdgis.atcct.data.model.Config;
import com.scbpfsdgis.atcct.data.model.DBHelper;
import com.scbpfsdgis.atcct.data.model.FIR;

public class ConfigRepo {
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public ConfigRepo() {
        Config cfg = new Config();
    };

    public static String createCFG() {
        String query = "CREATE TABLE IF NOT EXISTS " + Config.TABLE_CFG + " (" +
                Config.COL_COOR_NAME + " TEXT, " +
                Config.COL_LOGIN_DATE + " TEXT, " +
                Config.COL_LOGOUT_DATE + " TEXT)";
        return query;
    }

    public void insert(Config cfg) {
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Config.COL_COOR_NAME, cfg.getCfgCoor());
//        values.put(Config.COL_LOGIN_DATE, cfg.getCfgLogin());
//        values.put(Config.COL_LOGOUT_DATE, "");

        // Inserting Row
        db.execSQL("DELETE FROM " + Config.TABLE_CFG);
        db.insert(Config.TABLE_CFG, null, values);
        db.close();
    }

    public String getCurCoor() {
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();
        String query = "SELECT " + Config.COL_COOR_NAME + " as CurCoor FROM " + Config.TABLE_CFG;

        Cursor cursor = db.rawQuery(query, null);
        String curCoor = "";

        if (cursor.moveToFirst()) {
            curCoor = cursor.getString(cursor.getColumnIndex("CurCoor"));
        }

        return curCoor;
    }


}
