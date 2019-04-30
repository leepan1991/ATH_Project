package cn.innovativest.ath.adapter;

import android.content.Context;
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

public class ImgAdapter extends BaseAdapter {
    private Context context;
    private List<String> lstImgs;

    public ImgAdapter(Context context, List<String> lstImgs) {
        this.context = context;
        this.lstImgs = lstImgs;
    }

    public void onRefresh(List<String> lstImgs) {
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
                    R.layout.item_img, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        // 封装数据
        String path = (String) getItem(position);

        GlideApp.with(context).load(path).placeholder(R.drawable.ic_fund_addimg).error(R.drawable.ic_fund_addimg).into(holder.ivImg);
        return convertView;
    }


    static class ViewHolder {
        @BindView(R.id.ivImg)
        ImageView ivImg;


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
