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
import com.idragonit.inspection.models.UnitInfo;
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
public class DoneWCI extends BaseFragment {

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

    final int ACTION_STEP1__SIGNATURE = 10002;
    final String MSG_STEP1__SIGNATURE = "Uploading Signature.....";
    final String ERR_STEP1__SIGNATURE = "Failed to Upload Signature.....";

    final int ACTION_STEP1__TESTING_SETUP = 10003;
    final String MSG_STEP1__TESTING_SETUP = "Uploading Picture of Duct Testing Setup.....";
    final String ERR_STEP1__TESTING_SETUP = "Failed to Upload Picture of Duct Testing Setup.....";

    final int ACTION_STEP1__MANOMETER = 10004;
    final String MSG_STEP1__MANOMETER = "Uploading Picture of Manometer.....";
    final String ERR_STEP1__MANOMETER = "Failed to Upload Picture of Manometer.....";

    final int ACTION_STEP5 = 10011;
    final String MSG_STEP5 = "Please wait for a moment.....";
    final String ERR_STEP5 = "Failed to Submit Inspection.....";


    public static DoneWCI newInstance(Object... args) {
        return new DoneWCI();
    }

    @Override
    public String getFragmentTag() {
        return DoneWCI.class.getSimpleName();
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


    private void uploadFrontImage() {
        if (AppData.INSPECTION.front_building.mode == Constants.PICTURE_LOCAL) {
            mLoading.setText(MSG_STEP1__FRONT);

            if (!DeviceUtils.isInternetAvailable(getActivity())) {
                showMessage(Constants.MSG_CONNECTION);
                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                return;
            }

            try {
                ///////////////////////////////////////////////////////////////////
                // Modified by Bongbong. 20160416
                String url = Constants.API__BASEPATH + Constants.API__UPLOAD_PICTURE + Constants.API__KIND[AppData.KIND] + "/front";
                String path = AppData.INSPECTION.front_building.image;
                HttpHelper.UploadFile(url, path, new HttpHelper.OnResponseListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response == null) {
                            showMessage(ERR_STEP1__FRONT);
                            mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                            return;
                        }

                        Log.i("Step1 Response", response.toString());

                        try {
                            int code = Utils.checkNull(response.getInt("code"), 0);
                            if (code == 0) {
                                String url = Utils.checkNull(response.getString("url"));
                                String path = Utils.checkNull(response.getString("path"));

                                AppData.INSPECTION.front_building.image = url;
                                AppData.INSPECTION.front_building.mode = Constants.PICTURE_SERVER;

                                mHandler.sendEmptyMessageDelayed(ACTION_STEP1__SIGNATURE, ACTION_SUCCESS_TIME);

                            } else {
                                showMessage(ERR_STEP1__FRONT);
                                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                            }
                        } catch (Exception e) {
                            showMessage(ERR_STEP1__FRONT);
                            mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                        }
                    }
                });
            } catch (Exception e) {
                showMessage(ERR_STEP1__FRONT);
                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
            }

        } else {
            mHandler.sendEmptyMessageDelayed(ACTION_STEP1__SIGNATURE, ACTION_SUCCESS_TIME);
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
                HttpHelper.UploadFile(url, path, new HttpHelper.OnResponseListener() {
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

                                mHandler.sendEmptyMessageDelayed(ACTION_STEP1__TESTING_SETUP, ACTION_SUCCESS_TIME);

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
            mHandler.sendEmptyMessageDelayed(ACTION_STEP1__TESTING_SETUP, ACTION_SUCCESS_TIME);
        }
    }


    private void uploadTestingSetup() {
        if (AppData.INSPECTION.testing_setup.mode == Constants.PICTURE_LOCAL) {
            mLoading.setText(MSG_STEP1__TESTING_SETUP);

            if (!DeviceUtils.isInternetAvailable(getActivity())) {
                showMessage(Constants.MSG_CONNECTION);
                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                return;
            }

            try {
                ///////////////////////////////////////////////////////////////////
                // Modified by Bongbong. 20160416
                String url = Constants.API__BASEPATH + Constants.API__UPLOAD_PICTURE + Constants.API__KIND[AppData.KIND] + "/testing";
                String path = AppData.INSPECTION.testing_setup.image;
                HttpHelper.UploadFile(url, path, new HttpHelper.OnResponseListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response == null) {
                            showMessage(ERR_STEP1__TESTING_SETUP);
                            mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                            return;
                        }

                        Log.i("Step1 T Response", response.toString());

                        try {
                            int code = Utils.checkNull(response.getInt("code"), 0);
                            if (code == 0) {
                                String url = Utils.checkNull(response.getString("url"));
                                String path = Utils.checkNull(response.getString("path"));

                                AppData.INSPECTION.testing_setup.image = url;
                                AppData.INSPECTION.testing_setup.mode = Constants.PICTURE_SERVER;

                                mHandler.sendEmptyMessageDelayed(ACTION_STEP1__MANOMETER, ACTION_SUCCESS_TIME);

                            } else {
                                showMessage(ERR_STEP1__TESTING_SETUP);
                                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                            }
                        } catch (Exception e) {
                            showMessage(ERR_STEP1__TESTING_SETUP);
                            mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                        }
                    }
                });
            } catch (Exception e) {
                showMessage(ERR_STEP1__TESTING_SETUP);
                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
            }

        } else {
            mHandler.sendEmptyMessageDelayed(ACTION_STEP1__MANOMETER, ACTION_SUCCESS_TIME);
        }
    }

    private void uploadManometer() {
        if (AppData.INSPECTION.manometer.mode == Constants.PICTURE_LOCAL) {
            mLoading.setText(MSG_STEP1__MANOMETER);

            if (!DeviceUtils.isInternetAvailable(getActivity())) {
                showMessage(Constants.MSG_CONNECTION);
                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                return;
            }

            try {
                ///////////////////////////////////////////////////////////////////
                // Modified by Bongbong. 20160416
                String url = Constants.API__BASEPATH + Constants.API__UPLOAD_PICTURE + Constants.API__KIND[AppData.KIND] + "/manometer";
                String path = AppData.INSPECTION.manometer.image;
                HttpHelper.UploadFile(url, path, new HttpHelper.OnResponseListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response == null) {
                            showMessage(ERR_STEP1__MANOMETER);
                            mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                            return;
                        }

                        Log.i("Step1 M Response", response.toString());

                        try {
                            int code = Utils.checkNull(response.getInt("code"), 0);
                            if (code == 0) {
                                String url = Utils.checkNull(response.getString("url"));
                                String path = Utils.checkNull(response.getString("path"));

                                AppData.INSPECTION.manometer.image = url;
                                AppData.INSPECTION.manometer.mode = Constants.PICTURE_SERVER;

                                mHandler.sendEmptyMessageDelayed(ACTION_STEP5, ACTION_SUCCESS_TIME);

                            } else {
                                showMessage(ERR_STEP1__MANOMETER);
                                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                            }
                        } catch (Exception e) {
                            showMessage(ERR_STEP1__MANOMETER);
                            mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
                        }
                    }
                });
            } catch (Exception e) {
                showMessage(ERR_STEP1__MANOMETER);
                mHandler.sendEmptyMessageDelayed(ACTION_FAILED, ACTION_FAILED_TIME);
            }

        } else {
            mHandler.sendEmptyMessageDelayed(ACTION_STEP5, ACTION_SUCCESS_TIME);
        }
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

            req.put("job_number", AppData.INSPECTION.job_number);
            req.put("community", AppData.INSPECTION.community_name);
            req.put("lot", AppData.INSPECTION.lot);
            req.put("address", AppData.INSPECTION.address);
            req.put("start_date", AppData.INSPECTION.inspection_date);
            req.put("end_date", AppData.INSPECTION.inspection_date_last);
//            req.put("initials", AppData.INSPECTION.inspection_initials);
            req.put("region", AppData.INSPECTION.region);
            req.put("field_manager", AppData.INSPECTION.field_manager);
            req.put("latitude", AppData.INSPECTION.location.latitude);
            req.put("longitude", AppData.INSPECTION.location.longitude);
            req.put("accuracy", AppData.INSPECTION.location.accuracy);
            req.put("front_building", AppData.INSPECTION.front_building.mode==Constants.PICTURE_SERVER ? AppData.INSPECTION.front_building.image : "");
            req.put("house_ready", AppData.INSPECTION.ready_inspection ? 1 : 0);
            req.put("overall_comments", AppData.INSPECTION.overall_comments);

            req.put("signature", AppData.INSPECTION.signature.mode==Constants.PICTURE_SERVER ? AppData.INSPECTION.signature.image : "");
            req.put("testing_setup", AppData.INSPECTION.testing_setup.mode==Constants.PICTURE_SERVER ? AppData.INSPECTION.testing_setup.image : "");
            req.put("manometer", AppData.INSPECTION.manometer.mode==Constants.PICTURE_SERVER ? AppData.INSPECTION.manometer.image : "");

            req.put("city", AppData.INSPECTION.city);
            req.put("area", AppData.INSPECTION.area);
            req.put("volume", AppData.INSPECTION.volume);
            req.put("qn", AppData.INSPECTION.qn);

            req.put("wall_area", AppData.INSPECTION.wall_area);
            req.put("ceiling_area", AppData.INSPECTION.ceiling_area);
            req.put("design_location", AppData.INSPECTION.design_location);

            req.put("house_pressure", AppData.INSPECTION.house_pressure);
            req.put("flow", AppData.INSPECTION.flow);

            req.put("qn_out", AppData.INSPECTION.qn_out);
            req.put("ach50", AppData.INSPECTION.ach50);

            req.put("result_duct_leakage", AppData.INSPECTION.result_duct_leakage);
            req.put("result_envelop_leakage", AppData.INSPECTION.result_envelop_leakage);

            req.put("is_building_unit", AppData.INSPECTION.is_building_unit ? 1 : 0);

            JSONArray units = new JSONArray();
            for (int i=0; i<AppData.UNITS.size(); i++) {
                UnitInfo unit = AppData.UNITS.get(i);
                if (unit.isValid()) {
                    JSONObject  u = new JSONObject();
                    u.put("no", ""+unit.no);
                    u.put("supply", unit.i_supply);
                    u.put("return", unit.i_return);
                    units.put(u);
                }
            }
            req.put("unit", units);

            parameter = req.toString();
        } catch (Exception e){}

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
                    uploadFrontImage();
                    break;

                case ACTION_STEP1__SIGNATURE:
                    uploadSignature();
                    break;

                case ACTION_STEP1__TESTING_SETUP:
                    uploadTestingSetup();
                    break;

                case ACTION_STEP1__MANOMETER:
                    uploadManometer();
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
