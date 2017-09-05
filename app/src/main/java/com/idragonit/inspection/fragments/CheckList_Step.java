package com.idragonit.inspection.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.idragonit.inspection.AppData;
import com.idragonit.inspection.BaseFragment;
import com.idragonit.inspection.Constants;
import com.idragonit.inspection.R;
import com.idragonit.inspection.components.CheckingListAdapter;
import com.idragonit.inspection.models.CheckingItemInfo;
import com.idragonit.inspection.utils.Utils;

/**
 * Created by CJH on 2016.01.23.
 */
public class CheckList_Step extends BaseFragment implements View.OnClickListener{

    TextView mText_Title;
    ListView mListView;
    CheckingListAdapter mAdapter;
    int mIndex=0;

    public CheckList_Step() {
        mIndex = 0;
    }

    public static CheckList_Step newInstance(Object... args) {
        CheckList_Step fragment =  new CheckList_Step();
        if (args!=null && args.length>0) {
            fragment.mIndex = Utils.checkNull(args[0], 0);
        }

        return fragment;
    }

    @Override
    public String getFragmentTag() {
        return CheckList_Step.class.getSimpleName();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        if (mBridge == null)
            return root;

        View mContentView = inflater.inflate(R.layout.step_checklist, container, false);
        mText_Title = (TextView) mContentView.findViewById(R.id.txt_location);
        mText_Title.setText("Select Location :  " + AppData.STORAGE.checking_list.location);

        mListView = (ListView) mContentView.findViewById(R.id.listview);
        mAdapter = new CheckingListAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                processItemClick(position);
            }
        });

        return mContentView;
    }

    // Added By BongBong. 20160407
    private void processItemClick(final int position) {
        final CheckingItemInfo item = mAdapter.getItem(position);
//        if (item.status != Constants.CHECKING_STATUS__NONE) {
//            mBridge.switchTo(CheckPoint_Step.newInstance(position, item, -1), true);
//            return;
//        }

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity(), R.style.MyCustomDialog);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner);
        for (int i = 0; i < Constants.CHECKING_STATUS.length; i++)
            arrayAdapter.add(Constants.CHECKING_STATUS[i]);

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == Constants.CHECKING_STATUS__FAIL || which == Constants.CHECKING_STATUS__NOT_READY) {
                            mBridge.switchTo(CheckPoint_Step.newInstance(position, item, Constants.CHECKING_STATUS__NONE + which), true);
                            return;
                        }

                        item.status = which;
                        mAdapter.notifyDataSetChanged();
                    }
                });
        builderSingle.setCancelable(true);
        builderSingle.show();
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
        }
    }

    @Override
    public boolean validateForm() {
        return true;
    }

    @Override
    public void saveForm() {
        AppData.addLocation(AppData.STORAGE.checking_list);
    }

    @Override
    public void restoreForm() {
        mAdapter.clear();

        for (CheckingItemInfo item : AppData.STORAGE.checking_list.checking_list) {
            mAdapter.add(item);
        }

        mAdapter.notifyDataSetChanged();
        if (mAdapter.getCount()<mIndex+1) {
            mListView.smoothScrollToPosition(mIndex);
        } else {
            mListView.smoothScrollToPosition(mIndex+1);
        }

    }


}
