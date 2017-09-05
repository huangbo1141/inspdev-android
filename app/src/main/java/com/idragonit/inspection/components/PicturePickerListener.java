package com.idragonit.inspection.components;


/**
 * Created by CJH on 2015-06-28.
 */

public interface PicturePickerListener {
    abstract void onCamera();
    abstract void onGallery();
    abstract void onView();
    abstract void onRemove();
    abstract void onCapture();
}
