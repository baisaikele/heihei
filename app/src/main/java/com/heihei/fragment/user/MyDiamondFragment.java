package com.heihei.fragment.user;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.base.pay.PayController;
import com.base.pay.PayController.OnPayListener;
import com.base.pay.PayController.PayMode;
import com.base.utils.PackageUtils;
import com.base.utils.UIUtils;
import com.heihei.adapter.BaseAdapter;
import com.heihei.cell.RechargeDiamondCell;
import com.heihei.cell.RechargeDiamondCell.OnRightClickListener;
import com.heihei.fragment.BaseListFragment;
import com.heihei.logic.UserMgr;
import com.heihei.logic.event.EventManager;
import com.heihei.logic.event.EventTag;
import com.heihei.logic.present.PaymentPresent;
import com.heihei.model.pay.RechargeInfo;
import com.wmlives.heihei.R;

/**
 * 我的钻石/充值
 * 
 * @author chengbo
 */
public class MyDiamondFragment extends BaseListFragment implements OnClickListener {

    private View mHeader;

    private TextView tv_diamond_num;
    private RelativeLayout btn_pay_wx;
    private RelativeLayout btn_pay_zhifubao;

    private List<RechargeInfo> data;

    private List<RechargeInfo> wechatData;
    private List<RechargeInfo> aliData;
    private DiamondAdapter mAdapter;

    @Override
    protected void viewDidLoad() {
        super.viewDidLoad();
        mListLayout.setPullToRefreshEnable(false);
        mHeader = LayoutInflater.from(getContext()).inflate(R.layout.header_my_diamond, null);
        mListView.addHeaderView(mHeader, null, false);
        tv_diamond_num = (TextView) mHeader.findViewById(R.id.tv_diamond);
        btn_pay_wx = (RelativeLayout) mHeader.findViewById(R.id.btn_pay_wx);
        btn_pay_zhifubao = (RelativeLayout) mHeader.findViewById(R.id.btn_pay_zhifubao);
        btn_pay_wx.setOnClickListener(this);
        btn_pay_zhifubao.setOnClickListener(this);
    }

    @Override
    protected void refresh() {
        super.refresh();

        tv_diamond_num.setText("" + UserMgr.getInstance().getLoginUser().goldCount);

        if (data == null) {
            data = new ArrayList<>();
        }
        data.clear();

        if (mListView.getAdapter() == null) {
            mAdapter = new DiamondAdapter(data);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
        btn_pay_wx.performClick();
    }

    private PaymentPresent mPayPresent = new PaymentPresent();
    private PayController mPayController = new PayController();

    /**
     * 请求充值列表
     */
    private void requestRechargeList() {
        if (btn_pay_wx.isSelected()) {
            data.clear();
            if (wechatData != null && wechatData.size() > 0) {
                data.addAll(wechatData);
                mAdapter.notifyDataSetChanged();
            } else {
                mPayPresent.getWechatRechargeList(listResponse);
            }
        } else {
            data.clear();
            if (aliData != null && aliData.size() > 0) {
                data.addAll(aliData);
                mAdapter.notifyDataSetChanged();
            } else {
                mPayPresent.getAliRechargeList(listResponse);
            }
        }
    }

    private JSONResponse listResponse = new JSONResponse() {

        @Override
        public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
            if (errCode == ErrorCode.ERROR_OK && json != null) {
                JSONArray arr = json.optJSONArray("packages");
                if (arr != null && arr.length() > 0) {
                    if (btn_pay_wx.isSelected()) {
                        wechatData = new ArrayList<>();
                        for (int i = 0; i < arr.length(); i++) {
                            wechatData.add(new RechargeInfo(arr.optJSONObject(i)));
                        }
                        data.clear();
                        data.addAll(wechatData);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        aliData = new ArrayList<>();
                        for (int i = 0; i < arr.length(); i++) {
                            aliData.add(new RechargeInfo(arr.optJSONObject(i)));
                        }
                        data.clear();
                        data.addAll(aliData);
                        mAdapter.notifyDataSetChanged();
                    }

                }
            } else {
                UIUtils.showToast(msg);
            }

        }
    };

    class DiamondAdapter extends BaseAdapter<RechargeInfo> {

        public DiamondAdapter(List<RechargeInfo> data) {
            super(data);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.cell_recharge_diamond, null);
            }

            RechargeDiamondCell cell = (RechargeDiamondCell) convertView;
            cell.setOnRightClickListener(mListener);
            cell.setData(getItem(position), position, this);
            return convertView;
        }

    }

    private OnRightClickListener mListener = new OnRightClickListener() {

        @Override
        public void onRightClick(int position) {
            RechargeInfo info = data.get(position);
            createOrder(info);
        }
    };

    /**
     * 创建订单
     * 
     * @param info
     */
    private void createOrder(final RechargeInfo info) {
        if (RechargeInfo.WECHAT.equalsIgnoreCase(info.channel))// 微信订单
        {
            if (!PackageUtils.isPackageInstalled(getContext(), PackageUtils.PKGName.PKGNAME_WECHAT)) {
                UIUtils.showToast(R.string.share_wechat_no_avliible);
                return;
            }

            mPayPresent.createWechatOrder(info.id, new JSONResponse() {

                @Override
                public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
                    if (errCode == ErrorCode.ERROR_OK) {

                        final String order = json.optString("order");
                        JSONObject data = json.optJSONObject("data");
                        if (data == null) {
                            UIUtils.showToast(msg);
                            return;
                        }

                        mPayController.payByWechat(getActivity(), data, new OnPayListener() {

                            @Override
                            public void onPaySuccess(String data) {
                                UIUtils.showToast(getString(R.string.user_pay_success));
                                // sendOrderNotify(order, 1, data);
                                UserMgr.getInstance().getLoginUser().goldCount += info.gold;
                                tv_diamond_num.setText("" + UserMgr.getInstance().getLoginUser().goldCount);
                                EventManager.ins().sendEvent(EventTag.DIAMOND_CHANGED, 0, 0, null);
                            }

                            @Override
                            public void onPayError(String data) {
                                UIUtils.showToast(getString(R.string.user_pay_fail));
                                // sendOrderNotify(order, 2, data);
                            }

                            @Override
                            public void onPayCancel() {
                                UIUtils.showToast(getString(R.string.user_pay_cancel));
                            }

                        });
                    } else {
                        UIUtils.showToast(msg);
                    }

                }
            });
        } else if (RechargeInfo.ALIPAY.equalsIgnoreCase(info.channel))// 支付宝订单
        {
            mPayPresent.createAliOrder(info.id, new JSONResponse() {

                @Override
                public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
                    if (errCode == ErrorCode.ERROR_OK) {
                        final String order = json.optString("order");
                        String data = json.optString("data");

                        mPayController.payByAli(getActivity(), data, new OnPayListener() {

                            @Override
                            public void onPaySuccess(String data) {
                                UIUtils.showToast(getString(R.string.user_pay_success));
                                UserMgr.getInstance().getLoginUser().goldCount += info.gold;
                                tv_diamond_num.setText("" + UserMgr.getInstance().getLoginUser().goldCount);
                                // sendOrderNotify(order, 1, data);

                                EventManager.ins().sendEvent(EventTag.DIAMOND_CHANGED, 0, 0, null);

                            }

                            @Override
                            public void onPayError(String data) {
                                UIUtils.showToast(getString(R.string.user_pay_fail));
                                // sendOrderNotify(order, 2, data);
                            }

                            @Override
                            public void onPayCancel() {
                                UIUtils.showToast(getString(R.string.user_pay_cancel));
                            }

                        });
                    } else {
                        UIUtils.showToast(msg);
                    }

                }
            });
        }
    }

    /**
     * 支付结果通知
     * 
     * @param orderId
     *            订单号
     * @param status
     *            1-支付成功 2-支付失败
     */
    private void sendOrderNotify(String orderId, int status, String data) {
        mPayPresent.orderNotify(orderId, status, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_pay_wx :
            if (btn_pay_wx.isSelected()) {
                return;
            }
            btn_pay_wx.setSelected(true);
            btn_pay_zhifubao.setSelected(false);
            requestRechargeList();
            break;
        case R.id.btn_pay_zhifubao :
            if (btn_pay_zhifubao.isSelected()) {
                return;
            }
            btn_pay_wx.setSelected(false);
            btn_pay_zhifubao.setSelected(true);
            requestRechargeList();
            break;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventManager.ins().reomoveWhat(EventTag.WX_PAY);
        EventManager.ins().reomoveWhat(EventTag.ALI_PAY);
    }

}
