package com.idragonit.inspection.models;

/**
 * Created by CJH on 2016.01.23.
 */
public class StorageInfo {

    public String recipient_email;
    public CheckingInfo checking_list;

    public boolean is_exist;
    public SyncInfo inspection;

    public StorageInfo() {
        recipient_email = "";
        checking_list = new CheckingInfo();

    }

    public void init() {
        recipient_email = "";
        if (checking_list==null)
            checking_list = new CheckingInfo();
        else
            checking_list.init();

        initSync();
    }

    public void initSync() {
        is_exist = false;
        if (inspection==null)
            inspection = new SyncInfo();
        else
            inspection.init();
    }
}
