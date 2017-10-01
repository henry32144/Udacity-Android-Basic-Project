package com.henry.android.habittracker;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.henry.android.habittracker.Data.HabitContract;

/**
 * Created by Henry on 2017/9/7.
 */

public class EditorActivity extends AppCompatActivity{


    private EditText mNameEditText;

    private EditText mDayEditText;

    private EditText mTimeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.editor_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertHabit();
                finish();
            }
        });

        mNameEditText = (EditText) findViewById(R.id.edit_habit_name);
        mDayEditText = (EditText) findViewById(R.id.edit_frequency_day);
        mTimeEditText = (EditText) findViewById(R.id.edit_frequency_times);
    }

    private void insertHabit() {
        HabitDbHelper mDbHelper = new HabitDbHelper(this);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String nameString = nullNameFilter(mNameEditText.getText().toString().trim());
        int dayInt = Integer.parseInt(nullNumberFilter(mDayEditText.getText().toString().trim()));
        int timesInt = Integer.parseInt(nullNumberFilter(mTimeEditText.getText().toString().trim()));


        ContentValues values = new ContentValues();
        values.put(HabitContract.HabitEntry.COLUMN_HABIT_NAME, nameString);
        values.put(HabitContract.HabitEntry.COLUMN_HABIT_DAY, dayInt);
        values.put(HabitContract.HabitEntry.COLUMN_HABIT_TIMES, timesInt);

        long newRowId = db.insert(
                HabitContract.HabitEntry.TABLE_NAME,
                null,
                values);

        if(newRowId == -1) {
            Toast.makeText(this, "Error with saving habit", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Saved habit with id: " + newRowId, Toast.LENGTH_SHORT).show();
        }
    }

    private String nullNameFilter(String value) {
        if(value.length() > 0) {
            return value;
        }
        else return "Unknown";
    }

    private String nullNumberFilter(String value) {
        if(value.length() > 0) {
            return value;
        }
        else return "0";
    }

}
