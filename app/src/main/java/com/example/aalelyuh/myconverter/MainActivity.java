package com.example.aalelyuh.myconverter;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String COURSE_URL = "http://www.cbr.ru/scripts/XML_daily.asp";
    public static final String NATIVE_VALUTE = "RUB";

    private MyReciever mReciever;
    private MySQLiteHelper mHelper = new MySQLiteHelper(this);;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myconverter_activity);

        // найдем все view
        Button button = (Button) findViewById(R.id.button);
        final EditText convertSum = (EditText) findViewById(R.id.summ_in);
        final EditText convertedSum = (EditText) findViewById(R.id.summ_out);
        final EditText convertVal = (EditText) findViewById(R.id.val_from);
        final EditText convertedVal = (EditText) findViewById(R.id.val_to);

        // проверим и покажем дату загруженных ранее курсов, если есть
        String currentDate = getCurrentCoursesDate();
        if (!currentDate.isEmpty()) {
            TextView courseInfoView = (TextView) findViewById(R.id.course_info);
            courseInfoView.setText("Курсы актуальны на " + currentDate);
        }

        // запускаем сервис загрузки курсов только при первом запуске приложения
        if (savedInstanceState == null) {
            Intent intent = new Intent(this, DownloadService.class);
            intent.putExtra(DownloadService.DOWNLOAD_URL, COURSE_URL);
            startService(intent);
        }

        // конвертация
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // проверки заполнения полей пока все здесь
                // потом имеет смысл сделать провекру отдельно на каждое view при вводе данных
                if (!convertSum.getText().toString().isEmpty()
                        && !convertVal.getText().toString().isEmpty()
                        && !convertedVal.getText().toString().isEmpty()
                        && !convertVal.getText().toString().equals(convertedVal.getText().toString())) {

                    double convSum = Double.parseDouble(convertSum.getText().toString());
                    String valuteIn = convertVal.getText().toString();
                    String valuteOut = convertedVal.getText().toString();
                    int nominalValuteIn = 1, nominalValuteOut = 1;
                    double courseValuteIn = 1d, courseValuteOut = 1d;

                    // получаем курсор из БД и берем из него нужные значения
                    // курсор может вернуть 0 строк, это ошибка в любом случае (в оба введены ошибочные данные или рубли, или в БД пусто)
                    // 1 строка - либо обычная конвертация c рублями, либо ошибка ввода второй валюты
                    // 2 строки - кросс-конвертация

                    Cursor cursor = getCurrentCourses(valuteIn, valuteOut);
                    int count = cursor.getCount();

                    if (count == 1 && (valuteIn.equals(NATIVE_VALUTE) || valuteOut.equals(NATIVE_VALUTE))
                            || count == 2) {

                            String tempCourse;
                            String rowValute;
                            double resultSum;

                            // получаем значения из курсора
                            for (int i = 0; i < count; i++) {

                                cursor.moveToNext();
                                rowValute = cursor.getString(
                                        cursor.getColumnIndex(CoursesTable.COLUMN_COURSE_CODE));
                                tempCourse = cursor.getString(
                                        cursor.getColumnIndex(CoursesTable.COLUMN_COURSE_VALUE));
                                tempCourse = tempCourse.replace(",", ".");

                                if (rowValute.equals(valuteIn)) {
                                    nominalValuteIn = cursor.getInt(
                                            cursor.getColumnIndex(CoursesTable.COLUMN_COURSE_NOMINAL));
                                    courseValuteIn = Double.parseDouble(tempCourse);
                                } else if (rowValute.equals(valuteOut)) {
                                    nominalValuteOut = cursor.getInt(
                                            cursor.getColumnIndex(CoursesTable.COLUMN_COURSE_NOMINAL));
                                    courseValuteOut = Double.parseDouble(tempCourse);
                                };
                            }

                            // выполняем конвертацию
                            resultSum = convSum * (courseValuteIn / nominalValuteIn)
                                    * (nominalValuteOut / courseValuteOut);

                            // выводим результат во view
                            convertedSum.setText(((Double) resultSum).toString());

                            cursor.close();
                    }
                    else
                        makeText();
                }
                else
                    makeText();
            }
        });
    }

    // регистрируем ресивер на получение сообщений от сервиса
    @Override
    protected void onResume() {
        super.onResume();
        mReciever = new MyReciever(this);
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(mReciever, new IntentFilter(DownloadService.DOWNLOAD_COMPLETE));
        manager.registerReceiver(mReciever, new IntentFilter(DownloadService.DOWNLOAD_FAIL));
    }

    // снятие регистрации ресивера
    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReciever);
    }

    private Cursor getCurrentCourses(String convertVal, String convertedVal) {
        Cursor cursor = mHelper.getReadableDatabase().query(
                CoursesTable.TABLE_COURSES,
                null,
                CoursesTable.COLUMN_COURSE_CODE + " in (?,?) ",
                new String[] {convertVal,convertedVal},
                null, null,
                CoursesTable.COLUMN_COURSE_ID + " desc"
                );
        return cursor;
    }

    private String getCurrentCoursesDate() {
        String currentDate = "";
        Cursor cursor = mHelper.getReadableDatabase().query(
                CoursesTable.TABLE_COURSES,
                new String[] {CoursesTable.COLUMN_COURSE_DATE},
                null, null, null, null,
                CoursesTable.COLUMN_COURSE_ID + " desc",
                "1"
        );
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            currentDate = cursor.getString(
                    cursor.getColumnIndex(CoursesTable.COLUMN_COURSE_DATE));
        }
        cursor.close();
        return  currentDate;
    }

    public void notifyDownload(String courseDate) {
        if (!courseDate.isEmpty()) {
            TextView courseInfoView = (TextView) findViewById(R.id.course_info);
            courseInfoView.setText("Курсы актуальны на " + courseDate);
        }
        Toast.makeText(this,
                courseDate.isEmpty() ? "Не удалось загрузить актуальные курсы валют! Проверьте настройки интернета!"
                        : "Загружены курсы валют на " + courseDate,
                Toast.LENGTH_LONG).show();

    }

    private void makeText() {
        Toast.makeText(MainActivity.this,
                "Проверьте корректность заполнения полей!",
                Toast.LENGTH_LONG).show();
    }
}

