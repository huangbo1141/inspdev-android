package com.idragonit.inspection.components;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;

import com.idragonit.inspection.Constants;
import com.idragonit.inspection.R;

/**
 * Created by CJH on 2015-06-29.
 */
public class PicturePickerDialog extends Dialog implements View.OnClickListener{

    int mKind;
    Context mContext;
    PicturePickerListener mListener;

    public PicturePickerDialog(Context context, PicturePickerListener listener, int kind) {
        super(context, android.R.style.Theme_Holo_Light_Dialog);

        mContext = context;
        mListener = listener;
        mKind = kind;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_picture);

        setTitle("");
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        if (mKind == Constants.ACTION_NEW) {
            findViewById(R.id.btn_camera).setOnClickListener(this);
            findViewById(R.id.btn_gallery).setOnClickListener(this);

            findViewById(R.id.btn_view).setVisibility(View.GONE);
            findViewById(R.id.btn_remove).setVisibility(View.GONE);
        }

        if (mKind == Constants.ACTION_DELETE) {
            findViewById(R.id.btn_view).setOnClickListener(this);
            findViewById(R.id.btn_remove).setOnClickListener(this);

            findViewById(R.id.btn_camera).setVisibility(View.GONE);
            findViewById(R.id.btn_gallery).setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {
        if (mListener==null)
            return;

        switch (v.getId()){
            case R.id.btn_camera:
                mListener.onCamera();
                dismiss();
                break;

            case R.id.btn_gallery:
                mListener.onGallery();
                dismiss();
                break;

            case R.id.btn_view:
                mListener.onView();
                dismiss();
                break;

            case R.id.btn_remove:
                mListener.onRemove();
                dismiss();
                break;
        }
    }
}
