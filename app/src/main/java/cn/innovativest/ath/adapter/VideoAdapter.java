package cn.innovativest.ath.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.GlideApp;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.VideoItem;

public class VideoAdapter extends BaseAdapter {
    private Context context;
    private List<VideoItem> lstImgs;

    public VideoAdapter(Context context, List<VideoItem> lstImgs) {
        this.context = context;
        this.lstImgs = lstImgs;
    }

    public void onRefresh(List<VideoItem> lstImgs) {
        this.lstImgs = lstImgs;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return lstImgs.size();
    }

    public void setCount(int count) {
        this.lstImgs.size();
    }

    @Override
    public Object getItem(int position) {
        return lstImgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_video, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        // 封装数据
        VideoItem path = (VideoItem) getItem(position);

        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path.path, MediaStore.Video.Thumbnails.MICRO_KIND);

        GlideApp.with(context).load(bitmap).placeholder(R.drawable.ic_fund_addvideo).error(R.drawable.ic_fund_addvideo).into(holder.ivImg);

        if (path.isVideo) {
            holder.ivIcon.setVisibility(View.VISIBLE);
        } else {
            holder.ivIcon.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }


    static class ViewHolder {
        @BindView(R.id.ivImg)
        ImageView ivImg;

        @BindView(R.id.ivIcon)
        ImageView ivIcon;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
