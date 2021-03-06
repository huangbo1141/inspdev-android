package com.idragonit.inspection;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.idragonit.inspection.components.WaitingDialog;
import com.idragonit.inspection.utils.ImageUtils;
import com.idragonit.inspection.utils.SecurityUtils;
import com.idragonit.inspection.utils.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import cz.msebera.android.httpclient.Header;


public abstract class BaseFragment extends Fragment {

	public Bridge mBridge;
	public Handler mFragmentHandler = null;
	public WaitingDialog mWaitingDialog;

    public int ACTIVITY_RESULT__CAMERA = 101;
    public int ACTIVITY_RESULT__GALLERY = 102;
	
	public void setFragmentHandler(Handler handler){
		mFragmentHandler = handler;
	}
	
	public static void hideKeyboard(Context ctx) {
	    InputMethodManager inputManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);

	    // check if no view has focus:
	     View v = ((Activity) ctx).getCurrentFocus();
	     if (v == null)
	        return;

	    inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
	 }

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mBridge = (Bridge) getActivity();
	}

	@Override
	public void onStart() {
		super.onStart();

		if (mWaitingDialog!=null) {
			try {
				mWaitingDialog.show();
			}catch (Exception e){}
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mWaitingDialog!=null) {
			try {
				mWaitingDialog.show();
			}catch (Exception e){}
		}
	}

	@Override
	public void onStop() {
		super.onStop();

		if (mWaitingDialog!=null){
			try {
				mWaitingDialog.dismiss();
			}catch (Exception e){}
		}
	}

	public void showLoading(String message){
        mWaitingDialog = new WaitingDialog(getActivity(), message);
        mWaitingDialog.show();
	}

	public void hideLoading(){
        try {
            if (mWaitingDialog != null)
                mWaitingDialog.dismiss();
            mWaitingDialog = null;
        }catch (Exception e){}
	}

	public void selectFramgent(int fragment) {
//		Message msg = mFragmentHandler.obtainMessage(ACTION_FRAGMENT, fragment, ANIMATION_RIGHT_TO_LEFT);
//		mFragmentHandler.sendMessageDelayed(msg, ACTION_DELAY_TIME);
	}

	public void showMessage(String message){
		try {
			Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
		}catch (Exception e){}
	}

	public void showMessage(int message){
		try {
			Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
		}catch (Exception e){}
	}

    public void takePictureFromCamera(int activityResult){
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1){
            // Do something for lollipop and above versions
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", new File(AppData.TAKEN_PICTURE)));
        } else{
            // do something for phones running an SDK before lollipop
            intent.putExtra(MediaStore.EXTRA_OUTPUT, new File(AppData.TAKEN_PICTURE));
        }


        try {
            startActivityForResult(intent, activityResult);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void takePictureFromGallery(int activityResult){
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");
//
//        try {
//            startActivityForResult(intent, activityResult);
//        } catch (ActivityNotFoundException e) {
//            e.printStackTrace();
//        }

        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, "Select File"),
                ACTIVITY_RESULT__GALLERY);
    }

    public String getFilePathFromActivityResultUri(Uri uri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = null;

        if (res == null || res.length() == 0) {
            res = ImageUtils.getPath(getActivity(), uri);
        }

        return res;
    }

    public String getFilePathFromActivityResult(Uri uri) {
        String res = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            Cursor cursor = getActivity().managedQuery(uri, proj, null, null,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                int i1 = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                res = cursor.getString(i1);
            }
            if (cursor != null)
                cursor.close();
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }

        return res;
    }

	public void copyFile(File src, File dst) throws IOException {
		FileChannel inChannel = new FileInputStream(src).getChannel();
		FileChannel outChannel = new FileOutputStream(dst).getChannel();
		try	{
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} finally {
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
		}
	}


	public abstract String getFragmentTag();

	public abstract boolean validateForm();
    public abstract void saveForm();
    public abstract void restoreForm();

	public String applyImageRotation(String image ) {
		try {
			ExifInterface ei = new ExifInterface(image);
			int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
                    image = rotateImage(image, 90);
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
                    image = rotateImage(image, 180);
					break;
			}
			if (AppData.saveGallery){
                ImageUtils.addImageToGallery(image,getActivity());
            }

		} catch (Exception e){}


		return image;
	}

	private int getResolution() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        int resolution = Utils.checkNull(sp.getString(Constants.RESOLUTION_KEY, "0"), 0);
        return resolution;
    }

    public boolean getSmsStatus(int id) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        boolean status = sp.getBoolean(Constants.SMS_STATUS_KEY+String.valueOf(id),false);

        return status;
    }

    public void setSmsStatus(int id) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Constants.SMS_STATUS_KEY+String.valueOf(id),true);
        editor.commit();
    }

	public String scaleImage(String image) {
		try {
            int resolution = getResolution();
            if (resolution!=0) {
                int width = Constants.RESOLUTION_WIDTH[resolution];
                int height = Constants.RESOLUTION_HEIGHT[resolution];

                Log.e("Bitmap", image);
                Log.e("Resolution", width+"*"+height);

                Bitmap bitmap = getBitmap(image);
                int org_width = bitmap.getWidth();
                int org_height = bitmap.getHeight();

                Log.e("BitmapSize", org_width+"*"+org_height);

                if (org_width>org_height) {
                    int temp = width;
                    width = height;
                    height = temp;
                }

                if (org_width>width || org_height>height) {
                    float ratio = Math.min((float)width/org_width, (float)height/org_height);

                    int new_width = Math.round(ratio*org_width);
                    int new_height = Math.round(ratio*org_height);

                    Log.e("ScaleSize", new_width+"*"+new_height);

                    Bitmap new_bitmap = Bitmap.createScaledBitmap(bitmap, new_width, new_height, false);
                    writeBitmap(image, new_bitmap);
                }
            }

		} catch (Exception e){
            e.printStackTrace();
        }

		return image;
	}

    private Bitmap getBitmap(String path) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(path,bmOptions);
        return bitmap;
    }

    private void writeBitmap(String path, Bitmap bitmap) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 75, out); // bmp is your Bitmap instance
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

	private String rotateImage(String path, float angle) {
        Bitmap source = getBitmap(path);
		Bitmap retVal;

		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        writeBitmap(path, retVal);
		return path;
	}

	public void handleSms(int jobid){
        String url = Constants.API__BASEPATH + "send_sms_from_android3";

        RequestParams params = new RequestParams();
        params.put("job_id", jobid);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(Constants.CONNECTION_TIMEOUT * 1000);
        client.setSSLSocketFactory(SecurityUtils.getSSLSocketFactory());
        client.post(getActivity(), url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (statusCode==200 && response!=null) {
                    Log.i("handleSms Inspections", response.toString());

                    hideLoading();

                    try {
                        JSONArray list_numbers = response.getJSONArray("list_numbers");
                        for (int i=0;i<list_numbers.length();i++){
                            Message message = new Message();
                            message.what = 100;

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("response",response);
                            jsonObject.put("index",i);
                            message.obj = jsonObject;

                            twilioHandler.sendMessageDelayed(message,i*2000);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
//                    showMessage(Constants.MSG_CONNECTION);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("Basic_Step","onFailure");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("Basic_Step","onFailure");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e("Basic_Step","onFailure");
            }
        });
    }

    private Handler twilioHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    try{
                        JSONObject jsonObject = (JSONObject) msg.obj;
                        sendTwilio(jsonObject);
                    }catch (Exception ex){
                        Log.e("twilioHandler","onFailure");
                    }
                    break;
            }
        }

    };
    public void sendTwilio(JSONObject jsonObject){
        try{
            JSONObject response = jsonObject.getJSONObject("response");
            JSONArray list_numbers = response.getJSONArray("list_numbers");
            String sid = response.getString("sid");
            String token = response.getString("token");
            String from = response.getString("from");
            String text = response.getString("text");
            int index = jsonObject.getInt("index");


            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.CONNECTION_TIMEOUT * 1000);
            client.setSSLSocketFactory(SecurityUtils.getSSLSocketFactory());
            client.setBasicAuth(sid, token);
            String url = "https://api.twilio.com/2010-04-01/Accounts/"+sid+"/Messages";

            RequestParams params = new RequestParams();
            params.put("To",list_numbers.getString(index));
            params.put("From",from);
            params.put("Body",text);


            client.post(getActivity(), url, params, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    if (statusCode==200 && response!=null) {
                        Log.i("sendTwilio Inspections", response.toString());

                    } else {
//                    showMessage(Constants.MSG_CONNECTION);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e("Basic_Step","onFailure");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e("Basic_Step","onFailure");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Log.e("Basic_Step","onFailure");
                }
            });
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
