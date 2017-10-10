package com.idragonit.inspection;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.idragonit.inspection.components.ProgressBarAnimation;
import com.idragonit.inspection.components.WaitingDialog;
import com.idragonit.inspection.fragments.BasicWCI_Step;
import com.idragonit.inspection.fragments.Basic_Step;
import com.idragonit.inspection.fragments.CheckList_Step;
import com.idragonit.inspection.fragments.CheckPoint_Step;
import com.idragonit.inspection.fragments.CommentPoint_Step;
import com.idragonit.inspection.fragments.Done;
import com.idragonit.inspection.fragments.DoneWCI;
import com.idragonit.inspection.fragments.Email_Step;
import com.idragonit.inspection.fragments.Initials_Step;
import com.idragonit.inspection.fragments.JobNumber_Step;
import com.idragonit.inspection.fragments.Location_Step;
import com.idragonit.inspection.fragments.PhotoAndField_Step;
import com.idragonit.inspection.fragments.ResultWCI_Step;
import com.idragonit.inspection.fragments.Result_Step;
import com.idragonit.inspection.fragments.Sync;
import com.idragonit.inspection.fragments.UnitWCI_Step;
import com.idragonit.inspection.models.InspectionInfo;
import com.idragonit.inspection.utils.DeviceUtils;
import com.idragonit.inspection.utils.InspectionUtils;
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
public class InspectionActivity extends FragmentActivity implements Bridge, View.OnClickListener {

    final int ACTION_MAIN = 100;
    final int ACTION_STEP_BASIC = 101;
    final int ACTION_STEP_BASIC_EXIST = 102;
    final int ACTION_STEP_INITIALS = 105;
    final int ACTION_DONE = 120;

    final int ACTION_DELAY_TIME = 50;

    boolean bSubmitting = false;

    WaitingDialog mWaitingDialog;

    int step;

    ImageView mBtn_Prev, mBtn_Next;
    Button mBtn_Submit;
    TextView mText_Title;

    TextView mText_Progress;
    ProgressBar mProgressBar;

    RelativeLayout mLayout_Footer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState==null) {
            step = Constants.STEP__NONE;
        }

        setContentView(R.layout.activity_inspection);

        mText_Title = (TextView) findViewById(R.id.txt_title);
        mBtn_Prev = (ImageView) findViewById(R.id.btn_prev);
        mBtn_Next = (ImageView) findViewById(R.id.btn_next);
        mBtn_Submit = (Button) findViewById(R.id.btn_submit);

        mText_Progress = (TextView) findViewById(R.id.txt_progress);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mLayout_Footer = (RelativeLayout) findViewById(R.id.layout_footer);

        mBtn_Prev.setOnClickListener(this);
        mBtn_Next.setOnClickListener(this);
        mBtn_Submit.setOnClickListener(this);

        checkPermission();
    }

    private void init() {
        String title = InspectionUtils.getTitle(AppData.KIND,null);
        if (title.length()>0)
            mText_Title.setText(title);
        else if (AppData.MODE != Constants.MODE_SYNC)
            gotoMain();

        if (step == Constants.STEP__NONE) {
            if (AppData.KIND == Constants.INSPECTION_WCI) {
                if (AppData.MODE == Constants.MODE_NEW)
                    switchTo(BasicWCI_Step.newInstance(), true);

                if (AppData.MODE == Constants.MODE_SYNC)
                    switchTo(Sync.newInstance(), true);

            } else {
                if (AppData.MODE == Constants.MODE_NEW)
                    checkJob();
//                switchTo(JobNumber_Step.newInstance(), true);

                if (AppData.MODE == Constants.MODE_SYNC)
                    switchTo(Sync.newInstance(), true);
            }
        }
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            showMessage("You have no permission to read/write external storage");
            gotoMain();
        } else {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            showMessage("You have no permission to read/write external storage");
            gotoMain();
        } else {
            init();
        }
    }

    @Override
    public void onBackPressed() {
        if (step == Constants.STEP__DONE)
            return;

        if (step == Constants.STEP__JOB_NUMBER || step == Constants.STEP__SYNC || step == Constants.STEP__BASIC_WCI) {
            gotoMain();

        } else if (step == Constants.STEP__INITIALS) {
            onPrev();

        } else if (step == Constants.STEP__BASIC) {
            if (AppData.MODE == Constants.MODE_NEW)
                gotoMain();

            if (AppData.MODE == Constants.MODE_SYNC) {
                AppData.init(Constants.MODE_SYNC);
                switchTo(Sync.newInstance(), true);
            }
        } else {
            onPrev();
        }
    }

    @Override
    public void onBack() {
        onBackPressed();
    }

    @Override
    public void cleanBackStack() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    @Override
    public void switchTo(BaseFragment fragment, boolean cleanBackStack) {
        int progress = 0;

        if (cleanBackStack) {
            cleanBackStack();
        }

        hideKeyboard();

        String title = InspectionUtils.getTitle(AppData.KIND,fragment);
        if (title.length()>0)
            mText_Title.setText(title);
        else if (AppData.MODE == Constants.MODE_SYNC)
            mText_Title.setText(R.string.sync_inspection);

        mBtn_Next.setVisibility(View.INVISIBLE);
        mBtn_Prev.setVisibility(View.INVISIBLE);

        mBtn_Submit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.forward, 0);
        mBtn_Submit.setText(R.string.next);
        mLayout_Footer.setVisibility(View.VISIBLE);

        // wci
        if (fragment instanceof BasicWCI_Step) {
            step = Constants.STEP__BASIC_WCI;

            mBtn_Next.setVisibility(View.VISIBLE);
        }

        if (fragment instanceof PhotoAndField_Step){
            step = Constants.STEP__PHOTO_WCI;

            progress = 18;
            mBtn_Prev.setVisibility(View.VISIBLE);
            mBtn_Next.setVisibility(View.VISIBLE);
        }

        if (fragment instanceof UnitWCI_Step) {
            step = Constants.STEP__UNIT_WCI;
            progress = 30;

            mBtn_Prev.setVisibility(View.VISIBLE);
            mBtn_Next.setVisibility(View.VISIBLE);
        }

        if (fragment instanceof ResultWCI_Step) {
            step = Constants.STEP__RESULT_WCI;
            progress = 70;

            mBtn_Prev.setVisibility(View.VISIBLE);
            mBtn_Next.setVisibility(View.VISIBLE);
            mBtn_Submit.setText(R.string.submit);
        }

        if (fragment instanceof DoneWCI) {
            step = Constants.STEP__DONE_WCI;
            mLayout_Footer.setVisibility(View.GONE);
        }

        // -------
        if (fragment instanceof JobNumber_Step) {
            step = Constants.STEP__JOB_NUMBER;
            bSubmitting = false;

            mBtn_Next.setVisibility(View.VISIBLE);
        }

        if (fragment instanceof Sync) {
            step = Constants.STEP__SYNC;
            bSubmitting = false;

            mLayout_Footer.setVisibility(View.GONE);
        }

        if (fragment instanceof Initials_Step) {
            step = Constants.STEP__INITIALS;
            progress = 5;
            bSubmitting = false;

            mBtn_Next.setVisibility(View.VISIBLE);
        }

        if (fragment instanceof Basic_Step) {
            step = Constants.STEP__BASIC;
            progress = 20;

            mBtn_Next.setVisibility(View.VISIBLE);
        }

        if (fragment instanceof Location_Step) {
            step = Constants.STEP__LOCATION;
            progress = 40;

            mBtn_Prev.setVisibility(View.VISIBLE);
            mBtn_Next.setVisibility(View.VISIBLE);
        }

        if (fragment instanceof CheckList_Step) {
            step = Constants.STEP__CHECKLIST;
            progress = 40;

            mBtn_Prev.setVisibility(View.VISIBLE);
            mBtn_Submit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.circle_add, 0);
            mBtn_Submit.setText(R.string.add);
        }

        if (fragment instanceof CheckPoint_Step) {
            step = Constants.STEP__CHECKPOINT;
            progress = 40;

            mBtn_Prev.setVisibility(View.VISIBLE);
            mBtn_Submit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.circle_add, 0);
            mBtn_Submit.setText(R.string.add);
        }

        if (fragment instanceof Result_Step) {
            step = Constants.STEP__RESULT;
            progress = 60;

            mBtn_Prev.setVisibility(View.VISIBLE);
            mBtn_Next.setVisibility(View.VISIBLE);
        }

        if (fragment instanceof CommentPoint_Step) {
            step = Constants.STEP__RESULTPOINT;
            progress = 60;

            mBtn_Prev.setVisibility(View.VISIBLE);
            mBtn_Next.setVisibility(View.VISIBLE);
        }

        if (fragment instanceof Email_Step) {
            step = Constants.STEP__EMAIL;
            progress = 80;

            mBtn_Prev.setVisibility(View.VISIBLE);
            mBtn_Next.setVisibility(View.VISIBLE);
            mBtn_Submit.setText(R.string.submit);
        }

        if (fragment instanceof Done) {
            step = Constants.STEP__DONE;
            mLayout_Footer.setVisibility(View.GONE);
        }

        try {
            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            t = t.replace(R.id.container, fragment, fragment.getFragmentTag());
            t = t.addToBackStack(fragment.getFragmentTag());
            t.commitAllowingStateLoss();
        } catch (Exception das) {
        }

        setProgressStatus(progress);
    }

    @Override
    public void setProgressStatus(final int progress) {
        final int current = mProgressBar.getProgress();

        ProgressBarAnimation anim = new ProgressBarAnimation(mProgressBar, mText_Progress, current, progress);
        anim.setDuration(600);
        mProgressBar.startAnimation(anim);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_prev:
                onPrev();
                break;

            case R.id.btn_next:
            case R.id.btn_submit:
                onNext();
                break;
        }
    }

    private void onPrev() {
        BaseFragment fragment = null;

        switch (step) {
            // wci
            case Constants.STEP__BASIC_WCI:
                break;
            case Constants.STEP__PHOTO_WCI:
                fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(PhotoAndField_Step.class.getSimpleName());
                if (fragment!=null) {
                    fragment.saveForm();
                    switchTo(BasicWCI_Step.newInstance(), true);
                }
                break;
            case Constants.STEP__UNIT_WCI:
                fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(UnitWCI_Step.class.getSimpleName());
                if (fragment!=null) {
                    fragment.saveForm();
                    switchTo(PhotoAndField_Step.newInstance(), true);
                }
                break;

            case Constants.STEP__RESULT_WCI:
                fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(ResultWCI_Step.class.getSimpleName());
                if (fragment!=null) {
                    fragment.saveForm();
                    switchTo(UnitWCI_Step.newInstance(), true);
                }
                break;


            // ----------
            case Constants.STEP__BASIC:
                break;

            case Constants.STEP__INITIALS:
                fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(Initials_Step.class.getSimpleName());
                if (fragment!=null) {
                    switchTo(JobNumber_Step.newInstance(), true);
                }
                break;

            case Constants.STEP__LOCATION:
                fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(Location_Step.class.getSimpleName());
                if (fragment!=null) {
                    switchTo(Basic_Step.newInstance(), true);
                }
                break;

            case Constants.STEP__CHECKLIST:
                fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(CheckList_Step.class.getSimpleName());
                if (fragment!=null) {
                    switchTo(Location_Step.newInstance(), true);
                }
                break;

            case Constants.STEP__CHECKPOINT:
                fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(CheckPoint_Step.class.getSimpleName());
                if (fragment!=null) {
                    CheckPoint_Step f = (CheckPoint_Step) fragment;
                    switchTo(CheckList_Step.newInstance(f.mIndex), true);
                }
                break;

            case Constants.STEP__RESULT:
                fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(Result_Step.class.getSimpleName());
                if (fragment!=null) {
                    fragment.saveForm();
                    if (AppData.INSPECTION.ready_inspection)
                        switchTo(Location_Step.newInstance(), true);
                    else
                        switchTo(Basic_Step.newInstance(), true);
                }
                break;

            case Constants.STEP__RESULTPOINT:
                fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(CommentPoint_Step.class.getSimpleName());
                if (fragment!=null) {
                    CommentPoint_Step f = (CommentPoint_Step) fragment;
                    switchTo(Result_Step.newInstance(f.mIndex), true);
                }
                break;

            case Constants.STEP__EMAIL:
                fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(Email_Step.class.getSimpleName());
                if (fragment!=null) {
                    fragment.saveForm();
                    switchTo(Result_Step.newInstance(), true);
                }
                break;

            case Constants.STEP__DONE:
                break;
        }
    }

    private void onNext() {
        BaseFragment fragment = null;
        switch (step) {
            // wci
            case Constants.STEP__BASIC_WCI:
                fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(BasicWCI_Step.class.getSimpleName());
                if (fragment!=null && fragment.validateForm()) {
                    fragment.saveForm();
                    switchTo(PhotoAndField_Step.newInstance(), true);
                }
                break;
            case Constants.STEP__PHOTO_WCI:
                fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(PhotoAndField_Step.class.getSimpleName());
                if (fragment!=null && fragment.validateForm()) {
                    fragment.saveForm();
                    switchTo(UnitWCI_Step.newInstance(), true);
                }
                break;

            case Constants.STEP__UNIT_WCI:
                fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(UnitWCI_Step.class.getSimpleName());
                if (fragment!=null && fragment.validateForm()) {
                    fragment.saveForm();
                    switchTo(ResultWCI_Step.newInstance(), true);
                }
                break;

            case Constants.STEP__RESULT_WCI:
                fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(ResultWCI_Step.class.getSimpleName());
                if (fragment!=null && fragment.validateForm()) {
                    if (!bSubmitting) {
                        bSubmitting = true;
                        fragment.saveForm();
                        prepareSubmit();
                    }
                }
                break;


            // ---------
            case Constants.STEP__JOB_NUMBER:
                fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(JobNumber_Step.class.getSimpleName());
                if (fragment!=null && fragment.validateForm()) {
                    fragment.saveForm();
                    checkJob();
                }
                break;

            case Constants.STEP__INITIALS:
                fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(Initials_Step.class.getSimpleName());
                if (fragment!=null && fragment.validateForm()) {
                    fragment.saveForm();

                    if (AppData.STORAGE.is_exist)
                        mHandler.sendEmptyMessageDelayed(ACTION_STEP_BASIC_EXIST, ACTION_DELAY_TIME);
                    else
                        switchTo(Basic_Step.newInstance(), true);
                }
                break;

            case Constants.STEP__BASIC:
                fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(Basic_Step.class.getSimpleName());
                if (fragment!=null && fragment.validateForm()) {
                    fragment.saveForm();
                    if (AppData.INSPECTION.ready_inspection)
                        switchTo(Location_Step.newInstance(), true);
                    else
                        switchTo(Result_Step.newInstance(), true);
                }
                break;

            case Constants.STEP__LOCATION:
                fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(Location_Step.class.getSimpleName());
                if (fragment!=null && fragment.validateForm()) {
                    fragment.saveForm();
                    switchTo(Result_Step.newInstance(), true);
                }
                break;

            case Constants.STEP__CHECKLIST:
                fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(CheckList_Step.class.getSimpleName());
                if (fragment!=null && fragment.validateForm()) {
                    fragment.saveForm();
                    switchTo(Location_Step.newInstance(), true);
                }
                break;

            case Constants.STEP__CHECKPOINT:
                fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(CheckPoint_Step.class.getSimpleName());
                if (fragment!=null && fragment.validateForm()) {
                    fragment.saveForm();
                    CheckPoint_Step f = (CheckPoint_Step) fragment;
                    switchTo(CheckList_Step.newInstance(f.mIndex), true);
                }
                break;

            case Constants.STEP__RESULT:
                fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(Result_Step.class.getSimpleName());
                if (fragment!=null && fragment.validateForm()) {
                    fragment.saveForm();
                    switchTo(Email_Step.newInstance(), true);
                }
                break;

            case Constants.STEP__RESULTPOINT:
                fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(CommentPoint_Step.class.getSimpleName());
                if (fragment!=null && fragment.validateForm()) {
                    fragment.saveForm();

                    CommentPoint_Step f = (CommentPoint_Step) fragment;
                    switchTo(Result_Step.newInstance(f.mIndex), true);
                }
                break;

            case Constants.STEP__EMAIL:
                fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(Email_Step.class.getSimpleName());
                if (fragment!=null && fragment.validateForm()) {
                    if (!bSubmitting) {
                        bSubmitting = true;
                        fragment.saveForm();
                        prepareSubmit();
                    }
                }
                break;
        }
    }

    private void checkJob() {
        AppData.STORAGE.initSync();
        hideKeyboard();

        if (!DeviceUtils.isInternetAvailable(this)) {
            showMessage(Constants.MSG_CONNECTION);
            mHandler.sendEmptyMessageDelayed(ACTION_STEP_BASIC, ACTION_DELAY_TIME);
            return;
        }

        showLoading(Constants.MSG_LOADING);
        final RequestParams params = new RequestParams();
        params.put("user_id", SecurityUtils.encodeKey(AppData.USER.id));
        params.put("job", AppData.INSPECTION.job_number);
        params.put("is_building_unit", AppData.INSPECTION.is_building_unit ? "1" : "0");
        params.put("address", AppData.INSPECTION.address);
        if (AppData.INSPECTION_EDIT_ID!=0) {
            params.put("inspection_id", AppData.INSPECTION_EDIT_ID+"");
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(Constants.CONNECTION_TIMEOUT * 1000);
        client.setSSLSocketFactory(SecurityUtils.getSSLSocketFactory());
        client.post(this, Constants.API__BASEPATH + Constants.API__CHECK_JOB + Constants.API__KIND[AppData.KIND], params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                hideLoading();

                if (statusCode == 200 && response != null) {
                    Log.i("Job Request", params.toString());
                    Log.i("Job Response", response.toString());

                    try {
                        JSONObject status = response.getJSONObject("status");
                        int code = Utils.checkNull(status.getInt("code"), 0);
                        String message = Utils.checkNull(status.getString("message"));
                        if (code == 0) {
                            JSONObject obj = response.getJSONObject("response");

                            int is_schedule = Utils.checkNull(obj.getInt("is_schedule"), 0);
                            if (is_schedule==1) {
                                AppData.INSPECTION.is_exist = true;

                                // parse
                                JSONObject schedule = obj.getJSONObject("schedule");
                                if (schedule.has("manager_id")) {
                                    AppData.INSPECTION.field_manager = Utils.checkNull(schedule.get("manager_id"), 0);
                                }
//
                                if (!AppData.INSPECTION.is_building_unit)
                                    AppData.INSPECTION.address = Utils.checkNull(schedule.getString("address"));
//                                showMessage(InspectionActivity.this, Constants.MSG_SUCCESS);
                            }

                            int is_exist = Utils.checkNull(obj.getInt("is_exist"), 0);
                            if (is_exist==1) {
                                saveInspection(obj);

                                if (AppData.INSPECTION_EDIT_ID!=0) {
                                    duplicateInspection(true);
                                }
                            }

                            int is_initials = Utils.checkNull(obj.getInt("is_initials"), 0);
                            if (is_initials==1) {
                                AppData.INSPECTION.is_initials = true;
                                showAlertDialog("Please enter your initials to continue. It will be noted on the Report that the no Drainage Inspection was completed for this lot.");

                                mHandler.sendEmptyMessageDelayed(ACTION_STEP_INITIALS, ACTION_DELAY_TIME);
                            } else {
                                if (AppData.INSPECTION_EDIT_ID==0 && is_exist==1) {
                                    mHandler.sendEmptyMessageDelayed(ACTION_STEP_BASIC_EXIST, ACTION_DELAY_TIME);

                                } else {
                                    if (is_schedule==0) {
//                                        showMessage("Non-exist Job Number!");
                                    }

                                    mHandler.sendEmptyMessageDelayed(ACTION_STEP_BASIC, ACTION_DELAY_TIME);
                                }
                            }
                        } else {
                            showMessage(message);
                            mHandler.sendEmptyMessageDelayed(ACTION_STEP_BASIC, ACTION_DELAY_TIME);
                        }
                    } catch (Exception e) {
                        showMessage(Constants.MSG_FAILED);
                        mHandler.sendEmptyMessageDelayed(ACTION_STEP_BASIC, ACTION_DELAY_TIME);
                    }
                } else {
                    showMessage(Constants.MSG_CONNECTION);
                    mHandler.sendEmptyMessageDelayed(ACTION_STEP_BASIC, ACTION_DELAY_TIME);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                hideLoading();
                showMessage(Constants.MSG_CONNECTION);
                mHandler.sendEmptyMessageDelayed(ACTION_STEP_BASIC, ACTION_DELAY_TIME);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                hideLoading();
                showMessage(Constants.MSG_CONNECTION);
                mHandler.sendEmptyMessageDelayed(ACTION_STEP_BASIC, ACTION_DELAY_TIME);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                hideLoading();
                showMessage(Constants.MSG_CONNECTION);
                mHandler.sendEmptyMessageDelayed(ACTION_STEP_BASIC, ACTION_DELAY_TIME);
            }
        });
    }

    private void gotoMain() {
        try {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } catch (Exception e) {
        }

        finish();
    }

    private void prepareSubmit() {
        setProgressStatus(100);
        mHandler.sendEmptyMessageDelayed(ACTION_DONE, 1000);
    }

    public void showMessage(String message){
        try {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }catch (Exception e){}
    }

    public void showMessage(String message, boolean isLong){
        try {
            Toast.makeText(this, message, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
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

        if (mWaitingDialog!=null) {
            mWaitingDialog.show();
        }
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

    @Override
    protected void onDestroy() {
        try {
            if (mWaitingDialog != null) {
                mWaitingDialog.dismiss();
            }
            mWaitingDialog = null;
        }catch (Exception e){}

        super.onDestroy();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ACTION_MAIN:
                    gotoMain();
                    break;

                case ACTION_STEP_INITIALS:
                    switchTo(Initials_Step.newInstance(), true);
                    break;

                case ACTION_STEP_BASIC:
                    switchTo(Basic_Step.newInstance(), true);
                    break;

                case ACTION_STEP_BASIC_EXIST:
                    showDuplicateDialog();
                    break;

                case ACTION_DONE:
                    onDone();
                    break;
            }
        }
    };

    private void onDone() {
        if (AppData.KIND == Constants.INSPECTION_WCI) {
            switchTo(DoneWCI.newInstance(), true);
        } else {
            switchTo(Done.newInstance(), true);
        }
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

    public void saveInspection(JSONObject response) {
        AppData.STORAGE.initSync();

        try {
            JSONObject inspection = response.getJSONObject("inspection");
            JSONArray emails = response.getJSONArray("email");

            JSONObject location = response.getJSONObject("location");
            String left = Utils.checkNull(location.getString("left"));
            String right = Utils.checkNull(location.getString("right"));
            String front = Utils.checkNull(location.getString("front"));
            String back = Utils.checkNull(location.getString("back"));
            String comment = "";

            try {
                comment = Utils.checkNull(response.getString("comment"));
            } catch (Exception edfd){
                Log.i("AAA", edfd.getMessage());
            }

            AppData.STORAGE.inspection.type = Utils.checkNull(inspection.getString("type"), 0);
            AppData.STORAGE.inspection.data = inspection.toString();
            AppData.STORAGE.inspection.email = emails.toString();
            AppData.STORAGE.inspection.left = left;
            AppData.STORAGE.inspection.right = right;
            AppData.STORAGE.inspection.front = front;
            AppData.STORAGE.inspection.back = back;
            AppData.STORAGE.inspection.comment = comment;

            AppData.STORAGE.is_exist = true;
        } catch (Exception e){
            Log.i("CASADASD", e.getMessage());
        }
    }

    private void duplicateEditInspection() {
        String initials = "";
        boolean is_initials = AppData.INSPECTION.is_initials;

        if (AppData.INSPECTION.inspection_initials!=null && AppData.INSPECTION.inspection_initials.length()>0) {
            initials = AppData.INSPECTION.inspection_initials;
        }

        AppData.setInspection(AppData.STORAGE.inspection.data);
        if (initials.length()>0) {
            AppData.INSPECTION.inspection_initials = initials;
        }
        AppData.INSPECTION.is_initials = is_initials;

        AppData.setEmail(AppData.STORAGE.inspection.email);

        AppData.setLocation("Left", AppData.STORAGE.inspection.left);
        AppData.setLocation("Right", AppData.STORAGE.inspection.right);
        AppData.setLocation("Front", AppData.STORAGE.inspection.front);
        AppData.setLocation("Back", AppData.STORAGE.inspection.back);

        AppData.setComment(AppData.STORAGE.inspection.comment);

        AppData.STORAGE.initSync();
    }

    private void duplicateInspection() {
        duplicateInspection(false);
    }

    private void duplicateInspection(boolean is_edit) {
        String initials = "";
        boolean is_initials = AppData.INSPECTION.is_initials;

        if (AppData.INSPECTION.inspection_initials!=null && AppData.INSPECTION.inspection_initials.length()>0) {
            initials = AppData.INSPECTION.inspection_initials;
        }

        AppData.setInspection(AppData.STORAGE.inspection.data, is_edit);
        if (initials.length()>0) {
            AppData.INSPECTION.inspection_initials = initials;
        }
        AppData.INSPECTION.is_initials = is_initials;

        AppData.setEmail(AppData.STORAGE.inspection.email);

        AppData.setLocation("Left", AppData.STORAGE.inspection.left);
        AppData.setLocation("Right", AppData.STORAGE.inspection.right);
        AppData.setLocation("Front", AppData.STORAGE.inspection.front);
        AppData.setLocation("Back", AppData.STORAGE.inspection.back);

        AppData.setComment(AppData.STORAGE.inspection.comment);

        AppData.STORAGE.initSync();
        switchTo(Basic_Step.newInstance(), true);
    }

    public void showDuplicateDialog() {
        hideKeyboard();

        InspectionInfo temp = new InspectionInfo();
        temp.initWithJSON(AppData.STORAGE.inspection.data);

        String result = temp.getResultString();

        new AlertDialog.Builder(this, R.style.MyCustomDialog)
                .setCancelable(false)
                .setTitle("Inspection Report")
                .setMessage("" +
                        result + " " + Constants.INSPECTIONS[AppData.STORAGE.inspection.type] + " Inspection report already submitted for this lot.\n\n" +
                        "Do you want to submit another report?" +
                        "")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        duplicateInspection();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        gotoMain();
                    }
                })
                .show();
    }

    public void showAlertDialog(String message) {
        new AlertDialog.Builder(this, R.style.MyCustomDialog)
                .setTitle("Inspection Report")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Tap to Continue", null)
                .show();
    }
}
