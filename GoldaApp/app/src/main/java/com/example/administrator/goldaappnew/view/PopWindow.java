package com.example.administrator.goldaappnew.view;




import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.goldaappnew.R;
import com.example.administrator.goldaappnew.activity.GreenMarkerDetail;
import com.example.administrator.goldaappnew.activity.MainFragmentActivity;
import com.example.administrator.goldaappnew.activity.RedMarkerDetail;
import com.example.administrator.goldaappnew.bean.MarkerInfo;
import com.example.administrator.goldaappnew.utils.DrawableTool;


import java.util.ArrayList;

public class PopWindow extends PopupWindow implements OnItemClickListener {

    private Context mContext;
    private View view;
    private TextView tv_title;
    private ListView list;
    private MyAdspter myAdspter;
    private ArrayList<MarkerInfo> arrays;

    public PopWindow(Context mContext, ArrayList<MarkerInfo> arrays, int id, String title) {
        this.view = LayoutInflater.from(mContext).inflate(id, null);
        tv_title = (TextView) view.findViewById(R.id.title);
        if (title != null)
            tv_title.setText(title);
        this.mContext = mContext;
        this.arrays = arrays;
        list = (ListView) view.findViewById(R.id.markers_list);
        myAdspter = new MyAdspter(mContext, arrays);
        list.setAdapter(myAdspter);
        list.setScrollingCacheEnabled(false);
        list.setOnItemClickListener(this);
        // 设置外部可点击
        this.setOutsideTouchable(true);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        this.view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = view.findViewById(R.id.tv_title).getTop();

                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

    /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高
        this.setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);

        // 设置弹出窗体可点击
        this.setFocusable(true);
        // 设置弹出窗体的背景
        this.setBackgroundDrawable(new ColorDrawable(0xffffffff));

        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.pop_anim);

    }

    public void setTitle(String title) {
        if (title != null)
            tv_title.setText(title);
    }

    public void updateData() {
        myAdspter.notifyDataSetChanged();
    }





    class MyAdspter extends BaseAdapter {

        private ArrayList<MarkerInfo> markers;
        private LayoutInflater layoutInflater;

        public MyAdspter(Context context, ArrayList<MarkerInfo> markers) {
            this.markers = markers;
            this.layoutInflater = LayoutInflater.from(context);
        }

        /**
         * 组件集合，对应list.xml中的控件
         *
         * @author Administrator
         */
        public final class item {
            public TextView company;
            public ImageView icon;
            public TextView address;
            public TextView date;
            public TextView id;
            public TextView problem;
        }

        @Override
        public int getCount() {
            return markers.size();
        }

        /**
         * 获得某一位置的数据
         */
        @Override
        public Object getItem(int position) {
            return markers.get(position);
        }

        /**
         * 获得唯一标识
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            item item;
            if (convertView == null) {
                item = new item();
                Log.e("getView", "convertView是空的");
                //获得组件，实例化组件
                convertView = layoutInflater.inflate(R.layout.cardview_search, null);
                item.company = (TextView) convertView.findViewById(R.id.tv_company);
                item.icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                item.address = (TextView) convertView.findViewById(R.id.tv_address);
                item.date = (TextView) convertView.findViewById(R.id.tv_time);
                item.problem = (TextView) convertView.findViewById(R.id.tv_problem);
                item.id = (TextView) convertView.findViewById(R.id.tv_id);
                convertView.setTag(item);
            } else {
                Log.e("getView", "convertView是已有的");
                item = (item) convertView.getTag();
            }
            //绑定数据
            if (!markers.get(position).getCompany().equals(""))
                item.company.setText(markers.get(position).getCompany());
            else
                item.company.setText("未录入公司信息");
            Log.e("pop加载的图片", "" + markers.get(position).getMediatype());
            item.icon.setImageResource(DrawableTool.getValue(markers.get(position).getMediatype()));
            item.address.setText(markers.get(position).getAddress());
            item.id.setText(markers.get(position).getId() + "");
            item.date.setText(markers.get(position).getDateline());
            item.problem.setText(markers.get(position).getProblem());


            return convertView;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        dismiss();
        if (arrays.get(arg2).getType() == "red") {
            Intent intent = new Intent(mContext, RedMarkerDetail.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("AdRedBean", arrays.get(arg2).getAd_red());
            bundle.putInt("id", arrays.get(arg2).getId());
            intent.putExtras(bundle);
            ((MainFragmentActivity)mContext).startActivityForResult(intent, 1002);

        } else if (arrays.get(arg2).getType() == "green") {

            Intent intent = new Intent(mContext, GreenMarkerDetail.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("AdGreenBean", arrays.get(arg2).getAd_green());
            bundle.putInt("id", arrays.get(arg2).getId());
            intent.putExtras(bundle);
            ((MainFragmentActivity)mContext).startActivityForResult(intent, 1002);
        }

    }
}