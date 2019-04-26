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
import cn.innovativest.ath.bean.Comment;
import cn.innovativest.ath.common.AppConfig;
import cn.innovativest.ath.utils.AppUtils;

public class CommentAdapter extends BaseAdapter {
    private Context context;
    private List<Comment> lstComments;

    public CommentAdapter(Context context, List<Comment> lstComments) {
        this.context = context;
        this.lstComments = lstComments;
    }

    public void onRefresh(List<Comment> lstComments) {
        this.lstComments = lstComments;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return lstComments.size();
    }

    public void setCount(int count) {
        this.lstComments.size();
    }

    @Override
    public Object getItem(int position) {
        return lstComments.get(position);
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
                    R.layout.comment_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        // 封装数据
        Comment comment = (Comment) getItem(position);

        GlideApp.with(context).load(AppConfig.ATH_APP_URL + comment.commentUser.head_img_link).placeholder(R.mipmap.ic_launcher).optionalCircleCrop().error(R.mipmap.ic_launcher).into(holder.iv_avatar);


        holder.tvName.setText(comment.commentUser.name);

        holder.tvOrderDesc.setText(AppUtils.formatTimeToFormat("yyyy-MM-dd HH:mm:ss", comment.time));
        holder.tvTradeAmount.setText(comment.text);

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.iv_avatar)
        ImageView iv_avatar;

        @BindView(R.id.tvName)
        TextView tvName;

        @BindView(R.id.tvOrderDesc)
        TextView tvOrderDesc;

        @BindView(R.id.tvTradeAmount)
        TextView tvTradeAmount;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
