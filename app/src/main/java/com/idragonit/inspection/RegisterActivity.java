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
public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    EditText mText_Email, mText_Password, mText_ConfirmPassword;
    EditText mText_FirstName, mText_LastName, mText_Phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        mText_Email = (EditText) findViewById(R.id.txt_email);
        mText_Password = (EditText) findViewById(R.id.txt_password);
        mText_ConfirmPassword = (EditText) findViewById(R.id.txt_confirm_password);
        mText_FirstName = (EditText) findViewById(R.id.txt_firstname);
        mText_LastName = (EditText) findViewById(R.id.txt_lastname);
        mText_Phone = (EditText) findViewById(R.id.txt_phone);

        findViewById(R.id.btn_register).setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        showActivity(LoginActivity.class, Constants.ANIMATION_LEFT_TO_RIGHT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                if (validate())
                    onRegister();
                break;
        }
    }

    private boolean validate() {
        String firstname = mText_FirstName.getText().toString();
        String lastname = mText_LastName.getText().toString();
        String email = mText_Email.getText().toString();
        String password1 = mText_Password.getText().toString();
        String password2 = mText_ConfirmPassword.getText().toString();
        String phone = mText_Phone.getText().toString();

        if (firstname.length()==0) {
            showMessage(R.string.error__empty_firstname);
            return false;
        }

        if (lastname.length()==0) {
            showMessage(R.string.error__empty_lastname);
            return false;
        }

        if (email.length()==0) {
            showMessage(R.string.error__empty_email);
            return false;
        }

        if (phone.length()==0) {
            showMessage(R.string.error__empty_phone);
            return false;
        }

        if (password1.length()==0 || password2.length()==0) {
            showMessage(R.string.error__empty_password);
            return false;
        }

        if (!password1.equals(password2)) {
            showMessage(R.string.error__equal_password);
            return false;
        }

        return true;
    }

    private void onRegister() {
        String firstname = mText_FirstName.getText().toString();
        String lastname = mText_LastName.getText().toString();
        String email = mText_Email.getText().toString();
        String password = mText_Password.getText().toString();
        String phone = mText_Phone.getText().toString();

        hideKeyboard();

        showLoading(Constants.MSG_REGISTER);
        RequestParams params = new RequestParams();
        params.put("first_name", firstname);
        params.put("last_name", lastname);
        params.put("email", email);
        params.put("phone_number", phone);
        params.put("password", password);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(Constants.CONNECTION_TIMEOUT * 1000);
        client.setSSLSocketFactory(SecurityUtils.getSSLSocketFactory());
        client.post(this, Constants.API__BASEPATH + Constants.API__USER_REGISTER, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                hideLoading();

                if (statusCode==200 && response!=null) {
                    Log.i("Register Response", response.toString());

                    try{
                        JSONObject status = response.getJSONObject("status");
                        int code = Utils.checkNull(status.getInt("code"), 0);
                        String message = Utils.checkNull(status.getString("message"));
                        if (code == 0) {
                            showMessage(Constants.MSG_REGISTER_SUCCESS);
                            mHandler.sendEmptyMessageDelayed(ACTION_SUCCESS, ACTION_DELAY_TIME);
                        } else {
                            showMessage(message);
                        }
                    }catch (Exception e) {
                        showMessage(Constants.MSG_REGISTER_FAILED);
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
        showActivity(LoginActivity.class, Constants.ANIMATION_RIGHT_TO_LEFT);
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
