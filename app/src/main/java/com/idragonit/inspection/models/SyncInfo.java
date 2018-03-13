package com.idragonit.inspection.models;

import android.database.Cursor;

import com.idragonit.inspection.utils.Utils;

import java.io.Serializable;

/**
 * Created by CJH on 2016.01.28.
 */
public class SyncInfo implements Serializable {

    public int inspection_id;
    public int requested_inspection_id;
    public int edit_inspection_id;

    public int user_id;
    public int type;
    public String job_number;
    public String date;
    public String data;
    public String email;
    public String left;
    public String right;
    public String front;
    public String back;
    public String comment;
    public String unit;
    public String extra = "";

    public static  SyncInfo parseCursor(Cursor cursor){
        try{
            SyncInfo item = new SyncInfo();
            item.inspection_id = Utils.checkNull(cursor.getInt(0), 0);
            item.type = Utils.checkNull(cursor.getInt(1), 0);
            item.job_number = Utils.checkNull(cursor.getString(2));
            item.data = Utils.checkNull(cursor.getString(3));
            item.email = Utils.checkNull(cursor.getString(4));
            item.left = Utils.checkNull(cursor.getString(5));
            item.right = Utils.checkNull(cursor.getString(6));
            item.front = Utils.checkNull(cursor.getString(7));
            item.back = Utils.checkNull(cursor.getString(8));
            item.comment = Utils.checkNull(cursor.getString(9));
            item.date = Utils.checkNull(cursor.getString(10));
            item.requested_inspection_id = Utils.checkNull(cursor.getInt(11), 0);
            item.unit = Utils.checkNull(cursor.getString(12));
            item.edit_inspection_id= Utils.checkNull(cursor.getInt(13), 0);
            item.extra = Utils.checkNull(cursor.getString(14));
            return item;
        }catch (Exception ex){

        }
        return null;
    }
    public SyncInfo() {
        inspection_id = 0;
        requested_inspection_id = 0;
        edit_inspection_id = 0;

        user_id = 0;
        type = 0;
        job_number = "";
        date = "";
        data = "";
        email = "";
        left = "";
        right = "";
        front = "";
        back = "";
        comment = "";
        unit = "";
        extra = "";
    }

    public void init() {
        inspection_id = 0;
        requested_inspection_id = 0;
        edit_inspection_id = 0;

        user_id = 0;
        type = 0;
        job_number = "";
        date = "";
        data = "";
        email = "";
        left = "";
        right = "";
        front = "";
        back = "";
        comment = "";
        unit = "";
        extra = "";
    }
}
