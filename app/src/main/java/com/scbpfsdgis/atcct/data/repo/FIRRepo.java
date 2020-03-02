package com.scbpfsdgis.atcct.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.scbpfsdgis.atcct.data.DatabaseManager;
import com.scbpfsdgis.atcct.data.model.ATCC;
import com.scbpfsdgis.atcct.data.model.DBHelper;
import com.scbpfsdgis.atcct.data.model.FIR;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class FIRRepo {
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public FIRRepo() {
        FIR fir = new FIR();
    }

    public static String createFIRTbl() {
        String query = "CREATE TABLE IF NOT EXISTS " + FIR.TABLE_FIR + " (" +
                FIR.COL_ID + " TEXT, " +
                FIR.COL_FIRDATE + " TEXT, " +
                FIR.COL_FARMCODE + " TEXT, " +
                FIR.COL_FLDNO + " TEXT, " +
                FIR.COL_RFR_AREA + " DECIMAL(4,2), " +
                FIR.COL_OBST + " TEXT, " +
                FIR.COL_HARVMETH + " TEXT, " +
                FIR.COL_FLAGS + " INT, " +
                FIR.COL_START + " TEXT, " +
                FIR.COL_END + " TEXT, " +
                FIR.COL_NOTES + " TEXT, " +
                FIR.COL_COORNAME + " TEXT, " +
                FIR.COL_MAP + " TEXT, " +
                FIR.COL_FIRPATH + " TEXT)";
        System.out.println("Creating FIR Table: " + query);
        return query;
    }

    public static String createFIRAttTbl() {
        String query = "CREATE TABLE IF NOT EXISTS " + FIR.TABLE_FIR_ATT + " (" +
                FIR.COL_FIR_ID + " TEXT, " +
                FIR.COL_FIR_ATT_PATH + " TEXT, " +
                FIR.COL_FIR_ATT_CAP + " TEXT)";
        return query;
    }

    public void insertFIRAtt(String firID, String firAttPath, String firAttCaption) {
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(FIR.COL_FIR_ID, firID);
        values.put(FIR.COL_FIR_ATT_PATH, firAttPath);
        values.put(FIR.COL_FIR_ATT_CAP, firAttCaption);

        // Inserting Row
        db.insert(FIR.TABLE_FIR_ATT, null, values);
        db.close();
    }

    public void insert(FIR fir) {
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(FIR.COL_ID, fir.getFirID());
        values.put(FIR.COL_FIRDATE, fir.getFirDate());
        values.put(FIR.COL_FARMCODE, fir.getFirFarmCode());
        values.put(FIR.COL_FLDNO, fir.getFirFldNo());
        values.put(FIR.COL_RFR_AREA, fir.getFirRFRArea());
        values.put(FIR.COL_OBST, fir.getFirObst());
        values.put(FIR.COL_HARVMETH, fir.getFirHarvMeth());
        values.put(FIR.COL_FLAGS, fir.getFirFlags());
        values.put(FIR.COL_START, fir.getFirStart());
        values.put(FIR.COL_END, fir.getFirEnd());
        values.put(FIR.COL_NOTES, fir.getFirNotes());
        values.put(FIR.COL_COORNAME, fir.getFirCoorName());
        values.put(FIR.COL_MAP, fir.getFirMap());

        // Inserting Row
        db.insert(FIR.TABLE_FIR, null, values);
        db.close();
    }

    public void updateFIRPath(FIR fir) {
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(FIR.COL_FIRPATH, fir.getFirPath());

        db.update(FIR.TABLE_FIR, values, FIR.COL_ID + "= ? ", new String[]{fir.getFirID()});
        db.close();
    }

    public void delete(String firID) {
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        db.delete(FIR.TABLE_FIR, FIR.COL_ID + "='" + firID + "'", null);
        db.delete(FIR.TABLE_FIR_ATT, FIR.COL_ID + "='" + firID + "'", null);
    }

    public void deleteAtt(String firID) {
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        db.delete(FIR.TABLE_FIR_ATT, FIR.COL_ID + "='" + firID + "'", null);
    }

    public void update(FIR fir) {
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(FIR.COL_ID, fir.getFirID());
        values.put(FIR.COL_FIRDATE, fir.getFirDate());
        values.put(FIR.COL_FARMCODE, fir.getFirFarmCode());
        values.put(FIR.COL_FLDNO, fir.getFirFldNo());
        values.put(FIR.COL_RFR_AREA, fir.getFirRFRArea());
        values.put(FIR.COL_OBST, fir.getFirObst());
        values.put(FIR.COL_HARVMETH, fir.getFirHarvMeth());
        values.put(FIR.COL_FLAGS, fir.getFirFlags());
        values.put(FIR.COL_START, fir.getFirStart());
        values.put(FIR.COL_END, fir.getFirEnd());
        values.put(FIR.COL_NOTES, fir.getFirNotes());
        values.put(FIR.COL_COORNAME, fir.getFirCoorName());
        values.put(FIR.COL_MAP, fir.getFirMap());

        db.update(FIR.TABLE_FIR, values, FIR.COL_ID + "= ? ", new String[]{fir.getFirID()});
        db.close(); // Closing database connection
    }

    public ArrayList<HashMap<String, String>> getFIRList() {

        //Open connection to read only
        //db = DatabaseManager.getInstance().openDatabase();
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT " + FIR.COL_ID + " as ID, " +
                FIR.COL_FARMCODE + " as FarmCode," +
                FIR.COL_FLDNO + " as FldNo," +
                FIR.COL_RFR_AREA + " as Area " +
                "FROM " + FIR.TABLE_FIR + " " +
                "ORDER BY ID DESC";

        FarmsRepo farmsRepo = new FarmsRepo();
        ArrayList<HashMap<String, String>> firList = new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> fir = new HashMap<>();
                fir.put("ID", cursor.getString(cursor.getColumnIndex("ID")));
                fir.put("farmNameFIR", farmsRepo.getFarmByID(cursor.getString(cursor.getColumnIndex("FarmCode")), "M").getFarmName());
                fir.put("IDFldNo", cursor.getString(cursor.getColumnIndex("ID"))+ " - Fld. No. " + cursor.getString(cursor.getColumnIndex("FldNo")) + " (" + cursor.getString(cursor.getColumnIndex("Area")) + " has.)");
                firList.add(fir);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        db.close();
        return firList;
    }

    public ArrayList<HashMap<String, String>> getAttachments(String firID) {

        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT " + FIR.COL_ID + " as ID, " +
                FIR.COL_FIR_ATT_PATH + " as AttPath, " +
                FIR.COL_FIR_ATT_CAP + " as AttCaption " +
                "FROM " + FIR.TABLE_FIR_ATT + " " +
                "WHERE " + FIR.COL_ID + " = '" + firID + "'";

        ArrayList<HashMap<String, String>> attList = new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> fir = new HashMap<>();
                fir.put("ID", cursor.getString(cursor.getColumnIndex("ID")));
                fir.put("AttPath", cursor.getString(cursor.getColumnIndex("AttPath")));
                fir.put("AttCaption", cursor.getString(cursor.getColumnIndex("AttCaption")));
                attList.add(fir);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        db.close();
        return attList;
    }


    public String getFIRCount() {
        String count = "";
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(" + FIR.COL_ID + ") as Count FROM " + FIR.TABLE_FIR;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            count = cursor.getString(cursor.getColumnIndex("Count"));
        }
        return count;
    }

    public FIR getFIRByID(String ID) {
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();
        FIR fir = new FIR();

        String query = "SELECT " + FIR.COL_ID + " as ID, " +
                FIR.COL_FIRDATE + " as CreateDate, " +
                FIR.COL_FARMCODE + " as FarmCode, " +
                FIR.COL_FLDNO + " as FldNo, " +
                FIR.COL_RFR_AREA + " as RFRArea, " +
                FIR.COL_OBST + " as Obstructions, " +
                FIR.COL_HARVMETH + " as HarvestMethod, " +
                FIR.COL_FLAGS + " as Flags, " +
                FIR.COL_START + " as StartTime, " +
                FIR.COL_END + " as EndTime, " +
                FIR.COL_NOTES + " as Notes, " +
                FIR.COL_COORNAME + " as CoorName,  " +
                FIR.COL_MAP + " as Map, " +
                FIR.COL_FIRPATH + " as FIRPath " +
                "FROM " + FIR.TABLE_FIR + " " +
                "WHERE " + FIR.COL_ID + " =?";
        System.out.println(query);

        Cursor cursor = db.rawQuery(query, new String[]{ID});

        if (cursor.moveToFirst()) {
            do {
                fir.setFirID(cursor.getString(cursor.getColumnIndex("ID")));
                fir.setFirDate(cursor.getString(cursor.getColumnIndex("CreateDate")));
                fir.setFirFarmCode(cursor.getString(cursor.getColumnIndex("FarmCode")));
                fir.setFirFldNo(cursor.getString(cursor.getColumnIndex("FldNo")));
                fir.setFirRFRArea(cursor.getDouble(cursor.getColumnIndex("RFRArea")));
                fir.setFirObst(cursor.getString(cursor.getColumnIndex("Obstructions")));
                fir.setFirHarvMeth(cursor.getString(cursor.getColumnIndex("HarvestMethod")));
                fir.setFirFlags(cursor.getInt(cursor.getColumnIndex("Flags")));
                fir.setFirStart(cursor.getString(cursor.getColumnIndex("StartTime")));
                fir.setFirEnd(cursor.getString(cursor.getColumnIndex("EndTime")));
                fir.setFirNotes(cursor.getString(cursor.getColumnIndex("Notes")));
                fir.setFirCoorName(cursor.getString(cursor.getColumnIndex("CoorName")));
                fir.setFirMap(cursor.getString(cursor.getColumnIndex("Map")));
                fir.setFirPath(cursor.getString(cursor.getColumnIndex("FIRPath")));


            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return fir;
    }

    public String generateFIRID() {
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date today = new Date();
        int dateID = Integer.parseInt(sdf.format(today) + "01");

        String query = "SELECT MAX(" + FIR.COL_ID + ") as LastID " +
                "FROM " + FIR.TABLE_FIR;

        Cursor cursor = db.rawQuery(query, null);
        int result;
        if (cursor.moveToFirst()) {
            result = cursor.getInt(cursor.getColumnIndex("LastID"));
            System.out.println("FIR " + result);
            while (result >= dateID) {
                dateID += 1;
            }
        }
        return String.valueOf(dateID);
    }

}
