package com.situ.wandance.appviewer.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.Field;

/**
 * Create by xukai03
 * Date:2021/2/2
 * Description:
 */
public class ActivityUtils {
    /**
     * 沉浸状态栏
     */
    public static void immerseActivity(Activity activity) {
        try {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS); // Window负责绘制系统的导航栏及状态栏，然后具体的绘制颜色从getStatusBarColor中获取
                activity.getWindow().setStatusBarColor(Color.TRANSPARENT);// 设置getStatusBarColor的颜色
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // 设置全屏
                        | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // 亮色模式，图标黑色
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE); // 固定布局，状态栏及导航栏显示隐藏切换时，其他布局不调整
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                try {
                    @SuppressLint("PrivateApi") Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                    Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                    field.setAccessible(true);
                    field.setInt(activity.getWindow().getDecorView(), Color.TRANSPARENT);  //设置透明
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }
    }
}
