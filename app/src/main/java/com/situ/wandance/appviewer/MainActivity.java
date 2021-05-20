package com.situ.wandance.appviewer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.situ.wandance.appviewer.adapter.AppListAdapter;
import com.situ.wandance.appviewer.adapter.AppListDivider;
import com.situ.wandance.appviewer.beans.AppInfo;
import com.situ.wandance.appviewer.manager.AppDataManager;
import com.situ.wandance.appviewer.utils.ActivityUtils;
import com.situ.wandance.appviewer.utils.ComUtils;
import com.situ.wandance.appviewer.utils.DeviceUtils;
import com.umeng.commonsdk.statistics.common.DeviceConfig;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AppDataManager.ILoadListener, View.OnClickListener {
    private static final int RequestCode = 10000;
    private Activity mActivity;
    private RecyclerView mRecyclerView;
    private AppListAdapter mAdapter;
    private ImageView ivRefresh;
    private EditText etSearch;
    private ImageView ivSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = MainActivity.this;
        ActivityUtils.immerseActivity(mActivity);
        setContentView(R.layout.layout_activity_main);

        etSearch = findViewById(R.id.et_search_view);
        ivSearch = findViewById(R.id.iv_search);

        mAdapter = new AppListAdapter(mActivity);
        mRecyclerView = findViewById(R.id.rv_app_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new AppListDivider(mActivity));

        ivRefresh = findViewById(R.id.iv_refresh);
        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ivSearch.setOnClickListener(this);
        mRecyclerView.setItemAnimator(new RecyclerView.ItemAnimator() {
            @Override
            public boolean animateDisappearance(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @Nullable ItemHolderInfo postLayoutInfo) {
                return false;
            }

            @Override
            public boolean animateAppearance(@NonNull RecyclerView.ViewHolder viewHolder, @Nullable ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
                return false;
            }

            @Override
            public boolean animatePersistence(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
                return false;
            }

            @Override
            public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder, @NonNull RecyclerView.ViewHolder newHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
                return false;
            }

            @Override
            public void runPendingAnimations() {

            }

            @Override
            public void endAnimation(@NonNull RecyclerView.ViewHolder item) {

            }

            @Override
            public void endAnimations() {

            }

            @Override
            public boolean isRunning() {
                return false;
            }
        });
        AppDataManager.get().registerListener(this);
        AppDataManager.get().loadInstallApps(mActivity);
    }


    @Override
    public void loadSuccess(ArrayList<AppInfo> info) {
        mAdapter.setData(info);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppDataManager.get().unRegisterListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.iv_refresh) {
            AppDataManager.get().loadInstallApps(mActivity);
        } else if (id == R.id.iv_search) {
            getDeviceInfo();
        }
    }

    private void getDeviceInfo() {
        String[] necessaryPermissions = getNecessaryPermissions();
        if (necessaryPermissions.length != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mActivity.requestPermissions(necessaryPermissions, RequestCode);
            } else {
                ActivityCompat.requestPermissions(mActivity, necessaryPermissions, RequestCode);
            }
            return;
        }
        String deviceInfo = DeviceConfig.getDeviceIdForGeneral(mActivity);
        String mac = DeviceConfig.getMac(mActivity);
        String msg = String.format("{\"device_id\":\"%s\",\"mac\":\"%s\"}", deviceInfo, mac);
        ComUtils.copyToKeyBoard(mActivity, msg);
    }

    private String[] getNecessaryPermissions() {
        ArrayList<String> permissions = new ArrayList<>();
        if (!DeviceUtils.checkPermission(mActivity, Manifest.permission.READ_PHONE_STATE)) {
            permissions.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!DeviceUtils.checkPermission(mActivity, Manifest.permission.ACCESS_WIFI_STATE)) {
            permissions.add(Manifest.permission.ACCESS_WIFI_STATE);
        }
        if (!permissions.isEmpty()) {
            String[] ps = new String[permissions.size()];
            for (int i = 0; i < permissions.size(); i++) {
                ps[i] = permissions.get(i);
            }
            return ps;
        }
        return new String[0];
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RequestCode) {
            getDeviceInfo();
        }
    }
}
