package com.situ.wandance.appviewer;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

/**
 * @Author: kai.xu
 * @CreateDate: 2021/3/9 6:18 PM
 * @Description:
 */
public class MApplication extends Application {

    private static MApplication app;

    public static MApplication get() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        UMConfigure.setLogEnabled(true);
        UMConfigure.init(app, "60a6274dc9aacd3bd4df6ff4", "wan", UMConfigure.DEVICE_TYPE_PHONE, null);
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        UMConfigure.setProcessEvent(true);
    }
}
