package com.lh.rapid.ui.login;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lh.rapid.R;
import com.lh.rapid.components.storage.UserStorage;
import com.lh.rapid.ui.BaseActivity;
import com.lh.rapid.ui.main.MainActivity;
import com.lh.rapid.ui.widget.MyActionBar;
import com.lh.rapid.util.SPUtil;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by lh on 2018/4/25.
 */

public class LoginByCardActivity extends BaseActivity implements LoginContract.View {

    @Inject
    LoginPresenter mPresenter;
    @Inject
    SPUtil mSPUtil;
    @Inject
    UserStorage mUserStorage;
    @BindView(R.id.actionbar)
    MyActionBar mActionbar;
    @BindView(R.id.et_login_card_num)
    EditText mEtLoginCardNum;
    @BindView(R.id.et_login_password)
    EditText mEtLoginPassword;
    @BindView(R.id.tv_login_forget_password)
    TextView mTvLoginForgetPassword;
    @BindView(R.id.bt_login_sure)
    Button mBtLoginSure;

    @Override
    public int initContentView() {
        return R.layout.activity_card_login;
    }

    @Override
    public void initInjector() {
        DaggerLoginComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .build()
                .inject(this);
    }

    @Override
    public void initUiAndListener() {
        mPresenter.attachView(this);
        mSPUtil.setIS_LOGIN(0);
        mSPUtil.setTOKNE("");
        mUserStorage.setToken("");

        mActionbar.setBackClickListener(new MyActionBar.IActionBarClickListener() {
            @Override
            public void onActionBarClicked() {
                finish();
            }
        });
        mActionbar.setTitle("用户登录");
    }

    @Override
    public void loginSuccess() {
        openActivity(MainActivity.class);
    }

    @Override
    public void refreshSmsCodeUi() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @OnClick(R.id.bt_login_sure)
    public void mBtLoginSure() {
        String mUserName = mEtLoginCardNum.getText().toString().trim();
        String mPassword = mEtLoginPassword.getText().toString().trim();
        mPresenter.login(mUserName, mPassword, 2);
    }

}