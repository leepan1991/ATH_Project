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

import com.github.lzyzsd.circleprogress.CircleProgress;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.GlideApp;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.NewMainHot;
import cn.innovativest.ath.utils.CUtils;

public class MainHotAdapter extends BaseAdapter {
    private Context context;
    private List<NewMainHot> lstNewMainHots;

    public MainHotAdapter(Context context, List<NewMainHot> lstNewMainHots) {
        this.context = context;
        this.lstNewMainHots = lstNewMainHots;
    }

    public void onRefresh(List<NewMainHot> lstNewMainHots) {
        this.lstNewMainHots = lstNewMainHots;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return lstNewMainHots.size();
    }

    public void setCount(int count) {
        this.lstNewMainHots.size();
    }

    @Override
    public Object getItem(int position) {
        return lstNewMainHots.get(position);
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
                    R.layout.main_hot_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        // 封装数据
        NewMainHot newMainHot = (NewMainHot) getItem(position);

        if (!CUtils.isEmpty(newMainHot.newMainGetFundingText.video) && newMainHot.newMainGetFundingText.video.contains("|")) {
            GlideApp.with(context).load(newMainHot.newMainGetFundingText.video.substring(newMainHot.newMainGetFundingText.video.indexOf("|"))).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(holder.iv_avatar);
        } else {
            GlideApp.with(context).load(newMainHot.newMainGetFundingText.video).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(holder.iv_avatar);
        }

        holder.tvName.setText(newMainHot.newMainGetFundingText.title);

        holder.btnStartZc.setText(newMainHot.newMainGetFundingText.rmb + "起投");
        holder.tvAmount.setText(newMainHot.target_rmb);
        holder.tvStatus.setText(newMainHot.persons + "人参与");
        holder.tvCompleted.setText(newMainHot.reach_rmb);

        holder.circle_progress.setProgress(newMainHot.bai_fen_bi);
        holder.tvWatch.setText(newMainHot.newMainGetFundingText.view_num);

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.iv_avatar)
        ImageView iv_avatar;

        @BindView(R.id.iv_play)
        ImageView iv_play;

        @BindView(R.id.tvWatch)
        TextView tvWatch;

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
