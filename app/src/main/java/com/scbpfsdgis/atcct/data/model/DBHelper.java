package com.scbpfsdgis.atcct.data.model;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.scbpfsdgis.atcct.app.App;
import com.scbpfsdgis.atcct.data.repo.ATCCRepo;
import com.scbpfsdgis.atcct.data.repo.FarmsRepo;
import com.scbpfsdgis.atcct.data.repo.OwnersRepo;


/**
 * Created by William on 1/7/2018.
 */

public class DBHelper extends SQLiteOpenHelper {
    //version number to upgrade database version
    //each time if you Add, Edit table, you need to change the
    //version number.
    private static final int DATABASE_VERSION =4;
    // Database Name
    private static final String DATABASE_NAME = "ATCCT.db";
    private static final String TAG = DBHelper.class.getSimpleName();

    public int getDatabaseVersion() {
        return DATABASE_VERSION;
    }

    public DBHelper() {
        super(App.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FarmsRepo.createMasterTbl());
        db.execSQL(FarmsRepo.createFarmsTbl());
        db.execSQL(FarmsRepo.createFChgsTable());
        db.execSQL(OwnersRepo.createOwnersTbl());
        db.execSQL(ATCCRepo.createTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, String.format("SQLiteDatabase.onUpgrade(%d -> %d)", oldVersion, newVersion));
        if (oldVersion <= 3) {
            db.execSQL("ALTER TABLE " + Farms.TABLE_FARM_CHANGES + " ADD " + Farms.COL_REMARKS + " TEXT");
        }
        onCreate(db);
    }
}
