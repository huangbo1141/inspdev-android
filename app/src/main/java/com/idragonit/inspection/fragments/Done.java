package com.idragonit.inspection.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.idragonit.inspection.AppData;
import com.idragonit.inspection.BaseFragment;
import com.idragonit.inspection.Constants;
import com.idragonit.inspection.InspectionDatabase;
import com.idragonit.inspection.MainActivity;
import com.idragonit.inspection.R;
import com.idragonit.inspection.models.CheckingInfo;
import com.idragonit.inspection.models.CheckingItemInfo;
import com.idragonit.inspection.models.PictureInfo;
import com.idragonit.inspection.utils.DeviceUtils;
import com.idragonit.inspection.utils.HttpHelper;
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
public class Done extends BaseFragment {

    CircularProgressView mProgress;
    TextView mLoading;

    final int ACTION_DONE = 20000;
    final int ACTION_SYNC = 20001;
    final int ACTION_SUCCESS = 20002;
    final int ACTION_FAILED = 19999;

    final int ACTION_SUCCESS_TIME = 100;
    final int ACTION_FAILED_TIME = 1000;

    final int ACTION_START  = 10000;

    final int ACTION_STEP1__FRONT = 10001;
    final String MSG_STEP1__FRONT = "Uploading Picture of Front of Building.....";
    final String ERR_STEP1__FRONT = "Failed to Upload Picture of Front of Building.....";

    final int ACTION_STEP1__RIGHT = 10102;
    final String MSG_STEP1__RIGHT = "Uploading Picture of Right of Building.....";
    final String ERR_STEP1__RIGHT = "Failed to Upload Picture of Right of Building.....";

    final int ACTION_STEP1__LEFT = 10103;
    final String MSG_STEP1__LEFT = "Uploading Picture of Left of Building.....";
    final String ERR_STEP1__LEFT = "Failed to Upload Picture of Left of Building.....";

    final int ACTION_STEP1__BACK = 10104;
    final String MSG_STEP1__BACK = "Uploading Picture of Back of Building.....";
    final String ERR_STEP1__BACK = "Failed to Upload Picture of Back of Building.....";

    final int ACTION_STEP1__SIGNATURE = 10002;
    final String MSG_STEP1__SIGNATURE = "Uploading Signature.....";
    final String ERR_STEP1__SIGNATURE = "Failed to Upload Signature.....";

    final int ACTION_STEP2 = 10003;
    final String MSG_STEP2 = "Uploading Picture of Exception.....";
    final String ERR_STEP2 = "Failed to Upload Picture of Exception.....";

    final int ACTION_STEP2__UPLOAD = 10004;

    final int ACTION_STEP3 = 10005;
    final String MSG_STEP3 = "Uploading Picture in Checklist.....";
    final String ERR_STEP3 = "Failed to Upload Picture in Checklist.....";

    final int ACTION_STEP3__UPLOAD = 10006;
    final int ACTION_STEP3__COPY = 10007;

    final int ACTION_STEP4 = 10008;
    final String MSG_STEP4 = "Uploading Picture in Comment.....";
    final String ERR_STEP4 = "Failed to Upload Picture in Commment.....";

    final int ACTION_STEP4__UPLOAD = 10009;
    final int ACTION_STEP4__COPY = 10010;

    final int ACTION_STEP5 = 10011;
    final String MSG_STEP5 = "Please wait for a moment.....";
    final String ERR_STEP5 = "Failed to Submit Inspection.....";


    public static Done newInstance(Object... args) {
        return new Done();
    }

    @Override
    public String getFragmentTag() {
        return Done.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        if (mBridge == null)
            return root;

        View mContentView = inflater.inflate(R.layout.done, container, false);
        mProgress = (CircularProgressView) mContentView.findViewById(R.id.progress_loading);
        mLoading = (TextView) mContentView.findViewById(R.id.txt_loading);

        return mContentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        restoreForm();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public boolean validateForm() {
        return false;
    }

    @Override
    public void saveForm() {

    }

    @Override
    public void restoreForm() {
        mHandler.sendEmptyMessageDelayed(ACTION_START, 2000);
    }


    private void start() {
        mHandler.sendEmptyMessageDelayed(ACTION_STEP1__FRONT, ACTION_SUCCESS_TIME);
    }


    private void uploadImage(String mode, final int modetype, final PictureInfo building) {
        if (building.mode == Constants.PICTURE_LOCAL) {
            mLoading.setText(MSG_STEP1__FRONT);

            if (!DeviceUtils.isInternetAvailable(getActivity())) {
                showMessage(Constants.MSG_CONNECTION);
                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                return;
            }

            try {
                ///////////////////////////////////////////////////////////////////
                // Modified by Bongbong. 20160416
                String url = Constants.API__BASEPATH + Constants.API__UPLOAD_PICTURE + Constants.API__KIND[AppData.KIND] + "/" + mode;
                String path = building.image;
                HttpHelper.UploadFile(getActivity(),url, path, new HttpHelper.OnResponseListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response == null) {
                            showUploadErrorMessage(modetype);
                            return;
                        }

                        Log.i("Step1 Response", response.toString());

                        try {
                            int code = Utils.checkNull(response.getInt("code"), 0);
                            if (code == 0) {
                                String url = Utils.checkNull(response.getString("url"));
                                String path = Utils.checkNull(response.getString("path"));
                                building.image = url;
                                building.mode = Constants.PICTURE_SERVER;
                                goNextStepForUploadImage(modetype);
                            } else {
                                showUploadErrorMessage(modetype);
                            }
                        } catch (Exception e) {
                            showUploadErrorMessage(modetype);
                        }
                    }
                });
            } catch (Exception e) {
                showUploadErrorMessage(modetype);
            }

        } else {
            goNextStepForUploadImage(modetype);
        }
    }
    public void goNextStepForUploadImage(int modetype){
        switch (modetype){
            case 0:{

                mHandler.sendEmptyMessageDelayed(ACTION_STEP1__RIGHT, ACTION_SUCCESS_TIME);
                break;
            }
            case 1:{
                mHandler.sendEmptyMessageDelayed(ACTION_STEP1__LEFT, ACTION_SUCCESS_TIME);
                break;
            }
            case 2:{
                mHandler.sendEmptyMessageDelayed(ACTION_STEP1__BACK, ACTION_SUCCESS_TIME);
                break;
            }
            case 3:{
                mHandler.sendEmptyMessageDelayed(ACTION_STEP1__SIGNATURE, ACTION_SUCCESS_TIME);
                break;
            }
        }
    }
    private void showUploadErrorMessage(int modetype){
        switch (modetype){
            case 0:{
                showMessage(ERR_STEP1__FRONT);
                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                break;
            }
            case 1:{
                showMessage(ERR_STEP1__RIGHT);
                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                break;
            }
            case 2:{
                showMessage(ERR_STEP1__LEFT);
                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                break;
            }
            case 3:{
                showMessage(ERR_STEP1__BACK);
                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                break;
            }
        }
    }


    private void uploadSignature() {
        if (AppData.INSPECTION.signature.mode == Constants.PICTURE_LOCAL) {
            mLoading.setText(MSG_STEP1__SIGNATURE);

            if (!DeviceUtils.isInternetAvailable(getActivity())) {
                showMessage(Constants.MSG_CONNECTION);
                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                return;
            }

            try {
                ///////////////////////////////////////////////////////////////////
                // Modified by Bongbong. 20160416
                String url = Constants.API__BASEPATH + Constants.API__UPLOAD_PICTURE + Constants.API__KIND[AppData.KIND] + "/signature";
                String path = AppData.INSPECTION.signature.image;
                HttpHelper.UploadFile(getActivity(),url, path, new HttpHelper.OnResponseListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response == null) {
                            showMessage(ERR_STEP1__SIGNATURE);
                            mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                            return;
                        }

                        Log.i("Step1 S Response", response.toString());

                        try {
                            int code = Utils.checkNull(response.getInt("code"), 0);
                            if (code == 0) {
                                String url = Utils.checkNull(response.getString("url"));
                                String path = Utils.checkNull(response.getString("path"));

                                AppData.INSPECTION.signature.image = url;
                                AppData.INSPECTION.signature.mode = Constants.PICTURE_SERVER;

                                mHandler.sendEmptyMessageDelayed(ACTION_STEP2, ACTION_SUCCESS_TIME);

                            } else {
                                showMessage(ERR_STEP1__SIGNATURE);
                                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                            }
                        } catch (Exception e) {
                            showMessage(ERR_STEP1__SIGNATURE);
                            mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                        }
                    }
                });
            } catch (Exception e) {
                showMessage(ERR_STEP1__SIGNATURE);
                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
            }

        } else {
            mHandler.sendEmptyMessageDelayed(ACTION_STEP2, ACTION_SUCCESS_TIME);
        }
    }


    private void prepareExceptionImage() {
        if (AppData.INSPECTION.result==2) {
            mLoading.setText(MSG_STEP2);
            sendNextMessage_Step2(1);
        } else {
            mHandler.sendEmptyMessageDelayed(ACTION_STEP3, ACTION_SUCCESS_TIME);
        }
    }

    private void sendNextMessage_Step2(int arg1) {
        Message msg = mHandler.obtainMessage(ACTION_STEP2__UPLOAD, arg1, 0);
        mHandler.sendMessageDelayed(msg, ACTION_SUCCESS_TIME);
    }

    private void uploadExceptionImage(final int arg) {
        if (arg==1) {
            if (AppData.INSPECTION.exception_1.mode == Constants.PICTURE_LOCAL) {
                if (!DeviceUtils.isInternetAvailable(getActivity())) {
                    showMessage(Constants.MSG_CONNECTION);
                    mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                    return;
                }

                try {

                    ///////////////////////////////////////////////////////////////////
                    // Modified by Bongbong. 20160416
                    String url = Constants.API__BASEPATH + Constants.API__UPLOAD_PICTURE + Constants.API__KIND[AppData.KIND] + "/exception";
                    String path = AppData.INSPECTION.exception_1.image;
                    HttpHelper.UploadFile(getActivity(), url, path, new HttpHelper.OnResponseListener() {
                        @Override
                        public void onResponse(JSONObject response) {

                            if (response == null) {
                                showMessage(ERR_STEP2);
                                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                                return;
                            }

                            Log.i("Step2 Response", response.toString());

                            try {
                                int code = Utils.checkNull(response.getInt("code"), 0);
                                if (code == 0) {
                                    String url = Utils.checkNull(response.getString("url"));
                                    String path = Utils.checkNull(response.getString("path"));

                                    AppData.INSPECTION.exception_1.image = url;
                                    AppData.INSPECTION.exception_1.mode = Constants.PICTURE_SERVER;

                                    sendNextMessage_Step2(arg+1);
                                } else {
                                    showMessage(ERR_STEP2);
                                    mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                                }
                            } catch (Exception e) {
                                showMessage(ERR_STEP2);
                                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                            }
                        }
                    });

                } catch (Exception e) {
                    showMessage(ERR_STEP2);
                    mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                }
            } else {
                sendNextMessage_Step2(arg+1);
            }
        }

        if (arg==2) {
            if (AppData.INSPECTION.exception_2.mode == Constants.PICTURE_LOCAL) {
                if (!DeviceUtils.isInternetAvailable(getActivity())) {
                    showMessage(Constants.MSG_CONNECTION);
                    mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                    return;
                }

                try {
                    ///////////////////////////////////////////////////////////////////
                    // Modified by Bongbong. 20160416
                    String url = Constants.API__BASEPATH + Constants.API__UPLOAD_PICTURE + Constants.API__KIND[AppData.KIND] + "/exception";
                    String path = AppData.INSPECTION.exception_2.image;
                    HttpHelper.UploadFile(getActivity(),url, path, new HttpHelper.OnResponseListener() {
                        @Override
                        public void onResponse(JSONObject response) {

                            if (response == null) {
                                showMessage(ERR_STEP2);
                                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                                return;
                            }

                            Log.i("Step2_Exc Response", response.toString());

                            try {
                                int code = Utils.checkNull(response.getInt("code"), 0);
                                if (code == 0) {
                                    String url = Utils.checkNull(response.getString("url"));
                                    String path = Utils.checkNull(response.getString("path"));

                                    AppData.INSPECTION.exception_2.image = url;
                                    AppData.INSPECTION.exception_2.mode = Constants.PICTURE_SERVER;

                                    sendNextMessage_Step2(arg+1);
                                } else {
                                    showMessage(ERR_STEP2);
                                    mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                                }
                            } catch (Exception e) {
                                showMessage(ERR_STEP2);
                                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                            }
                        }
                    });

                } catch (Exception e) {
                    showMessage(ERR_STEP2);
                    mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                }
            } else {
                sendNextMessage_Step2(arg+1);
            }
        }

        if (arg==3) {
            if (AppData.INSPECTION.exception_3.mode == Constants.PICTURE_LOCAL) {
                if (!DeviceUtils.isInternetAvailable(getActivity())) {
                    showMessage(Constants.MSG_CONNECTION);
                    mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                    return;
                }

                try {
                    ///////////////////////////////////////////////////////////////////
                    // Modified by Bongbong. 20160416
                    String url = Constants.API__BASEPATH + Constants.API__UPLOAD_PICTURE + Constants.API__KIND[AppData.KIND] + "/exception";
                    String path = AppData.INSPECTION.exception_3.image;
                    HttpHelper.UploadFile(getActivity(),url, path, new HttpHelper.OnResponseListener() {
                        @Override
                        public void onResponse(JSONObject response) {

                            if (response == null) {
                                showMessage(ERR_STEP2);
                                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                                return;
                            }

                            Log.i("Step2_Exc Response", response.toString());

                            try {
                                int code = Utils.checkNull(response.getInt("code"), 0);
                                if (code == 0) {
                                    String url = Utils.checkNull(response.getString("url"));
                                    String path = Utils.checkNull(response.getString("path"));

                                    AppData.INSPECTION.exception_3.image = url;
                                    AppData.INSPECTION.exception_3.mode = Constants.PICTURE_SERVER;

                                    sendNextMessage_Step2(arg+1);
                                } else {
                                    showMessage(ERR_STEP2);
                                    mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                                }
                            } catch (Exception e) {
                                showMessage(ERR_STEP2);
                                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                            }
                        }
                    });

                } catch (Exception e) {
                    showMessage(ERR_STEP2);
                    mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                }
            } else {
                sendNextMessage_Step2(arg+1);
            }
        }

        if (arg==4) {
            if (AppData.INSPECTION.exception_4.mode == Constants.PICTURE_LOCAL) {
                if (!DeviceUtils.isInternetAvailable(getActivity())) {
                    showMessage(Constants.MSG_CONNECTION);
                    mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                    return;
                }

                try {
                    String url = Constants.API__BASEPATH + Constants.API__UPLOAD_PICTURE + Constants.API__KIND[AppData.KIND] + "/exception";
                    String path = AppData.INSPECTION.exception_4.image;
                    HttpHelper.UploadFile(getActivity(),url, path, new HttpHelper.OnResponseListener() {
                        @Override
                        public void onResponse(JSONObject response) {

                            if (response == null) {
                                showMessage(ERR_STEP2);
                                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                                return;
                            }

                            Log.i("Step2_Exc Response", response.toString());

                            try {
                                int code = Utils.checkNull(response.getInt("code"), 0);
                                if (code == 0) {
                                    String url = Utils.checkNull(response.getString("url"));
                                    String path = Utils.checkNull(response.getString("path"));

                                    AppData.INSPECTION.exception_4.image = url;
                                    AppData.INSPECTION.exception_4.mode = Constants.PICTURE_SERVER;

                                    sendNextMessage_Step2(arg+1);
                                } else {
                                    showMessage(ERR_STEP2);
                                    mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                                }
                            } catch (Exception e) {
                                showMessage(ERR_STEP2);
                                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                            }
                        }
                    });

                } catch (Exception e) {
                    showMessage(ERR_STEP2);
                    mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                }
            } else {
                sendNextMessage_Step2(arg+1);
            }
        }

        if (arg == 5) {
            mHandler.sendEmptyMessageDelayed(ACTION_STEP3, ACTION_SUCCESS_TIME);
        }
    }


    private void prepareCheckList() {
        if (!AppData.INSPECTION.ready_inspection) {
            mHandler.sendEmptyMessageDelayed(ACTION_STEP4, ACTION_SUCCESS_TIME);
        } else {
            mLoading.setText(MSG_STEP3);
            Message msg = mHandler.obtainMessage(ACTION_STEP3__UPLOAD, 0, 0);
            mHandler.sendMessageDelayed(msg, ACTION_SUCCESS_TIME);
        }
    }

    private void sendNextMessage_Step3(int arg1, int arg2) {
        Message msg = mHandler.obtainMessage(ACTION_STEP3__UPLOAD, arg1, arg2);
        mHandler.sendMessageDelayed(msg, ACTION_SUCCESS_TIME);
    }

    private void uploadChecklist(int arg1, int arg2) {
        if (AppData.LOCATIONS.size() > arg1) {
            CheckingInfo obj = AppData.LOCATIONS.get(arg1);
            if (obj.checking_list.size() > arg2) {
                final CheckingItemInfo item = obj.checking_list.get(arg2);
                if (item.status == Constants.CHECKING_STATUS__FAIL) {
                    if (item.primary.mode == Constants.PICTURE_LOCAL) {
                        uploadPrimaryPicture(arg1, arg2);
                    } else if (item.secondary.mode == Constants.PICTURE_LOCAL) {
                        uploadSecondaryPicture(arg1, arg2);
                    } else {
                        sendNextMessage_Step3(arg1, arg2 + 1);
                    }
                } else {
                    sendNextMessage_Step3(arg1, arg2 + 1);
                }
            } else {
                sendNextMessage_Step3(arg1 + 1, 0);
            }
        } else {
            mHandler.sendEmptyMessageDelayed(ACTION_STEP4, ACTION_SUCCESS_TIME);
        }
    }

    private void setChecklistResult(Bundle data) {
        int arg1 = data.getInt("arg1");
        int arg2 = data.getInt("arg2");
        boolean isPrimary = data.getBoolean("primary");
        String url = data.getString("url");

        CheckingInfo obj = AppData.LOCATIONS.get(arg1);
        CheckingInfo new_obj = new CheckingInfo();
        new_obj.copy(obj);

        CheckingItemInfo item = new_obj.checking_list.get(arg2);
        CheckingItemInfo new_item = new CheckingItemInfo();
        new_item.copy(item);

        if (isPrimary) {
            new_item.primary.mode = Constants.PICTURE_SERVER;
            new_item.primary.image = url;
        } else {
            new_item.secondary.mode = Constants.PICTURE_SERVER;
            new_item.secondary.image = url;
        }

        new_obj.checking_list.set(arg2, new_item);
        AppData.LOCATIONS.set(arg1, new_obj);

        sendNextMessage_Step3(arg1, arg2);
    }

    private void uploadPrimaryPicture(final int arg1, final int arg2) {
        try {
            mLoading.setText(MSG_STEP3 + " (" + AppData.LOCATIONS.get(arg1).location + ")");

            if (!DeviceUtils.isInternetAvailable(getActivity())) {
                showMessage(Constants.MSG_CONNECTION);
                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                return;
            }

            ///////////////////////////////////////////////////////////////////
            // Modified by Bongbong. 20160416
            String url = Constants.API__BASEPATH + Constants.API__UPLOAD_PICTURE + Constants.API__KIND[AppData.KIND] + "/checklist";
            String path = AppData.LOCATIONS.get(arg1).checking_list.get(arg2).primary.image;
            HttpHelper.UploadFile(getActivity(),url, path, new HttpHelper.OnResponseListener() {
                @Override
                public void onResponse(JSONObject response) {

                    if (response == null) {
                        showMessage(ERR_STEP3);
                        mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                        return;
                    }

                    Log.i("Step3 Response", response.toString());
                    try {
                        int code = Utils.checkNull(response.getInt("code"), 0);
                        if (code == 0) {
                            String url = Utils.checkNull(response.getString("url"));
                            String path = Utils.checkNull(response.getString("path"));

                            Message msg = mHandler.obtainMessage(ACTION_STEP3__COPY);
                            Bundle data = new Bundle();
                            data.putInt("arg1", arg1);
                            data.putInt("arg2", arg2);
                            data.putBoolean("primary", true);
                            data.putString("url", url);
                            data.putString("path", path);
                            msg.setData(data);
                            mHandler.sendMessageDelayed(msg, ACTION_SUCCESS_TIME);

                        } else {
                            showMessage(ERR_STEP3);
                            mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                        }
                    } catch (Exception e) {
                        showMessage(ERR_STEP3);
                        mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                    }
                }
            });

        } catch (Exception e) {
            showMessage(ERR_STEP3);
            mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
        }
    }

    private void uploadSecondaryPicture(final int arg1, final int arg2) {
        try {
            mLoading.setText(MSG_STEP3 + " (" + AppData.LOCATIONS.get(arg1).location + ")");

            if (!DeviceUtils.isInternetAvailable(getActivity())) {
                showMessage(Constants.MSG_CONNECTION);
                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                return;
            }

            ///////////////////////////////////////////////////////////////////
            // Modified by Bongbong. 20160416
            String url = Constants.API__BASEPATH + Constants.API__UPLOAD_PICTURE + Constants.API__KIND[AppData.KIND] + "/checklist";
            String path = AppData.LOCATIONS.get(arg1).checking_list.get(arg2).secondary.image;
            HttpHelper.UploadFile(getActivity(),url, path, new HttpHelper.OnResponseListener() {
                @Override
                public void onResponse(JSONObject response) {

                    if (response == null) {
                        showMessage(ERR_STEP3);
                        mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                        return;
                    }

                    hideLoading();

                    Log.i("Step3 Response", response.toString());

                    try {
                        int code = Utils.checkNull(response.getInt("code"), 0);
                        if (code == 0) {
                            String url = Utils.checkNull(response.getString("url"));
                            String path = Utils.checkNull(response.getString("path"));

                            Message msg = mHandler.obtainMessage(ACTION_STEP3__COPY);
                            Bundle data = new Bundle();
                            data.putInt("arg1", arg1);
                            data.putInt("arg2", arg2);
                            data.putBoolean("primary", false);
                            data.putString("url", url);
                            data.putString("path", path);
                            msg.setData(data);
                            mHandler.sendMessageDelayed(msg, ACTION_SUCCESS_TIME);

                        } else {
                            showMessage(ERR_STEP3);
                            mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                        }
                    } catch (Exception e) {
                        showMessage(ERR_STEP3);
                        mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                    }
                }
            });

        } catch (Exception e) {
            showMessage(ERR_STEP3);
            mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
        }
    }


    private void prepareCommentList() {
        mLoading.setText(MSG_STEP4);
        Message msg = mHandler.obtainMessage(ACTION_STEP4__UPLOAD, 0, 0);
        mHandler.sendMessageDelayed(msg, ACTION_SUCCESS_TIME);
    }

    private void sendNextMessage_Step4(int arg) {
        Message msg = mHandler.obtainMessage(ACTION_STEP4__UPLOAD, arg, 0);
        mHandler.sendMessageDelayed(msg, ACTION_SUCCESS_TIME);
    }

    private void uploadCommentlist(int arg) {
        if (AppData.COMMENT.checking_list.size() > arg) {
            final CheckingItemInfo item = AppData.COMMENT.checking_list.get(arg);
            if (item.status == Constants.CHECKING_STATUS__FAIL) {
                if (item.primary.mode == Constants.PICTURE_LOCAL) {
                    uploadPrimaryPicture(arg);
                } else if (item.secondary.mode == Constants.PICTURE_LOCAL) {
                    uploadSecondaryPicture(arg);
                } else {
                    sendNextMessage_Step4(arg + 1);
                }
            } else {
                sendNextMessage_Step4(arg + 1);
            }
        } else {
            mHandler.sendEmptyMessageDelayed(ACTION_STEP5, ACTION_SUCCESS_TIME);
        }
    }

    private void uploadPrimaryPicture(final int arg) {
        try {
            if (!DeviceUtils.isInternetAvailable(getActivity())) {
                showMessage(Constants.MSG_CONNECTION);
                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                return;
            }

            ///////////////////////////////////////////////////////////////////
            // Modified by Bongbong. 20160416
            String url = Constants.API__BASEPATH + Constants.API__UPLOAD_PICTURE + Constants.API__KIND[AppData.KIND] + "/comment";
            String path = AppData.COMMENT.checking_list.get(arg).primary.image;
            HttpHelper.UploadFile(getActivity(),url, path, new HttpHelper.OnResponseListener() {
                @Override
                public void onResponse(JSONObject response) {

                    if (response == null) {
                        showMessage(ERR_STEP4);
                        mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                        return;
                    }

                    Log.i("Step4 Response", response.toString());
                    try {
                        int code = Utils.checkNull(response.getInt("code"), 0);
                        if (code == 0) {
                            String url = Utils.checkNull(response.getString("url"));
                            String path = Utils.checkNull(response.getString("path"));

                            Message msg = mHandler.obtainMessage(ACTION_STEP4__COPY);
                            Bundle data = new Bundle();
                            data.putInt("arg", arg);
                            data.putBoolean("primary", true);
                            data.putString("url", url);
                            data.putString("path", path);
                            msg.setData(data);
                            mHandler.sendMessageDelayed(msg, ACTION_SUCCESS_TIME);

                        } else {
                            showMessage(ERR_STEP4);
                            mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                        }
                    } catch (Exception e) {
                        showMessage(ERR_STEP4);
                        mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                    }
                }
            });

        } catch (Exception e) {
            showMessage(ERR_STEP4);
            mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
        }
    }

    private void uploadSecondaryPicture(final int arg) {
        try {
            if (!DeviceUtils.isInternetAvailable(getActivity())) {
                showMessage(Constants.MSG_CONNECTION);
                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                return;
            }

            ///////////////////////////////////////////////////////////////////
            // Modified by Bongbong. 20160416
            String url = Constants.API__BASEPATH + Constants.API__UPLOAD_PICTURE + Constants.API__KIND[AppData.KIND] + "/comment";
            String path = AppData.COMMENT.checking_list.get(arg).secondary.image;
            HttpHelper.UploadFile(getActivity(),url, path, new HttpHelper.OnResponseListener() {
                @Override
                public void onResponse(JSONObject response) {

                    if (response == null) {
                        showMessage(ERR_STEP4);
                        mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                        return;
                    }

                    hideLoading();

                    Log.i("Step4 Response", response.toString());

                    try {
                        int code = Utils.checkNull(response.getInt("code"), 0);
                        if (code == 0) {
                            String url = Utils.checkNull(response.getString("url"));
                            String path = Utils.checkNull(response.getString("path"));

                            Message msg = mHandler.obtainMessage(ACTION_STEP4__COPY);
                            Bundle data = new Bundle();
                            data.putInt("arg", arg);
                            data.putBoolean("primary", false);
                            data.putString("url", url);
                            data.putString("path", path);
                            msg.setData(data);
                            mHandler.sendMessageDelayed(msg, ACTION_SUCCESS_TIME);

                        } else {
                            showMessage(ERR_STEP4);
                            mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                        }
                    } catch (Exception e) {
                        showMessage(ERR_STEP4);
                        mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                    }
                }
            });

        } catch (Exception e) {
            showMessage(ERR_STEP4);
            mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
        }
    }

    private void setCommentResult(Bundle data) {
        int arg = data.getInt("arg");
        boolean isPrimary = data.getBoolean("primary");
        String url = data.getString("url");

        CheckingItemInfo item = AppData.COMMENT.checking_list.get(arg);
        CheckingItemInfo new_item = new CheckingItemInfo();
        new_item.copy(item);

        if (isPrimary) {
            new_item.primary.mode = Constants.PICTURE_SERVER;
            new_item.primary.image = url;
        } else {
            new_item.secondary.mode = Constants.PICTURE_SERVER;
            new_item.secondary.image = url;
        }

        AppData.COMMENT.checking_list.set(arg, new_item);
        sendNextMessage_Step4(arg);
    }


    private void submitInspection() {
        mLoading.setText(MSG_STEP5);
        String parameter = "";

        if (!DeviceUtils.isInternetAvailable(getActivity())) {
            showMessage(Constants.MSG_CONNECTION);
            mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
            return;
        }

        try{
            JSONObject req = new JSONObject();

            req.put("requested_id", AppData.INSPECTION_REQUESTED_ID);
            if (AppData.INSPECTION_EDIT_ID!=0)
                req.put("inspection_id", AppData.INSPECTION_EDIT_ID);


            req.put("job_number", AppData.INSPECTION.job_number);
            req.put("community", AppData.INSPECTION.community);
            req.put("lot", AppData.INSPECTION.lot);
            req.put("address", AppData.INSPECTION.address);
            req.put("start_date", AppData.INSPECTION.inspection_date);
            req.put("end_date", AppData.INSPECTION.inspection_date_last);
            req.put("initials", AppData.INSPECTION.inspection_initials);
            req.put("region", AppData.INSPECTION.region);
            req.put("field_manager", AppData.INSPECTION.field_manager);
            req.put("latitude", AppData.INSPECTION.location.latitude);
            req.put("longitude", AppData.INSPECTION.location.longitude);
            req.put("accuracy", AppData.INSPECTION.location.accuracy);
            req.put("front_building", AppData.INSPECTION.front_building.mode==Constants.PICTURE_SERVER ? AppData.INSPECTION.front_building.image : "");
            req.put("right_building", AppData.INSPECTION.right_building.mode==Constants.PICTURE_SERVER ? AppData.INSPECTION.right_building.image : "");
            req.put("left_building", AppData.INSPECTION.left_building.mode==Constants.PICTURE_SERVER ? AppData.INSPECTION.left_building.image : "");
            req.put("back_building", AppData.INSPECTION.back_building.mode==Constants.PICTURE_SERVER ? AppData.INSPECTION.back_building.image : "");
            if (AppData.INSPECTION.reinspection == 1 && AppData.INSPECTION.result == 3){        // here 3 is fail
                req.put("house_ready", 0);
            }else{
                req.put("house_ready", AppData.INSPECTION.ready_inspection ? 1 : 0);
            }

            req.put("overall_comments", AppData.INSPECTION.overall_comments);
            req.put("result_code", AppData.INSPECTION.result);
            req.put("signature", AppData.INSPECTION.signature.mode==Constants.PICTURE_SERVER ? AppData.INSPECTION.signature.image : "");
            req.put("is_first", AppData.INSPECTION.is_exist ? 0 : 1);
            req.put("is_initials", AppData.INSPECTION.is_initials ? 1 : 0);
            req.put("exception_1", AppData.INSPECTION.exception_1.mode==Constants.PICTURE_SERVER ? AppData.INSPECTION.exception_1.image : "");
            req.put("exception_2", AppData.INSPECTION.exception_2.mode==Constants.PICTURE_SERVER ? AppData.INSPECTION.exception_2.image : "");
            req.put("exception_3", AppData.INSPECTION.exception_3.mode==Constants.PICTURE_SERVER ? AppData.INSPECTION.exception_3.image : "");
            req.put("exception_4", AppData.INSPECTION.exception_4.mode==Constants.PICTURE_SERVER ? AppData.INSPECTION.exception_4.image : "");

            req.put("is_building_unit", AppData.INSPECTION.is_building_unit ? 1 : 0);

            JSONArray emails = new JSONArray();
            for (String item : AppData.RECIPIENT_EMAILS) {
                emails.put(item);
            }
            req.put("emails", emails);

            JSONArray locations = new JSONArray();
            for (CheckingInfo item : AppData.LOCATIONS) {

                JSONArray checklist = new JSONArray();
                for (CheckingItemInfo sub_item : item.checking_list) {
                    JSONObject check = new JSONObject();
                    check.put("status", sub_item.status);
                    check.put("no", sub_item.no);
                    check.put("description", sub_item.status==Constants.CHECKING_STATUS__FAIL || sub_item.status==Constants.CHECKING_STATUS__NOT_READY ? sub_item.comment : "");
                    check.put("primary", sub_item.status==Constants.CHECKING_STATUS__FAIL && sub_item.primary.mode==Constants.PICTURE_SERVER ? sub_item.primary.image : "");
                    check.put("secondary", sub_item.status==Constants.CHECKING_STATUS__FAIL && sub_item.secondary.mode==Constants.PICTURE_SERVER ? sub_item.secondary.image : "");
                    checklist.put(check);
                }

                JSONObject location = new JSONObject();
                location.put("name", item.location);
                location.put("checklist", checklist);
                locations.put(location);
            }
            req.put("locations", locations);

            JSONArray comments = new JSONArray();
            for (CheckingItemInfo sub_item : AppData.COMMENT.checking_list) {
                if (sub_item.is_submit) {
                    JSONObject check = new JSONObject();
                    check.put("no", sub_item.no);
                    check.put("status", sub_item.status);
                    check.put("description", sub_item.status==Constants.CHECKING_STATUS__FAIL || sub_item.status==Constants.CHECKING_STATUS__NOT_READY ? sub_item.comment : "");
                    check.put("primary", sub_item.status==Constants.CHECKING_STATUS__FAIL && sub_item.primary.mode==Constants.PICTURE_SERVER ? sub_item.primary.image : "");
                    check.put("secondary", sub_item.status==Constants.CHECKING_STATUS__FAIL && sub_item.secondary.mode==Constants.PICTURE_SERVER ? sub_item.secondary.image : "");
                    comments.put(check);
                }
            }
            req.put("comments", comments);

            parameter = req.toString();
        } catch (Exception e){
            e.printStackTrace();

            return;
        }

        RequestParams params = new RequestParams();
        params.put("user_id", SecurityUtils.encodeKey(AppData.USER.id));
        params.put("request", parameter);
        params.put("version", DeviceUtils.getVersion(getActivity()));

        String url = Constants.API__BASEPATH + Constants.API__INSPECTION + Constants.API__KIND[AppData.KIND];
        Log.i("Step5 URL", url);
        Log.i("Step5 Request", AppData.USER.id + ">>>" + parameter);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(Constants.CONNECTION_TIMEOUT * 1000);
        client.setSSLSocketFactory(SecurityUtils.getSSLSocketFactory());
        client.post(getActivity(), url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                hideLoading();

                if (statusCode == 200 && response != null) {
                    Log.i("Step5 Response", response.toString());

                    try {
                        JSONObject status = response.getJSONObject("status");
                        int code = Utils.checkNull(status.getInt("code"), 0);
                        String message = Utils.checkNull(status.getString("message"));
                        if (code == 0) {
                            JSONObject obj = response.getJSONObject("response");
                            AppData.INSPECTION_ID = Utils.checkNull(obj.getString("inspection_id"));
                            mHandler.sendEmptyMessageDelayed(ACTION_SUCCESS, ACTION_SUCCESS_TIME);
                        } else {
                            showMessage(message);
                            mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                        }
                    } catch (Exception e) {
                        showMessage(ERR_STEP5);
                        mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                    }
                } else {
                    Log.i("Step5 Failed-0", "");
                    showMessage(ERR_STEP5);
                    mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("Step5 Failed-1", responseString);
                showMessage(ERR_STEP5);
                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.i("Step5 Failed-2", "");
                showMessage(ERR_STEP5);
                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.i("Step5 Failed-3", "");
                showMessage(ERR_STEP5);
                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
            }
        });
    }



    private void onSuccess() {
        final InspectionDatabase db = InspectionDatabase.getInstance(getActivity());

        if (AppData.MODE == Constants.MODE_SYNC) {
            db.deleteInspection(AppData.INSPECTION_ID_LOCAL);
        }

        db.deleteRequestedInspection(AppData.INSPECTION_REQUESTED_ID);

        RequestParams params = new RequestParams();
        params.put("inspection_id", SecurityUtils.encodeKey(AppData.INSPECTION_ID));
        params.put("user_id", SecurityUtils.encodeKey(AppData.USER.id));

        String url = Constants.API__BASEPATH + Constants.API__EMAIL;
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(Constants.CONNECTION_TIMEOUT * 1000);
        client.setSSLSocketFactory(SecurityUtils.getSSLSocketFactory());
        client.post(getActivity(), url, params, new JsonHttpResponseHandler());

        mHandler.sendEmptyMessageDelayed(ACTION_DONE, ACTION_SUCCESS_TIME);
    }

    private void onFailed() {
        showMessage( R.string.alert__failed);
//        AppData.INSPECTION_ID = "";
//        mBridge.switchTo(Step5.newInstance(), true);

        InspectionDatabase db = InspectionDatabase.getInstance(getActivity());
        if (AppData.MODE == Constants.MODE_SYNC) {
            db.deleteInspection(AppData.INSPECTION_ID_LOCAL);
        }

        db.insertInspection(Utils.checkNull(AppData.USER.id, 0), AppData.INSPECTION_REQUESTED_ID, AppData.INSPECTION_EDIT_ID, AppData.KIND, AppData.INSPECTION.job_number, AppData.getInspection(), AppData.getEmail(), AppData.getLocation("Left"), AppData.getLocation("Right"), AppData.getLocation("Front"), AppData.getLocation("Back"), AppData.getComment(), AppData.getUnit());
        mHandler.sendEmptyMessageDelayed(ACTION_SYNC, 1000);
    }

    private void gotoSync() {
        AppData.init(Constants.MODE_SYNC);
        mBridge.switchTo(Sync.newInstance(), true);
    }

    private void gotoMain() {
        showMessage(R.string.alert__success);

        try {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } catch (Exception e) {
        }

        getActivity().finish();
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ACTION_START:
                    start();
                    break;


                case ACTION_STEP1__FRONT:
                    uploadImage("front",0,AppData.INSPECTION.front_building);
                    break;
                case ACTION_STEP1__RIGHT:
                    uploadImage("right",1,AppData.INSPECTION.right_building);
                    break;
                case ACTION_STEP1__LEFT:
                    uploadImage("left",2,AppData.INSPECTION.left_building);
                    break;
                case ACTION_STEP1__BACK:
                    uploadImage("back",3,AppData.INSPECTION.back_building);
                    break;

                case ACTION_STEP1__SIGNATURE:
                    uploadSignature();
                    break;


                case ACTION_STEP2:
                    prepareExceptionImage();
                    break;

                case ACTION_STEP2__UPLOAD:
                    uploadExceptionImage(msg.arg1);
                    break;


                case ACTION_STEP3:
                    prepareCheckList();
                    break;

                case ACTION_STEP3__UPLOAD:
                    uploadChecklist(msg.arg1, msg.arg2);
                    break;

                case ACTION_STEP3__COPY:
                    setChecklistResult(msg.getData());
                    break;


                case ACTION_STEP4:
                    prepareCommentList();
                    break;

                case ACTION_STEP4__UPLOAD:
                    uploadCommentlist(msg.arg1);
                    break;

                case ACTION_STEP4__COPY:
                    setCommentResult(msg.getData());
                    break;

                case ACTION_STEP5:
                    submitInspection();
                    break;


                case ACTION_SUCCESS:
                    onSuccess();
                    break;

                case ACTION_FAILED:
                    onFailed();
                    break;

                case ACTION_SYNC:
                    gotoSync();
                    break;

                case ACTION_DONE:
                    gotoMain();
                    break;
            }
        }
    };

}
