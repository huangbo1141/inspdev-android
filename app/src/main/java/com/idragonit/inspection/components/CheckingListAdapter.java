package com.idragonit.inspection.components;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.idragonit.inspection.Constants;
import com.idragonit.inspection.R;
import com.idragonit.inspection.models.CheckingItemInfo;

/**
 * Created by CJH on 2016.01.24.
 */
public class CheckingListAdapter extends ArrayAdapter<CheckingItemInfo> {

    Context mContext;

    public CheckingListAdapter(Context context) {
        super(context, 0);

        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_checking, null);

        TextView title = (TextView) convertView.findViewById(R.id.txt_title);
        TextView status = (TextView) convertView.findViewById(R.id.txt_status);
        CheckingItemInfo item = getItem(position);
        title.setText(item.title);
        status.setText("Status :   " + Constants.CHECKING_STATUS[item.status]);

        if (item.status == Constants.CHECKING_STATUS__NONE)
           convertView.setBackgroundResource(R.color.background);
        else
            convertView.setBackgroundResource(R.color.highlight);

        status.setTextColor(ContextCompat.getColor(mContext,R.color.primary));
        if (item.status == Constants.CHECKING_STATUS__PASS)
            status.setTextColor(ContextCompat.getColor(mContext,R.color.color_pass));
        if (item.status == Constants.CHECKING_STATUS__FAIL)
            status.setTextColor(ContextCompat.getColor(mContext,R.color.color_fail));
        if (item.status == Constants.CHECKING_STATUS__NOT_READY)
            status.setTextColor(ContextCompat.getColor(mContext,R.color.color_ready));
        if (item.status == Constants.CHECKING_STATUS__NOT_APPLICABLE)
            status.setTextColor(ContextCompat.getColor(mContext,R.color.color_applicable));
        if (item.status == Constants.CHECKING_STATUS__CANNOT_VERIFY)
            status.setTextColor(ContextCompat.getColor(mContext,R.color.color_verify));

        return convertView;
    }
}
