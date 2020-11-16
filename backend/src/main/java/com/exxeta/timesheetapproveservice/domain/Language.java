package com.exxeta.timesheetapproveservice.domain;

import java.util.Locale;
import java.util.ResourceBundle;

public class Language {
    public static Locale locale = new Locale("de");
    public static String bundleName = "bundle";
    public static ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);
}
