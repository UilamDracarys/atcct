package com.scbpfsdgis.atcct.data.repo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.scbpfsdgis.atcct.data.model.DBHelper;
import com.scbpfsdgis.atcct.data.model.Farms;
import com.scbpfsdgis.atcct.data.model.Owners;

/**
 * Created by William on 12/17/2018.
 */

public class OwnersRepo {
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public OwnersRepo() {
       Owners owner = new Owners();
    }

    public static String createTable() {
        return "CREATE TABLE IF NOT EXISTS " + Owners.TABLE_OWNERS + " (" +
                Owners.COL_OWNERID + " TEXT, " +
                Owners.COL_OWNERNAME + " TEXT, " +
                Owners.COL_PHONEMOB + " TEXT, " +
                Owners.COL_PHONEBUS + " TEXT, " +
                Owners.COL_CONTACTPERSON + " TEXT)";
    }

    public void insert(Owners owner) {
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Owners.COL_OWNERID, owner.getOwnerID());
        values.put(Owners.COL_OWNERNAME, owner.getOwnerName());
        values.put(Owners.COL_PHONEMOB, owner.getMobileNo());
        values.put(Owners.COL_PHONEBUS, owner.getBusinessNo());
        values.put(Owners.COL_CONTACTPERSON, owner.getContactPrsn());

        // Inserting Row
        db.insert(Owners.TABLE_OWNERS, null, values);
        db.close();
    }

}
