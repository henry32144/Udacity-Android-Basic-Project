package com.henry.android.habittracker;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.henry.android.habittracker.Data.HabitContract;

import static com.henry.android.habittracker.Data.HabitContract.HabitEntry.TABLE_NAME;

/**
 * Created by Henry on 2017/9/7.
 */

public class EditItemActivity extends AppCompatActivity {

    private EditText mNameEditText;

    private EditText mDayEditText;

    private EditText mTimeEditText;

    private int habitId;

    private HabitDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.editor_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChange();
                finish();
            }
        });

        mNameEditText = (EditText) findViewById(R.id.edit_habit_name);
        mDayEditText = (EditText) findViewById(R.id.edit_frequency_day);
        mTimeEditText = (EditText) findViewById(R.id.edit_frequency_times);

        Intent intent = getIntent();
        //Get data from extras
        Bundle extras = intent.getExtras();

        habitId = extras.getInt("ID");
        String originalNameText = extras.getString("name");
        String originalDay = extras.getInt("day") + "";
        String originalTime = extras.getInt("time") + "";

        mNameEditText.setText(originalNameText);
        mDayEditText.setText(originalDay);
        mTimeEditText.setText(originalTime);

        mDbHelper = new HabitDbHelper(this);
    }

    private void saveChange() {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String nameString = nullNameFilter(mNameEditText.getText().toString().trim());
        int dayInt = Integer.parseInt(nullNumberFilter(mDayEditText.getText().toString().trim()));
        int timesInt = Integer.parseInt(nullNumberFilter(mTimeEditText.getText().toString().trim()));

        ContentValues values = new ContentValues();
        values.put(HabitContract.HabitEntry.COLUMN_HABIT_NAME, nameString);
        values.put(HabitContract.HabitEntry.COLUMN_HABIT_DAY, dayInt);
        values.put(HabitContract.HabitEntry.COLUMN_HABIT_TIMES, timesInt);

        // Which row to update, based on the ID
        String selection = HabitContract.HabitEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(habitId) };

        int count = db.update(
                TABLE_NAME,
                values,
                selection,
                selectionArgs);

        if(count == -1) {
            Toast.makeText(this, "Error with saving change", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Saved change to id: " + habitId, Toast.LENGTH_SHORT).show();
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