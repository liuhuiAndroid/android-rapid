package com.lh.rapid.ui.fragment1;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.frameproj.library.adapter.wrapper.LoadMoreWrapper;
import com.android.frameproj.library.decoration.RecyclerViewDivider;
import com.android.frameproj.library.util.ToastUtil;
import com.lh.rapid.Constants;
import com.lh.rapid.R;
import com.lh.rapid.bean.CategoryName;
import com.lh.rapid.bean.GoodBean;
import com.lh.rapid.bean.HomeCircleBean;
import com.lh.rapid.bean.HomePageBean;
import com.lh.rapid.bean.ProductListBean;
import com.lh.rapid.ui.BaseFragment;
import com.lh.rapid.ui.location.ChooseLocationActivity;
import com.lh.rapid.ui.main.MainComponent;
import com.lh.rapid.ui.productdetail.ProductDetailActivity;
import com.lh.rapid.util.BusUtil;
import com.lh.rapid.util.CommonEvent;
import com.lh.rapid.util.LocationUtil;
import com.lh.rapid.util.MyGlideImageLoader;
import com.lh.rapid.util.SPUtil;
import com.squareup.otto.Subscribe;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Fragment1 extends BaseFragment implements Fragment1Contract.View, LoadMoreWrapper.OnLoadMoreListener {

    @BindView(R.id.iv_home_place)
    ImageView mIvHomePlace;
    @BindView(R.id.tv_home_location)
    TextView mTvHomeLocation;
    @BindView(R.id.iv_home_down)
    ImageView mIvHomeDown;
    @BindView(R.id.iv_home_mine)
    ImageView mIvHomeMine;
    @BindView(R.id.iv_home_search)
    ImageView mIvHomeSearch;
    @BindView(R.id.et_home_name)
    EditText mEtHomeName;
    @BindView(R.id.iv_home_delete)
    ImageView mIvHomeDelete;
    @BindView(R.id.tv_home_search)
    TextView mTvHomeSearch;
    @BindView(R.id.iv_alf)
    ImageView mIvAlf;
    @BindView(R.id.main_banner)
    Banner mMainBanner;
    @BindView(R.id.iv_location)
    ImageView mIvLocation;
    @BindView(R.id.rv_form_name)
    RecyclerView mRvFormName;
    @BindView(R.id.rv_form_detail)
    RecyclerView mRvFormDetail;
    @BindView(R.id.cl_main)
    CoordinatorLayout mClMain;
    @BindView(R.id.tv_cart_product_detail)
    TextView mTvCartProductDetail;
    @BindView(R.id.tv_cart_product_detail_send)
    TextView mTvCartProductDetailSend;
    @BindView(R.id.iv_footer_cart_product_detail)
    ImageView mIvFooterCartProductDetail;
    @BindView(R.id.tv_cart_num_product_detail)
    TextView mTvCartNumProductDetail;
    @BindView(R.id.rl_footer_cart_product_detail)
    RelativeLayout mRlFooterCartProductDetail;
    @BindView(R.id.tv_favorite_product_detail_chose)
    TextView mTvFavoriteProductDetailChose;
    @BindView(R.id.rl_main_bottom)
    RelativeLayout mRlMainBottom;
    @BindView(R.id.ll_cart)
    LinearLayout mLlCart;
    @BindView(R.id.tv_circle_name)
    TextView mTvCircleName;
    private RxPermissions mRxPermissions;
    private CategoryCommNameAdapter nameAdapter;
    private CategoryCommodityAdapter commodityAdapter;

    private List<CategoryName> mNames = new ArrayList<>();
    private List<HomePageBean.CategoryListsBean.GoodListBean> goodBeans = new ArrayList<>();
    private SparseArray<GoodBean> selectedList;

    @Inject
    Fragment1Presenter mPresenter;
    @Inject
    SPUtil mSPUtil;

    public static BaseFragment newInstance() {
        Fragment1 fragment1 = new Fragment1();
        return fragment1;
    }

    //  0
    @Override
    public int initContentView() {
        return R.layout.fragment_1;
    }

    //  1
    @Override
    public void initInjector() {
        getComponent(MainComponent.class).inject(this);
    }

    //  2
    @Override
    public void getBundle(Bundle bundle) {

    }

    //  3
    @Override
    public void initUI(View view) {
        BusUtil.getBus().register(this);
        mPresenter.attachView(this);
        mRxPermissions = new RxPermissions(getActivity());
        mRxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA
                )
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean granted) throws Exception {
                        if (granted) {
                            LocationUtil.getInstance(getActivity()).startMonitor();
                        } else {
                            ToastUtil.showToast("没有权限部分功能不能正常运行!");
                            mPresenter.homeCircle(mSPUtil.getLONGITUDE(), mSPUtil.getLATITUDE());
                        }
                    }
                });
        selectedList = new SparseArray<>();
        initTitle();
    }

    private void initTitle() {
        mEtHomeName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mEtHomeName.getText().toString().equals("")) {
                    mIvHomeDelete.setVisibility(View.GONE);
                    mTvHomeSearch.setVisibility(View.GONE);
                } else {
                    mIvHomeDelete.setVisibility(View.VISIBLE);
                    mTvHomeSearch.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void initData() {
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onLoadMoreRequested() {

    }

    @OnClick(R.id.iv_home_mine)
    public void mIvHomeMine() {

    }

    //根据商品id获取当前商品的采购数量
    public int getSelectedItemCountById(int id) {
        GoodBean temp = selectedList.get(id);
        if (temp == null) {
            return 0;
        }
        return temp.getNum();
    }

    @Override
    public void onLoadDateCompleted(HomePageBean homePageBean) {
        initBanner(homePageBean.getBnTop());
        initRecyclerView(homePageBean.getCategoryLists());
        mTvCircleName.setText(homePageBean.getCircleName());
    }

    private void initBanner(List<HomePageBean.BnTopBean> bnTop) {
        mMainBanner.setImageLoader(new MyGlideImageLoader());
        mMainBanner.setImages(bnTop);
        mMainBanner.setDelayTime(2000);
        mMainBanner.start();
    }

    private void initRecyclerView(final List<HomePageBean.CategoryListsBean> categoryLists) {

        mRvFormName.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvFormName.addItemDecoration(new RecyclerViewDivider(getActivity().getApplicationContext(), LinearLayout.VERTICAL, 2, R.color.white));
        nameAdapter = new CategoryCommNameAdapter(this, getActivity(), categoryLists);
        nameAdapter.setItemClickLitener(new CategoryCommNameAdapter.rvItemClickLitener() {
            @Override
            public void itemClick(int position) {

                nameAdapter.setSelection(position);
                nameAdapter.notifyDataSetChanged();
                mPresenter.onLoadProductList(categoryLists.get(position).getCategoryId() + "", mSPUtil.getCIRCLE_ID() + "");
            }
        });
        mRvFormName.setAdapter(nameAdapter);

        goodBeans.clear();
        goodBeans.addAll(categoryLists.get(0).getGoodList());
        mRvFormDetail.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvFormDetail.addItemDecoration(new RecyclerViewDivider(getActivity().getApplicationContext(), LinearLayout.VERTICAL, 2, R.color.line));
        commodityAdapter = new CategoryCommodityAdapter(this, getActivity(), goodBeans, nameAdapter);
        commodityAdapter.setItemClickLitener(new CategoryCommodityAdapter.rvCateItemClickLitener() {
            @Override
            public void itemClick(int position) {
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra("goodsId", goodBeans.get(position).getGoodsId());
                startActivity(intent);
            }
        });
        mRvFormDetail.setAdapter(commodityAdapter);
    }

    @OnClick(R.id.rl_location)
    public void mRlLocation(View view) {
        Intent intent = new Intent(getActivity(), ChooseLocationActivity.class);
        startActivityForResult(intent, Constants.REQUEST_CHOOSE_LOCATION_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CHOOSE_LOCATION_CODE && resultCode == Constants.RESULT_CHOOSE_LOCATION_CODE) {
            double longitude = data.getDoubleExtra("longitude", -1);
            double latitude = data.getDoubleExtra("latitude", -1);
            String name = data.getStringExtra("name");
            String addr = data.getStringExtra("addr");
            mPresenter.homeCircle(longitude, latitude);
            mTvHomeLocation.setText(addr);
        }
    }

    @Subscribe
    public void onPaySuccessEvent(final CommonEvent.LocationEvent locationEvent) {
        mPresenter.homeCircle(locationEvent.getLongitude(), locationEvent.getLatitude());
        Observable.just(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        mTvHomeLocation.setText(locationEvent.getAddrStr());
                    }
                });
    }

    @Override
    public void homeCircleSuccess(List<HomeCircleBean> homeCircleBeanList) {
        if (homeCircleBeanList != null && homeCircleBeanList.size() > 0) {
            int circleId = homeCircleBeanList.get(0).getCircleId();
            String circleName = homeCircleBeanList.get(0).getCircleName();
            mPresenter.loadDate(circleId + "");
            mSPUtil.setCIRCLE_ID(circleId);
        }
    }

    @Override
    public void onLoadProductListSuccess(List<ProductListBean> goodsDetailBeanList) {
//        goodBeans.clear();
//        goodBeans.addAll(goodsDetailBeanList);
//        commodityAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusUtil.getBus().unregister(this);
    }

}