package com.situ.wandance.appviewer.manager;

import android.content.Context;

import com.situ.wandance.appviewer.beans.AppInfo;
import com.situ.wandance.appviewer.utils.AppUtils;
import com.situ.wandance.appviewer.utils.ThreadUtils;

import java.util.ArrayList;

/**
 * Create by xukai03
 * Date:2021/2/3
 * Description:
 */
public class AppDataManager {

    private ArrayList<AppInfo> appInfoList;

    private ArrayList<ILoadListener> listeners = new ArrayList<>();

    private boolean isLoading = false;

    public void loadInstallApps(final Context context) {
        if (isLoading) {
            return;
        }
        isLoading = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<AppInfo> apps = AppUtils.getAllApps(context.getApplicationContext());
                ThreadUtils.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isLoading = false;
                        callbackLoadAppSuccess(apps);
                    }
                });
            }
        }).start();
    }

    public void registerListener(ILoadListener listener) {
        if (appInfoList != null) {
            listener.loadSuccess(new ArrayList<>(appInfoList));
        }
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void unRegisterListener(ILoadListener listener) {
        listeners.remove(listener);
    }

    private void callbackLoadAppSuccess(ArrayList<AppInfo> newAppList) {
        appInfoList = newAppList;
        for (ILoadListener loadListener : listeners) {
            loadListener.loadSuccess(new ArrayList<>(appInfoList));
        }
    }

    private static class Inner {
        private static AppDataManager i = new AppDataManager();
    }

    public static AppDataManager get() {
        return Inner.i;
    }

    private AppDataManager() {
    }

    public interface ILoadListener {
        void loadSuccess(ArrayList<AppInfo> info);
    }
}
