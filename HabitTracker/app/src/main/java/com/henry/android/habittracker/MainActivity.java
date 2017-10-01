package com.henry.android.habittracker;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.henry.android.habittracker.Data.HabitContract;

import java.util.ArrayList;
import java.util.List;

import static com.henry.android.habittracker.Data.HabitContract.HabitEntry.TABLE_NAME;

public class MainActivity extends AppCompatActivity {

    private HabitDbHelper mDbHelper;

    private HabitAdpter mAdapter;

    private TextView mEmptyStateTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView habitListVew = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);

        habitListVew.setEmptyView(mEmptyStateTextView);

        mAdapter = new HabitAdpter(this, new ArrayList<Habit>());

        habitListVew.setAdapter(mAdapter);

        mEmptyStateTextView.setText(R.string.no_habit);

        habitListVew.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO: ADD editable item
                Habit currentHabit = (Habit) mAdapter.getItem(position);

                Intent modifyIntent = new Intent(MainActivity.this, EditItemActivity.class);

                Bundle extras = new Bundle();

                extras.clear();

                extras.putInt("ID", currentHabit.getId());
                extras.putString("name", currentHabit.getHabitName());
                extras.putInt("day", currentHabit.getHabitDay());
                extras.putInt("time", currentHabit.getHabitTime());

                modifyIntent.putExtras(extras);

                startActivity(modifyIntent);
            }
        });

        mDbHelper = new HabitDbHelper(this);

        displayDatabaseInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {
        mAdapter.clear();

        List<Habit> habitsList = new ArrayList<>();

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String[] projection = {
                HabitContract.HabitEntry._ID,
                HabitContract.HabitEntry.COLUMN_HABIT_NAME,
                HabitContract.HabitEntry.COLUMN_HABIT_DAY,
                HabitContract.HabitEntry.COLUMN_HABIT_TIMES
        };

        Cursor cursor = db.query(
                TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        try {
            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(HabitContract.HabitEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(HabitContract.HabitEntry.COLUMN_HABIT_NAME);
            int dayColumnIndex = cursor.getColumnIndex(HabitContract.HabitEntry.COLUMN_HABIT_DAY);
            int timeColumnIndex = cursor.getColumnIndex(HabitContract.HabitEntry.COLUMN_HABIT_TIMES);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {

                String currentHabitName = cursor.getString(nameColumnIndex);
                int currentHabitDay = cursor.getInt(dayColumnIndex);
                int currentHabitTime = cursor.getInt(timeColumnIndex);
                int currentHabitId = cursor.getInt(idColumnIndex);
                // Display the values from each column of the current row in the cursor in the TextView
                Habit habitObj = new Habit(currentHabitName, currentHabitDay, currentHabitTime, currentHabitId);
                habitsList.add(habitObj);
            }
            if (habitsList != null && !habitsList.isEmpty()) {
                mAdapter.addAll(habitsList);
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    private void deleteAll(){

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.delete(TABLE_NAME, null, null);

        displayDatabaseInfo();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            case R.id.action_delete:
                deleteAll();
                return true;
            case R.id.action_delete_database:
                this.deleteDatabase(HabitDbHelper.DATABASE_NAME);
                mDbHelper = new HabitDbHelper(this);
                displayDatabaseInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
