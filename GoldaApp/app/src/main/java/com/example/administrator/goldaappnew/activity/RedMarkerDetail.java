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
import com.example.administrator.goldaappnew.bean.AdRedBean;
import com.example.administrator.goldaappnew.fragment.FragmentXuncha;
import com.example.administrator.goldaappnew.staticClass.StaticMember;
import com.example.administrator.goldaappnew.utils.AssistUtil;
import com.example.administrator.goldaappnew.utils.CommonTools;
import com.example.administrator.goldaappnew.utils.HttpTools;
import com.example.administrator.goldaappnew.utils.MultiTool;
import com.example.administrator.goldaappnew.utils.PhotoBitmapUtils;
import com.example.administrator.goldaappnew.utils.SFTPChannel;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/8/22.
 */

public class RedMarkerDetail extends AppCompatActivity {
    @BindView(R.id.iv_save)
    ImageView iv_save;
    @BindView(R.id.iv_img)
    ImageView iv_image;
    @BindView(R.id.iv_latlng)
    ImageView iv_latlng;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.image_progress)
    ProgressBar image_progress;
    @BindView(R.id.type)
    Spinner sp_type;
    @BindView(R.id.classe)
    Spinner sp_classe;
    @BindView(R.id.kind)
    Spinner sp_kind;
    @BindView(R.id.sp_level)
    Spinner sp_level;
    @BindView(R.id.sp_belong)
    Spinner sp_belong;
    @BindView(R.id.dateline)
    TextView tv_dateline;
    @BindView(R.id.tv_id)
    TextView tv_id;
    @BindView(R.id.tv_latlng)
    TextView tv_latlng;
    @BindView(R.id.tv_adress)
    TextView tv_adress;
    @BindView(R.id.tv_find)
    TextView tv_find;
    @BindView(R.id.tv_company)
    TextView tv_company;
    @BindView(R.id.tv_shortcompany)
    TextView tv_shortCompany;
    @BindView(R.id.tv_screen)
    TextView tv_screen;
    @BindView(R.id.tv_adid)
    TextView tv_adid;
    @BindView(R.id.tv_level)
    TextView tv_level;
    @BindView(R.id.tv_belong)
    TextView tv_belong;
    @BindView(R.id.tv_question)
    TextView tv_question;
    @BindView(R.id.tv_contact)
    TextView tv_contact;
    @BindView(R.id.tv_contact_phone)
    TextView tv_contact_phone;
    @BindView(R.id.type_txt)
    TextView tv_type_txt;
    @BindView(R.id.classe_txt)
    TextView tv_classe_txt;
    @BindView(R.id.kind_txt)
    TextView tv_kind_txt;

    // 保存层
    @BindView(R.id.add_save)
    RelativeLayout add_save;

    private ArrayAdapter<String> adapter_level, adapter_check, adapter_type, adapter_classe, adapter_kind;
    int sp_count = 0; //设置spinner刚刚进来时默认
    int index; //spinner联动
    private File dir = new File(Environment.getExternalStorageDirectory(),"golda");//照片保存路径文件夹
    private String ImagefilePath = Environment.getExternalStorageDirectory()+"/golda/uploda.jpg";//照片路径
    private boolean toSave = true;//是否需要保存
    private boolean isphotoed = false;//是否拍照
    private AdRedBean ad;
    private float accuracy = 0;//默认的定位精度
    private ProgressDialog mpDialog;
    private String result_ftp, result_http;
    private String getPhoto;//获取图片url
    private Bitmap img;//获取到的图片
    private String markerLat, markerLng;//经纬度
    private String today;//指定新拍摄的图片上传路径
    private static String TAG="RedMarkDetail";

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
        setContentView(R.layout.activity_red);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(RedMarkerDetail.this);
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

    private void initData() {
        //今天的日期，用于图片文件夹的区分
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        today = format.format(new Date());//今天的日期
        ad = (AdRedBean) getIntent().getSerializableExtra("AdRedBean");
        accuracy = getIntent().getFloatExtra("accuracy", 10);//定位精度  默认�?10
        if (ad.getLid() != null && !ad.getLid().equals("")) {
            tv_id.setText(ad.getLid());
//            iv_save.setVisibility(View.INVISIBLE);
            add_save.setVisibility(View.GONE);
            toSave = false;
        }
        if ("系统自动生成".equals(tv_id.getText().toString())) {
            iv_latlng.setVisibility(View.GONE);
        }

        if (ad != null) {
            tv_dateline.setText(CommonTools.timeStamp2Date(ad.getDateline(), "yyyy-MM-dd"));
            markerLng = ad.getGis_x();
            markerLat = ad.getGis_y();

            tv_latlng.setText(markerLng + " , " + markerLat);
            tv_adress.setText(ad.getAddress());
            tv_find.setText(ad.getPeople_find());
            tv_adid.setText(ad.getBd_sn());
            tv_company.setText(ad.getCompany());
            tv_shortCompany.setText(ad.getShortname());
            tv_contact.setText(ad.getProcess_contact());
            tv_contact_phone.setText(ad.getProcess_tel());
            tv_question.setText(ad.getQuestion());
            tv_screen.setText(ad.getIntro());
            tv_belong.setText(ad.getBelongto());
            tv_level.setText(ad.getLevel());
            tv_type_txt.setText(ad.getIcon_type());
            tv_classe_txt.setText(ad.getIcon_class());
            tv_kind_txt.setText(ad.getIcon_cnname());

            Log.e("图片加载的正路径", "####ad.getGis_pic()="+ad.getGis_pic());

            //已有图片显示
            if (ad.getGis_pic() != null && ad.getGis_pic() != "") {
                if (ad.getGis_pic().contains(".png") || ad.getGis_pic().contains(".jpg")) {
                    String[] pics = null;
                    if (ad.getGis_pic().contains("|")) {
                        pics = ad.getGis_pic().split("\\|");
                        for (int i = pics.length-1; i >-1; i--) {
                            Log.e("for loop pic===", pics[i]);
                            if (pics[i].contains(".jpg")||pics[i].contains(".png")) {
                                getPhoto =pics[i];
                                break;
                            }
                        }
                    } else {
                        getPhoto = ad.getGis_pic();
                    }
                    getPhoto = StaticMember.ImageURL + getPhoto;

                    Log.i("","#### getPhoto =="+getPhoto);
//                    ImageLoader.getInstance().displayImage(getPhoto, iv_image);
//                    handler.sendEmptyMessage(1);

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
        //审批级别初始�?
        adapter_check = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, StaticMember.checkStr);
        adapter_check.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_belong.setAdapter(adapter_check);

        //问题等级初始�?
        adapter_level = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, StaticMember.problemStr);
        adapter_level.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_level.setAdapter(adapter_level);

        //广告种类
        adapter_type = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, StaticMember.type);
        adapter_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_type.setAdapter(adapter_type);

        //广告位置
        adapter_classe = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, StaticMember.classe[0]);
        adapter_classe.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_classe.setAdapter(adapter_classe);

        //广告类型
        adapter_kind = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, StaticMember.cnname[0][0]);
        adapter_kind.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_kind.setAdapter(adapter_kind);

        sp_belong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (sp_count < 5) {
                    sp_count++;
                    return;
                }
                tv_belong.setText(sp_belong.getSelectedItem().toString());
                toSave = true;
//                iv_save.setVisibility(View.VISIBLE);
                add_save.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        sp_level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (sp_count < 5) {
                    sp_count++;
                    return;
                }
                tv_level.setText(sp_level.getSelectedItem().toString());
                toSave = true;
//                iv_save.setVisibility(View.VISIBLE);
                add_save.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (sp_count < 5) {
                    sp_count++;
                    return;
                }
                adapter_classe = new ArrayAdapter<String>(RedMarkerDetail.this, android.R.layout.simple_spinner_item, StaticMember.classe[i]);
                adapter_classe.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_classe.setAdapter(adapter_classe);
                index = i;
                tv_type_txt.setText(sp_type.getSelectedItem().toString());
                toSave = true;
//                iv_save.setVisibility(View.VISIBLE);
                add_save.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_classe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (sp_count < 5) {
                    sp_count++;
                    return;
                }
                adapter_kind = new ArrayAdapter<String>(RedMarkerDetail.this, android.R.layout.simple_spinner_item, StaticMember.cnname[index][i]);
                adapter_kind.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_kind.setAdapter(adapter_kind);
                tv_classe_txt.setText(sp_classe.getSelectedItem().toString());
                toSave = true;
//                iv_save.setVisibility(View.VISIBLE);
                add_save.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_kind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (sp_count < 5) {
                    sp_count++;
                    return;
                }
                tv_kind_txt.setText(sp_kind.getSelectedItem().toString());
                toSave = true;
//                iv_save.setVisibility(View.VISIBLE);
                add_save.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @OnClick({R.id.iv_save, R.id.iv_shortcompany, R.id.iv_screen, R.id.iv_question,
            R.id.iv_adid, R.id.iv_camera, R.id.iv_latlng, R.id.iv_address, R.id.iv_company,
            R.id.iv_find, R.id.iv_contact, R.id.iv_contact_phone, R.id.add_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_save:
                saveData();
                break;
            case R.id.iv_save:
                saveData();
                break;
            case R.id.iv_camera:
                setPermissions();
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
            case R.id.iv_find:
                showInputDialog(tv_find);
                break;
            case R.id.iv_contact:
                showInputDialog(tv_contact);
                break;
            case R.id.iv_contact_phone:
                showInputDialog(tv_contact_phone);
                break;
            case R.id.iv_adid:
                showInputDialog(tv_adid);
                break;
            case R.id.iv_question:
                showInputDialog(tv_question);
                break;
            case R.id.iv_screen:
                showInputDialog(tv_screen);
                break;
            case R.id.iv_shortcompany:
                showInputDialog(tv_shortCompany);
                break;
            default:
                break;

        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode==RESULT_OK) {
            // 检查SDCard是否可用
            if(!AssistUtil.ExistSDCard()){
                Log.i(TAG, "SD card 不可用！");
                isphotoed = false;
                Toast.makeText(RedMarkerDetail.this, "SD卡不可用！", Toast.LENGTH_SHORT).show();
                return;
            }
//            Log.i("","#### ImagefilePath == "+ImagefilePath);
//            MyLogger.Log().i("#### ImagefilePath == "+ImagefilePath);
//            String tmpImageFilePath = ImagefilePath.trim();
//            if(!ImagefilePath.contains("file:///")){
//                tmpImageFilePath = "file:///"+ImagefilePath.trim();
//            }

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
            try {
                if(null != fis){
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
//            MyLogger.Log().i("## 111压缩前图片大小："+ CaremaUtil.getBitmapSize(bitmap));
            Bitmap newPhoto = compressImage(bitmap,ImagefilePath);
//            MyLogger.Log().i("## 222压缩后图片大小："+ CaremaUtil.getBitmapSize(newPhoto));
            iv_image.setImageBitmap(newPhoto);
            image_progress.setVisibility(View.GONE);
            toSave = true;
            isphotoed = true;
            add_save.setVisibility(View.VISIBLE);
            return;
        }
        if (requestCode == 1003) {
            if (data != null) {
                markerLat = data.getStringExtra("lat");
                markerLng = data.getStringExtra("lng");
                Log.i("Mod+++++++", "setMapOverlay: 经度" + markerLng + "纬度" + markerLat);
                tv_latlng.setText(markerLng + " , " + markerLat);
//                iv_save.setVisibility(View.VISIBLE);
                add_save.setVisibility(View.VISIBLE);
                toSave = true;
            }
        }
    }
    /**
     * 设置Android6.0的权限申请
     */
    private void setPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(RedMarkerDetail.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1){
            if (grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                takePhoto();
            }else {
                return;
            }
        }
    }

    public void takePhoto(){
        if (!dir.exists()) {
            dir.mkdirs();
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");//设置日期格式在android中，创建文件时，文件名中不能包含“：”冒号
        String filename = df.format(new Date());
        File currentImageFile  = new File(dir, filename + ".jpg");
        if (!currentImageFile .exists()){
            try {
                currentImageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ImagefilePath = currentImageFile.getAbsolutePath();//获取图片的绝对路径
        Log.e(TAG, "takePhoto: "+ImagefilePath );
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 指定调用相机拍照后的照片存储的路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentImageFile));
        startActivityForResult(intent, 1001);
    }

    public void takePhoto2(){

        ImagefilePath = PhotoBitmapUtils.getPhotoFileName(RedMarkerDetail.this); //获取图片的绝对路径
        Log.e(TAG, "takePhoto: "+ImagefilePath );
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(ImagefilePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, 1001);
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

    private void saveData() {
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

        int distance = (int) MultiTool.toDistance(FragmentXuncha.myLocationLL.latitude, FragmentXuncha.myLocationLL.longitude,
                Double.parseDouble(markerLat), Double.parseDouble(markerLng));
        if (distance > StaticMember.LENGTH + accuracy)
            showSnackBar("距离目标点过远，不允许上传数据！");
        if (tv_level.getText().toString().equals(""))
            tv_level.setText("无问题");
        if (tv_belong.getText().toString().equals(""))
            tv_belong.setText("未知");
        if (tv_type_txt.getText().toString().equals(""))
            tv_type_txt.setText("户外广告设施");
        if (tv_classe_txt.getText().toString().equals(""))
            tv_classe_txt.setText("建（构）筑物上的");
        if (tv_kind_txt.getText().toString().equals(""))
            tv_kind_txt.setText("常规屋顶广告");
        if (!isphotoed) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("您尚未选择新图片，确定要保存吗?");
            builder.setTitle("提示");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    mpDialog = new ProgressDialog(RedMarkerDetail.this);
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
//                                    Intent intent = new Intent(RedMarkerDetail.this, MainFragmentActivity.class);
//                                    startActivityForResult(intent, 10010);//添加的请求code,自定义但是和mainActivity里相同
                                    RedMarkerDetail.this.finish();
                                }
                            }
                        }
                    };
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                /**
                                 * 应后端接口要求，xid 默认1
                                 */
                                // String up_xid = "1";//  URLEncoder.encode("" + (int) (Math.random() * 1000000000), "utf-8"); //随机生成

                                AdRedBean adRedBean = new AdRedBean();
                                adRedBean.setLid(tv_id.getText().toString().trim());
                                adRedBean.setUid(StaticMember.USER.getUid());
                                // adRedBean.setXid(up_xid);
                                adRedBean.setBelongto(tv_belong.getText().toString().trim());
                                adRedBean.setProvince(ad.getProvince());
                                adRedBean.setCity(ad.getProvince());
                                adRedBean.setArea(ad.getArea());
                                adRedBean.setAddress(tv_adress.getText().toString().trim());
                                adRedBean.setCompany(tv_company.getText().toString().trim());
                                adRedBean.setShortname(tv_shortCompany.getText().toString().trim());
                                adRedBean.setIntro(tv_screen.getText().toString().trim());
                                adRedBean.setBd_sn(tv_adid.getText().toString().trim());
                                adRedBean.setGis_x(markerLng);
                                adRedBean.setGis_y(markerLat);
                                String up_gis_pic = "";
                                adRedBean.setGis_pic(up_gis_pic);
                                adRedBean.setQuestion(tv_question.getText().toString().trim());
                                String up_finder = tv_find.getText().toString().trim();
                                if (null == up_finder || "".equals(up_finder)) {
                                    up_finder = StaticMember.USER.getRealname().trim();
                                }
                                adRedBean.setPeople_find(up_finder);
                                adRedBean.setLevel(tv_level.getText().toString().trim());
//                                String up_icons = "";
//                                if ((tv_type_txt.getText().toString()).equals("店招牌匾设施")) {
//                                    up_icons = "small_door";
//                                } else {
//                                    up_icons = MultiTool.getADType(tv_kind_txt.getText().toString().trim());
//                                }
//                                Log.i("icon", "run: " + up_icons);
//                                adRedBean.setIcon(up_icons);

                                adRedBean.setProcess_contact(tv_contact.getText().toString().trim());
                                adRedBean.setProcess_tel(tv_contact_phone.getText().toString().trim());
                                adRedBean.setIcon_type(tv_type_txt.getText().toString().trim());
                                adRedBean.setIcon_class(tv_classe_txt.getText().toString().trim());
                                adRedBean.setIcon_cnname(tv_kind_txt.getText().toString().trim());
                                Log.i("up_uid", " StaticMember.USER.getUid()="+StaticMember.USER.getUid());

                                Gson gson = new Gson();
                                String dataStr = gson.toJson(adRedBean);
                                Log.i("dataStr", " #### dataStr="+dataStr);
                                result_http = HttpTools.sendPostRequest(StaticMember.URL + "mob_log.php", dataStr);

                                Log.i("222上传返回的结果", " #### result_http="+result_http);
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
        } else {
            mpDialog = new ProgressDialog(RedMarkerDetail.this);
            mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
            mpDialog.setMessage("正在上传数据,请勿关闭当前窗口");
            mpDialog.setIndeterminate(false);// 设置进度条是否为不明�?
            mpDialog.setCancelable(true);// 设置进度条是否可以按�?回键取消
            mpDialog.show();
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 1) {
                        mpDialog.cancel();
                        if ("Error".equals(result_http) || "0".equals(result_ftp)) {
                            showSnackBar("上传失败，请检查网络环境！");
                        } else {
                            Snackbar sn = Snackbar.make(toolbar, "上传成功", BaseTransientBottomBar.LENGTH_SHORT);
                            sn.show();
                            CommonTools.setSnackbarMessageTextColor(sn, getResources().getColor(R.color.orange));
//                            Intent intent = new Intent(RedMarkerDetail.this, MainFragmentActivity.class);
//                            startActivityForResult(intent, 10010);//添加的请求code,自定义但是和mainActivity里相同
                            RedMarkerDetail.this.finish();
                        }
                    }

                }
            };
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                        String str = formatter.format(new Date()).trim();
                        //图片命名法则：当前时�?+10亿分之一的随机数
                        String up_gis_pic = str + "_" + (int) (Math.random() * 1000000000) + ".jpg";
                        // String up_xid = "1";//  URLEncoder.encode("" + (int) (Math.random() * 1000000000), "utf-8"); //随机生成
                        AdRedBean adRedBean = new AdRedBean();
                        adRedBean.setLid(tv_id.getText().toString().trim());
                        adRedBean.setUid(StaticMember.USER.getUid());
                        // adRedBean.setXid(up_xid);
                        adRedBean.setBelongto(tv_belong.getText().toString().trim());
                        adRedBean.setProvince(ad.getProvince());
                        adRedBean.setCity(ad.getProvince());
                        adRedBean.setArea(ad.getArea());
                        adRedBean.setAddress(tv_adress.getText().toString().trim());
                        adRedBean.setCompany(tv_company.getText().toString().trim());
                        adRedBean.setShortname(tv_shortCompany.getText().toString().trim());
                        adRedBean.setIntro(tv_screen.getText().toString().trim());
                        adRedBean.setBd_sn(tv_adid.getText().toString().trim());
                        adRedBean.setGis_x(markerLng);
                        adRedBean.setGis_y(markerLat);
                        adRedBean.setGis_pic(today + "/" +  up_gis_pic);
                        adRedBean.setQuestion(tv_question.getText().toString().trim());
                        String up_finder = tv_find.getText().toString().trim();
                        if (null == up_finder || "".equals(up_finder)) {
                            up_finder = StaticMember.USER.getRealname().trim();
                        }
                        adRedBean.setPeople_find(up_finder);
                        adRedBean.setLevel(tv_level.getText().toString().trim());
//                        String up_icons = "";
//                        if ((tv_type_txt.getText().toString()).equals("店招牌匾设施")) {
//                            up_icons = "small_door";
//                        } else {
//                            up_icons = MultiTool.getADType(tv_kind_txt.getText().toString().trim());
//                        }
//                        Log.i("icon", "run: " + up_icons);
//                        adRedBean.setIcon(up_icons);

                        adRedBean.setProcess_contact(tv_contact.getText().toString().trim());
                        adRedBean.setProcess_tel(tv_contact_phone.getText().toString().trim());
                        adRedBean.setIcon_type(tv_type_txt.getText().toString().trim());
                        adRedBean.setIcon_class(tv_classe_txt.getText().toString().trim());
                        adRedBean.setIcon_cnname(tv_kind_txt.getText().toString().trim());
                        Log.i("up_uid", " StaticMember.USER.getUid()="+StaticMember.USER.getUid());
                        result_ftp = SFTPChannel.getChannel(ImagefilePath, StaticMember.FTPRemotePath + today, up_gis_pic, 1000);
                        Log.e(TAG, "run: "+today );
                        if (result_ftp == "1") {

                            Gson gson = new Gson();
                            String dataStr = gson.toJson(adRedBean);
                            Log.i("dataStr", " #### dataStr="+dataStr);
                            result_http = HttpTools.sendPostRequest(StaticMember.URL + "mob_log.php", dataStr);
                        }
                        Log.e("1上传返回的结果", "\t result_ftp=" + result_ftp + "\t result_http=" + result_http);

                        handler.sendEmptyMessageDelayed(1, 1000);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
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
                AlertDialog.Builder builder = new AlertDialog.Builder(RedMarkerDetail.this);
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
