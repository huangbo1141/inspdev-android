package com.idragonit.inspection.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import com.idragonit.inspection.components.SignatureDialog;
import com.idragonit.inspection.models.CheckingItemInfo;
import com.idragonit.inspection.models.PictureInfo;
import com.idragonit.inspection.models.UnitInfo;
import com.idragonit.inspection.utils.CalenderUtils;
import com.idragonit.inspection.utils.InspectionUtils;
import com.idragonit.inspection.utils.StorageUtils;
import com.idragonit.inspection.utils.Utils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.File;
import java.util.UUID;

/**
 * Created by CJH on 2016.01.23.
 */
public class ResultWCI_Step extends BaseFragment implements View.OnClickListener, PicturePickerListener {

    EditText mText_Comments;

    TextView mText_Signature;
    PictureInfo mPicture_Signature;


    TextView mResult_DuctLeakage, mResult_EnvelopLeakage;
    TextView mText_Qn, mText_QnOut, mText_Ach50;

    public ResultWCI_Step() {
        mPicture_Signature = new PictureInfo(Constants.PICTURE_SIGNATURE);

        AppData.TAKEN_KIND = 0;
        AppData.TAKEN_PICTURE = "";
    }

    public static ResultWCI_Step newInstance(Object... args) {
        ResultWCI_Step fragment = new ResultWCI_Step();

        if (args!=null && args.length>0) {
        }

        return fragment;
    }

    @Override
    public String getFragmentTag() {
        return ResultWCI_Step.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        if (mBridge == null)
            return root;

        View mContentView = inflater.inflate(R.layout.step_result_wci, container, false);

        mText_Comments = (EditText) mContentView.findViewById(R.id.txt_comments);

        mResult_DuctLeakage = (TextView) mContentView.findViewById(R.id.txt_result_duct_leakage);
        mResult_EnvelopLeakage = (TextView) mContentView.findViewById(R.id.txt_result_envelop_leakage);

        mText_Qn = (TextView) mContentView.findViewById(R.id.txt_qn);
        mText_QnOut = (TextView) mContentView.findViewById(R.id.txt_qn_out);
        mText_Ach50 = (TextView) mContentView.findViewById(R.id.txt_ach50);

        mText_Signature = (TextView) mContentView.findViewById(R.id.txt_signature);
        mText_Signature.setOnClickListener(this);
        mContentView.findViewById(R.id.btn_picture).setOnClickListener(this);

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
//        String comments = mText_Comments.getText().toString();
//
//        if (comments.length()==0) {
//            showMessage(R.string.error__empty_comments);
//            return false;
//        }

        if (mPicture_Signature.mode == Constants.PICTURE_EMPTY) {
            showMessage(R.string.error__empty_signature);
            return false;
        }

        return refreshResult(true);
    }

    @Override
    public void saveForm() {
        String comments = mText_Comments.getText().toString();

        AppData.INSPECTION.overall_comments = comments;
        AppData.INSPECTION.signature.copy(mPicture_Signature);



        refreshResult(true);
    }

    @Override
    public void restoreForm() {
        mText_Comments.setText(AppData.INSPECTION.overall_comments);
        mText_Comments.setHorizontallyScrolling(false);
        mText_Comments.setMaxLines(Integer.MAX_VALUE);

        mPicture_Signature.copy(AppData.INSPECTION.signature);
        mText_Signature.setText(mPicture_Signature.getDisplayedText());

        mText_Qn.setText(AppData.INSPECTION.qn+"");


        refreshResult(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_signature:
            case R.id.btn_picture:
                showPictureMenu(10);
                break;
        }
    }

    private boolean refreshResult(boolean warning) {
        float pressure = AppData.INSPECTION.house_pressure ;
        float flow = AppData.INSPECTION.flow ;

        emptyResult(1);
        emptyResult(2);

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

        UnitInfo unit1 = AppData.getUnit(1);
        UnitInfo unit2 = AppData.getUnit(2);
        UnitInfo unit3 = AppData.getUnit(3);
        UnitInfo unit4 = AppData.getUnit(4);

        float u1 = (InspectionUtils.getUnitValue(unit1.i_supply) + InspectionUtils.getUnitValue(unit1.i_return)) * 1.0f / 2;
        float u2 = (InspectionUtils.getUnitValue(unit2.i_supply) + InspectionUtils.getUnitValue(unit2.i_return)) * 1.0f / 2;
        float u3 = (InspectionUtils.getUnitValue(unit3.i_supply) + InspectionUtils.getUnitValue(unit3.i_return)) * 1.0f / 2;
        float u4 = (InspectionUtils.getUnitValue(unit4.i_supply) + InspectionUtils.getUnitValue(unit4.i_return)) * 1.0f / 2;

        float qn_out = (u1 + u2 + u3 + u4) * 1.0f / AppData.INSPECTION.area;
        AppData.INSPECTION.qn_out = qn_out + "";
        mText_QnOut.setText(AppData.INSPECTION.qn_out);


        if (qn_out<=AppData.INSPECTION.qn) {
            AppData.INSPECTION.result_duct_leakage = 1;

            mResult_DuctLeakage.setText(AppData.INSPECTION.getResultDuctLeakageString());
            mResult_DuctLeakage.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_pass));
            mText_QnOut.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_pass));
        } else {
            AppData.INSPECTION.result_duct_leakage = 3;

            mResult_DuctLeakage.setText(AppData.INSPECTION.getResultDuctLeakageString());
            mResult_DuctLeakage.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_fail));
            mText_QnOut.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_fail));
        }

        double c = flow / Math.pow(pressure, 0.65f);
        double cfm = c * 12.7154;
        double ach50 = (cfm * 60) / AppData.INSPECTION.volume;

        AppData.INSPECTION.ach50 = String.format("%.4f", ach50);
        mText_Ach50.setText(AppData.INSPECTION.ach50);

        if (ach50>7) {
            AppData.INSPECTION.result_envelop_leakage = 3;

            mResult_EnvelopLeakage.setText(AppData.INSPECTION.getResultEnvelopLeakageString());
            mResult_EnvelopLeakage.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_fail));
            mText_Ach50.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_fail));
        } else if (ach50<=3) {
            AppData.INSPECTION.result_envelop_leakage = 2;

            mResult_EnvelopLeakage.setText(AppData.INSPECTION.getResultEnvelopLeakageString());
            mResult_EnvelopLeakage.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_verify));
            mText_Ach50.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_verify));

        } else {
            AppData.INSPECTION.result_envelop_leakage = 1;

            mResult_EnvelopLeakage.setText(AppData.INSPECTION.getResultEnvelopLeakageString());
            mResult_EnvelopLeakage.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_pass));
            mText_Ach50.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_pass));
        }

        return true;
    }

    private void emptyResult(int kind)  {
        if (kind==1) {
            mText_QnOut.setText("");
            mResult_DuctLeakage.setText("");

            AppData.INSPECTION.result_duct_leakage = 0;
        }

        if (kind==2) {
            mText_Ach50.setText("");
            mResult_EnvelopLeakage.setText("");

            AppData.INSPECTION.result_envelop_leakage = 0;
        }
    }

    private void initTakenPath() {
        String path = StorageUtils.getAppDirectory();
        if (path.length()>0) {
            String filename = CalenderUtils.getTodayWith14Chars() + "_" + UUID.randomUUID().toString().replaceAll("-", "") + ".jpg";
            AppData.TAKEN_PICTURE = path + "/" + filename;
        }
    }

    public void showPictureMenu(int kind) {
        AppData.TAKEN_KIND = kind;

        if (kind == 10) {
            if (mPicture_Signature.mode == Constants.PICTURE_EMPTY) {
                initTakenPath();
                SignatureDialog dialog = new SignatureDialog(getActivity(), this);
                dialog.show();
            } else {
                PicturePickerDialog dialog = new PicturePickerDialog(getActivity(), this, Constants.ACTION_DELETE);
                dialog.show();
            }
        }
    }


    @Override
    public void onCamera() {
        String path = StorageUtils.getAppDirectory();
        if (path.length()>0) {
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
        if (AppData.TAKEN_KIND == 10)
            dialog = new PicturePreviewDialog(getActivity(), mPicture_Signature);

        if (dialog!=null)
            dialog.show();
    }

    @Override
    public void onRemove() {
        if (AppData.TAKEN_KIND == 10) {
            mPicture_Signature.init(Constants.PICTURE_SIGNATURE);
            mText_Signature.setText(mPicture_Signature.getDisplayedText());
        }

        AppData.TAKEN_KIND = 0;
        AppData.TAKEN_PICTURE = "";
    }

    @Override
    public void onCapture() {
        String file = AppData.TAKEN_PICTURE;
        if (new File(file).exists()) {
            mPicture_Signature.mode = Constants.PICTURE_LOCAL;
            mPicture_Signature.image = file;
            mText_Signature.setText(mPicture_Signature.getDisplayedText());
        }

        AppData.TAKEN_KIND = 0;
        AppData.TAKEN_PICTURE = "";
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

        if (ACTIVITY_RESULT__CAMERA==requestCode) {
            if (uri == null && AppData.TAKEN_PICTURE.length() > 0) {
                uri = Uri.fromFile(new File(AppData.TAKEN_PICTURE));
            }

            if (uri != null) {
                takePicture(AppData.TAKEN_PICTURE);
            }
        }

        if (ACTIVITY_RESULT__GALLERY==requestCode) {
            if (uri != null) {
                String filePath = getFilePathFromActivityResultUri(uri);
                if (filePath != null && filePath.length() > 0) {
                    String path = StorageUtils.getAppDirectory() + CalenderUtils.getTodayWith14Chars() + "_" + UUID.randomUUID().toString().replaceAll("-", "") + ".jpg";
                    try {
                        copyFile(new File(filePath), new File(path));
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    takePicture(path);
                }
            }
        }

    }

    public void takePicture(String filepath) {
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
            String filepath = applyImageRotation(params[0]);
            filepath = scaleImage(filepath);




            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            AppData.TAKEN_KIND = 0;
            AppData.TAKEN_PICTURE = "";

            hideLoading();
        }
    }

}
