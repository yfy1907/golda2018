package com.example.administrator.goldaappnew.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.example.administrator.goldaappnew.staticClass.StaticMember;

/**
 * http工具类
 */
@SuppressLint("UseValueOf")
public class HttpUtil {
    private static final String TAG = "HttpUtil";
    private static final String CHARSET = HTTP.UTF_8;
    private static HttpClient httpClient;
    private Context context;
    public boolean userAgent = false;
    public static String User_Agent = "Mozilla/5.0 (Linux; U; Android 2.3.6; zh-cn; GT-I9001 Build/GINGERBREAD) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";

    public HttpUtil() {
    }

    public HttpUtil(Context context) {
        this.context = context;
        userAgent = false;

    }

    public HttpUtil(Context context, boolean userAgent) {
        this.context = context;
        this.userAgent = userAgent;

    }

    @SuppressWarnings("deprecation")
    public void setUserAgent() {
        httpClient.getParams().setParameter(HttpProtocolParams.USER_AGENT, User_Agent);
    }

    /**
     * get请求方式获取网络资源
     *
     * @param url
     * @param httpParams
     * @return
     * @throws Exception
     */
    public static HttpResponse httpGet(String url, HttpParams httpParams)
            throws Exception {
        // 通过HTTPClient来进行下载
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        if (httpParams != null) {
            httpGet.setParams(httpParams);
        }
        HttpResponse httpResponse = httpClient.execute(httpGet);
        return httpResponse;
    }


    @SuppressLint("UseValueOf")
    @SuppressWarnings("deprecation")
    public static synchronized HttpClient getHttpClient() {
        HttpParams params = new BasicHttpParams();
        // 设置字符集
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setUseExpectContinue(params, false);
        HttpProtocolParams.setContentCharset(params, CHARSET);
        // 从连接池获取连接超时
        ConnManagerParams.setTimeout(params, StaticMember.HTTP_REQUEST_TIME_OUT);
        // 连接超时
        HttpConnectionParams.setConnectionTimeout(params, StaticMember.HTTP_REQUEST_TIME_OUT);
        // Socket超时
        HttpConnectionParams.setSoTimeout(params, StaticMember.HTTP_REQUEST_TIME_OUT);

        HttpConnectionParams.setSocketBufferSize(params, 8192);
//		// 设置最大连接数
//		ConnManagerParams.setMaxTotalConnections(params, 100);

//		ConnManagerParams.setMaxConnectionsPerRoute(params,
//				new ConnPerRoute() {
//					@Override
//					public int getMaxForRoute(HttpRoute route) {
//						// TODO Auto-generated method stub
//						return 100;
//					}
//				});
//
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), StaticMember.PORT));
//		schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//		schReg.register(new Scheme("https", PlainSocketFactory.getSocketFactory(), 80));
//		schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
//		schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 430));
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
		httpClient = new DefaultHttpClient(conMgr, params);

        final ThreadSafeClientConnManager connectionManager=new ThreadSafeClientConnManager(params,schReg);
        HttpClient httpClient=new DefaultHttpClient(connectionManager,params);
        // 异常自动恢复处理, 使用HttpRequestRetryHandler接口实现请求的异常恢复
        ((DefaultHttpClient) httpClient).setHttpRequestRetryHandler(myRetryHandler);
        //httpClient.getParams().setParameter(HttpProtocolParams.USER_AGENT, User_Agent);
        //httpClient.getParams().setParameter("http.socket.timeout", new Integer(Constants.HTTP_REQUEST_TIME_OUT));
        return httpClient;
    }

    @SuppressLint("UseValueOf")
    @SuppressWarnings("deprecation")
    public static synchronized HttpClient getHttpClient(String functionName1,int request_timeout) {
        HttpParams params = new BasicHttpParams();
        // 设置字符集
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setUseExpectContinue(params, false);
        HttpProtocolParams.setContentCharset(params, CHARSET);
        // 从连接池获取连接超时
        ConnManagerParams.setTimeout(params, request_timeout);
        // 连接超时
        HttpConnectionParams.setConnectionTimeout(params, request_timeout);
        // Socket超时
        HttpConnectionParams.setSoTimeout(params, request_timeout);

        HttpConnectionParams.setSocketBufferSize(params, 8192);
//		// 设置最大连接数
//		ConnManagerParams.setMaxTotalConnections(params, 100);

//		ConnManagerParams.setMaxConnectionsPerRoute(params,
//				new ConnPerRoute() {
//					@Override
//					public int getMaxForRoute(HttpRoute route) {
//						// TODO Auto-generated method stub
//						return 100;
//					}
//				});
//
        SchemeRegistry schReg = new SchemeRegistry();
//        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), Constants.SERVER_PORT));
//		schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//		schReg.register(new Scheme("https", PlainSocketFactory.getSocketFactory(), 80));
//		schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
//		schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 430));
//		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
//		httpClient = new DefaultHttpClient(conMgr, params);

        final ThreadSafeClientConnManager connectionManager=new ThreadSafeClientConnManager(params,schReg);
        HttpClient httpClient=new DefaultHttpClient(connectionManager,params);
        // 异常自动恢复处理, 使用HttpRequestRetryHandler接口实现请求的异常恢复
        ((DefaultHttpClient) httpClient).setHttpRequestRetryHandler(myRetryHandler);
        //httpClient.getParams().setParameter(HttpProtocolParams.USER_AGENT, User_Agent);
        //httpClient.getParams().setParameter("http.socket.timeout", new Integer(Constants.HTTP_REQUEST_TIME_OUT));
        return httpClient;
    }

    static HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
        @SuppressWarnings("deprecation")
        public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
            //System.out.println("retryRequest＝" + executionCount);
            if (executionCount >= 2) // 如果超过最大重试次数，那么就不要继续了
                return false;
            if (exception instanceof NoHttpResponseException)// 如果服务器丢掉了连接，那么就重试
                return true;
            if (exception instanceof SSLHandshakeException)// 不要重试SSL握手异常
                return false;
            if (exception instanceof ConnectTimeoutException)//
            {
                //System.out.println("retryRequest ConnectTimeoutException ");
                //MyLogger.Log().e("retryRequest ConnectTimeoutException functionName=“"+functionName+"”；");
                return false;
            } else if (exception instanceof SocketTimeoutException)//
            {
                //MyLogger.Log().e("retryRequest SocketTimeoutException functionName=“"+functionName+"”；");
                //System.out.println("retryRequest SocketTimeoutException ");
                return false;
            }
            HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
            boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
            if (idempotent) // 如果请求被认为是幂等的，那么就重试
                return true;
            return false;
        }

    };

    public static List<NameValuePair> convertMapToNameValuePairs(Map<String, Object> params) {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        if (params != null) {
            Set<String> keys = params.keySet();
            Iterator<String> it = keys.iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key).toString();
                NameValuePair pair;
                try {
                    /*
                     * pair = new BasicNameValuePair(key,
                     * URLEncoder.encode(value, "UTF-8"));
                     */
                    pair = new BasicNameValuePair(key, value);
                    pairs.add(pair);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return pairs;
    }

    public static String getResponseText(HttpResponse response) {
        HttpEntity responseEntity = response.getEntity();
        InputStream input = null;
        String result = null;
        try {
            input = responseEntity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    input));
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            result = sb.toString();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public static String parseStringFromEntity(HttpEntity entity) {
        String result = null;
        try {
            InputStream input = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    input));
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            result = sb.toString();
        } catch (Exception e) {
            System.out.println(e);
        }
        return result;
    }

    public static Map<String, String> decodeByDecodeNames(
            List<String> decodeNames, Map<String, String> map) {
        Set<String> keys = map.keySet();
        Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            String key = it.next();
            String value = map.get(key);
            for (String decodeName : decodeNames) {
                if (key.equals(decodeName)) {
                    value = URLDecoder.decode(value);
                    map.put(key, value);
                }
            }
        }
        return map;
    }


}
