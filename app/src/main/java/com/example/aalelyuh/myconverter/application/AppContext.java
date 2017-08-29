package com.example.aalelyuh.myconverter.application;

import android.app.Application;

/**
 * Класс для получения контекста приложения там, где это будет нужно
 * @author Лелюх Александр
 */

public class AppContext extends Application{

    private static AppContext mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppContext = new AppContext();
    }

    public static AppContext getAppContext() {
        return mAppContext;
    }
}
