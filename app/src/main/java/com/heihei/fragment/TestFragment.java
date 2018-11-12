package com.heihei.fragment;

import android.view.View;
import android.view.View.OnClickListener;

import com.base.host.BaseFragment;
import com.wmlives.heihei.R;

public class TestFragment extends BaseFragment {

    // ----------------R.layout.fragment_test-------------Start
    private com.heihei.fragment.live.widget.GiftNumberView number_view;

    public void autoLoad_fragment_test() {
//        number_view = (com.heihei.fragment.live.widget.GiftNumberView) findViewById(R.id.number_view);
    }

    // ----------------R.layout.fragment_test-------------End

    @Override
    protected void loadContentView() {
        setContentView(R.layout.fragment_test);

    }

    @Override
    protected void viewDidLoad() {
        autoLoad_fragment_test();
        
//        getView().setOnClickListener(new OnClickListener() {
//            
//            @Override
//            public void onClick(View v) {
//                number_view.increaseNumber(1);
//            }
//        });
    }

    @Override
    protected void refresh() {
        // TODO Auto-generated method stub

    }

	@Override
	public String getCurrentFragmentName() {
		// TODO Auto-generated method stub
		return "TestFragment";
	}

}
