package com.situ.wandance.appviewer.beans;

import android.annotation.NonNull;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;

import com.situ.wandance.appviewer.utils.AppUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Create by xukai03
 * Date:2021/2/2
 * Description:
 */
public class AppInfo {
    /**
     * APP名称
     */
    private String name;
    /**
     * APP包名
     */
    private String packageName;
    /**
     * 版本名称
     */
    private String versionName;
    /**
     * 版本号
     */
    private String versionCode;
    /**
     * 图标
     */
    private Drawable icon;
    /**
     * sha1
     */
    private String sha1;
    /**
     * md5
     */
    private String md5;
    /**
     * 安装时间
     */
    private long firstInstallTime;

    public long getFirstInstallTime() {
        return firstInstallTime;
    }

    public void setFirstInstallTime(long firstInstallTime) {
        this.firstInstallTime = firstInstallTime;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public static AppInfo create(Context context, PackageInfo info) {
        AppInfo app = new AppInfo();
        app.setName(info.applicationInfo.loadLabel(context.getPackageManager()).toString());
        app.setPackageName(info.packageName);
        app.setVersionName(info.versionName);
        app.setVersionCode(info.versionCode + "");
        app.setIcon(info.applicationInfo.loadIcon(context.getPackageManager()));
        app.setSha1(AppUtils.calculateSha1(context, info.packageName));
        app.setMd5(AppUtils.calculateMd5(context, info.packageName));
        app.setFirstInstallTime(info.firstInstallTime);
        return app;
    }

    @NonNull
    @Override
    public String toString() {
        return "应用名称：" + name + "\n" +
                "应用包名：" + packageName + "\n" +
                "小版本号：" + versionName + "\n" +
                "大版本号：" + versionCode + "\n" +
                "签名SHA1：" + sha1 + "\n" +
                "签名MD5：" + md5 + "\n" +
                "安装时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(firstInstallTime);
    }
}
