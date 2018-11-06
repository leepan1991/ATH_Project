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
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.CoinTop;
import cn.innovativest.ath.bean.PropertyInfo;
import cn.innovativest.ath.bean.PropertyItem;
import cn.innovativest.ath.utils.AppUtils;

public class PropertyAdapter extends BaseAdapter {
    private Context context;
    private List<PropertyItem> listPropertyItems;

    public PropertyAdapter(Context context, List<PropertyItem> listPropertyItems) {
        this.context = context;
        this.listPropertyItems = listPropertyItems;
    }

    public void onRefresh(List<PropertyItem> listPropertyItems) {
        this.listPropertyItems = listPropertyItems;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listPropertyItems.size();
    }

    public void setCount(int count) {
        this.listPropertyItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listPropertyItems.get(position);
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
                    R.layout.property_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        // 封装数据
        PropertyItem propertyItem = (PropertyItem) getItem(position);

        holder.tvwAddress.setText(propertyItem.recharge);
        holder.tvwAth.setText(propertyItem.ath);
        holder.tvwScore.setText(propertyItem.integral);
        holder.tvwPhone.setText(propertyItem.type);

        holder.tvwTime.setText(AppUtils.formatTimeToFormat("yyyy/MM/dd", propertyItem.time));

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tvwAddress)
        TextView tvwAddress;
        @BindView(R.id.tvwAth)
        TextView tvwAth;
        @BindView(R.id.tvwScore)
        TextView tvwScore;
        @BindView(R.id.tvwPhone)
        TextView tvwPhone;
        @BindView(R.id.tvwTime)
        TextView tvwTime;


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
