package com.heihei.fragment.user;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.base.utils.UIUtils;
import com.heihei.adapter.BaseAdapter;
import com.heihei.cell.ExchangeCell;
import com.heihei.cell.RechargeDiamondCell.OnRightClickListener;
import com.heihei.fragment.BaseListFragment;
import com.heihei.logic.UserMgr;
import com.heihei.logic.present.PaymentPresent;
import com.heihei.model.ExchangeInfo;
import com.wmlives.heihei.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 兑换钻石
 * 
 * @author chengbo
 */
public class ExchangeFragment extends BaseListFragment {

    private View mHeader;

    private TextView tv_ticker_num;

    private List<ExchangeInfo> data;
    private DiamondAdapter mAdapter;

    private PaymentPresent mPayPresent = new PaymentPresent();

    @Override
    protected String initTitle() {
        return getString(R.string.user_account_bill_frances_bore);
    }

    @Override
    protected void viewDidLoad() {
        super.viewDidLoad();
        mListLayout.setPullToRefreshEnable(false);
        mHeader = LayoutInflater.from(getContext()).inflate(R.layout.header_exchange, null);
        mListView.addHeaderView(mHeader, null, false);
        tv_ticker_num = (TextView) mHeader.findViewById(R.id.tv_diamond);
    }

    @Override
    protected void refresh() {
        super.refresh();

        tv_ticker_num.setText(UserMgr.getInstance().getLoginUser().point + "");
        
        if (data == null) {
            data = new ArrayList<>();
        }

        if (mListView.getAdapter() == null) {
            mAdapter = new DiamondAdapter(data);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }

        requestList();

    }

    /**
     * 请求兑换列表
     */
    private void requestList() {
        mPayPresent.exchangeList(new JSONResponse() {

            @Override
            public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
                if (errCode == ErrorCode.ERROR_OK) {
                    JSONArray result = json.optJSONArray("packages");
                    if (result != null && result.length() > 0) {
                        data.clear();
                        for (int i = 0; i < result.length(); i++) {
                            data.add(new ExchangeInfo(result.optJSONObject(i)));
                        }
                    }
                } else {
                    UIUtils.showToast(msg);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    class DiamondAdapter extends BaseAdapter<ExchangeInfo> {

        public DiamondAdapter(List<ExchangeInfo> data) {
            super(data);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.cell_exchange, null);
            }

            ExchangeCell cell = (ExchangeCell) convertView;
            cell.setOnRightClickListener(mListener);
            cell.setData(getItem(position), position, this);
            return convertView;
        }

    }

    private OnRightClickListener mListener = new OnRightClickListener() {

        @Override
        public void onRightClick(int position) {
            ExchangeInfo info = data.get(position);
            exchange(info.id);
        }
    };

    /**
     * 兑换
     * 
     * @param packageId
     */
    private void exchange(String packageId) {
        mPayPresent.exchange(packageId, new JSONResponse() {

            @Override
            public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
                if (errCode == ErrorCode.ERROR_OK) {
                    int point = json.optInt("point");
                    tv_ticker_num.setText(String.valueOf(point));
                    UserMgr.getInstance().getLoginUser().point = point;
                    UserMgr.getInstance().saveLoginUser();
                    UIUtils.showToast(getString(R.string.user_exchange_success));
                } else {
                    UIUtils.showToast(msg);
                }

            }
        });
    }

}
