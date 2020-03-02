package com.scbpfsdgis.atcct.data.model;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.scbpfsdgis.atcct.app.App;
import com.scbpfsdgis.atcct.data.repo.ATCCRepo;
import com.scbpfsdgis.atcct.data.repo.AuthRepRepo;
import com.scbpfsdgis.atcct.data.repo.ConfigRepo;
import com.scbpfsdgis.atcct.data.repo.ContactRepo;
import com.scbpfsdgis.atcct.data.repo.FIRRepo;
import com.scbpfsdgis.atcct.data.repo.FarmsRepo;
import com.scbpfsdgis.atcct.data.repo.OwnersRepo;

import java.security.acl.Owner;


/**
 * Created by William on 1/7/2018.
 */

public class DBHelper extends SQLiteOpenHelper {
    //version number to upgrade database version
    //each time if you Add, Edit table, you need to change the
    //version number.
    private static final int DATABASE_VERSION = 19;
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
        db.execSQL(OwnersRepo.createOwnerChgTbl());
        db.execSQL(ATCCRepo.createTable());
        db.execSQL(AuthRepRepo.createARTable());
        db.execSQL(FIRRepo.createFIRTbl());
        db.execSQL(ConfigRepo.createCFG());
        db.execSQL(ContactRepo.createContactsTbl());
        db.execSQL(FIRRepo.createFIRAttTbl());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, String.format("SQLiteDatabase.onUpgrade(%d -> %d)", oldVersion, newVersion));
        if (oldVersion <= 3) {
            db.execSQL("ALTER TABLE " + Farms.TABLE_FARM_CHANGES + " ADD " + Farms.COL_REMARKS + " TEXT");
        }
        if (oldVersion <= 5) {
            db.execSQL("ALTER TABLE " + ATCC.TABLE_ATCC + " ADD " + ATCC.COL_FILE + " TEXT");
        }
        if (oldVersion <= 6) {
            db.execSQL("ALTER TABLE " + ATCC.TABLE_ATCC + " ADD " + ATCC.COL_SIGNATORY + " TEXT");
        }
        if (oldVersion <= 7) {
            db.execSQL("ALTER TABLE " + Owners.TABLE_OWNERS + " ADD " + Owners.COL_BASES + " TEXT");
        }
        if (oldVersion <= 8) {
            db.execSQL("ALTER TABLE " + Farms.TABLE_MASTERTBL + " ADD " + Owners.COL_BASES + " TEXT");
        }

        if (oldVersion <= 11) {
            db.execSQL("ALTER TABLE " + Farms.TABLE_FARMS + " ADD " + Farms.COL_CONTPRSN + " TEXT");
            db.execSQL("ALTER TABLE " + Farms.TABLE_FARMS + " ADD " + Farms.COL_CONTNUM + " TEXT");
            db.execSQL("ALTER TABLE " + Farms.TABLE_MASTERTBL + " ADD " + Farms.COL_CONTPRSN + " TEXT");
            db.execSQL("ALTER TABLE " + Farms.TABLE_MASTERTBL + " ADD " + Farms.COL_CONTNUM + " TEXT");
            db.execSQL(FIRRepo.createFIRTbl());
        }

        if (oldVersion <= 12) {
            db.execSQL("ALTER TABLE " + FIR.TABLE_FIR + " ADD " +  FIR.COL_COORNAME + " TEXT") ;
        }

        if (oldVersion <= 15) {
            db.execSQL("ALTER TABLE " + FIR.TABLE_FIR + " ADD " + FIR.COL_FIRPATH + " TEXT");
        }

        if (oldVersion <= 16) {
            db.execSQL(ContactRepo.createContactsTbl());
        }

        if (oldVersion <= 18) {
            db.execSQL(FIRRepo.createFIRAttTbl());
            db.execSQL("DROP TABLE IF EXISTS T_ATTACH");
        }

        onCreate(db);
    }
}
