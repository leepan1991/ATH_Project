package cn.innovativest.ath.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.CoinActive;
import cn.innovativest.ath.bean.ImgsItem;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.BaseResponse;
import cn.innovativest.ath.utils.LogUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class ShareTextAdapter extends BaseAdapter {
    private Context context;
    private List<ImgsItem> lstImgs;

    public ShareTextAdapter(Context context, List<ImgsItem> lstImgs) {
        this.context = context;
        this.lstImgs = lstImgs;
    }

    public void onRefresh(List<ImgsItem> lstImgs) {
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
                    R.layout.share_content_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        // 封装数据
        ImgsItem imgsItem = (ImgsItem) getItem(position);
        holder.tvwDesc.setText(imgsItem.text);

        return convertView;
    }

    static class ViewHolder {

        @BindView(R.id.tvwDesc)
        TextView tvwDesc;


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
