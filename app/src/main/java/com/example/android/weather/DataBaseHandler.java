package com.example.android.weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.R.attr.bitmap;

/**
 * Created by Ajish on 11-07-2017.
 */

public class DataBaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "weatherManager";
    private static final String TABLE_WEATHER = "weather";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_TIME = "time";
    private static final String KEY_TEMP = "temp";
    private static final String KEY_HUMIDITY = "humidity";
    private static final String KEY_PRESSURE = "pressure";
    private static final String KEY_SPEED = "speed";
    private static final String KEY_VISIBILITY = "visibility";
    private static final String KEY_DESC = "desc";
    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_WEATHER + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + KEY_NAME + " TEXT NOT NULL UNIQUE,"
                + KEY_IMAGE + " BLOB," + KEY_TIME + " TEXT NOT NULL," + KEY_DESC + " TEXT NOT NULL," + KEY_TEMP + " REAL NOT NULL," + KEY_HUMIDITY + " INT NOT NULL," + KEY_PRESSURE + " REAL NOT NULL," + KEY_SPEED + " REAL NOT NULL," + KEY_VISIBILITY  + " INT NOT NULL" + ")" ;

        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEATHER);
        onCreate(db);
    }

    public void addWeather(Weather weather){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, weather.getMname());
        byte[] image_byte = weather.getMimageByte();
        values.put(KEY_IMAGE, image_byte);
        Long date = weather.getMtime()*1000;
        Date dt = new java.util.Date(date);
        String formatted_date = new SimpleDateFormat("EEEE, h:mm a").format(dt);
        values.put(KEY_TIME, formatted_date);
        values.put(KEY_DESC, weather.getMdesc());
        values.put(KEY_TEMP, weather.getMtemp());
        values.put(KEY_HUMIDITY, weather.getMhumidity());
        values.put(KEY_PRESSURE, weather.getMpressure());
        values.put(KEY_SPEED, weather.getMwindSpeed());
        values.put(KEY_VISIBILITY,weather.getMvisibility());
        db.insert(TABLE_WEATHER, null, values);
        db.close();

    }



    public void deleteWeather(Weather weather){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WEATHER, KEY_NAME + " = ?",
                new String[] { weather.getMname() });
        db.close();

    }

    public Weather select(String nameCity){
        Weather weather = null;
        String selectQuery = "SELECT  * FROM " + TABLE_WEATHER + " WHERE NAME = \'"+nameCity.trim() + "\'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

            String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
            byte[] image  = cursor.getBlob(cursor.getColumnIndex(KEY_IMAGE));
            String date = cursor.getString(cursor.getColumnIndex(KEY_TIME));
            String desc = cursor.getString(cursor.getColumnIndex(KEY_DESC));
            double temp = cursor.getDouble(cursor.getColumnIndex(KEY_TEMP));
            int humidity = cursor.getInt(cursor.getColumnIndex(KEY_HUMIDITY));
            double pressure = cursor.getDouble(cursor.getColumnIndex(KEY_PRESSURE));
            double speed = cursor.getDouble(cursor.getColumnIndex(KEY_SPEED));
            int visibility = cursor.getInt(cursor.getColumnIndex(KEY_VISIBILITY));
            weather = new Weather(0l,name,desc,temp,humidity,speed,visibility,pressure,"");
            weather.setDate(date);
            weather.setImageByte(image);


        }

        return weather;
    }

    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_WEATHER);
    }
}
