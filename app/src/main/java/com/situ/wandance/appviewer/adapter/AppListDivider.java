package com.situ.wandance.appviewer.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import com.situ.wandance.appviewer.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Create by xukai03
 * Date:2021/2/3
 * Description:
 */
public class AppListDivider extends RecyclerView.ItemDecoration {

    private static final int dividerH = 2;
    private int leftOffSet;
    private Paint mPaint;

    public AppListDivider(Context context) {
        leftOffSet = (int) (context.getResources().getDisplayMetrics().density * 22);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(context.getResources().getColor(R.color.colorGray));
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.bottom = dividerH;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int itemCount = parent.getChildCount();
        final int left = parent.getPaddingLeft() + leftOffSet;
        final int right = parent.getWidth() - parent.getPaddingRight() - leftOffSet;
        for (int i = 0; i < itemCount - 1; i++) {
            View child = parent.getChildAt(i);
            if (child == null) return;
            final int top = child.getBottom() + parent.getPaddingBottom();
            final int bottom = top + dividerH;
            c.drawRect(left, top, right, bottom, mPaint);
        }
    }
}
