package com.idragonit.inspection.core;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;

import com.idragonit.inspection.Constants;
import com.idragonit.inspection.InspectionDatabase;
import com.idragonit.inspection.models.SpinnerInfo;
import com.idragonit.inspection.utils.DeviceUtils;
import com.idragonit.inspection.utils.SecurityUtils;
import com.idragonit.inspection.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by CJH on 7/2/2016.
 */
public class LocalStorageService extends Service {
    final String TAG = "LocalStorageService";

    final int SLEEP_TIME = 1000*60*60;
//    final int SLEEP_TIME = 1000*60;

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mServiceHandler.sendEmptyMessage(0);
        return START_STICKY;
    }

    private void syncRegion() {
        if (!DeviceUtils.isInternetAvailable(this))
            return;

        ArrayList<String> ids = new ArrayList<String>();
        final InspectionDatabase db = InspectionDatabase.getInstance(this);
        ArrayList<SpinnerInfo> regions = db.loadRegion();
        for (SpinnerInfo region : regions) {
            ids.add(region.id+"");
        }

        RequestParams params = new RequestParams();
        params.put("ids", ids);

        SyncHttpClient client = new SyncHttpClient();
        client.setTimeout(Constants.CONNECTION_TIMEOUT * 1000);
        client.setSSLSocketFactory(SecurityUtils.getSSLSocketFactory());
        client.post(this, Constants.API__BASEPATH + Constants.API__SYNC_REGION, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (statusCode==200 && response!=null) {
                    Log.i("Sync Region", response.toString());

                    try{
                        JSONObject status = response.getJSONObject("status");
                        int code = Utils.checkNull(status.getInt("code"), 0);
                        String message = Utils.checkNull(status.getString("message"));
                        if (code == 0) {
                            JSONObject data = response.getJSONObject("response");

                            JSONArray obj_delete = data.getJSONArray("delete");
                            for (int i=0; i<obj_delete.length(); i++) {
                                int id = Utils.checkNull(obj_delete.getString(i), 0);
                                db.deleteRegion(id);
                            }

                            JSONArray obj = data.getJSONArray("region");
                            for (int i=0; i<obj.length(); i++) {
                                JSONObject result = obj.getJSONObject(i);
                                int id = Utils.checkNull(result.getString("id"), 0);
                                String region = Utils.checkNull(result.getString("region"));

                                if (db.isExistRegion(id))
                                    db.updateRegion(id, region);
                                else
                                    db.insertRegion(id, region);
                            }
                        } else {
                        }
                    }catch (Exception e) {
                    }
                } else {
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
            }
        });
    }

    private void syncFieldManager() {
        if (!DeviceUtils.isInternetAvailable(this))
            return;

        final InspectionDatabase db = InspectionDatabase.getInstance(this);
        ArrayList<String> ids = new ArrayList<>();

        ArrayList<SpinnerInfo> fms = db.loadFieldManager();
        for (SpinnerInfo fm : fms) {
            ids.add(fm.id+"");
        }

        RequestParams params = new RequestParams();
        params.put("ids", ids);

        SyncHttpClient client = new SyncHttpClient();
        client.setTimeout(Constants.CONNECTION_TIMEOUT * 1000);
        client.setSSLSocketFactory(SecurityUtils.getSSLSocketFactory());
        client.post(this, Constants.API__BASEPATH + Constants.API__SYNC_FIELD_MANAGER, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (statusCode==200 && response!=null) {
                    Log.i("Sync FM", response.toString());

                    try{
                        JSONObject status = response.getJSONObject("status");
                        int code = Utils.checkNull(status.getInt("code"), 0);
                        String message = Utils.checkNull(status.getString("message"));
                        if (code == 0) {
                            JSONObject data = response.getJSONObject("response");

                            JSONArray obj_delete = data.getJSONArray("delete");
                            for (int i=0; i<obj_delete.length(); i++) {
                                int id = Utils.checkNull(obj_delete.getString(i), 0);
                                db.deleteFieldManager(id);
                            }

                            JSONArray obj = data.getJSONArray("fm");
                            for (int i=0; i<obj.length(); i++) {
                                JSONObject result = obj.getJSONObject(i);
                                int id = Utils.checkNull(result.getString("id"), 0);
                                String region = Utils.checkNull(result.getString("region"));
                                String first_name = Utils.checkNull(result.getString("first_name"));
                                String last_name = Utils.checkNull(result.getString("last_name"));
                                String updated_at = Utils.checkNull(result.getString("updated_at"));

                                if (db.isExistFieldManager(id))
                                    db.updateFieldManager(id, region, first_name + " " + last_name, updated_at);
                                else
                                    db.insertFieldManager(id, region, first_name + " " + last_name, updated_at);
                            }
                        } else {
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
            }
        });
    }

    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            while (true) {
                syncRegion();
                syncFieldManager();

                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (Exception e){}
            }
        }
    }
}
