package com.example.aalelyuh.myconverter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.lang.ref.WeakReference;

/**
 * Created by Lelyuh-AA on 18.07.2017.
 */

public class MyReciever extends BroadcastReceiver{

    private WeakReference<MainActivity> mainActivityRef;

    public MyReciever(MainActivity activity) {
        this.mainActivityRef = new WeakReference<>(activity);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (!action.isEmpty() && mainActivityRef != null) {

            MainActivity mainActivity = mainActivityRef.get();

            if (action.equals(DownloadService.DOWNLOAD_COMPLETE)) {
                String courseDate = intent.getStringExtra(CoursesTable.COLUMN_COURSE_DATE);
                mainActivity.notifyDownload(courseDate);
            } else if (action.equals(DownloadService.DOWNLOAD_FAIL)) {
                mainActivity.notifyDownload("");
            }
            ;
        };

    }
}
