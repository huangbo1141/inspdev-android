package com.idragonit.inspection.fragments;

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
import com.idragonit.inspection.R;
import com.idragonit.inspection.components.SpinnerAdapter;
import com.idragonit.inspection.models.CheckingInfo;

/**
 * Created by CJH on 2016.01.23.
 */
public class Location_Step extends BaseFragment implements View.OnClickListener{

    LinearLayout mLayout_LocationContainer;
    Spinner mSpinner;
    SpinnerAdapter mAdapter;

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

        final String item = mSpinner.getSelectedItem().toString();
        Log.i("LocationStep", "Selected Location: >>>" + item);

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
    }

    @Override
    public void restoreForm() {
        synchronized (AppData.SYNC) {
            for (CheckingInfo item : AppData.LOCATIONS) {
                addItemToContainer(item.location);
                mAdapter.remove(item.location);
            }
        }

        mAdapter.notifyDataSetChanged();
    }

    public void prepareCheckingList(CheckingInfo item) {
        AppData.STORAGE.checking_list.init();
        AppData.STORAGE.checking_list.copy(item);
    }

}
