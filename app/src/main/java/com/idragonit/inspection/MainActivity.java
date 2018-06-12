package com.idragonit.inspection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.idragonit.inspection.components.InspectionAdapterListener;
import com.idragonit.inspection.components.RequestedInspectionAdapter;
import com.idragonit.inspection.models.RequestedInspectionInfo;
import com.idragonit.inspection.models.SyncInfo;
import com.idragonit.inspection.utils.DeviceUtils;
import com.idragonit.inspection.utils.InspectionUtils;
import com.idragonit.inspection.utils.SecurityUtils;
import com.idragonit.inspection.utils.StorageUtils;
import com.idragonit.inspection.utils.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import yuku.iconcontextmenu.IconContextMenu;

/**
 * Created by CJH on 2016.01.23.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, InspectionAdapterListener {

    ImageView mMenu;

    RelativeLayout mRoot;
    LinearLayout scrollContent;
    List<ViewHolder> listRows = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        scrollContent = (LinearLayout) findViewById(R.id.scrollContent);

        mRoot = (RelativeLayout) findViewById(R.id.root);
//        findViewById(R.id.btn_drainage).setOnClickListener(this);
//        findViewById(R.id.btn_lath).setOnClickListener(this);
        findViewById(R.id.btn_sync).setOnClickListener(this);
        findViewById(R.id.btn_refresh).setOnClickListener(this);

        mMenu = (ImageView) findViewById(R.id.btn_menu);
        mMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IconContextMenu menu = new IconContextMenu(MainActivity.this, R.menu.setting);
                menu.setOnIconContextItemSelectedListener(new IconContextMenu.IconContextItemSelectedListener() {
                    @Override
                    public void onIconContextItemSelected(MenuItem item, Object info) {
                        switch (item.getItemId()) {
                            case R.id.menu_setting:
                                onSettings();
                                break;

                            case R.id.menu_profile:
                                onProfile();
                                break;

                            case R.id.menu_logout:
                                onLogout();
                                break;
                            case R.id.menu_about:
                                onAbout();
                                break;
                        }
                    }
                });
                menu.show();
            }
        });

        init();
        checkPermission();
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION }, 200);
        }
    }

    private void init() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                InspectionDatabase db = InspectionDatabase.getInstance(MainActivity.this);
                if (db.hasData()) {
                    mHandler.sendEmptyMessageDelayed(1, 100);
                } else {
                    StorageUtils.initAppDirectory();
                }
            }
        }).start();

//        loadInspections();
        loadSys();
        mHandler.sendEmptyMessageDelayed(0, 100);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.btn_drainage:
//                onInspection(Constants.INSPECTION_DRAINAGE);
//                break;
//
//            case R.id.btn_lath:
//                onInspection(Constants.INSPECTION_LATH);
//                break;

            case R.id.btn_sync:
                onSync();
                break;

            case R.id.btn_refresh:
                onRefresh();
                break;
        }
    }

    private void onLogout() {
        LoginManager.logout(this);

        showActivity(LoginActivity.class, Constants.ANIMATION_RIGHT_TO_LEFT);
    }
    PopupWindow mPopupWindow;
    private void onAbout(){
        if (true){
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

            // Inflate the custom layout/view
            View customView = inflater.inflate(R.layout.layout_about,null);

            TextView appVersion = (TextView) customView.findViewById(R.id.txtAppVersion);
            appVersion.setText(DeviceUtils.getVersion(MainActivity.this));

            TextView credit = (TextView) customView.findViewById(R.id.txtCredit);
            credit.setText(String.valueOf(Constants.CREDITS));

            MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                    .customView(customView,false)
                    .title("About")
                    .positiveText("OK")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        }
                    });
            builder.show();


        }
    }

    private void onInspection(int kind) {
        AppData.init();
        AppData.KIND = kind;
        showActivity(InspectionActivity.class, Constants.ANIMATION_RIGHT_TO_LEFT);
    }

    private void onInspection(RequestedInspectionInfo requested) {
        AppData.init();

        AppData.KIND = requested.type;
        AppData.INSPECTION_REQUESTED_ID = requested.id;
        AppData.INSPECTION_EDIT_ID = requested.edit_inspection_id;

        AppData.INSPECTION.job_number = requested.job_number;
        AppData.INSPECTION.lot = requested.lot;
        AppData.INSPECTION.community = requested.community;
        AppData.INSPECTION.community_name = requested.community_name;

        AppData.INSPECTION.address = requested.address;
        AppData.INSPECTION.region = requested.region;
        AppData.INSPECTION.field_manager = requested.field_manager;
        AppData.INSPECTION.inspection_date = requested.inspection_date;
        AppData.INSPECTION.inspection_date_last = requested.inspection_date;

        AppData.INSPECTION.city = requested.city;
        AppData.INSPECTION.area = requested.area;
        AppData.INSPECTION.volume = requested.volume;
        AppData.INSPECTION.qn = requested.qn;
        AppData.INSPECTION.wall_area = requested.wall_area;
        AppData.INSPECTION.ceiling_area = requested.ceiling_area;
        AppData.INSPECTION.design_location = requested.design_location;

        AppData.INSPECTION.is_building_unit = requested.is_building_unit;
        AppData.INSPECTION.reinspection = requested.reinspection;

        if (AppData.KIND == Constants.INSPECTION_WCI || AppData.KIND == Constants.INSPECTION_PULTE_DUCT) {
            AppData.initUnit();
            AppData.INSPECTION.ready_inspection = true;
        } else {
            AppData.initComment();
        }

        showActivity(InspectionActivity.class, Constants.ANIMATION_RIGHT_TO_LEFT);
    }

    private void onProfile() {
        showActivity(ProfileActivity.class, Constants.ANIMATION_BOTTOM_TO_UP);
    }

    private void onSync() {
        AppData.init(Constants.MODE_SYNC);
        showActivity(InspectionActivity.class, Constants.ANIMATION_RIGHT_TO_LEFT);
    }

    private void onSettings() {
        showActivity(SettingActivity.class, Constants.ANIMATION_RIGHT_TO_LEFT);
    }

    private void loadSys() {
        if (!DeviceUtils.isInternetAvailable(this)) {
            return;
        }

        RequestParams params = new RequestParams();
        params.put("user_id", SecurityUtils.encodeKey(AppData.USER.id));
        params.put("date", Utils.getToday("-"));

        final InspectionDatabase db = InspectionDatabase.getInstance(this);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(Constants.CONNECTION_TIMEOUT * 1000);
        client.setSSLSocketFactory(SecurityUtils.getSSLSocketFactory());
        client.post(this, Constants.API__BASEPATH + Constants.API__SYS_ENERGY_INSPECTION, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (statusCode==200 && response!=null) {
                    Log.i("Assigned Inspections", response.toString());

                    hideLoading();

                    try{
                        JSONObject status = response.getJSONObject("status");
                        int code = Utils.checkNull(status.getInt("code"), 0);
                        String message = Utils.checkNull(status.getString("message"));
                        if (code == 0) {
                            JSONObject obj = response.getJSONObject("response");
                            JSONArray rows =  obj.getJSONArray("rows");
                            for (int i=0;i<rows.length(); i++){
                                JSONObject iobj = (JSONObject) rows.get(i);
                                String icode = iobj.getString("code");
                                String ivalue = iobj.getString("value");
                                if (icode!=null&& ivalue != null){
                                    AppData.sys_energy_inspection.put(icode,ivalue);
                                }
                            }
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
//                        showMessage(Constants.MSG_FAILED);
                    }

                    try{
                        JSONObject status = response.getJSONObject("status");
                        int code = Utils.checkNull(status.getInt("code"), 0);
                        if (code == 0) {
                            JSONObject obj = response.getJSONObject("response");
                            JSONArray rows =  obj.getJSONArray("sys_config");
                            for (int i=0;i<rows.length(); i++){
                                JSONObject iobj = (JSONObject) rows.get(i);
                                String icode = iobj.getString("code");
                                String ivalue = iobj.getString("value");
                                if (icode!=null&& ivalue != null){
                                    AppData.sys_config.put(icode,ivalue);
                                }
                            }
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
//                    showMessage(Constants.MSG_CONNECTION);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                hideLoading();
//                showMessage(Constants.MSG_CONNECTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                showMessage(Constants.MSG_CONNECTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                showMessage(Constants.MSG_CONNECTION);
            }
        });
    }

//    private void loadInspections(){
//        RequestParams params = new RequestParams();
//        params.put("inspection_id", SecurityUtils.encodeKey("14977"));
//        params.put("user_id", SecurityUtils.encodeKey("2"));
//
//        AsyncHttpClient client = new AsyncHttpClient();
//        client.setTimeout(Constants.CONNECTION_TIMEOUT * 1000);
//        client.setSSLSocketFactory(SecurityUtils.getSSLSocketFactory());
//        String url = Constants.API__BASEPATH + Constants.API__EMAIL;
//        client.post(this, url, params, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                if (statusCode==200 && response!=null) {
//                    try{
//                        JSONObject status = response.getJSONObject("status");
//                        JSONObject request = response.getJSONObject("request");
//                        int p = 0;
//                    }catch (Exception ex){
//                        ex.printStackTrace();
//                    }
//                }
//            }
//        });
//    }
    private void loadInspections() {
        showLoading(Constants.MSG_LOADING);

        if (!DeviceUtils.isInternetAvailable(this)) {
            showMessage(Constants.MSG_CONNECTION);
            loadInspectionsFromLocalStorage();
            return;
        }

        RequestParams params = new RequestParams();
        params.put("user_id", SecurityUtils.encodeKey(AppData.USER.id));
        params.put("date", Utils.getToday("-"));

        final InspectionDatabase db = InspectionDatabase.getInstance(this);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(Constants.CONNECTION_TIMEOUT * 1000);
        client.setSSLSocketFactory(SecurityUtils.getSSLSocketFactory());
        client.post(this, Constants.API__BASEPATH + Constants.API__REQUESTED_INSPECTION, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (statusCode==200 && response!=null) {
                    Log.i("Assigned Inspections", response.toString());

                    hideLoading();

                    try{
                        JSONObject status = response.getJSONObject("status");
                        int code = Utils.checkNull(status.getInt("code"), 0);
                        String message = Utils.checkNull(status.getString("message"));
                        if (code == 0) {
                            JSONArray obj = response.getJSONArray("response");
                            scrollContent.removeAllViews();
                            listRows = new ArrayList<>();
                            db.deleteRequestedInspection();

                            for (int i=0; i<obj.length(); i++) {
                                JSONObject result = obj.getJSONObject(i);
                                RequestedInspectionInfo item = RequestedInspectionInfo.parseJson(result);
                                if (item.id>0 && db.getInspection(item.id)==null) {
                                    db.insertRequestedInspection(item.id, item.type, item.job_number, item.inspection_date, item.inspection_date, item.toTemp());

                                    LayoutInflater inflater = getLayoutInflater();
                                    View view = inflater.inflate(R.layout.layout_requested_inspection, null);
                                    ViewHolder viewHolder = new ViewHolder(view);
                                    scrollContent.addView(viewHolder.mView);
                                    viewHolder.setData(item);

                                    listRows.add(viewHolder);
                                }
                            }

                            if (listRows.size()==0)
                                showMessage("No Assigned Inspections");

                        } else {
                            showMessage(message);
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                        showMessage(Constants.MSG_FAILED);
                    }
                } else {
                    showMessage(Constants.MSG_CONNECTION);

                    loadInspectionsFromLocalStorage();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                hideLoading();
                showMessage(Constants.MSG_CONNECTION);

                loadInspectionsFromLocalStorage();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                hideLoading();
                showMessage(Constants.MSG_CONNECTION);

                loadInspectionsFromLocalStorage();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                hideLoading();
                showMessage(Constants.MSG_CONNECTION);

                loadInspectionsFromLocalStorage();
            }
        });
    }

    private void loadInspectionsFromLocalStorage() {
//        showLoading(Constants.MSG_LOADING);
        listRows = new ArrayList<>();
        scrollContent.removeAllViews();
        final InspectionDatabase db = InspectionDatabase.getInstance(this);
        ArrayList<RequestedInspectionInfo> inspections = db.loadRequestedInspection();
        for (RequestedInspectionInfo inspection : inspections) {
            if (db.getInspection(inspection.id)==null){
                //
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.layout_requested_inspection, null);
                ViewHolder viewHolder = new ViewHolder(view);
                scrollContent.addView(viewHolder.mView);
                viewHolder.setData(inspection);

                listRows.add(viewHolder);
            }
        }
        hideLoading();
    }

    @Override
    public void onSubmit(SyncInfo item) {

    }

    @Override
    public void onDelete(SyncInfo item) {

    }

    @Override
    public void onSubmit(RequestedInspectionInfo item) {
//        String message = (String) AppData.sys_energy_inspection.get(Constants.SYS_HOME_MESSAGE1);
//        MaterialDialog.Builder builder = new MaterialDialog.Builder(MainActivity.this)
//                .title(message)
//                .positiveText("OK")
//                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//
//                    }
//                });
//        builder.show();

        if (item.inspection_date.equals(Utils.getToday("-"))){
            // today item
            onInspection(item);
        }else{
            String message = (String) AppData.sys_energy_inspection.get(Constants.SYS_HOME_MESSAGE1);
            MaterialDialog.Builder builder = new MaterialDialog.Builder(MainActivity.this)
                    .content(message)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        }
                    });
            builder.show();
        }

    }

    private void onRefresh() {
        mHandler.sendEmptyMessageDelayed(0, 100);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==0) {
                loadInspections();
            }

            if (msg.what==1) {
                findViewById(R.id.btn_sync).setVisibility(View.VISIBLE);
            }
        }
    };

    public class ViewHolder {

        private View mView,layFuture;

        TextView job,type,community,address,date;
        LinearLayout mark;

        public Object item;
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item != null && item instanceof RequestedInspectionInfo) {
                    RequestedInspectionInfo item = (RequestedInspectionInfo) ViewHolder.this.item;
                    onSubmit(item);
                }
            }
        };

        public ViewHolder(View localView) {

            mView = localView;
            job = (TextView) localView.findViewById(R.id.txt_job_number);
            type = (TextView) localView.findViewById(R.id.txt_type);
            community = (TextView) localView.findViewById(R.id.txt_community);
            address = (TextView) localView.findViewById(R.id.txt_address);
            date = (TextView) localView.findViewById(R.id.txt_date);
            mark = (LinearLayout) localView.findViewById(R.id.layMark);
            layFuture = localView.findViewById(R.id.layFuture);

        }

        public void setData(final Object data) {
            job.setText("");
            type.setText("");
            community.setText("");
            address.setText("");
            date.setText("");
            item = data;
            if (data instanceof RequestedInspectionInfo) {
                final RequestedInspectionInfo item = (RequestedInspectionInfo) data;
                String job_number = "Job Number: " + item.job_number;
                type.setText(InspectionUtils.getTitle(item.type,null));

                community.setText("Community: " + item.community_name);
                address.setText("Address: " + item.address);

                job_number += ", LOT: " + item.lot;

                job.setText(job_number);

                date.setText("Requested: " + item.inspection_date);

                mView.setOnClickListener(onClickListener);

                if (item.type == 3 || item.type == 1 || item.type == 2 || item.type == 4){
                    // wci
                    if (item.inspection_date.equals(Utils.getToday("-"))){
                        // today item
                        // no need to specify
                    }else{
                        layFuture.setVisibility(View.VISIBLE);
                        mView.setBackgroundColor(Color.parseColor("#a0111111"));
                    }
                }
            }
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
