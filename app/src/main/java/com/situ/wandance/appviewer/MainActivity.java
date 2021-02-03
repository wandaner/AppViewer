package com.situ.wandance.appviewer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.situ.wandance.appviewer.adapter.AppListAdapter;
import com.situ.wandance.appviewer.adapter.AppListDivider;
import com.situ.wandance.appviewer.beans.AppInfo;
import com.situ.wandance.appviewer.manager.AppDataManager;
import com.situ.wandance.appviewer.utils.ActivityUtils;
import com.situ.wandance.appviewer.utils.AppUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AppDataManager.ILoadListener {
    private Activity mActivity;
    private RecyclerView mRecyclerView;
    private AppListAdapter mAdapter;
    private ImageView ivRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = MainActivity.this;
        ActivityUtils.immerseActivity(mActivity);
        setContentView(R.layout.activity_main);
        mAdapter = new AppListAdapter(mActivity);
        mRecyclerView = findViewById(R.id.rv_app_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new AppListDivider(mActivity));

        ivRefresh = findViewById(R.id.iv_refresh);
        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDataManager.get().loadInstallApps(mActivity);
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
}
