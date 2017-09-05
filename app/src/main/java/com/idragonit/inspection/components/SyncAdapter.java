package com.idragonit.inspection.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.idragonit.inspection.R;
import com.idragonit.inspection.models.SyncInfo;
import com.idragonit.inspection.utils.Utils;

/**
 * Created by CJH on 2016.01.24.
 */
public class SyncAdapter extends ArrayAdapter<SyncInfo> {

    Context mContext;
    InspectionAdapterListener mListener;

    public SyncAdapter(Context context, InspectionAdapterListener listener) {
        super(context, 0);

        mContext = context;
        mListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_sync, null);

        TextView type = (TextView) convertView.findViewById(R.id.txt_type);
        TextView job = (TextView) convertView.findViewById(R.id.txt_job_number);
        TextView date = (TextView) convertView.findViewById(R.id.txt_date);

        final SyncInfo item = getItem(position);
        type.setText(item.type==1 ? "Drainage Plane Inspection" : "Lath Inspection");
        job.setText("Job Number: " + item.job_number);
        date.setText("Last submited: " + Utils.toDisplayDate(item.date));

        convertView.findViewById(R.id.layout_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSubmit(item);
            }
        });

        return convertView;
    }
}
