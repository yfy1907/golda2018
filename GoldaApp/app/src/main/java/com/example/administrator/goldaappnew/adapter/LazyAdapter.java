package com.example.administrator.goldaappnew.adapter;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.goldaappnew.R;
import com.example.administrator.goldaappnew.common.ImageLoader;
import com.example.administrator.goldaappnew.staticClass.StaticMember;
import com.example.administrator.goldaappnew.utils.StringUtil;

import java.util.ArrayList;
import java.util.Map;

public class LazyAdapter extends BaseAdapter {

    private Activity activity;
    private static LayoutInflater inflater = null;
    private ImageLoader imageLoader;

//    private ListView mListView;
    private ArrayList<Map<String, String>> listData;

    private IDialogControl dialogControl;
    private IDialogControl dialog2Control;

    class RemoveFileImageOnClickListener implements View.OnClickListener{
        private int position;
        public RemoveFileImageOnClickListener(int pos){
            position = pos;
        }
        @Override
        public void onClick(View view) {
            if(null != dialog2Control){
                dialog2Control.onShowDialog();// 显示对话框
                dialog2Control.getPosition(position);
            }
        }
    }

    class AddFileImageOnClickListener implements View.OnClickListener{
        private int position;
        public AddFileImageOnClickListener(int pos){
            position = pos;
        }
        @Override
        public void onClick(View view) {
            if(null != dialogControl){
                dialogControl.onShowDialog();// 显示对话框
                dialogControl.getPosition(position);
            }
        }
    }

    // 内部接口
    public interface IDialogControl {
        void onShowDialog();
        void getPosition(int position);
    }

    public LazyAdapter(Activity a, IDialogControl dialogControl, IDialogControl dialog2Control, ArrayList<Map<String, String>> paramList) {
        activity = a;
        listData = paramList;

        this.dialogControl = dialogControl;
        this.dialog2Control = dialog2Control;

        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return listData.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.attach_item, null);

        TextView tv_title = ((TextView) vi.findViewById(R.id.tv_title));
        ImageView iv_add_attach = ((ImageView) vi.findViewById(R.id.iv_add_attach));
        ImageView iv_view_small = ((ImageView) vi.findViewById(R.id.iv_view_small));

        // 设置添加附件标题
        tv_title.setText(listData.get(position).get("title"));
        tv_title.setTag(listData.get(position).get("title"));
        // 设置添加附件点击
        iv_add_attach.setOnClickListener(new AddFileImageOnClickListener(position));
        // 查看详细时，隐藏添加图片按钮
        if(null == dialogControl){
            iv_add_attach.setVisibility(View.INVISIBLE);
        }else{
            iv_add_attach.setVisibility(View.VISIBLE);
        }

        // 设置点击缩略图删除
        iv_view_small.setOnClickListener(new RemoveFileImageOnClickListener(position));

        String fileUrl = listData.get(position).get("file_name");
        String loadUrl = fileUrl;
        iv_view_small.setTag(loadUrl);

        if(!StringUtil.isEmpty(fileUrl)){
            String suffix = fileUrl.substring(fileUrl.lastIndexOf(".")+1).toLowerCase();
            // Log.i("","## 文件地址："+fileUrl+"; 后缀名："+suffix);
            if("pdf".equals(suffix)){
                // PDF文件显示PDF图标
                iv_view_small.setImageResource(R.drawable.pdf_icon);
            }else{
                if(!fileUrl.contains(StaticMember.ImageURL)){
                    loadUrl = StaticMember.ImageURL+fileUrl;
                }
                imageLoader.DisplayImage(loadUrl, iv_view_small);
            }
        }else{
            iv_view_small.setImageResource(R.drawable.no_file);
        }
        return vi;
    }
}