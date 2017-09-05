package com.idragonit.inspection.models;

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
    }
}
