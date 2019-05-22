package com.scbpfsdgis.atcct.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;

import com.scbpfsdgis.atcct.data.DatabaseManager;
import com.scbpfsdgis.atcct.data.model.ATCC;
import com.scbpfsdgis.atcct.data.model.DBHelper;
import com.scbpfsdgis.atcct.data.model.Owners;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by William on 12/17/2018.
 */

public class ATCCRepo extends AppCompatActivity {
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
                ATCC.COL_BANKNAME + " TEXT, " +
                ATCC.COL_BANKADD + " TEXT, " +
                ATCC.COL_REMARKS + " TEXT, " +
                ATCC.COL_DTECREATED + " TEXT, " +
                ATCC.COL_DTEMODIFIED + " TEXT, " +
                ATCC.COL_DTESIGNED + " TEXT," +
                ATCC.COL_FILE + " TEXT," +
                ATCC.COL_SIGNATORY + " TEXT)";
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

    public void updateATCCT(ATCC atcc, String type) {
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (type.equalsIgnoreCase("Signed")) {
            values.put(ATCC.COL_DTESIGNED, atcc.getDteSigned());
            values.put(ATCC.COL_FILE, atcc.getFileName());
            values.put(ATCC.COL_SIGNATORY, atcc.getSignatory());
        } else {
            values.put(ATCC.COL_PMTMETHOD, atcc.getPmtMethod());
            values.put(ATCC.COL_PICKUPPOINT, atcc.getPickupPt());
            values.put(ATCC.COL_ACCNAME, atcc.getAccName());
            values.put(ATCC.COL_ACCNO, atcc.getAccNo());
            values.put(ATCC.COL_BANKNAME, atcc.getBankName());
            values.put(ATCC.COL_BANKADD, atcc.getBankAdd());
            values.put(ATCC.COL_REMARKS, atcc.getRemarks());
            values.put(ATCC.COL_DTEMODIFIED, atcc.getDteModified());
            values.put(ATCC.COL_DTESIGNED, "");
            values.put(ATCC.COL_FILE, "");
            values.put(ATCC.COL_SIGNATORY, "");
        }

        db.update(ATCC.TABLE_ATCC, values, ATCC.COL_ATCCNO + "= ? ", new String[]{String.valueOf(atcc.getAtccNo())});
        db.close(); // Closing database connection
    }

    public ArrayList<HashMap<String, String>> getATCCForList(String filter) {
        String filterClause = " WHERE A." + ATCC.COL_DTESIGNED + "";
        System.out.println("Filter " + filter);
        if (filter.equalsIgnoreCase("Signed")) {
            filterClause += " IS NOT NULL AND A." + ATCC.COL_DTESIGNED + " <> ''";
            System.out.println("Filter Clause: " + filterClause);
        } else if (filter.equalsIgnoreCase("To Sign")) {
            filterClause += " IS NULL OR A." + ATCC.COL_DTESIGNED + " = ''";
        } else {
            filterClause = "";
            System.out.println("Filter " + filter);
        }

        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT A." + ATCC.COL_ATCCNO + " as ATCCNo, " +
                "A." + ATCC.COL_OWNERID + " as OwnerID, " +
                "O." + Owners.COL_OWNERNAME + " as OwnerName, " +
                "A." + ATCC.COL_DTECREATED + " as CreateDate," +
                "A." + ATCC.COL_DTESIGNED + " as DateSigned " +
                "FROM " + ATCC.TABLE_ATCC + " A " +
                "LEFT JOIN " + Owners.TABLE_OWNERS + " O ON " +
                "A." + ATCC.COL_OWNERID + " = " + "O." + Owners.COL_OWNERID +
                filterClause +
                " ORDER BY OwnerName";
        System.out.println(selectQuery);

        ArrayList<HashMap<String, String>> atccList = new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, null);
        OwnersRepo ownersRepo = new OwnersRepo();
        Owners owner;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                owner = ownersRepo.getOwnerByID(cursor.getString(cursor.getColumnIndex("OwnerID")), "M");
                HashMap<String, String> atcc = new HashMap<>();
                atcc.put("ATCCNo", cursor.getString(cursor.getColumnIndex("ATCCNo")));
                atcc.put("OwnerID", cursor.getString(cursor.getColumnIndex("OwnerID")));
                atcc.put("OwnerName", owner.getOwnerName());
                atcc.put("ATCCTDetails", cursor.getString(cursor.getColumnIndex("ATCCNo")) + " | " + cursor.getString(cursor.getColumnIndex("CreateDate")));
                atcc.put("DateSigned", cursor.getString(cursor.getColumnIndex("DateSigned")));
                atccList.add(atcc);

            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return atccList;
    }

    public void delete(String atccNo) {
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        db.delete(ATCC.TABLE_ATCC, ATCC.COL_ATCCNO + "='" + atccNo + "'", null);
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
                atcc.setAtccNo(cursor.getString(cursor.getColumnIndex(ATCC.COL_ATCCNO)));
                atcc.setOwnerID(cursor.getString(cursor.getColumnIndex(ATCC.COL_OWNERID)));
                atcc.setPmtMethod(cursor.getString(cursor.getColumnIndex(ATCC.COL_PMTMETHOD)));
                atcc.setPickupPt(cursor.getString(cursor.getColumnIndex(ATCC.COL_PICKUPPOINT)));
                atcc.setAccName(cursor.getString(cursor.getColumnIndex(ATCC.COL_ACCNAME)));
                atcc.setAccNo(cursor.getString(cursor.getColumnIndex(ATCC.COL_ACCNO)));
                atcc.setBankName(cursor.getString(cursor.getColumnIndex(ATCC.COL_BANKNAME)));
                atcc.setBankAdd(cursor.getString(cursor.getColumnIndex(ATCC.COL_BANKADD)));
                atcc.setRemarks(cursor.getString(cursor.getColumnIndex(ATCC.COL_REMARKS)));
                atcc.setFileName(cursor.getString(cursor.getColumnIndex(ATCC.COL_FILE)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return atcc;
    }

    public int getATCCTCount(String filter) {

        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();
        StringBuilder query = new StringBuilder();
        query.append("SELECT COUNT(*) FROM " + ATCC.TABLE_ATCC);
        if (filter.equalsIgnoreCase("Signed")) {
            query.append(" WHERE " + ATCC.COL_DTESIGNED + " IS NOT NULL AND " + ATCC.COL_DTESIGNED + " <> ''");
        }
        if (filter.equalsIgnoreCase("To Sign")) {
            query.append(" WHERE " + ATCC.COL_DTESIGNED + " IS NULL OR " + ATCC.COL_DTESIGNED + " = ''");
        }
        int count = 0;
        Cursor cursor = db.rawQuery(query.toString(), null);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        db.close();
        return count;
    }

    public String selectATCCTs() {
        return "SELECT A.ATCC_NO AS \"ATCC No.\",\n" +
                "A.OWNER_ID AS \"Planter ID\",\n" +
                "O.OWNER_NAME AS \"Planter Name\",\n" +
                "A.PAYMENT_METHOD AS \"Payment Method\",\n" +
                "A.REMARKS AS Remarks,\n" +
                "A.DATE_CREATED AS Created,\n" +
                "A.DATE_MODIFIED AS Modified,\n" +
                "A.FILENAME AS Filename,\n" +
                "A.SIGNATORY AS \"Signed By\",\n" +
                "\"" + getDeviceName() + "\" AS Device\n" +
                "FROM T_ATCC A JOIN T_OWNERS O\n" +
                "ON A.OWNER_ID = O.OWNER_ID";
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        return manufacturer + " " + model;
    }

}
