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
import com.example.administrator.goldaappnew.jpush.LocalBroadcastManager;
import com.example.administrator.goldaappnew.utils.AppManager;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_main);

        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();


        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("MainFragment");
        localBroadcastManager.registerReceiver(mAdReceiver, intentFilter);


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

    private BroadcastReceiver mAdReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
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
        }
    };

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
            localBroadcastManager.unregisterReceiver( mAdReceiver );
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }



}
