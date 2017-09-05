package com.idragonit.inspection.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.idragonit.inspection.AppData;
import com.idragonit.inspection.BaseFragment;
import com.idragonit.inspection.R;

/**
 * Created by CJH on 2016.01.23.
 */
public class Initials_Step extends BaseFragment {

    EditText mText_Initials;

    public static Initials_Step newInstance(Object... args) {
        return new Initials_Step();
    }

    @Override
    public String getFragmentTag() {
        return Initials_Step.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        if (mBridge == null)
            return root;

        View mContentView = inflater.inflate(R.layout.step_initials, container, false);
        mText_Initials = (EditText) mContentView.findViewById(R.id.txt_inspector_initials);

        return mContentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public boolean validateForm() {
        String initials = mText_Initials.getText().toString();
        if (initials.length()==0) {
            showMessage(R.string.error__empty_initials);
            return false;
        }

        return true;
    }

    @Override
    public void restoreForm() {

    }

    @Override
    public void saveForm() {
        String initials = mText_Initials.getText().toString();
        AppData.INSPECTION.inspection_initials = initials;
    }
}
