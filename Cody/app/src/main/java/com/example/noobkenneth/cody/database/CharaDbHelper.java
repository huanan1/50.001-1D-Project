package com.example.noobkenneth.cody.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.example.noobkenneth.cody.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by norman_lee on 6/10/
 * Adapted by thaddeus_phua for 50.001 1D project
 */

public class CharaDbHelper extends SQLiteOpenHelper {


    private final Context context;
    private static String PACKAGE_NAME;
    private static final int DATABASE_VERSION = 45;
    private SQLiteDatabase sqLiteDatabase;
    private SQLiteDatabase readableDb;
    private SQLiteDatabase writeableDb;
    private static CharaDbHelper charaDbHelper;

    // Create the Constructor and make it a singleton
    private CharaDbHelper(Context context) {
        super(context, CharaContract.CharaEntry.TABLE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    static CharaDbHelper createCharaDbHelper(Context context) {
        if (charaDbHelper == null) {
            charaDbHelper = new CharaDbHelper(context.getApplicationContext());
        }
        return charaDbHelper;
    }

    //Creates the database and fills the table with default values
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CharaContract.CharaSql.SQL_CREATE_TABLE);
//        fillTable(sqLiteDatabase);
    }

    //Check if database version has changed, in which case the table is dropped and data is removed
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(CharaContract.CharaSql.SQL_DROP_TABLE);
        onCreate(sqLiteDatabase);
    }


    // Query one row at random
    public CharaData queryOneRowRandom() {
        if (readableDb == null) {
            readableDb = getReadableDatabase();
        }
        Cursor cursor = readableDb.rawQuery(
                CharaContract.CharaSql.SQL_QUERY_ONE_RANDOM_ROW,
                null);

        return getDataFromCursor(0, cursor);

    }

    //queryOneRow gets the entire database returns the row in position as a CharaData object
    public CharaData queryOneRow(int position) {
        if (readableDb == null) {
            readableDb = getReadableDatabase();
        }
        Cursor cursor = readableDb.rawQuery(
                CharaContract.CharaSql.SQL_QUERY_ALL_ROWS,
                null);

        return getDataFromCursor(position, cursor);
    }

    //queryOneRowDate gets the data from an ootd worn on the day
    public CharaData queryOneRowDate(String date) {
        Log.i("Logcat", CharaContract.CharaSql.SQL_QUERY_ONE_DATE + date);
        if (readableDb == null) {
            readableDb = getReadableDatabase();
        }
        Cursor cursor = readableDb.rawQuery(
                CharaContract.CharaSql.SQL_QUERY_ONE_DATE + "'"+date+"'"+ "and ootd = '1'",
                null);
        int count = cursor.getCount();
        if (count == 0) {
            return null;
        }
        return getDataFromCursor(0, cursor);
    }


    //queryOneRowWhereCatOotd queries data from the database filtered by category and whether
    // it is an article of clothing or an ootd
    public CharaData queryOneRowWhereCatOotd(int position, String category, String ootd) {
        if (readableDb == null) {
            readableDb = getReadableDatabase();
        }
        Cursor cursor = null;
        if (category == null && ootd == null) {
            cursor = readableDb.rawQuery(
                    CharaContract.CharaSql.SQL_QUERY_WHERE +
                            "category is not null and ootd is not null",
                    null);
        } else if (category != null && ootd != null) {
            cursor = readableDb.rawQuery(
                    CharaContract.CharaSql.SQL_QUERY_WHERE +
                            "category = " + category + " and ootd = " + ootd,
                    null);
        } else if (category == null) {
            cursor = readableDb.rawQuery(
                    CharaContract.CharaSql.SQL_QUERY_WHERE +
                            " ootd = " + ootd,
                    null);
        } else {
            cursor = readableDb.rawQuery(
                    CharaContract.CharaSql.SQL_QUERY_WHERE +
                            " category = " + category,
                    null);
        }
        return getDataFromCursor(position, cursor);
    }


    //Get the data from cursor
    private CharaData getDataFromCursor(int position, Cursor cursor) {

        int id = 0;
        String category = null;
        String formality = null;
        String last_used = null;
        boolean ootd = false;
        Bitmap bitmap = null;

        cursor.moveToPosition(position);

        int idIndex = cursor.getColumnIndex(CharaContract.CharaEntry._ID);
        id = Integer.parseInt(cursor.getString(idIndex));

        int categoryIndex = cursor.getColumnIndex(CharaContract.CharaEntry.COL_CATEGORY);
        category = cursor.getString(categoryIndex);

        int formalityIndex = cursor.getColumnIndex(CharaContract.CharaEntry.COL_FORMALITY);
        formality = cursor.getString(formalityIndex);

        int last_usedIndex = cursor.getColumnIndex(CharaContract.CharaEntry.COL_LAST_USED);
        last_used = cursor.getString(last_usedIndex);

        int ootdIndex = cursor.getColumnIndex(CharaContract.CharaEntry.COL_OOTD);
        ootd = Boolean.parseBoolean(cursor.getString(ootdIndex));

        int bitmapIndex = cursor.getColumnIndex(CharaContract.CharaEntry.COL_FILE);
        byte[] bitmapByteArray = cursor.getBlob(bitmapIndex);
        bitmap = BitmapFactory.decodeByteArray(bitmapByteArray,
                0,
                bitmapByteArray.length);

        Log.i("Logcat", "CharaDbHelper.getDataFromCursor" +
                category + " " + formality + " " +
                last_used + " " + ootd);

        return new CharaData(id, category, formality, last_used, ootd, bitmap);
    }

    //Insert one row when data is passed to it
    public void insertOneRow(CharaData charaData) {

        Log.i("Logcat", "CharaDbHelper.insertOneRow  " +
                        charaData.getCategory() + " " + charaData.getFormality() + " " +
                        charaData.getLastUsed() + " " + charaData.getOotd());

        if (writeableDb == null) {
            writeableDb = getWritableDatabase();
        }
        ContentValues contentValues
                = new ContentValues();

        contentValues.put(
                CharaContract.CharaEntry.COL_CATEGORY,
                charaData.getCategory());

        contentValues.put(
                CharaContract.CharaEntry.COL_FORMALITY,
                charaData.getFormality());

        contentValues.put(
                CharaContract.CharaEntry.COL_LAST_USED,
                charaData.getLastUsed());

        contentValues.put(
                CharaContract.CharaEntry.COL_OOTD,
                charaData.getOotd());

        byte[] bitmapdata = Utils.convertBitmapToByteArray(
                charaData.getBitmap());
        contentValues.put(
                CharaContract.CharaEntry.COL_FILE,
                bitmapdata);
        writeableDb.insert(CharaContract.CharaEntry.TABLE_NAME,
                null, contentValues);
    }


    //Delete one row given the unique id
    public int deleteOneRow(String id) {
        if (writeableDb == null) {
            writeableDb = getWritableDatabase();
        }
        String WHERE_CLAUSE
                = CharaContract.CharaEntry._ID + " = ?";
        String[] WHERE_ARGS = {id};
        int rowsDeleted = writeableDb.delete(CharaContract.CharaEntry.TABLE_NAME,
                WHERE_CLAUSE, WHERE_ARGS);
        Log.i("Logcat", "rows deleted: " + rowsDeleted);
        return rowsDeleted;
    }

    //Return the total number of rows in the database
    public long queryNumRows() {
        if (readableDb == null) {
            readableDb = getReadableDatabase();
        }
        return DatabaseUtils.queryNumEntries(readableDb,
                CharaContract.CharaEntry.TABLE_NAME);
    }

    //return the number of rows for the category and string query
    public long queryNumRowsCatOotd(String category, String ootd) {
        if (readableDb == null) {
            readableDb = getReadableDatabase();
        }
        Log.i("Logcat", "queryNumRows: "+category + ootd);
        Cursor cursor = null;
        if (category == null && ootd == null) {

            cursor = readableDb.rawQuery(
                    CharaContract.CharaSql.SQL_QUERY_WHERE +
                            "category is not null and ootd is not null",
                    null);
        } else if (category != null && ootd != null) {
            cursor = readableDb.rawQuery(
                    CharaContract.CharaSql.SQL_QUERY_WHERE +
                            "category = " + category + " and ootd = " + ootd,
                    null);
        } else if (category == null) {
            cursor = readableDb.rawQuery(
                    CharaContract.CharaSql.SQL_QUERY_WHERE +
                            " ootd = " + ootd,
                    null);
        } else {
            cursor = readableDb.rawQuery(
                    CharaContract.CharaSql.SQL_QUERY_WHERE +
                            " category = " + category,
                    null);
        }
        int count = cursor.getCount();
        Log.i("Logcat", "CharaDbHelper.queryNumRows: " + " Category: " + category +
                " ootd: " + ootd + " count: " + count);
        return count;
    }

    public Context getContext() {
        return context;
    }


    //Create a model class to represent our data
    public static class CharaData {

        private int id;
        private String category;
        private String formality;
        private String last_used;
        private boolean ootd;
        private String file;
        private Bitmap bitmap;

        public CharaData(String category, String formality,
                         String last_used, boolean ootd, Bitmap bitmap) {
            Log.i("Logcat", "CharaData constructor called " + category + " " +
                    formality + " " + last_used + " " + ootd);
            this.category = category;
            this.formality = formality;
            this.last_used = last_used;
            this.ootd = ootd;
            this.bitmap = bitmap;
        }

        public CharaData(int id, String category, String formality,
                         String last_used, boolean ootd, Bitmap bitmap) {
            Log.i("Logcat", "CharaData constructor called " + id + " " + category + " " +
                    formality + " " + last_used + " " + ootd);
            this.id = id;
            this.category = category;
            this.formality = formality;
            this.last_used = last_used;
            this.ootd = ootd;
            this.bitmap = bitmap;
        }

        public int getId() { return id; }

        public String getCategory() {
            return category;
        }

        public String getFormality() {
            return formality;
        }

        public String getLastUsed() {
            return last_used;
        }

        public boolean getOotd() {
            return ootd;
        }

        public Bitmap getBitmap() { return bitmap; }

        public String getFile() {
            return file;
        }




    }

}
