package com.scbpfsdgis.atcct.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.scbpfsdgis.atcct.data.model.AuthRep;
import com.scbpfsdgis.atcct.data.model.DBHelper;
import com.scbpfsdgis.atcct.data.model.Farms;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by William on 4/17/2019.
 */

public class AuthRepRepo {

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public AuthRepRepo() {
        AuthRep authRep = new AuthRep();
    }

    public static String createARTable() {
        String query = "CREATE TABLE IF NOT EXISTS " + AuthRep.TABLE_AUTHREP + " (" +
                AuthRep.COL_OWNER_ID + " TEXT, " +
                AuthRep.COL_AR_NAME + " TEXT, " +
                AuthRep.COL_AR_REL + " TEXT, " +
                AuthRep.COL_AR_IDTYPE + " TEXT)";
        return query;
    }

    public void delete(String ownerID) {
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        db.delete(AuthRep.TABLE_AUTHREP, AuthRep.COL_OWNER_ID + "=?", new String[]{String.valueOf(ownerID)});
        db.close();
    }

    public void insert(AuthRep ar) {
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(AuthRep.COL_OWNER_ID, ar.getOwnerID());
        values.put(AuthRep.COL_AR_NAME, ar.getArFullName());
        values.put(AuthRep.COL_AR_REL, ar.getArRelation());
        values.put(AuthRep.COL_AR_IDTYPE, ar.getArIDType());


        // Inserting Row
        db.insert(AuthRep.TABLE_AUTHREP, null, values);
        db.close();
    }

    public ArrayList<HashMap<String, String>> getAuthRepForOwner(String ownerId) {
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + AuthRep.TABLE_AUTHREP + " WHERE " + AuthRep.COL_OWNER_ID + "=?";

        ArrayList<HashMap<String, String>> arList = new ArrayList<>();
        Cursor cursor = db.rawQuery(query, new String[] { ownerId});

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> authReps = new HashMap<>();
                authReps.put("ARName", cursor.getString(cursor.getColumnIndex(AuthRep.COL_AR_NAME)));
                authReps.put("ARRel", cursor.getString(cursor.getColumnIndex(AuthRep.COL_AR_REL)));
                authReps.put("ARIDType", cursor.getString(cursor.getColumnIndex(AuthRep.COL_AR_IDTYPE)));
                authReps.put("ARRelID", cursor.getString(cursor.getColumnIndex(AuthRep.COL_AR_REL))+ " | " + cursor.getString(cursor.getColumnIndex(AuthRep.COL_AR_IDTYPE)));
               arList.add(authReps);

            } while (cursor.moveToNext());
        }
        return arList;
    }
}
