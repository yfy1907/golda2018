package com.example.administrator.goldaappnew.activity;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.goldaappnew.R;
import com.example.administrator.goldaappnew.fragment.FragmentBoard;
import com.example.administrator.goldaappnew.fragment.FragmentShenbao;
import com.example.administrator.goldaappnew.fragment.FragmentMe;
import com.example.administrator.goldaappnew.fragment.FragmentXuncha;
import com.example.administrator.goldaappnew.jpush.ExampleUtil;
import com.example.administrator.goldaappnew.jpush.JpushMainActivity;
import com.example.administrator.goldaappnew.jpush.LocalBroadcastManager;
import com.example.administrator.goldaappnew.staticClass.StaticMember;
import com.example.administrator.goldaappnew.utils.AppManager;
import com.example.administrator.goldaappnew.utils.StringUtil;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class MainFragmentActivity extends AppCompatActivity implements View.OnClickListener {

    private FragmentTransaction fragmentTransaction;

    // 定位四个Fragment
    private Fragment shenbaoFragment = new FragmentShenbao();
    private Fragment xunchaFragment = new FragmentXuncha();
    private Fragment boardFragment = new FragmentBoard();
    private Fragment meFragment = new FragmentMe();

    // tab中的四个帧布局
    private FrameLayout frame_layout_shenbao, frame_layout_xuncha, frame_layout_board, frame_layout_me;

    // tab中的四个帧布局中的四个图片组件
    private ImageView imageview_shenbao , imageview_xuncha, imageview_board, imageview_me;

    // tab中的四个帧布局中的四个图片对应文字
    private TextView textview_shenbao, textview_xuncha, textview_board,textview_me;

    /**
     * 是否退出程序 默认false
     **/
    private static boolean isExit = false;
    public Handler home_exit_handler = null;


    protected LocalBroadcastManager localBroadcastManager ;
    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.golda.jpush.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_main);

        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        // 推送
        registerMessageReceiver();
        initPush();

        AppManager.getAppManager().finishActivity(MainFragmentActivity.class);
        Log.i("", "#### onCreate。。。。————————————————————>>>>>>>>>");
        AppManager.getAppManager().addActivity(this);
        // 初始化组件
        initView();

        // 初始化按钮单击事件
        initClickEvent();

        // 初始化所有fragment
        initFragment();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#000000"));
            getWindow().setNavigationBarColor(Color.BLACK);
        }

        this.home_exit_handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                isExit = false;
            }
        };
    }

    // 初始化 JPush。如果已经初始化，但没有登录成功，则执行重新登录。
    private void initPush(){

        // 设置别名
        setTagAndAlias();
    }

    /**
     * 设置标签与别名
     */
    private void setTagAndAlias() {
        /**
         *这里设置了别名，在这里获取的用户登录的信息
         *并且此时已经获取了用户的userId,然后就可以用用户的userId来设置别名了
         **/
        //false状态为未设置标签与别名成功
        //if (UserUtils.getTagAlias(getHoldingActivity()) == false) {
        Set<String> tags = new HashSet<String>();
        //这里可以设置你要推送的人，一般是用户uid 不为空在设置进去 可同时添加多个

        String user_id = StaticMember.USER.getUid();
        user_id = "1010117";
        Log.e("TAG", "#############当前登录UID="+user_id);


        String alias = StaticMember.USER.getUid();
        if (TextUtils.isEmpty(alias)) {
            Toast.makeText(this,"设置广播别名不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!ExampleUtil.isValidTagAndAlias(alias)) {
            Toast.makeText(this,"设置别名错误！", Toast.LENGTH_SHORT).show();
            return;
        }

        // 调用 Handler 来异步设置别名
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
    }
    private static final int MSG_SET_ALIAS = 1001;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d(TAG, "####### Set alias in handler.");
                    // 调用 JPush 接口来设置别名。
                    JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, mAliasCallback);
                    break;
                default:
                    Log.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };

    /**
     * /**
     * TagAliasCallback类是JPush开发包jar中的类，用于
     * 设置别名和标签的回调接口，成功与否都会回调该方法
     * 同时给定回调的代码。如果code=0,说明别名设置成功。
     * /**
     * 6001   无效的设置，tag/alias 不应参数都为 null
     * 6002   设置超时    建议重试
     * 6003   alias 字符串不合法    有效的别名、标签组成：字母（区分大小写）、数字、下划线、汉字。
     * 6004   alias超长。最多 40个字节    中文 UTF-8 是 3 个字节
     * 6005   某一个 tag 字符串不合法  有效的别名、标签组成：字母（区分大小写）、数字、下划线、汉字。
     * 6006   某一个 tag 超长。一个 tag 最多 40个字节  中文 UTF-8 是 3 个字节
     * 6007   tags 数量超出限制。最多 100个 这是一台设备的限制。一个应用全局的标签数量无限制。
     * 6008   tag/alias 超出总长度限制。总长度最多 1K 字节
     * 6011   10s内设置tag或alias大于3次 短时间内操作过于频繁
     **/
    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    //这里可以往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    //UserUtils.saveTagAlias(getHoldingActivity(), true);
                    logs = "###################Set tag and alias success极光推送别名设置成功";
                    Log.e("TAG", logs);
                    break;
                case 6002:
                    //极低的可能设置失败 我设置过几百回 出现3次失败 不放心的话可以失败后继续调用上面那个方面 重连3次即可 记得return 不要进入死循环了...
                    logs = "########################Failed to set alias and tags due to timeout. Try again after 60s.极光推送别名设置失败，60秒后重试";
                    Log.e("TAG", logs);
                    break;
                default:
                    logs = "##############极光推送设置失败，Failed with errorCode = " + code;
                    Log.e("TAG", logs);
                    break;
            }
        }
    };

    /**
     * 注册推送消息
     */
    public void registerMessageReceiver() {

        mMessageReceiver = new MessageReceiver();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("MainFragment");

        intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        intentFilter.addAction(MESSAGE_RECEIVED_ACTION);
        localBroadcastManager.registerReceiver(mMessageReceiver, intentFilter);
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String change = intent.getStringExtra("change");
                Log.w("","#### change==="+change);
                if ("shenbao".equals(change)) {
                    // 这地方只能在主线程中刷新UI,子线程中无效，因此用Handler来实现
                    new Handler().post(new Runnable() {
                        public void run() {
                            //在这里来写你需要刷新的地方
                            clickTab(shenbaoFragment);
                        }
                    });
                }else if("board".equals(change)){

                }
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    if (!StringUtil.isEmpty(extras)) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
                    setCostomMsg(showMsg.toString());
                }
            } catch (Exception e){
            }
        }
    }

    private void setCostomMsg(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }


    /**
     * 初始化所有fragment
     */
    private void initFragment() {
        fragmentTransaction = getFragmentManager().beginTransaction();
        if (!shenbaoFragment.isAdded()) {
            fragmentTransaction.add(R.id.content, shenbaoFragment);
            fragmentTransaction.hide(shenbaoFragment);
        }
        if (!xunchaFragment.isAdded()) {
            fragmentTransaction.add(R.id.content, xunchaFragment);
            fragmentTransaction.hide(xunchaFragment);
        }
        if (!boardFragment.isAdded()) {
            fragmentTransaction.add(R.id.content, boardFragment);
            fragmentTransaction.hide(boardFragment);
        }
        if (!meFragment.isAdded()) {
            fragmentTransaction.add(R.id.content, meFragment);
            fragmentTransaction.hide(meFragment);
        }
        hideAllFragment(fragmentTransaction);
        // 默认显示第一个fragment
        fragmentTransaction.show(shenbaoFragment);
        fragmentTransaction.commit();
    }

    /**
     * 隐藏所有fragment
     *
     * @param fragmentTransaction
     */
    private void hideAllFragment(FragmentTransaction fragmentTransaction) {
        fragmentTransaction.hide(shenbaoFragment);
        fragmentTransaction.hide(xunchaFragment);
        fragmentTransaction.hide(boardFragment);
        fragmentTransaction.hide(meFragment);
        Log.i("" ,"隐藏所有Fragment...");
    }

    /**
     * 初始化按钮单击事件
     */
    private void initClickEvent() {
        frame_layout_shenbao.setOnClickListener(this);
        frame_layout_xuncha.setOnClickListener(this);
        frame_layout_board.setOnClickListener(this);
        frame_layout_me.setOnClickListener(this);
    }

    /**
     * 初始化组件
     */
    private void initView() {
        frame_layout_shenbao = (FrameLayout) findViewById(R.id.frame_layout_shenbao);
        frame_layout_xuncha = (FrameLayout) findViewById(R.id.frame_layout_xuncha);
        frame_layout_board = (FrameLayout) findViewById(R.id.frame_layout_board);
        frame_layout_me = (FrameLayout) findViewById(R.id.frame_layout_me);

        imageview_shenbao = (ImageView) findViewById(R.id.imageview_shenbao);
        imageview_xuncha = (ImageView) findViewById(R.id.imageview_xuncha);
        imageview_board = (ImageView) findViewById(R.id.imageview_board);
        imageview_me = (ImageView) findViewById(R.id.imageview_me);

        textview_shenbao = (TextView) findViewById(R.id.textview_shenbao);
        textview_xuncha = (TextView) findViewById(R.id.textview_xuncha);
        textview_board = (TextView) findViewById(R.id.textview_board);
        textview_me = (TextView) findViewById(R.id.textview_me);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frame_layout_shenbao:
                // 点击申报tab
                clickTab(shenbaoFragment);
                break;

            case R.id.frame_layout_xuncha:
                // 点击巡查tab
                clickTab(xunchaFragment);
                break;

            case R.id.frame_layout_board:
                // 点击广告牌tab
                Log.i("","点击广告牌了。。。。。。");
                clickTab(boardFragment);
                break;

            case R.id.frame_layout_me:
                // 点击我tab
                clickTab(meFragment);
                break;

            default:
                break;
        }
    }

    /**
     * 点击下面的Tab按钮
     *
     * @param tabFragment
     */
    public void clickTab(Fragment tabFragment) {

        // 清除上次选中状态
        clearSeleted();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        // 隐藏所有fragment
        hideAllFragment(fragmentTransaction);
        // 显示该Fragment
        fragmentTransaction.show(tabFragment);
        // 提交事务
        fragmentTransaction.commit();
        // 改变tab的样式,设置为选中状态
        changeTabStyle(tabFragment);
    }

    /**
     * 清除上次选中状态
     */
    private void clearSeleted() {
        if (!shenbaoFragment.isHidden()) {
            imageview_shenbao.setImageResource(R.drawable.icon_menu_1_off);
            textview_shenbao.setTextColor(Color.parseColor("#999999"));
            Log.i("" ,"清除申报选中。");
        }

        if (!xunchaFragment.isHidden()) {
            imageview_xuncha.setImageResource(R.drawable.icon_menu_2_off);
            textview_xuncha.setTextColor(Color.parseColor("#999999"));
            Log.i("" ,"清除巡查选中。");
        }

        if (!boardFragment.isHidden()) {
            imageview_board.setImageResource(R.drawable.icon_menu_3_off);
            textview_board.setTextColor(Color.parseColor("#999999"));
            Log.i("" ,"清除广告牌选中。");
        }

        if (!meFragment.isHidden()) {
            imageview_me.setImageResource(R.drawable.icon_menu_4_off);
            textview_me.setTextColor(Color.parseColor("#999999"));
            Log.i("" ,"清除wo选中。");
        }
    }

    /**
     * 根据Fragment的状态改变样式
     */
    private void changeTabStyle(Fragment tabFragment) {
        if (tabFragment instanceof FragmentShenbao) {
            imageview_shenbao.setImageResource(R.drawable.icon_menu_1_on);
            textview_shenbao.setTextColor(Color.parseColor("#FFF05B48"));
            Log.i("" ,"选中申报。");
        }

        if (tabFragment instanceof FragmentXuncha) {
            imageview_xuncha.setImageResource(R.drawable.icon_menu_2_on);
            textview_xuncha.setTextColor(Color.parseColor("#FFF05B48"));
            Log.i("" ,"选中巡查。");
        }

        if (tabFragment instanceof FragmentBoard) {
            imageview_board.setImageResource(R.drawable.icon_menu_3_on);
            textview_board.setTextColor(Color.parseColor("#FFF05B48"));
            Log.i("" ,"选中广告牌。");
        }

        if (tabFragment instanceof FragmentMe) {
            imageview_me.setImageResource(R.drawable.icon_menu_4_on);
            textview_me.setTextColor(Color.parseColor("#FFF05B48"));
            Log.i("" ,"选中wo 。");
        }
    }

    /**
     * TODO 返回键值事件
     */
    @SuppressLint("WrongConstant")
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /** 获取网页标题 **/
        // String name = this.getTextEditValue(this.titlebar_name_text);
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (!isExit) {
                isExit = true;
                Toast.makeText(this, "再按一次退出程序!", Toast.LENGTH_SHORT).show();
                this.home_exit_handler.sendEmptyMessageDelayed(0, 2500);
            } else {
                AppManager.getAppManager().finishAllActivity();
                Intent localIntent2 = new Intent("android.intent.action.MAIN");
                localIntent2.addCategory("android.intent.category.HOME");
                localIntent2.setFlags(67108864);
                startActivity(localIntent2);
                new Handler().postDelayed(new Runnable()
                {
                    public void run()
                    {
                        System.exit(0);
                    }
                } , 20L);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }
        return false;
    }

    private static final String TAG = "MainFragmentActivity";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10010)//添加广告1001
        {
            //getDataFromServer();

            clickTab(xunchaFragment);
            Log.i(TAG,"-----------------"+TAG+"==添加广告牌巡查回调。。。");
        }
        else if (requestCode == 10020)//修改广告1002
        {
            //getDataFromServer();

            clickTab(xunchaFragment);
            Log.i(TAG,"-----------------"+TAG+"==修改广告牌巡查回调。。。");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消注册广播,防止内存泄漏
        try{
            localBroadcastManager.unregisterReceiver( mMessageReceiver );
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }



}
