package com.example.administrator.goldaappnew.staticClass;


import com.example.administrator.goldaappnew.R;
import com.example.administrator.goldaappnew.bean.UserBean;

public class StaticMember {
    public static String EMAIL="golda_ad@126.com";//意见反馈邮箱
    public static String TEL_NUM="400-086-0056";//意见反馈电话
    public static String EMAIL_HOME="测试";//软件使用者
    public static String database_name = "golda_tracy.db";

    public static int HTTP_REQUEST_TIME_OUT = 15000;
    public static String use_permissions = "1"; // 使用权限标记

    public static int PORT = 80;
    // public static String city_short="cs";
    public static String city_short="yz";
    //public static String URL = "http://"+city_short+".jdjk.net/";
    public static String URL = "http://"+city_short+".jdjk.net/mob/";
    public static String ImageURL = "http://"+city_short+".jdjk.net/";
    /*ftp服务器信息*/
    public static String FTPServer = city_short+".jdjk.net";
    public static String FTPUser = "root";
    public static String FTPpsw = "golda888";
    public static String FTPport = "22";
    // public static String FTPRemotePath = "www/"+city_short+"/upload/";    //图片远程指定路径,'/'不可以省略
    public static String FTPRemotePath = "www/"+city_short+"/uploads/patrol/";	//图片远程指定路径,'/'不可以省略
    public static String RemotePath = "uploads/patrol/";	//图片远程指定路径,'/'不可以省略

    public static String WebUrl = URL + "gis_log.php";

    //sftp端口、用户名、密码等
    public static final String SFTP_REQ_HOST = city_short+".jdjk.net";
    public static final String SFTP_REQ_PORT = "22";
    public static final String SFTP_REQ_USERNAME = "root";
    public static final String SFTP_REQ_PASSWORD = "golda888";
    public static final int SFTP_DEFAULT_PORT = 22;
    public static final String SFTP_REQ_LOC = "location";


    public static final String APP_NAME = "golda";

    public static final String APP_PACKAGE_NAME = "com.example.administrator.goldaapp";

    public static final String DOWNLOAD_APK_URL = ImageURL+"public/apk/app-release.apk";


    public static final String ERROR_LOG_PATH = "crash/";

    public static final String RUN_LOG = "/"+APP_NAME +"/log/";

    public static final boolean LOG_FLAG = true;

    public static final boolean MYLOG_WRITE_SWITCH = true;

    public static boolean SHOW_UPLOAD_IMAGE = true; // 是否显示上传的图片

    public static UserBean USER = new UserBean();//用户信息

    public static final int NUM = 100;

    // 官网地址
    public static final String MyWebUrl = "http://cs.jdjk.net/forum.php?mobile=no";

    /*数据接口*/
    public static final int ADLIST_GREEN = 101;
    public static final int ADLIST_RED = 102;
    public static final int USERLIST = 103;
    public static final int SEARCH = 104;
    public static final int BOARD_LIST = 105;

    public static final int REQUEST_GREEN_DATA_RESULT = 106;
    public static final int REQUEST_RED_DATE_RESULT = 107;
    public static final int REQUEST_BOARD_LIST_DATE_RESULT = 108;

    public static final int LENGTH = 200;//当前位置与目标点的最远距离
    public static final int CLICK_LENGTH = 25;//点击marker显示周围x米的范围的marker
    public static final int RESULT_CAPTURE_IMAGE_1 = 2000;

    public static final int UPLOAD_IMAGE_RESULT = 2001; // 上传图片结果
    public static final int CHOOSE_IMAGE_RESULT = 2002; // 选择图片结果
    public static final int CHOOSE_IMAGE_TYPE = 2003;   // 选择图片
    public static final int PHOTO_IMAGE_TYPE = 2004;    // 拍照
    public static final int SAVE_SHENBAO_DATA = 2005;   // 申报数据保存

    public static final int CHOOSE_TYPE1 = 3000;
    public static final int CHOOSE_TYPE2 = 3001;
    public static final int CHOOSE_TYPE3 = 3002;


    public static String[] problemStr = { "无问题", "一般", "严重","非常严重" };
    //审批等级
    public static String[] checkStr = {"未知","省审批","市审批","区县审批","其他"};
    //广告牌类型

    //广告类型
    public static String[] type={"户外广告设施","店招牌匾设施"};

    //广告位置
    public static String[][] classe={{"建（构）筑物上的","地面上的","公共设施上的","移动物体上的","其它及临时性的"},{"建（构）筑物上的","地面上的","其它的"}};

    //广告种类
    public static String[][][] cnname={{{"常规屋顶广告","常规墙面广告","大型显示屏广告","中小型显示屏广告","霓虹灯广告","三面翻广告","复合式广告","常规墙面垂直式广告","悬挂式广告","窗户（橱窗）广告","亮化及媒体立面广告","桥体广告"},{"大型立柱式广告","大型落地广告","中小型落地广告","实物造型广告","导向标识广告","景观雕塑式广告","地面喷绘广告","装置广告","大型显示屏广告","中小型显示屏广告","霓虹灯广告","三面翻广告","复合式广告"},{"候车亭广告","报刊亭广告","遮阳棚广告"},{"车船广告","空中漂浮类广告"},{"围墙（挡）广告","挂旗广告","布幅条幅广告","充气广告","投影广告"}},{{"屋顶招牌","门头招牌","墙面招牌","墙面垂直式招牌","屋顶单体字","门头单体字","墙面附着单体字","品牌墙窗户（橱窗）招牌","悬挂式招牌"},{"跨街招牌","落地招牌"},{"传统匾额招幌","遮棚招牌"}}};

    //定位频率
    public static String[] local_space={"每隔1秒(默认)","每隔2秒","每隔5秒","每隔10秒","每隔30秒"};
    //刷新距离
    public static String[] refresh_length={"10m触发","20m触发","50m触发","150m触发(默认)","300m触发"};
    //常用的排在前面；
    public static String[] kind = {"门头店招", "悬挂小型箱(面)式广告", "落地小型箱(面)式广告", "大型立柱式广告",
            "墙体牌", "候车亭广告", "大型电子显示广告", "小型电子显示广告", "跨桥广告",
            "围挡广告", "橱窗广告", "墙面垂直广告", "落地大型面式广告", "楼顶大牌", "报亭广告",
            "电话亭广告", "挂旗广告", "垃圾箱广告", "遮阳广告", "霓虹灯", "指示牌广告", "三面翻广告",
            "实物造型广告", "亮化", "海报", "墙面涂层广告", "横幅", "电子显示与看板合二为一", "地面涂层广告",
            "充气广告", "空中气球广告", "车身广告"};

    public static String[] kindValue = {"small_door", "small_single", "big_single_board", "big_three_stand",
            "big_wall", "small_waittingroom", "big_led", "small_led", "big_bridge",
            "big_fence", "small_showwindow", "big_all_stand", "big_two_stand", "big_building", "small_pager",
            "small_phone", "small_falg", "small_dusbin", "small_shadow", "big_neon", "small_direction", "big_three_face",
            "small_alien_lamp", "small_neon", "small_poster", "big_wall_paint", "small_banner", "big_led_board", "small_land_paint",
            "small_gas", "small_balloon", "big_car"};

    public static int[] kind_drawable = {
            R.drawable.red_small_door, R.drawable.red_small_single, R.drawable.red_big_single_board, R.drawable.red_big_three_stand,
            R.drawable.red_big_wall, R.drawable.red_small_waittingroom, R.drawable.red_big_led, R.drawable.red_small_led,
            R.drawable.red_big_bridge, R.drawable.red_big_fence, R.drawable.red_small_showwindow, R.drawable.red_big_all_stand,
            R.drawable.red_big_two_stand, R.drawable.red_big_building, R.drawable.red_small_pager, R.drawable.red_small_phone,
            R.drawable.red_small_falg, R.drawable.red_small_dusbin, R.drawable.red_small_shadow, R.drawable.red_big_neon,
            R.drawable.red_small_direction, R.drawable.red_big_three_face, R.drawable.red_small_alien_lamp, R.drawable.red_small_neon,
            R.drawable.red_small_poster, R.drawable.red_big_wall_paint, R.drawable.red_small_banner, R.drawable.red_big_led_board,
            R.drawable.red_small_land_paint, R.drawable.red_small_gas, R.drawable.red_small_balloon, R.drawable.red_big_car};
}
