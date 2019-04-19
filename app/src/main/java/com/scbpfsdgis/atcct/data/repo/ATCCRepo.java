package com.scbpfsdgis.atcct.data.repo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.scbpfsdgis.atcct.data.model.ATCC;
import com.scbpfsdgis.atcct.data.model.DBHelper;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by William on 12/17/2018.
 */

public class ATCCRepo {
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public ATCCRepo() {
       ATCC atcc = new ATCC();
    }

    public static String createTable() {
        return "CREATE TABLE IF NOT EXISTS " + ATCC.TABLE_ATCC + " (" +
                ATCC.COL_ATCCNO + " TEXT, " +
                ATCC.COL_OWNERID + " TEXT, " +
                ATCC.COL_PMTMETHOD + " TEXT, " +
                ATCC.COL_PICKUPPOINT + "TEXT, " +
                ATCC.COL_ACCNAME + " TEXT, " +
                ATCC.COL_ACCNO + " TEXT, " +
                ATCC.COL_BANKNAME  + "TEXT, " +
                ATCC.COL_BANKADD + "TEXT, " +
                ATCC.COL_REMARKS + " TEXT, " +
                ATCC.COL_DTECREATED + " DATE, " +
                ATCC.COL_DTESIGNED + " DATE)";
    }

    public void insert(ATCC atcc) {
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD" , Locale.getDefault());

        values.put(ATCC.COL_ATCCNO, atcc.getAtccNo());
        values.put(ATCC.COL_OWNERID, atcc.getOwnerID());
        values.put(ATCC.COL_PMTMETHOD, atcc.getPmtMethod());
        values.put(ATCC.COL_PICKUPPOINT, atcc.getPickupPt());
        values.put(ATCC.COL_ACCNAME, atcc.getAccName());
        values.put(ATCC.COL_ACCNO, atcc.getAccNo());
        values.put(ATCC.COL_BANKNAME, atcc.getBankName());
        values.put(ATCC.COL_BANKADD, atcc.getBankAdd());
        values.put(ATCC.COL_REMARKS, atcc.getRemarks());
        values.put(ATCC.COL_DTECREATED, sdf.format(atcc.getDteCreated()));
        values.put(ATCC.COL_ACCNO, sdf.format(atcc.getAccNo()));

        // Inserting Row
        db.insert(ATCC.TABLE_ATCC, null, values);
        db.close();
    }

}
