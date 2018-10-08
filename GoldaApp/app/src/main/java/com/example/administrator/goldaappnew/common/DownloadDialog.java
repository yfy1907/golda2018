package com.example.administrator.goldaappnew.common;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.goldaappnew.R;


/**
 * 下载提示框，有下载进度条
 */
public class DownloadDialog extends Dialog {

    private Context context = null;//上下文信息
    private TextView textViewMsg;//提示信息
    private ProgressBar progressbar = null;//进度条
    private Button btnHiddenDownload = null;

    /**
     * 构造函数
     *
     * @param msg     提示信息
     * @param context 上下文信息
     */
    public DownloadDialog(String msg, Context context, View.OnClickListener hiddenDownClick, boolean isHiddenBtn) {
        super(context, R.style.CustomProgressDialog);
        this.context = context;
        // 加载自己定义的布局
        View view = LayoutInflater.from(context).inflate(R.layout.custom_download_dialog, null);
        view.setBackgroundDrawable(new BitmapDrawable());
        //提示信息
        textViewMsg = (TextView) view.findViewById(R.id.message);
        //后台下载按钮
        btnHiddenDownload = (Button) view.findViewById(R.id.dialog_btn2);
        btnHiddenDownload.setOnClickListener(hiddenDownClick);

        if (isHiddenBtn) {
            btnHiddenDownload.setVisibility(View.GONE);
        }
        //进度条
        progressbar = (ProgressBar) view.findViewById(R.id.loadProgressBar);


        setContentView(view);
        setMsg(msg);
    }

    /**
     * 设置提示信息
     *
     * @param msg 提示信息
     */
    public void setMsg(String msg) {
        if (null != textViewMsg) {
            textViewMsg.setText(msg);
        }
    }

    /**
     * 设置进度条最大值
     *
     * @param max 进度条最大值
     */
    public void setMax(int max) {
        progressbar.setMax(max);
    }

    /**
     * 设置提示信息和进度条当前值
     *
     * @param msg   提示信息
     * @param value 进度条当前值
     */
    public void setMsgAndValue(String msg, int value) {
        progressbar.setProgress(value);
        textViewMsg.setText(msg);
    }

    /**
     * 设置提示信息
     *
     * @param resId XML中配置的信息的编号
     */
    public void setMsg(int resId) {
        if (null != textViewMsg) {
            textViewMsg.setText(context.getString(resId));
        }
    }
}