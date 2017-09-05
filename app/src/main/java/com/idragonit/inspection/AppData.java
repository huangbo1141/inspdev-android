package com.idragonit.inspection;

import com.idragonit.inspection.models.CheckingInfo;
import com.idragonit.inspection.models.InspectionInfo;
import com.idragonit.inspection.models.StorageInfo;
import com.idragonit.inspection.models.UnitInfo;
import com.idragonit.inspection.models.UserInfo;
import com.idragonit.inspection.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by CJH on 2016.01.23.
 */
public class AppData {

    public static int MODE = 0;
    public static int KIND = 0;

    public static String TAKEN_PICTURE = "";
    public static int TAKEN_KIND = 0;

    public static String INSPECTION_ID = "";
    public static int INSPECTION_ID_LOCAL = 0;
    public static int INSPECTION_REQUESTED_ID = 0;
    public static int INSPECTION_EDIT_ID = 0;

    public static UserInfo USER = new UserInfo();
    public static InspectionInfo INSPECTION = new InspectionInfo();
    public static ArrayList<String> RECIPIENT_EMAILS = new ArrayList<>();
    public static ArrayList<CheckingInfo> LOCATIONS = new ArrayList<>();
    public static CheckingInfo COMMENT = new CheckingInfo();
    public static ArrayList<UnitInfo> UNITS = new ArrayList<>();

    public static int RESULT_SCROLL_POSITION = 0;

    public static Object SYNC = new Object();

    public static StorageInfo STORAGE = new StorageInfo();

    public static Map<String,Object> sys_energy_inspection = new HashMap<>();

    public static void init() {
        MODE = Constants.MODE_NEW;
        KIND = Constants.INSPECTION_NONE;

        INSPECTION_ID = "";
        INSPECTION_ID_LOCAL = 0;
        INSPECTION_REQUESTED_ID = 0;
        INSPECTION_EDIT_ID = 0;

        INSPECTION.init();
        RECIPIENT_EMAILS.clear();
        LOCATIONS.clear();
        STORAGE.init();
        COMMENT.init();
        UNITS.clear();
    }

    public static void init(int mode) {
        MODE = mode;
        KIND = Constants.INSPECTION_NONE;

        INSPECTION_ID = "";
        INSPECTION_ID_LOCAL = 0;

        INSPECTION.init();
        RECIPIENT_EMAILS.clear();
        LOCATIONS.clear();
        STORAGE.init();
        COMMENT.init();
        UNITS.clear();
    }

    public static void addRecipientEmail(String email) {
        synchronized (SYNC) {
            RECIPIENT_EMAILS.add(email);
        }
    }

    public static boolean checkRecipientEmail(String email) {
        synchronized (SYNC) {
            for (String mail : RECIPIENT_EMAILS) {
                if (email.equals(mail))
                    return false;
            }
        }

        return true;
    }

    public static void removeRecipientEmail(String email) {
        synchronized (SYNC) {
            try {
                RECIPIENT_EMAILS.remove(email);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void addLocation(CheckingInfo location) {
        removeLocation(location.location);

        synchronized (SYNC) {
            CheckingInfo item = new CheckingInfo();
            item.copy(location);
            LOCATIONS.add(item);
        }
    }

    public static void removeLocation(String location) {
        synchronized (SYNC) {
            for (int i=LOCATIONS.size()-1; i>=0; i--) {
                CheckingInfo item = LOCATIONS.get(i);
                if (item.location.equals(location)) {
                    LOCATIONS.remove(i);
                }
            }
        }
    }

    public static String getInspection() {
        return INSPECTION.toJSON();
    }

    public static void setInspection(String inspection) {
        setInspection(inspection, false);
    }

    public static void setInspection(String inspection, boolean is_edit) {
        INSPECTION.initWithJSON(inspection, is_edit);
    }

    public static String getEmail() {
        try {
            JSONArray emails = new JSONArray();
            for (String email : RECIPIENT_EMAILS) {
                emails.put(email);
            }

            return emails.toString();
        } catch (Exception e) {}

        return "";
    }

    public static void setEmail(String emails) {
        if (emails.length()==0)
            return;

        try {
            JSONArray result = new JSONArray(emails);
            for (int i=0; i<result.length(); i++) {
                RECIPIENT_EMAILS.add(Utils.checkNull(result.getString(i)));
            }
        }catch (Exception e){}
    }


    public static String getLocation(String loc) {
        try {
            for (CheckingInfo location : LOCATIONS) {
                if (location.location.equals(loc)) {
                    return location.toJSON().toString();
                }
            }
        } catch (Exception e) {}

        return "";
    }

    public static void setLocation(String name, String json) {
        if (name.length()==0 || json.length()==0)
            return;

        CheckingInfo location = new CheckingInfo();

        try {
            JSONObject obj = new JSONObject(json);
            int omit = 0;

            try {
                omit = Utils.checkNull(obj.getString("omit"), 0);
            } catch (Exception ed) {}

            int front = 0;
            try {
                front = Utils.checkNull(obj.getString("front"), 0);
            } catch (Exception ere){}

            location.init(name, omit==1 ? true : false, front==1 ? true : false);

            JSONArray list = obj.getJSONArray("list");
            location.initWithJSON(list==null ? "" : list.toString());

        } catch (Exception e) {}

        LOCATIONS.add(location);
    }

    public static void initComment() {
        COMMENT.init(true);
    }

    public static String getComment() {
        try {
            return AppData.COMMENT.toJSON();
        } catch (Exception e) {}

        return "";
    }

    public static void setComment(String json) {
        if (json.length()==0)
            return;

        try {
            JSONObject obj = new JSONObject(json);
            JSONArray list = obj.getJSONArray("list");
            AppData.COMMENT.initWithJSON(list==null ? "" : list.toString());
        } catch (Exception e) {}
    }

    public static void initUnit() {
        UNITS.clear();

        UnitInfo unit1 = new UnitInfo(1);
        UNITS.add(unit1);

        UnitInfo unit2 = new UnitInfo(2);
        UNITS.add(unit2);

        UnitInfo unit3 = new UnitInfo(3);
        UNITS.add(unit3);

        UnitInfo unit4 = new UnitInfo(4);
        UNITS.add(unit4);
    }

    public static UnitInfo getUnit(int no) {
        for (int i=0; i<UNITS.size(); i++) {
            UnitInfo u = UNITS.get(i);
            if (u.no==no) {
                return u;
            }
        }

        return new UnitInfo(no);
    }

    public static String getUnit() {
        try {
            JSONArray result = new JSONArray();

            for (UnitInfo unit : UNITS) {
                result.put(unit.toJSON());
            }

            return result.toString();
        } catch (Exception e) {}

        return "";
    }

    public static void setUnit(UnitInfo unit) {
        for (int i=0; i<UNITS.size(); i++) {
            UnitInfo u = UNITS.get(i);
            if (u.no==unit.no) {
                u.copy(unit);
                UNITS.set(i, u);
                return;
            }
        }
//        UNITS.add(unit);
    }

    public static void setUnit(String json) {
        if (json.length()==0)
            return;

        try {
            JSONArray result = new JSONArray(json);
            for (int i=0; i<result.length(); i++) {
                JSONObject obj = result.getJSONObject(i);

                UnitInfo unit = new UnitInfo();
                unit.initWithJSON(obj.toString());

                setUnit(unit);
            }
        } catch (Exception e) {}
    }

}
