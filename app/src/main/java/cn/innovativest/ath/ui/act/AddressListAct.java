package cn.innovativest.ath.ui.act;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.adapter.AddressAdapter;
import cn.innovativest.ath.bean.Address;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.AddressResponse;
import cn.innovativest.ath.response.BaseResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.AppUtils;
import cn.innovativest.ath.utils.LoadingUtils;
import cn.innovativest.ath.utils.PrefsManager;
import cn.innovativest.ath.widget.CustomDialog;
import cn.innovativest.ath.widget.SwipeListView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by leepan on 21/03/2018.
 */

public class AddressListAct extends BaseAct implements AdapterView.OnItemClickListener {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;
    @BindView(R.id.lltNotHaveAddress)
    LinearLayout lltNotHaveAddress;

    @BindView(R.id.lltAddress)
    LinearLayout lltAddress;

    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;

    @BindView(R.id.address_listview)
    SwipeListView address_listview;

    private List<Address> lstAddress;
    private AddressAdapter addressAdapter;

    private CustomDialog mDialog;

    boolean mIsRefreshing;

    private int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_list_act);
        ButterKnife.bind(this);
        if (getIntent().hasExtra("type")) {
            type = getIntent().getIntExtra("type", -1);
        }
        initView();
        request();
    }

    private void initView() {
        mDialog = new CustomDialog(mCtx);

        btnBack.setVisibility(View.VISIBLE);
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("收货地址");
        btnBack.setOnClickListener(this);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mIsRefreshing) {
                    swipeRefresh.setRefreshing(false);
                    return;
                }
                request();
            }
        });

        lltAddress.setOnClickListener(this);
        lstAddress = new ArrayList<Address>();
        address_listview.setRightViewWidth(AppUtils.dip2px(AddressListAct.this, 200));
        addressAdapter = new AddressAdapter(this, lstAddress, AppUtils.dip2px(AddressListAct.this, 200),
                new AddressAdapter.IOnItemEditClickListener() {
                    public void onRightClick(View paramAnonymousView,
                                             int paramAnonymousInt) {
                        startActivityForResult(new Intent(AddressListAct.this, AddAddressAct.class).putExtra("flag", "1").putExtra("address", lstAddress.get(paramAnonymousInt)), 200);
                        address_listview.hiddenRightWithoutAnim(paramAnonymousInt);
                    }
                },

                new AddressAdapter.IOnItemDelClickListener() {
                    @Override
                    public void onRightClick(View paramView, final int paramInt) {
                        mDialog.setIsCancelable(true).setMsg("确定删除该地址吗？")
                                .setChooseListener(new CustomDialog.ChooseListener() {

                                    @Override
                                    public void onChoose(int which) {
                                        if (which == WHICH_RIGHT) {
                                            delAddress(lstAddress.get(paramInt));
                                            address_listview.hiddenRightWithoutAnim(paramInt);
                                        }
                                    }
                                }).show();

                    }
                }
        );
        address_listview.setAdapter(addressAdapter);
        address_listview.setOnItemClickListener(this);
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    mIsRefreshing = false;
                    if (swipeRefresh.isRefreshing()) {
                        swipeRefresh.setRefreshing(false);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void request() {
        //获取数据
        if (mIsRefreshing) {
            return;
        }
        swipeRefresh.setRefreshing(true);
        mIsRefreshing = true;
        getAddressList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("AddressListAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("AddressListAct");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void delAddress(final Address address) {

        AthService service = App.get().getAthService();
        service.site_del(address.id + "").observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse baseResponse) {
                LoadingUtils.getInstance().dialogDismiss();
                if (baseResponse != null) {
                    if (App.selectAddress != null) {
                        if (App.selectAddress.id == address.id) {
                            PrefsManager.get().save("address", "");
                            App.selectAddress = null;
                        }
                    }
                    lstAddress.remove(address);
                    addressAdapter.notifyDataSetChanged();
                    App.toast(AddressListAct.this, baseResponse.message);
                } else {
                    App.toast(AddressListAct.this, "删除失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
            }
        });

//        new HttpTask(AddressAct.this, Config.Urls.public_user_deleteAddress)
//                .setMethod(HttpMethod.POST)
//                .addBodyParameter("session_id", App.get().user.id)
//                .addBodyParameter("id", address.id + "")
//                .addResponseListener(new HttpTask.ResponseListener() {
//                                         @Override
//                                         public void onSuccess(Object data, String msg) throws Exception {
//                                             if (App.selectAddress != null) {
//                                                 if (App.selectAddress.id == address.id) {
//                                                     PrefsManager.get().save("address", "");
//                                                     App.selectAddress = null;
//                                                 }
//                                             }
//                                             lstAddress.remove(address);
//                                             addressAdapter.notifyDataSetChanged();
//                                         }
//
//                                         @Override
//                                         public void onFailed(int code, String msg) {
//
//                                         }
//                                     }
//
//                ).start();
    }

    private void getAddressList() {

        AthService service = App.get().getAthService();
        service.site().observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<AddressResponse>() {
            @Override
            public void call(AddressResponse addressResponse) {
                LoadingUtils.getInstance().dialogDismiss();
                if (addressResponse != null) {
                    if (addressResponse.lstAddress != null && addressResponse.lstAddress.size() > 0) {
                        lstAddress.clear();
                        lstAddress.addAll(addressResponse.lstAddress);
                        addressAdapter.notifyDataSetChanged();
                        lltNotHaveAddress.setVisibility(View.GONE);
                    } else {
                        lltNotHaveAddress.setVisibility(View.VISIBLE);
                        App.toast(AddressListAct.this, addressResponse.message);
                    }
                } else {
                    App.toast(AddressListAct.this, "数据获取失败");
                }
                handler.sendEmptyMessage(1);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LoadingUtils.getInstance().dialogDismiss();
                handler.sendEmptyMessage(1);
                App.toast(AddressListAct.this, "添加失败");
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.lltAddress:
                startActivityForResult(new Intent(AddressListAct.this, AddAddressAct.class).putExtra("flag", "0"), 100);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
            case 200:
                if (resultCode == RESULT_OK) {
                    request();
                }
                break;

        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (type == 1) {
            Address tempAddress = lstAddress.get(i);
            PrefsManager.get().save("address", App.get().gson.toJson(tempAddress));
            setResult(RESULT_OK, new Intent().putExtra("address", tempAddress));
            finish();
        }
    }
}
