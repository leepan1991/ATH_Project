package cn.innovativest.ath.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.GlideApp;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.OrderItem;
import cn.innovativest.ath.common.AppConfig;
import cn.innovativest.ath.utils.AppUtils;
import cn.innovativest.ath.utils.CUtils;

public class OrderAdapter extends BaseAdapter {
    private Context context;
    private List<OrderItem> listOrderItems;

    public OrderAdapter(Context context, List<OrderItem> listOrderItems) {
        this.context = context;
        this.listOrderItems = listOrderItems;
    }

    public void onRefresh(List<OrderItem> listOrderItems) {
        this.listOrderItems = listOrderItems;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listOrderItems.size();
    }

    public void setCount(int count) {
        this.listOrderItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listOrderItems.get(position);
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
                    R.layout.order_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }


        // 封装数据
        OrderItem orderItem = (OrderItem) getItem(position);

        if (!CUtils.isEmpty(orderItem.getHead_img_link())) {
            GlideApp.with(context).load(AppConfig.ATH_APP_URL + orderItem.getHead_img_link()).placeholder(R.drawable.mine_avatar).error(R.drawable.mine_avatar).optionalCircleCrop().into(holder.iv_avatar);
        } else {
            GlideApp.with(context).load("").placeholder(R.drawable.mine_avatar).error(R.drawable.mine_avatar).optionalCircleCrop().into(holder.iv_avatar);
        }

        holder.tvName.setText(TextUtils.isEmpty(orderItem.getName()) ? "" : orderItem.getName());
        if (orderItem.getState().equals("0")) {//取消
            holder.tvOrderDesc.setText("订单已取消，如有疑问，请联系客服！");
            holder.tvOrderStatus.setText("已取消");
        } else if (orderItem.getState().equals("1")) {//待付款
            holder.tvOrderDesc.setText("您已成功下单，请及时支付！");
            holder.tvOrderStatus.setText("未付款");
        } else if (orderItem.getState().equals("2")) {//已付款
            if (orderItem.getType() == 1) {//买入
                holder.tvOrderDesc.setText("您已付款，您将收到对方的出售！");
            } else if (orderItem.getType() == 2) {//卖出
                holder.tvOrderDesc.setText("对方已付款，对方将收到你的出售！");
            }
            holder.tvOrderStatus.setText("已付款");
        } else if (orderItem.getState().equals("3")) {//已完成
            holder.tvOrderDesc.setText("订单已完成！");
            holder.tvOrderStatus.setText("已完成");
        }
        if (orderItem.getType() == 1) {
            holder.tvTradeMethod.setText("买入：" + String.format("%.6f", Float.parseFloat(orderItem.getAth_number())) + "ATH");
        } else if (orderItem.getType() == 2) {
            holder.tvTradeMethod.setText("卖出：" + String.format("%.6f", Float.parseFloat(orderItem.getAth_number())) + "ATH");
        }

        holder.tvTradeAmount.setText("交易金额：" + String.format("%.2f", Float.parseFloat(orderItem.getAth_price()) * Float.parseFloat(orderItem.getAth_number())));
        holder.tvOrderDate.setText(AppUtils.formatTimeToFormat("yyyy-MM-dd HH:mm:ss", orderItem.getCreatime()));

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.iv_avatar)
        ImageView iv_avatar;
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.ivRealed)
        ImageView ivRealed;
        @BindView(R.id.tvOrderDesc)
        TextView tvOrderDesc;
        @BindView(R.id.tvTradeMethod)
        TextView tvTradeMethod;
        @BindView(R.id.tvOrderStatus)
        TextView tvOrderStatus;
        @BindView(R.id.tvTradeAmount)
        TextView tvTradeAmount;
        @BindView(R.id.tvOrderDate)
        TextView tvOrderDate;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
