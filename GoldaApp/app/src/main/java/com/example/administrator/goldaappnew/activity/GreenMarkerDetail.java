package com.example.administrator.goldaappnew.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.goldaappnew.R;
import com.example.administrator.goldaappnew.bean.AdGreenBean;
import com.example.administrator.goldaappnew.fragment.FragmentXuncha;
import com.example.administrator.goldaappnew.staticClass.StaticMember;
import com.example.administrator.goldaappnew.utils.AssistUtil;
import com.example.administrator.goldaappnew.utils.CommonTools;
import com.example.administrator.goldaappnew.utils.HttpTools;
import com.example.administrator.goldaappnew.utils.MultiTool;
import com.example.administrator.goldaappnew.utils.SFTPChannel;
import com.example.administrator.goldaappnew.view.PinchImageView;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/8/22.
 */

public class GreenMarkerDetail extends AppCompatActivity {
    @BindView(R.id.add_check)
    LinearLayout add_check;
    @BindView(R.id.add_check_level)
    LinearLayout add_check_level;
    @BindView(R.id.add_finder)
    LinearLayout add_finder;
    @BindView(R.id.tv_find)
    TextView tv_finder;
    @BindView(R.id.tv_question)
    TextView tv_question;
    @BindView(R.id.sp_level)
    Spinner sp_level;
    @BindView(R.id.tv_level)
    TextView tv_level;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_save)
    ImageView iv_save;
    @BindView(R.id.dateline)
    TextView tv_dateline;
    @BindView(R.id.iv_img)
    PinchImageView iv_image;
    @BindView(R.id.image_progress)
    ProgressBar image_progress;
    @BindView(R.id.tv_id)
    TextView tv_id;
    @BindView(R.id.tv_contact)
    TextView tv_contact;
    @BindView(R.id.tv_contact_phone)
    TextView tv_contact_phone;
    @BindView(R.id.tv_latlng)
    TextView tv_latlng;
    @BindView(R.id.tv_adress)
    TextView tv_adress;
    @BindView(R.id.tv_company)
    TextView tv_company;
    @BindView(R.id.tv_shortcompany)
    TextView tv_shortCompany;
    @BindView(R.id.tv_screen)
    TextView tv_screen;
    @BindView(R.id.tv_adid)
    TextView tv_adid;
    @BindView(R.id.tv_kind)
    TextView tv_kind_txt;
    @BindView(R.id.tv_belong)
    TextView tv_belong;

    // 保存层
    @BindView(R.id.add_save)
    RelativeLayout add_save;

    private ArrayAdapter<String> adapter_level;
    private File dir=new File(Environment.getExternalStorageDirectory(),"golda");//照片保存路径文件夹
    private String ImagefilePath =Environment.getExternalStorageDirectory()+"/golda/uploda.jpg";//照片路径
    private boolean toSave = true;//是否需要保存
    private boolean isphotoed = false;//是否拍照
    private AdGreenBean ad;
    private float accuracy = 0;//默认的定位精度
    private ProgressDialog mpDialog;
    private String result_ftp, result_http;
    private String getPhoto;//获取图片url
    private Bitmap img;//获取到的图片
    private String markerLat, markerLng;//经纬度
    private String today;//指定新拍摄的图片上传路径
    private static String TAG="GreenMarkerDetail";
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                iv_image.setImageBitmap(img);
                image_progress.setVisibility(View.GONE);
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_green);
        ButterKnife.bind(this);
        CommonTools.setStateBarColor(this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toSave) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GreenMarkerDetail.this);
                    builder.setTitle("提示");
                    builder.setMessage("是否保存修改?");
                    builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            saveData();
                        }
                    });
                    builder.show();
                } else
                    finish();
            }
        });
        initData();
    }
    private void initData(){
        //今天的日期，用于图片文件夹的区分
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        today=format.format(new Date());
        ad= (AdGreenBean) getIntent().getSerializableExtra("AdGreenBean");
        accuracy=getIntent().getFloatExtra("accuracy",0);//定位精度，默认为0
        if(ad!=null) {
            if (!"".equals(ad.getBoard_id())) {
                tv_id.setText(ad.getBoard_id());
//                iv_save.setVisibility(View.INVISIBLE);
                add_save.setVisibility(View.GONE);
                toSave = false;
            }
            Log.e(TAG, "initData: "+ad.getDateline() );
            tv_dateline.setText(CommonTools.timeStamp2Date(ad.getDateline(), "yyyy-MM-dd"));
            markerLng = ad.getGis_x();
            markerLat = ad.getGis_y();
            tv_latlng.setText(markerLng + " , " + markerLat);
            tv_adress.setText(ad.getAddress());
            tv_company.setText(ad.getCompany());
            tv_shortCompany.setText(ad.getShortname());
            if ("".equals(ad.getProcess_contact()))
                tv_contact.setText("未知");
            else
            tv_contact.setText(ad.getProcess_contact());
            if ("".equals(ad.getProcess_tel()))
                tv_contact_phone.setText("未知");
            else
            tv_contact_phone.setText(ad.getProcess_tel());
            tv_adid.setText(ad.getBd_sn());
            tv_screen.setText(ad.getIntro());
            tv_kind_txt.setText(ad.getIcon_cnname());
            tv_belong.setText(ad.getBelongto());

            //问题等级初始�?
            adapter_level = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, StaticMember.problemStr);
            adapter_level.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_level.setAdapter(adapter_level);

            sp_level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    tv_level.setText(sp_level.getSelectedItem().toString());
                    toSave = true;
//                    iv_save.setVisibility(View.VISIBLE);
                    add_save.setVisibility(View.VISIBLE);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            //已有图片显示
            if (ad.getB_attach_2() != null && ad.getB_attach_2() != "") {
                if (ad.getB_attach_2().contains(".png") || ad.getB_attach_2().contains(".jpg")) {
                    String[] pics = null;
                    if (ad.getB_attach_2().contains("|")) {
                        pics = ad.getB_attach_2().split("\\|");
                        for (int i = pics.length-1; i >-1; i--) {
                            Log.e("pic", pics[i]);
                            if (pics[i].contains(".jpg")||pics[i].contains(".png")) {
                                getPhoto =pics[i];
                                break;
                            }
                        }
                    } else {
                        getPhoto = ad.getB_attach_2();
                    }
                    getPhoto = StaticMember.ImageURL + getPhoto;
                    Log.e("图片加载的挖正路径", getPhoto);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            img = HttpTools.getUrlImage(getPhoto);
                            handler.sendEmptyMessage(1);
                        }
                    }).start();
                } else {
                    image_progress.setVisibility(View.INVISIBLE);
                }

            } else {
                image_progress.setVisibility(View.INVISIBLE);
            }
        }

    }
    private void saveData(){

        if (tv_adress.getText().toString().trim().equals("")){
            showSnackBar("地址不能为空");
            return;
        }
        if(null == FragmentXuncha.myLocationLL){
            showSnackBar("获取当前位置失败，请打开定位功能！");
            return;
        }
        Double lat = FragmentXuncha.myLocationLL.latitude;
        Double lon = FragmentXuncha.myLocationLL.longitude;
        if(null == lat || null == lon){
            showSnackBar("获取当前位置失败，请打开定位功能！");
            return;
        }

        int distance = (int) MultiTool.toDistance(FragmentXuncha.myLocationLL.latitude, FragmentXuncha
                .myLocationLL.longitude, Double.parseDouble(markerLat), Double.parseDouble(markerLng));
        if (distance > StaticMember.LENGTH + accuracy)
            showSnackBar("距离目标点过远，不允许上传数据！");
        if (!isphotoed) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("您尚未选择新图片，确定要保存吗?");
            builder.setTitle("提示");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    mpDialog = new ProgressDialog(GreenMarkerDetail.this);
                    mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
                    mpDialog.setMessage("正在上传数据,请勿关闭当前窗口");
                    mpDialog.setIndeterminate(false);// 设置进度条是否为不明?
                    mpDialog.setCancelable(true);// 设置进度条是否可以按?回键取消
                    mpDialog.show();
                    final Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                mpDialog.cancel();
                                if ("Error".equals(result_http)) {
                                    showSnackBar("上传失败，请检查网络环境！");
                                } else {
                                    Snackbar sn = Snackbar.make(toolbar, "上传成功", BaseTransientBottomBar.LENGTH_SHORT);
                                    sn.show();
                                    CommonTools.setSnackbarMessageTextColor(sn, getResources().getColor(R.color.orange));

//                                    Intent intent = new Intent(GreenMarkerDetail.this, MainFragmentActivity.class);
//                                    startActivityForResult(intent, 10010);//添加的请求code,自定义但是和mainActivity里相同
                                    GreenMarkerDetail.this.finish();
                                }
                            }
                        }
                    };
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
//                        String up_gis_pic = ad.getB_attach_2();
                        String up_finder = tv_finder.getText().toString().trim();
                        if (null == up_finder || "".equals(up_finder)) {
                            up_finder = StaticMember.USER.getRealname().trim();
                        }
                        AdGreenBean green = new AdGreenBean();
                        green.setBelongto(tv_belong.getText().toString().trim());
                        green.setProvince(ad.getProvince());
                        green.setCity(ad.getCity());
                        green.setArea(ad.getArea());
                        green.setAddress(tv_adress.getText().toString().trim());
                        green.setCompany(tv_company.getText().toString().trim());
                        green.setShortname(tv_shortCompany.getText().toString().trim());
                        green.setIntro(tv_screen.getText().toString().trim());
                        green.setBd_sn(tv_adid.getText().toString().trim());
                        green.setGis_x(markerLng);
                        green.setGis_y(markerLat);
//                        green.setGis_pic(up_gis_pic);
                        green.setBoard_id(ad.getBoard_id());
//                        green.setIcon(ad.getIcon().toString());
                        green.setProcess_contact(tv_contact.getText().toString().trim());
                        green.setProcess_tel(tv_contact_phone.getText().toString().trim());
                        green.setIcon_type(ad.getIcon_type().toString().trim());
                        green.setIcon_class(ad.getIcon_class().toString().trim());
                        green.setIcon_cnname(tv_kind_txt.getText().toString());
                        green.setStatus(ad.getStatus().toString());
                        green.setDateline(tv_dateline.getText().toString().trim());
//                        green.setLid(tv_id.getText().toString());
//                        green.setXid("" + (int) (Math.random() * 1000000000)); // 随机数
                        green.setUid(StaticMember.USER.getUid());
                        green.setPeople_find(up_finder);
                        green.setLevel(tv_level.getText().toString().trim());
                        green.setQuestion(tv_question.getText().toString().trim());

                        Gson gson = new Gson();
                        String dataStr = gson.toJson(green);
                        // result_http = HttpTools.getPostMsg(StaticMember.URL + "mob_log.php", dataStr);
//                        Log.e("","##11 green dataStr ="+dataStr);
                        result_http = HttpTools.sendPostRequest(StaticMember.URL + "mob_log.php", dataStr);
//                        Log.i("","11修改绿标结果："+result_http);

                        handler.sendEmptyMessageDelayed(1, 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
                }
            });
            builder.setNegativeButton("返回拍照", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    return;
                }
            });
            builder.create().show();
        }else {
            mpDialog = new ProgressDialog(GreenMarkerDetail.this);
            mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
            mpDialog.setMessage("正在上传数据,请勿关闭当前窗口");
            mpDialog.setIndeterminate(false);// 设置进度条是否为不明确
            mpDialog.setCancelable(true);// 设置进度条是否可以按退回键取消
            mpDialog.show();

            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 1) {
                        mpDialog.cancel();
                        if ("Error".equals(result_http)) {
                            showSnackBar("上传失败，请检查网络环境！");
                        } else {
                            Snackbar sn = Snackbar.make(toolbar, "上传成功", BaseTransientBottomBar.LENGTH_SHORT);
                            sn.show();
                            CommonTools.setSnackbarMessageTextColor(sn, getResources().getColor(R.color.orange));

//                            Intent intent = new Intent(GreenMarkerDetail.this, MainFragmentActivity.class);
//                            startActivityForResult(intent, 10010);//添加的请求code,自定义但是和mainActivity里相同
                            GreenMarkerDetail.this.finish();
                        }
                    }
                }
            };

            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {

                        String up_finder = tv_finder.getText().toString().trim();
                        if (null == up_finder || "".equals(up_finder)) {
                            up_finder = StaticMember.USER.getRealname().trim();
                        }
                        AdGreenBean green = new AdGreenBean();
                        green.setBelongto(tv_belong.getText().toString().trim());
                        green.setProvince(ad.getProvince());
                        green.setCity(ad.getCity());
                        green.setArea(ad.getArea());
                        green.setAddress(tv_adress.getText().toString().trim());
                        green.setCompany(tv_company.getText().toString().trim());
                        green.setShortname(tv_shortCompany.getText().toString().trim());
                        green.setIntro(tv_screen.getText().toString().trim());
                        green.setBd_sn(tv_adid.getText().toString().trim());
                        green.setGis_x(markerLng);
                        green.setGis_y(markerLat);

                        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                        String str = formatter.format(new Date()).trim();
                        //图片命名法则：当前时�?+10亿分之一的随机数
                        String up_gis_pic =  str + "_" + (int) (Math.random() * 1000000000) + ".jpg";
                        green.setGis_pic(today + "/" +  up_gis_pic);

                        green.setBoard_id(ad.getBoard_id());
//                        green.setIcon(ad.getIcon().toString());
                        green.setProcess_contact(tv_contact.getText().toString().trim());
                        green.setProcess_tel(tv_contact_phone.getText().toString().trim());
                        green.setIcon_type(ad.getIcon_type().toString().trim());
                        green.setIcon_class(ad.getIcon_class().toString().trim());
                        green.setIcon_cnname(tv_kind_txt.getText().toString());
                        green.setStatus(ad.getStatus().toString());
                        green.setDateline(tv_dateline.getText().toString().trim());
//                        green.setLid(tv_id.getText().toString());
//                        green.setXid("" + (int) (Math.random() * 1000000000)); // 随机数
                        green.setUid(StaticMember.USER.getUid());
                        green.setPeople_find(up_finder);
                        green.setLevel(tv_level.getText().toString().trim());
                        green.setQuestion(tv_question.getText().toString().trim());

                        Gson gson = new Gson();
                        String dataStr = gson.toJson(green);
//                        Log.e("","## green dataStr ="+dataStr);
                        result_ftp = SFTPChannel.getChannel(ImagefilePath, StaticMember.FTPRemotePath + today, up_gis_pic, 1000);
                        if (result_ftp == "1") {
                            // result_http = HttpTools.getPostMsg(StaticMember.URL + "mob_log.php", parms);
                            result_http = HttpTools.sendPostRequest(StaticMember.URL + "mob_log.php", dataStr);
                        }
                        Log.e("上传返回的结果", "\t result_ftp=" + result_ftp + "\t result_http=" + result_http);

                        handler.sendEmptyMessageDelayed(1, 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }
    @OnClick({R.id.iv_camera, R.id.iv_save, R.id.tv_contact_phone, R.id.iv_latlng, R.id.iv_address,
            R.id.iv_company, R.id.iv_shortcompany, R.id.iv_screen, R.id.iv_adid, R.id.iv_find,
            R.id.iv_question, R.id.add_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_camera:
                setPermissions();
                break;
            case R.id.add_save:
                saveData();
                break;
            case R.id.iv_save:
                saveData();
                break;
            case R.id.tv_contact_phone:
                setPermissions2Phone();
                break;
            case R.id.iv_find:
                showInputDialog(tv_finder);
                break;
            case R.id.iv_question:
                showInputDialog(tv_question);
                break;
            case R.id.iv_latlng:
                Intent i = new Intent(this, ModifyLatLngActivity.class);
                Log.e("修改的Marker坐标�?", ad.getGis_x() + "****" + ad.getGis_y());
                i.putExtra("lat", Double.parseDouble(ad.getGis_y()));
                i.putExtra("lng", Double.parseDouble(ad.getGis_x()));
                startActivityForResult(i, 1003);
                break;
            case R.id.iv_address:
                showInputDialog(tv_adress);
                break;
            case R.id.iv_company:
                showInputDialog(tv_company);
                break;
            case R.id.iv_shortcompany:
                showInputDialog(tv_shortCompany);
                break;
            case R.id.iv_screen:
                showInputDialog(tv_screen);
                break;
            case R.id.iv_adid:
                showInputDialog(tv_adid);
                break;
            default:
                break;
        }
    }
    public void showInputDialog(final TextView textView) {
        AlertDialog viewdialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改信息");
        final LinearLayout input = (LinearLayout) getLayoutInflater().inflate(R.layout.input_layout, null);
        final EditText editText = (EditText) input.findViewById(R.id.edit);
        builder.setView(input);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textView.setText(editText.getText());
//                iv_save.setVisibility(View.VISIBLE);
                add_save.setVisibility(View.VISIBLE);
                add_check.setVisibility(View.VISIBLE);
                add_check_level.setVisibility(View.VISIBLE);
                add_finder.setVisibility(View.VISIBLE);
                toSave = true;
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        viewdialog = builder.create();
        viewdialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001&& resultCode==RESULT_OK) {
            // 检查SDCard是否可用
            if(!AssistUtil.ExistSDCard()){
                Log.i(TAG, "SD card 不可用！");
                isphotoed = false;
                Toast.makeText(GreenMarkerDetail.this, "SD卡不可用！", Toast.LENGTH_SHORT).show();
                return;
            }

            /** 使用ImageLoader 控件加载本地图片-20180721修改 -------------------- begin  */
//            //原图
//            Log.e(TAG, "onActivityResult: "+ImagefilePath );
//            Bitmap bitmap = BitmapFactory.decodeFile(ImagefilePath);
//            compressImage(bitmap,ImagefilePath);
//            iv_image.setImageBitmap(bitmap);

            FileInputStream fis = null;
            try {
                fis = new FileInputStream(ImagefilePath);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            BitmapFactory.Options opts=new BitmapFactory.Options();
            opts.inTempStorage = new byte[100 * 1024];
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            opts.inPurgeable = true;
            opts.inSampleSize = 4;
            opts.inInputShareable = true;
            Bitmap bitmap = BitmapFactory.decodeStream(fis,null, opts);
//            if(null != bitmap){
//                Log.i("","## 压缩前图片大小："+CaremaUtil.getBitmapSize(bitmap));
//            }
            try {
                if(null != fis){
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap newPhoto = compressImage(bitmap,ImagefilePath);
//            if(null != newPhoto){
//                Log.i("","## 压缩后图片大小："+CaremaUtil.getBitmapSize(newPhoto));
//            }
            iv_image.setImageBitmap(newPhoto);
            image_progress.setVisibility(View.GONE);
            /** 使用ImageLoader 控件加载本地图片-20180721修改 -------------------- end */

            toSave = true;
            isphotoed = true;
//                iv_save.setVisibility(View.VISIBLE);
            add_save.setVisibility(View.VISIBLE);
            add_check.setVisibility(View.VISIBLE);
            add_check_level.setVisibility(View.VISIBLE);
            add_finder.setVisibility(View.VISIBLE);

        }
        if (requestCode == 1003) {
            if (data != null) {
                DecimalFormat df = new DecimalFormat("#.000000");
                markerLat = df.format(data.getDoubleExtra("lat", 0.0));
                markerLng = df.format(data.getDoubleExtra("lng", 0.0));
                Log.i("Mod+++++++", "setMapOverlay: 经度" + markerLng + "纬度" + markerLat);
                tv_latlng.setText(markerLng + " , " + markerLat);
//                iv_save.setVisibility(View.VISIBLE);
                add_save.setVisibility(View.VISIBLE);
                add_check.setVisibility(View.VISIBLE);
                add_check_level.setVisibility(View.VISIBLE);
                add_finder.setVisibility(View.VISIBLE);
                toSave = true;
            }
        }
    }


    /**
     * 设置Android6.0的权限申请
     */
    private void setPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(GreenMarkerDetail.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //Android 6.0申请权限
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA} ,1);
            }else{
                Log.i(TAG,"权限申请ok");
                takePhoto();
            }
        }else {
            takePhoto();
        }
    }
    private void setPermissions2Phone() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(GreenMarkerDetail.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                //Android 6.0申请权限
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE} ,2);
            }else{
                Log.i(TAG,"权限申请ok");
                callPhone();
            }
        }else {
            callPhone();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1){
            if (grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                takePhoto();
            }else {
            }
        }
        if (requestCode==2){
            if (grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                callPhone();
            }else {
            }
        }
    }
    private void callPhone()
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:"+tv_contact_phone.toString().trim()));
        startActivity(intent);
    }
    public void takePhoto(){
        if (!dir.exists()) {
            dir.mkdirs();
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式在android中，创建文件时，文件名中不能包含“：”冒号
        String filename = df.format(new Date());
        File currentImageFile  = new File(dir, filename + ".jpg");
        if (!currentImageFile .exists()){
            try {
                currentImageFile .createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ImagefilePath=currentImageFile.getAbsolutePath();//获取图片的觉得路径
        Log.e(TAG, "takePhoto: "+ImagefilePath );
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 指定调用相机拍照后的照片存储的路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentImageFile));
        startActivityForResult(intent, 1001);
    }

    /**
     * 展示SnackBar
     */
    public void showSnackBar(String message) {
        //去掉虚拟按键
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION //隐藏虚拟按键�?
                | View.SYSTEM_UI_FLAG_IMMERSIVE //防止点击屏幕�?,隐藏虚拟按键栏又弹了出来
        );
        final Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), message, Snackbar.LENGTH_INDEFINITE);
        CommonTools.setSnackbarMessageTextColor(snackbar, getResources().getColor(R.color.orange));

        snackbar.setAction("知道了", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                //隐藏SnackBar时记得恢复隐藏虚拟按键栏,不然屏幕底部会多出一块空白
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }).show();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (toSave) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GreenMarkerDetail.this);
                builder.setTitle("提示");
                builder.setMessage("修改尚未保存，是否保存？");
                builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveData();
                    }
                });
                builder.show();
            } else
                finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    // 质量压缩法：
    private Bitmap compressImage(Bitmap image, String filepath) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            while (baos.toByteArray().length / 1024 > 500) {    //循环判断如果压缩后图片是否大于500k,大于继续压缩
                baos.reset();//重置baos即清空baos
                options -= 10;//每次都减少10
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中

            }
            Log.e(TAG, "compressImage: "+baos.toByteArray().length );
            //压缩好后写入文件中
            FileOutputStream fos = new FileOutputStream(filepath);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
