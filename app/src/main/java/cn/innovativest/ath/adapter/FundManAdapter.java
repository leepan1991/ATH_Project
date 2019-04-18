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
import cn.innovativest.ath.bean.FundManItem;
import cn.innovativest.ath.utils.CUtils;

public class FundManAdapter extends BaseAdapter {
    private Context context;
    private List<FundManItem> lstFunManItems;

    public FundManAdapter(Context context, List<FundManItem> lstFunManItems) {
        this.context = context;
        this.lstFunManItems = lstFunManItems;
    }

    public void onRefresh(List<FundManItem> lstFunManItems) {
        this.lstFunManItems = lstFunManItems;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return lstFunManItems.size();
    }

    public void setCount(int count) {
        this.lstFunManItems.size();
    }

    @Override
    public Object getItem(int position) {
        return lstFunManItems.get(position);
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
        FundManItem fundManItem = (FundManItem) getItem(position);

        if (!CUtils.isEmpty(fundManItem.get_crowd_funding_text.getImgLink()) && fundManItem.get_crowd_funding_text.getImgLink().contains("|")) {
            GlideApp.with(context).load(fundManItem.get_crowd_funding_text.getImgLink().substring(fundManItem.get_crowd_funding_text.getImgLink().indexOf("|"))).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(holder.iv_avatar);
        } else {
            GlideApp.with(context).load(fundManItem.get_crowd_funding_text.getImgLink()).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(holder.iv_avatar);
        }

        holder.tvName.setText(fundManItem.get_crowd_funding_text.getTitle());

        holder.btnStartZc.setText("200起投");
        holder.tvAmount.setText(fundManItem.target_rmb);
        holder.tvStatus.setText(fundManItem.status);
        holder.tvCompleted.setText(fundManItem.reach_rmb);

        holder.circle_progress.setProgress(fundManItem.bai_fen_bi);

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
