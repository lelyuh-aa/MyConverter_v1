package com.example.aalelyuh.myconverter.xml;

import com.example.aalelyuh.myconverter.CoursesTable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;

/**
 * Created by Lelyuh-AA on 19.07.2017.
 */

@Root(name = "Valute")
public class Course {

    private String mCode;
    private int mNominal;
    private String mName;
    private String mValueTemp; // временное поле для сохранения значения из xml, конвертация в @Commit
    private double mValue;

    @Element(name = "CharCode")
    public String getCode() {
        return mCode;
    }

    @Element(name = "CharCode")
    public void setCode(String code) {
        this.mCode = code;
    }

    @Element(name = "Nominal")
    public int getNominal() {
        return mNominal;
    }

    @Element(name = "Nominal")
    public void setNominal(int nominal) {
        this.mNominal = nominal;
    }

    @Element(name = "Name")
    public String getName() {
        return mName;
    }

    @Element(name = "Name")
    public void setName(String name) {
        this.mName = name;
    }

    @Element(name = "Value")
    public String getValueTemp() {
        return mValueTemp;
    }

    @Element(name = "Value")
    public void setmValueTemp(String value) {
        this.mValueTemp = value;
    }

    public double getValue() { return mValue; }

    @Commit
    public void decode() {
        // преобразование временных полей
        String valueTemp = mValueTemp;
        valueTemp = valueTemp.replace(",",".");
        mValue = Double.parseDouble(valueTemp);

        // TODO падежи в наименованиях курсов каким-то  образом поправить...
    }
}
