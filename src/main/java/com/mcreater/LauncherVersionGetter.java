package com.mcreater;

import com.mcreater.amcl.util.J8Utils;

import java.util.Calendar;

public class LauncherVersionGetter {
    public static void main(String[] args) {
        int week = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int work = 0;
        char workS = (char) (97 + work);
        System.out.println(year % 100 + "w" + (week >= 10 ? week : "0" + week) + workS);
    }
}
