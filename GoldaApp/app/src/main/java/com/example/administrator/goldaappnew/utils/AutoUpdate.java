package com.example.administrator.goldaappnew.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;

import com.example.administrator.goldaappnew.activity.LoginActivity;
import com.example.administrator.goldaappnew.common.DownloadDialog;
import com.example.administrator.goldaappnew.staticClass.StaticMember;

@SuppressLint({ "HandlerLeak", "DefaultLocale" })
public class AutoUpdate {
    public Activity activity = null;
    public int versionCode = 0;
    public String versionName = "";
    public String newVersionName = "updateGolda.apk";
    public String newVersionCode = "";
    private static final String TAG = "AutoUpdate";
    private String currentFilePath = "";
    private String currentTempFilePath = "";
    private String strURL = StaticMember.DOWNLOAD_APK_URL;
    private ProgressDialog dialog;
    // 变量类
    private int fileSize;
    private int downLoadFileSize;
    private boolean isMustUpdate = false; // 是否强制升级（强制升级，隐藏 后台下载 按钮）
    private AlertDialog alertDialog;

    public AutoUpdate(Activity activity, boolean isMustUpdate, String newVersionCode) {
        this.activity = activity;
        this.isMustUpdate = isMustUpdate;
        this.newVersionCode = newVersionCode;
        getCurrentVersion();
    }

    public static boolean isNetworkAvailable(Context ctx) {
        try {
            ConnectivityManager cm = (ConnectivityManager) ctx
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            return (info != null && info.isConnected());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    OnClickListener ok_listener = new OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            showWaitDialog();
            downloadTheFile(strURL);
        }
    };

    OnClickListener cancel_listener = new OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            Log.d(TAG, "cancel_listener...");
        }
    };

    /**
     * 强制升级版本下，不升级直接退出系统
     */
    OnClickListener cancel_system_listener = new OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            Log.d(TAG, "cancel_listener...");
            activity.finish();
        }
    };

    public void setCancelClickListener(OnClickListener cancel_listener) {
        this.cancel_listener = cancel_listener;
    }

    private Dialog noticeDialog;
    // 外部接口让主Activity调用
    public void checkUpdateInfo(String updateMsg) {
        showNoticeDialog(updateMsg);
    }
    private void showNoticeDialog(String updateMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);

        //设置点击对话框外部区域不关闭对话框
        builder.setCancelable(false);

        builder.setTitle("软件版本更新");
        builder.setMessage(updateMsg);
        builder.setPositiveButton("下载", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                showWaitDialog();
                downloadTheFile(strURL);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("以后再说", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent i = new Intent(activity,LoginActivity.class);
                activity.startActivity(i);
                ((Activity)activity).finish();
            }
        });
        noticeDialog = builder.create();
        noticeDialog.show();
    }


    public void showUpdateDialog2() {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this.activity);
        localBuilder.setTitle("新版本："+this.newVersionCode).setMessage("检测到新版本，是否立刻更新?")
//                .setIcon(R.drawable.ic_launcher)
                .setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
                    public void onClick(
                            DialogInterface paramAnonymousDialogInterface,
                            int paramAnonymousInt) {
                        alertDialog.dismiss();
                    }
                }).setPositiveButton("现在升级", new DialogInterface.OnClickListener() {
            public void onClick(
                    DialogInterface paramAnonymousDialogInterface,
                    int paramAnonymousInt) {
                alertDialog.dismiss();
                showWaitDialog();
                downloadTheFile(strURL);
            }
        });
        alertDialog = localBuilder.show();
    }

    private void sendMsg(int flag) {
        Message msg = new Message();
        msg.what = flag;
        handler.sendMessage(msg);
    }

    public DownloadDialog downdlg;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// 定义一个Handler，用于处理下载线程与UI间通讯
            super.handleMessage(msg);
            if (msg.what == 0) {
                downdlg.setMax(fileSize);
                downdlg.setMsgAndValue("新版本下载中，请稍候：" + "0%", downLoadFileSize);
            } else if (msg.what == 1) {
                int result = downLoadFileSize * 100 / fileSize;
                downdlg.setMsgAndValue("新版本下载中，请稍候：" + result + "%", downLoadFileSize);
                if (result == 100) {
                    if (null != downdlg) {
                        downdlg.cancel();
                        downdlg.dismiss();
                    }
                }
            }
        }
    };

    /**
     * 显示版本更新对话框（强制升级）
     */
    public void showMustUpdateDialog() {
        showWaitDialog();
        downloadTheFile(strURL);
    }

    /**
     * 显示新版本下载进度对话框
     */
    public void showWaitDialog() {
        downdlg = new DownloadDialog("新版本下载中，请耐心等候...", this.activity,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downdlg.dismiss();
                    }
                }, isMustUpdate);
        downdlg.setCancelable(false);
        downdlg.show();
    }

    /**
     * 显示当前版本信息
     */
    public void getCurrentVersion() {
        try {
            PackageInfo info = activity.getPackageManager().getPackageInfo(
                    activity.getPackageName(), 0);
            this.versionCode = info.versionCode;
            this.versionName = info.versionName;
            // Log.d(TAG,"当前客户端versionCode="+versionCode+",versionName="+versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载新版本文件
     *
     * @param strPath
     */
    private void downloadTheFile(final String strPath) {
        try {
            if (strPath.equals(currentFilePath)) {
                doDownloadTheFile(strPath);
            }
            currentFilePath = strPath;
            /***** 判断存储条件，SD卡是否有用，自带存储是否有用 *****/
            if (AssistUtil.ExistSDCard()) {// AssistUtil.ExistSDCard() 判断SD卡是否可用
                Log.i("SD卡剩余空间：", AssistUtil.getSDFreeSize() + "");
                // AssistUtil.getSDFreeSize() 获取SD卡剩余空间 单位：M
                if (AssistUtil.getSDFreeSize() < 5) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            this.activity).setTitle("系统升级")
//                            .setIcon(R.drawable.info)
                            .setMessage("SD卡剩余空间不足，无法进行下载升级，请删减后重试！");
                    builder.setNegativeButton("确认", cancel_listener);
                    builder.show();
                    if (dialog != null) {
                        dialog.cancel();
                        dialog.dismiss();
                    }
                    return;
                }
            } else {
                // 如果SD卡不可用，判断自带存储空间是否足够
                // AssistUtil.getSDFreeSize() 获取SD卡剩余空间 单位：M
                if (AssistUtil.getSystemDataFreeSize() < 5) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            this.activity).setTitle("系统升级")
//                            .setIcon(R.drawable.info)
                            .setMessage("SD卡不可用，且手机存储空间不足，无法进行下载升级！");
                    builder.setNegativeButton("确认", cancel_listener);
                    builder.show();
                    if (dialog != null) {
                        dialog.cancel();
                        dialog.dismiss();
                    }
                    return;
                }
            }

            // 如果满足下载条件，调用下载方法下载安装
            Runnable r = new Runnable() {
                public void run() {
                    try {
                        doDownloadTheFile(strPath);
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            };
            new Thread(r).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SdCardPath")
    private void doDownloadTheFile(String strPath) throws Exception {
        if (!URLUtil.isNetworkUrl(strPath)) {
            Log.i(TAG, "getDataSource() It's a wrong URL!");
        } else {
            URL myURL = new URL(strPath);
            URLConnection conn = myURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            fileSize = conn.getContentLength();// 根据响应获取文件大小
            downLoadFileSize = 0;
            sendMsg(0);
            if (is == null) {
                throw new RuntimeException("stream is null");
            }
            if (fileSize <= 0) {
                throw new RuntimeException("无法获知文件大小 ");
            }
            File path = null;
            // 如果SD卡存在，将文件保存到sd卡指定目录
            if (AssistUtil.ExistSDCard()) {
                path = new File(Environment.getExternalStorageDirectory() +"/"+ StaticMember.APP_NAME);// 获取sd卡
            } else {
                // 否则保存到自带存储
                // com.zpdyf.huilanyiliao 为AndroidManifest.xml里面package对应的值
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
            // newVersionName新客户端版本号
            FileOutputStream fos = null;
            File myTempFile = new File(path + "/" + newVersionName);
            fos = new FileOutputStream(myTempFile);
            byte buf[] = new byte[1024];
            do {
                int numread = is.read(buf);
                if (numread <= 0) {
                    break;
                }
                fos.write(buf, 0, numread);
                downLoadFileSize += numread;
                sendMsg(1);
            } while (true);
            Log.i(TAG, "getDataSource() Download  ok...");
            // dialog.cancel();
            // dialog.dismiss();
            try {
                is.close();
                fos.flush();
                fos.close();
            } catch (Exception ex) {
                Log.e(TAG, "getDataSource() error: " + ex.getMessage(), ex);
                ex.printStackTrace();
            }
            openFile(myTempFile);
        }
    }

    /**
     * 打开下载文件，执行安装操作
     *
     * @param f
     */
    private void openFile2(File f) {
        /**
         * 安装新版本之前，清除当前应用的所有数据
         */
        DataCleanManager.cleanApplicationData(activity, AssistUtil.getMemoryPath());
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        String type = getMIMEType(f);

        // 如果SD卡不可用
        if (!AssistUtil.ExistSDCard()) {
            // 设置文件目录访问权限
            // com.x 为AndroidManifest.xml里面package对应的值
            String cmd = "chmod 777 /data/data/"+StaticMember.APP_PACKAGE_NAME+"/files/"+StaticMember.APP_NAME;
            try {
                Runtime.getRuntime().exec(cmd);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        intent.setDataAndType(Uri.fromFile(f), type);
        activity.startActivity(intent);
    }

    private void openFile(File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(activity.getApplicationContext(), "com.example.administrator.goldaapp.fileprovider", apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        activity.startActivity(intent);
    }

    /**
     * 安装 apk 文件
     *
     * @param apkFile
     */
    public void installApk(File apkFile) {
       /* Intent installApkIntent = new Intent();
        installApkIntent.setAction(Intent.ACTION_VIEW);
        installApkIntent.addCategory(Intent.CATEGORY_DEFAULT);
        installApkIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        installApkIntent.setDataAndType(Uri.fromFile(apkFile), MIME_TYPE_APK);

        if (sApp.getPackageManager().queryIntentActivities(installApkIntent, 0).size() > 0) {
            sApp.startActivity(installApkIntent);
        }*/
        //Toast.makeText(sApp,apkFile.getPath(),Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(activity, "com.example.administrator.goldaapp.fileprovider", apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (activity.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
            activity.startActivity(intent);
        }
    }

    /**
     * 执行Linux命令，并返回执行结果。 这里主要用来改变文件或者文件夹权限 apps:drwx---r-x → (4+2+1) + (0+0+0)
     * + (4+0+1) → 705 files.apk :-rw----r-- → (4+2+0) + (0+0+0) + (4+0+0) → 604
     * 即对apps执行：chmod 705 /data/data/<app_package>/apps 对file.apk执行:chmod 604
     * /data/data/<app_package>/apps/file.apk
     * */
    public static String exec(String[] args) {
        String result = "";
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = -1;
            process = processBuilder.start();
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1) {
                baos.write(read);
            }
            baos.write('\n');
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }
            byte[] data = baos.toByteArray();
            result = new String(data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (errIs != null) {
                    errIs.close();
                }
                if (inIs != null) {
                    inIs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
        System.out.println(result);
        return result;
    }

    /**
     * 删除文件
     */
    public void delFile() {
        Log.i(TAG, "The TempFile(" + currentTempFilePath + ") was deleted.");
        File myFile = new File(currentTempFilePath);
        if (myFile.exists()) {
            myFile.delete();
        }
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

    /**
     * 比较版本号
     *
     * @return true 远程版本比当前版本新
     */
    public boolean checkVersion(String versionName) {
        newVersionName = versionName;
        int exc = versionName.compareTo(this.versionName);
        return exc > 0 ? true : false;
    }

}