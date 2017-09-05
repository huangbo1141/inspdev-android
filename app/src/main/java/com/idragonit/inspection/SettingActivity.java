package com.idragonit.inspection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import com.idragonit.inspection.models.UserInfo;
import com.idragonit.inspection.utils.StorageUtils;
import com.idragonit.inspection.utils.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by CJH on 2016.01.23.
 */
public class SettingActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onBackPressed() {
        showActivity(MainActivity.class, Constants.ANIMATION_LEFT_TO_RIGHT);
    }

    public void showActivity(Class activity, int animation){
        try{
            Intent intent = new Intent(this, activity);
            startActivity(intent);

            switch(animation) {
                case Constants.ANIMATION_RIGHT_TO_LEFT:
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;

                case Constants.ANIMATION_LEFT_TO_RIGHT:
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    break;

                case Constants.ANIMATION_BOTTOM_TO_UP:
                    overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                    break;

                case Constants.ANIMATION_UP_TO_BOTTOM:
                    overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
                    break;
            }

            finish();
        }catch(Exception e){}
    }

}
