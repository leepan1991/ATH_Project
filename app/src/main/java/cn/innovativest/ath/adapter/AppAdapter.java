package cn.innovativest.ath.adapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.GlideApp;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.AppItem;
import cn.innovativest.ath.common.AppConfig;

public class AppAdapter extends BaseAdapter {
    private Context context;
    private List<AppItem> listAppItems;

    public AppAdapter(Context context, List<AppItem> listAppItems) {
        this.context = context;
        this.listAppItems = listAppItems;
    }

    public void onRefresh(List<AppItem> listAppItems) {
        this.listAppItems = listAppItems;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listAppItems.size();
    }

    public void setCount(int count) {
        this.listAppItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listAppItems.get(position);
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
                    R.layout.app_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        // 封装数据
        AppItem appItem = (AppItem) getItem(position);

        GlideApp.with(context).load(AppConfig.ATH_APP_URL + appItem.img).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(holder.iv_logo);
        holder.tvwName.setText(appItem.name);
        if (isInstallApp(context, appItem.schemes)) {
            holder.btnFun.setText("打开");
        } else {
            holder.btnFun.setText("安装");
        }
        return convertView;
    }

    public static boolean isInstallApp(Context context, String pname) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName.toLowerCase(Locale.ENGLISH);
                if (pn.equals(pname)) {
                    return true;
                }
            }
        }
        return false;
    }

    static class ViewHolder {
        @BindView(R.id.iv_logo)
        ImageView iv_logo;
        @BindView(R.id.tvwName)
        TextView tvwName;
        @BindView(R.id.btnFun)
        Button btnFun;


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
