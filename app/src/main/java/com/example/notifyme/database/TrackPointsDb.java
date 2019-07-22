package com.example.notifyme.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.notifyme.model.TrackPoints;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class TrackPointsDb extends SQLiteOpenHelper {

    // DATAbase constants
    private static final int DATABASE_VERSION = 2; // data version
    private static final String DATABASE_NAME = "db_TrackPoints"; // database name
    private static final String TABLE_NAME = "TrackPoints";  // table name
    private static final String COL_CORD = "cordinates";  // this column store alarm' minutes
    private static final String COL_COLOR = "color";  // this column store alarm' minutes

    // this string defines table and data type use for onCreate method
    private String CREATE_TABLE_ALARM = "CREATE TABLE " + TABLE_NAME + " ("
            + COL_COLOR + " TEXT, "  // this column contain alarm's hour
            + COL_CORD + " TEXT) "; // alarm's minute

    // TODO:   this is data base constructor
    public TrackPointsDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create data base for the first time
        db.execSQL(CREATE_TABLE_ALARM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format(" DROP TABLE IF EXISTS %s", CREATE_TABLE_ALARM));
        onCreate(db);

    }

    // TODO: insert alarm to database
    public void insert(TrackPoints trackPoints) {
        // getting write data
        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            // ContentValues like a box to contain value in there
            ContentValues values = new ContentValues();
            // put value to each column
            values.put(COL_COLOR, trackPoints.getColor());
            values.put(COL_CORD, trackPoints.getCordinates());
            // insert to table
            db.insert(TABLE_NAME, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // finally close it
            if (db != null) {
                db.close();

            }
        }
    }

    public void updateColor(String cordinates)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_COLOR,"GREEN");
        db.update(TABLE_NAME, contentValues, "cordinates =?",new String[] { cordinates });
        if (db != null) {
            db.close();
        }
    }
    // TODO: get all Alarm from database and return arrayList alarm
    public ArrayList<TrackPoints> getTrackList() {
        // getting read data
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<TrackPoints> TrackPointsArrayList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            // method moveToFirst return true if cursor not empty
            if (cursor.moveToFirst()) {
                do {
                    TrackPoints TrackPoints = new TrackPoints(cursor.getString(0), cursor.getString(1));
                    TrackPointsArrayList.add(TrackPoints);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "getTrackPoints: exception cause " + e.getCause() + " message " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return TrackPointsArrayList;

    }

}
