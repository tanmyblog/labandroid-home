package com.example.kdaig.lab.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBManager extends SQLiteOpenHelper {

    // ten the de ghi log
    public  static final String TAG = "SQLite";

    // phien ban
    private static final int DATABASE_VERSION = 1;

    // ten csdl
    public static final String DATABASE_NAME = "restaurantdb";

    // ten bang restaurant, cac cot tuong ung
    public static final String RESTAURANT_TABLE_NAME = "tblrestaurant";
    public static final String RESTAURANT_ID = "id";
    public static final String RESTAURANT_NAME = "name";
    public static final String RESTAURANT_ADDRESS = "address";
    public static final String RESTAURANT_TYPE = "type";

    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("DBManager", "DBManager");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sqlQuery = "CREATE TABLE " + RESTAURANT_TABLE_NAME + "("
                + RESTAURANT_ID + " TEXT PRIMARY KEY, "
                + RESTAURANT_NAME + " TEXT, "
                + RESTAURANT_ADDRESS + " TEXT, "
                + RESTAURANT_TYPE + " TEXT )";
        db.execSQL(sqlQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RESTAURANT_TABLE_NAME);
        onCreate(db);
    }
}
