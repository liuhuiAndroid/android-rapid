package com.lh.rapid.ui.fragment1;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.frameproj.library.adapter.CommonAdapter;
import com.android.frameproj.library.adapter.MultiItemTypeAdapter;
import com.android.frameproj.library.adapter.base.ViewHolder;
import com.android.frameproj.library.decoration.DividerGridItemDecoration;
import com.android.frameproj.library.decoration.RecyclerViewDivider;
import com.android.frameproj.library.util.InputUtil;
import com.android.frameproj.library.util.ToastUtil;
import com.android.frameproj.library.util.log.Logger;
import com.google.gson.Gson;
import com.lh.rapid.Constants;
import com.lh.rapid.R;
import com.lh.rapid.bean.CategoryName;
import com.lh.rapid.bean.DictionaryBean;
import com.lh.rapid.bean.HomeCircleBean;
import com.lh.rapid.bean.HomePageBean;
import com.lh.rapid.bean.ProductListBean;
import com.lh.rapid.ui.BaseFragment;
import com.lh.rapid.ui.choosecircle.ChooseCircleActivity;
import com.lh.rapid.ui.main.MainComponent;
import com.lh.rapid.ui.productdetail.ProductDetailActivity;
import com.lh.rapid.ui.widget.SelectNumberView;
import com.lh.rapid.util.BusUtil;
import com.lh.rapid.util.CommonEvent;
import com.lh.rapid.util.LocationUtil;
import com.lh.rapid.util.MyGlideImageLoader;
import com.lh.rapid.util.SPUtil;
import com.squareup.otto.Subscribe;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

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

public class Fragment1 extends BaseFragment implements Fragment1Contract.View {

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
    @BindView(R.id.viewMask)
    View mViewMask;
    @BindView(R.id.tv_circle_name2)
    TextView mTvCircleName2;
    @BindView(R.id.recyclerViewSearch)
    RecyclerView mRecyclerViewSearch;
    @BindView(R.id.rl_search)
    LinearLayout mRlSearch;
    private RxPermissions mRxPermissions;
    private CategoryCommNameAdapter nameAdapter;
    private CategoryCommodityAdapter commodityAdapter;

    private List<CategoryName> mNames = new ArrayList<>();
    private List<HomePageBean.CategoryListsBean.GoodListBean> goodBeans = new ArrayList<>();

    @Inject
    Fragment1Presenter mPresenter;
    @Inject
    SPUtil mSPUtil;
    private CommonAdapter mSearchCommonAdapter;

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
        mPresenter.commonDictionaryQuery();
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

                    mViewMask.setVisibility(View.GONE);
                    mRlSearch.setVisibility(View.GONE);
                    InputUtil.hideSoftInput(getActivity());
                } else {
                    mIvHomeDelete.setVisibility(View.VISIBLE);
                    mIvHomeDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mEtHomeName.setText("");
                        }
                    });
                    mTvHomeSearch.setVisibility(View.VISIBLE);
                    mViewMask.setVisibility(View.VISIBLE);
                    mTvHomeSearch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InputUtil.hideSoftInput(getActivity());
                            mPresenter.search("", mEtHomeName.getText().toString(), "", "", "", mSPUtil.getCIRCLE_ID() + "");
                        }
                    });
                }
            }
        });
    }

    private boolean isFirst = true;

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        Logger.i("Fragment1 onSupportVisible");
        if (!isFirst) {
            mPresenter.loadDate(mSPUtil.getCIRCLE_ID() + "");
            isFirst = false;
        }
    }

    @Override
    public void initData() {
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @OnClick(R.id.iv_home_mine)
    public void mIvHomeMine() {

    }

    //根据商品id获取当前商品的采购数量
    public int getSelectedItemCountById(int id) {
        HomePageBean.CategoryListsBean.GoodListBean temp = null;
        for (int i = 0; i < goodBeans.size(); i++) {
            HomePageBean.CategoryListsBean.GoodListBean goodListBean = goodBeans.get(i);
            if (goodListBean.getGoodsId() == id) {
                temp = goodListBean;
            }
        }
        if (temp == null) {
            return 0;
        }
        return temp.getCounts();
    }

    @Override
    public void onLoadDateCompleted(HomePageBean homePageBean) {
        initBanner(homePageBean.getBnTop());
        initRecyclerView(homePageBean.getCategoryLists());
        mTvCircleName.setText(homePageBean.getCircleName());
        mTvCircleName2.setText(homePageBean.getCircleName());
    }

    private void initBanner(final List<HomePageBean.BnTopBean> bnTop) {
        mMainBanner.setImageLoader(new MyGlideImageLoader());
        mMainBanner.setImages(bnTop);
        mMainBanner.setDelayTime(2000);
        mMainBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra("goodsId", bnTop.get(position).getBnRelationId());
                startActivity(intent);
            }
        });
        mMainBanner.start();
    }

    private void initRecyclerView(final List<HomePageBean.CategoryListsBean> categoryListsBeans) {

        mRvFormName.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvFormName.addItemDecoration(new RecyclerViewDivider(getActivity().getApplicationContext(), LinearLayout.VERTICAL, 2, R.color.white));
        nameAdapter = new CategoryCommNameAdapter(this, getActivity(), categoryListsBeans);
        nameAdapter.setItemClickLitener(new CategoryCommNameAdapter.rvItemClickLitener() {
            @Override
            public void itemClick(int position) {
                nameAdapter.setSelection(position);
                nameAdapter.notifyDataSetChanged();

                HomePageBean.CategoryListsBean categoryListsBean = categoryListsBeans.get(position);
                // 标签类型：1 商品分类 2 标签
                if (categoryListsBean.getCategoryType() == 1) {
                    mPresenter.onLoadProductList(categoryListsBeans.get(position).getCategoryId() + "", mSPUtil.getCIRCLE_ID() + "");
                } else if (categoryListsBean.getCategoryType() == 2) {
                    goodBeans.clear();
                    goodBeans.addAll(categoryListsBeans.get(position).getGoodList());
                    commodityAdapter.notifyDataSetChanged();
                }
            }
        });
        mRvFormName.setAdapter(nameAdapter);

        goodBeans.clear();
        if (categoryListsBeans != null & categoryListsBeans.size() > 0) {
            goodBeans.addAll(categoryListsBeans.get(0).getGoodList());
        }
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

    @OnClick(R.id.rl_circle)
    public void mRlLocation(View view) {
        Intent intent = new Intent(getActivity(), ChooseCircleActivity.class);
        intent.putExtra("comefrom", 1);
        startActivityForResult(intent, Constants.REQUEST_CHOOSE_CIRCLE_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CHOOSE_CIRCLE_CODE && resultCode == Constants.RESULT_CHOOSE_CIRCLE_CODE) {
            String circleName = data.getStringExtra("name");
            int circleId = data.getIntExtra("id", -1);
            mPresenter.loadDate(circleId + "");
            mSPUtil.setCIRCLE_ID(circleId);
            mTvCircleName.setText(circleName);
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
            mTvCircleName.setText(circleName);
        }
    }

    @Override
    public void onLoadProductListSuccess(List<HomePageBean.CategoryListsBean.GoodListBean> goodListBeans) {
        goodBeans.clear();
        goodBeans.addAll(goodListBeans);
        commodityAdapter.notifyDataSetChanged();
    }

    @Override
    public void cartGoodsAddSuccess(String s) {

    }

    @Override
    public void cartGoodsDeleteSuccess(String s) {

    }

    @Override
    public void searchSuccess(final List<ProductListBean> productListBeanList) {
        mRlSearch.setVisibility(View.VISIBLE);
        mSearchCommonAdapter = new CommonAdapter<ProductListBean>(getActivity(), R.layout.layout_product_list, productListBeanList) {
            @Override
            protected void convert(ViewHolder holder, final ProductListBean goodsDetailBean, int position) {
                holder.setImageUrl(R.id.iv_cart_item_pic, goodsDetailBean.getGoodsImg());
                holder.setText(R.id.tv_cart_item_name, goodsDetailBean.getGoodsName());
                holder.setText(R.id.tv_cart_item_weight, goodsDetailBean.getWeight());
                holder.setText(R.id.tv_cart_item_price, "￥" + goodsDetailBean.getGoodsPrice());
                final SelectNumberView numberView = holder.getView(R.id.sn_cart_item);
                numberView.setQuantity(goodsDetailBean.getCounts());
                numberView.setSelectCallback(new SelectNumberView.ISelectCallback() {
                    @Override
                    public void onResult(int index, int qualitity) {
                        mPresenter.cartGoodsAdd(goodsDetailBean.getGoodsId() + "", qualitity + "", mSPUtil.getCIRCLE_ID() + "");
                        goodsDetailBean.setCounts(qualitity);
                        mSearchCommonAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onMaxQuantity() {

                    }

                    @Override
                    public void onMinQuantity() {

                    }
                });
            }
        };
        mSearchCommonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra("goodsId", productListBeanList.get(position).getGoodsId());
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewSearch.setLayoutManager(mLinearLayoutManager);
        mRecyclerViewSearch.setAdapter(mSearchCommonAdapter);
        mRecyclerViewSearch.setItemAnimator(new DefaultItemAnimator());
        mRecyclerViewSearch.addItemDecoration(new DividerGridItemDecoration(getActivity(), 1));
    }

    @Override
    public void commonDictionaryQuerySuccess(List<DictionaryBean> dictionaryBeanList) {
        if (dictionaryBeanList != null && dictionaryBeanList.size() > 0) {
            mSPUtil.setDICTIONARY_DATA(new Gson().toJson(dictionaryBeanList));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusUtil.getBus().unregister(this);
    }

    public void handlerCarNum(int type, int position, boolean refreshGoodList) {
        int count = -1;
        HomePageBean.CategoryListsBean.GoodListBean temp = goodBeans.get(position);
        if (type == 0) {
            if (temp != null) {
                if (temp.getCounts() < 2) {
                    temp.setCounts(0);
                    count = 0;
                } else {
                    int i = temp.getCounts();
                    temp.setCounts(--i);
                    count = i;
                }
            }

        } else if (type == 1) {
            if (temp == null) {
                temp.setCounts(1);
                count = 1;
            } else {
                int i = temp.getCounts();
                temp.setCounts(++i);
                count = i;
            }
        }
        commodityAdapter.notifyDataSetChanged();

        mPresenter.cartGoodsAdd(temp.getGoodsId() + "", count + "",
                mSPUtil.getCIRCLE_ID() + "");
    }

}