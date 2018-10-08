package com.example.administrator.goldaappnew.common;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.example.administrator.goldaappnew.staticClass.StaticMember;
import com.example.administrator.goldaappnew.utils.AssistUtil;
import com.example.administrator.goldaappnew.utils.DateHelper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.Log;


public class MyLogger {

    private static boolean logFlag = true;
    public final static String tag = "[nj-golda]";
    private final static int logLevel = Log.VERBOSE;
    // 用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();
    private static Hashtable<String, MyLogger> sLoggerTable = new Hashtable<String, MyLogger>();
    private String mClassName;

    private static MyLogger dlog;

    private static final String NATON = "@Golda@ ";

    // 日志写入文件总开关
    private static Boolean mylog_write_switch = true;
    // 日志文件在SdCard中的路径
    private static String mylog_path_sdcard_dir = AssistUtil.getMemoryPath() + StaticMember.RUN_LOG;

    // sd卡中日志文件的最多保存天数
    private static int sdcard_log_file_save_days = 2;
    // 本类输出的日志文件名称
    private static String write_file_prefix = "runLog-";	//文件前缀
    private static String write_file_suffix = ".txt";		//文件后缀
    private MyLogger(String name) {
        mClassName = name;
    }

    /**
     * @param className
     * @return
     */
    @SuppressWarnings("unused")
    private static MyLogger getLogger(String className) {
        MyLogger classLogger = (MyLogger) sLoggerTable.get(className);
        if (classLogger == null) {
            classLogger = new MyLogger(className);
            sLoggerTable.put(className, classLogger);
        }
        return classLogger;
    }

    /**
     * Purpose:Mark user one
     *
     * @return
     */
    public static MyLogger Log() {
        if (dlog == null) {
            dlog = new MyLogger(NATON);
            logFlag = StaticMember.LOG_FLAG;
            mylog_write_switch = StaticMember.MYLOG_WRITE_SWITCH;
        }
        return dlog;
    }

    /**
     * Get The Current Function Name
     *
     * @return
     */
    private String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return null;
        }
        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            if (st.getClassName().equals(this.getClass().getName())) {
                continue;
            }
            return mClassName + "[ " + Thread.currentThread().getName() + ": "
                    + st.getFileName() + ":" + st.getLineNumber() + " "
                    + st.getMethodName() + " ]";
        }
        return null;
    }

    /**
     * The Log Level:i
     *
     * @param str
     */
    public void i(Object str) {
        if (logFlag) {
            if (logLevel <= Log.INFO) {
                String name = getFunctionName();
                if (name != null) {
                    Log.i(tag, name + " ->\n" + str);
                    writeLogtoFile(tag, "I", name + " ->\n" + str);
                } else {
                    Log.i(tag, str.toString());
                    writeLogtoFile(tag, "I", str.toString());
                }
            }
        }
    }

    /**
     * The Log Level:d
     *
     * @param str
     */
    public void d(Object str) {
        if (logFlag) {
            if (logLevel <= Log.DEBUG) {
                String name = getFunctionName();
                if (name != null) {
                    Log.d(tag, name + "-\n" + str);
                    writeLogtoFile(tag, "D", name + "-\n" + str);
                } else {
                    Log.d(tag, "\n" + str.toString());
                    writeLogtoFile(tag, "D", "\n" + str.toString());
                }
            }
        }
    }

    /**
     * The Log Level:V
     *
     * @param str
     */
    public void v(Object str) {
        if (logFlag) {
            if (logLevel <= Log.VERBOSE) {
                String name = getFunctionName();
                if (name != null) {
                    Log.v(tag, name + " - " + str);
                    writeLogtoFile(tag, "V", name + " - " + str);
                } else {
                    Log.v(tag, str.toString());
                    writeLogtoFile(tag, "V", str.toString());
                }
            }
        }
    }

    /**
     * The Log Level:w
     *
     * @param str
     */
    public void w(Object str) {
        if (logFlag) {
            if (logLevel <= Log.WARN) {
                String name = getFunctionName();
                if (name != null) {
                    Log.w(tag, name + " - " + str);
                    writeLogtoFile(tag, "W", name + " - " + str);
                } else {
                    Log.w(tag, str.toString());
                    writeLogtoFile(tag, "W", str.toString());
                }
            }
        }
    }

    /**
     * The Log Level:e
     *
     * @param str
     */
    public void e(Object str) {
        if (logFlag) {
            if (logLevel <= Log.ERROR) {
                String name = getFunctionName();
                if (name != null) {
                    writeLogtoFile(tag, "E", name + " - " + str);
                    Log.e(tag, name + " - " + str);
                } else {
                    writeLogtoFile(tag, "E", str.toString());
                    Log.e(tag, str.toString());
                }
            }
        }
    }

    /**
     * The Log Level:e
     *
     * @param ex
     */
    public void e(Exception ex) {
        if (logFlag) {
            if (logLevel <= Log.ERROR) {
                writeLogtoFile(tag, "E", ex.toString());
                Log.e(tag, "error", ex);
            }
        }
    }

    /**
     * The Log Level:e
     *
     * @param log
     * @param tr
     */
    public void e(String log, Throwable tr) {
        if (logFlag) {
            String line = getFunctionName();
            String strs = "{Thread:" + Thread.currentThread().getName() + "}"
                    + "[" + mClassName + line + ":] " + log + "\n";
            writeLogtoFile(tag, "ERROR", strs);
            Log.e(tag, "{Thread:" + Thread.currentThread().getName() + "}"
                    + "[" + mClassName + line + ":] " + log + "\n", tr);
        }
    }

    /**
     * 打开日志文件并写入日志
     *
     * @return
     * **/
    private static void writeLogtoFile(String mylogtype, String tag, String text) {
        if(!mylog_write_switch){
            return;
        }
        // 新建或打开日志文件
        String time_stamp = DateHelper.getToday("yyyy-MM-dd");
        String needWriteMessage = "["+DateHelper.getToday("yyyy-MM-dd HH:mm:ss")+"]"+ "-->" + mylogtype
                + "    " + tag + "    " + text;
        File filePath = new File(mylog_path_sdcard_dir);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        File file = new File(mylog_path_sdcard_dir, write_file_prefix + time_stamp+ write_file_suffix);
        try {
            if (file.exists()) {
                file.createNewFile();
            }
            FileWriter filerWriter = new FileWriter(file, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(needWriteMessage);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();

            String timeStamp = DateHelper.getDateString(getDateBefore(), "yyyy-MM-dd");
            delFile(mylog_path_sdcard_dir, write_file_prefix + timeStamp+ write_file_suffix);//删除文件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除制定的日志文件
     */
    public static void delFile(String file_path,String file_name) {
        File file = new File(file_path,file_name);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 得到现在时间前的几天日期，用来得到需要删除的日志文件名
     */
    private static Date getDateBefore() {
        Date nowtime = new Date();
        Calendar now = Calendar.getInstance();
        now.setTime(nowtime);
        now.set(Calendar.DATE, now.get(Calendar.DATE)
                - sdcard_log_file_save_days);
        return now.getTime();
    }

    /**
     * 收集设备参数信息
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
            infos.put("mobileModel", Build.MODEL);
            infos.put("androidVersion", Build.VERSION.RELEASE);
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    infos.put(field.getName(), field.get(null).toString());
                    this.d(field.getName() + " : " + field.get(null));
                } catch (Exception e) {
                }
            }

            StringBuffer sb = new StringBuffer();
            for (Map.Entry<String, String> entry : infos.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                sb.append(key + "=" + value + "\n");
            }

            String path = AssistUtil.getMemoryPath() + StaticMember.RUN_LOG;
            String fileName = "deviceInfo-"+DateHelper.getToday("yyyy-MM-dd")+".log";
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(path, fileName);
            if (file.exists()) {
                file.createNewFile();
            }
            FileWriter filerWriter = new FileWriter(file, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(sb.toString());
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
            this.d("\n设备信息写入日志文件成功。\n");

            String file_name = "deviceInfo-"+DateHelper.getDateString(getDateBefore(), "yyyy-MM-dd")+".log";
            delFile(path, file_name);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

