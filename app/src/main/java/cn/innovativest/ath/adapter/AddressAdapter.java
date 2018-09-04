package cn.innovativest.ath.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.Address;

public class AddressAdapter extends BaseAdapter {
    private List<Address> lstAddress = null;
    private Context mContext = null;
    private IOnItemEditClickListener mEdtListener = null;
    private IOnItemDelClickListener mDelListener = null;
    private int mRightWidth = 0;

    public AddressAdapter(Context paramContext, List<Address> paramList,
                          int paramInt,
                          IOnItemEditClickListener mEdtListener, IOnItemDelClickListener mDelListener) {
        this.mContext = paramContext;
        this.mRightWidth = paramInt;
        this.mEdtListener = mEdtListener;
        this.mDelListener = mDelListener;
        this.lstAddress = paramList;
    }

    public int getCount() {
        return this.lstAddress.size();
    }

    public Object getItem(int paramInt) {
        return this.lstAddress.get(paramInt);
    }

    public long getItemId(int paramInt) {
        return paramInt;
    }

    public View getView(final int paramInt, View convertView,
                        ViewGroup paramViewGroup) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.address_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }


        // 封装数据
        Address address = (Address) getItem(paramInt);

        holder.tvAddressOrder
                .setText("地址" + String.valueOf(paramInt + 1));
        holder.tvAddress
                .setText(address.site);
        holder.tvName.setText(address.name);

        LinearLayout.LayoutParams localLayoutParams1 = new LinearLayout.LayoutParams(
                -1, -1);
        holder.item_left.setLayoutParams(localLayoutParams1);
        LinearLayout.LayoutParams localLayoutParams2 = new LinearLayout.LayoutParams(
                this.mRightWidth, -1);
        holder.item_right.setLayoutParams(localLayoutParams2);
        holder.item_right_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AddressAdapter.this.mEdtListener != null)
                    AddressAdapter.this.mEdtListener.onRightClick(
                            view, paramInt);
            }
        });
        holder.item_right_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AddressAdapter.this.mDelListener != null)
                    AddressAdapter.this.mDelListener.onRightClick(
                            view, paramInt);
            }
        });
        return convertView;

    }

    public interface IOnItemEditClickListener {
        void onRightClick(View paramView, int paramInt);
    }

    public interface IOnItemDelClickListener {
        void onRightClick(View paramView, int paramInt);
    }

    static class ViewHolder {
        @BindView(R.id.tvAddressOrder)
        TextView tvAddressOrder;
        @BindView(R.id.tvAddress)
        TextView tvAddress;
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.item_left)
        View item_left;
        @BindView(R.id.item_right)
        View item_right;
        @BindView(R.id.item_right_edit)
        TextView item_right_edit;
        @BindView(R.id.item_right_del)
        TextView item_right_del;


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
