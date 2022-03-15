package com.borham.simplecrud;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MySqlliteDatabase {
    private final int DBVerison = 1;
    private final String DBName = "faculty.db";
    private final String TBName = "students";
    private final String sid = "id";
    private final String sname = "name";
    private final String scity = "city";
    private final String sage = "age";
    private final String DBQuary = "Create Table " + TBName + " (" + sid + " INTEGER PRIMARY KEY AUTOINCREMENT ," + sname + " TEXT ," + scity + " TEXT ," + sage + " TEXT);";
    private final DatabaseHelper helper;

    public MySqlliteDatabase(Context context) {
        helper = new DatabaseHelper(context);
    }

    public long addEntry(DatabaseFormat format) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(sname, format.getName());
        values.put(scity, format.getCity());
        values.put(sage, format.getAge());
        long output = db.insert(TBName, null, values);
        db.close();
        return output;
    }

    public DatabaseFormat getEntry(String id) {
        DatabaseFormat df = new DatabaseFormat("", "", "");
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] columns = {sname, scity, sage};
        Cursor c = db.query(TBName, columns, "id = ?", new String[]{id}, null, null, null);
        while (c.moveToNext()) {
            df = new DatabaseFormat(c.getString(0), c.getString(1), c.getString(2));
        }
        db.close();
        c.close();
        return df;
    }

    public ArrayList<DatabaseFormat> getAllEntry() {
        ArrayList<DatabaseFormat> df = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] columns = {"id", sname, scity, sage};
        Cursor c = db.query(TBName, columns, null, null, null, null, null);
        while (c.moveToNext()) {
            df.add(new DatabaseFormat(c.getInt(0), c.getString(1), c.getString(2), c.getString(3)));
        }
        db.close();
        c.close();
        return df;
    }

    public void updateEntry(String id, DatabaseFormat format) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(sname, format.getName());
        values.put(scity, format.getCity());
        values.put(sage, format.getAge());
        db.update(TBName, values, "id = ?", new String[]{id});
        db.close();
    }

    public void deleteEntry(String id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(TBName, "id = ?", new String[]{id});
        db.close();
    }

    public boolean searchEntry(String id) {
        boolean is_id_exist = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(TBName, new String[]{"id"}, null, null, null, null, null);
        while (c.moveToNext()) {
            if (c.getString(0).equals(id)) {
                is_id_exist = true;
            }
        }
        db.close();
        c.close();
        return is_id_exist;
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(@Nullable Context context) {
            super(context, DBName, null, DBVerison);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DBQuary);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TBName);
            db.execSQL(DBQuary);
        }
    }
}
