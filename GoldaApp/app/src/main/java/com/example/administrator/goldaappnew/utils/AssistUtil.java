package com.example.administrator.goldaappnew.utils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;

import com.example.administrator.goldaappnew.staticClass.StaticMember;

public class AssistUtil {
    /**
     * 已安装插件
     */
    public static final int PLUGIN_FOUND = 1;
    /**
     * 未安装插件
     */
    public static final int PLUGIN_NOT_FOUND = 0;

    @SuppressLint("WrongConstant")
    public static int checkApp(Activity activity, String packageName) {
        try {
            activity.getPackageManager().getApplicationInfo(packageName, 1);
        } catch (NameNotFoundException e) {
            return AssistUtil.PLUGIN_NOT_FOUND;
        }
        return AssistUtil.PLUGIN_FOUND;
    }

    @SuppressWarnings("static-access")
    public static List<PackageInfo> getAllApps(Context context) {
        List<PackageInfo> appsList = new ArrayList<PackageInfo>();
        PackageManager pManager = context.getPackageManager();
        //获取手机内所有应用
        List<PackageInfo> paklist = pManager.getInstalledPackages(0);
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo pak = (PackageInfo) paklist.get(i);
            //判断是否为非系统预装的应用程序
            if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
                // customs applications
                appsList.add(pak);
            }
        }
        return appsList;
    }

    /**
     * 安装插件
     *
     * @param activity
     * @param apkName  插件apk名称
     * @return
     */
    public static boolean installPlugin(Activity activity, String apkName) {
        try {
            Object localObject1 = activity.getAssets().open(apkName);
            @SuppressLint("WrongConstant") Object localObject2 = activity.openFileOutput(apkName, 1);
            byte[] arrayOfByte = new byte[1024];
            int j = 0;
            while ((j = ((InputStream) localObject1).read(arrayOfByte)) > 0)
                ((FileOutputStream) localObject2).write(arrayOfByte, 0, j);
            ((FileOutputStream) localObject2).close();
            ((InputStream) localObject1).close();
            localObject1 = activity.getFilesDir().getAbsolutePath();
            localObject2 = localObject1 + File.separator + apkName;
            if (localObject1 != null) {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setDataAndType(Uri.parse("file:" + (String) localObject2), "application/vnd.android.package-archive");
                activity.startActivity(intent);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean installPlugin(Activity activity) {
        String str = "/RemovePlugin.apk";
        String fileName = Environment.getExternalStorageDirectory() + str;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
        activity.startActivity(intent);
        return true;
    }


    /**
     * 判断SD卡是否可用
     *
     * @return true：可以使用 false：不可使用
     */
    public static boolean ExistSDCard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }

    /**
     * 获取SD卡存储剩余空间
     *
     * @return
     */
    public static long getSDFreeSize() {
        //取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        //空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        //返回SD卡空闲大小
        //return freeBlocks * blockSize;  //单位Byte
        //return (freeBlocks * blockSize)/1024;   //单位KB
        return (freeBlocks * blockSize) / 1024 / 1024; //单位MB
    }

    /**
     * 获取手机存储剩余空间
     *
     * @return
     */
    public static long getSystemDataFreeSize() {
        //取得SD卡文件路径
        File path = Environment.getDataDirectory();
        StatFs sf = new StatFs(path.getPath());
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        //空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        //返回SD卡空闲大小
        //return freeBlocks * blockSize;  //单位Byte
        //return (freeBlocks * blockSize)/1024;   //单位KB
        return (freeBlocks * blockSize) / 1024 / 1024; //单位MB
    }

    public static String getMemoryPath() {
        String path = "";//路径规范：sd卡、机身存储空间/njry/项目名/日志、错误日志等文件夹
        if (ExistSDCard() == true && getSDFreeSize() > 5) {
            path = "/sdcard/" + StaticMember.APP_NAME + "/";
        } else {
            path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + StaticMember.APP_NAME + "/";
        }
        return path;
    }
}