package com.scbpfsdgis.atcct.data.repo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.scbpfsdgis.atcct.data.model.DBHelper;
import com.scbpfsdgis.atcct.data.model.Farms;

/**
 * Created by William on 12/17/2018.
 */

public class FarmsRepo {
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public FarmsRepo() {
       Farms farms = new Farms();
    }

    public static String createTable() {
        String query = "CREATE TABLE IF NOT EXISTS " + Farms.TABLE_FARMS + " (" +
                Farms.COL_FARMCODE + " TEXT, " +
                Farms.COL_FIELDGRP + " TEXT, " +
                Farms.COL_BASE + " TEXT)";
        System.out.println(query);
        return query;
    }

    public void insert(Farms farms) {
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Farms.COL_FARMCODE, farms.getFarmCode());
        values.put(Farms.COL_FIELDGRP, farms.getFieldGrp());
        values.put(Farms.COL_BASE, farms.getBase());

        // Inserting Row
        db.insert(Farms.TABLE_FARMS, null, values);
        db.close();
    }

}
