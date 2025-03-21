package com.scbpfsdgis.atcct.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.scbpfsdgis.atcct.data.DatabaseManager;
import com.scbpfsdgis.atcct.data.model.DBHelper;
import com.scbpfsdgis.atcct.data.model.Farms;
import com.scbpfsdgis.atcct.data.model.Owners;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by William on 12/17/2018.
 */

public class FarmsRepo {
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public FarmsRepo() {
        Farms farms = new Farms();
    }

    public static String createFarmsTbl() {
        String query = "CREATE TABLE IF NOT EXISTS " + Farms.TABLE_FARMS + " (" +
                Farms.COL_FARMCODE + " TEXT, " +
                Farms.COL_FARMNAME + " TEXT, " +
                Farms.COL_BASE + " TEXT, " +
                Farms.COL_STATUS + " TEXT, " +
                Farms.COL_OWNERID + " TEXT)";
        return query;
    }

    public static String createMasterTbl() {
        String query = "CREATE TABLE IF NOT EXISTS " + Farms.TABLE_MASTERTBL + " (" +
                Farms.COL_FARMCODE + " TEXT, " +
                Farms.COL_FARMNAME + " TEXT, " +
                Farms.COL_BASE + " TEXT, " +
                Farms.COL_STATUS + " TEXT, " +
                Farms.COL_OWNERID + " TEXT, " +
                Owners.COL_OWNERNAME + " TEXT, " +
                Owners.COL_OWNERMOB + " TEXT, " +
                Owners.COL_OWNEREMAIL + " TEXT, " +
                Owners.COL_OWNERADDRESS + " TEXT, " +
                Owners.COL_BASES + " TEXT)";
        return query;
    }

    public static String createFChgsTable() {
        String query = "CREATE TABLE IF NOT EXISTS " + Farms.TABLE_FARM_CHANGES + " (" +
                Farms.COL_FARMCODE + " TEXT, " +
                Farms.COL_FARMNAME + " TEXT, " +
                Farms.COL_BASE + " TEXT, " +
                Farms.COL_STATUS + " TEXT, " +
                Farms.COL_OWNERID + " TEXT, " +
                Farms.COL_REMARKS + " TEXT)";
        return query;
    }

    public void insertChange(Farms chgs) {
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Farms.COL_FARMCODE, chgs.getFarmCode());
        values.put(Farms.COL_FARMNAME, chgs.getFarmName());
        values.put(Farms.COL_BASE, chgs.getFarmBase());
        values.put(Farms.COL_STATUS, chgs.getFarmStatus());
        values.put(Farms.COL_OWNERID, chgs.getFarmOwnerID());
        values.put(Farms.COL_REMARKS, chgs.getFarmRemarks());

        // Inserting Row
        db.insert(Farms.TABLE_FARM_CHANGES, null, values);
        db.close();
    }

    public void updateChange(Farms chgs) {
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Farms.COL_FARMCODE, chgs.getFarmCode());
        values.put(Farms.COL_FARMNAME, chgs.getFarmName());
        values.put(Farms.COL_BASE, chgs.getFarmBase());
        values.put(Farms.COL_STATUS, chgs.getFarmStatus());
        values.put(Farms.COL_OWNERID, chgs.getFarmOwnerID());
        values.put(Farms.COL_REMARKS, chgs.getFarmRemarks());

        db.update(Farms.TABLE_FARM_CHANGES, values, Farms.COL_FARMCODE + "= ? ", new String[]{String.valueOf(chgs.getFarmCode())});
        db.close(); // Closing database connection
    }

    public ArrayList<HashMap<String, String>> getFarmsListByOwner(String ownerID, String avail) {
        //Open connection to read only
        //db = DatabaseManager.getInstance().openDatabase();
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();

        String statusFilter = "";
        if (avail.equalsIgnoreCase("A")) {
            statusFilter = "STATUS = 'A'";
        } else {
            statusFilter = "STATUS <> 'A'";
        }

        String selectQuery = "SELECT M." + Farms.COL_FARMCODE + " AS FarmCode,\n" +
                "CASE \n" +
                "WHEN C." + Farms.COL_FARMNAME + " IS NULL OR C." + Farms.COL_FARMNAME + " = '' THEN M." + Farms.COL_FARMNAME + "\n" +
                "ELSE C." + Farms.COL_FARMNAME + "\n" +
                "END FarmName,\n" +
                "CASE \n" +
                "WHEN C." + Farms.COL_BASE + " IS NULL OR C." + Farms.COL_BASE + " = '' THEN M." + Farms.COL_BASE + "\n" +
                "ELSE C." + Farms.COL_BASE + "\n" +
                "END Base,\n" +
                "CASE\n" +
                "WHEN C." + Farms.COL_STATUS + " IS NULL OR C." + Farms.COL_STATUS + " = '' THEN M." + Farms.COL_STATUS + "\n" +
                "ELSE C." + Farms.COL_STATUS + "\n" +
                "END Status,\n" +
                "CASE\n" +
                "WHEN C." + Farms.COL_OWNERID + " IS NULL OR C." + Farms.COL_OWNERID + " = '' THEN M." + Farms.COL_OWNERID + "\n" +
                "ELSE C." + Farms.COL_OWNERID + "\n" +
                "END OwnerID,\n" +
                "CASE \n" +
                "WHEN C." + Farms.COL_OWNERID + " IS NULL OR C." + Farms.COL_OWNERID + " = '' THEN (SELECT " + Owners.COL_OWNERNAME + " FROM " + Owners.TABLE_OWNERS + " WHERE " + Owners.COL_OWNERID + " = M." + Farms.COL_OWNERID + ")\n" +
                "ELSE (SELECT " + Owners.COL_OWNERNAME + " FROM " + Owners.TABLE_OWNERS + " WHERE " + Owners.COL_OWNERID + " = C." + Farms.COL_OWNERID + ")\n" +
                "END OwnerName," +
                "C." + Farms.COL_REMARKS + " As Remarks " +
                "FROM " + Farms.TABLE_FARMS + " M LEFT JOIN " + Farms.TABLE_FARM_CHANGES + " C\n" +
                "ON M." + Farms.COL_FARMCODE + " = C." + Farms.COL_FARMCODE + "\n" +
                "WHERE OwnerID = '" + ownerID + "' \n" +
                "AND " + statusFilter + "\n" +
                "ORDER BY FarmName";

        ArrayList<HashMap<String, String>> farmsList = new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> farms = new HashMap<>();
                farms.put("farmCode", cursor.getString(cursor.getColumnIndex("FarmCode")));
                farms.put("farmName", cursor.getString(cursor.getColumnIndex("FarmName")));
                farms.put("company", cursor.getString(cursor.getColumnIndex("Base")));
                farms.put("status", cursor.getString(cursor.getColumnIndex("Status")));
                farms.put("remarks", cursor.getString(cursor.getColumnIndex("Remarks")));
                farmsList.add(farms);

            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return farmsList;
    }

    //Get List of Farms
    public ArrayList<HashMap<String, String>> getFarmsList() {

        //Open connection to read only
        //db = DatabaseManager.getInstance().openDatabase();
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT M." + Farms.COL_FARMCODE + " AS FarmCode,\n" +
                "CASE \n" +
                "WHEN C." + Farms.COL_FARMNAME + " IS NULL OR C." + Farms.COL_FARMNAME + " = '' THEN M." + Farms.COL_FARMNAME + "\n" +
                "ELSE C." + Farms.COL_FARMNAME + "\n" +
                "END FarmName,\n" +
                "CASE \n" +
                "WHEN C." + Farms.COL_BASE + " IS NULL OR C." + Farms.COL_BASE + " = '' THEN M." + Farms.COL_BASE + "\n" +
                "ELSE C." + Farms.COL_BASE + "\n" +
                "END Base,\n" +
                "CASE\n" +
                "WHEN C." + Farms.COL_STATUS + " IS NULL OR C." + Farms.COL_STATUS + " = '' THEN M." + Farms.COL_STATUS + "\n" +
                "ELSE C." + Farms.COL_STATUS + "\n" +
                "END Status,\n" +
                "CASE\n" +
                "WHEN C." + Farms.COL_OWNERID + " IS NULL OR C." + Farms.COL_OWNERID + " = '' THEN M." + Farms.COL_OWNERID + "\n" +
                "ELSE C." + Farms.COL_OWNERID + "\n" +
                "END OwnerID,\n" +
                "CASE \n" +
                "WHEN C." + Farms.COL_OWNERID + " IS NULL OR C." + Farms.COL_OWNERID + " = '' THEN (SELECT " + Owners.COL_OWNERNAME + " FROM " + Owners.TABLE_OWNERS + " WHERE " + Owners.COL_OWNERID + " = M." + Farms.COL_OWNERID + ")\n" +
                "ELSE (SELECT " + Owners.COL_OWNERNAME + " FROM " + Owners.TABLE_OWNERS + " WHERE " + Owners.COL_OWNERID + " = C." + Farms.COL_OWNERID + ")\n" +
                "END OwnerName\n" +
                "FROM " + Farms.TABLE_FARMS + " M LEFT JOIN " + Farms.TABLE_FARM_CHANGES + " C\n" +
                "ON M." + Farms.COL_FARMCODE + " = C." + Farms.COL_FARMCODE + "\n" +
                "ORDER BY FarmName";

        ArrayList<HashMap<String, String>> farmsList = new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> farms = new HashMap<>();
                farms.put("farmCode", cursor.getString(cursor.getColumnIndex("FarmCode")));
                farms.put("farmName", cursor.getString(cursor.getColumnIndex("FarmName")));
                farms.put("company", cursor.getString(cursor.getColumnIndex("Base")));
                farms.put("planter", cursor.getString(cursor.getColumnIndex("OwnerName")));
                farmsList.add(farms);

            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        db.close();
        return farmsList;
    }

    public Boolean isChgExist(String farmCode) {
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + Farms.TABLE_FARM_CHANGES + "" +
                " WHERE " + Farms.COL_FARMCODE + " =?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{farmCode});

        if (cursor.moveToFirst()) {
            return true;
        } else {
            return false;
        }

    }

    public Farms getFarmByID(String farmCode, String qryType) {
        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();
        String selMergeQry = "SELECT M." + Farms.COL_FARMCODE + " AS FarmCode,\n" +
                "CASE \n" +
                "WHEN C." + Farms.COL_FARMNAME + " IS NULL OR C." + Farms.COL_FARMNAME + " = '' THEN M." + Farms.COL_FARMNAME + "\n" +
                "ELSE C." + Farms.COL_FARMNAME + "\n" +
                "END FarmName,\n" +
                "CASE \n" +
                "WHEN C." + Farms.COL_BASE + " IS NULL OR C." + Farms.COL_BASE + " = '' THEN M." + Farms.COL_BASE + "\n" +
                "ELSE C." + Farms.COL_BASE + "\n" +
                "END Base,\n" +
                "CASE\n" +
                "WHEN C." + Farms.COL_STATUS + " IS NULL OR C." + Farms.COL_STATUS + " = '' THEN M." + Farms.COL_STATUS + "\n" +
                "ELSE C." + Farms.COL_STATUS + "\n" +
                "END Status,\n" +
                "CASE\n" +
                "WHEN C." + Farms.COL_OWNERID + " IS NULL OR C." + Farms.COL_OWNERID + " = '' THEN M." + Farms.COL_OWNERID + "\n" +
                "ELSE C." + Farms.COL_OWNERID + "\n" +
                "END OwnerID,\n" +
                "CASE \n" +
                "WHEN C." + Farms.COL_OWNERID + " IS NULL OR C." + Farms.COL_OWNERID + " = '' THEN (SELECT " + Owners.COL_OWNERNAME + " FROM " + Owners.TABLE_OWNERS + " WHERE " + Owners.COL_OWNERID + " = M." + Farms.COL_OWNERID + ")\n" +
                "ELSE (SELECT " + Owners.COL_OWNERNAME + " FROM " + Owners.TABLE_OWNERS + " WHERE " + Owners.COL_OWNERID + " = C." + Farms.COL_OWNERID + ")\n" +
                "END OwnerName,\n" +
                "C." + Farms.COL_REMARKS + " as Remarks " +
                "FROM " + Farms.TABLE_FARMS + " M LEFT JOIN " + Farms.TABLE_FARM_CHANGES + " C\n" +
                "ON M." + Farms.COL_FARMCODE + " = C." + Farms.COL_FARMCODE + "\n" +
                "WHERE M." + Farms.COL_FARMCODE + " =?";

        String selOrigQry = "SELECT M." + Farms.COL_FARMCODE + " AS FarmCode,\n" +
                "M." + Farms.COL_FARMNAME + " AS FarmName,\n" +
                "M." + Farms.COL_BASE + " AS Base,\n" +
                "M." + Farms.COL_STATUS + " AS Status,\n" +
                "M." + Farms.COL_OWNERID + "  AS OwnerID,\n" +
                "O." + Owners.COL_OWNERNAME + " AS OwnerName,\n" +
                "\'\' as Remarks " +
                "FROM " + Farms.TABLE_FARMS + " M LEFT JOIN " + Owners.TABLE_OWNERS + " O\n" +
                "ON M." + Farms.COL_OWNERID + " = O." + Owners.COL_OWNERID + "\n" +
                "WHERE M." + Farms.COL_FARMCODE + "=?";

        Farms farms = new Farms();
        String query = "";
        if (qryType.equalsIgnoreCase("M")) {
            query = selMergeQry;
        } else {
            query = selOrigQry;
        }
        Cursor cursor = db.rawQuery(query, new String[]{farmCode});

        if (cursor.moveToFirst()) {
            do {
                farms.setFarmCode(cursor.getString(cursor.getColumnIndex("FarmCode")));
                farms.setFarmName(cursor.getString(cursor.getColumnIndex("FarmName")));
                farms.setFarmBase(cursor.getString(cursor.getColumnIndex("Base")));
                farms.setFarmStatus(cursor.getString(cursor.getColumnIndex("Status")));
                farms.setFarmOwnerID(cursor.getString(cursor.getColumnIndex("OwnerID")));
                farms.setFarmRemarks(cursor.getString(cursor.getColumnIndex("Remarks")));
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return farms;
    }

    public int getFarmCount(String table) {

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
        return "SELECT F." + Farms.COL_FARMCODE + " AS Farmcode,\n" +
                "F." + Farms.COL_FARMNAME + " AS Farmname,\n" +
                "F." + Farms.COL_OWNERID + " AS OwnerID,\n" +
                "CASE WHEN \n" +
                "(SELECT " + Owners.COL_OWNERNAME + " FROM " + Owners.TABLE_OWNERS_CHANGES + " WHERE " + Owners.COL_OWNERID + " = F." + Farms.COL_OWNERID + ") IS NOT NULL OR \n" +
                "(SELECT " + Owners.COL_OWNERNAME + " FROM " + Owners.TABLE_OWNERS_CHANGES + " WHERE " + Owners.COL_OWNERID + " = F." + Farms.COL_OWNERID + ") <> ''\n" +
                "THEN (SELECT " + Owners.COL_OWNERNAME + " FROM " + Owners.TABLE_OWNERS_CHANGES + " WHERE " + Owners.COL_OWNERID + " = F." + Farms.COL_OWNERID + ")\n" +
                "ELSE (SELECT " + Owners.COL_OWNERNAME + " FROM " + Owners.TABLE_OWNERS + " WHERE " + Owners.COL_OWNERID + " = F." + Farms.COL_OWNERID + ")\n" +
                "END OwnerName,\n" +
                "F." + Farms.COL_BASE + " AS \"Base/Company\",\n" +
                "F." + Farms.COL_STATUS + " AS Status,\n" +
                "(SELECT " + Farms.COL_FARMNAME + " FROM " + Farms.TABLE_FARM_CHANGES + " WHERE " + Farms.COL_FARMCODE + " = F." + Farms.COL_FARMCODE + ") AS \"New Farm Name\",\n" +
                "C." + Farms.COL_OWNERID + " AS \"New Farm OwnerID\",\n" +
                "CASE WHEN \n" +
                "(SELECT " + Owners.COL_OWNERNAME + " FROM " + Owners.TABLE_OWNERS_CHANGES + " WHERE " + Owners.COL_OWNERID + " = C." + Farms.COL_OWNERID + ") IS NOT NULL OR \n" +
                "(SELECT " + Owners.COL_OWNERNAME + " FROM " + Owners.TABLE_OWNERS_CHANGES + " WHERE " + Owners.COL_OWNERID + " = C." + Farms.COL_OWNERID + ") <> '' \n" +
                "THEN (SELECT " + Owners.COL_OWNERNAME + " FROM " + Owners.TABLE_OWNERS_CHANGES + " WHERE " + Owners.COL_OWNERID + " = C." + Farms.COL_OWNERID + ")\n" +
                "ELSE (SELECT " + Owners.COL_OWNERNAME + " FROM " + Owners.TABLE_OWNERS + " WHERE " + Owners.COL_OWNERID + " = C." + Farms.COL_OWNERID + ")\n" +
                "END \"New Farm Owner\",\n" +
                "(SELECT C." + Farms.COL_BASE + " FROM " + Farms.TABLE_FARM_CHANGES + " WHERE " + Farms.COL_FARMCODE + " = F." + Farms.COL_FARMCODE + ") AS \"New Base\",\n" +
                "(SELECT C." + Farms.COL_STATUS + " FROM " + Farms.TABLE_FARM_CHANGES + " WHERE " + Farms.COL_FARMCODE + " = F." + Farms.COL_FARMCODE + ") AS \"New Status\",\n" +
                "(SELECT C." + Farms.COL_REMARKS + " FROM " + Farms.TABLE_FARM_CHANGES + " WHERE " + Farms.COL_FARMCODE + " = F." + Farms.COL_FARMCODE + ") AS \"Comments\"\n" +
                "FROM " + Farms.TABLE_FARMS + " F JOIN " + Owners.TABLE_OWNERS + " O JOIN " + Farms.TABLE_FARM_CHANGES + " C\n" +
                "ON F." + Farms.COL_OWNERID + " = O." + Owners.COL_OWNERID + " AND \n" +
                "F." + Farms.COL_FARMCODE + "= C." + Farms.COL_FARMCODE + "\n";
    }

}
