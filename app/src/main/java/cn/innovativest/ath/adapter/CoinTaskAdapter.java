package cn.innovativest.ath.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.CoinActive;
import cn.innovativest.ath.bean.UserInfo;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.BaseResponse;
import cn.innovativest.ath.ui.act.FriendHelpAct;
import cn.innovativest.ath.ui.act.ShareAct;
import cn.innovativest.ath.utils.AESUtils;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.LogUtils;
import cn.innovativest.ath.utils.PrefsManager;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class CoinTaskAdapter extends BaseAdapter {
    private Context context;
    private List<CoinActive> listActives;
    private Handler handler;

    public CoinTaskAdapter(Context context, List<CoinActive> listActives, android.os.Handler handler) {
        this.context = context;
        this.listActives = listActives;
        this.handler = handler;
    }

    public void onRefresh(List<CoinActive> listActives) {
        this.listActives = listActives;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listActives.size();
    }

    public void setCount(int count) {
        this.listActives.size();
    }

    @Override
    public Object getItem(int position) {
        return listActives.get(position);
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
                    R.layout.coin_task_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        // 封装数据
        CoinActive coinActive = (CoinActive) getItem(position);

        holder.btnTop.setText(coinActive.val);
        holder.tvwDesc.setText(coinActive.text);

        if (coinActive.state == 0) {
            holder.ivGet.setEnabled(false);
            holder.ivGet.setImageResource(R.drawable.ic_coin_linqu_disable);
        } else if (coinActive.state == 1) {
            holder.ivGet.setEnabled(true);
            holder.ivGet.setImageResource(R.drawable.ic_coin_linqu_enable);
        }
        holder.ivGet.setTag(coinActive);
        holder.ivGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CoinActive coinActiveTemp = (CoinActive) view.getTag();
                if (!TextUtils.isEmpty(coinActiveTemp.type) && "22".equals(coinActiveTemp.type)) {
                    if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
                        UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
                        context.startActivity(new Intent(context, ShareAct.class).putExtra("shareCode", userInfo.code));
                    }

                } else {
                    draw_tops(coinActiveTemp.id);
                }
            }
        });

        return convertView;
    }

    private void draw_tops(String id) {

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("id", id);
        AthService service = App.get().getAthService();
        service.draw_tops(map).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse baseResponse) {
                LogUtils.e(baseResponse.message);
                App.toast(context, baseResponse.message);
                if (baseResponse.status == 1) {
                    handler.sendEmptyMessage(2);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage().toString());
            }
        });
    }

    static class ViewHolder {
        @BindView(R.id.btnTop)
        Button btnTop;
        @BindView(R.id.tvwDesc)
        TextView tvwDesc;
        @BindView(R.id.ivGet)
        ImageView ivGet;


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
