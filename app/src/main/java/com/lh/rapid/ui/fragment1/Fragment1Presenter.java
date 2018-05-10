package com.lh.rapid.ui.fragment1;

import android.support.annotation.NonNull;

import com.lh.rapid.api.common.CommonApi;
import com.lh.rapid.bean.HomeCircleBean;
import com.lh.rapid.bean.HomePageBean;
import com.lh.rapid.bean.HttpResult;
import com.lh.rapid.bean.ProductListBean;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by WE-WIN-027 on 2016/9/27.
 *
 * @des ${TODO}
 */
public class Fragment1Presenter implements Fragment1Contract.Presenter {

    private final CompositeDisposable disposables = new CompositeDisposable();
    private Fragment1Contract.View mView;
    private CommonApi mCommonApi;

    @Inject
    public Fragment1Presenter(CommonApi commonApi) {
        mCommonApi = commonApi;
    }

    @Override
    public void attachView(@NonNull Fragment1Contract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        disposables.clear();
    }

    @Override
    public void loadDate(String circleId) {
        mView.showLoading();
        disposables.add(mCommonApi.homePage(circleId)
                .debounce(800, TimeUnit.MILLISECONDS)
                .flatMap(new Function<HttpResult<HomePageBean>, ObservableSource<HomePageBean>>() {
                    @Override
                    public ObservableSource<HomePageBean> apply(@io.reactivex.annotations.NonNull HttpResult<HomePageBean> homePageBeanHttpResult) throws Exception {
                        return CommonApi.flatResponse(homePageBeanHttpResult);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideLoading();
                    }
                })
                .subscribe(new Consumer<HomePageBean>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull HomePageBean homePageBean) throws Exception {
                        mView.onLoadDateCompleted(homePageBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                        mView.loadError(throwable);
                    }
                }));
    }

    @Override
    public void homeCircle(double longitude, double latitude) {
        mView.showLoading();
        disposables.add(mCommonApi.homeCircle(longitude, latitude)
                .debounce(800, TimeUnit.MILLISECONDS)
                .flatMap(new Function<HttpResult<List<HomeCircleBean>>, ObservableSource<List<HomeCircleBean>>>() {
                    @Override
                    public ObservableSource<List<HomeCircleBean>> apply(@io.reactivex.annotations.NonNull HttpResult<List<HomeCircleBean>> homePageBeanHttpResult) throws Exception {
                        return CommonApi.flatResponse(homePageBeanHttpResult);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideLoading();
                    }
                })
                .subscribe(new Consumer<List<HomeCircleBean>>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull List<HomeCircleBean> homeCircleBeanList) throws Exception {
                        mView.homeCircleSuccess(homeCircleBeanList);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                        mView.loadError(throwable);
                    }
                }));
    }

    @Override
    public void onLoadProductList(String categoryId, String circleId) {
        disposables.add(mCommonApi.goodsList(null, null, null, null, categoryId, circleId, 1, 10)
                .debounce(800, TimeUnit.MILLISECONDS)
                .flatMap(new Function<HttpResult<List<ProductListBean>>, ObservableSource<List<ProductListBean>>>() {
                    @Override
                    public ObservableSource<List<ProductListBean>> apply(@io.reactivex.annotations.NonNull HttpResult<List<ProductListBean>> homePageBeanHttpResult) throws Exception {
                        return CommonApi.flatResponse(homePageBeanHttpResult);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ProductListBean>>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull List<ProductListBean> goodsDetailBeanList) throws Exception {
                        mView.onLoadProductListSuccess(goodsDetailBeanList);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                        mView.loadError(throwable);
                    }
                }));
    }

}
