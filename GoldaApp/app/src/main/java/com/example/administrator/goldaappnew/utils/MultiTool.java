package com.example.administrator.goldaappnew.utils;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.Spinner;

public class MultiTool {
    /*加入时间戳*/
    @SuppressLint("WrongConstant")
    public static Bitmap createBitmap(String username, Bitmap src) {// 加入时间戳
        if (src == null) {
            return null;
        }

        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy年MM月dd日   HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
//		paint.setAntiAlias(true);
        paint.setTextSize(20);
        int w = src.getWidth();
        int h = src.getHeight();
        // 实例化一个空bitmap
        Bitmap newb = Bitmap.createBitmap(w, h, Config.RGB_565);// 创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(newb);
        // 写入内容
        cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
        cv.drawText(str+"    "+username, w - 350, h - 20, paint);
        cv.save(Canvas.CLIP_SAVE_FLAG);// 保存
        cv.restore();// 存储
        return newb;
    }

    /*计算两点之间距离*/
    public static double toDistance(double lat_a, double lng_a, double lat_b, double lng_b) {
        double EARTH_RADIUS = 6378137.0;
        double radLat1 = (lat_a * Math.PI / 180.0);
        double radLat2 = (lat_b * Math.PI / 180.0);
        double a = radLat1 - radLat2;
        double b = (lng_a - lng_b) * Math.PI / 180.0;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    /*获得平板编号*/
    public static String getSerialnum(){

        if(1==1)
        return "1";


        String serialnum = "";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class );
            serialnum = (String)(   get.invoke(c, "ro.serialno", "unknown" )  );

        }
        catch (Exception ignored)
        {
            return "获取平板编号失败";
        }
        return serialnum;
    }

    //根据icon名称，获取中文名称;或根据中文名称，获取icon名称
    public static String getADType(String icons) {
        if(null ==icons || "".equals(icons)){
            return "";
        }

        //常用的排在前面；
        String[] icon = {"店招牌匾设施","常规屋顶广告","常规墙面广告","大型显示屏广告","中小型显示屏广告",
                "霓虹灯广告","三面翻广告","复合式广告","常规墙面垂直式广告","悬挂式广告","窗户（橱窗）广告",
                "亮化及媒体立面广告","桥体广告","大型立柱式广告","大型落地广告","中小型落地广告",
                "实物造型广告","导向标识广告","景观雕塑式广告","地面喷绘广告","装置广告",
                "候车亭广告","报刊亭广告","遮阳棚广告","其他公共设施广告",
                "车船广告","空中漂浮类广告","围墙（挡）广告","挂旗广告","布幅条幅广告","充气广告","投影广告",};

        String[] iconValue = {"small_door","big_building","big_wall","big_led","small_led",
                "big_neon","big_three_face","big_led_board","big_all_stand","small_single","small_showwindow",
                "small_neon","big_bridge","big_three_stand","big_two_stand","big_single_board",
                "small_alien_lamp","small_direction","small_dusbin","small_land_paint","small_poster",
                "small_waittingroom","small_pager","small_shadow","small_phone",
                "big_car","small_balloon","big_fence","small_falg","small_banner","small_gas","big_wall_paint"};

//        String[] icon = {
//        		"电子显示与看板合二为一", "车身广告", "大型电子显示广告", "落地小型箱(面)式广告",
//        		"跨桥广告", "楼顶大牌", "霓虹灯", "墙面涂层广告",
//        		"墙面垂直广告", "墙体牌", "三面翻广告", "大型立柱式广告",
//        		"落地大型面式广告", "围挡广告", "报亭广告", "充气广告",
//        		"橱窗广告", "地面涂层广告", "电话亭广告", "悬挂小型箱(面)式广告",
//        		"挂旗广告", "海报", "横幅", "候车亭广告",
//        		"空中气球广告", "门头店招标识", "小型电子显示广告", "亮化",
//                "实物造型广告", "遮阳广告", "指示牌广告","垃圾箱广告"
//        };
//        String[] iconValue = { "big_led_board", "big_car", "big_led", "big_single_board", "big_bridge",
//                "big_building", "big_neon", "big_wall_paint", "big_all_stand", "big_wall", "big_three_face",
//                "big_three_stand", "big_two_stand", "big_fence", "small_pager", "small_gas",
//                "small_showwindow", "small_land_paint", "small_phone", "small_single", "small_falg",
//                "small_poster", "small_banner", "small_waittingroom", "small_balloon", "small_door",
//                "small_led", "small_neon", "small_alien_lamp", "small_shadow", "small_direction","small_dusbin" };
        for (int i = 0; i < icon.length; i++) {
            if (icons.equals(icon[i])) {
                icons = iconValue[i];
                break;
            } else if (icons.equals(iconValue[i])) {
                icons = icon[i];
                break;
            }
        }
        return icons;
    }

    //获取广告牌的index
    public static int getADTypeIndex(String icon) {
        int index = 0;
        String[] iconValue = {"small_door","small_single","big_single_board","big_three_stand",
                "big_wall","small_waittingroom","big_led","small_led","big_bridge",
                "big_fence","small_showwindow","big_all_stand","big_two_stand","big_building","small_pager",
                "small_phone","small_falg","small_dusbin","small_shadow","big_neon","small_direction","big_three_face",
                "small_alien_lamp","small_neon","small_poster","big_wall_paint","small_banner","big_led_board","small_land_paint",
                "small_gas","small_balloon","big_car"};
//        String[] iconValue = { "big_led_board", "big_car", "big_led", "big_single_board", "big_bridge",
//                "big_building", "big_neon", "big_wall_paint", "big_all_stand", "big_wall", "big_three_face",
//                "big_three_stand", "big_two_stand", "big_fence", "small_pager", "small_gas",
//                "small_showwindow", "small_land_paint", "small_phone", "small_single", "small_falg",
//                "small_poster", "small_banner", "small_waittingroom", "small_balloon", "small_door",
//                "small_led", "small_neon", "small_alien_lamp", "small_shadow", "small_direction","small_dusbin" };

        for (int i = 0; i < iconValue.length; i++) {
            if (icon.equals(iconValue[i])) {
                index = i;
                break;
            }
        }
        return index;
    }
    //获取问题类型的index
    public static int getLevelIndex(String level) {
        int index = 0;
        if("非常严重".equals(level)){
            index = 0;
        }else if("严重".equals(level)){
            index = 1;
        }else if("一般".equals(level)){
            index = 2;
        }else if("无问题".equals(level)){
            index = 3;
        }
        return index;
    }
    //获取审批级别的index
    public static int getCheckIndex(String level) {
        int index = 0;
        if("未知".equals(level)){
            index = 0;
        }else if("省审批".equals(level)){
            index = 1;
        }else if("市审批".equals(level)){
            index = 2;
        }else if("区县审批".equals(level)){
            index = 3;
        }else if("其他".equals(level)){
            index = 4;
        }
        return index;
    }

}
