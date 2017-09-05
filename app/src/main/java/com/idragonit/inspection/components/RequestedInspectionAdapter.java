package com.idragonit.inspection.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.idragonit.inspection.Constants;
import com.idragonit.inspection.R;
import com.idragonit.inspection.models.RequestedInspectionInfo;
import com.idragonit.inspection.utils.InspectionUtils;

/**
 * Created by CJH on 2016.01.24.
 */
public class RequestedInspectionAdapter extends ArrayAdapter<RequestedInspectionInfo> {

    Context mContext;
    InspectionAdapterListener mListener;

    public RequestedInspectionAdapter(Context context, InspectionAdapterListener listener) {
        super(context, 0);

        mContext = context;
        mListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_requested_inspection, null);

        TextView job = (TextView) convertView.findViewById(R.id.txt_job_number);
        TextView type = (TextView) convertView.findViewById(R.id.txt_type);
        TextView community = (TextView) convertView.findViewById(R.id.txt_community);
        TextView address = (TextView) convertView.findViewById(R.id.txt_address);
        TextView date = (TextView) convertView.findViewById(R.id.txt_date);

        final RequestedInspectionInfo item = getItem(position);
        if (item!=null) {
            String job_number = "Job Number: " + item.job_number;
            type.setText(InspectionUtils.getTitle(item.type));

            community.setText("Community: " + item.community_name);
            address.setText("Address: " + item.address);

            job_number += ", LOT: " + item.lot;

            job.setText(job_number);

//            if (item.type == Constants.INSPECTION_WCI) {
//                job_number += ", LOT: " + item.lot;
//                lot.setText("Area:" + item.area + ", Volume:" + item.volume + ", Qn:" + item.qn);
//            } else {
//                job_number += ", LOT: " + item.lot;
//            }

            date.setText("Requested: " + item.inspection_date);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener!=null)
                        mListener.onSubmit(item);
                }
            });
        }

        return convertView;
    }
}
