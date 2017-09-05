package com.idragonit.inspection.models;

import com.idragonit.inspection.utils.Utils;

import org.json.JSONObject;

/**
 * Created by CJH on 2016.01.23.
 */

public class LocationInfo {

    public double latitude;
    public double longitude;
    public double accuracy;
    public String address;

    public LocationInfo() {
        latitude = -1;
        longitude = -1;
        accuracy = -1;
        address = "";
    }

    public void init() {
        latitude = -1;
        longitude = -1;
        accuracy = -1;
        address = "";
    }

    public void copy(LocationInfo obj) {
        latitude = obj.latitude;
        longitude = obj.longitude;
        accuracy = obj.accuracy;
        address = obj.address;
    }

    public String getDisplayedText() {
        if (address.length()==0)
            return "Location not captured";
        return String.format("%.3f, %.3f, Accuracy: %.2f", latitude, longitude, accuracy) + "m";
    }

    public void refresh() {
        if (latitude==-1 && longitude==-1 && accuracy==-1)
            address = "";
        else
            address = "Captured";
    }

    public String toJSON() {
        if (latitude==-1 && longitude==-1 && accuracy==-1)
            return "";

        try {
            JSONObject result = new JSONObject();

            result.put("lat", ""+latitude);
            result.put("lon", ""+longitude);
            result.put("acc", ""+accuracy);

            return result.toString();
        } catch (Exception e) {}

        return "";
    }

    public  void initWithJSON(String json) {
        if (json.length()==0)
            return;

        try {
            JSONObject result = new JSONObject(json);

            latitude = Utils.checkNull(result.getString("lat"), (double)-1);
            longitude = Utils.checkNull(result.getString("lon"), (double)-1);
            accuracy = Utils.checkNull(result.getString("acc"), (double)-1);

            refresh();
        } catch (Exception e) {}
    }

}
