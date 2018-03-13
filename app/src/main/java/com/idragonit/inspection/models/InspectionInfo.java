package com.idragonit.inspection.models;

import com.idragonit.inspection.AppData;
import com.idragonit.inspection.Constants;
import com.idragonit.inspection.utils.Utils;

import org.json.JSONObject;

/**
 * Created by CJH on 2016.01.23.
 */
public class InspectionInfo {

    public String address;
    public String community;
    public String community_name;
    public String lot;
    public String job_number;
    public String inspection_date;
    public String inspection_initials;

    public int region;
    public int field_manager;

    public boolean ready_inspection;
    public LocationInfo location;
    public PictureInfo front_building;
    public PictureInfo right_building;
    public PictureInfo left_building;
    public PictureInfo back_building;

    public String overall_comments;
    public String inspection_date_last;
    public int result;
    public PictureInfo signature;

    public PictureInfo exception_1;
    public PictureInfo exception_2;
    public PictureInfo exception_3;
    public PictureInfo exception_4;

    public boolean is_exist;
    public boolean is_initials;

    public String city;
    public int area;
    public int volume;
    public float qn;

    public int wall_area;
    public int ceiling_area;
    public String design_location;

    public PictureInfo testing_setup;
    public PictureInfo manometer;

    public float house_pressure;
    public float flow;

    public String qn_out;
    public String ach50;

    public int result_duct_leakage;
    public int result_envelop_leakage;

    public boolean is_building_unit;
    public int reinspection;


    public InspectionInfo() {
        address = "";
        community = "";
        community_name = "";
        lot = "";
        job_number = "";
        inspection_date = Utils.getTodayForInspection();
        inspection_initials = "";

        region = 0;
        field_manager = 0;

        ready_inspection = false;
        location = new LocationInfo();
        front_building = new PictureInfo();
        right_building = new PictureInfo();
        left_building = new PictureInfo();
        back_building = new PictureInfo();

        overall_comments = "";
        inspection_date = Utils.getTodayForInspection();
        result = 0;
        signature = new PictureInfo(Constants.PICTURE_SIGNATURE);

        exception_1 = new PictureInfo();
        exception_2 = new PictureInfo();
        exception_3 = new PictureInfo();
        exception_4 = new PictureInfo();

        is_exist = false;
        is_initials = false;

        city = "";
        area = 0;
        volume = 0;
        qn = 0;

        wall_area = 0;
        ceiling_area = 0;
        design_location = "";

        testing_setup = new PictureInfo();
        manometer = new PictureInfo();

        house_pressure = 0.0f;
        flow = 0.0f;

        result_duct_leakage = 0;
        result_envelop_leakage = 0;

        qn_out = "";
        ach50 = "";

        is_building_unit = false;
        reinspection = 0;
    }

    public void init() {
        address = "";
        community = "";
        community_name = "";
        lot = "";
        job_number = "";
        inspection_date = Utils.getTodayForInspection();
        inspection_initials = "";

        region = 0;
        field_manager = 0;

        ready_inspection = false;

        if (front_building == null)
            front_building = new PictureInfo();
        else
            front_building.init();

        if (right_building == null)
            right_building = new PictureInfo();
        else
            right_building.init();

        if (left_building == null)
            left_building = new PictureInfo();
        else
            left_building.init();

        if (back_building == null)
            back_building = new PictureInfo();
        else
            back_building.init();

        if (location==null)
            location = new LocationInfo();
        else
            location.init();

        overall_comments = "";
        inspection_date_last = Utils.getTodayForInspection();
        result = 0;

        if (signature == null)
            signature = new PictureInfo(Constants.PICTURE_SIGNATURE);
        else
            signature.init(Constants.PICTURE_SIGNATURE);

        if (exception_1 == null)
            exception_1 = new PictureInfo();
        else
            exception_1.init();

        if (exception_2 == null)
            exception_2 = new PictureInfo();
        else
            exception_2.init();

        if (exception_3 == null)
            exception_3 = new PictureInfo();
        else
            exception_3.init();

        if (exception_4 == null)
            exception_4 = new PictureInfo();
        else
            exception_4.init();

        is_exist = false;
        is_initials = false;

        city = "";
        area = 0;
        volume = 0;
        qn = 0;

        wall_area = 0;
        ceiling_area = 0;
        design_location = "";

        testing_setup = new PictureInfo();
        manometer = new PictureInfo();

        house_pressure = 0;
        flow = 0;

        result_duct_leakage = 0;
        result_envelop_leakage = 0;

        qn_out = "";
        ach50 = "";

        is_building_unit = false;
        reinspection = 0;

        if (AppData.sys_energy_inspection.containsKey(Constants.SYS_HOUSE_PRESSURE)){
            String pressure = (String) AppData.sys_energy_inspection.get(Constants.SYS_HOUSE_PRESSURE);
            try{
                house_pressure = Float.parseFloat(pressure);
            }catch (Exception ex){

            }

        }
    }

    public String toJSON() {
        try {
            JSONObject result = new JSONObject();

            result.put("addr", address);
            result.put("date", inspection_date);
            result.put("date_l", inspection_date_last);
            result.put("init", inspection_initials);

            result.put("region", ""+region);
            result.put("fm", ""+field_manager);

            result.put("ready", ready_inspection ? "1" : "0");
            result.put("result", ""+this.result);
            result.put("overall", overall_comments);

            result.put("loc", location.toJSON());
            result.put("front", front_building.toJSON());
            result.put("sign", signature.toJSON());

            result.put("ex1", exception_1.toJSON());
            result.put("ex2", exception_2.toJSON());
            result.put("ex3", exception_3.toJSON());
            result.put("ex4", exception_4.toJSON());

            result.put("exist", is_exist ? "1" : "0");
            result.put("initials", is_initials ? "1" : "0");

            result.put("city", city);
            result.put("area", area+"");
            result.put("volume", volume+"");
            result.put("qn", qn+"");

            result.put("w_area", wall_area+"");
            result.put("c_area", ceiling_area+"");
            result.put("des_loc", design_location);

            result.put("lot", lot);

            result.put("setup", testing_setup.toJSON());
            result.put("mano", manometer.toJSON());

            result.put("pressure", house_pressure+"");
            result.put("flow", flow+"");

            result.put("duct_leakage", result_duct_leakage+"");
            result.put("envelop_leakage", result_envelop_leakage+"");

            result.put("qn_out", qn_out+"");
            result.put("ach50", ach50+"");

            result.put("is_bu", is_building_unit ? "1" : "0");
            result.put("reinspection",reinspection);

            if (community!=null){
                result.put("community",community);
            }
            if (community_name!=null){
                result.put("community_name",community_name);
            }

            if (permit_number!=null){
                result.put("permit_number",permit_number);
            }




            return result.toString();
        } catch (Exception e) {}

        return "";
    }

    public void initWithJSON(String json) {
        initWithJSON(json, false);
    }

    public void initWithJSON(String json, boolean is_edit) {
        try {
            JSONObject result = new JSONObject(json);

            address = Utils.checkNull(result.getString("addr"));

            if (is_edit) {
                inspection_date = Utils.checkNull(result.getString("date"));
                inspection_date_last = Utils.checkNull(result.getString("date_l"));
            } else {
                inspection_date = Utils.getToday("-");
                inspection_date_last = Utils.getToday("-");
            }

            inspection_initials = Utils.checkNull(result.getString("init"));

            region = Utils.checkNull(result.getString("region"), 0);
            field_manager = Utils.checkNull(result.getString("fm"), 0);

            int ready = Utils.checkNull(result.getString("ready"), 0);
            ready_inspection = ready==1 ? true : false;

            this.result = Utils.checkNull(result.getString("result"), 0);
            overall_comments = Utils.checkNull(result.getString("overall"));

            location.initWithJSON(Utils.checkNull(result.getString("loc")));
            front_building.initWithJSON(Utils.checkNull(result.getString("front")));
            signature.initWithJSON(Utils.checkNull(result.getString("sign")));

            exception_1.initWithJSON(Utils.checkNull(result.getString("ex1")));
            exception_2.initWithJSON(Utils.checkNull(result.getString("ex2")));
            exception_3.initWithJSON(Utils.checkNull(result.getString("ex3")));
            if (result.has("ex4"))
                exception_4.initWithJSON(Utils.checkNull(result.getString("ex4")));

            is_exist = false;
            try {
                int temp = Utils.checkNull(result.getString("is_exist"), 0);
                is_exist = temp==1 ? true : false;
            } catch (Exception e) {}

            is_initials = false;
            try {
                int temp = Utils.checkNull(result.getString("is_initials"), 0);
                is_initials = temp==1 ? true : false;
            } catch (Exception e) {}

            try {
                city = Utils.checkNull(result.getString("city"));
                area = Utils.checkNull(result.getString("area"), 0);
                volume = Utils.checkNull(result.getString("volume"), 0);
                qn = Utils.checkNull(result.getString("qn"), 0.0f);

                wall_area = Utils.checkNull(result.getString("w_area"), 0);
                ceiling_area = Utils.checkNull(result.getString("c_area"), 0);
                design_location = Utils.checkNull(result.getString("des_loc"));

                lot = Utils.checkNull(result.getString("lot"));

                house_pressure = Utils.checkNull(result.getString("pressure"), 0.0f);
                flow = Utils.checkNull(result.getString("flow"), 0.0f);
            } catch (Exception dfd) {}

            try {
                testing_setup.initWithJSON(Utils.checkNull(result.getString("setup")));
            } catch (Exception dfd){}

            try {
                manometer.initWithJSON(Utils.checkNull(result.getString("mano")));
            } catch (Exception dfd){}

            try {
                result_duct_leakage = Utils.checkNull(result.getString("duct_leakage"), 0);
                result_envelop_leakage = Utils.checkNull(result.getString("envelop_leakage"), 0);

                qn_out = Utils.checkNull(result.getString("qn_out"));
                ach50 = Utils.checkNull(result.getString("ach50"));
            } catch (Exception dfd){}

            try {
                if (result.has("is_bu")) {
                    if (Utils.checkNull(result.getString("is_bu"), 0)==1)
                        is_building_unit = true;
                }
            }catch (Exception eddfd) {}

            try{
                reinspection = Utils.checkNull(result.getString("reinspection"), 0);
            }catch (Exception ex){

            }

            try{
                community = result.getString("community");
            }catch (Exception ex){

            }

            try{
                community_name = result.getString("community_name");
            }catch (Exception ex){

            }
            try{
                permit_number = result.getString("permit_number");
            }catch (Exception ex){

            }

        } catch (Exception e) {}
    }

    public String getResultString() {
        if (result==1)
            return "Pass";

        if (result==2)
            return "Pass with Exception";

        if (result==3)
            return "Fail";

        return "None";
    }

    public String getResultDuctLeakageString() {
        if (result_duct_leakage==1) {
            return "Pass";
        }

        if (result_duct_leakage==2) {
        }

        if (result_duct_leakage==3) {
            return "Fail";
        }

        return "None";
    }

    public String getResultEnvelopLeakageString() {
        if (result_envelop_leakage==1) {
            return "Pass";
        }

        if (result_envelop_leakage==2) {
            return "Pass (Mechanical Ventilation Required)";
        }

        if (result_envelop_leakage==3) {
            return "Fail";
        }

        return "None";
    }

    public String getCommunityName() {
        if (community_name.isEmpty())
            return community;

        return community_name;
    }

    public String permit_number;
}
