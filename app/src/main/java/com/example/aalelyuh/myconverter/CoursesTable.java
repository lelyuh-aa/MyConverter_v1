package com.example.aalelyuh.myconverter;

/**
 * Created by Lelyuh-AA on 17.07.2017.
 */

import android.database.sqlite.SQLiteDatabase;

public class CoursesTable {

    public static final String TABLE_COURSES = "courses";
    public static final String COLUMN_COURSE_ID = "_id";
    public static final String COLUMN_COURSE_DATE = "date";
    public static final String COLUMN_COURSE_CODE = "code";
    public static final String COLUMN_COURSE_NOMINAL = "nominal";
    public static final String COLUMN_COURSE_NAME = "name";
    public static final String COLUMN_COURSE_VALUE = "value";

    public static final String COURSES_CREATE = "create table "
            + TABLE_COURSES + " ("
            + COLUMN_COURSE_ID + " integer primary key autoincrement, "
            + COLUMN_COURSE_DATE + " text not null, "
            + COLUMN_COURSE_CODE + " text not null, "
            + COLUMN_COURSE_NOMINAL + " integer not null, "
            + COLUMN_COURSE_NAME + " text not null, "
            + COLUMN_COURSE_VALUE + " text not null);";

    public static void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL(COURSES_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES + ";");
        onCreate(sqLiteDatabase);
    }
}
