package com.example.kdaig.lab.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.kdaig.lab.model.ClassRestaurant;

import java.sql.SQLData;
import java.util.ArrayList;
import java.util.List;

public class RestaurantDAO {
    //Đối tượng đại diện cho Database
    DBManager dbManager;

    public RestaurantDAO(Context context) {
        dbManager = new DBManager(context);
    }
    public void create(ClassRestaurant classRestaurant) {
        Log.i(DBManager.TAG, "DBManager.create ... " + classRestaurant.getId());

        SQLiteDatabase db = dbManager.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBManager.RESTAURANT_ID, classRestaurant.getId());
        values.put(DBManager.RESTAURANT_NAME, classRestaurant.getName());
        values.put(DBManager.RESTAURANT_ADDRESS, classRestaurant.getAddress());
        values.put(DBManager.RESTAURANT_TYPE, classRestaurant.getType());

        //Nếu để null thì khi value bằng null thì lỗi

        db.insert(DBManager.RESTAURANT_TABLE_NAME, null, values);
        db.close();
    }

    //Chọn 1 classroom theo ID
    public ClassRestaurant read(String id) {
        SQLiteDatabase db = dbManager.getReadableDatabase();
        Cursor cursor = db.query(DBManager.RESTAURANT_TABLE_NAME, new String[]{DBManager.RESTAURANT_ID,
                        DBManager.RESTAURANT_NAME, DBManager.RESTAURANT_ADDRESS, DBManager.RESTAURANT_TYPE},
                DBManager.RESTAURANT_ID + "=?",
                new String[]{id}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ClassRestaurant classRestaurant = new ClassRestaurant(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                cursor.getString(3));
        cursor.close();
        db.close();
        return classRestaurant;
    }

    //Lấy đầy đủ ClassRoom
    public List<ClassRestaurant> readAll() {
        List<ClassRestaurant> classRestaurantList = new ArrayList<ClassRestaurant>();
        //Câu lệnh truy vấn
        String selectQuery = "SELECT * FROM " + DBManager.RESTAURANT_TABLE_NAME;

        SQLiteDatabase db = dbManager.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ClassRestaurant classRestaurant = new ClassRestaurant();
                classRestaurant.setId(cursor.getString(0));
                classRestaurant.setName(cursor.getString(1));
                classRestaurant.setAddress(cursor.getString(2));
                classRestaurant.setType(cursor.getString(3));

                classRestaurantList.add(classRestaurant);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return classRestaurantList;
    }


    //Cập nhật C
    public int update(ClassRestaurant classRestaurant) {
        SQLiteDatabase db = dbManager.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBManager.RESTAURANT_NAME, classRestaurant.getName());
        values.put(DBManager.RESTAURANT_ADDRESS, classRestaurant.getAddress());
        values.put(DBManager.RESTAURANT_TYPE, classRestaurant.getType());

        return db.update(DBManager.RESTAURANT_TABLE_NAME, values, DBManager.RESTAURANT_ID + "=?",
                new String[]{String.valueOf(classRestaurant.getId())});
    }

    //Xóa CLassRoom
    public void delete(String id) {
        Log.i(DBManager.TAG, "Delete restaurant id = " + id);

        SQLiteDatabase db = dbManager.getWritableDatabase();
        db.delete(DBManager.RESTAURANT_TABLE_NAME, DBManager.RESTAURANT_ID + "=?",
                new String[]{id});
        db.close();
    }

    //Đếm số dòng
    public int count() {

        String countQuery = "SELECT * FROM " + DBManager.RESTAURANT_TABLE_NAME;
        SQLiteDatabase db = dbManager.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = 0;
        try {
            if (cursor.moveToFirst()) {
                count = cursor.getCount();
            }
            return count;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}


