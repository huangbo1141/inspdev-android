package com.idragonit.inspection.models;

/**
 * Created by CJH on 3/22/2016.
 */
public class SpinnerInfo {

    public int id;
    public String name;
    public boolean selected;

    public SpinnerInfo() {
        this.id = 0;
        this.name = "";
        selected = false;
    }

    public SpinnerInfo(int id, String name) {
        this.id = id;
        this.name = name;
        selected = false;
    }

    public void init() {
        this.id = 0;
        this.name = "";
        selected = false;
    }

    @Override
    public String toString() {
        return name;
    }
}
