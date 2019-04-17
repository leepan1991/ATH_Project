package cn.innovativest.ath.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.CircleProgress;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.GlideApp;
import cn.innovativest.ath.R;
import cn.innovativest.ath.common.AppConfig;
import cn.innovativest.ath.response.FundItem;
import cn.innovativest.ath.utils.CUtils;

public class FundAdapter extends BaseAdapter {
    private Context context;
    private List<FundItem> lstFundItems;

    public FundAdapter(Context context, List<FundItem> lstFundItems) {
        this.context = context;
        this.lstFundItems = lstFundItems;
    }

    public void onRefresh(List<FundItem> lstFundItems) {
        this.lstFundItems = lstFundItems;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return lstFundItems.size();
    }

    public void setCount(int count) {
        this.lstFundItems.size();
    }

    @Override
    public Object getItem(int position) {
        return lstFundItems.get(position);
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
                    R.layout.fund_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        // 封装数据
        FundItem fundItem = (FundItem) getItem(position);

        if (!CUtils.isEmpty(fundItem.getGetCrowdFundingText().getImgLink()) && fundItem.getGetCrowdFundingText().getImgLink().contains("|")) {
            GlideApp.with(context).load(fundItem.getGetCrowdFundingText().getImgLink().substring(fundItem.getGetCrowdFundingText().getImgLink().indexOf("|"))).placeholder(R.drawable.mine_avatar).error(R.drawable.mine_avatar).into(holder.iv_avatar);
        } else {
            GlideApp.with(context).load(fundItem.getGetCrowdFundingText().getImgLink()).placeholder(R.drawable.mine_avatar).error(R.drawable.mine_avatar).into(holder.iv_avatar);
        }

        holder.tvName.setText(fundItem.getGetCrowdFundingText().getTitle());

        holder.tvAmount.setText(fundItem.getTargetRmb() + "万");
        holder.tvStatus.setText(fundItem.getStatus());
        holder.tvCompleted.setText(fundItem.getReachRmb() + "万");

        holder.circle_progress.setProgress(fundItem.getBaiFenBi());

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.iv_avatar)
        ImageView iv_avatar;
        @BindView(R.id.tvName)
        TextView tvName;

        @BindView(R.id.btnStartZc)
        Button btnStartZc;

        @BindView(R.id.tvAmount)
        TextView tvAmount;

        @BindView(R.id.tvStatus)
        TextView tvStatus;

        @BindView(R.id.tvCompleted)
        TextView tvCompleted;

        @BindView(R.id.circle_progress)
        CircleProgress circle_progress;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
