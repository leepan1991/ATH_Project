package cn.innovativest.ath.adapter;

import android.content.Context;
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
import cn.innovativest.ath.bean.TradeItem;
import cn.innovativest.ath.common.AppConfig;
import cn.innovativest.ath.utils.CUtils;

public class TradeAdapter extends BaseAdapter {
    private Context context;
    private List<TradeItem> listTradeItems;

    public TradeAdapter(Context context, List<TradeItem> listTradeItems) {
        this.context = context;
        this.listTradeItems = listTradeItems;
    }

    public void onRefresh(List<TradeItem> listTradeItems) {
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
        TradeItem tradeItem = (TradeItem) getItem(position);

        if (!CUtils.isEmpty(tradeItem.getHead_img_link())) {
            GlideApp.with(context).load(AppConfig.ATH_APP_URL + tradeItem.getHead_img_link()).placeholder(R.drawable.mine_avatar).error(R.drawable.mine_avatar).optionalCircleCrop().into(holder.iv_avatar);
        } else {
            GlideApp.with(context).load("").placeholder(R.drawable.mine_avatar).error(R.drawable.mine_avatar).optionalCircleCrop().into(holder.iv_avatar);
        }

        holder.tvName.setText(TextUtils.isEmpty(tradeItem.getName()) ? tradeItem.getPhone() : tradeItem.getName());

        if (CUtils.isEmpty(tradeItem.getId_number())) {
            holder.ivRealed.setVisibility(View.INVISIBLE);
        } else {
            holder.ivRealed.setVisibility(View.VISIBLE);
        }

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

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
