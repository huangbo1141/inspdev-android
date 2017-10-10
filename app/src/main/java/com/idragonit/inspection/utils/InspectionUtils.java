package com.idragonit.inspection.utils;

import com.idragonit.inspection.BaseFragment;
import com.idragonit.inspection.Constants;
import com.idragonit.inspection.fragments.BasicWCI_Step;
import com.idragonit.inspection.fragments.PhotoAndField_Step;
import com.idragonit.inspection.fragments.UnitWCI_Step;

/**
 * Created by CJH on 8/18/2016.
 */

public class InspectionUtils {

    public static String getTitle(int type, BaseFragment fragment) {
        switch (type) {
            case Constants.INSPECTION_DRAINAGE:
                return "Drainage Plane Inspection";

            case Constants.INSPECTION_LATH:
                return "Lath Inspection";

            case Constants.INSPECTION_WCI:
                if (fragment!=null){
                    if (fragment instanceof BasicWCI_Step){
                        return "Energy Inspection";
                    }else  if (fragment instanceof PhotoAndField_Step){
                        return "Blower Door Test";
                    }else  if (fragment instanceof UnitWCI_Step){
                        return "Duct Leakage Test";
                    }
                }

                return "Energy Inspection";
        }

        return "";
    }

    public static int getUnitValue(String unit) {
        if (unit.length()==0)
            return 0;

        return Utils.getInt(unit);
    }
}
