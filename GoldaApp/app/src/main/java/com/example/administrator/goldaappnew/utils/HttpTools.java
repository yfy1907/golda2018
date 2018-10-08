package com.example.administrator.goldaappnew.utils;

/**
 * Created by Administrator on 2017/7/26.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.example.administrator.goldaappnew.common.MyLogger;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
/**
 */
public class HttpTools {

    private static final String TAG="HttpUtils";
    private static String resultMsg="";
    private static List list = new ArrayList();
    public static final MediaType JSON = MediaType.parse("application/json; charset=gbk");
    /**
     * Get数据
     * @param url
     * @param param
     * @param type
     * @return
     */
    public synchronized static List getJson(String url, String param, int type) {
        String urls=url+"?"+param;
        Log.i(TAG, "getJson: "+urls);
        MyLogger.Log().i("## 加载列表："+urls);
//        String urls="http://cs.jdjk.net/mob_login.php?username=admin&password=111111";
        OkHttpClient mOkHttpClient=new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder()
                .addHeader("accept", "*/*")
                .addHeader("connection", "Keep-Alive")
                .addHeader("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)")
                .url(urls);
        //可以省略，默认是GET请求
        requestBuilder.method("GET",null);
        Request request = requestBuilder.build();
        Call mcall= mOkHttpClient.newCall(request);

//        同步
        try {
            Response response=mcall.execute();
            if (null != response.cacheResponse()) {
                String str = response.cacheResponse().toString();
                Log.i(TAG, "onResult" + str);
            } else {
                try {
                    ResponseBody body=response.body();
                    String str = response.networkResponse().toString();
                    Log.i(TAG, "onResult请求结果" + str);
                    resultMsg=body.string();
                    Log.i(TAG, "onResult、----------" + resultMsg);
                    list=JsonTools.getListByJson(resultMsg, type);

                } catch (Exception e) {
                    Log.i(TAG, "onResponse: Get解析出错");
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "onResponse: Get"+list);

        if(null == list){
            list = new ArrayList();
        }
        return list;
    }

    public static String getPostMsg(String url,String param){
        String urls = url + "?"+param;
        Log.e(TAG, "getPostMsg: "+urls );
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder()
                .addHeader("accept", "*/*")
                .addHeader("connection", "Keep-Alive")
                .addHeader("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)")
                .url(urls);
        requestBuilder.method("GET",null);

        Request request = requestBuilder.build();
        Call mcall= mOkHttpClient.newCall(request);
        try {
            Response response=mcall.execute();
            if (null != response.cacheResponse()) {
                String str = response.cacheResponse().toString();
                Log.i(TAG, "onResult" + str);
            } else {
                ResponseBody body=response.body();
                String str = response.networkResponse().toString();
                Log.i(TAG, "onResult请求结果" + str);
                resultMsg=body.string();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "Error";
        }

        return resultMsg;
    }
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");//mdiatype 这个需要和服务端保持一致.
    /**
     * 统一为请求添加头信息
     * @return
     */
    private static Request.Builder addHeaders() {
        Request.Builder builder = new Request.Builder()
                .addHeader("accept", "*/*")
                .addHeader("connection", "Keep-Alive")
                .addHeader("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)")
                .addHeader("phoneModel", Build.MODEL)
                .addHeader("systemVersion", Build.VERSION.RELEASE);
        return builder;
    }

    public static String sendPostRequest222(String url,String data){
        //String urls = url + "?"+json;
        Log.e(TAG, "sendPostRequest: "+url );
        if(null == data){
            return "Error";
        }
        Map<String, String> paramsMap = GsonUtil.parseStringMap(data);
        if(null == paramsMap){
            return "Error";
        }
        //处理参数
        StringBuilder tempParams = new StringBuilder();
        int pos = 0;
        for (String key : paramsMap.keySet()) {
            if (pos > 0) {
                tempParams.append("&");
            }
            try {
                //tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                tempParams.append(String.format("%s=%s", key, paramsMap.get(key)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            pos++;
        }
        //生成参数
        String params = tempParams.toString();
        Log.i(",,,","params==="+params);
        OkHttpClient mOkHttpClient = new OkHttpClient();

        //使用JSONObject封装参数
        //创建RequestBody对象，将参数按照指定的MediaType封装
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, params);
        // 3 创建请求方式
        // Request request = new Request.Builder().url(url).post(requestBody).build();
        final Request request = addHeaders().url(url).post(requestBody).build();
        Call mcall= mOkHttpClient.newCall(request);
        try {
            Response response=mcall.execute();
            if (null != response.cacheResponse()) {
                String str = response.cacheResponse().toString();
                Log.i(TAG, "onResult" + str);
            } else {
                ResponseBody body=response.body();
                String str = response.networkResponse().toString();
                Log.i(TAG, "onResult请求结果：" + str);
                resultMsg=body.string();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "Error";
        }
        return resultMsg;
    }

    public static String sendPostRequest(String url,String data){
        Map<String, String> paramsMap = GsonUtil.parseStringMap(data);
        if(null == paramsMap){
            return "Error";
        }
        //处理参数
        ArrayList<BasicNameValuePair> localArrayList = new ArrayList<BasicNameValuePair>();
        String strs = "";
        for (String key : paramsMap.keySet()) {
            Log.i("","=====key="+key+"; values="+paramsMap.get(key));
            BasicNameValuePair vals = new BasicNameValuePair(key, paramsMap.get(key));
            localArrayList.add(vals);
            strs += "&"+key+"="+paramsMap.get(key);
        }
        Log.i("sendPostRequest##","url ="+url);
        Log.i("sendPostRequest##","strs ="+strs);

        HttpPost localHttpPost =  new HttpPost(url);
        try {
            localHttpPost.setEntity(new UrlEncodedFormEntity(localArrayList, "UTF-8"));
            HttpClient client = HttpUtil.getHttpClient();
            HttpResponse localHttpResponse = client.execute(localHttpPost);
            int httpStateCode = localHttpResponse.getStatusLine().getStatusCode();
            if (httpStateCode == 200) {
                resultMsg = EntityUtils.toString(localHttpResponse.getEntity());
            }else{
                return "Error";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error";
        }
        return resultMsg;
    }

    /**
     * 获取数据库版本号
     * @param url
     */
    public static String getNewVersion(String url){
        OkHttpClient okHttpClient=new OkHttpClient();
        Request.Builder requestBuilder=new Request.Builder()
                .addHeader("accept", "*/*")
                .addHeader("connection", "Keep-Alive")
                .addHeader("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)")
                .url(url);
        requestBuilder.method("GET",null);
        Request request=requestBuilder.build();
        Call call=okHttpClient.newCall(request);

        try {
            Response response=call.execute();
            if (null != response.cacheResponse()) {
                String str = response.cacheResponse().toString();
                Log.i(TAG, "onResult" + str);
            }else {
                resultMsg=response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultMsg;
    }

    public static Bitmap getUrlImage(String url){
        Bitmap image = null;
//        OkHttpClient mOkHttpClient = new OkHttpClient();
        // HTTP 超时使用
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30000, TimeUnit.MILLISECONDS)
                .readTimeout(30000, TimeUnit.MILLISECONDS)
                .build();
        Request.Builder requestBuilder=new Request.Builder().url(url);
        Request request=requestBuilder.build();
        Call call = mOkHttpClient.newCall(request);
        try {
            Response response=call.execute();
            InputStream is=response.body().byteStream();
            image=BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * @param context
     * 查看网络连接状态
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
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

}
