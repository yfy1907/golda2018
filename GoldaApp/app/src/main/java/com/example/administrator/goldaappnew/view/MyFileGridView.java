package com.example.administrator.goldaappnew.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class MyFileGridView extends GridView {

    public MyFileGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public MyFileGridView(Context context) {
        super(context);
    }

    public MyFileGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
