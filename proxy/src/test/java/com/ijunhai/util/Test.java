package com.ijunhai.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Test {

    public static DateTimeFormatter formatA = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    public static DateTimeFormatter formatB = DateTimeFormat.forPattern("yyyy-MM-dd");
    public static DateTime startTime;
    public static DateTime endTime;
    public static DateTime startOfDay;

    public static void main(String[] args) {
        String start = "2018-05-01 12:23:44";
        String end = "2018-05-02 14:33:46";
        startOfDay = new DateTime().withTimeAtStartOfDay();
        if (start.contains(" ") && end.contains(" ")) {
            startTime = DateTime.parse(start, formatA);
            endTime = DateTime.parse(end, formatA);
        }else{
            startTime = DateTime.parse(start, formatB);
            endTime = DateTime.parse(end, formatB);
        }


//        endTime = DateTime.parse(end, formatB).compareTo(startOfDay) > 0 ? startOfDay : DateTime.parse(end, formatB);

        System.out.println("startOfDay :" + startOfDay);
        System.out.println("startTime :" + startTime);
        System.out.println("endTime :" + endTime);

        System.out.println("-----------------------------");

        System.out.println("startOfDay :" + startOfDay.toString("yyyy-MM-dd"));
        System.out.println("startTime :" + startTime.toString("yyyy-MM-dd"));
        System.out.println("endTime :" + endTime.toString("yyyy-MM-dd"));


    }
}
