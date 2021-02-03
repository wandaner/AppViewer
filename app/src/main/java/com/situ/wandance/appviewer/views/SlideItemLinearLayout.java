package com.situ.wandance.appviewer.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

/**
 * Create by xukai03
 * Date:2021/1/4
 * Description:
 */
public class SlideItemLinearLayout extends LinearLayout {

    private int mTouchSlop;

    private int touchDownX;
    private int touchDownY;
    private boolean isMoved;

    public SlideItemLinearLayout(Context context) {
        this(context, null);
    }

    public SlideItemLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideItemLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDownX = (int) event.getX();
                touchDownY = (int) event.getY();
                isMoved = false;
                break;
            case MotionEvent.ACTION_MOVE:
                isMoved = true;
                break;
            case MotionEvent.ACTION_BUTTON_PRESS:
                break;
            case MotionEvent.ACTION_UP:
                if (!isMoved && Math.abs(event.getX() - touchDownX) < mTouchSlop && Math.abs(event.getY() - touchDownY) < mTouchSlop) {
                    performClick();
                }
                break;
        }
        return true;
    }
}
