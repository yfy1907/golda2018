package com.example.administrator.goldaappnew.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.goldaappnew.R;
import com.example.administrator.goldaappnew.staticClass.StaticMember;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @name GoldaApp
 * @class name：com.example.administrator.goldaapp.activity
 * @class describe
 * @chang time
 * @class describe
 */
public class SettingActivity extends AppCompatActivity {


    @BindView(R.id.tv_sure_cancel)
    TextView tvSureCancel;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.work_count)
    LinearLayout workCount;
    @BindView(R.id.tv_local_space)
    TextView tvLocalSpace;
    @BindView(R.id.local_space)
    LinearLayout localSpace;
    @BindView(R.id.tv_refresh_length)
    TextView tvRefreshLength;
    @BindView(R.id.refresh_length)
    LinearLayout refreshLength;
    @BindView(R.id.soft_info)
    LinearLayout softInfo;
    @BindView(R.id.open_web)
    LinearLayout openweb;
    @BindView(R.id.login_out)
    LinearLayout loginOut;

    private int tv_localSpace,tv_refreshLength;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initData();
    }

    private void initData() {
        tvUserName.setText(StaticMember.USER.getRealname());
        tv_localSpace=getSharedPreferences("setting",MODE_PRIVATE).getInt("local_space",1000);
        if (tv_localSpace==1000)
            tvLocalSpace.setText("默认");
        else if (tv_localSpace==2000)
            tvLocalSpace.setText("2秒");
        else if (tv_localSpace==5000)
            tvLocalSpace.setText("5秒");
        else if (tv_localSpace==10000)
            tvLocalSpace.setText("10秒");
        else if (tv_localSpace==30000)
            tvLocalSpace.setText("30秒");

        tv_refreshLength=getSharedPreferences("setting",MODE_PRIVATE).getInt("refresh_length",150);

        if (tv_refreshLength==10)
            tvRefreshLength.setText("10米");
        else if (tv_refreshLength==20)
            tvRefreshLength.setText("20米");
        else if (tv_refreshLength==50)
            tvRefreshLength.setText("50米");
        else if (tv_refreshLength==150)
            tvRefreshLength.setText("默认");
        else if (tv_refreshLength==300)
            tvRefreshLength.setText("300米");

    }

    @OnClick({ R.id.work_count, R.id.local_space, R.id.refresh_length,  R.id.login_out, R.id.open_web,R.id.soft_info})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.work_count:
                Intent intent=new Intent(SettingActivity.this, WorkCountActivity.class);
                startActivity(intent);
                break;
            case R.id.local_space:
                AlertDialog.Builder dialog=new AlertDialog.Builder(SettingActivity.this);
                dialog.setItems(StaticMember.local_space, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = getSharedPreferences("setting", MODE_PRIVATE)
                                .edit();
                        switch (which) {
                            case 0:
                                editor.putInt("local_space", 1 * 1000);
                                tvLocalSpace.setText("默认");
                                break;
                            case 1:
                                editor.putInt("local_space", 2 * 1000);
                                tvLocalSpace.setText("2秒");
                                break;
                            case 2:
                                editor.putInt("local_space", 5 * 1000);
                                tvLocalSpace.setText("5秒");
                                break;
                            case 3:
                                editor.putInt("local_space", 10 * 1000);
                                tvLocalSpace.setText("10秒");
                                break;
                            case 4:
                                editor.putInt("local_space", 30 * 1000);
                                tvLocalSpace.setText("30秒");
                                break;
                        }
                        editor.apply();
                        Log.e("设置的间隔", getSharedPreferences("setting", MODE_PRIVATE).getInt("local_space", 5555) + "");
                    }
                });
                dialog.show();
                break;
            case R.id.refresh_length:
                AlertDialog.Builder dialog2=new AlertDialog.Builder(SettingActivity.this);
                dialog2.setItems(StaticMember.refresh_length, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = getSharedPreferences("setting", MODE_PRIVATE)
                                .edit();
                        switch (which) {
                            case 0:
                                editor.putInt("refresh_length", 10);
                                tvRefreshLength.setText("10米");
                                break;
                            case 1:
                                editor.putInt("refresh_length", 20);
                                tvRefreshLength.setText("20米");
                                break;
                            case 2:
                                editor.putInt("refresh_length", 50);
                                tvRefreshLength.setText("50米");
                                break;
                            case 3:
                                editor.putInt("refresh_length", 150);
                                tvRefreshLength.setText("默认");
                                break;
                            case 4:
                                editor.putInt("refresh_length", 300);
                                tvRefreshLength.setText("300米");
                                break;
                        }
                        editor.apply();
                        Log.e("设置的刷新距离", getSharedPreferences("setting", MODE_PRIVATE).getInt("refresh_length", 666) + "");
                    }
                });
                dialog2.show();
                break;
            case R.id.soft_info:
                Intent iSetting=new Intent(SettingActivity.this,SoftInfoActivity.class);
                startActivity(iSetting);
                break;
            case R.id.open_web:
                Intent iWeb=new Intent(SettingActivity.this,OpenWebActivity.class);
                startActivity(iWeb);
                break;
            case R.id.login_out:
                Intent iLogin = new Intent(this, LoginActivity.class);
                startActivity(iLogin);
                Intent Lntent = new Intent();
                Lntent.setAction("close_app"); // 说明动作
                sendBroadcast(Lntent);// 该函数用于发送广播
                finish();
                overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
                break;
        }
    }
}
