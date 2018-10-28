package com.example.administrator.goldaappnew;

import android.app.Application;
import android.content.Context;

import com.example.administrator.goldaappnew.common.CrashHandler;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import cn.jpush.android.api.JPushInterface;


public class APPAplication extends Application{

    public static APPAplication CONTEXT;
    private static final int REQUEST_READ_PHONE_STATE = 1;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        CONTEXT = this;
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());

        // TODO:  2016/12/28  设置开启日志,发布时请关闭日志
        //初始化极光推送
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

    }


    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

}
