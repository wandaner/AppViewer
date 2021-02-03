package com.situ.wandance.appviewer.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.situ.wandance.appviewer.beans.AppInfo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Create by xukai03
 * Date:2021/2/2
 * Description:
 */
public class AppUtils {

    /**
     * 查询手机内非系统应用
     */
    public static ArrayList<AppInfo> getAllApps(Context context) {
        long s = System.currentTimeMillis();
        ArrayList<AppInfo> appList = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        //获取手机内所有应用
        List<PackageInfo> apkList = packageManager.getInstalledPackages(0);
        for (int i = 0; i < apkList.size(); i++) {
            PackageInfo packageInfo = apkList.get(i);
            //判断是否为非系统预装的应用程序
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                // customs applications
                appList.add(AppInfo.create(context, packageInfo));
            }
        }
        Log.i("xukai", String.valueOf(System.currentTimeMillis() - s));
        return appList;
    }

    /**
     * 计算 SHA1
     */
    @SuppressLint("PackageManagerGetSignatures")
    public static String calculateSha1(Context context, String packageName) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info;
            byte[] cert;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                info = manager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES);
                cert = info.signingInfo.getApkContentsSigners()[0].toByteArray();
            } else {
                info = manager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
                cert = info.signatures[0].toByteArray();
            }
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuilder hexString = new StringBuilder();
            for (byte key : publicKey) {
                String appendString = Integer.toHexString(0xFF & key).toUpperCase(Locale.US);
                if (appendString.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length() - 1);
        } catch (NoSuchAlgorithmException | NullPointerException | PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 计算 MD5
     */
    @SuppressLint("PackageManagerGetSignatures")
    public static String calculateMd5(Context context, String packageName) {
        String md5 = "";
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info;
            byte[] paramArrayOfByte;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                info = manager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES);
                paramArrayOfByte = info.signingInfo.getApkContentsSigners()[0].toByteArray();
            } else {
                info = manager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
                paramArrayOfByte = info.signatures[0].toByteArray();
            }
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(paramArrayOfByte);
            md5 = toHexString(localMessageDigest.digest());
        } catch (NoSuchAlgorithmException | PackageManager.NameNotFoundException | NullPointerException e) {
            e.printStackTrace();
        }
        return md5;
    }

    private static String toHexString(byte[] paramArrayOfByte) {
        if (paramArrayOfByte == null) {
            return "";
        }
        StringBuilder localStringBuilder = new StringBuilder(2 * paramArrayOfByte.length);
        for (int i = 0; ; i++) {
            if (i >= paramArrayOfByte.length) {
                return localStringBuilder.toString();
            }
            String str = Integer.toString(0xFF & paramArrayOfByte[i], 16);
            if (str.length() == 1) {
                str = "0" + str;
            }
            localStringBuilder.append(str);
        }
    }
}
