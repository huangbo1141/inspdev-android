package com.idragonit.inspection.models;

/**
 * Created by CJH on 2016.01.23.
 */
public class UserInfo {

    public String id;
    public String email;
    public String password;
    public String firstname;
    public String lastname;
    public String phone;

    // added 2017.1.30
    public String address;
    public float fee;

    public UserInfo() {
        id = "";
        email = "";
        password = "";
        firstname = "";
        lastname = "";
        phone = "";
        address = "";
        fee = 0;
    }

    public void init() {
        id = "";
        email = "";
        password = "";
        firstname = "";
        lastname = "";
        phone = "";
        address = "";
        fee = 0;
    }

    public void copy(UserInfo user) {
        id = user.id;
        email = user.email;
        password = user.password;
        firstname = user.firstname;
        lastname = user.lastname;
        phone = user.phone;
        address = user.address;
        fee = user.fee;
    }
}
