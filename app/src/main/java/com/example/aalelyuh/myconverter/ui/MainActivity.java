package com.example.aalelyuh.myconverter.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.aalelyuh.myconverter.R;
import com.example.aalelyuh.myconverter.mvp.presenter.MainPresenter;
import com.example.aalelyuh.myconverter.mvp.view.IMainView;

public class MainActivity extends MvpAppCompatActivity implements IMainView {

    @InjectPresenter
    MainPresenter mainPresenter;

    private Button mConvert;
    private EditText mConvertVal;
    private EditText mConvertedVal;
    private EditText mConvertSum;
    private EditText mConvertedSum;
    private TextView mCourseInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myconverter_activity);

        // найдем все view
        mConvert = (Button) findViewById(R.id.button);
        mConvertVal = (EditText) findViewById(R.id.val_from);
        mConvertedVal = (EditText) findViewById(R.id.val_to);
        mConvertSum = (EditText) findViewById(R.id.summ_in);
        mConvertedSum = (EditText) findViewById(R.id.summ_out);
        mCourseInfo = (TextView) findViewById(R.id.course_info);

        // проверим и покажем дату загруженных ранее курсов, если есть
        //mainPresenter.getCurrentCoursesDate();

        // конвертация
        mConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String inputSumS = mConvertSum.getText().toString();
                String valuteIn  = mConvertVal.getText().toString();
                String valuteOut = mConvertedVal.getText().toString();

                if (mainPresenter.checkInputFields(inputSumS, valuteIn, valuteOut)) {
                    double inputSumD = Double.parseDouble(inputSumS);
                    mainPresenter.convertSum(inputSumD, valuteIn, valuteOut);
                }
            }
        });
    }

    @Override
    public void notifyDownload (String courseDate) {
        if (!courseDate.isEmpty()) {
            setInfo(courseDate);
        }
        Toast.makeText(this,
                courseDate.isEmpty() ? "Не удалось загрузить актуальные курсы валют! Проверьте настройки интернета!"
                        : "Загружены курсы валют на " + courseDate,
                Toast.LENGTH_LONG).show();

    }

    @Override
    public void setConvertedSum(String convertedSum) {
        mConvertedSum.setText(convertedSum);
    }

    @Override
    public void showErrorMessage() {
        Toast.makeText(this,
                "Проверьте корректность заполнения полей!",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void setActualCourseDate(String currentDate) {
        setInfo(currentDate);
    }

    private void setInfo (String cDate) {
        String info = "Курсы актуальны на " + cDate;
        mCourseInfo.setText(info);
    }
}

