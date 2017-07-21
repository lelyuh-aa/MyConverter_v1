package com.example.aalelyuh.myconverter.xml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Lelyuh-AA on 19.07.2017.
 */

@Root(name = "ValCurs")
public class CourseList {

    private String mCourseDate;
    private List<Course> mCourseList;

    @Attribute(name = "Date")
    public String getCourseDate() {
        return mCourseDate;
    }

    @Attribute(name = "Date")
    public void setCourseDate(String courseDate) {
        this.mCourseDate = courseDate;
    }

    @ElementList(inline = true, name = "Valute")
    public List<Course> getCourseList() {
        return mCourseList;
    }

    @ElementList(inline = true, name = "Valute")
    public void setCourseList(List<Course> courseList) {
        this.mCourseList = courseList;
    }
}
