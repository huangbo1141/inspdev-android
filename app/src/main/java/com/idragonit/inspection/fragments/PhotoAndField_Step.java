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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.idragonit.inspection.AppData;
import com.idragonit.inspection.BaseFragment;
import com.idragonit.inspection.Constants;
import com.idragonit.inspection.R;
import com.idragonit.inspection.components.MaskedEditText;
import com.idragonit.inspection.components.PicturePickerDialog;
import com.idragonit.inspection.components.PicturePickerListener;
import com.idragonit.inspection.components.PicturePreviewDialog;
import com.idragonit.inspection.core.LocationTracker;
import com.idragonit.inspection.models.PictureInfo;
import com.idragonit.inspection.models.UnitInfo;
import com.idragonit.inspection.utils.CalenderUtils;
import com.idragonit.inspection.utils.InspectionUtils;
import com.idragonit.inspection.utils.StorageUtils;
import com.idragonit.inspection.utils.Utils;

import java.io.File;
import java.util.UUID;

/**
 * Created by CJH on 2016.01.23.
 */
public class PhotoAndField_Step extends BaseFragment implements View.OnClickListener, PicturePickerListener {

    MaskedEditText mText_Pressure, mText_Flow;

    PictureInfo mPicture_Manometer;
    TextView mText_Manometer;

    public PhotoAndField_Step() {
        mPicture_Manometer = new PictureInfo();

        AppData.TAKEN_KIND = 0;
        AppData.TAKEN_PICTURE = "";
    }

    public static PhotoAndField_Step newInstance(Object... args) {
        return new PhotoAndField_Step();
    }

    @Override
    public String getFragmentTag() {
        return PhotoAndField_Step.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        if (mBridge == null)
            return root;

        View mContentView = inflater.inflate(R.layout.step_photo_field, container, false);

        mText_Pressure = (MaskedEditText) mContentView.findViewById(R.id.txt_pressure);
        mText_Pressure.setMask("##.#");
        mText_Pressure.setPlaceholder('0');



        mText_Flow = (MaskedEditText) mContentView.findViewById(R.id.txt_flow);
        mText_Flow.setMask("####.#");
        mText_Flow.setPlaceholder('0');

        mText_Manometer = (TextView) mContentView.findViewById(R.id.txt_manometer);

        mText_Manometer.setOnClickListener(this);
        mContentView.findViewById(R.id.btn_manometer).setOnClickListener(this);

        mText_Flow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mHandler.sendEmptyMessageDelayed(100,100);

                return false;
            }
        });

        mText_Pressure.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mHandler.sendEmptyMessageDelayed(200,100);

                return false;
            }
        });

        return mContentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        restoreForm();

        mText_Pressure.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                refreshResult(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mText_Flow.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                refreshResult(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private boolean refreshResult(boolean warning) {
        float pressure = 0;
        float flow = 0;

        String text = mText_Pressure.getText().toString();
        if (text.length()>0)
            pressure = Utils.getFloat(text);

        text = mText_Flow.getText().toString();
        if (text.length()>0)
            flow = Utils.getFloat(text);

        if (pressure==0) {
            if (warning)
                showMessage("Please Enter Home Pressure!");

            return false;
        }

        if (flow==0) {
            if (warning)
                showMessage("Please Enter Flow!");

            return false;
        }

        return true;
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
        AppData.INSPECTION.manometer.copy(mPicture_Manometer);
        AppData.INSPECTION.house_pressure = Utils.getFloat(mText_Pressure.getText().toString());
        AppData.INSPECTION.flow = Utils.getFloat(mText_Flow.getText().toString());
    }

    @Override
    public void restoreForm() {

        mPicture_Manometer.copy(AppData.INSPECTION.manometer);
        mText_Manometer.setText(mPicture_Manometer.getDisplayedText());

        mText_Pressure.setText(AppData.INSPECTION.house_pressure+"");
        mText_Flow.setText(AppData.INSPECTION.flow+"");


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

        }

        if (AppData.TAKEN_KIND==2) {

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
            }

            switch (msg.what){
                case 100:{
                    mText_Flow.setSelection(0,0);

                    break;
                }
                case 200:{
                    mText_Pressure.setSelection(0,0);
                    break;
                }
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
        }
        if (AppData.TAKEN_KIND==2) {
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
        }
        if (AppData.TAKEN_KIND==2) {
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
            }
            if (AppData.TAKEN_KIND==2) {
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

            if (AppData.TAKEN_KIND==1){
            }

            if (AppData.TAKEN_KIND==2){

            }
            if (AppData.TAKEN_KIND==3)
                mText_Manometer.setText(mPicture_Manometer.getDisplayedText());

            AppData.TAKEN_KIND=0;
            hideLoading();
        }
    }
}
