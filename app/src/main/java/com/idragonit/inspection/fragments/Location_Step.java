package com.idragonit.inspection.fragments;

 import android.content.Intent;
 import android.net.Uri;
 import android.os.AsyncTask;
 import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
 import android.util.Log;
 import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
 import com.idragonit.inspection.components.SpinnerAdapter;
import com.idragonit.inspection.models.CheckingInfo;
 import com.idragonit.inspection.models.PictureInfo;
 import com.idragonit.inspection.utils.CalenderUtils;
 import com.idragonit.inspection.utils.StorageUtils;

 import java.io.File;
 import java.util.UUID;

/**
 * Created by CJH on 2016.01.23.
 */
public class Location_Step extends BaseFragment implements View.OnClickListener,PicturePickerListener {

    LinearLayout mLayout_LocationContainer;
    Spinner mSpinner;
    SpinnerAdapter mAdapter;

    PictureInfo mPicture_RightBuilding = new PictureInfo();
    PictureInfo mPicture_LeftBuilding = new PictureInfo();
    PictureInfo mPicture_BackBuilding = new PictureInfo();;
    int pictureMode = 0;
    String item = "";


    public static Location_Step newInstance(Object... args) {
        return new Location_Step();
    }

    @Override
    public String getFragmentTag() {
        return Location_Step.class.getSimpleName();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        if (mBridge == null)
            return root;

        View mContentView = inflater.inflate(R.layout.step_location, container, false);
        mLayout_LocationContainer = (LinearLayout) mContentView.findViewById(R.id.layout_locations);
        mSpinner = (Spinner) mContentView.findViewById(R.id.spinner_location);
        mAdapter = new SpinnerAdapter(getActivity());
        mAdapter.add("");
        mAdapter.add("Front");
        mAdapter.add("Right");
        mAdapter.add("Back");
        mAdapter.add("Left");
        mSpinner.setAdapter(mAdapter);

        mContentView.findViewById(R.id.btn_add).setOnClickListener(this);

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
            case R.id.btn_add:
                onAdd();
                break;
        }
    }

    private void onAdd() {

        if (mSpinner.getSelectedItemPosition()==0)
            return;
        item = mSpinner.getSelectedItem().toString();
        Log.i("LocationStep", "Selected Location: >>>" + item);
        if(item.equalsIgnoreCase("right")){
            pictureMode = 1;
        }else if(item.equalsIgnoreCase("left")){
            pictureMode = 2;
        }else if(item.equalsIgnoreCase("back")){
            pictureMode = 3;
        }else{
            pictureMode = 4;
        }
        if (pictureMode == 4){
            goStep2();
        }else{
            showPictureMenu();
        }

    }

    private void addLocation(boolean isOmit, boolean isFront) {
        String location = mSpinner.getSelectedItem().toString();
        CheckingInfo item = new CheckingInfo();
        item.init(location, isOmit, isFront);

        prepareCheckingList(item);
        mBridge.switchTo(CheckList_Step.newInstance(), true);
    }

    private void addItemToContainer(final String location) {
        final View root = LayoutInflater.from(getActivity()).inflate(R.layout.item_field, null, false);
        final TextView txt_value = (TextView) root.findViewById(R.id.txt_value);
        final ImageView btn_remove = (ImageView) root.findViewById(R.id.btn_remove);
        root.setTag(location);

        txt_value.setText(location);
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View root = mLayout_LocationContainer.findViewWithTag(location);
                mLayout_LocationContainer.removeView(root);
                AppData.removeLocation(location);
                mAdapter.add(location);
            }
        });

        txt_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (CheckingInfo item : AppData.LOCATIONS) {
                    if (item.location.equals(location)) {
                        prepareCheckingList(item);
                        mBridge.switchTo(CheckList_Step.newInstance(), true);
                        break;
                    }
                }
            }
        });

        mLayout_LocationContainer.addView(root);
    }

    @Override
    public boolean validateForm() {
        if (AppData.LOCATIONS.size()==0) {
            showMessage(R.string.error__empty_location);
            return false;
        }

        return true;
    }

    @Override
    public void saveForm() {
        AppData.INSPECTION.right_building.copy(mPicture_RightBuilding);
        AppData.INSPECTION.left_building.copy(mPicture_LeftBuilding);
        AppData.INSPECTION.back_building.copy(mPicture_BackBuilding);
    }

    @Override
    public void restoreForm() {
        synchronized (AppData.SYNC) {
            for (CheckingInfo item : AppData.LOCATIONS) {
                String location = item.location.toLowerCase();
                addItemToContainer(item.location);
                mAdapter.remove(item.location);

            }
        }

        mPicture_RightBuilding.copy(AppData.INSPECTION.right_building);
        mPicture_LeftBuilding.copy(AppData.INSPECTION.left_building);
        mPicture_BackBuilding.copy(AppData.INSPECTION.back_building);

        mAdapter.notifyDataSetChanged();
    }

    public void prepareCheckingList(CheckingInfo item) {
        AppData.STORAGE.checking_list.init();
        AppData.STORAGE.checking_list.copy(item);
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
        switch (pictureMode){
            case 1:{
                // right
                PicturePreviewDialog dialog = new PicturePreviewDialog(getActivity(), mPicture_RightBuilding);
                dialog.show();
                break;
            }
            case 2:{
                // left
                PicturePreviewDialog dialog = new PicturePreviewDialog(getActivity(), mPicture_LeftBuilding);
                dialog.show();
                break;
            }
            case 3:{
                // back
                PicturePreviewDialog dialog = new PicturePreviewDialog(getActivity(), mPicture_BackBuilding);
                dialog.show();
                break;
            }
        }

    }

    public void showPictureMenu() {
        PictureInfo pictureInfo = null;
        switch (pictureMode){
            case 1:{
                // right
                pictureInfo = mPicture_RightBuilding;
                break;
            }
            case 2:{
                // left
                pictureInfo = mPicture_LeftBuilding;
                break;
            }
            case 3:{
                // back
                pictureInfo = mPicture_BackBuilding;
                break;
            }
        }
        PicturePickerDialog dialog;
        if (pictureInfo.mode == Constants.PICTURE_EMPTY) {
            dialog = new PicturePickerDialog(getActivity(), this, Constants.ACTION_NEW);
        } else {
            dialog = new PicturePickerDialog(getActivity(), this, Constants.ACTION_DELETE);
        }
        dialog.show();
    }

    @Override
    public void onRemove() {
        switch (pictureMode){
            case 1:{
                // right
                mPicture_RightBuilding.init();
                break;
            }
            case 2:{
                // left
                mPicture_LeftBuilding.init();
                break;
            }
            case 3:{
                // back
                mPicture_BackBuilding.init();
                break;
            }
        }
    }

    @Override
    public void onCapture() {

    }

    public class ImageProcessing extends AsyncTask<String, Void, Void> {

        int mode;

        public ImageProcessing(int mode){
            this.mode = mode;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading(Constants.MSG_LOADING);
        }

        @Override
        protected Void doInBackground(String... params) {
            String file = applyImageRotation(params[0]);
            file = scaleImage(file);

            switch (mode){
                case 1:{
                    // right
                    mPicture_RightBuilding.mode = Constants.PICTURE_LOCAL;
                    mPicture_RightBuilding.image = file;
                    break;
                }
                case 2:{
                    // left
                    mPicture_LeftBuilding.mode = Constants.PICTURE_LOCAL;
                    mPicture_LeftBuilding.image = file;
                    break;
                }
                case 3:{
                    // back
                    mPicture_BackBuilding.mode = Constants.PICTURE_LOCAL;
                    mPicture_BackBuilding.image = file;
                    break;
                }
            }


            AppData.TAKEN_PICTURE = "";
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideLoading();
            goStep2();
        }
    }

    private void goStep2(){
        saveForm();
        new AlertDialog.Builder(getActivity(), R.style.MyCustomDialog)
                .setCancelable(false)
                .setTitle(R.string.app_name)
                .setMessage("IS WOOD FRAMING PRESENT ON THIS SIDE OF THE BUILDING?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        addLocation(false, item.equalsIgnoreCase("front"));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        addLocation(true, item.equalsIgnoreCase("front"));
                    }
                })
                .show();
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
        new ImageProcessing(pictureMode).execute(filepath);
    }
}
