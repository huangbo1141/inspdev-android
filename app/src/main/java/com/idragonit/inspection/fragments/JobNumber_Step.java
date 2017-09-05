package com.idragonit.inspection.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.idragonit.inspection.AppData;
import com.idragonit.inspection.BaseFragment;
import com.idragonit.inspection.R;
import com.idragonit.inspection.components.MaskedEditText;

/**
 * Created by CJH on 2016.01.23.
 */
public class JobNumber_Step extends BaseFragment {

    TextView mText_JobNumber;
    final String format = "####-###-##";

    public static JobNumber_Step newInstance(Object... args) {
        return new JobNumber_Step();
    }

    @Override
    public String getFragmentTag() {
        return JobNumber_Step.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        if (mBridge == null)
            return root;

        View mContentView = inflater.inflate(R.layout.step_jobnumber, container, false);
        mText_JobNumber = (TextView) mContentView.findViewById(R.id.txt_job_number);
//        mText_JobNumber.setMask(format);
//        mText_JobNumber.setPlaceholder('#');

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
        String job_number = mText_JobNumber.getText().toString();
        if (job_number.length()==0) {
            showMessage(R.string.error__empty_job_number);
            return false;
        }

        if (job_number.length()!=11 || job_number.indexOf("#")>-1) {
            showMessage(R.string.error__wrong_job_number);
            return false;
        }

        return true;
    }

    @Override
    public void restoreForm() {
        mText_JobNumber.setText(AppData.INSPECTION.job_number);
    }

    @Override
    public void saveForm() {
        String job_number = mText_JobNumber.getText().toString();
        AppData.INSPECTION.job_number = job_number;
        AppData.INSPECTION.lot = job_number.substring(5, 8);
        AppData.INSPECTION.community = job_number.substring(0, 4);
    }
}
