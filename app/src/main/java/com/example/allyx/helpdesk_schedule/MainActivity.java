package com.example.allyx.helpdesk_schedule;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allyx.helpdesk_schedule.baseclasses.DatabaseOpenHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher{

    SQLiteDatabase mDb=null;
    DatabaseOpenHelper mDbHelper;

    Cursor helpdeskCursor;

    Button btnSave, btnShow, btnShowAll;
    EditText editTextName, editTextPhone;
    TextView textViewWeekend, textView9to17, textView17to1, textView1to9;
    CheckBox checkBoxWeekend, checkBox9to17, checkBox17to1, checkBox1to9;
    String lastDigit = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    private void initialize() {
        btnSave  = (Button)findViewById(R.id.btnSave);
        btnShow = (Button)findViewById(R.id.btnShow);
        btnShowAll = (Button)findViewById(R.id.btnShowAll);

        btnSave.setOnClickListener(this);
        btnShow.setOnClickListener(this);
        btnShowAll.setOnClickListener(this);

        editTextName = (EditText)findViewById(R.id.editTextName);
        editTextPhone = (EditText)findViewById(R.id.editTextPhone);

        editTextPhone.addTextChangedListener(this);

        textViewWeekend = (TextView)findViewById(R.id.textViewWeekend);
        textView9to17 = (TextView)findViewById(R.id.textView9to17);
        textView17to1 = (TextView)findViewById(R.id.textView17to1);
        textView1to9 = (TextView)findViewById(R.id.textView1to9);

        checkBoxWeekend = (CheckBox) findViewById(R.id.checkBoxWeekend);
        checkBox9to17 = (CheckBox) findViewById(R.id.checkBox9to17);
        checkBox17to1 = (CheckBox) findViewById(R.id.checkBox17to1);
        checkBox1to9 = (CheckBox) findViewById(R.id.checkBox1to9);


        mDbHelper = new DatabaseOpenHelper(this);
        createDatabase();

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        int phoneDigits = editTextPhone.getText().toString().length();
        if (phoneDigits > 1){
            lastDigit = editTextPhone.getText().toString().substring(phoneDigits - 1);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int phoneDigits = editTextPhone.getText().toString().length();
        if (!lastDigit.equals("-")) {
            if (phoneDigits == 3 || phoneDigits == 7) {
                editTextPhone.append("-");
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btnSave :
                addUserInput();
                break;

            case R.id.btnShow :
                showOneRow();
                break;
            case R.id.btnShowAll :
                goToNextActivity();
                break;
        }
    }



    private void createDatabase() {
        Log.d("DATABASE", "run create database method");
        mDb =  mDbHelper.getWritableDatabase();
    }

    private void addUserInput() {
        // validates user input
        if (!checkUserInput()) {
            return;
        }
        else {
            //gets the availability form the availability function
            String availability = getAvailability();

            Cursor IdCourse = getIdFromName();
            String HelpDeskID = "";
            if (IdCourse != null && IdCourse.getCount() > 0){
                while (IdCourse.moveToNext()) {
                     HelpDeskID = IdCourse.getString(IdCourse.getColumnIndexOrThrow("_id"));
                }

                updateOneRow(availability, HelpDeskID);
                message("The record has been updated successfully.");
                clear();
            }
            else {
                addOneRow(availability);
                message("The record has been added successfully.");
                clear();

            }
        }

    }

    private void addOneRow(String avb) {


        ContentValues values = new ContentValues();

        values.put(mDbHelper.HELPDESK_NAME,
                editTextName.getText().toString());
        values.put(mDbHelper.PHONE,
                editTextPhone.getText().toString());
        values.put(mDbHelper.AVAILABILITY,
                avb);
        mDb.insert(mDbHelper.TABLE_NAME, null, values);
    }


    private void updateOneRow(String avb, String id) {

        //checks the user input
        checkUserInput();

        //gets the availability form the availability function
        String availability = getAvailability();

        ContentValues values = new ContentValues();
        values.put(mDbHelper.PHONE,
                editTextPhone.getText().toString());
        values.put(mDbHelper.AVAILABILITY,
                availability);
        mDb.update(mDbHelper.TABLE_NAME,
                values,
                mDbHelper._ID + "=?",
                new String[]
                        {id});
    }

    private Cursor readOneRow(){

        String selectQuery = "SELECT * FROM " + DatabaseOpenHelper.TABLE_NAME + " WHERE "
                + DatabaseOpenHelper._ID + " = last_insert_rowid()";

        Cursor result=
                mDb.rawQuery(selectQuery, null);
        return result;
    }

    private Cursor getIdFromName(){

        String selectQuery = "SELECT _id FROM " + DatabaseOpenHelper.TABLE_NAME + " WHERE "
                + DatabaseOpenHelper.HELPDESK_NAME + "=?";


        Cursor result=
                mDb.rawQuery(selectQuery, new String[]
                        {editTextName.getText().toString()});
        return result;
    }

    private void showOneRow() {
        helpdeskCursor = readOneRow();

        ArrayList<String> helpdesk = new ArrayList<>();
        if (helpdeskCursor != null && helpdeskCursor.getCount() > 0){
            while (helpdeskCursor.moveToNext()) {

                helpdesk.add(helpdeskCursor.getString(helpdeskCursor.getColumnIndexOrThrow("_id")));
                helpdesk.add(helpdeskCursor.getString(helpdeskCursor.getColumnIndexOrThrow("helpdesk_name")));
                helpdesk.add(helpdeskCursor.getString(helpdeskCursor.getColumnIndexOrThrow("phone")));
                helpdesk.add(helpdeskCursor.getString(helpdeskCursor.getColumnIndexOrThrow("availability")));
            }

            Toast.makeText(this, TextUtils.join(", ", helpdesk),
                    Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "No recently inserted records",
                    Toast.LENGTH_LONG).show();
        }

    }
    // ============ returns the helpdesk's availability ===================
    private String getAvailability() {
        StringBuilder strAvailability = new StringBuilder();

        if (checkBoxWeekend.isChecked()){
            strAvailability.append("[" + textViewWeekend.getText().toString() + "]\n");
        }

        if (checkBox9to17.isChecked()){
            strAvailability.append("[" + textView9to17.getText().toString() + "]\n");
        }

        if(checkBox17to1.isChecked()){
            strAvailability.append("[" + textView17to1.getText().toString() + "]\n");
        }

        if(checkBox1to9.isChecked()){
            strAvailability.append("[" + textView1to9.getText().toString() + "]");
        }
        return strAvailability.toString();
    }


    private void goToNextActivity() {

        Intent i = new Intent(this, ListviewActivity.class);
        startActivity(i);
    }
    // =========== Input validation helper functions ====================
    private boolean isEditTextEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0){
            return false;
        }
        return true;
    }

    private boolean isOneCheckBoxChecked() {
        boolean selectionExists = false;
        selectionExists = (boolean) ((CheckBox) findViewById(R.id.checkBoxWeekend)).isChecked() ? true : false ||
                (boolean) ((CheckBox) findViewById(R.id.checkBox9to17)).isChecked() ? true : false ||
                (boolean) ((CheckBox) findViewById(R.id.checkBox17to1)).isChecked() ? true : false ||
                (boolean) ((CheckBox) findViewById(R.id.checkBox1to9)).isChecked() ? true : false;

        return selectionExists;
    }

    // ============= Checks the user input by calling the above-created functions ======

    private boolean checkUserInput() {
        if (isEditTextEmpty(editTextName)){
            message("Please enter a name!");

            return false;
        }

        if (isEditTextEmpty(editTextPhone)){
            message("Please enter a phone number!");

            return false;
        }

        if (!isOneCheckBoxChecked()) {
            message("Please make at least one selection for availability!");

            return false;
        }
        return true;
    }

    //=================== message function for toast ========================
    private void message(String msg){
        Toast.makeText(this, msg,
                Toast.LENGTH_SHORT).show();
    }

    //=================== clear user input after inserting a record =========
    private void clear() {
        editTextName.setText("");
        editTextPhone.setText("");

        checkBoxWeekend.setChecked(false);
        checkBox9to17.setChecked(false);
        checkBox17to1.setChecked(false);
        checkBox1to9.setChecked(false);

    }
}
