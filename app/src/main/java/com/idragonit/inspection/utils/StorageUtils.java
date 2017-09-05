package com.idragonit.inspection.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by CJH on 2015-06-28.
 */

public class StorageUtils {
    static final String dir = "/Inspection/";

    public static void makeAppDirectory(){
        try{
            String storagePath = Environment.getExternalStorageDirectory().toString();
            String appPath = storagePath + dir;

            File file = new File(appPath);
            if (!file.exists()) {
                file.mkdir();
            }
        }catch (Exception e){}
    }

    public static void initAppDirectory() {
        try{
            String storagePath = Environment.getExternalStorageDirectory().toString();
            String appPath = storagePath + dir;

            File file = new File(appPath);
            if (file.exists()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    if (f.isFile())
                        f.delete();
                }
            }
        }catch (Exception e){}
    }

    public static String getAppDirectory(){
        try{
            String storagePath = Environment.getExternalStorageDirectory().toString();
            String appPath = storagePath + dir;
            return appPath;
        }catch (Exception e){}

        return "";
    }

    public static String makeWorkingDirectory(String date){
        try{
            String storagePath = Environment.getExternalStorageDirectory().toString();
            String appPath = storagePath + dir + date + "/";

            File file = new File(appPath);
            if (!file.exists()) {
                if (file.mkdir())
                    return file.getAbsolutePath();
                return "";
            }

            return file.getAbsolutePath();
        }catch (Exception e){}

        return "";
    }
}
