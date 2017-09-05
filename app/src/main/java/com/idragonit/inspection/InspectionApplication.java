package com.idragonit.inspection;

import android.app.Application;
import android.content.Context;

/**
 * Created by CJH on 2016.01.23.
 */
public class InspectionApplication extends Application {

    private static InspectionApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        InspectionDatabase.getInstance(this);

        instance = this;
    }
    public static Context getAppContext(){
        return instance;
    }

}
