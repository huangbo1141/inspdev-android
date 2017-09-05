package com.idragonit.inspection.utils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Utility Class.
 * support serveral functions. ( Date, String, ... ) 
 *
 * @author CJH
 * 
 */

public class Utils {
	
	private static final String DEFAULT_STRING="";
	private static final int DEFAULT_INT=0;

	/**
	 * If string is null then return default value, else no changes.
	 * 
	 * @param string
	 * @param def Default Value
	 * @return string
	 */
    public static String checkNull(String string, String def) {
        if (string == null || string.trim().toLowerCase().equals("null") || string.trim().length()==0)
            return def;
        
        return string;
    }

	/**
	 * Convert string to integer value.
	 * If string is null then return default value, else no changes.
	 * 
	 * @param string
	 * @param def Default Value
	 * @return integer
	 */
    public static int checkNull(String string, int def) {
        if (string == null || string.equals(""))
            return def;
        
        return getInt(string, def);
    }
    
	/**
	 * Convert string to long value.
	 * If string is null then return default value, else no changes.
	 * 
	 * @param string
	 * @param def Default Value
	 * @return long
	 */
    public static long checkNull(String string, long def) {
        if (string == null || string.equals(""))
            return def;
        
        return getLong(string);
    }

	/**
	 * Convert string to double value.
	 * If string is null then return default value, else no changes.
	 * 
	 * @param string
	 * @param def Default Value
	 * @return double
	 */
    public static double checkNull(String string, double def) {
        if (string == null || string.equals(""))
            return def;
        return getDouble(string);
    }

	public static float checkNull(String string, float def) {
		if (string == null || string.equals(""))
			return def;
		return getFloat(string);
	}

	/**
	 * Convert Long to long value.
	 * If string is null then return default value, else no changes.
	 * 
	 * @param string
	 * @param def Default Value
	 * @return long
	 */
    public static long checkNull(Long string, long def) {
        if (string == null)
            return def;
        
        return string.longValue();
    }

	/**
	 * Convert string to double value.
	 * If string is null then return default value, else no changes.
	 * 
	 * @param string
	 * @param def Default Value
	 * @return double
	 */
    public static double checkNull(Double string, double def) {
        if (string == null)
            return def;
        return string.doubleValue();
    }

	/**
	 * If string is null then return default value, else no changes.
	 * 
	 * @param string
	 * @return string
	 */
    public static String checkNull(String string) {
        return checkNull(string, DEFAULT_STRING);
    }

	/**
	 * Convert object to string.
	 * If string is null then return default value, else no changes.
	 * 
	 * @param string
	 * @return string
	 */
    public static String checkNull(Object string) {
    	if (string==null)
    		return "";
        return checkNull(string.toString(), DEFAULT_STRING);
    }

    /**
	 * Convert object to int.
	 * If string is null then return default value, else no changes.
	 * 
     * @param object
     * @param def
     * @return integer
     */
	public static int checkNull(Object object, int def) {
		return checkNull(object.toString(), def);
	}
    
    /**
     * Get current year.
     * E.g:
     * if Today is 2009/10/24, return <code>2009</code>.
     * 
     * @return year
     */
    public static String getYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
    	return sdf.format(new Date());
    }

    /**
     * Get current month.
     * E.g:
     * if Today is 2009/10/24, return <code>10</code>.
     * 
     * @return month
     */
    public static String getMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
    	return sdf.format(new Date());
    }
    
    /**
     * Get current day.
     * E.g:
     * if Today is 2009/10/24, return <code>24</code>.
     * 
     * @return day
     */
    public static String getDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
    	return sdf.format(new Date());
    }

    /**
     * Get today's date. 
     * E.g:
     * <code>20091203</code>
     *  
     * @return date	
     */
    public static String getToday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    	return sdf.format(new Date());
    }

    /**
     * Get today's date with format. 
     * E.g:
     * <code>2009-12-03</code>
     * <code>2009/12/03</code>
     * <code>2009|12|03</code>
     * 
     * @param format 
     * @return date	
     */
    public static String getToday(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + format + "MM" + format + "dd");
    	return sdf.format(new Date());
    }
    
    /**
     * Get today's date. 
     * E.g:
     * <code>20091203151317</code>
     *  
     * @return date	
     */
	public static String getTodayWithTime() {
		return getToday() + getTime();
	}

    /**
     * Get today's date with format. 
     * E.g:
     * <code>2009-12-03 11-12-12</code>
     * 
     * @param format 
     * @return date	
     */
    public static String getTodayWithTime(String format) {
    	return getToday(format) + " " + getTime(format);
    }

    /**
     * Get today's date with format. 
     * E.g:
     * <code>2009-12-03 11:12:12</code>
     * 
     * @param format1 The format of date
     * @param format2 The format of time 
     * @return date	
     */
    public static String getTodayWithTime(String format1, String format2) {
    	return getToday(format1) + " " + getTime(format2);
    }
    
    /**
     * Get today's date. 
     * E.g:
     * <code>200912031513</code>
     *  
     * @return date	
     */
	public static String getTodayWithMinute() {
		return getToday() + getTimeWithoutSecond();
	}
	

    /**
     * Get today's date with format. 
     * E.g:
     * <code>2009-12-03 11-12-12</code>
     * 
     * @param format 
     * @return date	
     */
    public static String getTodayWithMinute(String format) {
    	return getToday(format) + " " + getTimeWithoutSecond(format);
    }

    /**
     * Get today's date with format. 
     * E.g:
     * <code>2009-12-03 11:12:12</code>
     * 
     * @param format1 The format of date
     * @param format2 The format of time 
     * @return date	
     */
    public static String getTodayWithMinute(String format1, String format2) {
    	return getToday(format1) + " " + getTimeWithoutSecond(format2);
    }
	
    
    /**
     * Get current time 
     * E.g:
     * <code>121329</code>
     *  
     * @return time	
     */
	public static String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		return sdf.format(new Date());
	}
	
    /**
     * Get current time 
     * E.g:
     * <code>12:13:29</code>
     * <code>12.13.29</code>
     * 
     * @param format
     * @return time	
     */
	public static String getTime(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH"+format+"mm"+format+"ss");
		return sdf.format(new Date());
	}
    
    /**
     * Get current time 
     * E.g:
     * <code>1213</code>
     *  
     * @return time	
     */
	public static String getTimeWithoutSecond() {
		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		return sdf.format(new Date());
	}

	public static String getDateTime(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("MMMM, d, yyyy HH:mm");
		return sdf.format(date);
	}
	
    /**
     * Get current time 
     * E.g:
     * <code>12:13</code>
     * <code>12.13</code>
     * 
     * @param format
     * @return time	
     */
	public static String getTimeWithoutSecond(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH"+format+"mm");
		return sdf.format(new Date());
	}
	
	/**
	 * If smaller than 10, 0
	 * 
	 * @param value 
	 * @return string
	 */
	public static String fillZero10(int value){
		String tem="";
		if (value>=10){
			tem=String.valueOf(value);
		}
		else{
			tem="0"+String.valueOf(value);
		}
		return tem;
	}
	
	/**
	 * Fill zero string,  equals specified length.
	 * from start. prefix.
	 * 
	 * @param str
	 * @param len
	 * @return string
	 */
    public static String fillZero(String str, int len) {
        String tmp = "0000000000000000000000000000000000000000000000000000000000000000000000000"+str;
        return tmp.substring(tmp.length()-len);
    }

	/**
	 * Fill zero string,  equals specified length.
	 * from start. prefix.
	 * 
	 * @param str
	 * @param len
	 * @return string
	 */
    public static String fillZero(String str, String len) {
        int length = Integer.valueOf(len).intValue();    
        return fillZero(str, length);
    }

    /**
	 * Fill zero string,  equals specified length.
	 * from end. suffix.
	 * 
	 * @param str 
	 * @param len 
	 * @return string
	 */
	public static String fillZero2End(String str, int len) {
		String tmp = str+"0000000000000000000000000";
		return tmp.substring(0, len);
	}

    /**
	 * Fill nine string,  equals specified length.
	 * from end. suffix.
	 * 
	 * @param str 
	 * @param len 
	 * @return string
	 */
	public static String fillNine2End(String str, int len) {
		String tmp = str+"9999999999999999999999999";
		return tmp.substring(0,len);
	}

	/**
	 * Convert string to integer.
	 * 
	 * @param value
	 * @return integer
	 */
	public static int getInt(String value){
		return Integer.parseInt(value);
	}

	/**
	 * Convert string to integer.
	 * if null, return default value.
	 * 
	 * @param value
	 * @param defaultVal
	 * @return integer
	 */
	public static int getInt(String value, int defaultVal){
		int val = defaultVal;
		try{
			val = Integer.parseInt(value);
		}catch(Exception e){ }
		return val;
	}

	/**
	 * Convert string to long.
	 * 
	 * @param value
	 * @return long
	 */
	public static long getLong(String value){
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
        }

        return 0L;
	}

	/**
	 * Convert string to double.
	 * 
	 * @param value
	 * @return double
	 */
	public static double getDouble(String value){
		return Double.parseDouble(value);
	}

	public static float getFloat(String value){
		try {
			return Float.parseFloat(value);
		} catch (Exception e) {
		}

		return 0.0f;
	}

	/**
	 * Convert string to double.
	 * 
	 * @param value
	 * @param digit The length of digit
	 * @return double
	 */
	public static double getDouble(String value, int digit){
        try {
            double a = Double.parseDouble(value);
            return Math.round(a*digit*100) / (100*digit);
        } catch (Exception e) {}

        return 0;
	}

	/**
	 * Convert double to integer.
	 * 
	 * @param value
	 * @return integer
	 */
	public static int getInt(double value){
		return (int)value;
	}
	
	/**
	 * Convert string with utf-8 format.
	 * 
	 * @param value
	 * @return string 
	 */
	public static String encode(String value){
		String n_str=null;
		try{
			n_str=value;//new String(value.getBytes("ISO-8859-1"),"UTF-8");
		}catch (Exception q){
			n_str="";
		}
		return n_str;
	}
   
	/**
	 * Get end day of the current date.
	 * 
	 * @return day
	 */
	public static int getEndDayOfMonth(){
		int year = getInt(getYear());
		int month = getInt(getMonth());
		int days = 31;
	
		if (month == 1 || month ==3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12)
			days=31;
		else if (month==2){
			if (year % 400 == 0 )
				days = 29;
			else if (year % 100 == 0)
				days = 28;
			else if (year % 4 == 0 )
				days = 29;
			else
				days = 28;
		}else
			days=30;
		
		return days;
	}
	
	/**
	 * Get end day of the specified year, month.
	 * 
	 * @param year 
	 * @param month
	 * @return day
	 */
	public static int getEndDayOfMonth(int year, int month){
		int days = 31;
		
		if (month == 1 || month ==3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12)
			days=31;
		else if (month==2){
			if (year % 400 == 0 )
				days = 29;
			else if (year % 100 == 0)
				days = 28;
			else if (year % 4 == 0 )
				days = 29;
			else
				days = 28;
		}else
			days=30;
		
		return days;
	}

	/**
	 * Get ago string for difference days.
	 * E.g:
	 * <code>Yesterday</code>
	 * <code>A week ago</code>
	 *  
	 * @param today
	 * @param past
	 * @return string
	 */
	public static String getRelativeAgo(String today, String past){
		int t = getInt(today.substring(0,8));
		int p = getInt(past.substring(0,8));
		if (t == p)
			return getInt(past.substring(8,10)) + ":" + getInt(past.substring(10,12));
		if (t-p == 1)
			return "Yesterday";
		if (t-p == 7)
			return "A week ago";
		if (t-p == 30 || t-p == 31)
			return "A month ago";
		
		return (t-p) + " days ago";
	}
		
	/**
	 * Get the day before specified days.
	 * 
	 * @param date
	 * @param day
	 * @return string
	 */
	public static String beforeDay(String date, int day){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Date d = sdf.parse(date);
			d.setTime( d.getTime() - (long)day * 24 * 60 * 60 * 1000);
			date = sdf.format(d);
		} catch (Exception e) {
		}

		return date;
	}
	
	/**
	 * Get difference seconds.
	 * 
	 * @param time1
	 * @param time2
	 * @return integer
	 */
	public static int getDifferentTimes(String time1, String time2){
		String day1 = time1.substring(0, 8);
		String day2 = time2.substring(0, 8);
		
		if (!day1.equals(day2))
			return 1000;
		
		String hm1 = time1.substring(8);
		String hm2 = time2.substring(8);
		
		return getInt(hm1) - getInt(hm2);
	}

	/**
	 * Get difference days.
	 * 
	 * @param date1
	 * @param date2
	 * @return integer
	 */
	public static int getDifferenceDays(String date1, String date2){
		date1 = fromDisplayDate(date1);
		date2 = fromDisplayDate(date2);
		
		try{
			return Utils.getInt(date1) - Utils.getInt(date2);
		}catch (Exception e){}
		
		return 0;
	}

	/**
	 * Convert date with Display format.
	 * 
	 * @param date
	 * @return string
	 */
	public static String toDisplayDate(String date){
		String result = "";
		
		try{
			if (date.indexOf("-")>-1)
				return date;
			
			if (date.length()>4){
				if (date.length()>6){
					if (date.length()>8){
						if (date.length()>=12){
							result = date.substring(0, 4) + "/" + date.substring(4,6) + "/" + date.substring(6, 8) + " " + date.substring(8,10) + ":" + date.substring(10,12);
						}else{
							result = date.substring(0, 4) + "/" + date.substring(4,6) + "/" + date.substring(6, 8) + " " + date.substring(8);
						}
					}else{
						result = date.substring(0, 4) + "/" + date.substring(4,6) + "/" + date.substring(6);
					}
				}else{
					result = date.substring(0, 4) + "/" + date.substring(4);
				}
			}else
				result = date;
		}catch (Exception e){}
		
		return result;
	}
	
	/**
	 * Convert date with default format.
	 * 
	 * @param date
	 * @return string
	 */
	public static String fromDisplayDate(String date){
		return date.replaceAll("-", "");
	}
	
	/**
	 * Get date for searching.
	 * 
	 * @param date
	 * @return string
	 */
	public static String toSearchDate(String date){
		return date + " 23:59";
	}
	
	/**
	 * Get Timestamp.
	 * 
	 * @param value
	 * @return long
	 */
	public static Long getTimeStamp(String value){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Date date = sdf.parse(value);
			java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
			
//			Log.i(timestamp.getTime() + "");
//			return timestamp.toString();
			return timestamp.getTime();
		}catch (Exception e){}
		
		return (long)0;
	}

	/**
	 * Get extension of file.
	 * E.g:
	 * <code>txt</code>
	 * <code>xml</code>
	 * 
	 * @param file
	 * @return string
	 */
	public static String getExtension(String file){
		int i = file.lastIndexOf(".");
		try{
			if (i!=-1){
				String ext = file.substring(i+1, file.length());
				if (ext.length()==3 || ext.length() == 4)
					return ext;
			}
		}catch (Exception e){}
		return "";
	}

	/**
	 * Get random key.
	 * 
	 * @param length
	 * @return string
	 */
	public static String getKey(int length){
		String data = "";
		String charset = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random random = new Random();
		
		for (int i=0; i<length; i++){
			int index = random.nextInt(charset.length());
			data = data + charset.substring(index, index+1);
		}
		
		return data;
	}
	
	/**
	 * Get UUID.
	 * 
	 * @param length
	 * @return string
	 */
	public static String getUUID(int length){
		int c = (int)Math.ceil(length / 32);
		String result = "";
		for (int i=0; i<c; i++)
			result += UUID.randomUUID().toString().replaceAll("-", "");
		
		if (result.length()>length)
			result = result.substring(0, length);
		return result;
	}
	
	/**
	 * Return string with bracket.
	 * 
	 * @param type The type of bracket  					
	 * 					1: ()
	 * 					2: []
	 * 					3: 【】
	 * @param value
	 * @return string
	 */
	public static String bracket(int type, String value){
		if (value.length()==0)
			return value;
		
		if (type==1)
			return "(" + value + ")";
		if (type==2)
			return "[" + value + "]";
		if (type==3)
			return "【" + value + "】";
		return value;
	}
	
	/**
	 * Replace Quote to Space.
	 * 
	 * @param str
	 * @return string
	 */

	public static String toStringfromCSV(String str){
		return str.replaceAll("\"", "");
	}

	public static String getTodayForInspection() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		return sdf.format(new Date());
	}

}
