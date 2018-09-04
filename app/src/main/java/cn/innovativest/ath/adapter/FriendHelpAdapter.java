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
import cn.innovativest.ath.bean.FriendHelpItem;
import cn.innovativest.ath.utils.AppUtils;

public class FriendHelpAdapter extends BaseAdapter {
    private Context context;
    private List<FriendHelpItem> listFriendHelpItems;

    public FriendHelpAdapter(Context context, List<FriendHelpItem> listFriendHelpItems) {
        this.context = context;
        this.listFriendHelpItems = listFriendHelpItems;
    }

    public void onRefresh(List<FriendHelpItem> listFriendHelpItems) {
        this.listFriendHelpItems = listFriendHelpItems;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listFriendHelpItems.size();
    }

    public void setCount(int count) {
        this.listFriendHelpItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listFriendHelpItems.get(position);
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
                    R.layout.friend_help_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        // 封装数据
        FriendHelpItem friendHelpItem = (FriendHelpItem) getItem(position);

        holder.tvwPhone.setText(friendHelpItem.phone);
        holder.tvwTime.setText(AppUtils.formatTimeToFormat("yyyy-MM-dd", Long.parseLong(friendHelpItem.ctime)));
        holder.tvwPit.setText(friendHelpItem.pit);
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tvwPhone)
        TextView tvwPhone;
        @BindView(R.id.tvwTime)
        TextView tvwTime;
        @BindView(R.id.tvwPit)
        TextView tvwPit;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
