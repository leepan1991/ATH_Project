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
import cn.innovativest.ath.bean.CoinItem;
import cn.innovativest.ath.common.AppConfig;
import cn.innovativest.ath.utils.CUtils;

public class CoinAdapter extends BaseAdapter {
    private Context context;
    private List<CoinItem> listCoinItems;

    public CoinAdapter(Context context, List<CoinItem> listCoinItems) {
        this.context = context;
        this.listCoinItems = listCoinItems;
    }

    public void onRefresh(List<CoinItem> listCoinItems) {
        this.listCoinItems = listCoinItems;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listCoinItems.size();
    }

    public void setCount(int count) {
        this.listCoinItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listCoinItems.get(position);
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
                    R.layout.coin_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        // 封装数据
        CoinItem coinItem = (CoinItem) getItem(position);

        if (!CUtils.isEmpty(coinItem.img)) {
            GlideApp.with(context).load(AppConfig.ATH_APP_URL + coinItem.img).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).optionalCircleCrop().into(holder.iv_avatar);
        } else {
            GlideApp.with(context).load("").placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(holder.iv_avatar);
        }

        holder.tvwKind.setText(coinItem.title);
        holder.tvCoinNumber.setText(coinItem.need_integral);

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.iv_avatar)
        ImageView iv_avatar;
        @BindView(R.id.tvwKind)
        TextView tvwKind;
        @BindView(R.id.tvCoinNumber)
        TextView tvCoinNumber;


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
