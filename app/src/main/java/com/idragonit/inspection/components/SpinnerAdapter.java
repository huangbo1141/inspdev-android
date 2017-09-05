package com.idragonit.inspection.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.idragonit.inspection.R;

/**
 * Created by CJH on 2016.01.24.
 */
public class SpinnerAdapter extends ArrayAdapter<String> {

    Context mContext;

    public SpinnerAdapter(Context context) {
        super(context, R.layout.layout_spinner);

        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_spinner, null, false);
        }

        TextView title = (TextView) convertView;
        String v = getItem(position);
        title.setText(v);

        return convertView;
    }
}
