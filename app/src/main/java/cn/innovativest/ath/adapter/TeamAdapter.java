package cn.innovativest.ath.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.Gift;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.BaseResponse;
import cn.innovativest.ath.utils.LogUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class TeamAdapter extends BaseAdapter {
    private Context context;
    private List<Gift> listTeams;

    public TeamAdapter(Context context, List<Gift> listTeams) {
        this.context = context;
        this.listTeams = listTeams;
    }

    public void onRefresh(List<Gift> listTeams) {
        this.listTeams = listTeams;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listTeams.size();
    }

    public void setCount(int count) {
        this.listTeams.size();
    }

    @Override
    public Object getItem(int position) {
        return listTeams.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.team_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        // 封装数据
        Gift gift = (Gift) getItem(position);

        holder.tvwInfo.setText("达到" + gift.count + "积分，领取" + gift.val + "ATH");
        if (gift.state.equals("0")) {//已领取
            holder.btnGet.setBackgroundResource(R.drawable.geted);
            holder.btnGet.setText("已领取");
            holder.btnGet.setEnabled(false);
            holder.btnGet.setClickable(false);
        } else if (gift.state.equals("1")) {//未领取
            holder.btnGet.setBackgroundResource(R.drawable.team_get);
            holder.btnGet.setText("马上领取");
            holder.btnGet.setEnabled(true);
            holder.btnGet.setClickable(true);
        }
        holder.btnGet.setTag(gift);
        holder.btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gift tempGift = (Gift) view.getTag();
                getGift(holder, tempGift);
            }
        });
        return convertView;
    }

    private void getGift(final ViewHolder holder, Gift gift) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("id", gift.id);
        AthService service = App.get().getAthService();
        service.draw(map).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse baseResponse) {
                if (baseResponse != null) {
                    App.toast(context, baseResponse.message);
                    if (baseResponse.status == 1) {
                        holder.btnGet.setBackgroundResource(R.drawable.geted);
                        holder.btnGet.setText("已领取");
                    }
                } else {
                    App.toast(context, "操作失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage());
                App.toast(context, "操作失败");
            }
        });

    }

    static class ViewHolder {
        @BindView(R.id.tvwInfo)
        TextView tvwInfo;
        @BindView(R.id.btnGet)
        Button btnGet;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
