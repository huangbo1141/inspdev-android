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
import com.idragonit.inspection.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


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
        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", new File(AppData.TAKEN_PICTURE)));

        try {
            startActivityForResult(intent, activityResult);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void takePictureFromGallery(int activityResult){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        try {
            startActivityForResult(intent, activityResult);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
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

	public String applyImageRotation(String image) {
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
		} catch (Exception e){}

		return image;
	}

	private int getResolution() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        int resolution = Utils.checkNull(sp.getString(Constants.RESOLUTION_KEY, "0"), 0);
        return resolution;
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

		} catch (Exception e){}

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
}
