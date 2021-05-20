package com.situ.wandance.appviewer.views.useless;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

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
public class SlideRecyclerView extends RecyclerView {

    private static final int INVALID_POSITION = -1; // 触摸到的点不在子View范围内
    private static final int INVALID_CHILD_WIDTH = -1;  // 子ItemView不含两个子View
    private static final int SNAP_VELOCITY = 600;   // 最小滑动速度

    private VelocityTracker mVelocityTracker;   // 速度追踪器

    private int mTouchSlop; // 认为是滑动的最小距离（一般由系统提供）

    private Scroller mScroller;

    private Rect mItemRect;   // 子View所在的矩形范围

    private float mFirstX, mFirstY;
    private float mLastX;

    private ViewGroup mFlingView;   // 触碰的子View

    private int mPosition;
    private LinearLayoutManager mLayoutManager;


    private int mMenuViewWidth;    // 菜单按钮宽度
    private boolean mIsSlide; // 是否处于侧滑


    public SlideRecyclerView(Context context) {
        this(context, null);
    }

    public SlideRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
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
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mFirstX = mLastX = x;
                mFirstY = y;
                View proFlingView = mFlingView;
                calculateTouchPosition(x, y);
                if (mPosition != INVALID_POSITION && mFlingView != null) {
                    if (proFlingView != null && proFlingView != mFlingView && proFlingView.getScrollX() != 0) {
                        proFlingView.scrollTo(0, 0);
                    }
                    if (mFlingView.getChildCount() == 2) {
                        mMenuViewWidth = mFlingView.getChildAt(1).getWidth();
                    } else {
                        mMenuViewWidth = INVALID_CHILD_WIDTH;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.computeCurrentVelocity(1000);
                float xVelocity = mVelocityTracker.getXVelocity();
                float yVelocity = mVelocityTracker.getYVelocity();
                if (Math.abs(xVelocity) > SNAP_VELOCITY && Math.abs(xVelocity) > Math.abs(yVelocity) || Math.abs(x - mFirstX) >= mTouchSlop && Math.abs(x - mFirstX) > Math.abs(y - mFirstY)) {
                    mIsSlide = true;
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                releaseVelocity();
                break;
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (mIsSlide && mPosition != INVALID_POSITION) {
            float x = e.getX();
            obtainVelocity(e);
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mMenuViewWidth != INVALID_CHILD_WIDTH) {
                        float dx = mLastX - x;
                        if (mFlingView.getScrollX() + dx <= mMenuViewWidth && mFlingView.getScrollX() + dx > 0) {
                            mFlingView.scrollBy((int) dx, 0);
                        }
                        mLastX = x;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (mMenuViewWidth != INVALID_CHILD_WIDTH) {
                        int scrollX = mFlingView.getScrollX();
                        mVelocityTracker.computeCurrentVelocity(1000);
                        float xVelocity = mVelocityTracker.getXVelocity();
                        if (xVelocity < -SNAP_VELOCITY) { // 左滑速度大于最小临界速度，则继续滑动到最大
                            mScroller.startScroll(scrollX, 0, mMenuViewWidth - scrollX, 0, Math.abs(mMenuViewWidth - scrollX));
                        } else if (xVelocity >= SNAP_VELOCITY) { // 右滑达到最小临界速度，则继续滑动到关闭
                            mScroller.startScroll(scrollX, 0, -scrollX, 0, Math.abs(scrollX));
                        } else if (scrollX > mMenuViewWidth / 2) { // 左滑超控件一半，继续滑动到最大
                            mScroller.startScroll(scrollX, 0, mMenuViewWidth - scrollX, 0, Math.abs(mMenuViewWidth - scrollX));
                        } else {
                            mScroller.startScroll(scrollX, 0, -scrollX, 0, Math.abs(scrollX));
                        }
                        invalidate();
                    }
                    mMenuViewWidth = INVALID_CHILD_WIDTH;
                    mIsSlide = false;
                    mPosition = INVALID_POSITION;
                    releaseVelocity();
                    break;
            }
            return true;
        } else {
            if (mFlingView != null && mFlingView.getScrollX() != 0) {
                mFlingView.scrollTo(0, 0);
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

    private void calculateTouchPosition(int x, int y) {
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
            if (child.getVisibility() == VISIBLE) {
                child.getHitRect(itemRect);
                if (itemRect.contains(x, y)) {
                    if (child instanceof ViewGroup) {
                        mFlingView = (ViewGroup) child;
                    }
                    mPosition = firstPosition + i;
                    return;
                }
            }
        }
        mFlingView = null;
        mPosition = INVALID_POSITION;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mFlingView != null) {
                mFlingView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
                invalidate();
            }
        }
    }
}
