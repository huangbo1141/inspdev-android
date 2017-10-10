package com.idragonit.inspection;

import android.Manifest;
import android.content.pm.PackageManager;
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
import com.idragonit.inspection.utils.SecurityUtils;
import com.idragonit.inspection.utils.StorageUtils;
import com.idragonit.inspection.utils.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import yuku.iconcontextmenu.IconContextMenu;

/**
 * Created by CJH on 2016.01.23.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, InspectionAdapterListener {

    ImageView mMenu;

    ListView mListView;
    RequestedInspectionAdapter mAdapter;

    RelativeLayout mRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mAdapter = new RequestedInspectionAdapter(this, this);
        mListView = (ListView) findViewById(R.id.list);
        mListView.setAdapter(mAdapter);

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

        if (AppData.KIND == Constants.INSPECTION_WCI) {
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
                        } else {
//                            showMessage(message);
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
//                        showMessage(Constants.MSG_FAILED);
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
                            mAdapter.clear();
                            db.deleteRequestedInspection();

                            for (int i=0; i<obj.length(); i++) {
                                JSONObject result = obj.getJSONObject(i);

                                RequestedInspectionInfo item = new RequestedInspectionInfo();
                                item.id = Utils.checkNull(result.getString("id"), 0);
                                item.type = Utils.checkNull(result.getString("category"), 0);

                                item.address = Utils.checkNull(result.getString("address"));
                                item.job_number = Utils.checkNull(result.getString("job_number"));

                                if (item.type==Constants.INSPECTION_WCI) {
                                    item.community = "";

                                    item.city = Utils.checkNull(result.getString("city_duct"));
                                    item.area = Utils.checkNull(result.getString("area"), 0);
                                    item.volume = Utils.checkNull(result.getString("volume"), 0);
                                    item.qn = Utils.checkNull(result.getString("qn"), 0.0f);

                                    item.wall_area = Utils.checkNull(result.getString("wall_area"), 0);
                                    item.ceiling_area = Utils.checkNull(result.getString("ceiling_area"), 0);
                                    item.design_location = Utils.checkNull(result.getString("design_location"));
                                } else {
                                    item.community = item.job_number.substring(0, 4);
                                }

                                item.lot = Utils.checkNull(result.getString("lot"));

                                item.community_name = Utils.checkNull(result.getString("community_name"));
                                item.inspection_date = Utils.checkNull(result.getString("requested_at"));

                                item.region = Utils.checkNull(result.getString("region"), 0);
                                item.field_manager = Utils.checkNull(result.getString("manager_id"), 0);

                                if (result.has("is_building_unit")) {
                                    if (Utils.checkNull(result.getString("is_building_unit"), 0)==1)
                                        item.is_building_unit = true;
                                }

                                if (result.has("edit_inspection_id")) {
                                    item.edit_inspection_id = Utils.checkNull(result.getString("edit_inspection_id"), 0);
                                }

                                if (item.id>0 && db.getInspection(item.id)==null) {
                                    db.insertRequestedInspection(item.id, item.type, item.job_number, item.inspection_date, item.inspection_date, item.toTemp());
                                    mAdapter.add(item);
                                }
                            }

                            mAdapter.notifyDataSetChanged();

                            if (mAdapter.getCount()==0)
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

        mAdapter.clear();
        final InspectionDatabase db = InspectionDatabase.getInstance(this);
        ArrayList<RequestedInspectionInfo> inspections = db.loadRequestedInspection();
        for (RequestedInspectionInfo inspection : inspections) {
            if (db.getInspection(inspection.id)==null)
                mAdapter.add(inspection);
        }

        mAdapter.notifyDataSetChanged();
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

}
