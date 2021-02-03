package com.situ.wandance.appviewer.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.situ.wandance.appviewer.R;
import com.situ.wandance.appviewer.beans.AppInfo;
import com.situ.wandance.appviewer.utils.ComUtils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Create by xukai03
 * Date:2021/2/2
 * Description:
 */
public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppItemVh> {

    private Context mContext;
    private LayoutInflater mInflater;

    private ArrayList<AppInfo> data = new ArrayList<>();

    public AppListAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(this.mContext);
    }

    public void setData(ArrayList<AppInfo> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppItemVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AppItemVh(mContext, mInflater.inflate(R.layout.layout_item_app_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AppItemVh holder, int position) {
        holder.bindData(data.get(position));
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    static class AppItemVh extends RecyclerView.ViewHolder {

        private ImageView mIcon;
        private TextView mTitle;
        private TextView mVersion;

        private TextView mSub1;
        private TextView mSub2;

        private TextView mBtn1;
        private TextView mBtn2;

        private AppInfo info;
        private Context mCxt;

        public AppItemVh(Context context, @NonNull View itemView) {
            super(itemView);
            mCxt = context;
            mIcon = itemView.findViewById(R.id.icon);
            mTitle = itemView.findViewById(R.id.title);
            mSub1 = itemView.findViewById(R.id.sub1);
            mSub2 = itemView.findViewById(R.id.sub2);
            mVersion = itemView.findViewById(R.id.version);
            mBtn1 = itemView.findViewById(R.id.btn1);
            mBtn2 = itemView.findViewById(R.id.btn2);

            mSub1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (info != null) {
                        String message = info.toString();
                        ClipboardManager cm = (ClipboardManager) mCxt.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData mClipData = ClipData.newPlainText("AppInfo", message);
                        assert cm != null;
                        cm.setPrimaryClip(mClipData);
                        ComUtils.toast(mCxt, String.format("内容复制到粘贴板 >> [%s]", message));
                    }
                }
            });

            mSub2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (info != null) {
                        ComUtils.shareTextBySys(mCxt, info.toString());
                    }
                }
            });
        }

        void bindData(AppInfo info) {
            if (info == null) {
                return;
            }
            this.info = info;
            if (info.getIcon() != null) {
                mIcon.setImageDrawable(info.getIcon());
            }
            mTitle.setText(info.getName());
            mSub1.setText(info.getPackageName());
            mSub2.setText(String.format("签名（md5）:%s", info.getMd5()));
            mVersion.setText(String.format("（%s）", info.getVersionName()));
        }
    }
}
