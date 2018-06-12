package com.idragonit.inspection.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import com.idragonit.inspection.components.PicturePickerDialog;
import com.idragonit.inspection.components.PicturePickerListener;
import com.idragonit.inspection.components.PicturePreviewDialog;
import com.idragonit.inspection.components.SignatureDialog;
import com.idragonit.inspection.models.CheckingItemInfo;
import com.idragonit.inspection.models.PictureInfo;
import com.idragonit.inspection.utils.CalenderUtils;
import com.idragonit.inspection.utils.StorageUtils;
import com.idragonit.inspection.utils.Utils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.File;
import java.util.UUID;

/**
 * Created by CJH on 2016.01.23.
 */
public class Result_Step extends BaseFragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, PicturePickerListener {

    EditText mText_Comments;
    Spinner mSpinner_Result;
    TextView mText_InspectionDate, mText_Signature;

    PictureInfo mPicture_Signature;
    PictureInfo mPicture_Exception_1, mPicture_Exception_2, mPicture_Exception_3, mPicture_Exception_4;

    TextView mText_Exception_1, mText_Exception_2, mText_Exception_3, mText_Exception_4;

    LinearLayout mLayout_ExceptionPicture;
    LinearLayout mList_Comment;

    ScrollView mScrollView;

    int mIndex;

    public Result_Step() {
        mPicture_Signature = new PictureInfo(Constants.PICTURE_SIGNATURE);
        mPicture_Exception_1 = new PictureInfo();
        mPicture_Exception_2 = new PictureInfo();
        mPicture_Exception_3 = new PictureInfo();
        mPicture_Exception_4 = new PictureInfo();

        mIndex = 0;

        AppData.TAKEN_KIND = 0;
        AppData.TAKEN_PICTURE = "";
    }

    public static Result_Step newInstance(Object... args) {
        Result_Step fragment = new Result_Step();

        if (args!=null && args.length>0) {
            fragment.mIndex = Utils.checkNull(args[0], 0);
        }

        return fragment;
    }

    @Override
    public String getFragmentTag() {
        return Result_Step.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        if (mBridge == null)
            return root;

        View mContentView = inflater.inflate(R.layout.step_result, container, false);
        mScrollView = (ScrollView) mContentView;

        mText_Comments = (EditText) mContentView.findViewById(R.id.txt_comments);
        mText_InspectionDate = (TextView) mContentView.findViewById(R.id.txt_inspection_date);
        mText_Signature = (TextView) mContentView.findViewById(R.id.txt_signature);

        mList_Comment = (LinearLayout) mContentView.findViewById(R.id.comment_list);

        mLayout_ExceptionPicture = (LinearLayout) mContentView.findViewById(R.id.layout_exception_picture);

        mSpinner_Result = (Spinner) mContentView.findViewById(R.id.spinner_result);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.result_list, R.layout.layout_spinner);
        mSpinner_Result.setAdapter(adapter);
        mSpinner_Result.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 2) {
                    mLayout_ExceptionPicture.setVisibility(View.VISIBLE);
                } else {
                    mLayout_ExceptionPicture.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mText_InspectionDate.setOnClickListener(this);

        mText_Signature.setOnClickListener(this);
        mContentView.findViewById(R.id.btn_picture).setOnClickListener(this);

        mText_Exception_1 = (TextView) mContentView.findViewById(R.id.txt_1_picture);
        mText_Exception_2 = (TextView) mContentView.findViewById(R.id.txt_2_picture);
        mText_Exception_3 = (TextView) mContentView.findViewById(R.id.txt_3_picture);
        mText_Exception_4 = (TextView) mContentView.findViewById(R.id.txt_4_picture);

        mContentView.findViewById(R.id.btn_1_picture).setOnClickListener(this);
        mContentView.findViewById(R.id.btn_2_picture).setOnClickListener(this);
        mContentView.findViewById(R.id.btn_3_picture).setOnClickListener(this);
        mContentView.findViewById(R.id.btn_4_picture).setOnClickListener(this);

        mText_Exception_1.setOnClickListener(this);
        mText_Exception_2.setOnClickListener(this);
        mText_Exception_3.setOnClickListener(this);
        mText_Exception_4.setOnClickListener(this);

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
        String comments = mText_Comments.getText().toString();

        if (comments.length()==0) {
            showMessage(R.string.error__empty_comments);
            return false;
        }

        if (mSpinner_Result.getSelectedItemPosition() == 0) {
            showMessage(R.string.error__empty_result);
            return false;
        }

        if (mPicture_Signature.mode == Constants.PICTURE_EMPTY) {
            showMessage(R.string.error__empty_signature);
            return false;
        }

        return true;
    }

    @Override
    public void saveForm() {
        String comments = mText_Comments.getText().toString();
        String inspection_date = mText_InspectionDate.getText().toString();

        AppData.INSPECTION.overall_comments = comments;
        AppData.INSPECTION.inspection_date_last = inspection_date;
        AppData.INSPECTION.result = mSpinner_Result.getSelectedItemPosition();

        AppData.INSPECTION.signature.copy(mPicture_Signature);
        AppData.INSPECTION.exception_1.copy(mPicture_Exception_1);
        AppData.INSPECTION.exception_2.copy(mPicture_Exception_2);
        AppData.INSPECTION.exception_3.copy(mPicture_Exception_3);
        AppData.INSPECTION.exception_4.copy(mPicture_Exception_4);
    }

    @Override
    public void restoreForm() {
        int y = 0;

        mText_Comments.setText(AppData.INSPECTION.overall_comments);

        mText_Comments.setHorizontallyScrolling(false);
        mText_Comments.setMaxLines(Integer.MAX_VALUE);

        mText_InspectionDate.setText(AppData.INSPECTION.inspection_date_last);
        mSpinner_Result.setSelection(AppData.INSPECTION.result);

        mPicture_Signature.copy(AppData.INSPECTION.signature);
        mText_Signature.setText(mPicture_Signature.getDisplayedText());

        mPicture_Exception_1.copy(AppData.INSPECTION.exception_1);
        mText_Exception_1.setText(mPicture_Exception_1.getDisplayedText());

        mPicture_Exception_2.copy(AppData.INSPECTION.exception_2);
        mText_Exception_2.setText(mPicture_Exception_2.getDisplayedText());

        mPicture_Exception_3.copy(AppData.INSPECTION.exception_3);
        mText_Exception_3.setText(mPicture_Exception_3.getDisplayedText());

        mPicture_Exception_4.copy(AppData.INSPECTION.exception_4);
        mText_Exception_4.setText(mPicture_Exception_4.getDisplayedText());

        for (int i=0; i<AppData.COMMENT.checking_list.size(); i++) {
            final CheckingItemInfo item = AppData.COMMENT.checking_list.get(i);
            final int position = i;

            final View convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_checking, null);
            if (convertView!=null) {
                final TextView title = (TextView) convertView.findViewById(R.id.txt_title);
                final TextView status = (TextView) convertView.findViewById(R.id.txt_status);
                final CheckBox submit = (CheckBox) convertView.findViewById(R.id.chk_submit);

                status.setVisibility(View.GONE);
                submit.setVisibility(View.VISIBLE);

                final View divider1 = convertView.findViewById(R.id.layout_divider1);
                final View divider2 = convertView.findViewById(R.id.layout_divider2);
                if (i==0) {
                    divider1.setVisibility(View.VISIBLE);
                }
                divider2.setVisibility(View.VISIBLE);

                title.setText(item.title);

                submit.setChecked(item.is_submit);

                refreshComment(convertView, status, item);

                submit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        item.is_submit = isChecked;
                        refreshComment(convertView, status, item);

                        setCommentData(position, item);
                    }
                });

//                convertView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (!item.is_submit)
//                            return;
//
//                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity(), R.style.MyCustomDialog);
//                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner);
//                        for (int i = 0; i < Constants.CHECKING_STATUS.length; i++)
//                            arrayAdapter.add(Constants.CHECKING_STATUS[i]);
//
//                        builderSingle.setAdapter(
//                                arrayAdapter,
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        if (which == Constants.CHECKING_STATUS__FAIL || which == Constants.CHECKING_STATUS__NOT_READY) {
//                                            AppData.RESULT_SCROLL_POSITION = mScrollView.getScrollY();
//                                            mBridge.switchTo(CommentPoint_Step.newInstance(position, item, Constants.CHECKING_STATUS__NONE + which), true);
//                                            return;
//                                        }
//
//                                        item.status = which;
//                                        refreshComment(convertView, status, item);
//
//                                        setCommentData(position, item);
//                                    }
//                                });
//                        builderSingle.setCancelable(true);
//                        builderSingle.show();
//                    }
//                });

                mList_Comment.addView(convertView);
            }
        }

        if (mIndex>0) {
            mIndex = 0;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScrollView.smoothScrollTo(0, AppData.RESULT_SCROLL_POSITION);
                    AppData.RESULT_SCROLL_POSITION = 0;
                }
            }, 100);
        }
    }

    private void refreshComment(View convertView, TextView status, CheckingItemInfo item) {
        status.setText("Status :   " + Constants.CHECKING_STATUS[item.status]);

        if (!item.is_submit)
            convertView.setBackgroundResource(R.color.disabled);
//        else if (item.status == Constants.CHECKING_STATUS__NONE)
//            convertView.setBackgroundResource(R.color.background);
        else
            convertView.setBackgroundResource(R.color.highlight);

//        status.setTextColor(ContextCompat.getColor(getActivity(), R.color.primary));
//        if (item.status == Constants.CHECKING_STATUS__PASS)
//            status.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_pass));
//        if (item.status == Constants.CHECKING_STATUS__FAIL)
//            status.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_fail));
//        if (item.status == Constants.CHECKING_STATUS__NOT_READY)
//            status.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_ready));
//        if (item.status == Constants.CHECKING_STATUS__NOT_APPLICABLE)
//            status.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_applicable));
//        if (item.status == Constants.CHECKING_STATUS__CANNOT_VERIFY)
//            status.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_verify));

    }

    private void setCommentData(int position, CheckingItemInfo item) {
        try {
            CheckingItemInfo comment = new CheckingItemInfo();
            comment.copy(item);
            AppData.COMMENT.checking_list.set(position, comment);
        } catch (Exception e) {}
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_inspection_date:
//                showCalendar();
                break;

            case R.id.txt_signature:
            case R.id.btn_picture:
                showPictureMenu(10);
                break;

            case R.id.txt_1_picture:
            case R.id.btn_1_picture:
                showPictureMenu(1);
                break;

            case R.id.txt_2_picture:
            case R.id.btn_2_picture:
                showPictureMenu(2);
                break;

            case R.id.txt_3_picture:
            case R.id.btn_3_picture:
                showPictureMenu(3);
                break;

            case R.id.txt_4_picture:
            case R.id.btn_4_picture:
                showPictureMenu(4);
                break;
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int month, int day) {
        Log.e("onDateSet", year + "/" + month + "/" + day);
        String date = Utils.fillZero(String.valueOf(month+1), 2) + "/" + Utils.fillZero(String.valueOf(day), 2) + "/" + Utils.fillZero(String.valueOf(year), 4);
        mText_InspectionDate.setText(date);
    }

    private void showCalendar() {
        String date = mText_InspectionDate.getText().toString();
        final int year = Utils.getInt(date.substring(6, 10));
        final int month = Utils.getInt(date.substring(0, 2));
        final int day = Utils.getInt(date.substring(3, 5));

        DatePickerDialog dialog = DatePickerDialog.newInstance(this, year, month-1, day);
        dialog.show(getActivity().getFragmentManager(), "Datepickerdialog");
    }

    private void initTakenPath() {
        String path = StorageUtils.getAppDirectory();
        if (path.length()>0) {
            String filename = CalenderUtils.getTodayWith14Chars() + "_" + UUID.randomUUID().toString().replaceAll("-", "") + ".jpg";
            if (path.endsWith("/")){
                AppData.TAKEN_PICTURE = path + filename;
            }else{
                AppData.TAKEN_PICTURE = path + "/" + filename;
            }
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

        if (kind == 1) {
            if (mPicture_Exception_1.mode == Constants.PICTURE_EMPTY) {
                PicturePickerDialog dialog = new PicturePickerDialog(getActivity(), this, Constants.ACTION_NEW);
                dialog.show();
            } else {
                PicturePickerDialog dialog = new PicturePickerDialog(getActivity(), this, Constants.ACTION_DELETE);
                dialog.show();
            }
        }

        if (kind == 2) {
            if (mPicture_Exception_2.mode == Constants.PICTURE_EMPTY) {
                PicturePickerDialog dialog = new PicturePickerDialog(getActivity(), this, Constants.ACTION_NEW);
                dialog.show();
            } else {
                PicturePickerDialog dialog = new PicturePickerDialog(getActivity(), this, Constants.ACTION_DELETE);
                dialog.show();
            }
        }

        if (kind == 3) {
            if (mPicture_Exception_3.mode == Constants.PICTURE_EMPTY) {
                PicturePickerDialog dialog = new PicturePickerDialog(getActivity(), this, Constants.ACTION_NEW);
                dialog.show();
            } else {
                PicturePickerDialog dialog = new PicturePickerDialog(getActivity(), this, Constants.ACTION_DELETE);
                dialog.show();
            }
        }

        if (kind == 4) {
            if (mPicture_Exception_4.mode == Constants.PICTURE_EMPTY) {
                PicturePickerDialog dialog = new PicturePickerDialog(getActivity(), this, Constants.ACTION_NEW);
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
        PicturePreviewDialog dialog = null;
        if (AppData.TAKEN_KIND == 10)
            dialog = new PicturePreviewDialog(getActivity(), mPicture_Signature);

        if (AppData.TAKEN_KIND == 1)
            dialog = new PicturePreviewDialog(getActivity(), mPicture_Exception_1);
        if (AppData.TAKEN_KIND == 2)
            dialog = new PicturePreviewDialog(getActivity(), mPicture_Exception_2);
        if (AppData.TAKEN_KIND == 3)
            dialog = new PicturePreviewDialog(getActivity(), mPicture_Exception_3);
        if (AppData.TAKEN_KIND == 4)
            dialog = new PicturePreviewDialog(getActivity(), mPicture_Exception_4);

        if (dialog!=null)
            dialog.show();
    }

    @Override
    public void onRemove() {
        if (AppData.TAKEN_KIND == 10) {
            mPicture_Signature.init(Constants.PICTURE_SIGNATURE);
            mText_Signature.setText(mPicture_Signature.getDisplayedText());
        }

        if (AppData.TAKEN_KIND == 1) {
            mPicture_Exception_1.init();
            mText_Exception_1.setText(mPicture_Exception_1.getDisplayedText());
        }

        if (AppData.TAKEN_KIND == 2) {
            mPicture_Exception_2.init();
            mText_Exception_2.setText(mPicture_Exception_2.getDisplayedText());
        }

        if (AppData.TAKEN_KIND == 3) {
            mPicture_Exception_3.init();
            mText_Exception_3.setText(mPicture_Exception_3.getDisplayedText());
        }

        if (AppData.TAKEN_KIND == 4) {
            mPicture_Exception_4.init();
            mText_Exception_4.setText(mPicture_Exception_4.getDisplayedText());
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

            if (AppData.TAKEN_KIND == 1) {
                mPicture_Exception_1.mode = Constants.PICTURE_LOCAL;
                mPicture_Exception_1.image = filepath;
            }

            if (AppData.TAKEN_KIND == 2) {
                mPicture_Exception_2.mode = Constants.PICTURE_LOCAL;
                mPicture_Exception_2.image = filepath;
            }

            if (AppData.TAKEN_KIND == 3) {
                mPicture_Exception_3.mode = Constants.PICTURE_LOCAL;
                mPicture_Exception_3.image = filepath;
            }

            if (AppData.TAKEN_KIND == 4) {
                mPicture_Exception_4.mode = Constants.PICTURE_LOCAL;
                mPicture_Exception_4.image = filepath;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (AppData.TAKEN_KIND == 1) {
                mText_Exception_1.setText(mPicture_Exception_1.getDisplayedText());
            }

            if (AppData.TAKEN_KIND == 2) {
                mText_Exception_2.setText(mPicture_Exception_2.getDisplayedText());
            }

            if (AppData.TAKEN_KIND == 3) {
                mText_Exception_3.setText(mPicture_Exception_3.getDisplayedText());
            }

            if (AppData.TAKEN_KIND == 4) {
                mText_Exception_4.setText(mPicture_Exception_4.getDisplayedText());
            }

            AppData.TAKEN_KIND = 0;
            AppData.TAKEN_PICTURE = "";

            hideLoading();
        }
    }

}
