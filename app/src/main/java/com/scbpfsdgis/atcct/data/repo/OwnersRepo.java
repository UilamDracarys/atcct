package com.scbpfsdgis.atcct.data.repo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.scbpfsdgis.atcct.data.DatabaseManager;
import com.scbpfsdgis.atcct.data.model.DBHelper;
import com.scbpfsdgis.atcct.data.model.Owners;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by William on 4/15/2019.
 */

public class OwnersRepo {

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public OwnersRepo() {
        Owners owners = new Owners();
    }

    public static String createOwnersTbl() {
        String query = "CREATE TABLE IF NOT EXISTS " + Owners.TABLE_OWNERS + " (" +
                Owners.COL_OWNERID + " TEXT," +
                Owners.COL_OWNERNAME + " TEXT, " +
                Owners.COL_OWNERMOB + " TEXT, " +
                Owners.COL_OWNEREMAIL + " TEXT, " +
                Owners.COL_OWNERADDRESS + " TEXT)";
        return query;
    }


    public Owners getOwnerByID(String id) {
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + Owners.TABLE_OWNERS + " WHERE " + Owners.COL_OWNERID + " = ?";
        System.out.println(selectQuery);

        Owners owners = new Owners();

        Cursor cursor = db.rawQuery(selectQuery, new String[]{id});

        if (cursor.moveToFirst()) {
            do {
                owners.setOwnerID(cursor.getString(cursor.getColumnIndex(Owners.COL_OWNERID)));
                owners.setOwnerName(cursor.getString(cursor.getColumnIndex(Owners.COL_OWNERNAME)));
                owners.setOwnerMobile(cursor.getString(cursor.getColumnIndex(Owners.COL_OWNERMOB)));
                owners.setOwnerEmail(cursor.getString(cursor.getColumnIndex(Owners.COL_OWNEREMAIL)));
                owners.setOwnerAddress(cursor.getString(cursor.getColumnIndex(Owners.COL_OWNERADDRESS)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return owners;
    }

    //Get List of Farms
    public ArrayList<HashMap<String, String>> getOwnersList() {

        //Open connection to read only
        //db = DatabaseManager.getInstance().openDatabase();
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + Owners.TABLE_OWNERS + " ORDER BY " + Owners.COL_OWNERNAME;

        System.out.println("OwnersListQuery: " + selectQuery);

        ArrayList<HashMap<String, String>> ownersList = new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, null);
        System.out.println("GetOwnersList: " + cursor.getCount());

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> owners = new HashMap<>();
                owners.put("ownerID", cursor.getString(cursor.getColumnIndex(Owners.COL_OWNERID)));
                owners.put("ownerName", cursor.getString(cursor.getColumnIndex(Owners.COL_OWNERNAME)));
                owners.put("ownerMobile", cursor.getString(cursor.getColumnIndex(Owners.COL_OWNERMOB)));
                ownersList.add(owners);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return ownersList;
    }

    public ArrayList<String> getOwnersForSpinner() {

        //Open connection to read only
        //db = DatabaseManager.getInstance().openDatabase();
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + Owners.TABLE_OWNERS + " ORDER BY " + Owners.COL_OWNERNAME;

        System.out.println("OwnersListQuery: " + selectQuery);

        ArrayList<String> ownersList = new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, null);
        System.out.println("GetOwnersList: " + cursor.getCount());

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                //HashMap<String, String> owners = new HashMap<>();
                String item = cursor.getString(cursor.getColumnIndex(Owners.COL_OWNERNAME)) + " [" +
                        "" + cursor.getString(cursor.getColumnIndex(Owners.COL_OWNERID)) + "]";
                ownersList.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return ownersList;
    }

    public String consolFarms(String ownerID) {
        String farms = "Available Farms: \n";
        FarmsRepo repo = new FarmsRepo();
        ArrayList<HashMap<String, String>> availFarms = repo.getFarmsListByOwner(ownerID, "A");
        ArrayList<HashMap<String, String>> nAvailFarms = repo.getFarmsListByOwner(ownerID, "-");
        int availFarmCount = availFarms.size();
        int nAvailFarmCount = nAvailFarms.size();
        String lineBreak = "\n";

        for (int i = 0; i < availFarmCount; i++) {
            if (i == availFarmCount - 1) {
                lineBreak = "";
            }
            farms += (i + 1) + ". " + availFarms.get(i).get("farmName") + " [" + availFarms.get(i).get("farmCode") + "] - " + availFarms.get(i).get("company") + lineBreak;
        }

        if (nAvailFarmCount > 0) {
            farms += "\nNot Available Farms: \n";
            lineBreak = "\n";
            for (int i = 0; i < nAvailFarmCount; i++) {
                if (i == nAvailFarmCount - 1) {
                    lineBreak = "";
                }
                farms += (i + 1) + ". " + nAvailFarms.get(i).get("farmName") + " [" + nAvailFarms.get(i).get("farmCode") + "] - " + nAvailFarms.get(i).get("company") + " " +
                        "[" + nAvailFarms.get(i).get("status") + "] " + nAvailFarms.get(i).get("remarks") + lineBreak;
            }
        }

        System.out.println(farms);
        return farms;
    }
}
