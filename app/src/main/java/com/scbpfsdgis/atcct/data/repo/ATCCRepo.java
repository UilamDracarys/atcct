package com.scbpfsdgis.atcct.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.scbpfsdgis.atcct.data.DatabaseManager;
import com.scbpfsdgis.atcct.data.model.ATCC;
import com.scbpfsdgis.atcct.data.model.DBHelper;
import com.scbpfsdgis.atcct.data.model.Farms;
import com.scbpfsdgis.atcct.data.model.Owners;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
                ATCC.COL_PICKUPPOINT + " TEXT, " +
                ATCC.COL_ACCNAME + " TEXT, " +
                ATCC.COL_ACCNO + " TEXT, " +
                ATCC.COL_BANKNAME  + " TEXT, " +
                ATCC.COL_BANKADD + " TEXT, " +
                ATCC.COL_REMARKS + " TEXT, " +
                ATCC.COL_DTECREATED + " TEXT, " +
                ATCC.COL_DTESIGNED + " TEXT)";
    }

    public void insert(ATCC atcc) {
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ATCC.COL_ATCCNO, atcc.getAtccNo());
        values.put(ATCC.COL_OWNERID, atcc.getOwnerID());
        values.put(ATCC.COL_PMTMETHOD, atcc.getPmtMethod());
        values.put(ATCC.COL_PICKUPPOINT, atcc.getPickupPt());
        values.put(ATCC.COL_ACCNAME, atcc.getAccName());
        values.put(ATCC.COL_ACCNO, atcc.getAccNo());
        values.put(ATCC.COL_BANKNAME, atcc.getBankName());
        values.put(ATCC.COL_BANKADD, atcc.getBankAdd());
        values.put(ATCC.COL_REMARKS, atcc.getRemarks());
        values.put(ATCC.COL_DTECREATED, atcc.getDteCreated());

        // Inserting Row
        db.insert(ATCC.TABLE_ATCC, null, values);
        db.close();
    }

    public ArrayList<HashMap<String, String>> getATCCForList() {
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT A." + ATCC.COL_ATCCNO + " as ATCCNo, " +
                "A." + ATCC.COL_OWNERID + " as OwnerID, " +
                "O." + Owners.COL_OWNERNAME + " as OwnerName, " +
                "A." + ATCC.COL_DTECREATED + " as CreateDate " +
                "FROM " + ATCC.TABLE_ATCC + " A " +
                "LEFT JOIN " + Owners.TABLE_OWNERS + " O ON " +
                "A." + ATCC.COL_OWNERID + " = " + "O." + Owners.COL_OWNERID;

        ArrayList<HashMap<String, String>> atccList = new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> atcc = new HashMap<>();
                atcc.put("ATCCNo", cursor.getString(cursor.getColumnIndex("ATCCNo")));
                atcc.put("OwnerID", cursor.getString(cursor.getColumnIndex("OwnerID")));
                atcc.put("OwnerName", cursor.getString(cursor.getColumnIndex("OwnerName")));
                atcc.put("ATCCTDetails", cursor.getString(cursor.getColumnIndex("ATCCNo"))+ " | " + cursor.getString(cursor.getColumnIndex("CreateDate")));
                atccList.add(atcc);

            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return atccList;
    }

    public Boolean isATCCExist(String ownerID) {
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + ATCC.TABLE_ATCC + "" +
                " WHERE " + ATCC.COL_OWNERID + " =?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{ownerID});

        if (cursor.moveToFirst()) {
            return true;
        } else {
            return false;
        }

    }

    public ATCC getATCCByNo(String atccNo) {
        ATCC atcc = new ATCC();
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + ATCC.TABLE_ATCC + " WHERE " + ATCC.COL_ATCCNO + " =?";


        Cursor cursor = db.rawQuery(selectQuery, new String[]{atccNo});

        if (cursor.moveToFirst()) {
            do {
                atcc.setAtccNo(cursor.getString(cursor.getColumnIndex(ATCC.COL_ACCNO)));
                atcc.setOwnerID(cursor.getString(cursor.getColumnIndex(ATCC.COL_OWNERID)));
                atcc.setPmtMethod(cursor.getString(cursor.getColumnIndex(ATCC.COL_PMTMETHOD)));
                atcc.setPickupPt(cursor.getString(cursor.getColumnIndex(ATCC.COL_PICKUPPOINT)));
                atcc.setAccName(cursor.getString(cursor.getColumnIndex(ATCC.COL_ACCNAME)));
                atcc.setAccNo(cursor.getString(cursor.getColumnIndex(ATCC.COL_ACCNO)));
                atcc.setBankName(cursor.getString(cursor.getColumnIndex(ATCC.COL_BANKNAME)));
                atcc.setBankAdd(cursor.getString(cursor.getColumnIndex(ATCC.COL_BANKADD)));
                atcc.setRemarks(cursor.getString(cursor.getColumnIndex(ATCC.COL_REMARKS)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return atcc;
    }

}
