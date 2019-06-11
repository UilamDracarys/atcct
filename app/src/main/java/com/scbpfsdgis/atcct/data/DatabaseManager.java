package com.scbpfsdgis.atcct.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ir.mahdi.mzip.zip.ZipArchive;

/**
 * Created by William on 12/17/2018.
 */

public class DatabaseManager {
    private Integer mOpenCounter = 0;

    private static DatabaseManager instance;
    private static SQLiteOpenHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;

    public static synchronized void initializeInstance(SQLiteOpenHelper helper) {
        if (instance == null) {
            instance = new DatabaseManager();
            mDatabaseHelper = helper;
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(DatabaseManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }

        return instance;
    }

    public synchronized SQLiteDatabase openDatabase() {
        mOpenCounter += 1;
        if (mOpenCounter == 1) {
            // Opening new database
            mDatabase = mDatabaseHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    public synchronized void closeDatabase() {
        mOpenCounter -= 1;
        if (mOpenCounter == 0) {
            // Closing database
            mDatabase.close();
        }
    }

    public void backupDB(Context context, String dbName, String bakPath, String password) throws IOException {

        String backupDirectory = Environment.getExternalStorageDirectory() + bakPath;
        File backupDir = new File(backupDirectory, "");
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
        final String inFileName = context.getDatabasePath(dbName).toString();
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.getDefault());

        String bakFilePath = backupDirectory + "/BAK_" + df.format(date) + ".rar";

        ZipArchive zipArchive = new ZipArchive();
        zipArchive.zip(inFileName, bakFilePath, password);
        Toast.makeText(context, "Database successfully backed up!", Toast.LENGTH_SHORT).show();
    }
}
