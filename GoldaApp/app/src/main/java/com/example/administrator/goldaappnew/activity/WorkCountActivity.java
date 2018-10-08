package com.example.administrator.goldaappnew.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.goldaappnew.R;
import com.example.administrator.goldaappnew.adapter.SearchRedAdapter;
import com.example.administrator.goldaappnew.bean.AdRedBean;
import com.example.administrator.goldaappnew.staticClass.StaticMember;
import com.example.administrator.goldaappnew.utils.CommonTools;
import com.example.administrator.goldaappnew.utils.HttpTools;
import com.example.administrator.goldaappnew.view.MyChatView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WorkCountActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.iv_refresh)
    ImageView ivRefresh;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.my_view)
    MyChatView myView;
    @BindView(R.id.recycleView)
    RecyclerView recycleView;
    private List<AdRedBean> list;
    private String starttime;
    private String endtime;
    private String today;
    private Calendar calendar;
    private SearchRedAdapter recycleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_count);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initData();
    }

    private void initData(){
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        today=format.format(new Date());
        tvStartTime.setText(today);
        tvEndTime.setText(today);

        try {
            starttime= CommonTools.dateToStamp(today+" 00:00:00");
            endtime=CommonTools.dateToStamp(today+" 23:59:59");

        } catch (ParseException e) {
            Log.i("errow", "initData: "+today+"..."+starttime+"...."+endtime);
            e.printStackTrace();
        }
        Log.i("ss", "initData: "+today+"..."+starttime+"...."+endtime);


        searchAndShow();
    }

    private void searchAndShow(){
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what==1){
                    Log.e("查到的广告数", list.size() + "个");
                    Snackbar sn = Snackbar.make(toolbar, "当前时段内完成了"+list.size()+"条广告巡查", Snackbar.LENGTH_INDEFINITE)
                            .setAction("知道了", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                    sn.show();
                    CommonTools.setSnackbarMessageTextColor(sn, getResources().getColor(R.color.orange));
                    LinearLayoutManager linearLayoutManager=new LinearLayoutManager(WorkCountActivity.this);
                    recycleView.setLayoutManager(linearLayoutManager);
                    recycleAdapter=new SearchRedAdapter(list,WorkCountActivity.this);
                    recycleView.setAdapter(recycleAdapter);
                    float num1 = 0.000f;//门头店招
                    float num2 = 0.000f;//户外广告
                    for (int i = 0,len=list.size(); i < len; i++) {
                        if (list.get(i).getIcon().equals("small_door"))
                            num1++;
                        else
                            num2++;
                    }
                    //更新统计图
                    if (list.size() == 0) {
                        clearData();
                    } else {
                        myView.setStr(new String[]{"门头店招", "户外广告"});
                        myView.setCount(list.size());
                        Log.e("***", num1 * 100 / list.size() + "---" + num2 * 100 / list.size());
                        myView.setStrPercent(new float[]{(float) (Math.round(num1 * 100 / list.size() * 100)) / 100, (float) (Math.round(num2 * 100 / list.size() * 100)) / 100});
                        myView.startDraw();
                    }
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("sss", "initData: "+starttime+"...."+endtime);
                list= HttpTools.getJson(StaticMember.URL+"mob_work_search.php","uid="+StaticMember.USER.getUid() + "&starttime=" + starttime + "&endtime=" + endtime,
                        StaticMember.ADLIST_RED);
                handler.sendEmptyMessage(1);
            }
        }).start();

    }
    private void clearData(){
        list.clear();
        recycleAdapter.notifyItemRangeChanged(0,list.size());
        recycleView.removeAllViews();
        myView.setStr(new String[]{"门头店招", "户外广告"});
        myView.setCount(list.size());
        myView.setStrPercent(new float[]{100.0f, 0.0f});
        myView.startDraw();

    }
    private void showDatePicDialog(final TextView textView){

        calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog.OnDateSetListener datePickerDialog= new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                clearData();
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
                if (textView==tvStartTime){
                    try {
                        starttime = CommonTools.dateToStamp(mTime+" 00:00:00");
                        Log.e("日历上选择的开始时间", mTime + "****" + starttime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Log.e("starttime","错误");
                    }
                }else if (textView==tvEndTime){
                    try {
                        endtime=CommonTools.dateToStamp(mTime+" 23:59:59");
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Log.e("endtime","错误");
                    }
                }

                Log.i("所选时间段", "onDateSet: "+starttime+"--"+endtime);

            }
        };

        new DatePickerDialog(this,datePickerDialog,year,month,day).show();
    }
    @OnClick({R.id.tv_start_time, R.id.iv_refresh, R.id.tv_end_time})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_start_time:
                showDatePicDialog(tvStartTime);
                break;
            case R.id.iv_refresh:
                searchAndShow();
                break;
            case R.id.tv_end_time:
                showDatePicDialog(tvEndTime);
                break;
        }
    }
}
