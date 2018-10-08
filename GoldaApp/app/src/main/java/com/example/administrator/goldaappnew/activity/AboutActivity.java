package com.example.administrator.goldaappnew.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.administrator.goldaappnew.R;
import com.example.administrator.goldaappnew.adapter.SearchGreenAdapter;
import com.example.administrator.goldaappnew.adapter.SearchRedAdapter;
import com.example.administrator.goldaappnew.bean.AdGreenBean;
import com.example.administrator.goldaappnew.bean.AdRedBean;
import com.example.administrator.goldaappnew.fragment.FragmentXuncha;
import com.example.administrator.goldaappnew.staticClass.StaticMember;
import com.example.administrator.goldaappnew.utils.CommonTools;
import com.example.administrator.goldaappnew.utils.MultiTool;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Drink.Xu
 *
 * @time 2018/1/11
 * Description : 周围广告牌
 */
public class AboutActivity extends AppCompatActivity{
    private List<AdRedBean> redList;
    private List<AdGreenBean> greenList;

    private List<AdRedBean> redAroundList;
    private List<AdGreenBean> greenAroundList;

    // 巡查列表
    @BindView(R.id.tv_Patrol)
    TextView tv_patrol;

    // 审批列表
    @BindView(R.id.tv_Trial)
    TextView tv_trial;

    @BindView(R.id.recycle_red)
    RecyclerView mRedRecycleView;

    @BindView(R.id.recycle_green)
    RecyclerView mGreenRecycleView;

    // 红标适配器
    private SearchRedAdapter redRecycleAdapter;
    // 绿标适配器
    private SearchGreenAdapter greenRecycleAdapter;

    LinearLayoutManager layoutManager ;

    // 周围广告牌 筛选条件
    @BindView(R.id.radio_group)
    RadioGroup radioGroup;

    // 200米
    @BindView(R.id.radio_200m)
    RadioButton radio_200m;
    // 500米
    @BindView(R.id.radio_500m)
    RadioButton radio_500m;
    // 1000米
    @BindView(R.id.radio_1000m)
    RadioButton radio_1000m;

    // 当前位置所在经纬度
    private float accuracy = 0;//默认的定位精度

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_around);
        ButterKnife.bind(this);
        initView();
        initData();

        initRadioGroundEvent();
    }

    private void initData() {

        /** 20180726 默认选中200范围内的巡查或审批数据 */
        if(null != radio_200m){
            radio_200m.setChecked(true);
        }
        if(null != radio_500m){
            radio_500m.setChecked(false);
        }
        if(null != radio_1000m){
            radio_1000m.setChecked(false);
        }



        redList = new ArrayList<AdRedBean>();
        greenList = new ArrayList<AdGreenBean>();
        redList = (List<AdRedBean>) getIntent().getSerializableExtra("AdRedList");
        greenList = (List<AdGreenBean>) getIntent().getSerializableExtra("AdGreenList");
//        for (int i = 0; i < greenList.size(); i++) {
//            Log.e("TAG", "initData: "+greenList.get(i) );
//        }

//        // 加载全部绿标
//        getGreenView(greenList);
//        // 加载全部红标
//        getRedView(redList);

        accuracy = getIntent().getFloatExtra("accuracy", 10);//定位精度


        // 初始化标点数据，默认200米
        initIconData();

    }
    private void initView() {

    }

    @OnClick({R.id.tv_Patrol,R.id.tv_Trial})
    public void showAbound(View view){
        switch (view.getId()){
            case R.id.tv_Patrol:
                mGreenRecycleView.setVisibility(View.GONE);
                mRedRecycleView.setVisibility(View.VISIBLE);
                break;

            case R.id.tv_Trial:
                mRedRecycleView.setVisibility(View.GONE);
                mGreenRecycleView.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    /**
     * 单选按钮选择监听事件
     */
    private void initRadioGroundEvent(){
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                initIconData();
            }
        });
    }

    private void initIconData(){
        if (greenList != null) {
            greenAroundList = searchGreenDataList(whoChecked());
            getGreenView(greenAroundList);
        }
        if (redList != null) {
            redAroundList = searchRedDataList(whoChecked());
            getRedView(redAroundList);
        }
    }

    // 根据范围筛选广告牌
    private List<AdGreenBean> searchGreenDataList(int length){
        List<AdGreenBean> dataList = new ArrayList<AdGreenBean>();
        double lat =  FragmentXuncha.myLocationLL.latitude;
        double lon = FragmentXuncha.myLocationLL.longitude;
        if(null != greenList && greenList.size() > 0){
            for (int i = 0; i < greenList.size(); i++) {
                AdGreenBean greenBean = greenList.get(i);
                double x = CommonTools.gisXYParseDouble(greenBean.getGis_x());
                double y = CommonTools.gisXYParseDouble(greenBean.getGis_y());
                int distance = (int) MultiTool.toDistance(lon,lat, x, y);

//                Log.i("","## green distance="+distance);
                if (distance <= StaticMember.LENGTH + accuracy){
                    // 在搜索距离范围内
                    dataList.add(greenBean);
                }
            }
        }
//        MyLogger.Log().i("##绿标："+dataList.size() +"; 长度："+length+"; 总数："+greenList.size()+"; ==="+StaticMember.LENGTH + accuracy);
        return dataList;
    }

    // 根据范围筛选广告牌
    private List<AdRedBean> searchRedDataList(int length){
        List<AdRedBean> dataList = new ArrayList<AdRedBean>();
        double lat =  FragmentXuncha.myLocationLL.latitude;
        double lon = FragmentXuncha.myLocationLL.longitude;
        if(null != redList && redList.size() > 0){
            for (int i = 0; i < redList.size(); i++) {
                AdRedBean redBean = redList.get(i);
                double x = CommonTools.gisXYParseDouble(redBean.getGis_x());
                double y = CommonTools.gisXYParseDouble(redBean.getGis_y());
                int distance = (int) MultiTool.toDistance(lon,lat, x, y);
//                Log.i("","## red distance="+distance);
                if (distance <= StaticMember.LENGTH + accuracy){
                    // 在搜索距离范围内
                    dataList.add(redBean);
                }
            }
        }
//        MyLogger.Log().i("##红标："+dataList.size() +"; 长度："+length+" ; 总数："+redList.size()+"; ==="+StaticMember.LENGTH + accuracy);
        return dataList;
    }

    /**
     * 查看当前radiobutton选中状态
     * @return
     */
    private int whoChecked() {
        int length = 0;
        if (radio_200m.isChecked()) {
            length = 200;
        }
        if (radio_500m.isChecked()) {
            length = 500;
        }
        if (radio_1000m.isChecked()) {
            length = 1000;
        }
        return length;
    }

    private void getGreenView(List<AdGreenBean> pGreenList){
        layoutManager = new LinearLayoutManager(AboutActivity.this);
        mGreenRecycleView.setLayoutManager(layoutManager);
        greenRecycleAdapter = new SearchGreenAdapter(pGreenList,this);
        mGreenRecycleView.setAdapter(greenRecycleAdapter);
    }

    private void getRedView(List<AdRedBean> pRedList){
        layoutManager = new LinearLayoutManager(AboutActivity.this);
        mRedRecycleView.setLayoutManager(layoutManager);
        redRecycleAdapter = new SearchRedAdapter(pRedList,this);
        mRedRecycleView.setAdapter(redRecycleAdapter);
    }

}
