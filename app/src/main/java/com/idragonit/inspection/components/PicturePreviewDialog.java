package com.idragonit.inspection.components;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.Window;

import com.idragonit.inspection.R;
import com.idragonit.inspection.models.PictureInfo;
import com.idragonit.inspection.utils.ImageUtils;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by CJH on 2015-06-29.
 */
public class PicturePreviewDialog extends Dialog {

    Context mContext;
    PictureInfo mPicture;

    PhotoView photoView;

    public PicturePreviewDialog(Context context, PictureInfo picture) {
        super(context, android.R.style.Theme_Holo_Light_Dialog);

        mContext = context;
        mPicture = picture;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_preview);

        setTitle("");
        setCancelable(true);
        setCanceledOnTouchOutside(false);

        photoView = (PhotoView) findViewById(R.id.image);
        Picasso.with(mContext).load(ImageUtils.url_encode(mPicture.getImageUri())).placeholder(R.drawable.ic_loading).error(R.drawable.ic_error).into(photoView);
    }

}
