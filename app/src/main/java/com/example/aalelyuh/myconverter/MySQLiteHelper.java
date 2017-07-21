package com.example.aalelyuh.myconverter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lelyuh-AA on 17.07.2017.
 */

public class MySQLiteHelper extends SQLiteOpenHelper{

    public static final String COURSES_DATABASE = "courses.db";
    public static final int COURSES_DB_VERSION = 1;

    public MySQLiteHelper(Context context) {
        super(context, COURSES_DATABASE, null, COURSES_DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        CoursesTable.onCreate(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        CoursesTable.onUpgrade(sqLiteDatabase, i, i1);
    }
}
