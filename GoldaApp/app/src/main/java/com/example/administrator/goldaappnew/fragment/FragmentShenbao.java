package com.example.administrator.goldaappnew.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.example.administrator.goldaappnew.R;
import com.example.administrator.goldaappnew.adapter.LazyAdapter;
import com.example.administrator.goldaappnew.bean.BoardBean;
import com.example.administrator.goldaappnew.bean.JsonBean;
import com.example.administrator.goldaappnew.common.JsonFileReader;
import com.example.administrator.goldaappnew.common.MyLogger;
import com.example.administrator.goldaappnew.jpush.LocalBroadcastManager;
import com.example.administrator.goldaappnew.staticClass.StaticMember;
import com.example.administrator.goldaappnew.utils.AssistUtil;
import com.example.administrator.goldaappnew.utils.CaremaUtil;
import com.example.administrator.goldaappnew.utils.CommonTools;
import com.example.administrator.goldaappnew.utils.DateHelper;
import com.example.administrator.goldaappnew.utils.FileUtil;
import com.example.administrator.goldaappnew.utils.HttpTools;
import com.example.administrator.goldaappnew.utils.MultiTool;
import com.example.administrator.goldaappnew.utils.SFTPChannel;
import com.example.administrator.goldaappnew.utils.httpsupport.AsyncTaskOwner;
import com.example.administrator.goldaappnew.view.MyDialogFileChose;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FragmentShenbao extends BaseFragment implements MyDialogFileChose.OnButtonClickListener,AsyncTaskOwner {

    /** Called when the activity is first created. */
    private final static String TAG = "FragmentShenbao";
    private Activity activity;
    private ViewPager viewPager = null;
    private List<View> viewContainter = new ArrayList<View>();   //存放容器
    private ViewPagerAdapter viewPagerAdapter = null;   //声明适配器
    private TabHost mTabHost = null;
    private TabWidget mTabWidget = null;

    // 省、市、区
    private ArrayList<JsonBean> options1Items = new ArrayList<JsonBean>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<ArrayList<String>>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<ArrayList<ArrayList<String>>>();

    private String province = "";
    private String city = "";
    private String area = "";

    // 广告牌类型
    private ArrayList<JsonBean> optionsType1Items = new ArrayList<JsonBean>();
    private ArrayList<ArrayList<String>> optionsType2Items = new ArrayList<ArrayList<String>>();
    private ArrayList<ArrayList<ArrayList<String>>> optionsType3Items = new ArrayList<ArrayList<ArrayList<String>>>();

    private String icon_type = "";   // 请选择分类（类型）
    private String icon_class = "";  // 请选择种类（类型）
    private String icon_cnname = ""; // 请选择标识（类型）


    // 分类规划下拉选择
    private ArrayList<JsonBean> optionsPlan1Items = new ArrayList<JsonBean>();
    private ArrayList<ArrayList<String>> optionsPlan2Items = new ArrayList<ArrayList<String>>();

    private Unbinder unbinder;
    @BindView(R.id.tv_city_area)
    TextView tv_city_area;  // 省份、城市、地区
    @BindView(R.id.edittext_adress)
    EditText edittext_adress;   // 设置地点
    @BindView(R.id.edittext_area_line)
    EditText edittext_area_line;    // 路段
    @BindView(R.id.edittext_company)
    EditText edittext_company; //申请公司名称
    @BindView(R.id.edittext_company_address)
    EditText edittext_company_address;          // 公司地址
    @BindView(R.id.edittext_person)
    EditText edittext_person; // 法定代表人
    @BindView(R.id.edittext_contact)
    EditText edittext_contact; // 联系电话号码
    @BindView(R.id.edittext_process_contact)
    EditText edittext_process_contact; // 联系人
    @BindView(R.id.edittext_process_tel)
    EditText edittext_process_tel; // 联系人电话号码
    @BindView(R.id.edittext_email)
    EditText edittext_email; // 联系邮箱
    @BindView(R.id.tv_icon_type)
    TextView tv_icon_type; // 类型
    @BindView(R.id.edittext_material)
    EditText edittext_material; // 广告牌材质
    @BindView(R.id.edittext_material_time)
    TextView edittext_material_time; // 材质有效期
    @BindView(R.id.edittext_wt)
    EditText edittext_wt; // 外凸(米)
    @BindView(R.id.edittext_model)
    EditText edittext_model; // 数量(个)
    @BindView(R.id.edittext_facenum)
    EditText edittext_facenum; // 展示面数(面)
    @BindView(R.id.edittext_ad_x)
    EditText edittext_ad_x; // 长度(米)
    @BindView(R.id.edittext_ad_y)
    EditText edittext_ad_y; // 宽度(米)
    @BindView(R.id.edittext_ad_s)
    EditText edittext_ad_s; // 面积(平方米)
    @BindView(R.id.edittext_li_height)
    EditText edittext_li_height; // 离地高度(米)


    @BindView(R.id.tv_plan)
    TextView tv_plan;  // 规划分类
    @BindView(R.id.edittext_json_id)
    EditText edittext_json_id; // 规划分类ID

    private String cat_name = "";    // 计划分类总类
    private String plan_name = ""; // 计划名称

    // 申报项目保存
//    @BindView(R.id.add_save)
    RelativeLayout add_save;

    /**
     * 附件上传ViewPage
     */
    private MyDialogFileChose myDialogFileChose;// 图片选择对话框
    public static final int NONE = 0;
    private ListView addAttachListView;
//    private AttachListViewAdapter attachListViewAdapter;
    private ArrayList<Map<String, String>> listAttachData;
    private LazyAdapter lazyAdapter;

    private String[] attachArray = new String[]{};

    private File imageDir = new File(AssistUtil.getMemoryPath()+"uploadImage/");//照片保存路径文件夹
    private String ImagefilePath = "";  // 上传文件图片路径
    private String ImageFileName = "";  // 上传文件图片名称
    private String fileSuffix = "jpg";     // 上传文件后缀
    private int choseFileIndex = 0;
    private String path; // 选择文件路径


    private String today = "";  // 文件上传日期文件夹
    private String upload_file_result = ""; // 文件上传结果
    private String upload_save_result = ""; // 数据保存结果
    private ProgressDialog mpDialog;          // 等待框
    private BoardBean boardBean;              // 保存数据对象
    private String de_id = "0";       // 申报ID，0表示新增，否则修改
    private boolean is_allow_update = true; // 是否允许更新， true 是， false 否

    protected MyBroadcastReceiver myBroadcastReceiver ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        View view = inflater.inflate(R.layout.activity_fragment_shenbao, container,false);
        // 加载fragment的布局控件（通过layout根元素加载)
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this.activity);
        initMyTabHost(view);

//        unbinder = ButterKnife.bind(this, view);

        add_save = (RelativeLayout) view.findViewById(R.id.add_save);
        add_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log.i("","点击保存按钮了。。。。");
                submitSaveData();
            }
        });

        // 绑定组件
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        initViewPagerContainter();  //初始viewPager
        viewPagerAdapter = new ViewPagerAdapter();
        //设置adapter的适配器
        viewPager.setAdapter(viewPagerAdapter);
        //设置viewPager的监听器
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            //当 滑动 切换时
            @Override
            public void onPageSelected(int position) {
                mTabWidget.setCurrentTab(position);
            }
            @Override
            public void onPageScrollStateChanged(int arg0) {
                //arg0 ==1的时表示正在滑动，arg0==2的时表示滑动完毕了，arg0==0的时表示什么都没做。
                if(arg0 == 0){

                }else if(arg0 == 1){

                }else if(arg0 == 2){
                    int viewIndex = viewPager.getCurrentItem();
                    if(viewIndex == 0){
                        mTabHost.setCurrentTab(0);
                    }else{
                        mTabHost.setCurrentTab(1);
                    }
                }
            }
        });
        //TabHost的监听事件
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if(tabId.equals("tab1")){
                    viewPager.setCurrentItem(0);
                }else{
                    viewPager.setCurrentItem(1);
                }
                // 切换Tab样式
                updateTabStyle();
            }
        });

        //解决开始时不显示viewPager
        mTabHost.setCurrentTab(1);
        mTabHost.setCurrentTab(0);

        // 初始化省市区数据
        initJsonData();

        // 初始化广告牌类型数据
        initAdTypeJsonData();

        // 初始规划分类
        initPlanJsonData();

        /*
         * 防止键盘挡住输入框 不希望遮挡设置activity属性 android:windowSoftInputMode="adjustPan"
         * 希望动态调整高度 android:windowSoftInputMode="adjustResize"
         */
        this.activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        // 锁定屏幕
        this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }


    private Calendar calendar;

    /**
     * 选择日期控件
     * @param textView
     */
    private void showDatePicDialog(final TextView textView){

        calendar= Calendar.getInstance();
        calendar.setTime(new Date());
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog.OnDateSetListener datePickerDialog= new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                String mMonth,mDay,mTime;
                if (i1<9)
                    mMonth="0"+(i1+1);
                else
                    mMonth=""+(i1+1);
                if (i2<10)
                    mDay="0"+i2;
                else
                    mDay=""+i2;

                mTime=i+"-"+mMonth+"-"+mDay;

                textView.setText(mTime);

            }
        };
        new DatePickerDialog(this.activity,datePickerDialog,year,month,day).show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册广播
        registerReceiver();
    }

    /**
     * 注册广播接收器
     */
    public void registerReceiver() {
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        myBroadcastReceiver = new MyBroadcastReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("myaction");
        localBroadcastManager.registerReceiver(myBroadcastReceiver, intentFilter);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction() ;
            Log.i("","## action ==="+action);
            if ( "myaction".equals( action )){
                //Log.d( "tttt 消息：" + intent.getStringExtra( "data" )  , "线程： " + Thread.currentThread().getName() ) ;
                BoardBean boardBean = (BoardBean) intent.getSerializableExtra("BoardBean");
                if(null != boardBean){
                    Log.e("","##getDe_id =="+boardBean.getDe_id());

                    de_id = boardBean.getDe_id();
                    setFormValus(boardBean);
                }
//                // 这地方只能在主线程中刷新UI,子线程中无效，因此用Handler来实现
//                new Handler().post(new Runnable() {
//                    public void run() {
//                        //在这里来写你需要刷新的地方
//
//                    }
//                });
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
            if(null != localBroadcastManager){
                //取消注册广播,防止内存泄漏
                localBroadcastManager.unregisterReceiver( myBroadcastReceiver );
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    //初始化TabHost
    public void initMyTabHost(View view){
        //绑定id
        mTabHost = (TabHost) view.findViewById(android.R.id.tabhost);
        mTabHost.setup();
        mTabWidget = mTabHost.getTabWidget();
        /**
         * newTabSpec（）   就是给每个Tab设置一个ID
         * setIndicator()   每个Tab的标题
         * setCount()       每个Tab的标签页布局
         */
        mTabHost.addTab(mTabHost.newTabSpec("tab1").setContent(R.id.tab1).setIndicator("基本信息"));
        mTabHost.addTab(mTabHost.newTabSpec("tab2").setContent(R.id.tab2).setIndicator("图片信息"));

        updateTabStyle();
    }

    private void updateTabStyle(){
        TabWidget localTabWidget = this.mTabHost.getTabWidget();
        int i = 0;
        //设置选项卡title字体大小和样式
        while(i < localTabWidget.getChildCount()){
            TextView localTextView = (TextView)localTabWidget.getChildAt(i).findViewById(android.R.id.title);
            localTextView.setTextSize(14.0F);

            //设置背景图
            if (mTabHost.getCurrentTab() == i) {
                localTextView.setTextColor(getResources().getColorStateList(R.color.orange));
                localTabWidget.getChildAt(i).setBackgroundResource(R.color.white);
            }else {
                localTextView.setTextColor(this.getResources().getColorStateList(R.color.heise));
                localTabWidget.getChildAt(i).setBackgroundResource(R.color.gray_light);
            }
            i++;
        }
    }

    //初始化viewPager
    public void initViewPagerContainter(){
        //建立两个view的样式，并找到他们
        View view_1 = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.fragment_shenbao_viewpage1,null);
        View view_2 = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.fragment_shenbao_viewpage2,null);

        //加入ViewPage的容器
        viewContainter.add(view_1);
        viewContainter.add(view_2);

        unbinder = ButterKnife.bind(this, view_1);

        // 保存按钮隐藏（选择省市区、广告牌类型时显示）
        add_save.setVisibility(View.GONE);

//        edittext_material_time = (TextView) view.findViewById(R.id.edittext_material_time);
        edittext_material_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicDialog(edittext_material_time);
            }
        });

        /**
         * 长、宽计算出面积 监听
         */
        edittext_ad_x.addTextChangedListener(new EditChangedListener());
        edittext_ad_y.addTextChangedListener(new EditChangedListener());

        // view2 初始化
        initViewPage2UI(view_2);

        // 设置选择省市区监听
        setListener();

    }

    private void initViewPage2UI(View view){
        myDialogFileChose = new MyDialogFileChose(this.activity);
        myDialogFileChose.setOnButtonClickListener(this);

        addAttachListView = (ListView) view.findViewById(R.id.addAttachListView);
        initViewPageFileAdapterUI();
    }

    private void initViewPageFileAdapterUI(){
        listAttachData = new ArrayList<>();
        attachArray = new String[]{"设置申请书","公司营业执照","个人身份证明","效果图","实景图","规格平面图","产权证书或\n房屋租赁协议"
                ,"载体安全证明","相关书面协议","场地租用合同","结构设计图","施工图","施工说明书","建安资质证书","施工保证书","规划拍卖意见",
                "授权人身份证","授权委托书","规划相关截图"};
        for(int i = 0; i < attachArray.length; i++ ){
            Map<String,String> map = new HashMap<>();
            map.put("title",attachArray[i]);
            map.put("file_path","");
            map.put("file_name","");
            if("规划相关截图".equals(attachArray[i])){
                // 规划相关截图 (20181028 新加字段：b_attach_21 )
                map.put("file_key","b_attach_21");
            }else{
                map.put("file_key","b_attach_"+(i+1));
            }
            map.put("file_id",""+i);
            listAttachData.add(map);
        }

        lazyAdapter = new LazyAdapter(activity, AddFileDialogControl, RemoveFileDialogControl,listAttachData);
        addAttachListView.setAdapter(lazyAdapter);
    }

    /**
     * 保存成功后，清除表单内容
     */
    private void clearFrom(){
        de_id = "0";
        province = "";
        city = "";
        area = "";

        icon_type = "";
        icon_class = "";
        icon_cnname = "";

        tv_city_area.setText("");
        edittext_adress.setText("");
        edittext_area_line.setText("");
        edittext_company.setText("");
        edittext_company_address.setText("");
        edittext_person.setText("");
        edittext_contact.setText("");
        edittext_process_contact.setText("");
        edittext_process_tel.setText("");
        edittext_email.setText("");
        tv_icon_type.setText("");
        edittext_material.setText("");
        edittext_material_time.setText("");
        edittext_wt.setText("");
        edittext_model.setText("");
        edittext_facenum.setText("");
        edittext_ad_x.setText("");
        edittext_ad_y.setText("");
        edittext_ad_s.setText("");
        edittext_li_height.setText("");

        tv_plan.setText("");
        edittext_json_id.setText("");

        // 隐藏保存按钮
        add_save.setVisibility(View.GONE);

        // 清空图片列表
        for(int i=0; i<listAttachData.size(); i++){
            listAttachData.get(i).put("file_name","");
        }
        lazyAdapter.notifyDataSetChanged();
    }

    /**
     * 设置填充表单内容
     * @param boardBean
     */
    private void setFormValus(BoardBean boardBean){
        province = boardBean.getProvince();
        city = boardBean.getCity();
        area = boardBean.getArea();

        icon_type = boardBean.getIcon_type();
        icon_class = boardBean.getIcon_class();
        icon_cnname = boardBean.getIcon_cnname();

        tv_city_area.setText(boardBean.getProvince()+" "+boardBean.getCity()+" "+boardBean.getArea());
        tv_icon_type.setText(boardBean.getIcon_type() + " "+boardBean.getIcon_class()+" "+boardBean.getIcon_cnname());

        edittext_adress.setText(boardBean.getAddress());
        edittext_area_line.setText(boardBean.getArea_line());
        edittext_company.setText(boardBean.getCompany());
        edittext_company_address.setText(boardBean.getCompany_address());
        edittext_person.setText(boardBean.getPerson());
        edittext_contact.setText(boardBean.getContact());
        edittext_process_contact.setText(boardBean.getProcess_contact());
        edittext_process_tel.setText(boardBean.getProcess_tel());
        edittext_email.setText(boardBean.getEmail());

        edittext_material.setText(boardBean.getMaterial());
        edittext_material_time.setText(boardBean.getMaterial_time());

        edittext_wt.setText(boardBean.getWt());
        edittext_model.setText(boardBean.getModel());
        edittext_facenum.setText(boardBean.getFacenum());
        edittext_ad_x.setText(boardBean.getAd_x());
        edittext_ad_y.setText(boardBean.getAd_y());
        edittext_ad_s.setText(boardBean.getAd_s());

        Log.i("","## 设置面积："+boardBean.getAd_s());
        if(null != boardBean.getAd_x() && !"".equals(boardBean.getAd_x()) && null != boardBean.getAd_y() && !"".equals(boardBean.getAd_y())){
            double adX = Double.parseDouble(boardBean.getAd_x());
            double adY = Double.parseDouble(boardBean.getAd_y());
            double adS = adX * adY;
            edittext_ad_s.setText(adS+"");
        }else{
            edittext_ad_s.setText("");
        }

        edittext_li_height.setText(boardBean.getLi_height());
        edittext_json_id.setText(boardBean.getJson_id());

        // 1查看，0处理
        if("1".equals(boardBean.getConfirm_status())){
            // 查看隐藏保存按钮
            add_save.setVisibility(View.GONE);
        }else{
            // 处理显示保存按钮
            add_save.setVisibility(View.VISIBLE);
        }
        Log.i("","加载信息了啦啦啦啦");
        // 显示附件列表
        listAttachData.get(0).put("file_name",boardBean.getB_attach_1());
        listAttachData.get(1).put("file_name",boardBean.getB_attach_2());
        listAttachData.get(2).put("file_name",boardBean.getB_attach_3());
        listAttachData.get(3).put("file_name",boardBean.getB_attach_4());
        listAttachData.get(4).put("file_name",boardBean.getB_attach_5());
        listAttachData.get(5).put("file_name",boardBean.getB_attach_6());
        listAttachData.get(6).put("file_name",boardBean.getB_attach_7());
        listAttachData.get(7).put("file_name",boardBean.getB_attach_8());
        listAttachData.get(8).put("file_name",boardBean.getB_attach_9());
        listAttachData.get(9).put("file_name",boardBean.getB_attach_10());
        listAttachData.get(10).put("file_name",boardBean.getB_attach_11());
        listAttachData.get(11).put("file_name",boardBean.getB_attach_12());
        listAttachData.get(12).put("file_name",boardBean.getB_attach_13());
        listAttachData.get(13).put("file_name",boardBean.getB_attach_14());
        listAttachData.get(14).put("file_name",boardBean.getB_attach_15());
        listAttachData.get(15).put("file_name",boardBean.getB_attach_16());
        listAttachData.get(16).put("file_name",boardBean.getB_attach_17());
        listAttachData.get(17).put("file_name",boardBean.getB_attach_18());

        if(listAttachData.size() >=21){
            if("现场核查意见书".equals(listAttachData.get(18).get("title"))){
                // 现场核查意见书 b_attach_19, 备案通知书 b_attach_20 (审核完成了以后让申报的人看到)
                listAttachData.get(18).put("file_name",boardBean.getB_attach_19());
            }
            if("备案通知书".equals(listAttachData.get(19).get("title"))){
                // 现场核查意见书 b_attach_19, 备案通知书 b_attach_20 (审核完成了以后让申报的人看到)
                listAttachData.get(19).put("file_name",boardBean.getB_attach_20());
            }
            if("规划相关截图".equals(listAttachData.get(20).get("title"))){
                // 后补字段
                listAttachData.get(20).put("file_name",boardBean.getB_attach_21());
            }
        }else if(listAttachData.size() >=19){
            if("规划相关截图".equals(listAttachData.get(18).get("title"))){
                // 后补字段
                listAttachData.get(18).put("file_name",boardBean.getB_attach_21());
            }
        }

        lazyAdapter.notifyDataSetChanged();
    }

    /**
     * 添加图片信息选择弹出框
     */
    private LazyAdapter.IDialogControl AddFileDialogControl = new LazyAdapter.IDialogControl() {
        @Override
        public void onShowDialog() {
            // TODO Auto-generated method stub
            myDialogFileChose.show();
        }
        @Override
        public void getPosition(int position) {
            // TODO Auto-generated method stub
            choseFileIndex = position;
        }
    };

    private void showMessage(String message){
        Toast.makeText(this.activity,message,Toast.LENGTH_SHORT).show();
    }

    /**
     * 移除图片信息附件选择弹出框
     */
    private LazyAdapter.IDialogControl RemoveFileDialogControl = new LazyAdapter.IDialogControl() {
        @Override
        public void onShowDialog() {
            // TODO Auto-generated method stub
            removeFileDialog();
        }
        @Override
        public void getPosition(int position) {
            // TODO Auto-generated method stub
            choseFileIndex = position;
        }
    };

    /*
     * Dialog对话框提示用户删除操作 position为删除图片位置
     */
    protected void removeFileDialog() {
        final int position = choseFileIndex;
        if("".equals(listAttachData.get(position).get("file_name"))){
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("确认移除吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                listAttachData.get(position).put("file_path","");
                listAttachData.get(position).put("file_name","");
                lazyAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

//    @OnClick({R.id.add_save})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.add_save:
//                Log.i(TAG,"点击保存了。。。222222222");
//                submitSaveData();
//                break;
//            default:
//                break;
//        }
//    }

    /**
     * 保存数据
     */
    private void submitSaveData(){

        if(null == icon_cnname || "".equals(icon_cnname)){
            showMessage("请选择广告牌类型！");
            return;
        }

        if(!rightAdX || !rightAdY){
            showMessage("广告牌长度 或 宽度输入错误！");
            return;
        }

        boardBean = new BoardBean();
        boardBean.setDe_id(de_id);
        Log.i("","## 保存的广告牌ID="+de_id);
        boardBean.setUid(StaticMember.USER.getUid());
        boardBean.setProvince(province);
        boardBean.setCity(city);
        boardBean.setArea(area);
        boardBean.setIcon_type(icon_type);
        boardBean.setIcon_class(icon_class);
        boardBean.setIcon_cnname(icon_cnname);


        String icon = "";
        if("店招牌匾设施".equals(icon_cnname)){
            icon = "small_door";
        }else{
            icon = MultiTool.getADType(icon_cnname);
        }
        boardBean.setIcon(icon);  // 非用户填写,通过icon_cnname的中文，转换出对应的英文，用在鸟瞰图上显示图标
        boardBean.setAddress(edittext_adress.getText().toString()); // 设置地点
        boardBean.setArea_line(edittext_area_line.getText().toString());    //  路段
        boardBean.setCompany(edittext_company.getText().toString());    // 申请公司名称
        boardBean.setCompany_address(edittext_company_address.getText().toString());    // 公司地址
        boardBean.setPerson(edittext_person.getText().toString());      // 法定代表人
        boardBean.setContact(edittext_contact.getText().toString());    // 联系电话号码
        boardBean.setProcess_contact(edittext_process_contact.getText().toString());    // 联系人
        boardBean.setProcess_tel(edittext_process_tel.getText().toString());    // 联系电话号码
        boardBean.setEmail(edittext_email.getText().toString());    // 联系邮箱
        boardBean.setMaterial(edittext_material.getText().toString()); // 广告牌材质
        boardBean.setMaterial_time(edittext_material_time.getText().toString()); // 广告牌材质有效期
        boardBean.setWt(edittext_wt.getText().toString());  // 外凸(米)
        boardBean.setModel(edittext_model.getText().toString());    // 数量(个)
        boardBean.setFacenum(edittext_facenum.getText().toString()); // 展示面数(面)
        boardBean.setAd_x(edittext_ad_x.getText().toString());  // 长度(米)
        boardBean.setAd_y(edittext_ad_y.getText().toString());  // 宽度(米)
        boardBean.setAd_x(edittext_ad_x.getText().toString());  // 面积(平方米)
        boardBean.setLi_height(edittext_li_height.getText().toString());    // 离地高度(米)

        boardBean.setCat_name(cat_name);    // 计划分类
        boardBean.setPlan_name(plan_name);  // 计划分类名称
        boardBean.setJson_id(edittext_json_id.getText().toString());    // 分类ID

        boardBean.setB_attach_1(listAttachData.get(0).get("file_name"));
        boardBean.setB_attach_2(listAttachData.get(1).get("file_name"));
        boardBean.setB_attach_3(listAttachData.get(2).get("file_name"));
        boardBean.setB_attach_4(listAttachData.get(3).get("file_name"));
        boardBean.setB_attach_5(listAttachData.get(4).get("file_name"));
        boardBean.setB_attach_6(listAttachData.get(5).get("file_name"));
        boardBean.setB_attach_7(listAttachData.get(6).get("file_name"));
        boardBean.setB_attach_8(listAttachData.get(7).get("file_name"));
        boardBean.setB_attach_9(listAttachData.get(8).get("file_name"));
        boardBean.setB_attach_10(listAttachData.get(9).get("file_name"));
        boardBean.setB_attach_11(listAttachData.get(10).get("file_name"));
        boardBean.setB_attach_12(listAttachData.get(11).get("file_name"));
        boardBean.setB_attach_13(listAttachData.get(12).get("file_name"));
        boardBean.setB_attach_14(listAttachData.get(13).get("file_name"));
        boardBean.setB_attach_15(listAttachData.get(14).get("file_name"));
        boardBean.setB_attach_16(listAttachData.get(15).get("file_name"));
        boardBean.setB_attach_17(listAttachData.get(16).get("file_name"));
        boardBean.setB_attach_18(listAttachData.get(17).get("file_name"));

        boardBean.setB_attach_21(listAttachData.get(18).get("file_name"));

        int imageSize = 0;
        for(int i=0; i<listAttachData.size() ; i++){
            if(!"".equals(listAttachData.get(i).get("file_name"))){
                imageSize ++;
            }
        }

        String msgTips = "";
        if(imageSize == 0){
            msgTips = "<font color=red>您尚未上传图片文件，确定要保存吗?</font>";
        }else if(imageSize < attachArray.length){
            msgTips = "您上传了"+imageSize+"张文件，剩余"+(attachArray.length - imageSize)+"张文件未上传，确认保存吗?";
        }else{
            msgTips = "确定要保存吗?";
        }
        msgTips += "<br/><br/><font color=red>*提交后不能修改</font>";

        Log.i(TAG,"### msgTips="+msgTips+"; size="+imageSize+"; attachArray.length="+attachArray.length);
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setMessage(Html.fromHtml(msgTips));
        builder.setTitle("提示");
        builder.setPositiveButton("暂存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                add_save.setVisibility(View.GONE);
                boardBean.setConfirm_status("0");
                saveData();
            }
        });
        builder.setNeutralButton("提交", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                add_save.setVisibility(View.GONE);
                boardBean.setConfirm_status("1");
                saveData();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                add_save.setVisibility(View.VISIBLE);
            }
        });
        builder.show();
    }

    private void saveData(){
        mpDialog = new ProgressDialog(activity);
        mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
        mpDialog.setMessage("正在上传数据,请勿关闭当前窗口");
        mpDialog.setIndeterminate(false);// 设置进度条是否为不明?
        mpDialog.setCancelable(true);// 设置进度条是否可以按?回键取消
        mpDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Gson gson = new Gson();
                    String dataStr = gson.toJson(boardBean);
                    Log.i("dataStr", " #### dataStr="+dataStr);
                    upload_save_result = HttpTools.sendPostRequest(StaticMember.URL + "mob_declare.php", dataStr);
                    // MyLogger.Log().i("申报项目保存数据结果："+upload_save_result);

                    Message msg = new Message();
                    msg.arg1 = StaticMember.SAVE_SHENBAO_DATA;
                    if (!"0".equals(upload_save_result)) {
                        msg.what = 1;
                    }else{
                        msg.what = 0;
                    }
                    mHandler.sendMessage(msg);
                    Log.e("1上传返回的结果", "upload_save_result=" + upload_save_result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    /**
     * 上传文件
     */
    private void uploadFileResult(){
        if("".equals(ImagefilePath)){
            showMessage("你还没有选择图片！");
            return;
        }
        mpDialog = new ProgressDialog(this.activity);
        mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
        mpDialog.setMessage("正在上传数据,请勿关闭当前窗口");
        mpDialog.setIndeterminate(false);// 设置进度条是否为不明?
        mpDialog.setCancelable(true);// 设置进度条是否可以按?回键取消
        mpDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    today = DateHelper.getToday("yyyy-MM-dd");

                    if(!"".equals(fileSuffix)){
                        ImageFileName = DateHelper.getToday("yyyyMMddHHmmssSSS")+"_"+StaticMember.USER.getUid()+"_"+ (int) (Math.random() * 1000) + "."+fileSuffix;
                    }else{
                        ImageFileName = DateHelper.getToday("yyyyMMddHHmmssSSS")+"_"+StaticMember.USER.getUid()+"_"+ (int) (Math.random() * 1000) + ".jpg";
                    }
                    // MyLogger.Log().i("上传文件名称："+ImageFileName+";  文件路径："+today+"; "+";  文件后缀名："+fileSuffix);
                    upload_file_result = SFTPChannel.getChannel(ImagefilePath, StaticMember.FTPRemotePath + today, ImageFileName, 10000);
                    Message msg = new Message();
                    msg.arg1 = StaticMember.UPLOAD_IMAGE_RESULT;
                    if (upload_file_result == "1") {
                        msg.what = 1;
                    }else{
                        msg.what = 0;
                    }
                    mHandler.sendMessage(msg);
                    Log.e("1上传返回的结果", "upload_file_result=" + upload_file_result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    // 定义一个消息处理handler
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.arg1 == StaticMember.UPLOAD_IMAGE_RESULT){
                if (msg.what == 1) {
                    if(null != mpDialog){
                        mpDialog.cancel();
                    }
                    // 获取上传图片的文件名称显示在lieb列表中
                    // String fileName = FileUtil.getFileNameByPath(ImagefilePath);
                    listAttachData.get(choseFileIndex).put("file_path",ImagefilePath);
                    listAttachData.get(choseFileIndex).put("file_name",StaticMember.RemotePath + today + "/" + ImageFileName);
                    // MyLogger.Log().i("## 操作成功::: ImageFileName："+ ImageFileName);

//                    attachListViewAdapter.update(choseFileIndex);
                    lazyAdapter.notifyDataSetChanged();

                    ImagefilePath = "";
                    ImageFileName = "";
                    showMessage("文件上传成功！");
                }else if(msg.what == 0){
                    if(null != mpDialog){
                        mpDialog.cancel();
                    }
                    showMessage("文件上传失败，请重试！");
                    add_save.setVisibility(View.VISIBLE);
                }
            }else if(msg.arg1 == StaticMember.CHOOSE_IMAGE_RESULT){
                // 拍照或选择图片上传处理
                if(msg.what == 1){
                    // 拍照处理
                }else if(msg.what == 2){
                    // 选择图片处理
                }else if(msg.what == 3){
                    // 选择PDF文件处理
                }
                uploadFileResult();
                return;
            }else if(msg.arg1 == StaticMember.SAVE_SHENBAO_DATA){
                if(null != mpDialog){
                    mpDialog.cancel();
                }
                add_save.setVisibility(View.VISIBLE);
                if(msg.what == 1){
                    // 保存成功
                    showMessage("数据保存成功！");

                    // 清空表单
                    clearFrom();

                }else if(msg.what == 0){
                    // 保存失败
                    showMessage("数据保存失败，请重试！");
                    showSnackbarMessage("数据保存失败！");
                }
            }
        }
    };

    /**
     * 设置Android6.0的权限申请
     */
    private void setPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //Android 6.0申请权限
                ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.CAMERA} ,1);
            }else{
                Log.i("","权限申请ok");
                takePhoto();
            }
        }else {
            takePhoto();
        }
    }

    /**
     * 权限申请结果处理
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1){
            if (grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                takePhoto();
            }else {
                return;
            }
        }
    }

    @Override
    public void camera() {
        if(null != myDialogFileChose){
            myDialogFileChose.dismiss();
        }
        setPermissions();
    }

    /**
     * 拍照
     */
    private void takePhoto(){
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }
        fileSuffix = "jpg";
        String filename = DateHelper.getToday("yyyyMMddHHmmssSSS")+"_"+StaticMember.USER.getUid()+"_"+ (int) (Math.random() * 1000);    //设置日期格式在android中，创建文件时，文件名中不能包含“：”冒号
        File currentImageFile  = new File(imageDir, filename + ".jpg");
        if (!currentImageFile .exists()){
            try {
                currentImageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ImagefilePath = currentImageFile.getAbsolutePath();//获取图片的绝对路径
        Log.e("", "#### ImagefilePath: "+ImagefilePath );
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 指定调用相机拍照后的照片存储的路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentImageFile));
        startActivityForResult(intent, StaticMember.PHOTO_IMAGE_TYPE);
    }

    /**
     * 从相册中选择
     */
    @Override
    public void gallery() {
        myDialogFileChose.dismiss();
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, StaticMember.CHOOSE_IMAGE_TYPE);
    }

    /**
     * 选择文件（pdf）
     */
    @Override
    public void choseFile() {
        myDialogFileChose.dismiss();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//无类型限制
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, StaticMember.CHOOSE_IMAGE_TYPE);
    }

    @Override
    public void cancel() {
        myDialogFileChose.cancel();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == NONE)
            return;

        Log.i("","## choseFileIndex===="+choseFileIndex);

        // 拍照
        if (requestCode == StaticMember.PHOTO_IMAGE_TYPE) {
            // 检查SDCard是否可用
            if(!AssistUtil.ExistSDCard()){
                Log.i("", "SD card 不可用！");
                Toast.makeText(activity, "SD卡不可用！", Toast.LENGTH_SHORT).show();
                return;
            }
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(ImagefilePath);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            BitmapFactory.Options opts=new BitmapFactory.Options();
            opts.inTempStorage = new byte[100 * 1024];
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            opts.inPurgeable = true;
            opts.inSampleSize = 4;
            opts.inInputShareable = true;
            Bitmap bitmap = BitmapFactory.decodeStream(fis,null, opts);
            try {
                if(null != fis){
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            MyLogger.Log().i("## 111压缩前图片大小："+ CaremaUtil.getBitmapSize(bitmap));
            Bitmap newPhoto = CaremaUtil.compressImage(bitmap,ImagefilePath);
            MyLogger.Log().i("## 222压缩后图片大小："+ CaremaUtil.getBitmapSize(newPhoto));
            //iv_image.setImageBitmap(newPhoto);
            Message msg = new Message();
            msg.arg1 = StaticMember.CHOOSE_IMAGE_RESULT;
            msg.what = 1;
            mHandler.sendMessage(msg);
            return;
        }
        if (data == null)
            return;

        // 选择文件
        if(requestCode == StaticMember.CHOOSE_IMAGE_TYPE){
            Uri uri = data.getData();
            if ("file".equalsIgnoreCase(uri.getScheme())){//使用第三方应用打开
                path = uri.getPath();
                Log.e(TAG,"##111--=== path="+path);
                ImagefilePath = uri.getPath();//获取图片的绝对路径
                ImageFileName = uri.getScheme();

                if(!checkFileTypes(ImagefilePath)){
                    showMessage("只能选择图片、PDF格式文件");
                    return;
                }

                /**
                 * 限制选择文件大小： 20971520 （20M）
                 * 限制选择文件大小：1024*1024*2=2097152
                 */
                if(FileUtil.getFileSizes(ImagefilePath) > 2097152){
                    showMessage("选择文件不能大于2M");
                    return;
                }

                Message msg = new Message();
                msg.arg1 = StaticMember.CHOOSE_IMAGE_RESULT;
                msg.what = 1;
                mHandler.sendMessage(msg);
                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                path = FileUtil.getPath(activity, uri);
                ImagefilePath = FileUtil.getPath(activity, uri);//获取图片的绝对路径
                Log.e(TAG,"##222--=== path="+path);
                // Toast.makeText(this,path,Toast.LENGTH_SHORT).show();

                if(!checkFileTypes(ImagefilePath)){
                    showMessage("只能选择图片、PDF格式文件");
                    return;
                }

                /**
                 * 限制选择文件大小： 20971520 （20M）
                 * 限制选择文件大小：1024*1024*2=2097152
                 */
                if(FileUtil.getFileSizes(ImagefilePath) > 2097152){
                    showMessage("选择文件不能大于2M");
                    return;
                }

                Message msg = new Message();
                msg.arg1 = StaticMember.CHOOSE_IMAGE_RESULT;
                msg.what = 1;
                mHandler.sendMessage(msg);
                return;
            } else {//4.4以下下系统调用方法
                path = FileUtil.getRealPathFromURI(uri);
                ImagefilePath = FileUtil.getRealPathFromURI(uri);//获取图片的绝对路径
                Log.e(TAG,"##333--=== path="+path);

                if(!checkFileTypes(ImagefilePath)){
                    showMessage("只能选择图片、PDF格式文件");
                    return;
                }

                /**
                 * 限制选择文件大小： 20971520 （20M）
                 * 限制选择文件大小：1024*1024*2=2097152
                 */
                if(FileUtil.getFileSizes(ImagefilePath) > 2097152){
                    showMessage("选择文件不能大于2M");
                    return;
                }

                Message msg = new Message();
                msg.arg1 = StaticMember.CHOOSE_IMAGE_RESULT;
                msg.what = 1;
                mHandler.sendMessage(msg);
                return;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean checkFileTypes(String path){
        boolean result = false;
        /*这里要调用这个getPath方法来能过uri获取路径不能直接使用uri.getPath。
            因为如果选的图片的话直接使用得到的path不是图片的本身路径*/
        File file = new File(path);
        /* 取得扩展名 */
        String end = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName	().length()).toLowerCase();
        if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg") || end.equals("bmp")
                || end.equals("pdf")) {
            result = true;
            this.fileSuffix = end;
        }else{
            result = false;
        }

        return result;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    //内部类实现viewpager的适配器
    private class ViewPagerAdapter extends PagerAdapter {

        //该方法 决定 并 返回 viewpager中组件的数量
        @Override
        public int getCount() {
            return viewContainter.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        //滑动切换的时候，消除当前组件
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewContainter.get(position));
        }

        //每次滑动的时候生成的组件
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewContainter.get(position));
            return viewContainter.get(position);
        }
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    private boolean rightAdX  = true; // 广告牌长度是否输入正确
    private boolean rightAdY  = true; // 广告牌宽度是否输入正确

    class EditChangedListener implements TextWatcher {
        // 输入文本之前的状态
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //Log.d("TAG", "beforeTextChanged--------------->");
        }
        // 输入文字中的状态，count是一次性输入字符数
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //Log.d("TAG", "onTextChanged--------------->");
        }
        // 输入文字后的状态
        @Override
        public void afterTextChanged(Editable s) {
            //Log.d("TAG", "afterTextChanged--------------->");
            String adx = edittext_ad_x.getText().toString();
            String ady = edittext_ad_y.getText().toString();
            if(null != adx && !"".equals(adx)){
                try {
                    double x = Double.parseDouble(adx);
                    rightAdX = true;
                } catch (Exception e) {
                    showMessage("请输入正确的长度！");
                    rightAdX = false;
                }
            }
            if(null != ady && !"".equals(ady)){
                try {
                    double y = Double.parseDouble(ady);
                    rightAdY = true;
                } catch (Exception e) {
                    showMessage("请输入正确的宽度！");
                    rightAdY = false;
                }
            }

            if(rightAdX && rightAdY){
                if(null != adx && !"".equals(adx) && null != ady && !"".equals(ady)){
                    double adX = Double.parseDouble(adx);
                    double adY = Double.parseDouble(ady);
                    double adS = adX * adY;
                    edittext_ad_s.setText(adS+"");
                }else{
                    edittext_ad_s.setText("");
                }
            }else{
                rightAdX = false;
                rightAdY = false;
                edittext_ad_s.setText("");
            }
        }
    };

    // 设置监听
    private void setListener() {

        // 省、市、区级联监听
        tv_city_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickerView();
            }
        });

        // 广告牌类型监听
        tv_icon_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickerViewAdType();
            }
        });

        // 规划分类
        tv_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickerViewPlan();
            }
        });

    }

    /**
     * 选择省、市、区级联
     */
    private void showPickerView() {
        if(options1Items.size() == 0){
            Toast.makeText(this.activity,"数据未加载。",Toast.LENGTH_SHORT).show();
            return;
        }
        OptionsPickerView pvOptions = new OptionsPickerView.Builder(activity, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String text = options1Items.get(options1).getPickerViewText() +"<br/>"+
                        options2Items.get(options1).get(options2) +"<br/>"+
                        options3Items.get(options1).get(options2).get(options3);

                province = options1Items.get(options1).getPickerViewText();
                city = options2Items.get(options1).get(options2);
                area = options3Items.get(options1).get(options2).get(options3);

                add_save.setVisibility(View.VISIBLE);

                tv_city_area.setText(Html.fromHtml(text));
            }
        }).setTitleText("")
                .setDividerColor(Color.GRAY)
                .setTextColorCenter(Color.GRAY)
                .setContentTextSize(14)
                .setOutSideCancelable(false)
                .build();
          /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

    /**
     * 选择广告牌级联
     */
    private void showPickerViewAdType() {
        if(optionsType1Items.size() == 0){
            Toast.makeText(this.activity,"数据未加载。",Toast.LENGTH_SHORT).show();
            return;
        }
        OptionsPickerView pvOptions = new OptionsPickerView.Builder(activity, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String text = optionsType1Items.get(options1).getPickerViewText() + "  \n"+
                        optionsType2Items.get(options1).get(options2) +"  \n"+
                        optionsType3Items.get(options1).get(options2).get(options3);

                icon_type = optionsType1Items.get(options1).getPickerViewText();
                icon_class = optionsType2Items.get(options1).get(options2);
                icon_cnname = optionsType3Items.get(options1).get(options2).get(options3);

                add_save.setVisibility(View.VISIBLE);

                tv_icon_type.setText(Html.fromHtml(text));
            }
        }).setTitleText("")
                .setDividerColor(Color.GRAY)
                .setTextColorCenter(Color.GRAY)
                .setContentTextSize(14)
                .setOutSideCancelable(false)
                .build();
          /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(optionsType1Items, optionsType2Items, optionsType3Items);//三级选择器
        pvOptions.show();
    }

    /**
     * 选择规划分类
     */
    private void showPickerViewPlan() {
        if(optionsPlan1Items.size() == 0){
            Toast.makeText(this.activity,"数据未加载。",Toast.LENGTH_SHORT).show();
            return;
        }
        OptionsPickerView pvOptions = new OptionsPickerView.Builder(activity, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String text = optionsPlan1Items.get(options1).getPickerViewText() + "  \n"+
                        optionsPlan2Items.get(options1).get(options2);

                cat_name = optionsPlan1Items.get(options1).getPickerViewText();
                plan_name = optionsPlan2Items.get(options1).get(options2);

                add_save.setVisibility(View.VISIBLE);
                tv_plan.setText(Html.fromHtml(text));
            }
        }).setTitleText("")
                .setDividerColor(Color.GRAY)
                .setTextColorCenter(Color.GRAY)
                .setContentTextSize(14)
                .setOutSideCancelable(false)
                .build();
          /*pvOptions.setPicker(optionsPlan1Items);//一级选择器 */
        pvOptions.setPicker(optionsPlan1Items, optionsPlan2Items);//二级选择器
        /*pvOptions.setPicker(optionsPlan1Items, optionsPlan2Items, optionsType3Items);//三级选择器 */
        pvOptions.show();
    }

    /**
     * 加载省、市、区级联数据
     */
    private void initJsonData() {   //解析数据
        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        //  获取json数据
        String JsonData = JsonFileReader.getJson(activity, "province_data.json");
        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体
        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;
        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市

                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    City_AreaList.add("");
                } else {
                    for (int d = 0; d < jsonBean.get(i).getCityList().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                        String AreaName = jsonBean.get(i).getCityList().get(c).getArea().get(d);
                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }
            /**
             * 添加城市数据
             */
            options2Items.add(CityList);
            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
        }
    }

    /**
     * 加载广告牌类型级联数据
     */
    private void initAdTypeJsonData() {   //解析数据
        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        //  获取json数据
        String JsonData = JsonFileReader.getJson(activity, "ad_types_data.json");
        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体
        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        optionsType1Items = jsonBean;
        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市

                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    City_AreaList.add("");
                } else {
                    for (int d = 0; d < jsonBean.get(i).getCityList().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                        String AreaName = jsonBean.get(i).getCityList().get(c).getArea().get(d);
                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }
            /**
             * 添加城市数据
             */
            optionsType2Items.add(CityList);
            /**
             * 添加地区数据
             */
            optionsType3Items.add(Province_AreaList);
        }
    }

    /**
     * 加载分类规划数据
     */
    private void initPlanJsonData() {   //解析数据
        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        //  获取json数据
        String JsonData = JsonFileReader.getJson(activity, "plan_data.json");
        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体
        /**
         * 添加数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        optionsPlan1Items = jsonBean;
        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该分类下的二级数据
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加二级分类
            }
            /**
             * 添加 二级数据
             */
            optionsPlan2Items.add(CityList);

        }
    }

    private ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }

}
