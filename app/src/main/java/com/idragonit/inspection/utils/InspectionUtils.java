package com.idragonit.inspection.utils;

import com.idragonit.inspection.Constants;

/**
 * Created by CJH on 8/18/2016.
 */

public class InspectionUtils {

    public static String getTitle(int type) {
        switch (type) {
            case Constants.INSPECTION_DRAINAGE:
                return "Drainage Plane Inspection";

            case Constants.INSPECTION_LATH:
                return "Lath Inspection";

            case Constants.INSPECTION_WCI:
                return "WCI Duct Leakage Inspection";
        }

        return "";
    }

    public static int getUnitValue(String unit) {
        if (unit.length()==0)
            return 0;

        return Utils.getInt(unit);
    }
}
