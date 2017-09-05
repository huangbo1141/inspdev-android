package com.idragonit.inspection;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.idragonit.inspection.utils.SecurityUtils;
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
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    EditText mText_Email, mText_Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mText_Email = (EditText) findViewById(R.id.txt_email);
        mText_Password = (EditText) findViewById(R.id.txt_password);

        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_register).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (validate())
                    onLogin();
                break;

            case R.id.btn_register:
                onRegister();
                break;
        }
    }

    private boolean validate() {
        String email = mText_Email.getText().toString();
        String password = mText_Password.getText().toString();

        if (email.length()==0) {
            showMessage(R.string.error__empty_email);
            return false;
        }

        if (password.length()==0) {
            showMessage(R.string.error__empty_password);
            return false;
        }

        return true;
    }

    private void onLogin() {
        String email = mText_Email.getText().toString();
        String password = mText_Password.getText().toString();

        hideKeyboard();

        showLoading(Constants.MSG_LOGIN);
        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("password", password);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(Constants.CONNECTION_TIMEOUT * 1000);
        client.setSSLSocketFactory(SecurityUtils.getSSLSocketFactory());
        client.post(this, Constants.API__BASEPATH + Constants.API__USER_LOGIN, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                hideLoading();

                if (statusCode==200 && response!=null) {
                    Log.i("Login Response", response.toString());

                    try{
                        JSONObject status = response.getJSONObject("status");
                        int code = Utils.checkNull(status.getInt("code"), 0);
                        String message = Utils.checkNull(status.getString("message"));
                        if (code == 0) {
                            JSONObject obj = response.getJSONObject("response");

                            AppData.USER.init();
                            AppData.USER.email = Utils.checkNull(obj.getString("email"));
                            AppData.USER.firstname = Utils.checkNull(obj.getString("first_name"));
                            AppData.USER.lastname = Utils.checkNull(obj.getString("last_name"));
                            AppData.USER.phone = Utils.checkNull(obj.getString("phone_number"));
                            AppData.USER.id = Utils.checkNull(obj.getString("id"));

                            AppData.USER.address = Utils.checkNull(obj.getString("address"));
                            AppData.USER.fee = Utils.checkNull(obj.getString("fee"), 0.0f);

                            LoginManager.setUser(LoginActivity.this, AppData.USER);

                            mHandler.sendEmptyMessageDelayed(ACTION_SUCCESS, ACTION_DELAY_TIME);
                        } else {
                            showMessage(message);
                        }
                    }catch (Exception e) {
                        showMessage(Constants.MSG_LOGIN_FAILED);
                    }
                } else {
                    showMessage(Constants.MSG_CONNECTION);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                hideLoading();
                showMessage(Constants.MSG_CONNECTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                hideLoading();
                showMessage(Constants.MSG_CONNECTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                hideLoading();
                showMessage(Constants.MSG_CONNECTION);
            }
        });
    }

    private void onSuccess() {
        showActivity(MainActivity.class, Constants.ANIMATION_LEFT_TO_RIGHT);
    }

    private void onRegister() {
        hideKeyboard();
        showActivity(RegisterActivity.class, Constants.ANIMATION_RIGHT_TO_LEFT);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==ACTION_SUCCESS) {
                onSuccess();
            }
        }
    };

}
