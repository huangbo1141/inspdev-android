package com.idragonit.inspection.fragments;

 import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.idragonit.inspection.AppData;
import com.idragonit.inspection.BaseFragment;
import com.idragonit.inspection.Constants;
import com.idragonit.inspection.InspectionDatabase;
import com.idragonit.inspection.MainActivity;
import com.idragonit.inspection.R;
import com.idragonit.inspection.components.SyncAdapter;
import com.idragonit.inspection.components.InspectionAdapterListener;
 import com.idragonit.inspection.models.RequestedInspectionInfo;
 import com.idragonit.inspection.models.SyncInfo;
import com.idragonit.inspection.utils.Utils;

import java.util.ArrayList;

/**
 * Created by CJH on 2016.01.23.
 */
public class Sync extends BaseFragment implements InspectionAdapterListener {

    ListView mListView;
    SyncAdapter mAdapter;

    public static Sync newInstance(Object... args) {
        return new Sync();
    }

    @Override
    public String getFragmentTag() {
        return Sync.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        if (mBridge == null)
            return root;

        View mContentView = inflater.inflate(R.layout.step_sync, container, false);
        mListView = (ListView) mContentView.findViewById(R.id.listview);
        mAdapter = new SyncAdapter(getActivity(), this);
        mListView.setAdapter(mAdapter);

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
        return true;
    }

    @Override
    public void restoreForm() {
        mAdapter.clear();

        InspectionDatabase db = InspectionDatabase.getInstance(getActivity());

        if (!db.hasData()) {
            gotoMain();

        } else {
            ArrayList<SyncInfo> list = db.loadInspection(Utils.checkNull(AppData.USER.id, 0));
            for (SyncInfo item : list) {
                mAdapter.add(item);
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void saveForm() {
    }

    @Override
    public void onSubmit(SyncInfo item) {
        AppData.init(Constants.MODE_SYNC);

        AppData.KIND = item.type;
        AppData.INSPECTION.job_number = item.job_number;
        AppData.INSPECTION_ID_LOCAL = item.inspection_id;

        AppData.INSPECTION_REQUESTED_ID = item.requested_inspection_id;
        AppData.INSPECTION_EDIT_ID = item.edit_inspection_id;

        AppData.setInspection(item.data);

        if (AppData.KIND == Constants.INSPECTION_WCI || AppData.KIND == Constants.INSPECTION_PULTE_DUCT) {
            AppData.initUnit();
            AppData.setUnit(item.unit);

            mBridge.switchTo(BasicWCI_Step.newInstance(), true);
        } else {
            AppData.INSPECTION.lot = item.job_number.substring(5, 8);
            AppData.INSPECTION.community = item.job_number.substring(0, 4);

            AppData.initComment();

            AppData.setEmail(item.email);
            AppData.setLocation("Left", item.left);
            AppData.setLocation("Right", item.right);
            AppData.setLocation("Front", item.front);
            AppData.setLocation("Back", item.back);

            AppData.setComment(item.comment);

            mBridge.switchTo(Basic_Step.newInstance(), true);
        }
    }

    @Override
    public void onDelete(SyncInfo item) {
        showConfirmDialog(item.inspection_id);
    }

    @Override
    public void onSubmit(RequestedInspectionInfo item) {

    }

    private void showConfirmDialog(final int inspection_id) {
        new AlertDialog.Builder(getActivity(), R.style.MyCustomDialog)
                .setCancelable(false)
                .setTitle(R.string.app_name)
                .setMessage("Are you sure to delete this inspection?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Message msg = mHandler.obtainMessage(1, inspection_id, 0);
                        mHandler.sendMessageDelayed(msg, 100);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void gotoMain() {
        try {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } catch (Exception e) {
        }

        getActivity().finish();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                InspectionDatabase db = InspectionDatabase.getInstance(getActivity());
                db.deleteInspection(msg.arg1);

                restoreForm();
            }
        }
    };

}
