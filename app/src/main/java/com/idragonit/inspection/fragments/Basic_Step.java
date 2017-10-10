package com.idragonit.inspection.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.idragonit.inspection.AppData;
import com.idragonit.inspection.BaseFragment;
import com.idragonit.inspection.Constants;
import com.idragonit.inspection.InspectionDatabase;
import com.idragonit.inspection.R;
import com.idragonit.inspection.components.PicturePickerDialog;
import com.idragonit.inspection.components.PicturePickerListener;
import com.idragonit.inspection.components.PicturePreviewDialog;
import com.idragonit.inspection.components.SpinnerAdapter;
import com.idragonit.inspection.core.LocationTracker;
import com.idragonit.inspection.models.PictureInfo;
import com.idragonit.inspection.models.SpinnerInfo;
import com.idragonit.inspection.utils.CalenderUtils;
import com.idragonit.inspection.utils.DeviceUtils;
import com.idragonit.inspection.utils.SecurityUtils;
import com.idragonit.inspection.utils.StorageUtils;
import com.idragonit.inspection.utils.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;

/**
 * Created by CJH on 2016.01.23.
 */
public class Basic_Step extends BaseFragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, PicturePickerListener {

    EditText mText_Community, mText_Lot, mText_Address, mText_JobNumber, mText_InspectorInitials,mText_PermitNumber;
    TextView mText_InspectionDate, mText_Location, mText_FrontBuilding;

    LinearLayout mLayout_Region, mLayout_FieldManager;

    Spinner mSpinner_ReadyInspection, mSpinner_Region, mSpinner_FieldManager;
    SpinnerAdapter mAdapter_Region, mAdapter_FieldManager;
    ArrayList<SpinnerInfo> mList_Region, mList_FieldManager;

    PictureInfo mPicture_FrontBuilding;

    boolean bFirst;

    public Basic_Step() {
        mPicture_FrontBuilding = new PictureInfo();
        AppData.TAKEN_PICTURE = "";
        bFirst = true;
    }

    public static Basic_Step newInstance(Object... args) {
        return new Basic_Step();
    }

    @Override
    public String getFragmentTag() {
        return Basic_Step.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        if (mBridge == null)
            return root;

        View mContentView = inflater.inflate(R.layout.step_basic, container, false);

        mLayout_Region = (LinearLayout) mContentView.findViewById(R.id.layout_region);
        mLayout_FieldManager = (LinearLayout) mContentView.findViewById(R.id.layout_fm);

        mText_Community = (EditText) mContentView.findViewById(R.id.txt_community);
        mText_PermitNumber = (EditText) mContentView.findViewById(R.id.txt_permit_number);
        mText_Lot = (EditText) mContentView.findViewById(R.id.txt_lot);
        mText_Address = (EditText) mContentView.findViewById(R.id.txt_address);
        mText_JobNumber = (EditText) mContentView.findViewById(R.id.txt_job_number);
        mText_InspectorInitials = (EditText) mContentView.findViewById(R.id.txt_inspector_initials);

        if (AppData.INSPECTION.is_initials) {
            mText_InspectorInitials.setInputType(InputType.TYPE_NULL);
            mText_InspectorInitials.setEnabled(false);
        }

        mText_InspectionDate = (TextView) mContentView.findViewById(R.id.txt_inspection_date);
        mText_Location = (TextView) mContentView.findViewById(R.id.txt_gps_location);
        mText_FrontBuilding = (TextView) mContentView.findViewById(R.id.txt_front_building);

        mSpinner_ReadyInspection = (Spinner) mContentView.findViewById(R.id.spinner_ready_inspection);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.yes_no, R.layout.layout_spinner);
        mSpinner_ReadyInspection.setAdapter(adapter);

        mList_Region = new ArrayList<>();
        mList_FieldManager = new ArrayList<>();
        mAdapter_Region = new SpinnerAdapter(getActivity());
        mAdapter_FieldManager = new SpinnerAdapter(getActivity());

        mSpinner_Region = (Spinner) mContentView.findViewById(R.id.spinner_region);
        mSpinner_Region.setAdapter(mAdapter_Region);

        mSpinner_FieldManager = (Spinner) mContentView.findViewById(R.id.spinner_fm);
        mSpinner_FieldManager.setAdapter(mAdapter_FieldManager);

        mText_InspectionDate.setOnClickListener(this);

        mText_Location.setOnClickListener(this);
        mContentView.findViewById(R.id.btn_gps).setOnClickListener(this);

        mText_FrontBuilding.setOnClickListener(this);
        mContentView.findViewById(R.id.btn_picture).setOnClickListener(this);

        mLayout_Region.setVisibility(View.GONE);
        mLayout_FieldManager.setVisibility(View.GONE);

        mSpinner_Region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (bFirst)
                    return;

                if (mList_Region.size()>position) {
                    SpinnerInfo item = mList_Region.get(position);
                    AppData.INSPECTION.region = item.id;
                    loadFieldManager(true);
                } else {
                    AppData.INSPECTION.region = 0;
                    loadFieldManager(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSpinner_FieldManager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mList_FieldManager.size()==0)
                    return true;

                return false;
            }
        });

        return mContentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bFirst = true;
        restoreForm();
    }

    @Override
    public boolean validateForm() {
        String community = mText_Community.getText().toString();
        String address = mText_Address.getText().toString();

//        if (community.length()==0) {
//            showMessage(getActivity(), R.string.error__empty_community);
//            return false;
//        }

//        if (address.length()==0) {
//            showMessage(getActivity(), R.string.error__empty_address);
//            return false;
//        }

        String initials = mText_InspectorInitials.getText().toString();
        if (AppData.KIND==Constants.INSPECTION_LATH && !AppData.INSPECTION.is_initials && initials.length()==0) {
            showMessage(R.string.error__empty_initials);
            return false;
        }

//        int region = AppData.INSPECTION.region;
//        if (region==0 && mLayout_Region.getVisibility()==View.VISIBLE) {
//            showMessage(getActivity(), R.string.error__empty_region);
//            return false;
//        }

//        if (mList_FieldManager.size()==0) {
//            showMessage(getActivity(), R.string.error__another_region);
//            return false;
//        }

        if (mList_FieldManager.size()>0) {
            int position = mSpinner_FieldManager.getSelectedItemPosition();
            if (mList_FieldManager.size()>position) {
                SpinnerInfo item = mList_FieldManager.get(position);
                if (item!=null && item.id!=0) {
                    AppData.INSPECTION.field_manager = item.id;

                } else {
//                showMessage(getActivity(), R.string.error__empty_field_manager);
//                return false;
                }
            } else {
//            showMessage(getActivity(), R.string.error__empty_field_manager);
//            return false;
            }
        }

        return true;
    }

    @Override
    public void saveForm() {
        //String community = mText_Community.getText().toString();  // Removed by BongBong. 20160407
        String address = mText_Address.getText().toString();
        String inspector_initial = mText_InspectorInitials.getText().toString();
        String inspection_date = mText_InspectionDate.getText().toString();
        String permit_number = mText_PermitNumber.getText().toString();

        //AppData.INSPECTION.community = community;  // Removed by BongBong. 20160407
        AppData.INSPECTION.address = address;

        AppData.INSPECTION.inspection_initials = inspector_initial;
        AppData.INSPECTION.inspection_date = inspection_date;
        AppData.INSPECTION.ready_inspection = mSpinner_ReadyInspection.getSelectedItemPosition() == 1 ? true : false;
        AppData.INSPECTION.permit_number = permit_number;

        AppData.INSPECTION.front_building.copy(mPicture_FrontBuilding);
    }

    @Override
    public void restoreForm() {
        mText_JobNumber.setText(AppData.INSPECTION.job_number);

        mText_Community.setText(AppData.INSPECTION.getCommunityName());
        mText_Lot.setText(AppData.INSPECTION.lot);
        mText_Address.setText(AppData.INSPECTION.address);
        mText_PermitNumber.setText(AppData.INSPECTION.permit_number);

        if (AppData.INSPECTION.is_building_unit) {
            mText_Address.setEnabled(false);
        } else {
            mText_Address.setEnabled(true);
        }

        mText_InspectorInitials.setText(AppData.INSPECTION.inspection_initials);
        mText_InspectionDate.setText(AppData.INSPECTION.inspection_date);
        mSpinner_ReadyInspection.setSelection(AppData.INSPECTION.ready_inspection ? 1 : 0);

        mText_Location.setText(AppData.INSPECTION.location.getDisplayedText());

        mPicture_FrontBuilding.copy(AppData.INSPECTION.front_building);
        mText_FrontBuilding.setText(mPicture_FrontBuilding.getDisplayedText());

        emptyRegion();
        emptyFieldManager();

        loadRegion();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_inspection_date:
//                showCalendar();
                break;

            case R.id.btn_gps:
            case R.id.txt_gps_location:
                getCurrentLocation();
                break;

            case R.id.btn_picture:
            case R.id.txt_front_building:
                showPictureMenu();
                break;
        }
    }

    private void showCalendar() {
        String date = mText_InspectionDate.getText().toString();
        final int year = Utils.getInt(date.substring(6, 10));
        final int month = Utils.getInt(date.substring(0, 2));
        final int day = Utils.getInt(date.substring(3, 5));

        DatePickerDialog dialog = DatePickerDialog.newInstance(this, year, month - 1, day);
        dialog.show(getActivity().getFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int month, int day) {
        Log.e("onDateSet", year + "/" + month + "/" + day);
        String date = Utils.fillZero(String.valueOf(month + 1), 2) + "/" + Utils.fillZero(String.valueOf(day), 2) + "/" + Utils.fillZero(String.valueOf(year), 4);
        mText_InspectionDate.setText(date);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        getLocation();
    }

    private void getLocation() {
        LocationTracker tracker = new LocationTracker(getActivity());
        Location location = tracker.getCurrentLocation();
        if (location != null) {
            AppData.INSPECTION.location.latitude = location.getLatitude();
            AppData.INSPECTION.location.longitude = location.getLongitude();
            AppData.INSPECTION.location.accuracy = location.getAccuracy();
            AppData.INSPECTION.location.address = "captured";
        } else {
            AppData.INSPECTION.location.address = "";
        }

        mHandler.sendEmptyMessageDelayed(1, 100);
    }


    public void getCurrentLocation() {
        showLoading("Capturing Location.....");

        if (Build.VERSION.SDK_INT>=23) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION }, 200);
            } else {
                getLocation();
            }
        } else {
            getLocation();
        }
    }

    public void showPictureMenu() {
        PicturePickerDialog dialog;
        if (mPicture_FrontBuilding.mode == Constants.PICTURE_EMPTY) {
            dialog = new PicturePickerDialog(getActivity(), this, Constants.ACTION_NEW);
        } else {
            dialog = new PicturePickerDialog(getActivity(), this, Constants.ACTION_DELETE);
        }
        dialog.show();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                hideLoading();
                mText_Location.setText(AppData.INSPECTION.location.getDisplayedText());

                if (AppData.INSPECTION.location.address.length()==0)
                    showMessage("Failed to get location");
            }

            if (msg.what == 100) {

            }
        }
    };

    @Override
    public void onCamera() {
        String path = StorageUtils.getAppDirectory();
        if (path.length() > 0) {
            String filename = CalenderUtils.getTodayWith14Chars() + "_" + UUID.randomUUID().toString().replaceAll("-", "") + ".jpg";
            AppData.TAKEN_PICTURE = path + "/" + filename;
            takePictureFromCamera(ACTIVITY_RESULT__CAMERA);
        }
    }

    @Override
    public void onGallery() {
        takePictureFromGallery(ACTIVITY_RESULT__GALLERY);
    }

    @Override
    public void onView() {
        PicturePreviewDialog dialog = new PicturePreviewDialog(getActivity(), mPicture_FrontBuilding);
        dialog.show();
    }

    @Override
    public void onRemove() {
        mPicture_FrontBuilding.init();
        mText_FrontBuilding.setText(mPicture_FrontBuilding.getDisplayedText());
    }

    @Override
    public void onCapture() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != getActivity().RESULT_OK)
            return;

        Uri uri = null;

        if (data != null) {
            uri = data.getData();
        }

        if (ACTIVITY_RESULT__CAMERA == requestCode) {
            if (uri == null && AppData.TAKEN_PICTURE.length() > 0) {
                uri = Uri.fromFile(new File(AppData.TAKEN_PICTURE));
            }

            if (uri != null) {
                takePicture(AppData.TAKEN_PICTURE);
            }
        }

        if (ACTIVITY_RESULT__GALLERY == requestCode) {
            if (uri != null) {
                String filePath = getFilePathFromActivityResultUri(uri);
                if (filePath != null && filePath.length() > 0) {
                    String path = StorageUtils.getAppDirectory() + CalenderUtils.getTodayWith14Chars() + "_" + UUID.randomUUID().toString().replaceAll("-", "") + ".jpg";
                    try {
                        copyFile(new File(filePath), new File(path));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    takePicture(path);
                }
            }
        }

    }


    public void takePicture(final String filepath) {
        new ImageProcessing().execute(filepath);
    }

    private void loadRegionFromLocalStorage() {
        final InspectionDatabase db = InspectionDatabase.getInstance(getActivity());

        int index = 0;
        int selected = -1;

        ArrayList<SpinnerInfo> regions = db.loadRegion();
        for (SpinnerInfo item : regions) {
            mAdapter_Region.add(item.name);
            mList_Region.add(item);

            if (item.id == AppData.INSPECTION.region)
                selected = index;

            index++;
        }

        mLayout_Region.setVisibility(View.VISIBLE);
        mAdapter_Region.notifyDataSetChanged();

        if (selected != -1)
            mSpinner_Region.setSelection(selected);
        else {
            if (mList_Region.size() > 0)
                AppData.INSPECTION.region = mList_Region.get(0).id;
            else
                AppData.INSPECTION.region = 0;
        }

        if (mList_Region.size()==0)
            emptyRegion();

        loadFieldManager(false);
    }

    private void loadRegion() {
        mAdapter_Region.clear();
        mList_Region.clear();

        mAdapter_FieldManager.clear();
        mList_FieldManager.clear();

        showLoading(Constants.MSG_LOADING);

        if (!DeviceUtils.isInternetAvailable(getActivity())) {
            showMessage(Constants.MSG_CONNECTION);
            loadRegionFromLocalStorage();
            return;
        }

        RequestParams params = new RequestParams();
        params.put("community_id", AppData.INSPECTION.community);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(Constants.CONNECTION_TIMEOUT * 1000);
        client.setSSLSocketFactory(SecurityUtils.getSSLSocketFactory());
        client.post(getActivity(), Constants.API__BASEPATH + Constants.API__CHECK_COMMUNITY, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (statusCode == 200 && response != null) {
                    Log.i("Community", response.toString());

                    try {
                        JSONObject status = response.getJSONObject("status");
                        int code = Utils.checkNull(status.getInt("code"), 0);
                        String message = Utils.checkNull(status.getString("message"));
                        if (code == 0) {
                            JSONObject obj = response.getJSONObject("response");
                            Log.i("Region", obj.toString());

                            String community_name = Utils.checkNull(obj.getString("community_name"));
                            AppData.INSPECTION.community_name = community_name;

//                            String date = Utils.checkNull(obj.getString("date"));
//                            AppData.INSPECTION.inspection_date = date;
//                            AppData.INSPECTION.inspection_date_last = date;

                            int region = Utils.checkNull(obj.getInt("region"), 0);
                            if (region == 0) {
                                hideLoading();

                                int index = 0;
                                int selected = -1;
                                JSONArray regions = obj.getJSONArray("regions");
                                for (int i = 0; i < regions.length(); i++) {
                                    JSONObject r = regions.getJSONObject(i);
                                    int id = Utils.checkNull(r.getString("id"), 0);
                                    String name = Utils.checkNull(r.getString("region"));

                                    if (id != 0) {
                                        SpinnerInfo item = new SpinnerInfo(id, name);
                                        mAdapter_Region.add(name);
                                        mList_Region.add(item);

                                        if (id == AppData.INSPECTION.region)
                                            selected = index;

                                        index++;
                                    }
                                }

                                mLayout_Region.setVisibility(View.VISIBLE);
                                mAdapter_Region.notifyDataSetChanged();

                                if (selected != -1)
                                    mSpinner_Region.setSelection(selected);
                                else {
                                    if (mList_Region.size() > 0)
                                        AppData.INSPECTION.region = mList_Region.get(0).id;
                                    else
                                        AppData.INSPECTION.region = 0;
                                }

                                if (mList_Region.size()==0)
                                    emptyRegion();

                                loadFieldManager(false);
                            } else {
                                AppData.INSPECTION.region = region;
                                loadFieldManager(false);
                            }
                        } else {
                            hideLoading();
                            showMessage(message);
                        }
                    } catch (Exception e) {
                        hideLoading();
                        showMessage(Constants.MSG_FAILED);
                    }
                } else {
//                    hideLoading();
                    showMessage(Constants.MSG_CONNECTION);

                    loadRegionFromLocalStorage();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                hideLoading();
                showMessage(Constants.MSG_CONNECTION);

                loadRegionFromLocalStorage();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                hideLoading();
                showMessage(Constants.MSG_CONNECTION);

                loadRegionFromLocalStorage();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                hideLoading();
                showMessage(Constants.MSG_CONNECTION);

                loadRegionFromLocalStorage();
            }
        });
    }

    private void loadFieldManagerFromLocalStorage(final boolean loading) {
        final InspectionDatabase db = InspectionDatabase.getInstance(getActivity());

        int index = 0;
        int selected = -1;

        ArrayList<SpinnerInfo> fms = db.loadFieldManager(AppData.INSPECTION.region);
        for (SpinnerInfo item : fms) {
            mAdapter_FieldManager.add(item.name);
            mList_FieldManager.add(item);

            if (item.id == AppData.INSPECTION.field_manager && !loading)
                selected = index;

            index++;
        }

        mAdapter_FieldManager.notifyDataSetChanged();
        if (selected != -1)
            mSpinner_FieldManager.setSelection(selected);
        else {
            if (mList_FieldManager.size() > 0)
                AppData.INSPECTION.field_manager = mList_FieldManager.get(0).id;
            else
                AppData.INSPECTION.field_manager = 0;
        }

        if (mList_FieldManager.size()==0)
            emptyFieldManager();

        bFirst = false;
        hideLoading();
    }

    private void loadFieldManager(final boolean loading) {
        mText_Community.setText(AppData.INSPECTION.getCommunityName());
        mText_InspectionDate.setText(AppData.INSPECTION.inspection_date);

        mAdapter_FieldManager.clear();
        mList_FieldManager.clear();

        if (loading)
            showLoading(Constants.MSG_LOADING);

        if (AppData.INSPECTION.region==0) {
            hideLoading();
            AppData.INSPECTION.field_manager = 0;

            bFirst = false;
            emptyFieldManager();
            return;
        }

        mLayout_FieldManager.setVisibility(View.VISIBLE);

        if (!DeviceUtils.isInternetAvailable(getActivity())) {
            showMessage(Constants.MSG_CONNECTION);
            loadFieldManagerFromLocalStorage(loading);
            return;
        }

        RequestParams params = new RequestParams();
        params.put("region", SecurityUtils.encodeKey(""+AppData.INSPECTION.region));

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(Constants.CONNECTION_TIMEOUT * 1000);
        client.setSSLSocketFactory(SecurityUtils.getSSLSocketFactory());
        client.post(getActivity(), Constants.API__BASEPATH + Constants.API__USER_FIELD_MANAGER, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                if (statusCode == 200 && response != null) {
                    hideLoading();
                    Log.i("Field Manager", AppData.INSPECTION.region + " ---> " + response.toString());

                    try {
                        JSONObject status = response.getJSONObject("status");
                        int code = Utils.checkNull(status.getInt("code"), 0);
                        String message = Utils.checkNull(status.getString("message"));
                        if (code == 0) {
                            JSONObject obj = response.getJSONObject("response");
                            Log.i("FM", obj.toString());

                            int index = 0;
                            int selected = -1;
                            JSONArray users = obj.getJSONArray("user");
                            for (int i = 0; i < users.length(); i++) {
                                JSONObject r = users.getJSONObject(i);
                                int id = Utils.checkNull(r.getString("id"), 0);
                                String first_name = Utils.checkNull(r.getString("first_name"));
                                String last_name = Utils.checkNull(r.getString("last_name"));
                                String name = first_name + " " + last_name;

                                if (id != 0) {
                                    SpinnerInfo item = new SpinnerInfo(id, name);
                                    mAdapter_FieldManager.add(name);
                                    mList_FieldManager.add(item);

                                    if (id == AppData.INSPECTION.field_manager && !loading)
                                        selected = index;

                                    index++;
                                }
                            }

                            mAdapter_FieldManager.notifyDataSetChanged();
                            if (selected != -1)
                                mSpinner_FieldManager.setSelection(selected);
                            else {
                                if (mList_FieldManager.size() > 0)
                                    AppData.INSPECTION.field_manager = mList_FieldManager.get(0).id;
                                else
                                    AppData.INSPECTION.field_manager = 0;
                            }

                            if (mList_FieldManager.size()==0)
                                emptyFieldManager();

                        } else {
                            showMessage(message);
                        }
                    } catch (Exception e) {
                        showMessage(Constants.MSG_FAILED);
                    }
                } else {
                    showMessage(Constants.MSG_CONNECTION);

                    loadFieldManagerFromLocalStorage(loading);
                }

                bFirst = false;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                bFirst = false;
//                hideLoading();
                showMessage(Constants.MSG_CONNECTION);

                loadFieldManagerFromLocalStorage(loading);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                bFirst = false;
//                hideLoading();
                showMessage(Constants.MSG_CONNECTION);

                loadFieldManagerFromLocalStorage(loading);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                bFirst = false;
//                hideLoading();
                showMessage(Constants.MSG_CONNECTION);

                loadFieldManagerFromLocalStorage(loading);
            }
        });
    }

    private void emptyRegion() {
        mAdapter_Region.clear();
        mAdapter_Region.add("No Region");
        mAdapter_Region.notifyDataSetChanged();
    }

    private void emptyFieldManager() {
        mAdapter_FieldManager.clear();
        mAdapter_FieldManager.add("No Field Manager");
        mAdapter_FieldManager.notifyDataSetChanged();
    }

    public class ImageProcessing extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            showLoading(Constants.MSG_LOADING);
        }

        @Override
        protected Void doInBackground(String... params) {
            String file = applyImageRotation(params[0]);
            file = scaleImage(file);

            mPicture_FrontBuilding.mode = Constants.PICTURE_LOCAL;
            mPicture_FrontBuilding.image = file;

            AppData.TAKEN_PICTURE = "";
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mText_FrontBuilding.setText(mPicture_FrontBuilding.getDisplayedText());
            hideLoading();
        }
    }
}
