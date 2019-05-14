package com.scbpfsdgis.atcct;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.scbpfsdgis.atcct.data.CustomAdapter;
import com.scbpfsdgis.atcct.data.model.DBHelper;
import com.scbpfsdgis.atcct.data.model.Farms;
import com.scbpfsdgis.atcct.data.model.Owners;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLOutput;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int PERMISSION_REQUEST_WRITESTORAGE = 0;
    public static final int requestcode = 1;
    SQLiteDatabase db;
    DBHelper dbHelper;
    private View mLayout;
    String downloadURL = "https://drive.google.com/uc?authuser=0&id=15EfjKmsv511ehyhLelD_QteaoC5sX1OD&export=download";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();
        mLayout = findViewById(R.id.main_layout);
        initMenus();

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        delSigCache();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("Request Code: " + requestCode);
        if (data == null) {
            System.out.println("Cancelled");
            return;
        }
        switch (requestCode) {
            case requestcode:
                Uri uri = data.getData();
                String filepath = Utils.getActualPath(this, uri);
                System.out.println("CSV path: " + filepath);
                db = dbHelper.getWritableDatabase();
                String tableName = Farms.TABLE_MASTERTBL;
                db.execSQL("DELETE FROM " + tableName);
                try {
                    if (resultCode == RESULT_OK) {
                        try {

                            System.out.println("RESULT " + resultCode);
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
                                    String[] str = line.split(";", 9);  // defining 8 columns with null or blank field //values acceptance
                                    String farmCode = str[0];
                                    String farmName = str[1];
                                    String farmBase = str[2];
                                    String farmStatus = str[3];
                                    String ownerID = str[4];
                                    String ownerName = str[5];
                                    String ownerMobile = str[6];
                                    String ownerEmail = str[7];
                                    String ownerAddress = str[8];

                                    contentValues.put(Farms.COL_FARMCODE, farmCode);
                                    contentValues.put(Farms.COL_FARMNAME, farmName);
                                    contentValues.put(Farms.COL_BASE, farmBase);
                                    contentValues.put(Farms.COL_STATUS, farmStatus);
                                    contentValues.put(Farms.COL_OWNERID, ownerID);
                                    contentValues.put(Owners.COL_OWNERNAME, ownerName);
                                    contentValues.put(Owners.COL_OWNERMOB, ownerMobile);
                                    contentValues.put(Owners.COL_OWNEREMAIL, ownerEmail);
                                    contentValues.put(Owners.COL_OWNERADDRESS, ownerAddress);
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
                                    Owners.COL_OWNERADDRESS + ") " +
                                    "SELECT " + Farms.COL_OWNERID + ", " +
                                    Owners.COL_OWNERNAME + ", " +
                                    Owners.COL_OWNERMOB + ", " +
                                    Owners.COL_OWNEREMAIL + ", " +
                                    Owners.COL_OWNERADDRESS + " FROM " + Farms.TABLE_MASTERTBL +
                                    " GROUP BY " + Farms.COL_OWNERID + " ";
                            System.out.println(insertOwners);
                            db.execSQL(insertOwners);
                            db.execSQL("DELETE FROM " + Farms.TABLE_MASTERTBL);
                            db.setTransactionSuccessful();
                            db.endTransaction();
                            Toast.makeText(this, "Data import successful.", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            System.out.println(e.getMessage() + "first");
                        }
                    } else {
                        if (db.inTransaction())
                            db.endTransaction();
                        Dialog d = new Dialog(this);
                        d.setTitle("Only CSV files allowed");
                        d.show();
                    }
                } catch (Exception ex) {
                    if (db.inTransaction())
                        db.endTransaction();

                    Dialog d = new Dialog(this);
                    d.setTitle(ex.getMessage().toString() + "second");
                    d.show();
                }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_REQUEST_WRITESTORAGE) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                Snackbar.make(mLayout, "Storage access granted.",
                        Snackbar.LENGTH_SHORT)
                        .show();
            } else {
                // Permission request was denied.
                Snackbar.make(mLayout, "Storage access denied.",
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
        // END_INCLUDE(onRequestPermissionsResult)
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            Snackbar.make(mLayout, "Permission to access storage is required.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_WRITESTORAGE);
                }
            }).show();
        } else {
            Snackbar.make(mLayout,
                    "Permission is not available. Requesting storage permission.",
                    Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_WRITESTORAGE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the save_cancel; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_importdata:
                importData();
                return true;
            case R.id.action_downloadcsv:
                if (isConnectingToInternet()) {
                    new DownloadTask(MainActivity.this, downloadURL);
                    return true;
                } else {
                    Toast.makeText(this, "You are not connected to the internet.", Toast.LENGTH_SHORT).show();
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void delSigCache() {
        File sigDir = new File(getCacheDir(), "");
        if (sigDir.isDirectory()) {
            String[] children = sigDir.list();
            for (int i = 0; i < children.length; i++) {
                new File(sigDir, children[i]).delete();
            }
            Log.v("CLR", "Signature cache cleared.");
        }
    }

    private void importData() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available
            Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
            fileintent.setType("text/*");
            try {
                startActivityForResult(Intent.createChooser(fileintent, "Open CSV"), requestcode);
            } catch (ActivityNotFoundException e) {
                System.out.println("Not found");
            }
        } else {
            // Permission is missing and must be requested.
            requestStoragePermission();
        }
    }

    private void initMenus() {
        ListView menus = findViewById(R.id.menuList);
        String[] textString = {"Farms List", "Planters List", "ATCCTs"};
        String[] menuPreviews = {"No. of Farms", "No. of Planters", "No. of ATCCTs"};

        int[] drawableIds = {R.drawable.ic_farms, R.drawable.ic_people, R.drawable.ic_atccts};
        int[] drawableArrows = {R.drawable.ic_arrow, R.drawable.ic_arrow, R.drawable.ic_arrow};
        CustomAdapter adapter = new CustomAdapter(this, textString, menuPreviews, drawableIds, drawableArrows);
        menus.setAdapter(adapter);

        menus.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        showFarms();
                        return;
                    case 1:
                        showOwners();
                        return;
                    case 2:
                        showATCCTs();
                        return;
                    default:
                }
            }

        });
    }

    public void showFarms() {
        Intent intent = new Intent(this, FarmsList.class);
        intent.putExtra("action", "Farms List");
        startActivity(intent);
    }

    public void showOwners() {
        Intent intent = new Intent(this, OwnersList.class);
        intent.putExtra("action", "Owners List");
        startActivity(intent);
    }

    public void showATCCTs() {
        Intent intent = new Intent(this, ATCCTList.class);
        intent.putExtra("action", "ATCCT List");
        startActivity(intent);
    }

    private boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager
                .getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

}


