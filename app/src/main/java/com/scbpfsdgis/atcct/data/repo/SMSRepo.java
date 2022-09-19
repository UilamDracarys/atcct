package com.scbpfsdgis.atcct.data.repo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.scbpfsdgis.atcct.data.model.DBHelper;
import com.scbpfsdgis.atcct.data.model.FIR;
import com.scbpfsdgis.atcct.data.model.SMS;

public class SMSRepo {

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public SMSRepo() {
        SMS sms = new SMS();
    };

    public static String createSMSTbl() {
        String query = "CREATE TABLE IF NOT EXISTS " + SMS.TABLE_SMS + " (" +
                SMS.COL_ID + " TEXT," +
                SMS.COL_DATE + " TEXT, " +
                SMS.COL_FARM + " TEXT, " +
                SMS.COL_FIELD + " TEXT, " +
                SMS.COL_TOTAL_AREA + " INTEGER, " +
                SMS.COL_STATUS + " TEXT, " +
                SMS.COL_AREA + " INTEGER, " +
                SMS.COL_CLUSTER + " TEXT, " +
                SMS.COL_RMKS + " TEXT)";
        return query;
    }

    public void insert(SMS sms) {
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SMS.COL_ID, sms.getSmsID());
        values.put(SMS.COL_DATE, sms.getSmsDate());
        values.put(SMS.COL_FARM, sms.getSmsFarm());
        values.put(SMS.COL_FIELD, sms.getSmsField());
        values.put(SMS.COL_TOTAL_AREA, sms.getSmsArea());
        values.put(SMS.COL_STATUS, sms.getSmsStatus());
        values.put(SMS.COL_AREA, sms.getSmsArea());
        values.put(SMS.COL_CLUSTER, sms.getSmsCluster());
        values.put(SMS.COL_RMKS, sms.getSmsRemarks());

        // Inserting Row
        db.insert(FIR.TABLE_FIR, null, values);
        db.close();
    }
}
