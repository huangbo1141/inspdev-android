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
import android.view.LayoutInflater;
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
import com.idragonit.inspection.components.PicturePickerDialog;
import com.idragonit.inspection.components.PicturePickerListener;
import com.idragonit.inspection.components.PicturePreviewDialog;
import com.idragonit.inspection.core.LocationTracker;
import com.idragonit.inspection.models.PictureInfo;
import com.idragonit.inspection.models.UnitInfo;
import com.idragonit.inspection.utils.CalenderUtils;
import com.idragonit.inspection.utils.StorageUtils;

import java.io.File;
import java.util.UUID;

/**
 * Created by CJH on 2016.01.23.
 */
public class UnitWCI_Step extends BaseFragment implements View.OnClickListener {

    EditText mText_Supply1, mText_Return1;
    EditText mText_Supply2, mText_Return2;
    EditText mText_Supply3, mText_Return3;
    EditText mText_Supply4, mText_Return4;

    public UnitWCI_Step() {
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

        if (i_supply.length()==0) {
            showMessage("Please Enter Unit 1 Supply");
            return false;
        }

        if (i_return.length()==0) {
            showMessage("Please Enter Unit 1 Return");
            return false;
        }

        return true;
    }

    @Override
    public void saveForm() {
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
            }

            if (msg.what == 100) {
            }
        }
    };

}
