package com.idragonit.inspection.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.idragonit.inspection.AppData;
import com.idragonit.inspection.BaseFragment;
import com.idragonit.inspection.Constants;
import com.idragonit.inspection.R;
import com.idragonit.inspection.components.PicturePickerDialog;
import com.idragonit.inspection.components.PicturePickerListener;
import com.idragonit.inspection.components.PicturePreviewDialog;
import com.idragonit.inspection.models.PictureInfo;
import com.idragonit.inspection.models.UnitInfo;
import com.idragonit.inspection.utils.CalenderUtils;
import com.idragonit.inspection.utils.StorageUtils;

import java.io.File;
import java.util.UUID;

/**
 * Created by CJH on 2016.01.23.
 */
public class UnitWCI_Step extends BaseFragment implements View.OnClickListener, PicturePickerListener {

    EditText mText_Supply1, mText_Return1;
    EditText mText_Supply2, mText_Return2;
    EditText mText_Supply3, mText_Return3;
    EditText mText_Supply4, mText_Return4;

    TextView mText_TestingSetup;

    PictureInfo mPicture_TestingSetup;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
            }

            if (msg.what == 100) {
            }
        }
    };

    public UnitWCI_Step() {
        mPicture_TestingSetup = new PictureInfo();

        AppData.TAKEN_KIND = 0;
        AppData.TAKEN_PICTURE = "";
    }

    public static UnitWCI_Step newInstance(Object... args) {
        return new UnitWCI_Step();
    }

    @Override
    public String getFragmentTag() {
        return UnitWCI_Step.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        if (mBridge == null)
            return root;

        View mContentView = inflater.inflate(R.layout.step_unit_wci, container, false);

        mText_Supply1 = (EditText) mContentView.findViewById(R.id.txt_unit1_supply);
        mText_Return1 = (EditText) mContentView.findViewById(R.id.txt_unit1_return);

        mText_Supply2 = (EditText) mContentView.findViewById(R.id.txt_unit2_supply);
        mText_Return2 = (EditText) mContentView.findViewById(R.id.txt_unit2_return);

        mText_Supply3 = (EditText) mContentView.findViewById(R.id.txt_unit3_supply);
        mText_Return3 = (EditText) mContentView.findViewById(R.id.txt_unit3_return);

        mText_Supply4 = (EditText) mContentView.findViewById(R.id.txt_unit4_supply);
        mText_Return4 = (EditText) mContentView.findViewById(R.id.txt_unit4_return);

        mText_TestingSetup = (TextView) mContentView.findViewById(R.id.txt_testing_setup);
        mText_TestingSetup.setOnClickListener(this);

        mContentView.findViewById(R.id.btn_testing_setup).setOnClickListener(this);


        return mContentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        restoreForm();
    }

    @Override
    public boolean validateForm() {
        String i_supply = mText_Supply1.getText().toString();
        String i_return = mText_Return1.getText().toString();

//        if (i_supply.length() == 0) {
//            showMessage("Please Enter Unit 1 Supply");
//            return false;
//        }
//
//        if (i_return.length() == 0) {
//            showMessage("Please Enter Unit 1 Return");
//            return false;
//        }

        return true;
    }

    @Override
    public void saveForm() {

        AppData.INSPECTION.testing_setup.copy(mPicture_TestingSetup);

        String i_supply = mText_Supply1.getText().toString();
        String i_return = mText_Return1.getText().toString();

        UnitInfo unit1 = new UnitInfo(1, i_supply, i_return);
        AppData.setUnit(unit1);

        i_supply = mText_Supply2.getText().toString();
        i_return = mText_Return2.getText().toString();

        UnitInfo unit2 = new UnitInfo(2, i_supply, i_return);
        AppData.setUnit(unit2);

        i_supply = mText_Supply3.getText().toString();
        i_return = mText_Return3.getText().toString();

        UnitInfo unit3 = new UnitInfo(3, i_supply, i_return);
        AppData.setUnit(unit3);

        i_supply = mText_Supply4.getText().toString();
        i_return = mText_Return4.getText().toString();

        UnitInfo unit4 = new UnitInfo(4, i_supply, i_return);
        AppData.setUnit(unit4);
    }

    @Override
    public void restoreForm() {
        UnitInfo unit1 = AppData.getUnit(1);
        UnitInfo unit2 = AppData.getUnit(2);
        UnitInfo unit3 = AppData.getUnit(3);
        UnitInfo unit4 = AppData.getUnit(4);

        mText_Supply1.setText(unit1.i_supply);
        mText_Return1.setText(unit1.i_return);

        mText_Supply2.setText(unit2.i_supply);
        mText_Return2.setText(unit2.i_return);

        mText_Supply3.setText(unit3.i_supply);
        mText_Return3.setText(unit3.i_return);

        mText_Supply4.setText(unit4.i_supply);
        mText_Return4.setText(unit4.i_return);

        mPicture_TestingSetup.copy(AppData.INSPECTION.testing_setup);
        mText_TestingSetup.setText(mPicture_TestingSetup.getDisplayedText());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_testing_setup:
            case R.id.txt_testing_setup:
                showPictureMenu(2);
        }
    }

    public void showPictureMenu(int kind) {
        AppData.TAKEN_KIND = kind;

        PicturePickerDialog dialog = null;

        if (AppData.TAKEN_KIND == 1) {
        }

        if (AppData.TAKEN_KIND == 2) {
            if (mPicture_TestingSetup.mode == Constants.PICTURE_EMPTY) {
                dialog = new PicturePickerDialog(getActivity(), this, Constants.ACTION_NEW);
            } else {
                dialog = new PicturePickerDialog(getActivity(), this, Constants.ACTION_DELETE);
            }
        }

        if (AppData.TAKEN_KIND == 3) {
        }

        if (dialog != null)
            dialog.show();
    }


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

        if (AppData.TAKEN_KIND == 1) {
        }
        if (AppData.TAKEN_KIND == 2) {
            dialog = new PicturePreviewDialog(getActivity(), mPicture_TestingSetup);
        }
        if (AppData.TAKEN_KIND == 3) {
        }

        if (dialog != null)
            dialog.show();
    }

    @Override
    public void onRemove() {
        if (AppData.TAKEN_KIND == 1) {
        }
        if (AppData.TAKEN_KIND == 2) {
            mPicture_TestingSetup.init();
            mText_TestingSetup.setText(mPicture_TestingSetup.getDisplayedText());
        }
        if (AppData.TAKEN_KIND == 3) {
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

            if (AppData.TAKEN_KIND == 1) {
            }
            if (AppData.TAKEN_KIND == 2) {
                mPicture_TestingSetup.mode = Constants.PICTURE_LOCAL;
                mPicture_TestingSetup.image = file;
            }
            if (AppData.TAKEN_KIND == 3) {
            }

            AppData.TAKEN_PICTURE = "";
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (AppData.TAKEN_KIND == 1) {

            }
            if (AppData.TAKEN_KIND == 2) {
                mText_TestingSetup.setText(mPicture_TestingSetup.getDisplayedText());
            }

            if (AppData.TAKEN_KIND == 3) {

            }
            AppData.TAKEN_KIND = 0;
            hideLoading();
        }
    }
}
