package com.geziwulian.mapviewdemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.location.Criteria;
import android.location.Location;

import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements AMapLocationListener,LocationSource {

    private MapView mMapView;
    private AMap aMap;
    private UiSettings mUiSetting;
    private LocationManager manager;
    private Location location;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mlocationClient = new AMapLocationClient(this);
//初始化定位参数
        mLocationOption = new AMapLocationClientOption();
//设置定位监听
        mlocationClient.setLocationListener(this);
//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
//设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
// 在定位结束后，在合适的生命周期调用onDestroy()方法
// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//启动定位
        mlocationClient.startLocation();



        Criteria criteria = new Criteria();
        String bestProvider = manager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location = manager.getLastKnownLocation(bestProvider);
        if (aMap == null) {
            aMap = mMapView.getMap();
            mUiSetting = aMap.getUiSettings();
        }
        mUiSetting.setScaleControlsEnabled(true);
        aMap.setLocationSource(MainActivity.this);
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);
        aMap.setMyLocationEnabled(true);
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

//        aMap.setLocationSource(new LocationSource() {
//            @Override
//            public void activate(OnLocationChangedListener listener) {
//                mListener = listener;
//                //初始化定位
////                mlocationClient = new AMapLocationClient(MainActivity.this);
//                //初始化定位参数
////                mLocationOption = new AMapLocationClientOption();
//                //设置定位回调监听
////                mlocationClient.setLocationListener(MainActivity.this);
//                //设置为高精度定位模式
////                mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//                //设置定位参数
////                mlocationClient.setLocationOption(mLocationOption);
//                // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
//                // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
//                // 在定位结束后，在合适的生命周期调用onDestroy()方法
//                // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
////                mlocationClient.startLocation();//启动定位
//
//            }
//
//            @Override
//            public void deactivate() {
//
//            }
//        });
        aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(location.getLatitude(), location.getLongitude()), 18, 45, 0)));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mMapView.onSaveInstanceState(outState);
    }


    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {

//        if (mListener != null && amapLocation != null){
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    mListener.onLocationChanged(amapLocation);
                    //定位成功回调信息，设置相关消息
                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    amapLocation.getLatitude();//获取纬度
                    amapLocation.getLongitude();//获取经度
                    amapLocation.getAccuracy();//获取精度信息
                    String lat = amapLocation.getLatitude()+"";
                    String lon = amapLocation.getLongitude()+"";
                    Log.e("","获取经纬度===="+amapLocation.getLatitude()+"获取经度"+amapLocation.getLongitude()+"获取精度信息"+amapLocation.getAccuracy()+"");
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(amapLocation.getTime());
                    df.format(date);//定

                    MarkerOptions markerOption = new MarkerOptions();
                    LatLng x = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                    markerOption.position(x);
                    markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");

                    markerOption.draggable(true);//设置Marker可拖动
                    markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(),R.mipmap.ic_launcher)));
// 将Marker设置为贴地显示，可以双指下拉地图查看效果
                    markerOption.setFlat(true);//设置marker平贴地图效果
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());


                    String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                    Log.e("AmapErr",errText);
                }
            }
//        }

    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
    }

    @Override
    public void deactivate() {

    }
}
