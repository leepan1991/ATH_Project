package cn.innovativest.ath.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.GlideApp;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.ReleaseItem;
import cn.innovativest.ath.common.AppConfig;
import cn.innovativest.ath.utils.CUtils;

public class ReleaseAdapter extends BaseAdapter {
    private Context context;
    private List<ReleaseItem> listTradeItems;

    public ReleaseAdapter(Context context, List<ReleaseItem> listTradeItems) {
        this.context = context;
        this.listTradeItems = listTradeItems;
    }

    public void onRefresh(List<ReleaseItem> listTradeItems) {
        this.listTradeItems = listTradeItems;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listTradeItems.size();
    }

    public void setCount(int count) {
        this.listTradeItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listTradeItems.get(position);
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
                    R.layout.trade_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        // 封装数据
        ReleaseItem tradeItem = (ReleaseItem) getItem(position);

        if (!CUtils.isEmpty(tradeItem.getHead_img_link())) {
            GlideApp.with(context).load(AppConfig.ATH_APP_URL + tradeItem.getHead_img_link()).placeholder(R.drawable.mine_avatar).error(R.drawable.mine_avatar).optionalCircleCrop().into(holder.iv_avatar);
        } else {
            GlideApp.with(context).load("").placeholder(R.drawable.mine_avatar).error(R.drawable.mine_avatar).optionalCircleCrop().into(holder.iv_avatar);
        }

        holder.tvName.setText(TextUtils.isEmpty(tradeItem.getName()) ? tradeItem.getPhone() : tradeItem.getName());

        holder.ivRealed.setVisibility(View.INVISIBLE);

        holder.tvTradeNumber.setText(tradeItem.getSuccessful_order_number());

        if (!CUtils.isEmpty(tradeItem.getBank())) {
            holder.btnBank.setVisibility(View.VISIBLE);
        } else {
            holder.btnBank.setVisibility(View.GONE);
        }

        if (!CUtils.isEmpty(tradeItem.getWechat())) {
            holder.btnWechat.setVisibility(View.VISIBLE);
        } else {
            holder.btnWechat.setVisibility(View.GONE);
        }

        if (!CUtils.isEmpty(tradeItem.getAlipay())) {
            holder.btnAlipay.setVisibility(View.VISIBLE);
        } else {
            holder.btnAlipay.setVisibility(View.GONE);
        }

        holder.tvTradePrice.setText(String.format("%.2f", Float.parseFloat(tradeItem.getCny())) + " CNY");
        if (!CUtils.isEmpty(tradeItem.getQuota()) && tradeItem.getQuota().contains(",")) {
            holder.tvTradeLimit.setText(tradeItem.getQuota().replace(",", " - "));
        }
        holder.tvTradeAmount.setText(tradeItem.getNumber());

        if (tradeItem.getStatus().equals("1")) {
            holder.tvName.setTextColor(Color.GRAY);
            holder.tvNotice.setTextColor(Color.GRAY);
            holder.tvTradeNumber.setTextColor(Color.GRAY);
            holder.btnBank.setTextColor(Color.GRAY);
            holder.btnWechat.setTextColor(Color.GRAY);
            holder.btnAlipay.setTextColor(Color.GRAY);
            holder.tvTradePrice.setTextColor(Color.GRAY);
            holder.tvwL.setTextColor(Color.GRAY);
            holder.tvTradeLimit.setTextColor(Color.GRAY);
            holder.tvwS.setTextColor(Color.GRAY);
            holder.tvTradeAmount.setTextColor(Color.GRAY);
        } else {
            holder.tvName.setTextColor(Color.parseColor("#F08519"));
            holder.tvNotice.setTextColor(Color.parseColor("#666666"));
            holder.tvTradeNumber.setTextColor(Color.parseColor("#666666"));
            holder.btnBank.setTextColor(Color.parseColor("#666666"));
            holder.btnWechat.setTextColor(Color.parseColor("#666666"));
            holder.btnAlipay.setTextColor(Color.parseColor("#666666"));
            holder.tvTradePrice.setTextColor(Color.parseColor("#F08519"));
            holder.tvwL.setTextColor(Color.parseColor("#666666"));
            holder.tvTradeLimit.setTextColor(Color.parseColor("#666666"));
            holder.tvwS.setTextColor(Color.parseColor("#666666"));
            holder.tvTradeAmount.setTextColor(Color.parseColor("#666666"));
        }

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.iv_avatar)
        ImageView iv_avatar;
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.ivRealed)
        ImageView ivRealed;
        @BindView(R.id.tvTradeNumber)
        TextView tvTradeNumber;
        @BindView(R.id.btnBank)
        Button btnBank;
        @BindView(R.id.btnWechat)
        Button btnWechat;
        @BindView(R.id.btnAlipay)
        Button btnAlipay;
        @BindView(R.id.tvTradePrice)
        TextView tvTradePrice;
        @BindView(R.id.tvTradeLimit)
        TextView tvTradeLimit;
        @BindView(R.id.tvTradeAmount)
        TextView tvTradeAmount;

        @BindView(R.id.tvNotice)
        TextView tvNotice;

        @BindView(R.id.tvwL)
        TextView tvwL;

        @BindView(R.id.tvwS)
        TextView tvwS;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
