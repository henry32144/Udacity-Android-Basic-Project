package com.henry.android.habittracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Henry on 2017/9/7.
 */

public class HabitAdpter extends ArrayAdapter{

    public HabitAdpter(Context context, ArrayList<Habit> habits) {
        super(context, 0, habits);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The position in the list of data that should be displayed in the
     *                    list item view.
     * @param convertView The recycled view to populate.
     * @param parent      The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);

            holder.habitNameText = (TextView) convertView.findViewById(R.id.list_item_habit_name);
            holder.habitDayText = (TextView) convertView.findViewById(R.id.list_item_habit_day);
            holder.habitTimeText = (TextView) convertView.findViewById(R.id.list_item_habit_times);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Habit habit = (Habit) getItem(position);

        String habitName = habit.getHabitName();

        String habitDay = habit.getHabitDay() + "";

        String habitTime = habit.getHabitTime() + "";

        holder.habitNameText.setText(habitName);

        holder.habitDayText.setText(habitDay);

        holder.habitTimeText.setText(habitTime);

        return convertView;
    }
}
