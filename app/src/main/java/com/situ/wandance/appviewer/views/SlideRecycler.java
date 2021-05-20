package com.situ.wandance.appviewer.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.situ.wandance.appviewer.interfaces.ISlideItemView;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Create by xukai03
 * Date:2021/1/4
 * Description:
 * <p>
 * FlingView itemView  仅支持包含两个子view
 */
public class SlideRecycler extends RecyclerView {

    private static final int INVALID_POSITION = -1; // 触摸到的点不在子View范围内
    private static final int INVALID_CHILD_WIDTH = -1;  // 子ItemView不含两个子View
    private static final int SNAP_VELOCITY = 600;   // 最小滑动速度

    private VelocityTracker mVelocityTracker;   // 速度追踪器

    private int mTouchSlop; // 认为是滑动的最小距离（一般由系统提供）

    private Scroller mScroller;

    private Rect mItemRect;   // 子View所在的矩形范围

    /**
     * 触摸起始点
     */
    private float mTouchFirstX = 0.0f;
    private float mTouchFirstY = 0.0f;
    private float mTouchLastX = 0.0f;

    private ViewGroup mFlingView = null;   // 触碰的子View
    private int mPosition = INVALID_POSITION; // 触摸的Position

    private LinearLayoutManager mLayoutManager;

    public SlideRecycler(Context context) {
        this(context, null);
    }

    public SlideRecycler(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideRecycler(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScroller = new Scroller(context);
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        mLayoutManager = (LinearLayoutManager) layout;
        super.setLayoutManager(layout);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        int x = (int) e.getX();
        int y = (int) e.getY();
        obtainVelocity(e);
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN: // 所有触摸事件的第一个时间
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mTouchFirstX = x;
                mTouchFirstY = y;
                mTouchLastX = x;
                // 查找当前触摸的View, 还原上一个滑动的View
                final View mProFlingView = mFlingView;
                calculateTouchPositionAndFlingView(x, y);
                if (mProFlingView instanceof ISlideItemView && mProFlingView != mFlingView) {
                    ((ISlideItemView) mProFlingView).close();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.computeCurrentVelocity(1000);
                float xVelocity = mVelocityTracker.getXVelocity();
                float yVelocity = mVelocityTracker.getYVelocity();
                if (Math.abs(xVelocity) > SNAP_VELOCITY && Math.abs(xVelocity) > Math.abs(yVelocity) || Math.abs(x - mTouchFirstX) >= mTouchSlop && Math.abs(x - mTouchFirstX) > Math.abs(y - mTouchFirstY)) {
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                releaseVelocity();
                break;
        }
        return super.onInterceptTouchEvent(e);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        obtainVelocity(e);
        if (e.getAction() == MotionEvent.ACTION_UP) {
            if (mFlingView instanceof ISlideItemView) {
                ((ISlideItemView) mFlingView).fling();
            }
            releaseVelocity();
        }
        return super.onTouchEvent(e);
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

    /**
     * 计算触摸的View，设置为FlingView
     * 计算触摸的Position，设置给mPosition
     * 如果当前触摸的View处于动画状态，则不可选中
     *
     * @param x 触摸的 x
     * @param y 触摸的 y
     */
    private void calculateTouchPositionAndFlingView(int x, int y) {
        mFlingView = null;
        mPosition = INVALID_POSITION;
        if (mLayoutManager == null) {
            return;
        }
        int firstPosition = mLayoutManager.findFirstVisibleItemPosition();
        if (mItemRect == null) {
            mItemRect = new Rect();
        }
        Rect itemRect = mItemRect;
        final int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            if (child instanceof ViewGroup && child.getVisibility() == VISIBLE) {
                child.getHitRect(itemRect);
                if (itemRect.contains(x, y)) {
                    mFlingView = (ViewGroup) child;
                    mPosition = firstPosition + i;
                    return;
                }
            }
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mFlingView != null) {
                mFlingView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
                postInvalidate();
            }
        }
    }
}
