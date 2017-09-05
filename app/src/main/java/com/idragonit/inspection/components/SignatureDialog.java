package com.idragonit.inspection.components;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.idragonit.inspection.AppData;
import com.idragonit.inspection.R;

import java.io.FileOutputStream;

/**
 * Created by CJH on 2015-06-29.
 */
public class SignatureDialog extends Dialog implements View.OnClickListener {

    Context mContext;

    PicturePickerListener mListener;
    SignaturePad mSignature;

    public SignatureDialog(Context context, PicturePickerListener listener) {
        super(context, android.R.style.Theme_Holo_Light_Dialog);

        mContext = context;
        mListener = listener;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_signature);

        setTitle("");
        setCancelable(true);
        setCanceledOnTouchOutside(false);

        mSignature = (SignaturePad) findViewById(R.id.signature);

        findViewById(R.id.btn_done).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_done:
                done();
                break;

            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }

    public void done() {
        if (mSignature.isEmpty()) {
            Toast.makeText(mContext, R.string.error__empty_signature, Toast.LENGTH_SHORT).show();
            return;
        }

        if (save()) {
            mListener.onCapture();
        } else {
            Toast.makeText(mContext, "Failed to save signature!", Toast.LENGTH_SHORT).show();
        }

        dismiss();
    }

    public boolean save() {
        Bitmap bitmap = mSignature.getTransparentSignatureBitmap();
        bitmap = Bitmap.createScaledBitmap(bitmap, 200 * bitmap.getWidth() / bitmap.getHeight(), 200, false);
        if (bitmap!=null) {
            try {
                FileOutputStream out = new FileOutputStream(AppData.TAKEN_PICTURE);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
                return true;
            } catch (Exception e){}
        }

        return false;
    }
}
