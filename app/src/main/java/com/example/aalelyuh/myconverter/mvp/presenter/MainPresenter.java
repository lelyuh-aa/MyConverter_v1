package com.example.aalelyuh.myconverter.mvp.presenter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.aalelyuh.myconverter.CoursesTable;
import com.example.aalelyuh.myconverter.DownloadService;
import com.example.aalelyuh.myconverter.MySQLiteHelper;
import com.example.aalelyuh.myconverter.application.AppContext;
import com.example.aalelyuh.myconverter.mvp.view.IMainView;

/**
 * @author Лелюх Александр
 */

@InjectViewState
public class MainPresenter extends MvpPresenter<IMainView>{

    public static final String NATIVE_VALUTE = "RUB";

    private MySQLiteHelper mHelper;

    public MainPresenter() {
        // инициализация хэлпера
        mHelper = new MySQLiteHelper(AppContext.getAppContext());
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();

        //Context context = AppContext.getAppContext();

        // инициализация хэлпера
        //mHelper = new MySQLiteHelper(context);

        // запуск сервиса только при первом запуске активити
        /*Intent intent = new Intent(context, DownloadService.class);
        context.startService(intent);*/
    }

    public boolean checkInputFields (String inputSum, String valuteIn, String valuteOut) {

        if(!inputSum.isEmpty() && !valuteIn.isEmpty() && !valuteOut.isEmpty()
                && !valuteIn.equals(valuteOut)) {
            return true;
        }
        // покажем сообщение с предупреждением
        getViewState().showErrorMessage();
        return false;
    }

    public void getCurrentCoursesDate() {

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

        if (!currentDate.isEmpty()) {
            getViewState().setActualCourseDate(currentDate);
        }

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

    public void convertSum(double convSum, String valuteIn, String valuteOut) {

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

            double rowCourse;
            int rowNominal;
            String rowValute;
            double resultSum;

            // получаем значения из курсора
            for (int i = 0; i < count; i++) {

                cursor.moveToNext();
                rowValute = cursor.getString(
                        cursor.getColumnIndex(CoursesTable.COLUMN_COURSE_CODE));
                rowNominal = cursor.getInt(
                        cursor.getColumnIndex(CoursesTable.COLUMN_COURSE_NOMINAL));
                rowCourse = cursor.getDouble(
                        cursor.getColumnIndex(CoursesTable.COLUMN_COURSE_VALUE));

                if (rowValute.equals(valuteIn)) {
                    nominalValuteIn = rowNominal;
                    courseValuteIn = rowCourse;
                } else if (rowValute.equals(valuteOut)) {
                    nominalValuteOut = rowNominal;
                    courseValuteOut = rowCourse;
                }
            }

            // выполняем конвертацию
            resultSum = convSum * (courseValuteIn / nominalValuteIn)
                    * (nominalValuteOut / courseValuteOut);

            // выводим результат во view
            getViewState().setConvertedSum(((Double) resultSum).toString());

            cursor.close();
        }
        else
            getViewState().showErrorMessage();
    }
}
