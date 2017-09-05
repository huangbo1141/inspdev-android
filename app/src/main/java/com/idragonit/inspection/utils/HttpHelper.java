package com.idragonit.inspection.utils;

import android.os.AsyncTask;

import com.idragonit.inspection.Constants;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Great Summit on 2/19/2016.
 */
public class HttpHelper {

    public OnResponseListener responseListener = null;

    public interface OnResponseListener {
        public void onResponse(JSONObject response);
    }

    public static void UploadFile(String url, String filePath, OnResponseListener listener) {
        HttpHelper helper = new HttpHelper();
        helper.responseListener = listener;
        helper.UploadFileTask(url, filePath);
    }

    private void UploadFileTask(String url, String filePath) {
        new UploadFileAsyncTask().execute(url, filePath);
    }

    public class UploadFileAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            String filePath = params[1];
            try {
                //////////////////////////////////////////////
                // OKHttp code
                OkHttpClient client = new OkHttpClient().newBuilder().writeTimeout(Constants.CONNECTION_TIMEOUT, TimeUnit.SECONDS).readTimeout(Constants.CONNECTION_TIMEOUT, TimeUnit.SECONDS).connectTimeout(Constants.CONNECTION_TIMEOUT, TimeUnit.SECONDS).build();
                MediaType MEDIA_TYPE_IMAGE = MediaType.parse("image/*");

                File file = new File(filePath);
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", file.getName(), RequestBody.create(MEDIA_TYPE_IMAGE, file))
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();
                try {
                    return response.body().string();
                } catch (Exception e) {
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && !result.isEmpty()) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    responseListener.onResponse(jsonObject);
                    return;
                } catch (Exception e) {
                }
            }

            responseListener.onResponse(null);
            return;
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
