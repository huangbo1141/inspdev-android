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
public class BasicWCI_Step extends BaseFragment implements View.OnClickListener, PicturePickerListener {

    EditText mText_Community, mText_Lot, mText_Address, mText_JobNumber,mText_PermitNumber;
    TextView mText_Location, mText_FrontBuilding;

    Spinner mSpinner_ReadyInspection;

    PictureInfo mPicture_FrontBuilding, mPicture_TestingSetup, mPicture_Manometer;
    EditText mText_Area, mText_Volume, mText_WallArea, mText_CeilingArea;
    TextView mText_TestingSetup, mText_Manometer;

    public BasicWCI_Step() {
        mPicture_FrontBuilding = new PictureInfo();
        mPicture_TestingSetup = new PictureInfo();
        mPicture_Manometer = new PictureInfo();

        AppData.TAKEN_KIND = 0;
        AppData.TAKEN_PICTURE = "";
    }

    public static BasicWCI_Step newInstance(Object... args) {
        return new BasicWCI_Step();
    }

    @Override
    public String getFragmentTag() {
        return BasicWCI_Step.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        if (mBridge == null)
            return root;

        View mContentView = inflater.inflate(R.layout.step_basic_wci, container, false);

        mText_Community = (EditText) mContentView.findViewById(R.id.txt_community);
        mText_PermitNumber = (EditText) mContentView.findViewById(R.id.txt_permit_number);
        mText_Lot = (EditText) mContentView.findViewById(R.id.txt_lot);
        mText_Address = (EditText) mContentView.findViewById(R.id.txt_address);
        mText_JobNumber = (EditText) mContentView.findViewById(R.id.txt_job_number);

        mText_Location = (TextView) mContentView.findViewById(R.id.txt_gps_location);
        mText_FrontBuilding = (TextView) mContentView.findViewById(R.id.txt_front_building);

        mText_Area = (EditText) mContentView.findViewById(R.id.txt_area);
        mText_Volume = (EditText) mContentView.findViewById(R.id.txt_volume);
        mText_WallArea = (EditText) mContentView.findViewById(R.id.txt_wall_area);
        mText_CeilingArea = (EditText) mContentView.findViewById(R.id.txt_ceiling_area);

        mText_TestingSetup = (TextView) mContentView.findViewById(R.id.txt_testing_setup);
        mText_Manometer = (TextView) mContentView.findViewById(R.id.txt_manometer);

        mSpinner_ReadyInspection = (Spinner) mContentView.findViewById(R.id.spinner_ready_inspection);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.yes_no, R.layout.layout_spinner);
        mSpinner_ReadyInspection.setAdapter(adapter);

        mText_Location.setOnClickListener(this);
        mContentView.findViewById(R.id.btn_gps).setOnClickListener(this);

        mText_FrontBuilding.setOnClickListener(this);
        mContentView.findViewById(R.id.btn_picture).setOnClickListener(this);

        mText_TestingSetup.setOnClickListener(this);
        mContentView.findViewById(R.id.btn_testing_setup).setOnClickListener(this);

        mText_Manometer.setOnClickListener(this);
        mContentView.findViewById(R.id.btn_manometer).setOnClickListener(this);

        return mContentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        restoreForm();
    }

    @Override
    public boolean validateForm() {
//        String community = mText_Community.getText().toString();
//        String address = mText_Address.getText().toString();
//        String lot = mText_Lot.getText().toString();

//        if (community.length()==0) {
//            showMessage(R.string.error__empty_community);
//            return false;
//        }
//
//        if (address.length()==0) {
//            showMessage(R.string.error__empty_address);
//            return false;
//        }

//        if (lot.length()==0) {
//            showMessage(R.string.error__empty_lot);
//            return false;
//        }

        return true;
    }

    @Override
    public void saveForm() {
        AppData.INSPECTION.ready_inspection = mSpinner_ReadyInspection.getSelectedItemPosition() == 1 ? true : false;

        AppData.INSPECTION.front_building.copy(mPicture_FrontBuilding);

        AppData.INSPECTION.permit_number = mText_PermitNumber.getText().toString();
//        AppData.INSPECTION.testing_setup.copy(mPicture_TestingSetup);
//        AppData.INSPECTION.manometer.copy(mPicture_Manometer);
    }

    @Override
    public void restoreForm() {
        mText_JobNumber.setText(AppData.INSPECTION.job_number);

        mText_Community.setText(AppData.INSPECTION.getCommunityName());
        mText_PermitNumber.setText(AppData.INSPECTION.permit_number);
        mText_Lot.setText(AppData.INSPECTION.lot);
        mText_Address.setText(AppData.INSPECTION.address);

        mSpinner_ReadyInspection.setSelection(AppData.INSPECTION.ready_inspection ? 1 : 0);

        mText_Location.setText(AppData.INSPECTION.location.getDisplayedText());

        mPicture_FrontBuilding.copy(AppData.INSPECTION.front_building);
        mText_FrontBuilding.setText(mPicture_FrontBuilding.getDisplayedText());

        mPicture_TestingSetup.copy(AppData.INSPECTION.testing_setup);
        mText_TestingSetup.setText(mPicture_TestingSetup.getDisplayedText());

        mPicture_Manometer.copy(AppData.INSPECTION.manometer);
        mText_Manometer.setText(mPicture_Manometer.getDisplayedText());

        mText_Area.setText(AppData.INSPECTION.area+"");
        mText_Volume.setText(AppData.INSPECTION.volume+"");
        mText_WallArea.setText(AppData.INSPECTION.wall_area+"");
        mText_CeilingArea.setText(AppData.INSPECTION.ceiling_area+"");
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
                showPictureMenu(1);
                break;

            case R.id.btn_testing_setup:
            case R.id.txt_testing_setup:
                showPictureMenu(2);
                break;

            case R.id.btn_manometer:
            case R.id.txt_manometer:
                showPictureMenu(3);
                break;
        }
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

    public void showPictureMenu(int kind) {
        AppData.TAKEN_KIND = kind;

        PicturePickerDialog dialog = null;

        if (AppData.TAKEN_KIND==1) {
            if (mPicture_FrontBuilding.mode == Constants.PICTURE_EMPTY) {
                dialog = new PicturePickerDialog(getActivity(), this, Constants.ACTION_NEW);
            } else {
                dialog = new PicturePickerDialog(getActivity(), this, Constants.ACTION_DELETE);
            }
        }

        if (AppData.TAKEN_KIND==2) {
            if (mPicture_TestingSetup.mode == Constants.PICTURE_EMPTY) {
                dialog = new PicturePickerDialog(getActivity(), this, Constants.ACTION_NEW);
            } else {
                dialog = new PicturePickerDialog(getActivity(), this, Constants.ACTION_DELETE);
            }
        }

        if (AppData.TAKEN_KIND==3) {
            if (mPicture_Manometer.mode == Constants.PICTURE_EMPTY) {
                dialog = new PicturePickerDialog(getActivity(), this, Constants.ACTION_NEW);
            } else {
                dialog = new PicturePickerDialog(getActivity(), this, Constants.ACTION_DELETE);
            }
        }

        if (dialog!=null)
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
        PicturePreviewDialog dialog = null;

        if (AppData.TAKEN_KIND==1) {
            dialog = new PicturePreviewDialog(getActivity(), mPicture_FrontBuilding);
        }
        if (AppData.TAKEN_KIND==2) {
            dialog = new PicturePreviewDialog(getActivity(), mPicture_TestingSetup);
        }
        if (AppData.TAKEN_KIND==3) {
            dialog = new PicturePreviewDialog(getActivity(), mPicture_Manometer);
        }

        if (dialog!=null)
            dialog.show();
    }

    @Override
    public void onRemove() {
        if (AppData.TAKEN_KIND==1) {
            mPicture_FrontBuilding.init();
            mText_FrontBuilding.setText(mPicture_FrontBuilding.getDisplayedText());
        }
        if (AppData.TAKEN_KIND==2) {
            mPicture_TestingSetup.init();
            mText_TestingSetup.setText(mPicture_TestingSetup.getDisplayedText());
        }
        if (AppData.TAKEN_KIND==3) {
            mPicture_Manometer.init();
            mText_Manometer.setText(mPicture_Manometer.getDisplayedText());
        }
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

            if (AppData.TAKEN_KIND==1) {
                mPicture_FrontBuilding.mode = Constants.PICTURE_LOCAL;
                mPicture_FrontBuilding.image = file;
            }
            if (AppData.TAKEN_KIND==2) {
                mPicture_TestingSetup.mode = Constants.PICTURE_LOCAL;
                mPicture_TestingSetup.image = file;
            }
            if (AppData.TAKEN_KIND==3) {
                mPicture_Manometer.mode = Constants.PICTURE_LOCAL;
                mPicture_Manometer.image = file;
            }

            AppData.TAKEN_PICTURE = "";
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (AppData.TAKEN_KIND==1)
                mText_FrontBuilding.setText(mPicture_FrontBuilding.getDisplayedText());
            if (AppData.TAKEN_KIND==2)
                mText_TestingSetup.setText(mPicture_TestingSetup.getDisplayedText());
            if (AppData.TAKEN_KIND==3)
                mText_Manometer.setText(mPicture_Manometer.getDisplayedText());

            AppData.TAKEN_KIND=0;
            hideLoading();
        }
    }
}
