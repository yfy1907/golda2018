package com.example.administrator.goldaappnew.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.example.administrator.goldaappnew.R;
import com.example.administrator.goldaappnew.adapter.LazyAdapter;
import com.example.administrator.goldaappnew.bean.BoardBean;
import com.example.administrator.goldaappnew.fragment.FragmentShenbao;
import com.example.administrator.goldaappnew.utils.AppManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 查看广告牌申报详细
 */
public class DisplayBoardShenbaoDetail  extends AppCompatActivity {

    TextView tv_city_area;  // 省份、城市、地区
    TextView edittext_adress;   // 设置地点
    TextView edittext_area_line;    // 路段
    TextView edittext_company; //申请公司名称
    TextView edittext_company_address;          // 公司地址
    TextView edittext_person; // 法定代表人
    TextView edittext_contact; // 联系电话号码
    TextView edittext_process_contact; // 联系人
    TextView edittext_process_tel; // 联系人电话号码
    TextView edittext_email; // 联系邮箱
    TextView tv_icon_type; // 类型
    TextView edittext_material; // 广告牌材质
    TextView edittext_material_time; // 广告牌材质有效期
    TextView edittext_wt; // 外凸(米)
    TextView edittext_model; // 数量(个)
    TextView edittext_facenum; // 展示面数(面)
    TextView edittext_ad_x; // 长度(米)
    TextView edittext_ad_y; // 宽度(米)
    TextView edittext_ad_s; // 面积(平方米)
    TextView edittext_li_height; // 离地高度(米)

    TextView edittext_plan; // 计划分类
    TextView edittext_json_id; // 分类ID

    TextView text_op_tips; // 附件上传提示


    private ViewPager viewPager = null;
    private List<View> viewContainter = new ArrayList<View>();   //存放容器
    private ViewPagerAdapter viewPagerAdapter = null;   //声明适配器
    private TabHost mTabHost = null;
    private TabWidget mTabWidget = null;

    private ListView addAttachListView;
    private ArrayList<Map<String, String>> listAttachData;
    private LazyAdapter lazyAdapter;

    private String[] attachArray = new String[]{};

    boolean display_19_20_attach = false; // 是否显示：场核查意见书b_attach_19和备案通知书b_attach_20

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        setContentView(R.layout.activity_shenbao_display_detail);

        initMyTabHost();
    }

    //初始化TabHost
    public void initMyTabHost(){

        //绑定id
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
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

        viewPager = (ViewPager)findViewById(R.id.viewpager);
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
    }

    //初始化viewPager
    public void initViewPagerContainter(){
        //建立两个view的样式，并找到他们
        View view_1 = LayoutInflater.from(this).inflate(R.layout.fragment_shenbao_viewpage1_detail,null);
        View view_2 = LayoutInflater.from(this).inflate(R.layout.fragment_shenbao_viewpage2,null);

//        ButterKnife.bind(R.layout.fragment_shenbao_viewpage1_detail,view_1);
//        ButterKnife.bind(R.layout.fragment_shenbao_viewpage2,view_2);

        //加入ViewPage的容器
        viewContainter.add(view_1);
        viewContainter.add(view_2);

        // view1 初始化
        initViewPage1UI(view_1);
        // view2 初始化
        initViewPage2UI(view_2);

        initData();
    }


    private void initData(){
        Intent intent = getIntent();
        if(null != intent){
            BoardBean boardBean = (BoardBean) intent.getSerializableExtra("BoardBean");
            if(null != boardBean){
                Log.e("","##getDe_id =="+boardBean.getDe_id());

                String b_attach_20 = boardBean.getB_attach_20();
                if(null != b_attach_20 && !"".equals(b_attach_20) && b_attach_20.length() >0){
                    display_19_20_attach = true;
                }else{
                    display_19_20_attach = false;
                }
                setFormValus(boardBean);
            }
        }
    }

    private void initViewPage1UI(View view){
        tv_city_area = (TextView) view.findViewById(R.id.tv_city_area);
        tv_icon_type = (TextView) view.findViewById(R.id.tv_icon_type);

        edittext_adress = (TextView) view.findViewById(R.id.edittext_adress);
        edittext_area_line = (TextView) view.findViewById(R.id.edittext_area_line);
        edittext_company = (TextView) view.findViewById(R.id.edittext_company);
        edittext_company_address = (TextView) view.findViewById(R.id.edittext_company_address);
        edittext_person = (TextView) view.findViewById(R.id.edittext_person);
        edittext_contact = (TextView) view.findViewById(R.id.edittext_contact);
        edittext_process_contact = (TextView) view.findViewById(R.id.edittext_process_contact);
        edittext_process_tel = (TextView) view.findViewById(R.id.edittext_process_tel);
        edittext_email = (TextView) view.findViewById(R.id.edittext_email);

        edittext_material = (TextView) view.findViewById(R.id.edittext_material);
        edittext_material_time = (TextView) view.findViewById(R.id.edittext_material_time);
        edittext_wt = (TextView) view.findViewById(R.id.edittext_wt);
        edittext_model = (TextView) view.findViewById(R.id.edittext_model);
        edittext_facenum = (TextView) view.findViewById(R.id.edittext_facenum);
        edittext_ad_x = (TextView) view.findViewById(R.id.edittext_ad_x);
        edittext_ad_y = (TextView) view.findViewById(R.id.edittext_ad_y);
        edittext_ad_s = (TextView) view.findViewById(R.id.edittext_ad_s);

        edittext_ad_s = (TextView) view.findViewById(R.id.edittext_ad_s);
        edittext_li_height = (TextView) view.findViewById(R.id.edittext_li_height);
        edittext_plan = (TextView) view.findViewById(R.id.edittext_plan);
        edittext_json_id = (TextView) view.findViewById(R.id.edittext_json_id);
    }

    private void initViewPage2UI(View view){
        text_op_tips = (TextView) view.findViewById(R.id.text_op_tips);
        if(null != text_op_tips){
            text_op_tips.setVisibility(View.GONE);
        }
        addAttachListView = (ListView) view.findViewById(R.id.addAttachListView);
        listAttachData = new ArrayList<>();
    }

    /**
     * 设置填充表单内容
     * @param boardBean
     */
    private void setFormValus(BoardBean boardBean){

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
        edittext_plan.setText(boardBean.getCat_name()+" "+boardBean.getPlan_name());
        edittext_json_id.setText(boardBean.getJson_id());


        if(display_19_20_attach){
            attachArray = new String[]{"设置申请书","公司营业执照","个人身份证明","效果图","实景图","规格平面图","产权证书或\n房屋租赁协议"
                    ,"载体安全证明","相关书面协议","场地租用合同","结构设计图","施工图","施工说明书","建安资质证书","施工保证书","规划拍卖意见",
                    "授权人身份证","授权委托书","现场核查意见书","备案通知书","规划相关截图"};
        }else{
            attachArray = new String[]{"设置申请书","公司营业执照","个人身份证明","效果图","实景图","规格平面图","产权证书或\n房屋租赁协议"
                    ,"载体安全证明","相关书面协议","场地租用合同","结构设计图","施工图","施工说明书","建安资质证书","施工保证书","规划拍卖意见",
                    "授权人身份证","授权委托书","规划相关截图"};
        }

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
            map.put("file_key","b_attach_"+(i+1));
            map.put("file_id",""+i);
            listAttachData.add(map);
        }
        lazyAdapter = new LazyAdapter(this, null, null,listAttachData);
        addAttachListView.setAdapter(lazyAdapter);

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
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
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

}
