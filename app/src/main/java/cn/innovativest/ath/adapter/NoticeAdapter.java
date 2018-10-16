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
import cn.innovativest.ath.bean.Notice;
import cn.innovativest.ath.common.AppConfig;
import cn.innovativest.ath.utils.CUtils;

public class NoticeAdapter extends BaseAdapter {
    private Context context;
    private List<Notice> noticeList;

    public NoticeAdapter(Context context, List<Notice> noticeList) {
        this.context = context;
        this.noticeList = noticeList;
    }

    public void onRefresh(List<Notice> noticeList) {
        this.noticeList = noticeList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return noticeList.size();
    }

    public void setCount(int count) {
        this.noticeList.size();
    }

    @Override
    public Object getItem(int position) {
        return noticeList.get(position);
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
                    R.layout.notice_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        // 封装数据
        Notice notice = (Notice) getItem(position);

        holder.tvwTitle.setText(notice.exchange);

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tvwTitle)
        TextView tvwTitle;


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
