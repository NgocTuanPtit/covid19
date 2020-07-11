package com.example.covid19.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.covid19.model.Users;

public class UserRepository extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "covid_19";
    public static final String TABLE_USER = "users";
    public static final int VERSION = 1;
    Context context;

    String createTableUser = "CREATE TABLE " + TABLE_USER + " (" +
            "id " + "INTEGER" + " primary key, " +
            "full_name " + "TEXT, " +
            "email " + "TEXT, " +
            "password " + "TEXT, " +
            "address " + "TEXT" + " )";

    public UserRepository(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTableUser);
    }

    public long registerUser(Users users) {
        SQLiteDatabase exc = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("full_name", users.getFullName());
        values.put("email", users.getEmail());
        values.put("password", users.getPassword());
        values.put("address", users.getAddress());
        long stt = exc.insert(TABLE_USER, null, values);
        exc.close();
        return stt;
    }

    public boolean checkLogin(Users users) {
        SQLiteDatabase exc = this.getWritableDatabase();
        Cursor cursor = exc.query(true, TABLE_USER, new String[]{
                        "full_name",
                        "address"},
                "email" + "=?" + " and password" + "=?",
                new String[]{users.getEmail(), users.getPassword()},
                null, null, null, null);
        if (cursor.moveToFirst()) {
            return true;
        }
        return false;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
