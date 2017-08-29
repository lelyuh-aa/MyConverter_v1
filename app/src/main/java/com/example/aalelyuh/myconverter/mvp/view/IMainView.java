package com.example.aalelyuh.myconverter.mvp.view;

import com.arellomobile.mvp.MvpView;

/**
 * @author Лелюх Александр
 */

public interface IMainView extends MvpView {

    void notifyDownload(String courseDate);

    void showErrorMessage();

    void setActualCourseDate(String currentDate);

    void setConvertedSum(String convertSum);

}
