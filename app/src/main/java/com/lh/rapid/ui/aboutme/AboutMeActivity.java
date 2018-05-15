package com.lh.rapid.ui.aboutme;

import com.lh.rapid.R;
import com.lh.rapid.ui.BaseActivity;
import com.lh.rapid.ui.widget.MyActionBar;

import butterknife.BindView;

/**
 * Created by lh on 2018/5/15.
 */

public class AboutMeActivity extends BaseActivity {

    @BindView(R.id.actionbar)
    MyActionBar mActionbar;

    @Override
    public int initContentView() {
        return R.layout.activity_about_me;
    }

    @Override
    public void initInjector() {

    }

    @Override
    public void initUiAndListener() {
        mActionbar.setBackClickListener(new MyActionBar.IActionBarClickListener() {
            @Override
            public void onActionBarClicked() {
                finish();
            }
        });
        mActionbar.setTitle("关于我们");
    }

}
