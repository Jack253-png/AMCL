package com.mcreater.amcl.test;

import java.util.Calendar;

public class testMain {
    public static void main(String[] args){
        Calendar c = Calendar.getInstance();
        System.out.println(c.get(Calendar.WEEK_OF_YEAR));
    }
}
