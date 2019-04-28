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
import cn.innovativest.ath.bean.FundPart;
import cn.innovativest.ath.utils.AppUtils;

public class FundPartAdapter extends BaseAdapter {
    private Context context;
    private List<FundPart> lstFundParts;

    public FundPartAdapter(Context context, List<FundPart> lstFundParts) {
        this.context = context;
        this.lstFundParts = lstFundParts;
    }

    public void onRefresh(List<FundPart> lstFundParts) {
        this.lstFundParts = lstFundParts;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return lstFundParts.size();
    }

    public void setCount(int count) {
        this.lstFundParts.size();
    }

    @Override
    public Object getItem(int position) {
        return lstFundParts.get(position);
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
                    R.layout.fund_part_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        // 封装数据
        FundPart fundManItem = (FundPart) getItem(position);


        holder.tvName.setText(fundManItem.get_crowd_funding_text.getTitle());
        holder.tvAmount.setText(fundManItem.order_number);

        holder.tvMethod.setText(fundManItem.ali_number);

        holder.tvJine.setText(fundManItem.rmb);

        holder.tvCompleted.setText(AppUtils.formatTimeToFormat("yyyy-MM-dd hh:MM:ss", fundManItem.ctime));

        return convertView;
    }

    static class ViewHolder {

        @BindView(R.id.tvName)
        TextView tvName;

        @BindView(R.id.tvAmount)
        TextView tvAmount;

        @BindView(R.id.tvMethod)
        TextView tvMethod;

        @BindView(R.id.tvJine)
        TextView tvJine;

        @BindView(R.id.tvCompleted)
        TextView tvCompleted;


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
