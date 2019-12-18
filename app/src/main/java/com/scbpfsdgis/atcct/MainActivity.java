package com.scbpfsdgis.atcct;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.scbpfsdgis.atcct.Utils.CSVWriter;
import com.scbpfsdgis.atcct.Utils.DownloadTask;
import com.scbpfsdgis.atcct.Utils.Utils;
import com.scbpfsdgis.atcct.data.CustomAdapter;
import com.scbpfsdgis.atcct.data.DatabaseManager;
import com.scbpfsdgis.atcct.data.model.DBHelper;
import com.scbpfsdgis.atcct.data.model.Farms;
import com.scbpfsdgis.atcct.data.model.Owners;
import com.scbpfsdgis.atcct.data.repo.ATCCRepo;
import com.scbpfsdgis.atcct.data.repo.FIRRepo;
import com.scbpfsdgis.atcct.data.repo.FarmsRepo;
import com.scbpfsdgis.atcct.data.repo.OwnersRepo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    public static final int PERMISSION_REQUEST_WRITESTORAGE = 0;
    public static final int requestcode = 1;
    private String action = "";
    SQLiteDatabase db;
    DBHelper dbHelper;
    private View mLayout;
    AlertDialog alertDialog1;
    CharSequence[] values = {"SCBP", "SNBP", "NNBP"};
    String nnbpDataURL = "https://drive.google.com/uc?authuser=0&id=15EfjKmsv511ehyhLelD_QteaoC5sX1OD&export=download";
    String scbpDataURL = "https://drive.google.com/uc?authuser=0&id=1L8JoYCRAmKAaf2SFjvLXPk-Zfu6LhfHN&export=download";
    String snbpDataURL = "https://drive.google.com/uc?authuser=0&id=11ux9AxUsZTaPpcO2XGWCUK5APPcOTVKJ&export=download";
    //String nnbpDataURL = "https://drive.google.com//uc?authuser=0&id=1PxedAjqHh6QEsdSC5Y8x_3xpm-Jx47m4&export=download";


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
                                    Owners.COL_BASES + ") " +
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
                    d.setTitle(ex.getMessage() + "second");
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
                if (action.equalsIgnoreCase("backup")) {
                    backupDatabase();
                } else if (action.equalsIgnoreCase("download")) {
                    chooseCompany();
                } else if (action.equalsIgnoreCase("importcsv")) {
                    /*importFromCSV();*/
                    Toast.makeText(getApplicationContext(), "This function is no longer supported. Please go online and download the data. Thank you.", Toast.LENGTH_LONG).show();
                } else if (action.equalsIgnoreCase("exportchg")) {
                    exportChanges();
                }
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
    public void onRestart() {
        super.onRestart();
        initMenus();
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
                Toast.makeText(getApplicationContext(), "This function is no longer supported. Please go online and download the data. Thank you.", Toast.LENGTH_LONG).show();
                /*if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                  *//*  importFromCSV();*//*
                    return true;
                } else {
                    Toast.makeText(getApplicationContext(), "This function is no longer supported. Please go online and download the data. Thank you.", Toast.LENGTH_LONG).show();
                    *//*action = "importcsv";
                    requestStoragePermission();*//*
                    return true;
                }*/
                return true;
            case R.id.action_downloadcsv:
                if (isConnectingToInternet()) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        chooseCompany();
                        return true;
                    } else {
                        action = "download";
                        requestStoragePermission();
                        return true;
                    }
                } else {
                    Toast.makeText(this, "You are not connected to the internet.", Toast.LENGTH_SHORT).show();
                    return true;
                }
            case R.id.action_exportChgList:
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    exportChanges();
                    return true;
                } else {
                    action = "exportchg";
                    requestStoragePermission();
                    return true;
                }

            case R.id.action_backupdb:
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    backupDatabase();
                    return true;
                } else {
                    action = "backup";
                    requestStoragePermission();
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void chooseCompany() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Select Company");
        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        new DownloadTask(MainActivity.this, scbpDataURL);
                        break;
                    case 1:
                        new DownloadTask(MainActivity.this, snbpDataURL);
                        break;
                    case 2:
                        new DownloadTask(MainActivity.this, nnbpDataURL);
                        break;
                }
                alertDialog1.dismiss();

            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();
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

    private void importFromCSV() {
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
        String[] textString = {"Farms List", "Planters List", "ATCCTs", "Field Inspection Reports"};
        String[] menuPreviews = {getFarmsSubtitle(), getOwnersSubtitle(), getATCCTSubtitle(), getFIRSubtitle()};

        int[] drawableIds = {R.drawable.ic_farms,
                R.drawable.ic_people,
                R.drawable.ic_atccts,
                R.drawable.ic_atccts};
        int[] drawableArrows = {R.drawable.ic_arrow,
                R.drawable.ic_arrow,
                R.drawable.ic_arrow,
                R.drawable.ic_arrow};
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
                    case 3:
                        showFIRs();
                        return;
                    default:
                }
            }

        });
    }

    private String getFarmsSubtitle() {
        FarmsRepo repo = new FarmsRepo();
        StringBuilder sb = new StringBuilder();
        sb.append("Total Farms: " + repo.getFarmCount(Farms.TABLE_FARMS) + "\n");
        sb.append("Farms with Edits: " + repo.getFarmCount(Farms.TABLE_FARM_CHANGES));
        return sb.toString();
    }

    private String getOwnersSubtitle() {
        OwnersRepo repo = new OwnersRepo();
        StringBuilder sb = new StringBuilder();
        sb.append("Total Planters: " + repo.getOwnerCount(Owners.TABLE_OWNERS) + "\n");
        sb.append("Planters with Edits: " + repo.getOwnerCount(Owners.TABLE_OWNERS_CHANGES));
        return sb.toString();
    }

    private String getATCCTSubtitle() {
        ATCCRepo repo = new ATCCRepo();
        StringBuilder sb = new StringBuilder();
        sb.append("Total ATCCTs: " + repo.getATCCTCount("") + "\n");
        sb.append("Signed: " + repo.getATCCTCount("Signed") + "\n");
        sb.append("To Sign: " + repo.getATCCTCount("To Sign"));
        return sb.toString();
    }

    private String getFIRSubtitle() {
        FIRRepo repo = new FIRRepo();
        StringBuilder sb = new StringBuilder();
        sb.append("Total FIRs: " + repo.getFIRCount() );
        return sb.toString();
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

    public void showFIRs() {
        Intent intent = new Intent(this, FIRList.class);
        intent.putExtra("action", "FIR List");
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

    private void backupDatabase() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Backup");
        builder.setMessage("Do you want to create a backup of the database? \nEnter password and click OK to continue.");

        TextInputLayout textInputLayout = new TextInputLayout(this);
        textInputLayout.setPasswordVisibilityToggleEnabled(true);

        final TextInputEditText password = new TextInputEditText(this);
        textInputLayout.setPadding(50, 20, 50, 50);
        password.setHint("Password");
        password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        textInputLayout.addView(password);

        builder.setView(textInputLayout);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (password.length() < 6) {
                    if (password.length() == 0) {
                        Toast.makeText(getApplicationContext(), "Password for backup is required.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Password should be a minimum of 6 characters.", Toast.LENGTH_SHORT).show();
                    }
                    return;
                } else {
                    DatabaseManager db = new DatabaseManager();
                    try {
                        db.backupDB(getApplicationContext(), "ATCCT.db", Utils.mainDir + Utils.backupSubDir, password.getText().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create();
        builder.show();
    }

    private void exportChanges() {
        final File chgListDir = new File(Environment.getExternalStorageDirectory() + Utils.mainDir + Utils.chgListSubDir, "");
        if (!chgListDir.exists()) {
            chgListDir.mkdirs();
        }

        FarmsRepo farmsRepo = new FarmsRepo();
        OwnersRepo ownersRepo = new OwnersRepo();

        final DateFormat fileDF = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        final String strDate = fileDF.format(new Date());

        final String farmChgFilename = "FARM_CHGS_" + strDate + ".csv";
        final String planterChgFilename = "PLANTER_CGHS_" + strDate + ".csv";
        final File farmChanges = new File(chgListDir, farmChgFilename);
        final File planterChanges = new File(chgListDir, planterChgFilename);

        int farmChgCount = farmsRepo.getFarmCount(Farms.TABLE_FARM_CHANGES);
        int planterChgCount = ownersRepo.getOwnerCount(Owners.TABLE_OWNERS_CHANGES);

        String changes = "";
        if (farmChgCount > 0 && planterChgCount == 0) {
            changes = "Farms Only";
        } else if (farmChgCount == 0 && planterChgCount > 0) {
            changes = "Planters Only";
        } else if (farmChgCount > 0 && planterChgCount > 0) {
            changes = "Both";
        } else {
            changes = "None";
        }

        String message = "";
        switch (changes) {
            case "Farms Only":
                message = "Farm changes exported. No planter changes.";
                csvWriter(farmChanges, farmsRepo.getChgQuery());
                break;
            case "Planters Only":
                message = "Planter changes exported. No farm changes.";
                csvWriter(planterChanges, ownersRepo.getChgQuery());
                break;
            case "Both":
                message = "Changes successfully exported.";
                csvWriter(farmChanges, farmsRepo.getChgQuery());
                csvWriter(planterChanges, ownersRepo.getChgQuery());
                break;
            default:
                message = "No changes to export";
                break;
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void csvWriter(File file, String query) {
        DBHelper dbhelper = new DBHelper();
        SQLiteDatabase db = dbhelper.getReadableDatabase();

        Cursor curCSV = db.rawQuery(query, null);
        try {
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                String arrStr[] = new String[curCSV.getColumnCount()];
                for (int i = 0; i < curCSV.getColumnCount(); i++) {
                    arrStr[i] = curCSV.getString(i);
                }
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
        } catch (Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }


}


