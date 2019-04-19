package com.scbpfsdgis.atcct.data.repo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.scbpfsdgis.atcct.data.model.AuthRep;
import com.scbpfsdgis.atcct.data.model.DBHelper;
import com.scbpfsdgis.atcct.data.model.Farms;

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

    public void insert(AuthRep ar) {
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(AuthRep.COL_OWNER_ID, ar.getOwnerID());
        values.put(AuthRep.COL_AR_NAME, ar.getArFullName());
        values.put(AuthRep.COL_AR_IDTYPE, ar.getArRelation());
        values.put(AuthRep.COL_AR_REL, ar.getArIDType());


        // Inserting Row
        db.insert(AuthRep.TABLE_AUTHREP, null, values);
        db.close();
    }
}
