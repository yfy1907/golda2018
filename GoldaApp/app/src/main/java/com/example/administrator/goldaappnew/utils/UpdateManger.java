package com.example.administrator.goldaappnew.utils;

/**
 * Created by Administrator on 2017/7/31.
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.goldaappnew.R;
import com.example.administrator.goldaappnew.activity.LoginActivity;
import com.example.administrator.goldaappnew.staticClass.StaticMember;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateManger {
    private SharedPreferences loginInfo;
    private Context mContext;
    private static final String TAG="UpdateManger";

    static final String[] PERMISSION = new String[]{
            Manifest.permission.READ_CONTACTS,// 写入权限
            Manifest.permission.READ_EXTERNAL_STORAGE,  //读取权限
            Manifest.permission.WRITE_CALL_LOG,        //读取设备信息
            Manifest.permission.ACCESS_FINE_LOCATION, //定位信息
            Manifest.permission.CAMERA//相机权限
    };

    // 提示语
    private String updateMsg = "有最新的软件包哦，亲快下载吧~";

    // 返回你需要安装的安装包url
    private String apkUrl = StaticMember.ImageURL+"public/apk/app-release.apk";

    private Dialog noticeDialog;

    private Dialog downloadDialog;
    /* 下载包安装路径 */
    private static String newVersionName = "UpdateGolda.apk";

    /* 进度条与通知ui刷新的handler和msg常量 */
    private ProgressBar mProgress;
    private TextView tv_progress;

    private static final int DOWN_UPDATE = 1;

    private static final int DOWN_OVER = 2;

    private int progress;

    private Thread downLoadThread;

    private File updateApkTempFile = null;

    private boolean interceptFlag = false;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    Log.i(TAG, "handleMessage:+ "+progress);
                    mProgress.setProgress(progress);
                    tv_progress.setText(progress+"%");
                    break;
                case DOWN_OVER:
                    // installApk();

                    openFile(updateApkTempFile);
                    break;
                default:
                    break;
            }
        };
    };

    public UpdateManger(Context context) {
        this.mContext = context;
    }

    // 外部接口让主Activity调用
    public void checkUpdateInfo() {
        showNoticeDialog();
    }

    private void showNoticeDialog() {
        AlertDialog.Builder builder = new Builder(mContext);

        //设置点击对话框外部区域不关闭对话框
        builder.setCancelable(false);

        builder.setTitle("软件版本更新");
        builder.setMessage(updateMsg);
        builder.setPositiveButton("下载", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                showDownloadDialog();
            }
        });
        builder.setNegativeButton("以后再说", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent i = new Intent(mContext,LoginActivity.class);
                mContext.startActivity(i);
                ((Activity)mContext).finish();
            }
        });
        noticeDialog = builder.create();
        noticeDialog.show();
    }


    public void showDownLoad(){
        showDownloadDialog();
    }

    private void showDownloadDialog() {
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setCancelable(false);
        builder.setTitle("软件版本更新");

        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.main_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.pb_progress);
        tv_progress= (TextView) v.findViewById(R.id.tv_progress);
        builder.setView(v);
        builder.setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                interceptFlag = true;
                Intent i = new Intent(mContext,LoginActivity.class);
                mContext.startActivity(i);
                ((Activity)mContext).finish();
            }
        });
        downloadDialog = builder.create();
        downloadDialog.show();

        Log.i(TAG, "showDownloadDialog: 开始更新");
        downloadApk();
    }


    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            OkHttpClient mOkhttpClient=new OkHttpClient();
            Request request=new Request.Builder().url(apkUrl).build();
            Call mcall=mOkhttpClient.newCall(request);
            try {
                Response response=mcall.execute();
                if (null != response.cacheResponse()) {
                    String str = response.cacheResponse().toString();
                    Log.i(TAG, "onResult" + str);
                } else {
                    try {
                        String str = response.networkResponse().toString();
                        Log.i(TAG, "onResult请求结果" + str);

                        long length=response.body().contentLength();
                        InputStream is = response.body().byteStream();

                        File path = null;
                        // 如果SD卡存在，将文件保存到sd卡指定目录
                        if (AssistUtil.ExistSDCard()) {
                            path = new File(Environment.getExternalStorageDirectory() + "/"+StaticMember.APP_NAME);// 获取sd卡
                        } else {
                            // 否则保存到自带存储
                            // com.X为AndroidManifest.xml里面package对应的值
                            path = new File("/data/data/"+StaticMember.APP_PACKAGE_NAME+"/files/"+StaticMember.APP_NAME);// 获取手机自带存储
                        }
                        // 如果路径不存在，创建路径
                        if (!path.exists()) {
                            path.mkdir();
                        }
                        // com.X 为AndroidManifest.xml里面package对应的值，给文件夹授权访问权限
                        String cmd = "chmod 705 /data/data/"+StaticMember.APP_PACKAGE_NAME+"/files/"+StaticMember.APP_NAME;
                        try {
                            Runtime.getRuntime().exec(cmd);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        File myTempFile = new File(path + "/" + newVersionName);
                        updateApkTempFile = myTempFile;

                        FileOutputStream fos = new FileOutputStream(myTempFile);

                        int count = 0;
                        byte buf[] = new byte[1024];

                        do {
                            int numread = is.read(buf);
                            count += numread;
                            progress = (int) (((float) count / length) * 100);
                            // 更新进度
                            mHandler.sendEmptyMessage(DOWN_UPDATE);
                            if (numread <= 0) {
                                // 下载完成通知安装
                                mHandler.sendEmptyMessage(DOWN_OVER);
                                break;
                            }
                            fos.write(buf, 0, numread);
                        } while (!interceptFlag);// 点击取消就停止下载.

                        fos.close();
                        is.close();
                    } catch (Exception e) {
                        Log.i(TAG, "onResponse: Get解析出错");
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        };
    };

    /**
     * 下载apk
     *
     */

    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    /**
     * 打开下载文件，执行安装操作
     *
     * @param f
     */
    private void openFile(File f) {

        if(null == f){
            return;
        }
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        String type = getMIMEType(f);
        // 如果SD卡不可用
        if (!AssistUtil.ExistSDCard()) {
            // 设置文件目录访问权限
            // com.X 为AndroidManifest.xml里面package对应的值
            String cmd = "chmod 777 /data/data/"+StaticMember.APP_PACKAGE_NAME+"/files/"+StaticMember.APP_NAME;
            try {
                Runtime.getRuntime().exec(cmd);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        intent.setDataAndType(Uri.fromFile(f), type);
        mContext.startActivity(intent);
    }

    /**
     * 判断文件类型
     *
     * @param f
     * @return
     */
    private String getMIMEType(File f) {
        String type = "";
        String fName = f.getName();
        String end = fName
                .substring(fName.lastIndexOf(".") + 1, fName.length())
                .toLowerCase();
        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
                || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            type = "audio";
        } else if (end.equals("3gp") || end.equals("mp4")) {
            type = "video";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                || end.equals("jpeg") || end.equals("bmp")) {
            type = "image";
        } else if (end.equals("apk")) {
            type = "application/vnd.android.package-archive";
        } else {
            type = "*";
        }
        if (end.equals("apk")) {
        } else {
            type += "/*";
        }
        return type;
    }


}