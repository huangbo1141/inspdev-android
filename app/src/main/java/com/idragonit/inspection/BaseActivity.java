package com.idragonit.inspection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.idragonit.inspection.components.WaitingDialog;


public class BaseActivity extends Activity{

    public final int ACTION_SUCCESS = 1;
    public final int ACTION_FAILED = 2;
    public final int ACTION_DELAY_TIME = 50;

    WaitingDialog mWaitingDialog;

    public void showActivity(Class activity){
        try{
            Intent intent = new Intent(this, activity);
            startActivity(intent);
            finish();
        }catch(Exception e){}
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

    public void showActivity(Class activity, int animation, Bundle data){
        try{
            Intent intent = new Intent(this, activity);
            intent.putExtras(data);
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

    public void showMessage(String message){
        try {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }catch (Exception e){}
    }

    public void showMessage(int message){
        try {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }catch (Exception e){}
    }

    public void showLoading(String message){
        hideLoading();

        mWaitingDialog = new WaitingDialog(this, message);
        mWaitingDialog.show();
    }

    public void hideLoading(){
        if (mWaitingDialog!=null){
            mWaitingDialog.dismiss();
            mWaitingDialog = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            if (mWaitingDialog != null) {
                mWaitingDialog.dismiss();
            }
            mWaitingDialog = null;
        }catch (Exception e){}
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            if (mWaitingDialog != null) {
                mWaitingDialog.dismiss();
            }
            mWaitingDialog = null;
        }catch (Exception e){}
    }

    public void hideKeyboard() {
        View view = getCurrentFocus();
        try {
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }catch (Exception e){}
    }
}
