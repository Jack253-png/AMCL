package com.mcreater.amcl.util;

import com.sun.javafx.css.CalculatedValue;
import com.sun.javafx.geom.Vec2d;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;

public class LauncherVersionGetter {
    public static void main(String[] args) throws IOException {
        Calendar c = Calendar.getInstance();
        Vector<String> works = new Vector<>();
        works.add("a");
        works.add("b");
        works.add("c");
        works.add("d");
        works.add("e");
        works.add("f");
        works.add("g");
        System.out.printf("%dw%d%s%n", c.get(Calendar.YEAR) % 100, c.get(Calendar.WEEK_OF_YEAR), works.get(0));
    }
}
