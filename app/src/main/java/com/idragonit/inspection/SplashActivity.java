package com.idragonit.inspection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.idragonit.inspection.core.LocalStorageService;
import com.idragonit.inspection.models.UserInfo;
import com.idragonit.inspection.utils.SecurityUtils;
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
public class SplashActivity extends BaseActivity {

    final int ACTION_MAIN = 1000;
    final int ACTION_LOGIN = 1001;
    final int ACTION_SIGN = 1002;

    final int DELAY_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        init();
        checkPermission();
        checkLogin();
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION }, 200);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void checkLogin() {
        StorageUtils.makeAppDirectory();
//        StorageUtils.initAppDirectory();

        AppData.USER.init();
        UserInfo user = LoginManager.getUser(this);
        if (user==null) {
            mHandler.sendEmptyMessageDelayed(ACTION_LOGIN, DELAY_TIME);
        } else {
            AppData.USER.copy(user);
            mHandler.sendEmptyMessageDelayed(ACTION_SIGN, 100);
        }
    }

    private void init() {
        Intent intent = new Intent(this, LocalStorageService.class);
        startService(intent);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == ACTION_LOGIN) {
                AppData.USER.init();
                LoginManager.logout(SplashActivity.this);
                showActivity(LoginActivity.class, Constants.ANIMATION_BOTTOM_TO_UP);
            }

            if (msg.what == ACTION_SIGN) {
//                autoSign();
                showActivity(MainActivity.class, Constants.ANIMATION_BOTTOM_TO_UP);
            }

            if (msg.what == ACTION_MAIN) {
                showActivity(MainActivity.class, Constants.ANIMATION_BOTTOM_TO_UP);
            }
        }
    };

    private void autoSign() {
        RequestParams params = new RequestParams();
        params.put("email", AppData.USER.email);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(Constants.CONNECTION_TIMEOUT * 1000);
        client.setSSLSocketFactory(SecurityUtils.getSSLSocketFactory());
        client.post(this, Constants.API__BASEPATH + Constants.API__USER_SIGN, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (statusCode==200 && response!=null) {
                    Log.i("Sign Response", response.toString());

                    try{
                        JSONObject status = response.getJSONObject("status");
                        int code = Utils.checkNull(status.getInt("code"), 0);
                        String message = Utils.checkNull(status.getString("message"));
                        if (code == 0) {
                            mHandler.sendEmptyMessageDelayed(ACTION_MAIN, ACTION_DELAY_TIME);
                        } else {
                            showMessage(message);
                            mHandler.sendEmptyMessageDelayed(ACTION_LOGIN, ACTION_DELAY_TIME);
                        }
                    }catch (Exception e) {
                        mHandler.sendEmptyMessageDelayed(ACTION_LOGIN, ACTION_DELAY_TIME);
                    }
                } else {
                    mHandler.sendEmptyMessageDelayed(ACTION_LOGIN, ACTION_DELAY_TIME);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mHandler.sendEmptyMessageDelayed(ACTION_LOGIN, ACTION_DELAY_TIME);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                mHandler.sendEmptyMessageDelayed(ACTION_LOGIN, ACTION_DELAY_TIME);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                mHandler.sendEmptyMessageDelayed(ACTION_LOGIN, ACTION_DELAY_TIME);
            }
        });

    }

}
