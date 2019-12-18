package com.scbpfsdgis.atcct.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.scbpfsdgis.atcct.data.DatabaseManager;
import com.scbpfsdgis.atcct.data.model.Contact;
import com.scbpfsdgis.atcct.data.model.DBHelper;
import com.scbpfsdgis.atcct.data.model.Farms;
import com.scbpfsdgis.atcct.data.model.Owners;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by William on 4/15/2019.
 */

public class ContactRepo {

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public ContactRepo() {
        Contact contact = new Contact();
    }

    public static String createContactsTbl() {
        String query = "CREATE TABLE IF NOT EXISTS " + Contact.TABLE_CONTACT + " (" +
                Contact.COL_NAME + " TEXT, " +
                Contact.COL_NUM + " TEXT, " +
                Contact.COL_FARMCODE + " TEXT)";
        return query;
    }

    public void insert(Contact contact) {

        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Contact.COL_NAME, contact.getContName());
        values.put(Contact.COL_NUM, contact.getContNum());
        values.put(Contact.COL_FARMCODE, contact.getContFarmCode());

        db.insert(Contact.TABLE_CONTACT, null, values);
        db.close();
    }

    public void update(Contact contact) {
        dbHelper = new DBHelper();
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Contact.COL_NAME, contact.getContName());
        values.put(Contact.COL_NUM, contact.getContNum());

        db.update(Contact.TABLE_CONTACT, values, Contact.COL_FARMCODE + "= ? ", new String[]{String.valueOf(contact.getContFarmCode())});
        db.close(); // Closing database connection
    }

    public Contact getContactByFarm(String farmCode) {

        dbHelper = new DBHelper();
        db = dbHelper.getReadableDatabase();

        String query = "SELECT " + Contact.COL_FARMCODE + " as FarmCode, " +
                Contact.COL_NAME + " as ContactName, " +
                Contact.COL_NUM + " as ContactNum " +
                "FROM " + Contact.TABLE_CONTACT + " WHERE " + Contact.COL_FARMCODE + "=?";

        Cursor cursor = db.rawQuery(query, new String[]{farmCode});
        Contact contact = new Contact();

        if (cursor.moveToFirst()) {
            do {
                contact.setContName(cursor.getString(cursor.getColumnIndex("ContactName")));
                contact.setContNum(cursor.getString(cursor.getColumnIndex("ContactNum")));
                contact.setContFarmCode(cursor.getString(cursor.getColumnIndex("FarmCode")));
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return contact;
    }


}
