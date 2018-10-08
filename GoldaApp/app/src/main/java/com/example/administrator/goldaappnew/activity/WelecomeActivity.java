package com.example.administrator.goldaappnew.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.goldaappnew.BuildConfig;
import com.example.administrator.goldaappnew.R;
import com.example.administrator.goldaappnew.bean.UserBean;
import com.example.administrator.goldaappnew.staticClass.StaticMember;
import com.example.administrator.goldaappnew.utils.AutoUpdate;
import com.example.administrator.goldaappnew.utils.CommonTools;
import com.example.administrator.goldaappnew.utils.HttpTools;

import butterknife.BindView;

public class WelecomeActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static  final String TAG="WelecomeActivity";
    private SharedPreferences loginInfo;
    private String verName;
    private String newVersion;
    private AutoUpdate updateVersion;

    @BindView(R.id.activity_welecome)
    View view;
    @BindView(R.id.verName)
    TextView tv_verName;

    static final String[] PERMISSION = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE//读取设备信息
            ,Manifest.permission.READ_EXTERNAL_STORAGE //读取
            ,Manifest.permission.ACCESS_FINE_LOCATION //定位信息
            , Manifest.permission.CAMERA//相机权限
//            , Manifest.permission.CALL_PHONE//拨打电话权限
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            //设置让应用主题内容占据状态栏和导航栏
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //设置状态栏和导航栏颜色为透明
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_welecome);

        tv_verName = (TextView) findViewById(R.id.verName);

        getASVersionName();
        Log.i(TAG, "onCreate: "+verName);
        updateVersion =new AutoUpdate(this,false, verName);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setPermissions();
            }
        },1000);

    }

    /**
     * 设置Android6.0的权限申请
     */
    private void setPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(WelecomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //Android 6.0申请权限
                Log.i(TAG,"权限申请ok");
                ActivityCompat.requestPermissions(this,PERMISSION,1);
            }else{
                checkServiceVersionCode();
            }
        }else {
            checkServiceVersionCode();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode==1){
            if (grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                checkServiceVersionCode();
            }else {
                Intent i = new Intent(WelecomeActivity.this,LoginActivity.class);
                startActivity(i);
            }
        }
    }
    private void checkServiceVersionCode(){
        Log.i(TAG, "updateVerCode: new verName"+verName);
        final Handler handler=new Handler(){

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.i(TAG, "当前版本号："+verName+"_________最新版本号："+newVersion);
                if (null !=verName && !"".equals(verName) && newVersion!=null && !"".equals(newVersion) && !newVersion.equals(verName)){
                    updateVersion.checkUpdateInfo("检测到新版本，是否更新?");
                }else{
                    skipLogin();
                }

            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {

                newVersion = HttpTools.getNewVersion(StaticMember.URL + "mob_version2.php");
                Log.i(TAG, "run: 查看数据库最新版本"+newVersion);
                handler.sendEmptyMessage(1);
            }
        }).start();
    }


//    private void ToLoginActivity(){
//        Intent i = new Intent(WelecomeActivity.this,HomeActivity.class);
//        startActivity(i);
//        finish();
//        overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
//    }

    private void skipLogin()
    {

        loginInfo = getSharedPreferences("setting", MODE_PRIVATE);
        if(loginInfo.getBoolean("auto_login",false))
        {
            UserBean user = new UserBean();
            user.setUid(loginInfo.getString("uid",""));
            user.setRealname(loginInfo.getString("realname",""));
            user.setResideprovince(loginInfo.getString("resideprovince",""));
            user.setResidecity(loginInfo.getString("residecity",""));
            user.setResidedist(loginInfo.getString("residedist",""));
            user.setResidecommunity(loginInfo.getString("residecommunity",""));
            StaticMember.USER = user;
            Log.i("","#### user_id = "+StaticMember.USER.getUid());
            if(null == StaticMember.USER.getUid() || "0".equals(StaticMember.USER.getUid())){
                Toast.makeText(WelecomeActivity.this, "登录失败，当前账号未生成UID", Toast.LENGTH_SHORT).show();
                return;
            }

            CommonTools.setScaleAnim(view);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Intent i = new Intent(WelecomeActivity.this,HomeActivity.class);
                    Intent i = new Intent(WelecomeActivity.this,MainFragmentActivity.class);
                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
                }
            }, 900);
        }else {
            //CommonTools.setScaleAnim(view);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(WelecomeActivity.this,LoginActivity.class);
                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
                }
            }, 900);
        }
    }



    // 获取当前应用的版本号
    public void getASVersionName(){
        try {
            int versionCode = BuildConfig.VERSION_CODE;
            verName = BuildConfig.VERSION_NAME;
            Log.i(TAG,"verName===="+verName);
            if(null != tv_verName){
                tv_verName.setText(verName);
            }
        }catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG,"获取当前版本号异常！！！");
        }
    }


    /**
     * @param context
     * 查看网络连接状态
     * @return
     */
    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isConnected()) {
                // 判断当前网络是否已经连接
                if (mNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
//            if (mNetworkInfo != null) {

//                return mNetworkInfo.isConnected();
//            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.finish();

   }

}
