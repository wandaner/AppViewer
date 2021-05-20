package com.situ.wandance.appviewer.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

/**
 * Create by xukai03
 * Date:2021/2/3
 * Description:
 */
public class ComUtils {

    public static void toast(Activity activity, String msg) {
        if (checkActivityExist(activity)) {
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public static void toast(Activity activity, int msg) {
        if (checkActivityExist(activity)) {
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public static void toast(Context context, int msg) {
        if (context == null) {
            return;
        }
        if (context instanceof Activity) {
            toast((Activity) context, msg);
            return;
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void toast(Context context, String msg) {
        if (context == null) {
            return;
        }
        if (context instanceof Activity) {
            toast((Activity) context, msg);
            return;
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }


    /**
     * 校验Activity是否合法
     */
    public static boolean checkActivityExist(Activity activity) {
        if ((null == activity) || activity.isFinishing() || activity.isRestricted()) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                if (activity.isDestroyed()) {
                    return false;
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 分享
     */
    public static void shareTextBySys(Context context, String info) {
        try {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            //要分享的文本内容，选择某项后会直接把这段文本发送出去，相当于调用选中的应用的接口，并传参
            shareIntent.putExtra(Intent.EXTRA_TEXT, info);
            //需要使用Intent.createChooser，这里我们直接复用。第二个参数并不会显示出来
            shareIntent = Intent.createChooser(shareIntent, "应用信息分享");
            context.startActivity(shareIntent);
        } catch (Exception e) {

        }
    }

    /**
     * 拷贝到粘贴板
     */
    public static void copyToKeyBoard(Context context, String msg) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("AppInfo", msg);
        assert cm != null;
        cm.setPrimaryClip(mClipData);
        ComUtils.toast(context, String.format("内容复制到粘贴板 >> [%s]", msg));
    }
}
