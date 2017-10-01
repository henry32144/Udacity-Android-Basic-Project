package com.henry.android.habittracker.Data;

import android.provider.BaseColumns;

/**
 * Created by Henry on 2017/9/7.
 */

public class HabitContract {
    private HabitContract() {}
    public static final class HabitEntry implements BaseColumns {

        public final static String TABLE_NAME = "habits";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_HABIT_NAME ="name";
        public final static String COLUMN_HABIT_DAY = "day";
        public final static String COLUMN_HABIT_TIMES = "times";
    }
}
