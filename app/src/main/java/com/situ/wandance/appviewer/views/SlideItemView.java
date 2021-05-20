package com.situ.wandance.appviewer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.situ.wandance.appviewer.interfaces.ISlideItemView;

import androidx.annotation.Nullable;

/**
 * Create by xukai03
 * Date:2021/1/4
 * Description:
 */
public class SlideItemView extends LinearLayout implements ISlideItemView {

    private static final int SNAP_VELOCITY = 600;   // 最小滑动速度

    private int mTouchSlop;

    private Scroller scroller;

    /**
     * 触摸起始点
     */
    private float mTouchFirstX = 0.0f;
    private float mTouchFirstY = 0.0f;
    private float mTouchLastX = 0.0f;

    private VelocityTracker mVelocityTracker;   // 速度追踪器
    private boolean mIsInSlide = false;

    public SlideItemView(Context context) {
        this(context, null);
    }

    public SlideItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        scroller = new Scroller(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        int x = (int) e.getX();
        int y = (int) e.getY();
        obtainVelocity(e);
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN: // 所有触摸事件的第一个时间
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                }
                mTouchFirstX = x;
                mTouchFirstY = y;
                mTouchLastX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.computeCurrentVelocity(1000);
                float xVelocity = mVelocityTracker.getXVelocity();
                float yVelocity = mVelocityTracker.getYVelocity();
                if (Math.abs(xVelocity) > SNAP_VELOCITY && Math.abs(xVelocity) > Math.abs(yVelocity) || Math.abs(x - mTouchFirstX) >= mTouchSlop && Math.abs(x - mTouchFirstX) > Math.abs(y - mTouchFirstY)) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                releaseVelocity();
                if (mIsInSlide) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        obtainVelocity(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsInSlide = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = mTouchLastX - x;
                if (getScrollX() + dx <= getScrollWith() && getScrollX() + dx > 0) {
                    scrollBy((int) dx, 0);
                    mIsInSlide = true;
                }
                mTouchLastX = x;
                return mIsInSlide || super.onTouchEvent(event);
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();
                mVelocityTracker.computeCurrentVelocity(1000);
                float xVelocity = mVelocityTracker.getXVelocity();
                if (xVelocity < -SNAP_VELOCITY) { // 左滑速度大于最小临界速度，则继续滑动到最大
                    scroller.startScroll(scrollX, 0, getScrollWith() - scrollX, 0, Math.abs(getScrollWith() - scrollX));
                } else if (xVelocity >= SNAP_VELOCITY) { // 右滑达到最小临界速度，则继续滑动到关闭
                    scroller.startScroll(scrollX, 0, -scrollX, 0, Math.abs(scrollX));
                } else if (scrollX > getScrollWith() / 2) { // 左滑超控件一半，继续滑动到最大
                    scroller.startScroll(scrollX, 0, getScrollWith() - scrollX, 0, Math.abs(getScrollWith() - scrollX));
                } else {
                    scroller.startScroll(scrollX, 0, -scrollX, 0, Math.abs(scrollX));
                }
                postInvalidate();
                if (!mIsInSlide) {
                    performClick();
                }
                mIsInSlide = false;
                releaseVelocity();
                break;
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private void obtainVelocity(MotionEvent e) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(e);
    }

    private void releaseVelocity() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private int getScrollWith() {
        return 400;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    public void close() {
        int scrollX = getScrollX();
        if (scrollX != 0) {
            scroller.startScroll(scrollX, 0, -scrollX, 0, Math.abs(scrollX));
            postInvalidate();
        }
    }

    @Override
    public void fling() {
        int scrollX = getScrollX();
        if (scrollX > getScrollWith() / 2) { // 左滑超控件一半，继续滑动到最大
            scroller.startScroll(scrollX, 0, getScrollWith() - scrollX, 0, Math.abs(getScrollWith() - scrollX));
        } else {
            scroller.startScroll(scrollX, 0, -scrollX, 0, Math.abs(scrollX));
        }
        postInvalidate();
    }

}
