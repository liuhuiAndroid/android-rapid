package com.lh.rapid.util;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import static com.android.frameproj.library.util.ToastUtil.mContext;

/**
 * Created by we-win on 2016/11/18.
 */

public class LocationUtil {

    private final static boolean DEBUG = true;
    private final static String TAG = "LocationUtil";
    private static LocationUtil mInstance;
    private BDLocation mLocation = null;
    private MLocation mBaseLocation = new MLocation();
    private static SPUtil spUtil;

    public static LocationUtil getInstance(Context context) {
        spUtil = new SPUtil(mContext);
        if (mInstance == null) {
            mInstance = new LocationUtil(context);
        }

        return mInstance;
    }

    String mProvider;
    public BDLocationListener myListener = new MyLocationListener();
    private LocationClient mLocationClient;

    public LocationUtil(Context context) {
        mLocationClient = new LocationClient(context.getApplicationContext());
        initParams();
        mLocationClient.registerLocationListener(myListener);
    }

    public void startMonitor() {
        if (DEBUG)
            Log.d(TAG, "start monitor location");
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.requestLocation();
        } else {
            Log.d("LocSDK3", "locClient is null or not started");
        }
    }

    public void stopMonitor() {
        if (DEBUG)
            Log.d(TAG, "stop monitor location");
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
    }

    public BDLocation getLocation() {
        if (DEBUG)
            Log.d(TAG, "get location");
        return mLocation;
    }

    public MLocation getBaseLocation() {
        if (DEBUG)
            Log.d(TAG, "get location");
        return mBaseLocation;
    }

    private void initParams() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        //option.setPriority(LocationClientOption.NetWorkFirst);
        option.setAddrType("all");//返回的定位结果包含地址信息
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(2000);//设置发起定位请求的间隔时间为5000ms
        option.disableCache(true);//禁止启用缓存定位
        //        option.setPoiNumber(5);    //最多返回POI个数
        //        option.setPoiDistance(1000); //poi查询距离
        //        option.setPoiExtraInfo(true); //是否需要POI的电话和地址等详细信息
        mLocationClient.setLocOption(option);
    }

    public LocationClient getmLocationClient() {
        return mLocationClient;
    }

    private boolean isFirst = false;

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
            if(!isFirst){
                isFirst = true;
                BusUtil.getBus().post(new CommonEvent().new LocationEvent(location.getLatitude(),location.getLongitude(),location.getAddrStr()));
            }
            mLocation = location;
            mBaseLocation.latitude = mLocation.getLatitude();
            mBaseLocation.longitude = mLocation.getLongitude();
            String city = mLocation.getCity();

            spUtil.setLATITUDE(mLocation.getLatitude());
            spUtil.setLONGITUDE(mLocation.getLongitude());

            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            sb.append("\ncity : ");
            sb.append(location.getCity());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
            }
            if (DEBUG)
                Log.d(TAG, "" + sb);
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    public class MLocation {
        public double latitude;
        public double longitude;
    }

}
