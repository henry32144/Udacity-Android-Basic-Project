package com.henry.android.habittracker;

/**
 * Created by Henry on 2017/9/7.
 */

public class Habit {
    private String habitName;
    private int habitDay;
    private int habitTime;
    private int id;

    public Habit(String habitName, int habitDay, int habitTime, int id) {
        this.habitName = habitName;
        this.habitDay = habitDay;
        this.habitTime = habitTime;
        this.id = id;
    }

    public String getHabitName() {
        return habitName;
    }

    public int getHabitDay() {
        return habitDay;
    }

    public int getHabitTime() {
        return habitTime;
    }

    public int getId() {
        return id;
    }
}
