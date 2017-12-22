package com.example.allyx.helpdesk_schedule;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.allyx.helpdesk_schedule.baseclasses.DatabaseOpenHelper;

public class ListviewActivity extends AppCompatActivity {
ListView listViewHelpdesk;
    SQLiteDatabase mDb=null;
    DatabaseOpenHelper mDbHelper;

    SimpleCursorAdapter mySimpleCursorAdapter;
    Cursor helpdeskCursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        initialize();
    }

    private void initialize() {
        listViewHelpdesk = (ListView)findViewById(R.id.listViewHelpdesks);
        mDbHelper = new DatabaseOpenHelper(this);
        createDatabase();
        helpdeskCursor = readAllRows();
        mySimpleCursorAdapter =
                new SimpleCursorAdapter(this,
                        R.layout.one_element,helpdeskCursor,
                        DatabaseOpenHelper.columns,
                        new int[]{R.id.textViewId,
                                R.id.textViewName,
                                R.id.textViewPhone,
                                R.id.textViewAvailability1},0);
        listViewHelpdesk.setAdapter(mySimpleCursorAdapter);
    }

    private void createDatabase() {
        Log.d("DATABASE", "run create database method");
        mDb =  mDbHelper.getWritableDatabase();
    }
    private Cursor readAllRows(){
        Cursor result=
                mDb.query(DatabaseOpenHelper.TABLE_NAME,
                        DatabaseOpenHelper.columns,
                        null,
                        null,
                        null,null,null);
        return result;
    }
}
