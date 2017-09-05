package com.idragonit.inspection.models;

import com.idragonit.inspection.Constants;
import com.idragonit.inspection.utils.Utils;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by CJH on 2016.01.24.
 */
public class CheckingItemInfo implements Serializable {

    public int no;
    public String title;
    public int status;
    public String comment;
    public PictureInfo primary;
    public PictureInfo secondary;
    public boolean is_submit;

    public CheckingItemInfo() {
        no = 0;
        title = "";
        status = Constants.CHECKING_STATUS__NONE;
        comment = "";
        primary = new PictureInfo();
        secondary = new PictureInfo();
        is_submit = false;
    }

    public CheckingItemInfo(int no, String title) {
        this.no = no;
        this.title = title;
        status = Constants.CHECKING_STATUS__NONE;
        comment = "";
        primary = new PictureInfo();
        secondary = new PictureInfo();
        is_submit = false;
    }

    public void init() {
        no = 0;
        title = "";
        status = Constants.CHECKING_STATUS__NONE;
        comment = "";

        if (primary==null)
            primary = new PictureInfo();
        else
            primary.init();

        if (secondary==null)
            secondary = new PictureInfo();
        else
            secondary.init();
    }

    public void copy(CheckingItemInfo obj) {
        this.no = obj.no;
        this.title = obj.title;
        this.status = obj.status;
        this.is_submit = obj.is_submit;

        if (status==2 || status==3) {
            this.comment = obj.comment;
        }

        if (status==2) {
            primary.copy(obj.primary);
            secondary.copy(obj.secondary);
        }
    }

    public String toJSON() {
        try {
            JSONObject result = new JSONObject();

            result.put("kind", ""+no);
            result.put("stat", ""+status);
            result.put("submit", is_submit ? "1" : "0");

            if (no==0 || status==0)
                return result.toString();

            if (status==2 || status==3) {
                result.put("cmt", comment);
            }

            if (status==2) {
                result.put("prm", primary.toJSON());
                result.put("snd", secondary.toJSON());
            }

            return result.toString();
        } catch (Exception e) {}

        return null;
    }

    public void initWithJSON(String json) {
        try {
            JSONObject result = new JSONObject(json);
            no = Utils.checkNull(result.getString("kind"), 0);
            status = Utils.checkNull(result.getString("stat"), 0);

            int submit = 0;
            if (result.has("submit")) {
                submit = Utils.checkNull(result.getString("submit"), 0);
            }
            is_submit = submit==1 ? true : false;

            if (no==0 || status==0)
                return;

            if (status==2 || status==3)
                comment = Utils.checkNull(result.getString("cmt"));

            if (status==2) {
                primary.initWithJSON(Utils.checkNull(result.getString("prm")));
                secondary.initWithJSON(Utils.checkNull(result.getString("snd")));
            }
        } catch (Exception e){}
    }

}
