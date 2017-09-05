package com.idragonit.inspection.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.idragonit.inspection.AppData;
import com.idragonit.inspection.BaseFragment;
import com.idragonit.inspection.R;

/**
 * Created by CJH on 2016.01.23.
 */
public class Email_Step extends BaseFragment implements View.OnClickListener{

    LinearLayout mLayout_EmailContainer;
    EditText mText_Email;

    final int CONTACT_PICKER_RESULT = 1044;

    public static Email_Step newInstance(Object... args) {
        return new Email_Step();
    }

    @Override
    public String getFragmentTag() {
        return Email_Step.class.getSimpleName();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        if (mBridge == null)
            return root;

        View mContentView = inflater.inflate(R.layout.step_email, container, false);
        mText_Email = (EditText) mContentView.findViewById(R.id.txt_email);
        mLayout_EmailContainer = (LinearLayout) mContentView.findViewById(R.id.layout_recipient_email);

        mContentView.findViewById(R.id.btn_add).setOnClickListener(this);
        mContentView.findViewById(R.id.btn_contact).setOnClickListener(this);

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
                onAddEmail();
                break;

            case R.id.btn_contact:
                onContact();
                break;
        }
    }

    private void onAddEmail() {
        String email = mText_Email.getText().toString();
        if (email.length()==0) {
            showMessage( R.string.error__empty_email);
            return;
        }

        if (!AppData.checkRecipientEmail(email)) {
            showMessage( R.string.error__exist_email);
            return;
        }

        AppData.addRecipientEmail(email);
        addItemToContainer(email);

        mText_Email.setText("");
    }

    private void onContact() {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

    private void addItemToContainer(final String email) {
        final View root = LayoutInflater.from(getActivity()).inflate(R.layout.item_field, null, false);
        final TextView txt_email = (TextView) root.findViewById(R.id.txt_value);
        final ImageView btn_remove = (ImageView) root.findViewById(R.id.btn_remove);
        root.setTag(email);

        txt_email.setText(email);
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View root = mLayout_EmailContainer.findViewWithTag(email);
                mLayout_EmailContainer.removeView(root);
                AppData.removeRecipientEmail(email);
            }
        });

        mLayout_EmailContainer.addView(root);
    }

    @Override
    public boolean validateForm() {
//        if (AppData.RECIPIENT_EMAILS.size()==0) {
//            showMessage( R.string.error__empty_recipient_email);
//            return false;
//        }

        return true;
    }

    @Override
    public void saveForm() {
        String email = mText_Email.getText().toString();
        AppData.STORAGE.recipient_email = email;
    }

    @Override
    public void restoreForm() {
        mText_Email.setText(AppData.STORAGE.recipient_email);

        synchronized (AppData.SYNC) {
            for (String email : AppData.RECIPIENT_EMAILS) {
                addItemToContainer(email);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==CONTACT_PICKER_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                Cursor cursor = null;
                String email=null, name=null;

                try {
                    Uri result = data.getData();

                    // get the contact id from the Uri
                    String id = result.getLastPathSegment();

                    // query for everything email
                    cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,  null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?", new String[] { id }, null);

                    int nameId = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                    int emailIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
                    if (cursor.moveToFirst()) {
                        email = cursor.getString(emailIdx);
                        name = cursor.getString(nameId);
                        Log.i("AA", "Got email: " + email+"<<<"+name);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    try {
                        if (cursor != null) {
                            cursor.close();
                        }
                    } catch (Exception fd){}
                }

                if (email!=null && email.length()>0) {
                    mText_Email.setText(email);
                }

            }

            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
