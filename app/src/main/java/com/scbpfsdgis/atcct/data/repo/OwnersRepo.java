package com.scbpfsdgis.atcct.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.common.internal.BaseGmsClient;
import com.scbpfsdgis.atcct.data.DatabaseManager;
import com.scbpfsdgis.atcct.data.model.DBHelper;
import com.scbpfsdgis.atcct.data.model.Owners;

import java.text.DecimalFormat;
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
                Owners.COL_OWNERADDRESS + " TEXT, " +
                Owners.COL_BASES + " TEXT)";
        return query;
    }

    public static String createOwnerChgTbl() {
        String query = "CREATE TABLE IF NOT EXISTS " + Owners.TABLE_OWNERS_CHANGES + " (" +
                Owners.COL_OWNERID + " TEXT," +
                Owners.COL_OWNERNAME + " TEXT, " +
                Owners.COL_OWNERMOB + " TEXT, " +
                Owners.COL_OWNEREMAIL + " TEXT, " +
                Owners.COL_OWNERADDRESS + " TEXT)";
        return query;
    }

    public void insert(Owners owner, String table) {
        String tbl;
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (table.equalsIgnoreCase("Owner")) {
            tbl = Owners.TABLE_OWNERS;
        } else {
            tbl = Owners.TABLE_OWNERS_CHANGES;
        }

        values.put(Owners.COL_OWNERID, owner.getOwnerID());
        values.put(Owners.COL_OWNERNAME, owner.getOwnerName());
        values.put(Owners.COL_OWNERMOB, owner.getOwnerMobile());
        values.put(Owners.COL_OWNEREMAIL, owner.getOwnerEmail());
        values.put(Owners.COL_OWNERADDRESS, owner.getOwnerAddress());
        if (table.equalsIgnoreCase("Owner")) {
            values.put(Owners.COL_BASES, owner.getOwnerBases());
        }

        // Inserting Row

        db.insert(tbl, null, values);
        db.close();
    }

    public void updateChange(Owners chgs) {
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Owners.COL_OWNERID, chgs.getOwnerID());
        values.put(Owners.COL_OWNERNAME, chgs.getOwnerName());
        values.put(Owners.COL_OWNERMOB, chgs.getOwnerMobile());
        values.put(Owners.COL_OWNEREMAIL, chgs.getOwnerEmail());
        values.put(Owners.COL_OWNERADDRESS, chgs.getOwnerAddress());

        db.update(Owners.TABLE_OWNERS_CHANGES, values, Owners.COL_OWNERID + "= ? ", new String[]{String.valueOf(chgs.getOwnerID())});
        db.close(); // Closing database connection
    }

    public void update(Owners chgs) {
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Owners.COL_OWNERID, chgs.getOwnerID());
        values.put(Owners.COL_OWNERNAME, chgs.getOwnerName());
        values.put(Owners.COL_OWNERMOB, chgs.getOwnerMobile());
        values.put(Owners.COL_OWNEREMAIL, chgs.getOwnerEmail());
        values.put(Owners.COL_OWNERADDRESS, chgs.getOwnerAddress());

        db.update(Owners.TABLE_OWNERS, values, Owners.COL_OWNERID + "= ? ", new String[]{String.valueOf(chgs.getOwnerID())});
        db.close(); // Closing database connection
    }


    public Owners getOwnerByID(String id, String qryType) {
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();
        //String selectQuery = "SELECT * FROM " + Owners.TABLE_OWNERS + " WHERE " + Owners.COL_OWNERID + " =?";
        String selMergeQry = "SELECT O." + Owners.COL_OWNERID + " as OwnerID,\n" +
                "CASE\n" +
                "WHEN C." + Owners.COL_OWNERNAME + " IS NULL OR C." + Owners.COL_OWNERNAME + " = '' THEN O." + Owners.COL_OWNERNAME + "\n" +
                "ELSE C." + Owners.COL_OWNERNAME + "\n" +
                "END OwnerName,\n" +
                "CASE \n" +
                "WHEN C." + Owners.COL_OWNERMOB + " IS NULL OR C." + Owners.COL_OWNERMOB + " = '' THEN O." + Owners.COL_OWNERMOB + "\n" +
                "ELSE C." + Owners.COL_OWNERMOB + "\n" +
                "END OwnerMobile,\n" +
                "CASE \n" +
                "WHEN C." + Owners.COL_OWNEREMAIL + " IS NULL OR C." + Owners.COL_OWNEREMAIL + " = '' THEN O." + Owners.COL_OWNEREMAIL + "\n" +
                "ELSE C." + Owners.COL_OWNEREMAIL + "\n" +
                "END OwnerEmail,\n" +
                "CASE \n" +
                "WHEN C." + Owners.COL_OWNERADDRESS + " IS NULL OR C." + Owners.COL_OWNERADDRESS + " = '' THEN O." + Owners.COL_OWNERADDRESS + "\n" +
                "ELSE C." + Owners.COL_OWNERADDRESS + "\n" +
                "END OwnerAddress, " +
                "O." + Owners.COL_BASES + " as OwnerBases\n" +
                "FROM\n" +
                Owners.TABLE_OWNERS + " O LEFT JOIN " + Owners.TABLE_OWNERS_CHANGES + " C\n" +
                "ON O." + Owners.COL_OWNERID + " = C." + Owners.COL_OWNERID + "\n" +
                "WHERE OwnerID = ?";

        String selOrigQry = "SELECT " + Owners.COL_OWNERID + " as OwnerID,\n" +
                Owners.COL_OWNERNAME + " as OwnerName,\n" +
                Owners.COL_OWNERMOB + " as OwnerMobile,\n" +
                Owners.COL_OWNEREMAIL + " as OwnerEmail,\n" +
                Owners.COL_OWNERADDRESS + " as OwnerAddress, \n" +
                Owners.COL_BASES + " as OwnerBases FROM " +
                Owners.TABLE_OWNERS + " WHERE OwnerID =?";


        System.out.println(selMergeQry);
        String query = "";
        if (!qryType.equalsIgnoreCase("M")) {
            query = selOrigQry;
        } else {
            query = selMergeQry;
        }

        Owners owners = new Owners();

        Cursor cursor = db.rawQuery(query, new String[]{id});

        if (cursor.moveToFirst()) {
            do {
                owners.setOwnerID(cursor.getString(cursor.getColumnIndex("OwnerID")));
                owners.setOwnerName(cursor.getString(cursor.getColumnIndex("OwnerName")));
                owners.setOwnerMobile(cursor.getString(cursor.getColumnIndex("OwnerMobile")));
                owners.setOwnerEmail(cursor.getString(cursor.getColumnIndex("OwnerEmail")));
                owners.setOwnerAddress(cursor.getString(cursor.getColumnIndex("OwnerAddress")));
                owners.setOwnerBases(cursor.getString(cursor.getColumnIndex("OwnerBases")));
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
        //String selectQuery = "SELECT * FROM " + Owners.TABLE_OWNERS + " ORDER BY " + Owners.COL_OWNERNAME;

        String selectQuery = "SELECT O." + Owners.COL_OWNERID + " as OwnerID,\n" +
                "CASE\n" +
                "WHEN C." + Owners.COL_OWNERNAME + " IS NULL OR C." + Owners.COL_OWNERNAME + " = '' THEN O." + Owners.COL_OWNERNAME + "\n" +
                "ELSE C." + Owners.COL_OWNERNAME + "\n" +
                "END OwnerName,\n" +
                "CASE \n" +
                "WHEN C." + Owners.COL_OWNERMOB + " IS NULL OR C." + Owners.COL_OWNERMOB + " = '' THEN O." + Owners.COL_OWNERMOB + "\n" +
                "ELSE C." + Owners.COL_OWNERMOB + "\n" +
                "END OwnerMobile,\n" +
                "CASE \n" +
                "WHEN C." + Owners.COL_OWNEREMAIL + " IS NULL OR C." + Owners.COL_OWNEREMAIL + " = '' THEN O." + Owners.COL_OWNEREMAIL + "\n" +
                "ELSE C." + Owners.COL_OWNEREMAIL + "\n" +
                "END OwnerEmail,\n" +
                "CASE \n" +
                "WHEN C." + Owners.COL_OWNERADDRESS + " IS NULL OR C." + Owners.COL_OWNERADDRESS + " = '' THEN O." + Owners.COL_OWNERADDRESS + "\n" +
                "ELSE C." + Owners.COL_OWNERADDRESS + "\n" +
                "END OwnerAddress\n" +
                "FROM\n" +
                Owners.TABLE_OWNERS + " O LEFT JOIN " + Owners.TABLE_OWNERS_CHANGES + " C\n" +
                "ON O." + Owners.COL_OWNERID + " = C." + Owners.COL_OWNERID + "\n" +
                "ORDER BY OwnerName ASC";
        System.out.println("OwnersListQuery: " + selectQuery);

        ArrayList<HashMap<String, String>> ownersList = new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, null);
        System.out.println("GetOwnersList: " + cursor.getCount());

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> owners = new HashMap<>();
                owners.put("ownerID", cursor.getString(cursor.getColumnIndex("OwnerID")));
                owners.put("ownerName", cursor.getString(cursor.getColumnIndex("OwnerName")));
                owners.put("ownerMobile", cursor.getString(cursor.getColumnIndex("OwnerMobile")));
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
        String selectQuery = "SELECT O." + Owners.COL_OWNERID + " as OwnerID,\n" +
                "CASE\n" +
                "WHEN C." + Owners.COL_OWNERNAME + " IS NULL OR C." + Owners.COL_OWNERNAME + " = '' THEN O." + Owners.COL_OWNERNAME + "\n" +
                "ELSE C." + Owners.COL_OWNERNAME + "\n" +
                "END OwnerName,\n" +
                "CASE \n" +
                "WHEN C." + Owners.COL_OWNERMOB + " IS NULL OR C." + Owners.COL_OWNERMOB + " = '' THEN O." + Owners.COL_OWNERMOB + "\n" +
                "ELSE C." + Owners.COL_OWNERMOB + "\n" +
                "END OwnerMobile,\n" +
                "CASE \n" +
                "WHEN C." + Owners.COL_OWNEREMAIL + " IS NULL OR C." + Owners.COL_OWNEREMAIL + " = '' THEN O." + Owners.COL_OWNEREMAIL + "\n" +
                "ELSE C." + Owners.COL_OWNEREMAIL + "\n" +
                "END OwnerEmail,\n" +
                "CASE \n" +
                "WHEN C." + Owners.COL_OWNERADDRESS + " IS NULL OR C." + Owners.COL_OWNERADDRESS + " = '' THEN O." + Owners.COL_OWNERADDRESS + "\n" +
                "ELSE C." + Owners.COL_OWNERADDRESS + "\n" +
                "END OwnerAddress\n" +
                "FROM\n" +
                Owners.TABLE_OWNERS + " O LEFT JOIN " + Owners.TABLE_OWNERS_CHANGES + " C\n" +
                "ON O." + Owners.COL_OWNERID + " = C." + Owners.COL_OWNERID + "\n" +
                "ORDER BY OwnerName ASC";
        System.out.println("OwnersListQuery: " + selectQuery);

        ArrayList<String> ownersList = new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, null);
        System.out.println("GetOwnersList: " + cursor.getCount());

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                //HashMap<String, String> owners = new HashMap<>();
                String item = cursor.getString(cursor.getColumnIndex("OwnerName")) + " [" +
                        "" + cursor.getString(cursor.getColumnIndex("OwnerID")) + "]";
                ownersList.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return ownersList;
    }

    public String consolFarms(String ownerID) {
        StringBuilder farms = new StringBuilder();
        FarmsRepo repo = new FarmsRepo();
        ArrayList<HashMap<String, String>> availFarms = repo.getFarmsListByOwner(ownerID, "A");
        ArrayList<HashMap<String, String>> nAvailFarms = repo.getFarmsListByOwner(ownerID, "-");
        int availFarmCount = availFarms.size();
        int nAvailFarmCount = nAvailFarms.size();
        String lineBreak = "\n";

        if (availFarmCount > 0) {
            farms.append("Available Farms: \n");
            for (int i = 0; i < availFarmCount; i++) {
                if (i == availFarmCount - 1) {
                    lineBreak = "";
                }
                farms.append(i + 1).append(". ").append(availFarms.get(i).get("farmName")).append(" [").append(availFarms.get(i).get("farmCode")).append("] - ").append(availFarms.get(i).get("company")).append(lineBreak);
            }
        }

        if (nAvailFarmCount > 0) {
            if (availFarmCount > 0) {
                farms.append("\n\n");
            }
            farms.append("Not Available Farms:\n");
            lineBreak = "\n";
            for (int i = 0; i < nAvailFarmCount; i++) {
                if (i == nAvailFarmCount - 1) {
                    lineBreak = "";
                }
                farms.append(i + 1).append(". ").append(nAvailFarms.get(i).get("farmName")).append(" [").append(nAvailFarms.get(i).get("farmCode")).append("] - ").append(nAvailFarms.get(i).get("company")).append(" ").append("[").append(nAvailFarms.get(i).get("status")).append("] ").append(nAvailFarms.get(i).get("remarks")).append(lineBreak);
            }
        }

        if (availFarmCount == 0 && nAvailFarmCount == 0) {
            farms.setLength(0);
            farms.append("*** NO FARMS RECORDED ***");
        }

        OwnersRepo oRepo = new OwnersRepo();
        Owners owner = oRepo.getOwnerByID(ownerID, "M");
        if (!ownerID.startsWith("N-")) {
            if (Integer.parseInt(owner.getOwnerBases()) > 1) {
                if (availFarmCount > 0 || nAvailFarmCount > 0) {
                    farms.append("\n*This owner has farms in other companies.");
                }
            }
        }

        System.out.println(farms);
        return farms.toString();
    }

    public Boolean isChgExist(String ownerID) {
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + Owners.TABLE_OWNERS_CHANGES + "" +
                " WHERE " + Owners.COL_OWNERID + " =?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{ownerID});

        if (cursor.moveToFirst()) {
            return true;
        } else {
            return false;
        }

    }

    public String newOwnerID() {
        String newOwnerID;
        DecimalFormat id = new DecimalFormat("00000");
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT MAX(" + Owners.COL_OWNERID + ") AS MAX_ID FROM " + Owners.TABLE_OWNERS;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            String tempID = "";
            if (cursor.getString(cursor.getColumnIndex("MAX_ID")).startsWith("N-")) {
                tempID = cursor.getString(cursor.getColumnIndex("MAX_ID")).substring(2);

            } else {
                tempID = cursor.getString(cursor.getColumnIndex("MAX_ID"));
            }
            newOwnerID = "N-" + id.format(Integer.parseInt(tempID) + 1);
        } else {
            newOwnerID = "0";

        }

        db.close();
        return newOwnerID;
    }

    public int getOwnerCount(String table) {
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + table;
        int count = 0;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        db.close();
        return count;
    }

    public String getChgQuery() {
        return "SELECT O." + Owners.COL_OWNERID + " AS OwnerID,\n" +
                "O." + Owners.COL_OWNERNAME + " AS \"Owner Name\",\n" +
                "C." + Owners.COL_OWNERNAME + " AS \"New Owner Name\",\n" +
                "C." + Owners.COL_OWNERMOB + " AS \"New Mobile No.\",\n" +
                "C." + Owners.COL_OWNEREMAIL + " AS \"New Email\",\n" +
                "C." + Owners.COL_OWNERADDRESS + " AS \"New Address\"\n" +
                "FROM " + Owners.TABLE_OWNERS + " O JOIN " + Owners.TABLE_OWNERS_CHANGES + " C\n" +
                "ON O." + Owners.COL_OWNERID + " = C." + Owners.COL_OWNERID;
    }

}
