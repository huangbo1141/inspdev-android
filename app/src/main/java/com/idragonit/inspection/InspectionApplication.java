package com.idragonit.inspection;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.idragonit.inspection.utils.SecurityUtils;
import com.loopj.android.http.RequestParams;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by CJH on 2016.01.23.
 */
@ReportsCrashes(
        mailTo = "bohuang29@hotmail.com", // my email here
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text)
public class InspectionApplication extends MultiDexApplication {

    private static InspectionApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        ACRA.init(this);
        InspectionDatabase.getInstance(this);

        instance = this;


        AppData.INSPECTION_ID = "16312";
        AppData.USER.id = "45";

        Log.d("inspection_id",SecurityUtils.encodeKey(AppData.INSPECTION_ID));
        Log.d("user_id",SecurityUtils.encodeKey(AppData.USER.id));

        RequestParams params = new RequestParams();
        params.put("inspection_id", SecurityUtils.encodeKey(AppData.INSPECTION_ID));
        params.put("user_id", SecurityUtils.encodeKey(AppData.USER.id));
    }
    public static Context getAppContext(){
        return instance;
    }

}
