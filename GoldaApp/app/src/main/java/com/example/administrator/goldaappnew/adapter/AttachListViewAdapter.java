package com.example.administrator.goldaappnew.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.goldaappnew.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * 选择附件ListView
 */
public class AttachListViewAdapter extends BaseAdapter {

    private Activity activity;

    private ListView mListView;
    private ArrayList<Map<String, String>> listData;

    private IDialogControl dialogControl;
    private IDialogControl dialog2Control;

    @SuppressLint("WrongConstant")
    public AttachListViewAdapter(Activity activity, IDialogControl dialogControl,IDialogControl dialog2Control,
                                 ArrayList<Map<String, String>> paramList, ListView listView) {
        this.mListView = listView;
        this.activity = activity;
        this.listData = paramList;
        this.dialogControl = dialogControl;
        this.dialog2Control = dialog2Control;
    }

    public int getCount() {
        return this.listData.size();
    }

    public Object getItem(int paramInt) {
        return this.listData.get(paramInt);
    }

    public long getItemId(int paramInt) {
        return paramInt;
    }

    public View getView(final int paramInt, View convertView, ViewGroup parent) {
        if (mListView == null) {
            mListView = (ListView) parent;
        }
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.attach_item, null);
        }
        viewHolder.tv_title = ((TextView) convertView.findViewById(R.id.tv_title));
        viewHolder.iv_add_attach = ((ImageView) convertView.findViewById(R.id.iv_add_attach));
        viewHolder.iv_view_small = ((ImageView) convertView.findViewById(R.id.iv_view_small));
        convertView.setTag(paramInt);

        // 设置添加附件标题
        viewHolder.tv_title.setText(listData.get(paramInt).get("title"));
        // 设置添加附件点击
        viewHolder.iv_add_attach.setOnClickListener(new AddFileImageOnClickListener(paramInt));
        // 设置点击缩略图删除
        viewHolder.iv_view_small.setOnClickListener(new RemoveFileImageOnClickListener(paramInt));
        viewHolder.iv_view_small.setTag(listData.get(paramInt).get("file_name"));

        return convertView;
    }


    public class ViewHolder {
        private TextView tv_title;
        private ImageView iv_add_attach;
        private ImageView iv_view_small;
    }

    class RemoveFileImageOnClickListener implements View.OnClickListener{
        private int position;
        public RemoveFileImageOnClickListener(int pos){
            position = pos;
        }
        @Override
        public void onClick(View view) {
            dialog2Control.onShowDialog();// 显示对话框
            dialog2Control.getPosition(position);
        }
    }

    class AddFileImageOnClickListener implements View.OnClickListener{
        private int position;
        public AddFileImageOnClickListener(int pos){
            position = pos;
        }
        @Override
        public void onClick(View view) {
            dialogControl.onShowDialog();// 显示对话框
            dialogControl.getPosition(position);
        }
    }

    // 内部接口
    public interface IDialogControl {
        void onShowDialog();
        void getPosition(int position);
    }
}
