package cn.innovativest.ath.adapter;

import android.content.Context;
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
import cn.innovativest.ath.bean.CoinActive;
import cn.innovativest.ath.bean.CoinItem;
import cn.innovativest.ath.bean.CoinTop;
import cn.innovativest.ath.common.AppConfig;
import cn.innovativest.ath.utils.CUtils;

public class CoinTeamAdapter extends BaseAdapter {
    private Context context;
    private List<CoinTop> listCoinTops;

    public CoinTeamAdapter(Context context, List<CoinTop> listCoinTops) {
        this.context = context;
        this.listCoinTops = listCoinTops;
    }

    public void onRefresh(List<CoinTop> listCoinTops) {
        this.listCoinTops = listCoinTops;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listCoinTops.size();
    }

    public void setCount(int count) {
        this.listCoinTops.size();
    }

    @Override
    public Object getItem(int position) {
        return listCoinTops.get(position);
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
                    R.layout.coin_team_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        // 封装数据
        CoinTop coinTop = (CoinTop) getItem(position);
        if (position == 0) {
            holder.ivTop.setVisibility(View.VISIBLE);
            holder.tvwTop.setVisibility(View.GONE);
            holder.ivTop.setImageResource(R.drawable.ic_coin_one);
        } else if (position == 1) {
            holder.ivTop.setVisibility(View.VISIBLE);
            holder.tvwTop.setVisibility(View.GONE);
            holder.ivTop.setImageResource(R.drawable.ic_coin_two);
        } else if (position == 2) {
            holder.ivTop.setVisibility(View.VISIBLE);
            holder.tvwTop.setVisibility(View.GONE);
            holder.ivTop.setImageResource(R.drawable.ic_coin_three);
        } else {
            holder.ivTop.setVisibility(View.GONE);
            holder.tvwTop.setVisibility(View.VISIBLE);
            holder.tvwTop.setText(position + 1 + "");
        }

        holder.tvwTitle.setText(coinTop.jibie);
        holder.tvwPhone.setText(coinTop.phone);
        holder.tvwMoney.setText(coinTop.rmb);

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.ivTop)
        ImageView ivTop;
        @BindView(R.id.tvwTop)
        TextView tvwTop;
        @BindView(R.id.tvwTitle)
        TextView tvwTitle;
        @BindView(R.id.tvwPhone)
        TextView tvwPhone;
        @BindView(R.id.tvwMoney)
        TextView tvwMoney;


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
