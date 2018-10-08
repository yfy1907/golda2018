package com.example.administrator.goldaappnew.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.administrator.goldaappnew.R;
import com.example.administrator.goldaappnew.staticClass.StaticMember;
import com.example.administrator.goldaappnew.utils.CommonTools;
import com.example.administrator.goldaappnew.utils.MultiTool;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ModifyLatLngActivity extends AppCompatActivity implements BDLocationListener{

    @BindView(R.id.tv_submit) TextView tv_submit;
    private MapView mMapView;
    public LocationClient mLocClient = null;
    private BaiduMap mBaiduMap;
    private boolean isFirstLoc = true;
    private LatLng myLocationLL;
    private double markerLat;
    private double markerLng;
    private float accuracy;
    private Toolbar toolbar;
    private String longitude;
    private String latitude;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonTools.setStateBarColor(this);
        setContentView(R.layout.activity_modify_lat_lng);
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        init();
    }

    private void init() {
        markerLat = getIntent().getDoubleExtra("lat", 0.0);
        markerLng = getIntent().getDoubleExtra("lng", 0.0);
        Log.e("收到的marker坐标是",markerLat+"***"+markerLng);

        mMapView = (MapView) findViewById(R.id.mapview);
        mBaiduMap = mMapView.getMap();
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark);
        mBaiduMap.addOverlay(new MarkerOptions()
                .position(new LatLng(markerLat, markerLng))
                .animateType(MarkerOptions.MarkerAnimateType.grow)
                .period(10)
                .draggable(true)
                .anchor(0.5f, 1.0f)
                .icon(bitmap));
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(this);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        option.setNeedDeviceDirect(true);
        mLocClient.setLocOption(option);
        mLocClient.start();
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                int distance = (int) MultiTool.toDistance(markerLat, markerLng, latLng.latitude, latLng.longitude);
                if (distance > StaticMember.LENGTH + accuracy) {
                    showSnackBar("距离目标点过远，不允许上传数据！");

                }else {
                    setMapOverlay(latLng);
                }
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }

    @OnClick(R.id.tv_submit)
    public void submit(){
        Intent date=new Intent();
        Log.i("Modif.......", "setMapOverlay: 经度"+longitude+"纬度"+latitude);
        date.putExtra("lat",latitude);
        date.putExtra("lng",longitude);
        setResult(1003,date);
        finish();
    }
    /**
     * 展示一个SnackBar
     */
    public void showSnackBar(String message) {
        //去掉虚拟按键
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION //隐藏虚拟按键栏
                | View.SYSTEM_UI_FLAG_IMMERSIVE //防止点击屏幕时,隐藏虚拟按键栏又弹了出来
        );
        final Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), message, Snackbar.LENGTH_INDEFINITE);
        CommonTools.setSnackbarMessageTextColor(snackbar, getResources().getColor(R.color.orange));

        snackbar.setAction("知道了", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                //隐藏SnackBar时记得恢复隐藏虚拟按键栏,不然屏幕底部会多出一块空白布局出来,和难看
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }).show();
    }
    // 在地图上添加标注
    private void setMapOverlay(LatLng point) {
        latitude = new DecimalFormat("#.000000").format(point.latitude);
        longitude = new DecimalFormat("#.000000").format(point.longitude);
        Log.i("Modif", "setMapOverlay: 经度"+longitude+"纬度"+latitude);
        mBaiduMap.clear();
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark);
        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
        mBaiduMap.addOverlay(option);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理

        if(null != mLocClient){
            Log.i("","解除定位注册监听...");
            mLocClient.unRegisterLocationListener(this);
            mLocClient.stop();
        }
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (bdLocation == null || mMapView == null) {
            return;
        }
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(bdLocation.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(bdLocation.getDirection()).latitude(bdLocation.getLatitude())
                .longitude(bdLocation.getLongitude()).build();
        mBaiduMap.setMyLocationData(locData);
        myLocationLL = new LatLng(bdLocation.getLatitude(),
                bdLocation.getLongitude());
        accuracy = bdLocation.getRadius();
        if (isFirstLoc) {
            Log.e("首次定位", isFirstLoc + "");
            isFirstLoc = false;
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(myLocationLL).zoom(20.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {

    }
}
