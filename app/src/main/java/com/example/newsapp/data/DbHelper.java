package com.example.newsapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "newsAppDb.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "NewsApp_Table";

    // Columns
    private static final String COL_EMAIL = "Email";
    private static final String COL_PASS = "Password";
    private static final String COL_DARK = "DarkMode";
    private static final String COL_NAME = "Name";
    private static final String COL_IMAGE = "Image";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_EMAIL + " TEXT PRIMARY KEY, " +
                COL_PASS + " TEXT, " +
                COL_DARK + " INTEGER, " +
                COL_NAME + " TEXT, " +
                COL_IMAGE + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // --- Auth Methods ---

    public boolean registerUser(String email, String password, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EMAIL, email);
        values.put(COL_PASS, password);
        values.put(COL_NAME, name);
        values.put(COL_DARK, 0); // Default Light mode
        values.put(COL_IMAGE, "");

        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    public boolean loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_EMAIL + "=? AND " + COL_PASS + "=?", new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_EMAIL + "=?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean updatePassword(String email, String newPass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PASS, newPass);
        return db.update(TABLE_NAME, values, COL_EMAIL + "=?", new String[]{email}) > 0;
    }

    public Cursor getUserDetails(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_EMAIL + "=?", new String[]{email});
    }

    public boolean updateProfile(String oldEmail, String newEmail, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check collision if email changed
        if (!oldEmail.equals(newEmail) && checkEmailExists(newEmail)) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(COL_EMAIL, newEmail);
        values.put(COL_NAME, newName);
        return db.update(TABLE_NAME, values, COL_EMAIL + "=?", new String[]{oldEmail}) > 0;
    }

    // --- MISSING METHOD ADDED BELOW ---
    public void updateProfileImage(String email, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_IMAGE, imagePath);
        db.update(TABLE_NAME, values, COL_EMAIL + "=?", new String[]{email});
    }
}