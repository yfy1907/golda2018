package com.example.administrator.goldaappnew.fragment;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.administrator.goldaappnew.R;
import com.example.administrator.goldaappnew.activity.AboutActivity;
import com.example.administrator.goldaappnew.activity.GreenMarkerDetail;
import com.example.administrator.goldaappnew.activity.RedMarkerDetail;
import com.example.administrator.goldaappnew.activity.SearchActivity;
import com.example.administrator.goldaappnew.bean.AdGreenBean;
import com.example.administrator.goldaappnew.bean.AdRedBean;
import com.example.administrator.goldaappnew.bean.MarkerInfo;
import com.example.administrator.goldaappnew.common.MyLogger;
import com.example.administrator.goldaappnew.staticClass.StaticMember;
import com.example.administrator.goldaappnew.utils.CommonTools;
import com.example.administrator.goldaappnew.utils.DrawableTool;
import com.example.administrator.goldaappnew.utils.HttpTools;
import com.example.administrator.goldaappnew.utils.MultiTool;
import com.example.administrator.goldaappnew.view.PopWindow;
import com.zaaach.toprightmenu.TopRightMenu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.content.Context.MODE_PRIVATE;

public class FragmentXuncha extends BaseFragment implements BaiduMap.OnMapClickListener, BaiduMap.OnMarkerClickListener {

    private Unbinder unbinder;

    // 添加广告牌
    @BindView(R.id.fab)
    FloatingActionButton fab;
    // 手动添加
    @BindView(R.id.fab_add_by_hand)
    FloatingActionButton fab_add_by_hand;
    // 当前位置添加
    @BindView(R.id.fab_add_now)
    FloatingActionButton fab_add_now;

    // 回到当前位置
    @BindView(R.id.fab_local)
    FloatingActionButton fab_my_location;

    // 刷新点位
    @BindView(R.id.fab_refresh)
    FloatingActionButton fab_refresh;

    // 广告牌列表（周围广告牌）
    @BindView(R.id.fab_around)
    FloatingActionButton fab_around;

    // 切换卫星模式
    @BindView(R.id.fab_swich)
    FloatingActionButton fab_map_swich;

    // 左侧 设置中心
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.search_view)
    SearchView searchView;
    @BindView(R.id.iv_center_location)
    ImageView iv_center_location;
    @BindView(R.id.iv_choose)
    ImageView iv_choose;
    @BindView(R.id.search_src_text)
    SearchView.SearchAutoComplete textView;


    private boolean isMenuOpen = false;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LatLng centerMarkerLL;
    public static LatLng myLocationLL;//用于共享
    private static int refresh_count = 0;
    private int page = 1;// 分页.已废弃。
    private String loadTypeString = "2";//加载的图表类型，筛选条件
    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private LocationClientOption.LocationMode mCurrentMode;
    private boolean isFirstLoc = true; // 是否首次定位
    //成员变量
    public static List<AdGreenBean> adgreenList = new ArrayList<AdGreenBean>();
    public static List<AdRedBean> adredList = new ArrayList<AdRedBean>();
    //用于存储marker图标，节省内存。
    public static HashMap<Integer, BitmapDescriptor> bitmapDescriptorHashMap = new HashMap<>();
    private boolean canClickMap = false;//设置地图点击是否有效
    private String province = "";//当前省
    private String city = "";        //当前市
    private String district = ""; //当前街道
    private String street = "";//街道及街道号
    private float accuracy;//当前的定位精度
    private int Locat_Span;
    private LatLng lastRefreshPoint;
    private TopRightMenu mTopRightMenu;
    private int refresh_length;

    private boolean modeFlag = true;//设置初始时是普通模式
    static final String[] PERMISSION = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION //获取定位权限
    };
    /**
     * 是否退出程序 默认false
     **/
    private static boolean isExit = false;
    private Handler home_exit_handler = null;

    private Activity activity;

    // 写一个广播的内部类，当收到退出登录的动作时，结束当前的activity
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            activity.unregisterReceiver(this); // 这句话必须要写要不会报错，不写虽然能关闭，会报一堆错
            activity.finish();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现


        this.activity = getActivity();

        SDKInitializer.initialize(activity.getApplicationContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main,container,false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this.activity);

        Log.i("", "#### onCreate。。。。————————————————————>>>>>>>>>");

        CommonTools.setStateBarColor(activity);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        baseToolbar = toolbar;

//        toolbar.setTitle("");
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(activity, SettingActivity.class);
//                activity.startActivityForResult(i, 500);
//            }
//        });
        MapView.setMapCustomEnable(true);
//        setMapCustomFile(this);
        initView(view);
        checkGPS();
        initData();

        // 在当前的activity中注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("close_app");
        this.activity.registerReceiver(broadcastReceiver, filter); // 注册
    }

    /*初始化视图*/
    private void initView(View view) {
        adredList.clear();
        adgreenList.clear();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Intent i = new Intent(activity, SearchActivity.class);
                i.putExtra("query", query);
                startActivity(i);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        iv_center_location = (ImageView) view.findViewById(R.id.iv_center_location);
        mMapView = (MapView) view.findViewById(R.id.bmapView);
        mMapView.showScaleControl(false);//不显示比例尺
//        mMapView.showZoomControls(false);//不显示缩放控制

        // 隐藏百度的LOGO
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }

        // 不显示地图上比例尺
        mMapView.showScaleControl(false);

        // 不显示地图缩放控件（按钮控制栏）
        mMapView.showZoomControls(false);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setOnMapClickListener(this);
        mBaiduMap.setOnMarkerClickListener(this);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(null, true, null));

        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
                Log.e("mapStatusChange", "开始！");
            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
                //Log.e("mapStatusChange","正在改变！");
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                Log.e("mapStatusChange", "结束！");
                centerMarkerLL = mapStatus.target;//获得当前地图中心点坐标
                if (centerMarkerLL == null)
                    return;
                if (MultiTool.toDistance(centerMarkerLL.latitude, centerMarkerLL.longitude, lastRefreshPoint.latitude, lastRefreshPoint.longitude) > refresh_length) {
                    fab_refresh.setVisibility(View.VISIBLE);
                    CommonTools.setRotateAnim(fab_refresh, activity);
                    handler.sendEmptyMessageDelayed(3, 1000);
                    showCenterMarker();
                    getDataFromServer();
                }
            }
        });

    }

    /*初始化数据*/
    private void initData() {
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(activity);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        Locat_Span = activity.getSharedPreferences("setting", MODE_PRIVATE).getInt("local_space", 1000);
        option.setScanSpan(Locat_Span);
        option.setIsNeedAddress(true);
        option.setNeedDeviceDirect(true);
        mLocClient.setLocOption(option);
        mLocClient.start();
        View view = View.inflate(activity.getApplicationContext(), R.layout.marker, null);
        ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
        iv_icon.setImageResource(R.drawable.blue_big_fence);
        this.home_exit_handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                isExit = false;
            }
        };
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == StaticMember.REQUEST_GREEN_DATA_RESULT)//绿标加载完成
            {
                if (adgreenList == null || adgreenList.size() <= 0) {
                    // return;
                }else {

                    Log.e("准备绘制绿色广告marker", adgreenList.size() + "个");
                    boolean greenFlag = false;
                    for (int i = 0,len=adgreenList.size(); i < len; i++) {
                        if(adgreenList.get(i) instanceof AdGreenBean){
                            greenFlag = true;
                        }else{
                            greenFlag = false;
                        }
                        if(!greenFlag){
                            continue;
                        }
                        String iconStr= adgreenList.get(i).getIcon();
                        if (!CommonTools.isEnglishCharacter(iconStr)) {
                            adgreenList.remove(i);
                            continue;
                        }
                        String status = adgreenList.get(i).getStatus();
                        int index = 0;
                        if ("1".equals(status)) {    // 有效
                            index = DrawableTool.getValue("green_" + adgreenList.get(i).getIcon());
                        } else if ("-1".equals(status)) { // 处理中
                            index = DrawableTool.getValue("pink_" + adgreenList.get(i).getIcon());
                        } else if ("2".equals(status)) {  // 新建
                            index = DrawableTool.getValue("red_" + adgreenList.get(i).getIcon());
                        }
                        if(index ==0 ){
                            continue;
                        }
                        if(adgreenList.get(i).getGis_y() == null || adgreenList.get(i).getGis_x() == null){
                            continue;
                        }
                        if (bitmapDescriptorHashMap.containsKey(index)) {
                            Marker m = (Marker) mBaiduMap.addOverlay(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(adgreenList.get(i).getGis_y()), Double.parseDouble(adgreenList.get(i).getGis_x())))
                                    .animateType(MarkerOptions.MarkerAnimateType.grow)
                                    .period(10)
                                    .anchor(0.5f, 1.0f)
                                    .icon(bitmapDescriptorHashMap.get(index)));
                            Bundle b = new Bundle();
                            b.putSerializable("value", adgreenList.get(i));
                            b.putString("type", "green");
                            m.setExtraInfo(b);
                        } else if (index != 0) {
                            View view = View.inflate(activity.getApplicationContext(), R.layout.marker, null);
                            ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                            iv_icon.setImageResource(index);
                            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(view);
                            Marker m = (Marker) mBaiduMap.addOverlay(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(adgreenList.get(i).getGis_y()), Double.parseDouble(adgreenList.get(i).getGis_x())))
                                    .animateType(MarkerOptions.MarkerAnimateType.grow)
                                    .period(10)
                                    .anchor(0.5f, 1.0f)
                                    .icon(bitmapDescriptor));
                            Bundle b = new Bundle();
                            b.putSerializable("value", adgreenList.get(i));
                            b.putString("type", "green");
                            m.setExtraInfo(b);
                            bitmapDescriptorHashMap.put(index, bitmapDescriptor);
                        }
                    }
                }
            }
            if (msg.what == StaticMember.REQUEST_RED_DATE_RESULT)//红标加载完成
            {
                if (adredList == null || adredList.size() <= 0) {
                    // return;
                }
                else {
                    Log.e("准备绘制红色广告marker", adredList.size() + "个");
                    int index =0;
                    boolean redFlag = false;
                    for (int i = 0,len=adredList.size(); i < len; i++) {
                        if(adredList.get(i) instanceof AdRedBean){
                            redFlag = true;
                        }else{
                            redFlag = false;
                        }
                        if(!redFlag){
                            continue;
                        }

                        if(adredList.get(i).getGis_y() == null || adredList.get(i).getGis_x() == null){
                            continue;
                        }

                        index = DrawableTool.getValue("red_" + adredList.get(i).getIcon());
                        if(index ==0){
                            continue;
                        }
                        if (bitmapDescriptorHashMap.containsKey(index)) {
                            Marker m = (Marker) mBaiduMap.addOverlay(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(adredList.get(i).getGis_y()), Double.parseDouble(adredList.get(i).getGis_x())))
                                    .animateType(MarkerOptions.MarkerAnimateType.grow)
                                    .period(10)
                                    .anchor(0.5f, 1.0f)
                                    .icon(bitmapDescriptorHashMap.get(index)));
                            Bundle b = new Bundle();
                            b.putSerializable("value", adredList.get(i));
                            b.putString("type", "red");
                            m.setExtraInfo(b);
                        } else if (index != 0) {
                            View view = View.inflate(activity.getApplicationContext(), R.layout.marker, null);
                            ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                            iv_icon.setImageResource(index);
                            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(view);
                            Marker m = (Marker) mBaiduMap.addOverlay(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(adredList.get(i).getGis_y()), Double.parseDouble(adredList.get(i).getGis_x())))
                                    .animateType(MarkerOptions.MarkerAnimateType.grow)
                                    .period(10)
                                    .anchor(0.5f, 1.0f)
                                    .icon(bitmapDescriptor));
                            Bundle b = new Bundle();
                            b.putSerializable("value", adredList.get(i));
                            b.putString("type", "red");
                            m.setExtraInfo(b);
                            bitmapDescriptorHashMap.put(index, bitmapDescriptor);
                        }
                    }
                }
            }

            if (msg.what == 3) {
                //刷新动画结束，将刷新按钮消失掉
                fab_refresh.setVisibility(View.INVISIBLE);
            }
        }
    };


    /**
     * 展示一个SnackBar
     */
    public void showSnackBar(String message) {
        //去掉虚拟按键
//        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION //隐藏虚拟按键栏
//                | View.SYSTEM_UI_FLAG_IMMERSIVE //防止点击屏幕时,隐藏虚拟按键栏又弹了出来
//        );
        final Snackbar snackbar = Snackbar.make(activity.getWindow().getDecorView(), message, Snackbar.LENGTH_INDEFINITE);
        CommonTools.setSnackbarMessageTextColor(snackbar, getResources().getColor(R.color.orange));

        snackbar.setAction("知道了", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                //隐藏SnackBar时记得恢复隐藏虚拟按键栏,不然屏幕底部会多出一块空白布局出来,和难看
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }).show();
    }

    /*从数据库中查询中心点附近的坐标*/
    private void getDataFromServer() {
        //从服务器加载数据前清除当前地图上的所有标注
        mBaiduMap.clear();
        adredList=null;
        adgreenList=null;
        Log.e("从服务器加载信息", "getDataFromServer");
        lastRefreshPoint = centerMarkerLL;//每从服务器加载一次，刷新点也在这里
        if (HttpTools.isNetworkConnected(activity) == false) {
            showSnackBar("网络连接失败，请检查网络连接");
            return;
        }
        if (centerMarkerLL == null) {
            showSnackBar("获取中心点位置失败");
            return;
        }
        ExecutorService dataThreadPool = Executors.newSingleThreadExecutor();
        dataThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (adgreenList == null) {
                        adgreenList = new ArrayList<AdGreenBean>();
                    }
                    if (adgreenList.size() >= StaticMember.NUM || adgreenList.size() == 0 ) {
                        //Log.e("loadFromServer开始", "当前中心点：" + centerMarkerLL + "当前UID:" + StaticMember.USER.getUid() + "当前设备号：" + MultiTool.getSerialnum());
                        adgreenList = HttpTools.getJson(StaticMember.URL + "mob_gis.php",
                                "minx=1.0&maxx=136.0&miny=2.0&maxy=53.0&device_id=" + MultiTool.getSerialnum()
                                        + "&uid=" + StaticMember.USER.getUid()
                                        + "&order=board_id&page=" + page
                                        + "&lng=" + centerMarkerLL.latitude //数据库这里横纵坐标是反的，故反写
                                        + "&lat=" + centerMarkerLL.longitude
                                        + "&use_permissions="+StaticMember.use_permissions
                                        + "&type=" + loadTypeString,
                                StaticMember.ADLIST_GREEN);
                        Log.e("可以显示的绿标个数", adgreenList.size() + "===");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(StaticMember.REQUEST_GREEN_DATA_RESULT);

            }
        });
        dataThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    if (adredList == null) {
                        adredList = new ArrayList<AdRedBean>();
                    }
                    //StaticMember.USER.getUid()
                    if (adredList.size() >= StaticMember.NUM || adredList.size() == 0) {
                        adredList = HttpTools.getJson(StaticMember.URL + "mob_red.php",
                                "minx=1.0&maxx=136.0&miny=2.0&maxy=53.0&device_id="
                                        + MultiTool.getSerialnum()
                                        + "&uid=" + StaticMember.USER.getUid()
                                        + "&order=lid&page=" + page
                                        + "&lng=" + centerMarkerLL.latitude
                                        + "&lat=" + centerMarkerLL.longitude
                                        + "&use_permissions="+StaticMember.use_permissions
                                        + "&type=" + loadTypeString,
                                StaticMember.ADLIST_RED);
                        Log.e("878", adredList.size()+"===");
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(StaticMember.REQUEST_RED_DATE_RESULT);

            }
        });

    }

    /*获取当前屏幕中心点坐标并显示中心marker*/
    private void showCenterMarker() {
        Log.e("showCenterMarker", "加载中心点marker动画");
        PropertyValuesHolder tranxHolder = PropertyValuesHolder.ofFloat("translationY", -20f, 0f, -15f, 0f, -10f, 0f, -5f, 0f);
        PropertyValuesHolder sizeXHolder = PropertyValuesHolder.ofFloat("scaleX", 1.5f, 1.0f, 1.4f, 1.0f, 1.2f, 1.0f, 1.1f, 1.0f);
        PropertyValuesHolder sizeYHolder = PropertyValuesHolder.ofFloat("scaleY", 1.5f, 1.0f, 1.4f, 1.0f, 1.2f, 1.0f, 1.1f, 1.0f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(iv_center_location, tranxHolder, sizeXHolder, sizeYHolder);
        animator.setDuration(2000);
        //animator.setInterpolator(new BounceInterpolator());
        animator.start();
    }



    /*检查GPS是否打开*/
    private void checkGPS() {
        if (!CommonTools.checkGPS(activity))//如果未开启GPS,提醒
        {
            showSnackBar("GPS未打开，打开GPS可提高定位精度");
        }
    }


    private void showTypeChoose() {
        mTopRightMenu = new TopRightMenu(activity);

//添加菜单项
        List<com.zaaach.toprightmenu.MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new com.zaaach.toprightmenu.MenuItem(R.drawable.choose, "全部广告"));
        menuItems.add(new com.zaaach.toprightmenu.MenuItem(R.drawable.mentou, "门头店招"));
        menuItems.add(new com.zaaach.toprightmenu.MenuItem(R.drawable.huwai, "户外广告"));

        mTopRightMenu
                .setHeight(500)     //默认高度480
                .setWidth(430)      //默认宽度wrap_content
                .showIcon(true)     //显示菜单图标，默认为true
                .dimBackground(true)        //背景变暗，默认为true
                .needAnimationStyle(true)   //显示动画，默认为true
                .setAnimationStyle(R.style.TRM_ANIM_STYLE)
                .addMenuList(menuItems)
                .setOnMenuItemClickListener(new TopRightMenu.OnMenuItemClickListener() {
                    @Override
                    public void onMenuItemClick(int position) {
                        switch (position) {
                            case 1:
                                loadTypeString = "0";//门头。顺序就是这样，没有错。
                                iv_choose.setImageResource(R.drawable.mentou_w);
                                break;
                            case 2:
                                loadTypeString = "1";//户外
                                iv_choose.setImageResource(R.drawable.huwai_w);
                                break;
                            case 0:
                                loadTypeString = "2";//所有
                                iv_choose.setImageResource(R.drawable.menu_choose);
                                break;

                            default:
                                break;
                        }
                        getDataFromServer();//根据新选择的类型刷新
                    }
                })
                .showAsDropDown(iv_choose, -300, 35);//带偏移量
    }

    // 设置个性化地图config文件路径
    private void setMapCustomFile(Context context) {
        FileOutputStream out = null;
        InputStream inputStream = null;
        String moduleName = null;
        try {
            inputStream = context.getAssets()
                    .open("custom_config.txt");
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);

            moduleName = context.getFilesDir().getAbsolutePath();
            File f = new File(moduleName + "/" + "custom_config.txt");
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            out = new FileOutputStream(f);
            out.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        MapView.setCustomMapStylePath(moduleName + "/custom_config.txt");

    }

    private void addMarker(LatLng latLng) {

        double lat =  myLocationLL.latitude;
        double lon = myLocationLL.longitude;
        double x = latLng.longitude;
        double y = latLng.latitude;
        int distance = (int) MultiTool.toDistance(lon,lat, x, y);

        Log.i("","## 手动添加计算当前位置距离="+distance);
        if (distance > StaticMember.LENGTH + accuracy){
            // 不在添加范围内
            Snackbar sn = Snackbar.make(toolbar.getRootView(), "距离目标点过远，不允许添加！", BaseTransientBottomBar.LENGTH_INDEFINITE);
            sn.setAction("取消", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    canClickMap = false;
                    handler.sendEmptyMessage(StaticMember.REQUEST_GREEN_DATA_RESULT);
                    handler.sendEmptyMessage(StaticMember.REQUEST_RED_DATE_RESULT);
                }
            }) .show();
            CommonTools.setSnackbarMessageTextColor(sn, getResources().getColor(R.color.orange));
            return;
        }

        Intent i = new Intent(activity, RedMarkerDetail.class);
        AdRedBean ad = new AdRedBean();
        ad.setAddress(city + district + street);
        ad.setArea(district);
        ad.setCity(city);
        ad.setProvince(province);
        ad.setGis_x(String.valueOf(new DecimalFormat("#.000000").format( latLng.longitude)));
        ad.setGis_y(String.valueOf(new DecimalFormat("#.000000").format( latLng.latitude)));
        Bundle bundle = new Bundle();
        bundle.putFloat("accuracy", accuracy);
        bundle.putSerializable("AdRedBean", ad);
        i.putExtras(bundle);
        activity.startActivityForResult(i, 10020);//添加广告1001
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10010)//添加广告1001
        {
            Log.i("","--------------11111");
            getDataFromServer();
        } else if (requestCode == 10020)//修改广告1002
        {
            Log.i("","--------------22222");
            getDataFromServer();
        }
    }


    private void showPopWindow(Marker marker, List<AdRedBean> red_list, List<AdGreenBean> green_list, View rootView) {

        ArrayList<MarkerInfo> list = new ArrayList<MarkerInfo>();
        for (int m = 0,len=red_list.size(); m < len; m++) {
            if (MultiTool.toDistance(marker.getPosition().longitude, marker.getPosition().latitude,
                    Double.parseDouble(red_list.get(m).getGis_x()), Double.parseDouble(red_list.get(m).getGis_y())) < StaticMember.CLICK_LENGTH) {
                MarkerInfo mi = new MarkerInfo();
                mi.setCompany(red_list.get(m).getCompany());
                mi.setAddress(red_list.get(m).getAddress());
                mi.setMediatype("red_" + red_list.get(m).getIcon());
                mi.setAd_red(red_list.get(m));
                mi.setId(Integer.parseInt(red_list.get(m).getLid()));
                mi.setProblem(red_list.get(m).getQuestion());
                mi.setDateline(CommonTools.timeStamp2Date(red_list.get(m).getDateline(), "yyyy/MM/dd"));
                mi.setType("red");
                list.add(mi);
            }
        }

        for (int m = 0,len=green_list.size(); m <len; m++) {
            if (MultiTool.toDistance(marker.getPosition().longitude, marker.getPosition().latitude,
                    Double.parseDouble(green_list.get(m).getGis_x()), Double.parseDouble(green_list.get(m).getGis_y())) < 25) {
                MarkerInfo mi = new MarkerInfo();
                mi.setCompany(green_list.get(m).getCompany());
                mi.setAddress(green_list.get(m).getAddress());
                if (green_list.get(m).getStatus().equals("-1"))
                    mi.setMediatype("pink_" + green_list.get(m).getIcon());
                else
                    mi.setMediatype("green_" + green_list.get(m).getIcon());
                mi.setAd_green(green_list.get(m));
                mi.setId(Integer.parseInt(green_list.get(m).getBoard_id()));
                mi.setProblem("");
//                mi.setDateline(new SimpleDateFormat("yyyy/MM/dd").format(new Date()));
                mi.setDateline(CommonTools.timeStamp2Date(green_list.get(m).getDateline(), "yyyy/MM/dd"));
                mi.setType("green");
                list.add(mi);
            }
        }
        //多于一个记录
        if (list.size() > 1) {
            PopWindow popWindow = new PopWindow(activity, list, R.layout.pop_window, null);
            popWindow.showAtLocation(rootView, Gravity.CENTER | Gravity.BOTTOM, 0, 0);
        } else {
            //原先是点击某个marker就显示某个marker，但是marker变多了之后点击不方便，现在多于一个信息时，点击显示周围的信息
            if (marker.getExtraInfo().getString("type") == "green") {
                Intent i = new Intent(activity, GreenMarkerDetail.class);
                i.putExtra("AdGreenBean", marker.getExtraInfo().getSerializable("value"));
                activity.startActivityForResult(i, 1002);
            } else if (marker.getExtraInfo().getString("type") == "red") {
                Intent i = new Intent(activity, RedMarkerDetail.class);
                i.putExtra("AdRedBean", marker.getExtraInfo().getSerializable("value"));
                activity.startActivityForResult(i, 1002);
            }
        }
    }


    @OnClick(R.id.fab)
    public void fab(){
        if (!isMenuOpen) {
            CommonTools.doAnimateOpen(fab_add_now, 1, 2, 280);
            CommonTools.doAnimateOpen(fab_add_by_hand, 2, 2, 280);
        } else {
            CommonTools.doAnimateClose(fab_add_now, 1, 2, 280);
            CommonTools.doAnimateClose(fab_add_by_hand, 2, 2, 280);
        }
        isMenuOpen = !isMenuOpen;
    }
    @OnClick(R.id.fab_add_now)
    public void setFab_add_now(){
        if (isMenuOpen) {
            CommonTools.doAnimateClose(fab_add_now, 1, 2, 280);
            CommonTools.doAnimateClose(fab_add_by_hand, 2, 2, 280);
            isMenuOpen = !isMenuOpen;
            addMarker(myLocationLL);
        }
    }
    @OnClick(R.id.fab_add_by_hand)
    public void setFab_add_by_hand(){
        if (isMenuOpen) {
            CommonTools.doAnimateClose(fab_add_now, 1, 2, 280);
            CommonTools.doAnimateClose(fab_add_by_hand, 2, 2, 280);
            isMenuOpen = !isMenuOpen;
        }
        canClickMap = true;
        mBaiduMap.clear();

        Snackbar sn = Snackbar.make(toolbar.getRootView(), "请在屏幕上点击广告位置", BaseTransientBottomBar.LENGTH_INDEFINITE);
        sn.setAction("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canClickMap = false;
                handler.sendEmptyMessage(StaticMember.REQUEST_GREEN_DATA_RESULT);
                handler.sendEmptyMessage(StaticMember.REQUEST_RED_DATE_RESULT);
            }
        }) .show();
        CommonTools.setSnackbarMessageTextColor(sn, getResources().getColor(R.color.orange));

    }
    @OnClick(R.id.fab_refresh)
    public void refresh(){
        CommonTools.setRotateAnim(fab_refresh, activity);
        handler.sendEmptyMessageDelayed(3, 1000);
        showCenterMarker();
        getDataFromServer();
    }
    @OnClick(R.id.fab_local)
    public  void fab_local(){
        //首次定位结束后可以回到自己的位置
        if (!isFirstLoc) {
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(myLocationLL);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }
    @OnClick(R.id.fab_swich)
    public void fab_swich(){
        if(modeFlag){
            modeFlag = false;
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
            Toast.makeText(activity,"开启卫星模式",Toast.LENGTH_LONG).show();
        }else {
            modeFlag = true;
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
            Toast.makeText(activity, "开启普通模式", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 周围广告牌
     */
    @OnClick(R.id.fab_around)
    public void show_around(){
        Intent intent = new Intent(activity,AboutActivity.class);
        Bundle bundle = new Bundle();
        bundle.putFloat("accuracy", accuracy);
        bundle.putSerializable("AdRedList", (Serializable) adredList);
        bundle.putSerializable("AdGreenList", (Serializable) adgreenList);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick(R.id.iv_choose)
    public void iv_choose(){
        showTypeChoose();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (canClickMap) {
            addMarker(latLng);
            canClickMap = !canClickMap;
        }
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        showPopWindow(marker, adredList, adgreenList, toolbar);
        return false;
    }


    /**
     * 定位SDK监听函数
     */
    class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                MyLogger.Log().e("位置信息为空！");
                return;
            }
//            Log.e("定位", "*****************");
            if (fab_refresh.getVisibility() == View.INVISIBLE)
                refresh_count++;
            if (refresh_count > Locat_Span / 50) {
                fab_refresh.setVisibility(View.VISIBLE);
                refresh_count = 0;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            myLocationLL = new LatLng(location.getLatitude(),
                    location.getLongitude());
            province = location.getProvince();
            city = location.getCity();
            district = location.getDistrict();
            accuracy = location.getRadius();
            street = location.getStreet() + location.getStreetNumber();
            // Log.e("当前的地址是",address+""+province+city+district+location.getStreet()+location.getStreetNumber());
            if (isFirstLoc) {
                Log.e("首次定位", isFirstLoc + "");
                isFirstLoc = false;
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(myLocationLL).zoom(20.0f);
                lastRefreshPoint = myLocationLL;
                centerMarkerLL = myLocationLL;
                getDataFromServer();
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // 退出时销毁定位
        if(null != mLocClient){
            mLocClient.unRegisterLocationListener(myListener);
            mLocClient.stop();
        }
        try{
            if(null != broadcastReceiver){
                activity.unregisterReceiver(broadcastReceiver);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("mainActivity_onResume", "执行");
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        searchView.setFocusable(true);
        searchView.setFocusableInTouchMode(true);
        //重置定位设置
        Log.e("本地间隔", activity.getSharedPreferences("setting", MODE_PRIVATE).getInt("local_space", 1000) + "");
        mLocClient.getLocOption().setScanSpan(activity.getSharedPreferences("setting", MODE_PRIVATE).getInt("local_space", 1000));
        refresh_length = activity.getSharedPreferences("setting", MODE_PRIVATE).getInt("refresh_length", 150);
        Log.e("本地刷新距离", activity.getSharedPreferences("setting", MODE_PRIVATE).getInt("refresh_length", 150) + "");

        Log.i(",","### FragmentXuncha onResume.....");

        // getDataFromServer();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//
//        Log.e("main_onWindowFocus", "执行");
//        if (hasFocus)//拉下状态栏再拉回来会弹出输入键盘，现在处理不让它显示
//        {
//            searchView.setFocusable(true);
//            searchView.setFocusableInTouchMode(true);
//        }
//    }
//
//    /**
//     * TODO 返回键值事件
//     */
//    @SuppressLint("WrongConstant")
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        /** 获取网页标题 **/
//        // String name = this.getTextEditValue(this.titlebar_name_text);
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            if (!isExit) {
//                isExit = true;
//                Toast.makeText(activity, "再按一次退出程序!",
//                        Toast.LENGTH_SHORT).show();
//                this.home_exit_handler.sendEmptyMessageDelayed(0, 2500);
//            } else {
//                AppManager.getAppManager().finishAllActivity();
//                Intent localIntent2 = new Intent("android.intent.action.MAIN");
//                localIntent2.addCategory("android.intent.category.HOME");
//                localIntent2.setFlags(67108864);
//                startActivity(localIntent2);
//                new Handler().postDelayed(new Runnable()
//                {
//                    public void run()
//                    {
//                        System.exit(0);
//                    }
//                } , 20L);
//                android.os.Process.killProcess(android.os.Process.myPid());
//            }
//        }
//
//        return false;
//    }


}
