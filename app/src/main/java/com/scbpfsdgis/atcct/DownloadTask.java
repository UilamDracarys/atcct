package com.scbpfsdgis.atcct;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.scbpfsdgis.atcct.data.model.DBHelper;
import com.scbpfsdgis.atcct.data.model.Farms;
import com.scbpfsdgis.atcct.data.model.Owners;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by SONU on 29/10/15.
 */
public class DownloadTask {

    SQLiteDatabase db;
    DBHelper dbHelper;
    private static final String TAG = "Download Task";
    private Context context;
    private String downloadUrl = "", downloadFileName = "";
    public String downloadedFile = "";

    public DownloadTask(Context context, String downloadUrl) {
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();
        this.context = context;
        this.downloadUrl = downloadUrl;

        downloadFileName = "master_data.csv";
        Log.e(TAG, downloadFileName);

        //Start Downloading Task
        new DownloadingTask().execute();
    }

    private class DownloadingTask extends AsyncTask<Void, Void, Void> {

        File apkStorage = new File(Environment.getExternalStorageDirectory() + "/ATCCTMobile/Data", "");
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(context.getApplicationContext(), "Download Started...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (outputFile != null) {

                    Toast.makeText(context.getApplicationContext(), "Download completed! Importing...", Toast.LENGTH_SHORT).show();
                    importDownloaded(outputFile.getAbsolutePath());
                    outputFile.delete();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //
                        }
                    }, 3000);
                    Toast.makeText(context.getApplicationContext(), "Download Failed!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Download Failed");
                }
            } catch (Exception e) {
                e.printStackTrace();

                //Change button text if exception occurs
                Toast.makeText(context.getApplicationContext(), "Download Failed!", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //
                    }
                }, 3000);
                Log.e(TAG, "Download Failed with Exception - " + e.getLocalizedMessage());

            }
            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                URL url = new URL(downloadUrl);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                c.connect();//connect the URL Connection

                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());
                }


                if (!apkStorage.exists()) {
                    apkStorage.mkdirs();
                    Log.e(TAG, "Directory Created.");
                }

                outputFile = new File(apkStorage, downloadFileName);//Create Output file in Main File

                //Create New File if not present
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    Log.e(TAG, "File Created");
                }

                FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

                InputStream is = c.getInputStream();//Get InputStream for connection

                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);//Write new file
                }
                //Close all connection after doing task
                fos.close();
                is.close();

            } catch (Exception e) {

                //Read exception if something went wrong
                e.printStackTrace();
                outputFile = null;
                Log.e(TAG, "Download Error Exception " + e.getMessage());
            }

            return null;
        }
    }

    public String getDownloadedFile() {
        return downloadedFile;
    }

    public void setDownloadedFile(String downloadedFile) {
        this.downloadedFile = downloadedFile;
    }

    private void importDownloaded(String filePath) {
        String filepath = filePath;
        System.out.println("CSV path: " + filepath);

        db = dbHelper.getWritableDatabase();
        String tableName = Farms.TABLE_MASTERTBL;
        db.execSQL("DELETE FROM " + tableName);
        try {
            try {

                FileReader file = new FileReader(filepath);
                BufferedReader buffer = new BufferedReader(file);
                ContentValues contentValues = new ContentValues();
                String line = "";
                db.beginTransaction();
                int count = 0;

                while ((line = buffer.readLine()) != null) {

                    if (count == 0) {
                        System.out.println("Skipped header row");
                    } else {
                        String[] str = line.split(";", 10);  // defining 8 columns with null or blank field //values acceptance
                        String farmCode = str[0];
                        String farmName = str[1];
                        String farmBase = str[2];
                        String farmStatus = str[3];
                        String ownerID = str[4];
                        String ownerName = str[5];
                        String ownerMobile = str[6];
                        String ownerEmail = str[7];
                        String ownerAddress = str[8];
                        String ownerBases = str[9];

                        contentValues.put(Farms.COL_FARMCODE, farmCode);
                        contentValues.put(Farms.COL_FARMNAME, farmName);
                        contentValues.put(Farms.COL_BASE, farmBase);
                        contentValues.put(Farms.COL_STATUS, farmStatus);
                        contentValues.put(Farms.COL_OWNERID, ownerID);
                        contentValues.put(Owners.COL_OWNERNAME, ownerName);
                        contentValues.put(Owners.COL_OWNERMOB, ownerMobile);
                        contentValues.put(Owners.COL_OWNEREMAIL, ownerEmail);
                        contentValues.put(Owners.COL_OWNERADDRESS, ownerAddress);
                        contentValues.put(Owners.COL_BASES, ownerBases);
                        db.insert(tableName, null, contentValues);
                    }
                    count += 1;

                }
                String insertFarms = "INSERT INTO " + Farms.TABLE_FARMS + " (" + Farms.COL_FARMCODE + ", " +
                        Farms.COL_FARMNAME + ", " +
                        Farms.COL_BASE + ", " +
                        Farms.COL_STATUS + ", " +
                        Farms.COL_OWNERID + ") " +
                        "SELECT " + Farms.COL_FARMCODE + ", " +
                        Farms.COL_FARMNAME + ", " +
                        Farms.COL_BASE + ", " +
                        Farms.COL_STATUS + ", " +
                        Farms.COL_OWNERID + " FROM " + Farms.TABLE_MASTERTBL;
                db.execSQL("DELETE FROM " + Farms.TABLE_FARMS);
                System.out.println(insertFarms);
                db.execSQL(insertFarms);
                db.execSQL("DELETE FROM " + Owners.TABLE_OWNERS + " WHERE " + Owners.COL_OWNERID + " NOT LIKE 'N-%'");
                String insertOwners = "INSERT INTO " + Owners.TABLE_OWNERS + " (" +
                        Owners.COL_OWNERID + ", " +
                        Owners.COL_OWNERNAME + ", " +
                        Owners.COL_OWNERMOB + ", " +
                        Owners.COL_OWNEREMAIL + ", " +
                        Owners.COL_OWNERADDRESS + ", " +
                        Owners.COL_BASES + ") "+
                        "SELECT " + Farms.COL_OWNERID + ", " +
                        Owners.COL_OWNERNAME + ", " +
                        Owners.COL_OWNERMOB + ", " +
                        Owners.COL_OWNEREMAIL + ", " +
                        Owners.COL_OWNERADDRESS + ", " +
                        Owners.COL_BASES + " FROM " + Farms.TABLE_MASTERTBL +
                        " GROUP BY " + Farms.COL_OWNERID + " ";
                System.out.println(insertOwners);
                db.execSQL(insertOwners);
                db.execSQL("DELETE FROM " + Farms.TABLE_MASTERTBL);
                db.setTransactionSuccessful();
                db.endTransaction();
                Toast.makeText(context.getApplicationContext(), "Data import successful.", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                System.out.println(e.getMessage() + "first");
            }
        } catch (Exception ex) {
            if (db.inTransaction())
                db.endTransaction();
            Dialog d = new Dialog(context.getApplicationContext());
            d.setTitle(ex.getMessage() + "second");

            ex.printStackTrace();
            d.show();
        }
    }
}
