package com.idragonit.inspection.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.idragonit.inspection.AppData;
import com.idragonit.inspection.Constants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.HttpClient;
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
    private static String filterUrl(String url){
        String ret;
        ret = url.replace("https","http");
        return ret;
    }
    public static void UploadFile(Context context, String url, String filePath, final OnResponseListener listener) {
//        HttpHelper helper = new HttpHelper();
//        helper.responseListener = listener;
//        helper.UploadFileTask(url, filePath);

//        url = Constants.API__BASEPATH + Constants.API__UPLOAD_PICTURE;
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(Constants.CONNECTION_TIMEOUT_FOR_UPLOAD * 1000);
        client.setSSLSocketFactory(SecurityUtils.getSSLSocketFactory());
        File myFile = new File(filePath);
        RequestParams params = new RequestParams();
        try {
//            params.put("wci", "wci");
//            params.put("signature", "signature");
            params.put("file", myFile);

            client.post(context,url,params,new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    listener.onResponse(response);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    listener.onResponse(null);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        listener.onResponse(jsonObject);
                    }catch (Exception ex){
                        listener.onResponse(null);
                    }

                }


                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    listener.onResponse(null);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    listener.onResponse(null);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    listener.onResponse(null);
                }


                @Override
                public void onCancel() {
                    super.onCancel();
                    listener.onResponse(null);
                }

                @Override
                public void onUserException(Throwable error) {
                    super.onUserException(error);
                    listener.onResponse(null);
                }
            });
        } catch(Exception e) {
            listener.onResponse(null);
        }
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
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .writeTimeout(Constants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                        .readTimeout(Constants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                        .connectTimeout(Constants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                        .build();

//                OkHttpClient client = SecurityUtils.CustomTrust2();
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
