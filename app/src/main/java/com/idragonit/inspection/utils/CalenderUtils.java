package com.idragonit.inspection.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by CJH on 2015-06-27.
 */

public class CalenderUtils {

    /**
     * Get today's date.
     * E.g:
     * <code>20091203</code>
     *
     * @return date
     */
    public static String getTodayWith8Chars() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date());
    }

    public static String getTodayWith14Chars() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }

    /**
     * Get date.
     * E.g:
     * <code>20091203</code>
     *
     * @return date
     */
    public static String getDateWith8Chars(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(date);
    }

    /**
     * Get today's date with format.
     * E.g:
     * <code>2009-12-03</code>
     * <code>2009/12/03</code>
     * <code>2009|12|03</code>
     *
     * @param symbol
     * @return date
     */
    public static String getToday(String symbol) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + symbol + "MM" + symbol + "dd");
        return sdf.format(new Date());
    }

    public static String getDateWithSymbol(Date date, String symbol){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + symbol + "MM" + symbol + "dd");
        return sdf.format(date);
    }

    public static String getDateWithIsoFormat(Date date, int day){
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy, EEE");
        date.setTime(date.getTime() + day * 24 * 60 * 60 * 1000);
        return sdf.format(date);
    }

    public static String getDateWithIsoFormat(String date){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy");
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
            Date d = sdf1.parse(date);
            return sdf.format(d);
        }catch (Exception e){}

        return date;
    }

    public static String getDateWithIso2Format(String date){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
            Date d = sdf1.parse(date);
            return sdf.format(d);
        }catch (Exception e){}

        return date;
    }

    public static Date getDateWithIso3Format(String date){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = sdf.parse(date);
            return d;
        }catch (Exception e){}

        return null;
    }

    public static String getInspectionDateFromIsoFormat(String date){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d = sdf.parse(date);

            SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd/yyyy");
            return sdf.format(d);
        }catch (Exception e){}

        return date;
    }

}
