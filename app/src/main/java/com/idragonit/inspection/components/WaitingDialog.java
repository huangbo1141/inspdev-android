package com.idragonit.inspection.components;

import android.app.ProgressDialog;
import android.content.Context;

import com.idragonit.inspection.R;

public class WaitingDialog extends ProgressDialog{
	
	Context mContext;
	String mMessage;

	public WaitingDialog(Context context, String message) {
		super(context, R.style.MyCustomDialog);
		
		// TODO Auto-generated constructor stub
		mContext = context;
		mMessage = message;

		setIndeterminate(true);
		setCancelable(false);
		setMessage(message);

	}

}
