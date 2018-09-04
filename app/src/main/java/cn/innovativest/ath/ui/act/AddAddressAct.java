package cn.innovativest.ath.ui.act;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.Address;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.entities.AddressBody;
import cn.innovativest.ath.response.BaseResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.LoadingUtils;
import cn.innovativest.ath.utils.PrefsManager;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by leepan on 21/03/2018.
 */

public class AddAddressAct extends BaseAct {

    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.tvwTitle)
    TextView tvwTitle;
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etPhoneNumber)
    EditText etPhoneNumber;
    @BindView(R.id.etPostcode)
    EditText etPostcode;
    @BindView(R.id.etDetailAddress)
    EditText etDetailAddress;
    @BindView(R.id.btnSave)
    Button btnSave;


//    private ImageButton btnBack;
//    private TextView tvwTitle;
//    private TextView tvwAction;
//
//    private EditText edtName;
//    private EditText edtPhone;
//    private RelativeLayout rltAdd;
//    private TextView edtAddress;
//    private TextView tvwSelect;
//    private EditText edtDetailAddress;
//
//    private Button btnSave;
//
//    private String address;
//    private String areaCode;

    private String flag;
    private Address modifyAddress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_address_act);
        ButterKnife.bind(this);
        flag = getIntent().getStringExtra("flag");
        if (!TextUtils.isEmpty(flag) && flag.equals("1")) {
            modifyAddress = (Address) getIntent().getSerializableExtra("address");
        }
        initView();
    }

    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        btnBack.setVisibility(View.VISIBLE);
        tvwTitle.setText("添加收货地址");
        btnBack.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        if (modifyAddress != null) {
            tvwTitle.setText("修改收货地址");
            etName.setText(modifyAddress.name);
            etName.setSelection(modifyAddress.name.length());
            etPhoneNumber.setText(modifyAddress.tel);
            etPhoneNumber.setSelection(modifyAddress.tel.length());
            etPostcode.setText(modifyAddress.dak);
            etPostcode.setSelection(modifyAddress.dak.length());

            etPostcode.setText(modifyAddress.dak);
            etPostcode.setSelection(modifyAddress.dak.length());

            etDetailAddress.setText(modifyAddress.site);
            etDetailAddress.setSelection(modifyAddress.site.length());
        }
    }

    private void addAddress() {
        if (CUtils.isEmpty(etName.getText().toString().trim())) {
            App.toast(AddAddressAct.this, "请输入收货人姓名");
            return;
        }
        if (CUtils.isEmpty(etPhoneNumber.getText().toString().trim())) {
            App.toast(AddAddressAct.this, "请输入收货人电话");
            return;
        }
        if (CUtils.isEmpty(etPostcode.getText().toString().trim())) {
            App.toast(AddAddressAct.this, "请输入收货人邮编");
            return;
        }
        if (CUtils.isEmpty(etDetailAddress.getText().toString().trim())) {
            App.toast(AddAddressAct.this, "请输入详细收货地址");
            return;
        }
        httpPost();
    }

    private void httpPost() {

        AddressBody addressBody = new AddressBody();
        addressBody.name = etName.getText().toString();
        addressBody.tel = etPhoneNumber.getText().toString().trim();
        addressBody.dak = etPostcode.getText().toString().trim();
        addressBody.site = etDetailAddress.getText().toString();

        if (modifyAddress != null) {
            addressBody.id = modifyAddress.id + "";
            AthService service = App.get().getAthService();
            service.site_edit(addressBody).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BaseResponse>() {
                @Override
                public void call(BaseResponse baseResponse) {
                    LoadingUtils.getInstance().dialogDismiss();
                    if (baseResponse.status == 1) {
                        if (modifyAddress != null) {
                            modifyAddress.name = etName.getText().toString();
                            modifyAddress.tel = etPhoneNumber.getText().toString().trim();
                            modifyAddress.dak = etPostcode.getText().toString().trim();
                            modifyAddress.site = etDetailAddress.getText().toString();
                            PrefsManager.get().save("address", App.get().gson.toJson(modifyAddress));
                            App.selectAddress = modifyAddress;
                        }
                        setResult(RESULT_OK);
                        finish();
                    }
                    App.toast(AddAddressAct.this, baseResponse.message);

                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    LoadingUtils.getInstance().dialogDismiss();
                    App.toast(AddAddressAct.this, "修改失败");
                }
            });
        } else {

            AthService service = App.get().getAthService();
            service.site_add(addressBody).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BaseResponse>() {
                @Override
                public void call(BaseResponse baseResponse) {
                    LoadingUtils.getInstance().dialogDismiss();
                    if (baseResponse.status == 1) {

                        if (modifyAddress != null) {
                            modifyAddress.name = etName.getText().toString();
                            modifyAddress.tel = etPhoneNumber.getText().toString().trim();
                            modifyAddress.dak = etPostcode.getText().toString().trim();
                            modifyAddress.site = etDetailAddress.getText().toString();
                            PrefsManager.get().save("address", App.get().gson.toJson(modifyAddress));
                            App.selectAddress = modifyAddress;
                        }
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        App.toast(AddAddressAct.this, baseResponse.message);
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    LoadingUtils.getInstance().dialogDismiss();
                    App.toast(AddAddressAct.this, "添加失败");
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("AddAddressAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("AddAddressAct");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.btnSave:
                addAddress();
                break;
        }
    }
}
