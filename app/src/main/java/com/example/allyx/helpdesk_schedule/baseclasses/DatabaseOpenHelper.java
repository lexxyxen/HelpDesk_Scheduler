package com.example.allyx.helpdesk_schedule.baseclasses;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by allyx on 2016-06-24.
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

    public static final String DB_NAME="db_helpdesk";
    public static final String TABLE_NAME="helpdesk";
    public static final String _ID="_id";
    public static final String HELPDESK_NAME="helpdesk_name";
    public static final String PHONE="phone";
    public static final String AVAILABILITY="availability";
    public static String[] columns={_ID, HELPDESK_NAME, PHONE, AVAILABILITY};
    public static String
            CREATE_CMD ="CREATE TABLE IF NOT EXISTS "+TABLE_NAME+
            "("+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
            HELPDESK_NAME + " TEXT NOT NULL," +
            PHONE + " TEXT NOT NULL," +
            AVAILABILITY + " TEXT NOT NULL)";
    private static int version=1;
    private Context context;

    public DatabaseOpenHelper(Context context){
        super(context,DB_NAME,null,version);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DATABASE", "Processing create database");
        db.execSQL(CREATE_CMD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void deleteDatabase(){
        Log.d("DATABASE", "Processing remove database");
        context.deleteDatabase(DB_NAME);
    }

}
