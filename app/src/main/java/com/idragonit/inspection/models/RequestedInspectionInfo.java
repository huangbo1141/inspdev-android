package com.idragonit.inspection.models;

import com.idragonit.inspection.Constants;
import com.idragonit.inspection.utils.Utils;

import org.json.JSONObject;

/**
 * Created by CJH on 7/1/2016.
 */
public class RequestedInspectionInfo {

    public int id;
    public int type;

    public String address;
    public String community;
    public String community_name;

    public String lot;
    public String job_number;
    public int reinspection = 0;
    public String inspection_date;

    public int region;
    public int field_manager;

    public String city;
    public int area;
    public int volume;
    public float qn;

    public int wall_area;
    public int ceiling_area;
    public String design_location;

    public boolean is_building_unit;
    public int edit_inspection_id;

    public RequestedInspectionInfo() {
        id = 0;
        type = 0;

        address = "";
        community = "";
        community_name = "";

        lot = "";
        job_number = "";
        inspection_date = "";

        region = 0;
        field_manager = 0;

        city = "";
        area = 0;
        volume = 0;
        qn = 0;

        wall_area = 0;
        ceiling_area = 0;
        design_location = "";

        is_building_unit = false;

        edit_inspection_id = 0;
    }

    public String toTemp() {
        try {
            JSONObject obj = new JSONObject();

            obj.put("addr", address);
            obj.put("community", community);
            obj.put("community_name", community_name);

            obj.put("lot", lot);
            obj.put("region", region+"");
            obj.put("fm", field_manager+"");

            obj.put("city", city);
            obj.put("area", area+"");
            obj.put("volume", volume+"");
            obj.put("qn", qn+"");

            obj.put("w_area", wall_area+"");
            obj.put("c_area", ceiling_area+"");
            obj.put("loc", design_location);

            obj.put("is_building_unit", is_building_unit ? "1" : "0");

            obj.put("edit_id", edit_inspection_id+"");
            obj.put("reinspection", reinspection);

            return obj.toString();
        } catch (Exception e){}

        return "";
    }

    public void fromTemp(String temp) {
        try {
            JSONObject obj = new JSONObject(temp);

            address = Utils.checkNull(obj.getString("addr"));
            community = Utils.checkNull(obj.getString("community"));
            community_name = Utils.checkNull(obj.getString("community_name"));

            lot = Utils.checkNull(obj.getString("lot"));
            region = Utils.checkNull(obj.getString("region"), 0);
            field_manager = Utils.checkNull(obj.getString("fm"), 0);

            city = Utils.checkNull(obj.getString("city"));
            area = Utils.checkNull(obj.getString("area"), 0);
            volume = Utils.checkNull(obj.getString("volume"), 0);
            qn = Utils.checkNull(obj.getString("qn"), 0.0f);

            wall_area = Utils.checkNull(obj.getString("w_area"), 0);
            ceiling_area = Utils.checkNull(obj.getString("c_area"), 0);
            design_location = Utils.checkNull(obj.getString("loc"));

            if (obj.has("is_building_unit")) {
                if (Utils.checkNull(obj.getString("is_building_unit"), 0)==1)
                    is_building_unit = true;
            }

            if (obj.has("edit_id")) {
                edit_inspection_id = Utils.checkNull(obj.getString("edit_id"), 0);
            }
            if (obj.has("reinspection")){
                reinspection = Utils.checkNull(obj.getString("reinspection"),0);
            }

        } catch (Exception e){}
    }

    public static RequestedInspectionInfo parseJson(JSONObject result){
        try{
            RequestedInspectionInfo item = new RequestedInspectionInfo();

            item.id = Utils.checkNull(result.getString("id"), 0);
            item.type = Utils.checkNull(result.getString("category"), 0);

            item.address = Utils.checkNull(result.getString("address"));
            item.job_number = Utils.checkNull(result.getString("job_number"));
            item.reinspection = Utils.checkNull(result.getString("reinspection"),0);

            if (item.type== Constants.INSPECTION_WCI) {
                item.community = "";

                item.city = Utils.checkNull(result.getString("city_duct"));
                item.area = Utils.checkNull(result.getString("area"), 0);
                item.volume = Utils.checkNull(result.getString("volume"), 0);
                item.qn = Utils.checkNull(result.getString("qn"), 0.0f);

                item.wall_area = Utils.checkNull(result.getString("wall_area"), 0);
                item.ceiling_area = Utils.checkNull(result.getString("ceiling_area"), 0);
                item.design_location = Utils.checkNull(result.getString("design_location"));
            } else {
                item.community = item.job_number.substring(0, 4);
            }

            item.lot = Utils.checkNull(result.getString("lot"));

            item.community_name = Utils.checkNull(result.getString("community_name"));
            item.inspection_date = Utils.checkNull(result.getString("requested_at"));

            item.region = Utils.checkNull(result.getString("region"), 0);
            item.field_manager = Utils.checkNull(result.getString("manager_id"), 0);

            if (result.has("is_building_unit")) {
                if (Utils.checkNull(result.getString("is_building_unit"), 0)==1)
                    item.is_building_unit = true;
            }

            if (result.has("edit_inspection_id")) {
                item.edit_inspection_id = Utils.checkNull(result.getString("edit_inspection_id"), 0);
            }
            return item;
        }catch (Exception ex){

        }
        return null;
    }
}
