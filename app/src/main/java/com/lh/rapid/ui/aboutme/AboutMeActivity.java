package com.lh.rapid.ui.aboutme;

import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.frameproj.library.adapter.CommonAdapter;
import com.android.frameproj.library.adapter.MultiItemTypeAdapter;
import com.android.frameproj.library.adapter.base.ViewHolder;
import com.android.frameproj.library.decoration.DividerGridItemDecoration;
import com.lh.rapid.R;
import com.lh.rapid.bean.CommonNewsInfoBean;
import com.lh.rapid.ui.BaseActivity;
import com.lh.rapid.ui.h5.H5Activity;
import com.lh.rapid.ui.widget.MyActionBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by lh on 2018/5/15.
 */

public class AboutMeActivity extends BaseActivity implements AboutMeContract.View {

    @BindView(R.id.actionbar)
    MyActionBar mActionbar;

    @Inject
    AboutMePresenter mPresenter;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    private LinearLayoutManager mLinearLayoutManager;
    private CommonAdapter mCommonAdapter;

    @Override
    public int initContentView() {
        return R.layout.activity_about_me;
    }

    @Override
    public void initInjector() {
        DaggerAboutMeComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .build()
                .inject(this);
    }

    @Override
    public void initUiAndListener() {
        mPresenter.attachView(this);
        mActionbar.setBackClickListener(new MyActionBar.IActionBarClickListener() {
            @Override
            public void onActionBarClicked() {
                finish();
            }
        });
        mActionbar.setTitle("关于我们");

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mPresenter.commonNewsInfo();
            }
        });
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.autoRefresh();
    }

    @Override
    public void loadError(Throwable throwable) {
        super.loadError(throwable);
        mRefreshLayout.finishRefresh(300);
    }

    @Override
    public void commonNewsInfoSuccess(final List<CommonNewsInfoBean> commonNewsInfoBeanList) {
        mRefreshLayout.finishRefresh(300);
        if (mCommonAdapter == null) {
            mCommonAdapter = new CommonAdapter<CommonNewsInfoBean>(AboutMeActivity.this, R.layout.layout_about_me, commonNewsInfoBeanList) {
                @Override
                protected void convert(ViewHolder holder, final CommonNewsInfoBean commonNewsInfoBean, int position) {
                    holder.setText(R.id.textView, commonNewsInfoBean.getTitle());
                }
            };
            mCommonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                    Intent intent = new Intent(AboutMeActivity.this, H5Activity.class);
                    intent.putExtra("url",commonNewsInfoBeanList.get(position).getContent());
                    intent.putExtra("title",commonNewsInfoBeanList.get(position).getTitle());
                    startActivity(intent);
                }

                @Override
                public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                    return false;
                }
            });
            mLinearLayoutManager = new LinearLayoutManager(AboutMeActivity.this);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mRecyclerView.setAdapter(mCommonAdapter);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.addItemDecoration(new DividerGridItemDecoration(AboutMeActivity.this, 1));
        } else {
            if (mRecyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE || (mRecyclerView.isComputingLayout() == false)) {
                mCommonAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

}
