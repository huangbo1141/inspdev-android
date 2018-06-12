package com.idragonit.inspection.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
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
import com.idragonit.inspection.R;
import com.idragonit.inspection.components.PicturePickerDialog;
import com.idragonit.inspection.components.PicturePickerListener;
import com.idragonit.inspection.components.PicturePreviewDialog;
import com.idragonit.inspection.models.CheckingItemInfo;
import com.idragonit.inspection.utils.CalenderUtils;
import com.idragonit.inspection.utils.StorageUtils;
import com.idragonit.inspection.utils.Utils;

import java.io.File;
import java.util.UUID;

/**
 * Created by CJH on 2016.01.23.
 */
public class CheckPoint_Step extends BaseFragment implements View.OnClickListener, PicturePickerListener {

    TextView mText_Title;
    Spinner mSpinner;

    CheckingItemInfo mData;
    public int mIndex;
    public int mStatus = -1;

    TextView mText_PrimaryPicture, mText_SecondaryPicture;
    EditText mText_Comment;

    LinearLayout mLayout_Picture, mLayout_Comment;

    public CheckPoint_Step() {
        mIndex = -1;
        mData = new CheckingItemInfo();

        AppData.TAKEN_KIND = 0;
        AppData.TAKEN_PICTURE = "";
    }

    public static CheckPoint_Step newInstance(Object... args) {
        CheckPoint_Step fragment =  new CheckPoint_Step();
        if (args!=null && args.length>1) {
            fragment.mIndex = Utils.checkNull(args[0], 0);
            fragment.mData.copy((CheckingItemInfo) args[1]);
            // Added by BongBong 2016.0407
            fragment.mStatus = Utils.checkNull(args[2], -1);
        }
        return fragment;
    }

    @Override
    public String getFragmentTag() {
        return CheckPoint_Step.class.getSimpleName();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        if (mBridge == null)
            return root;

        View mContentView = inflater.inflate(R.layout.step_checkpoint, container, false);
        mText_Title = (TextView) mContentView.findViewById(R.id.txt_checking_title);
        mText_Title.setText(mData.title);

        mLayout_Picture = (LinearLayout) mContentView.findViewById(R.id.layout_picture);
        mLayout_Comment = (LinearLayout) mContentView.findViewById(R.id.layout_comment);

        mText_Comment = (EditText) mContentView.findViewById(R.id.txt_comment);
        mText_PrimaryPicture = (TextView) mContentView.findViewById(R.id.txt_primary_picture);
        mText_SecondaryPicture = (TextView) mContentView.findViewById(R.id.txt_secondary_picture);

        mSpinner = (Spinner) mContentView.findViewById(R.id.spinner_status);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.status_list, R.layout.layout_spinner);
        mSpinner.setAdapter(adapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshLayout(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                refreshLayout(0);
            }
        });

        mText_PrimaryPicture.setOnClickListener(this);
        mContentView.findViewById(R.id.btn_primary_picture).setOnClickListener(this);

        mText_SecondaryPicture.setOnClickListener(this);
        mContentView.findViewById(R.id.btn_secondary_picture).setOnClickListener(this);

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_primary_picture:
            case R.id.btn_primary_picture:
                showPictureMenu(1);
                break;

            case R.id.txt_secondary_picture:
            case R.id.btn_secondary_picture:
                showPictureMenu(2);
                break;
        }
    }

    @Override
    public boolean validateForm() {
        int status = mSpinner.getSelectedItemPosition();
        if (status == Constants.CHECKING_STATUS__FAIL) {
            if (mData.primary.mode == Constants.PICTURE_EMPTY) {
                showMessage(R.string.error__empty_photo);
                return false;
            }
        }

        if (status == Constants.CHECKING_STATUS__NOT_READY) {
            String comment = mText_Comment.getText().toString();
            if (comment.length()==0) {
                showMessage(R.string.error__empty_comment);
                return false;
            }
        }

        return true;
    }

    @Override
    public void saveForm() {
        if (mIndex>-1) {
            mData.status = mSpinner.getSelectedItemPosition();
            mData.comment = mText_Comment.getText().toString();

            CheckingItemInfo item = new CheckingItemInfo();
            item.copy(mData);
            AppData.STORAGE.checking_list.checking_list.set(mIndex, item);
        }
    }

    @Override
    public void restoreForm() {
        if (mData==null)
            return;

        // Modified by BongBong 2016.0407
        if (mStatus >= Constants.CHECKING_STATUS__NONE)
            mSpinner.setSelection(mStatus);
        else
            mSpinner.setSelection(mData.status);

        mText_Comment.setText(mData.comment);
        mText_PrimaryPicture.setText(mData.primary.getDisplayedText());
        mText_SecondaryPicture.setText(mData.secondary.getDisplayedText());
    }

    public void refreshLayout(int status) {
        mLayout_Picture.setVisibility(View.GONE);
        mLayout_Comment.setVisibility(View.GONE);

        switch (status) {
            case Constants.CHECKING_STATUS__FAIL:
                mLayout_Comment.setVisibility(View.VISIBLE);
                mLayout_Picture.setVisibility(View.VISIBLE);
                break;

            case Constants.CHECKING_STATUS__NOT_READY:
                mLayout_Comment.setVisibility(View.VISIBLE);
                break;

            default:
//                if (status!=Constants.CHECKING_STATUS__NONE) {
//                    saveForm();
//                    mBridge.switchTo(CheckList_Step.newInstance(mIndex), true);
//                }
                break;
        }
    }


    @Override
    public void onCamera() {
        String path = StorageUtils.getAppDirectory();
        if (path.length()>0) {
            String filename = CalenderUtils.getTodayWith14Chars() + "_" + UUID.randomUUID().toString().replaceAll("-", "") + ".jpg";
            if (path.endsWith("/")){
                AppData.TAKEN_PICTURE = path + filename;
            }else{
                AppData.TAKEN_PICTURE = path + "/" + filename;
            }
            takePictureFromCamera(ACTIVITY_RESULT__CAMERA);
        }
    }

    @Override
    public void onGallery() {
        takePictureFromGallery(ACTIVITY_RESULT__GALLERY);
    }

    @Override
    public void onView() {
        if (AppData.TAKEN_KIND == 1) {
            PicturePreviewDialog dialog = new PicturePreviewDialog(getActivity(), mData.primary);
            dialog.show();
        }

        if (AppData.TAKEN_KIND == 2) {
            PicturePreviewDialog dialog = new PicturePreviewDialog(getActivity(), mData.secondary);
            dialog.show();
        }
    }

    @Override
    public void onRemove() {
        if (AppData.TAKEN_KIND==1) {
            mData.primary.init();
            mText_PrimaryPicture.setText(mData.primary.getDisplayedText());
        }

        if (AppData.TAKEN_KIND==2) {
            mData.secondary.init();
            mText_SecondaryPicture.setText(mData.secondary.getDisplayedText());
        }
    }

    @Override
    public void onCapture() {

    }

    public void showPictureMenu(int kind) {
        AppData.TAKEN_KIND = kind;
        PicturePickerDialog dialog;

        if (kind == 1) {
            if (mData.primary.mode == Constants.PICTURE_EMPTY) {
                dialog = new PicturePickerDialog(getActivity(), this, Constants.ACTION_NEW);
            } else {
                dialog = new PicturePickerDialog(getActivity(), this, Constants.ACTION_DELETE);
            }
            dialog.show();
        }

        if (kind == 2) {
            if (mData.secondary.mode == Constants.PICTURE_EMPTY) {
                dialog = new PicturePickerDialog(getActivity(), this, Constants.ACTION_NEW);
            } else {
                dialog = new PicturePickerDialog(getActivity(), this, Constants.ACTION_DELETE);
            }
            dialog.show();
        }
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
                    takePicture(filePath);
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

            if (AppData.TAKEN_KIND == 1) {
                mData.primary.mode = Constants.PICTURE_LOCAL;
                mData.primary.image = filepath;
            }

            if (AppData.TAKEN_KIND == 2) {
                mData.secondary.mode = Constants.PICTURE_LOCAL;
                mData.secondary.image = filepath;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (AppData.TAKEN_KIND == 1) {
                mText_PrimaryPicture.setText(mData.primary.getDisplayedText());
            }

            if (AppData.TAKEN_KIND == 2) {
                mText_SecondaryPicture.setText(mData.secondary.getDisplayedText());
            }

            AppData.TAKEN_KIND = 0;
            AppData.TAKEN_PICTURE = "";

            hideLoading();
        }
    }

}
