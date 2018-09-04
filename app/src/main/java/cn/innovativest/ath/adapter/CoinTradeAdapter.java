package cn.innovativest.ath.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.CoinTradeItem;

public class CoinTradeAdapter extends BaseAdapter {
    private Context context;
    private List<CoinTradeItem> listCoinTradeItems;

    public CoinTradeAdapter(Context context, List<CoinTradeItem> listCoinTradeItems) {
        this.context = context;
        this.listCoinTradeItems = listCoinTradeItems;
    }

    public void onRefresh(List<CoinTradeItem> listCoinTradeItems) {
        this.listCoinTradeItems = listCoinTradeItems;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listCoinTradeItems.size();
    }

    public void setCount(int count) {
        this.listCoinTradeItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listCoinTradeItems.get(position);
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
                    R.layout.coin_trade_management_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        // 封装数据
        CoinTradeItem coinTradeItem = (CoinTradeItem) getItem(position);

        holder.tvTradeName.setText(coinTradeItem.getOrder_id());
        holder.tvTradeTime.setText(coinTradeItem.getNeed_integral());
        //Status:1为下单成功  2为发送途中 3为完成订单 0为下单失败
        if (coinTradeItem.getStatus() == 0) {
            holder.tvTradeStatus.setText("下单失败");
        } else if (coinTradeItem.getStatus() == 1) {
            holder.tvTradeStatus.setText("下单成功");
        } else if (coinTradeItem.getStatus() == 2) {
            holder.tvTradeStatus.setText("发送途中");
        } else if (coinTradeItem.getStatus() == 3) {
            holder.tvTradeStatus.setText("完成订单");
        }
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tvTradeName)
        TextView tvTradeName;
        @BindView(R.id.tvTradeTime)
        TextView tvTradeTime;
        @BindView(R.id.tvTradeStatus)
        TextView tvTradeStatus;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
