package cn.innovativest.ath.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.CoinActive;
import cn.innovativest.ath.bean.ImgsItem;
import cn.innovativest.ath.bean.UserInfo;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.BaseResponse;
import cn.innovativest.ath.utils.AESUtils;
import cn.innovativest.ath.utils.LogUtils;
import cn.innovativest.ath.utils.PrefsManager;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class ShareTextAdapter extends BaseAdapter {
    private Context context;
    private List<ImgsItem> lstImgs;
    Handler handler;

    public ShareTextAdapter(Context context, List<ImgsItem> lstImgs, Handler handler) {
        this.context = context;
        this.lstImgs = lstImgs;
        this.handler = handler;
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
        ImgsItem imgsItem1 = new Gson().fromJson(PrefsManager.get().getString("share"), ImgsItem.class);
        if (imgsItem1 == null) {
            if (position == 0) {
                PrefsManager.get().save("share", App.get().gson.toJson(imgsItem));
                holder.tvwDesc.setTextColor(context.getResources().getColor(android.R.color.holo_blue_bright));
                holder.lltContent.setBackground(context.getResources().getDrawable(R.drawable.share_btn_bg));
                handler.sendEmptyMessage(1);
            } else {
                holder.tvwDesc.setTextColor(Color.parseColor("#C4C3C1"));
                holder.lltContent.setBackground(context.getResources().getDrawable(R.drawable.nor_white));
            }
        } else {
            if (imgsItem1.text.equals(imgsItem.text)) {
                holder.lltContent.setBackground(context.getResources().getDrawable(R.drawable.share_btn_bg));
                holder.tvwDesc.setTextColor(context.getResources().getColor(android.R.color.holo_blue_bright));
            } else {
                holder.tvwDesc.setTextColor(Color.parseColor("#C4C3C1"));
                holder.lltContent.setBackground(context.getResources().getDrawable(R.drawable.nor_white));
            }
        }
        holder.tvwDesc.setText(imgsItem.text);

        return convertView;
    }

    static class ViewHolder {

        @BindView(R.id.tvwDesc)
        TextView tvwDesc;

        @BindView(R.id.lltContent)
        LinearLayout lltContent;


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
