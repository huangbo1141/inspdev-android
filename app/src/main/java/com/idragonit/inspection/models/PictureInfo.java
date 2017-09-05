package com.idragonit.inspection.models;

import com.idragonit.inspection.Constants;
import com.idragonit.inspection.utils.Utils;

import org.json.JSONObject;

/**
 * Created by CJH on 2016.01.24.
 */
public class PictureInfo {

    public int kind;
    public int mode;
    public String image;

    public PictureInfo() {
        kind = 0;
        mode = 0;
        image = "";
    }

    public PictureInfo(int kind) {
        this.kind = kind;
        mode = 0;
        image = "";
    }

    public void init() {
        mode = 0;
        kind = 0;
        image = "";
    }

    public void init(int kind) {
        this.kind = kind;
        mode = 0;
        image = "";
    }

    public void copy(PictureInfo obj) {
        kind = obj.kind;
        mode = obj.mode;
        image = obj.image;
    }

    public String getImageUri() {
        return mode== Constants.PICTURE_SERVER ? image : "file://" + image;
    }

    public String getDisplayedText() {
        if (kind == Constants.PICTURE_SIGNATURE) {
            if (image.length()==0)
                return "No Signature";

            return "Signature Captured";
        }

        if (image.length()==0)
            return "No Image";

        return "Image Captured";
    }

    public String toJSON() {
        if (mode==Constants.PICTURE_EMPTY)
            return "";

        try {
            JSONObject result = new JSONObject();

            result.put("mode", ""+mode);
            result.put("img", image);

            return result.toString();
        } catch (Exception e) {}

        return "";
    }

    public void initWithJSON(String json) {
        if (json.length()==0)
            return;

        try{
            JSONObject result = new JSONObject(json);
            mode = Utils.checkNull(result.getString("mode"), 0);
            image = Utils.checkNull(result.getString("img"));

        }catch (Exception e){}
    }

}
