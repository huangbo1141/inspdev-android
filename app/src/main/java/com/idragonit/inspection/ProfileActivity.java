package com.idragonit.inspection;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
public class ProfileActivity extends BaseActivity implements View.OnClickListener {

    TextView mText_Email;
    EditText mText_Password, mText_OldPassword, mText_ConfirmPassword;
    EditText mText_FirstName, mText_LastName, mText_Phone, mText_Address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        mText_Email = (TextView) findViewById(R.id.txt_email);
        mText_Password = (EditText) findViewById(R.id.txt_password);
        mText_OldPassword = (EditText) findViewById(R.id.txt_old_password);
        mText_ConfirmPassword = (EditText) findViewById(R.id.txt_confirm_password);
        mText_FirstName = (EditText) findViewById(R.id.txt_firstname);
        mText_LastName = (EditText) findViewById(R.id.txt_lastname);
        mText_Phone = (EditText) findViewById(R.id.txt_phone);
        mText_Address = (EditText) findViewById(R.id.txt_address);

        mText_Email.setText(AppData.USER.email);
        mText_FirstName.setText(AppData.USER.firstname);
        mText_LastName.setText(AppData.USER.lastname);
        mText_Phone.setText(AppData.USER.phone);
        mText_Address.setText(AppData.USER.address);

        findViewById(R.id.btn_update).setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        showActivity(MainActivity.class, Constants.ANIMATION_UP_TO_BOTTOM);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:
                if (validate())
                    onUpdate();
                break;
        }
    }

    private boolean validate() {
        String firstname = mText_FirstName.getText().toString();
        String lastname = mText_LastName.getText().toString();
        String email = mText_Email.getText().toString();
        String phone = mText_Phone.getText().toString();
        String password0 = mText_OldPassword.getText().toString();
        String password1 = mText_Password.getText().toString();
        String password2 = mText_ConfirmPassword.getText().toString();

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

        if (password0.length()==0 || password1.length()==0 || password2.length()==0) {
            showMessage(R.string.error__empty_password);
            return false;
        }

        if (!password1.equals(password2)) {
            showMessage(R.string.error__equal_password);
            return false;
        }

        return true;
    }

    private void onUpdate() {
        final String firstname = mText_FirstName.getText().toString();
        final String lastname = mText_LastName.getText().toString();
        String email = mText_Email.getText().toString();
        String password = mText_Password.getText().toString();
        String password0 = mText_OldPassword.getText().toString();
        final String phone = mText_Phone.getText().toString();
        final String address = mText_Address.getText().toString();

        hideKeyboard();

        showLoading(Constants.MSG_UPDATE);
        RequestParams params = new RequestParams();
        params.put("first_name", firstname);
        params.put("last_name", lastname);
        params.put("email", email);
        params.put("phone_number", phone);
        params.put("old_password", password0);
        params.put("new_password", password);
        params.put("address", address);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(Constants.CONNECTION_TIMEOUT * 1000);
        client.setSSLSocketFactory(SecurityUtils.getSSLSocketFactory());
        client.post(this, Constants.API__BASEPATH + Constants.API__USER_UPDATE, params, new JsonHttpResponseHandler(){
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

                            AppData.USER.firstname = firstname;
                            AppData.USER.lastname = lastname;
                            LoginManager.setUser(ProfileActivity.this, AppData.USER);

                            AppData.USER.phone = phone;
                            AppData.USER.address = address;

                            showMessage(Constants.MSG_UPDATE_SUCCESS);
                            mHandler.sendEmptyMessageDelayed(ACTION_SUCCESS, ACTION_DELAY_TIME);
                        } else {
                            showMessage(message);
                        }
                    }catch (Exception e) {
                        showMessage(Constants.MSG_UPDATE_FAILED);
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
        showActivity(MainActivity.class, Constants.ANIMATION_UP_TO_BOTTOM);
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
