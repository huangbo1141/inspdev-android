package com.idragonit.inspection.models;

import com.idragonit.inspection.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by CJH on 8/18/2016.
 */

public class UnitInfo {

    public int no;
    public String i_supply;
    public String i_return;

    public UnitInfo() {
        no = 0;
        i_supply = "";
        i_return = "";
    }

    public void init() {
        no = 0;
        i_supply = "";
        i_return = "";
    }

    public UnitInfo(int no) {
        this.no = no;
        i_supply = "";
        i_return = "";
    }

    public UnitInfo(int no, String i_supply, String i_return) {
        this.no = no;
        this.i_supply = i_supply;
        this.i_return = i_return;
    }

    public void copy(UnitInfo unit) {
        if (unit==null)
            return;;

        no = unit.no;
        i_supply = unit.i_supply;
        i_return = unit.i_return;
    }

    public boolean isValid() {
        if (no>0 && i_supply.length()>0 && i_return.length()>0)
            return true;
        return false;
    }

    public String toJSON() {
        try {
            JSONObject result = new JSONObject();

            result.put("no", no+"");
            result.put("supply", i_supply);
            result.put("return", i_return);

            return result.toString();
        } catch (Exception e) {}

        return "";
    }

    public void initWithJSON(String json) {
        if (json.length()==0)
            return;

        try{
            JSONObject result = new JSONObject(json);
            no = Utils.checkNull(result.getString("no"), 0);
            i_supply = Utils.checkNull(result.getString("supply"));
            i_return = Utils.checkNull(result.getString("return"));
        }catch (Exception e){}
    }

}
