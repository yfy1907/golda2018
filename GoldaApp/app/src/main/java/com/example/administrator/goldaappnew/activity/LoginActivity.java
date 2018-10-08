package com.example.administrator.goldaappnew.activity;
import com.example.administrator.goldaappnew.R;
import com.example.administrator.goldaappnew.bean.UserBean;
import com.example.administrator.goldaappnew.staticClass.StaticMember;
import com.example.administrator.goldaappnew.utils.HttpTools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 *
 */
public class LoginActivity extends Activity {



    @BindView(R.id.et_username)
    EditText et_username;
    @BindView(R.id.et_pw)
    EditText et_psw;
    @BindView(R.id.iv_pw_see)
    ImageView psw_see;
    @BindView(R.id.iv_user_clear)
    ImageView user_clear;
    @BindView(R.id.iv_pw_clear)
    ImageView psw_clear;
    @BindView(R.id.bt_login)
    Button bt_login;
    @BindView(R.id.iv_skip)
    ImageView iv_skip;
    private String username="";
    private String psw="";
    private List<UserBean> userList = new ArrayList<UserBean>();
    private SharedPreferences loginInfo;
    private boolean isAutoLogin = true;
    private boolean isPwVisiable = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        init();
    }


    private void init(){
        loginInfo = getSharedPreferences("setting", Context.MODE_PRIVATE);
        String user_name=loginInfo.getString("name","");
        String user_password=loginInfo.getString("password","");
        et_username.setText(user_name);
        et_psw.setText(user_password);
    }
    @OnTextChanged(R.id.et_username)
    public void toUsername(CharSequence editText){
        if ((et_username.getText().equals(""))||(et_username.getText().length()==0)||(et_username==null)){
                    user_clear.setVisibility(View.GONE);
                }else {
                    user_clear.setVisibility(View.VISIBLE);
                }
                username=et_username.getText().toString();
    }
    @OnTextChanged(R.id.et_pw)
    public void toPsd(CharSequence editText){


        if (et_psw.getText().equals("")||(et_psw.getText().length()==0)||(et_psw==null)){
                    psw_clear.setVisibility(View.GONE);
                    psw_see.setVisibility(View.GONE);
                }else {
                    psw_see.setVisibility(View.VISIBLE);
                    psw_clear.setVisibility(View.VISIBLE);
                }
                psw=et_psw.getText().toString();
    }


    @OnClick(R.id.bt_login)
    public void toLogin(Button button){

       isToLogin();
    }

    /**
     * 是否记住密码
     */
    @OnClick(R.id.iv_skip)
    public void skipLogin(){
        if (isAutoLogin)
            iv_skip.setImageResource(R.drawable.icon_unskip_login);
        else
            iv_skip.setImageResource(R.drawable.icon_skip_login);

        isAutoLogin = !isAutoLogin;
    }
    @OnClick(R.id.iv_user_clear)
    public void delUsername(){
        et_username.setText("");
        user_clear.setVisibility(View.GONE);
    }

    @OnClick(R.id.iv_pw_clear)
    public void delPwd(){
        et_psw.setText("");
        psw_clear.setVisibility(View.GONE);
        psw_see.setVisibility(View.GONE);

    }
    @OnClick(R.id.iv_pw_see)
    public void seePsw(){
        if (isPwVisiable) {
            et_psw.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            psw_see.setImageResource(R.drawable.icon_pw);
        } else {
            et_psw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            psw_see.setImageResource(R.drawable.icon_pw_un);
        }
        et_psw.setSelection(et_psw.getText().length());
        isPwVisiable = !isPwVisiable;
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
            if (mNetworkInfo != null) {

                return mNetworkInfo.isConnected();
            }
        }
        return false;
    }


    /**
     * 登录按钮监听
     */
    private void isToLogin(){

        if (HttpTools.isNetworkConnected(LoginActivity.this)==false) {
            Toast.makeText(LoginActivity.this, "无网络连接", Toast.LENGTH_SHORT).show();
            return;
        }
        if ((username.trim()).equals("")){
            Toast.makeText(LoginActivity.this, "用户名不能为空！",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if ((psw.trim()).equals("")){
            Toast.makeText(LoginActivity.this, "密码不能为空！",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (psw.contains("\040")) {
            Toast.makeText(LoginActivity.this, "密码不能包含空格！",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (psw.contains("\n")) {
            Toast.makeText(LoginActivity.this, "密码不能包含回车字符！",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what==1){
                    if (userList==null||userList.size()<1){
                        Toast.makeText(LoginActivity.this,
                                "登录失败，请检查用户名或密码是否正确",
                                Toast.LENGTH_SHORT).show();
                    }else {
                        if (isAutoLogin==true) {

                            SharedPreferences.Editor editor = loginInfo.edit();
                            editor.putBoolean("auto_login", isAutoLogin);
                            editor.putString("name", et_username.getText().toString());
                            editor.putString("password", et_psw.getText().toString());
                            editor.putString("uid", userList.get(0).getUid());
                            editor.putString("realname", userList.get(0).getRealname());
                            editor.putString("resideprovince", userList.get(0).getResideprovince());
                            editor.putString("residecity", userList.get(0).getResidecity());
                            editor.putString("residedist", userList.get(0).getResidedist());
                            editor.putString("residecommunity", userList.get(0).getResidecommunity());
                            editor.commit();
                        }else {
                            SharedPreferences.Editor editor = loginInfo.edit();
                            editor.putBoolean("auto_login", isAutoLogin);
                            editor.putString("name","" );
                            editor.putString("password","");
                            editor.putString("uid", "");
                            editor.putString("realname", "");
                            editor.putString("resideprovince","");
                            editor.putString("residecity", "");
                            editor.putString("residedist", "");
                            editor.putString("residecommunity","");
                            editor.commit();
                        }

                        if(null == userList.get(0)){
                            Toast.makeText(LoginActivity.this, "登录失败，当前账号信息异常", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        StaticMember.USER = userList.get(0);
                        if(null == StaticMember.USER.getUid() || "0".equals(StaticMember.USER.getUid())){
                            Toast.makeText(LoginActivity.this, "登录失败，当前账号未生成UID", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        SharedPreferences.Editor editor = loginInfo.edit();
                        editor.putBoolean("auto_login", isAutoLogin);
                        editor.putString("name", et_username.getText().toString());
                        editor.putString("password", et_psw.getText().toString());
                        editor.putString("uid", userList.get(0).getUid());
                        editor.putString("realname", userList.get(0).getRealname());
                        editor.putString("resideprovince", userList.get(0).getResideprovince());
                        editor.putString("residecity", userList.get(0).getResidecity());
                        editor.putString("residedist", userList.get(0).getResidedist());
                        editor.putString("residecommunity", userList.get(0).getResidecommunity());
                        editor.commit();

                        // Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        Intent intent = new Intent(LoginActivity.this, MainFragmentActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
                    }
                }
            }
        };


        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    userList = HttpTools.getJson(StaticMember.URL + "mob_login.php", "username=" + username + "&password=" + psw, StaticMember.USERLIST);

                }catch (Exception e){
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(1);
            }
        }).start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.bind(this).unbind();
    }
}
