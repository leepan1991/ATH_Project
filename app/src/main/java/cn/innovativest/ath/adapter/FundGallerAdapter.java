package cn.innovativest.ath.adapter;

import android.content.Context;
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
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.FundGallery;

public class FundGallerAdapter extends BaseAdapter {
    private Context context;
    private List<FundGallery> lstFundGallerys;

    public FundGallerAdapter(Context context, List<FundGallery> lstFundGallerys) {
        this.context = context;
        this.lstFundGallerys = lstFundGallerys;
    }

    public void onRefresh(List<FundGallery> lstFundGallerys) {
        this.lstFundGallerys = lstFundGallerys;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return lstFundGallerys.size();
    }

    public void setCount(int count) {
        this.lstFundGallerys.size();
    }

    @Override
    public Object getItem(int position) {
        return lstFundGallerys.get(position);
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
                    R.layout.fund_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        // 封装数据
        FundGallery fundGallery = (FundGallery) getItem(position);

//        GlideApp.with(context).load(AppConfig.ATH_APP_URL + fundGallery.getImg()).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(holder.iv_logo);
        holder.tvwName.setText(fundGallery.getName());

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.iv_logo)
        ImageView iv_logo;
        @BindView(R.id.tvwName)
        TextView tvwName;


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
