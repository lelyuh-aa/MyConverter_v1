package com.example.aalelyuh.myconverter.xml;

import com.example.aalelyuh.myconverter.CoursesTable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Lelyuh-AA on 19.07.2017.
 */

@Root(name = "Valute")
public class Course {

    private String mCode;
    private int mNominal;
    private String mName;

    // вместо double пришлось здесь использовать String, так как в файле разделитель "," и при десериализации возникает exception
    // преобразовывать в правильный double будем при самой конвертации
    private String mValue;

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
    public String getValue() {
        return mValue;
    }

    @Element(name = "Value")
    public void setValue(String value) {
        this.mValue = value;
    }
}
