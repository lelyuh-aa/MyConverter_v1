package com.example.aalelyuh.myconverter;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.example.aalelyuh.myconverter.xml.Course;
import com.example.aalelyuh.myconverter.xml.CourseList;

import org.simpleframework.xml.core.Persister;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by Lelyuh-AA on 17.07.2017.
 */

public class DownloadService extends IntentService {

    public static final String DOWNLOAD_URL = "DOWNLOAD_URL";
    public static final String DOWNLOAD_COMPLETE = "DOWNLOAD_COMPLETE";
    public static final String DOWNLOAD_FAIL = "DOWNLOAD_FAIL";

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        // проверка на доступность Интернета (мобильный, Wi-Fi)
        if (!checkNetwork()) {
            sendResult(false, "");
            return;
        }

        String courseDate = "";
        String courseUrl = intent.getStringExtra(DOWNLOAD_URL);

        if (!courseUrl.isEmpty()) {
            try {
                // создаем удаленное соединение
                URL url = new URL(courseUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream stream = connection.getInputStream();
                Reader reader = new InputStreamReader(stream);

                // десериализация XML
                Persister serializer = new Persister();
                CourseList courseList = serializer.read(CourseList.class, reader, false);

                // сохранение результата в БД
                if (courseList.getCourseList().size() > 0) {

                    MySQLiteHelper helper = new MySQLiteHelper(this);
                    ContentValues cValues = new ContentValues();
                    int rowCount = 0;

                    // подготовка к удалению текущих курсов
                    Cursor cursor = helper.getReadableDatabase().query(
                            CoursesTable.TABLE_COURSES,
                            new String[] {CoursesTable.COLUMN_COURSE_ID},
                            null,null,null,null,
                            CoursesTable.COLUMN_COURSE_ID + " desc"
                    );

                    if (cursor.getCount() > 0) {
                        cursor.move(1);
                        rowCount = cursor.getInt(cursor.getColumnIndex(CoursesTable.COLUMN_COURSE_ID));
                        cursor.close();
                    }

                    // добавление новых курсов
                    for (Course course : courseList.getCourseList()) {
                        cValues.put(CoursesTable.COLUMN_COURSE_DATE, courseList.getCourseDate());
                        cValues.put(CoursesTable.COLUMN_COURSE_CODE, course.getCode());
                        cValues.put(CoursesTable.COLUMN_COURSE_NOMINAL, course.getNominal());
                        cValues.put(CoursesTable.COLUMN_COURSE_NAME, course.getName());
                        cValues.put(CoursesTable.COLUMN_COURSE_VALUE, course.getValue());
                        helper.getWritableDatabase().insert(CoursesTable.TABLE_COURSES, null, cValues);
                        cValues.clear();
                    }

                    // после успешной записи новых курсов удалим из базы старые
                    helper.getWritableDatabase().delete(
                            CoursesTable.TABLE_COURSES,
                            CoursesTable.COLUMN_COURSE_ID + " <=?",
                            new String[] {Integer.toString(rowCount)}
                    );
                }

                // отправка уведомления об успешной загрузке курсов
                sendResult(true, courseList.getCourseDate());
            } catch (Exception e) {
                e.printStackTrace();
                sendResult(false, "");
            }
        }
        else
            sendResult(false, "");
    }

    private boolean checkNetwork() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI
                    || info.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
        }

        return false;
    };

    private void sendResult(boolean isResult, String courseDate) {
        Intent courseLoadResultIntent = new Intent();
        courseLoadResultIntent.setAction(isResult ? DOWNLOAD_COMPLETE : DOWNLOAD_FAIL);
        if (!courseDate.isEmpty()) {
            courseLoadResultIntent.putExtra(CoursesTable.COLUMN_COURSE_DATE, courseDate);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(courseLoadResultIntent);
    }
}
