package com.situ.wandance.appviewer.utils;


import android.os.Handler;
import android.os.Looper;

/**
 * Create by xukai03
 * Date:2021/2/3
 * Description:
 */
public class ThreadUtils {

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public void runOnUiThread(Runnable runnable) {
        mainHandler.post(runnable);
    }

    public void runOnUiThread(Runnable runnable, long delay) {
        mainHandler.postDelayed(runnable, delay);
    }

    private static class Inner {
        private static ThreadUtils i = new ThreadUtils();
    }

    public static ThreadUtils get() {
        return Inner.i;
    }

    private ThreadUtils() {
    }
}
