package com.example.administrator.goldaappnew.utils;

import android.util.Log;

import com.example.administrator.goldaappnew.bean.AdGreenBean;
import com.example.administrator.goldaappnew.bean.AdRedBean;
import com.example.administrator.goldaappnew.bean.BoardBean;
import com.example.administrator.goldaappnew.bean.UserBean;
import com.example.administrator.goldaappnew.staticClass.StaticMember;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */

public class JsonTools {
    private static final String TAG="JsonTools";
    public static List getListByJson(String json, int style) throws Exception {
        Log.i(TAG, "getListByJson: Gson");
        if(null == json || "".equals(json)){
            Log.i(TAG, "getListByJson: json为空");
            return null;
        }
        if(json.toString().equals("Error:Login")){
            Log.i(TAG, "getListByJson: error");
            return null;
        }

        try{
            Gson gson=new Gson();
            if(style == StaticMember.BOARD_LIST){
                List<BoardBean> boardList = gson.fromJson(json,new TypeToken<List<BoardBean>>(){}.getType());
                return boardList;
            }
            if (style== StaticMember.ADLIST_GREEN){

                List<AdGreenBean> greenlist = gson.fromJson(json,new TypeToken<List<AdGreenBean>>(){}.getType());
                return greenlist;

            }
            if (style==StaticMember.SEARCH){
                List<AdGreenBean> searchlist = gson.fromJson(json,new TypeToken<List<AdGreenBean>>(){}.getType());
                return searchlist;

            }
            if (style==StaticMember.ADLIST_RED){
                List<AdRedBean> redlist = gson.fromJson(json,new TypeToken<List<AdRedBean>>(){}.getType());
                return redlist;
            }
            if (style==StaticMember.USERLIST){
                List userlist=new ArrayList();
                UserBean userBean=gson.fromJson(json,UserBean.class);
                userlist.add(userBean);
//            List<UserBean> userlist = (List<UserBean>) gson.fromJson(json,UserBean.class);
                return userlist;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}
