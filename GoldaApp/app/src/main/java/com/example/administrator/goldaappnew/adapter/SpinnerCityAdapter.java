package com.example.administrator.goldaappnew.adapter;

import java.util.List;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.goldaappnew.bean.SpinnerCityListItem;

public class SpinnerCityAdapter extends BaseAdapter {

    private Context context;
    private List<SpinnerCityListItem> myList;

    public SpinnerCityAdapter(Context context, List<SpinnerCityListItem> myList) {
        this.context = context;
        this.myList = myList;
    }

    public int getCount() {
        return myList.size();
    }
    public Object getItem(int position) {
        return myList.get(position);
    }
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        SpinnerCityListItem myListItem = myList.get(position);
        return new MyAdapterView(this.context, myListItem );
    }

    class MyAdapterView extends LinearLayout {
        public static final String LOG_TAG = "MyAdapterView";

        public MyAdapterView(Context context, SpinnerCityListItem spinnerListItem ) {
            super(context);
            this.setOrientation(HORIZONTAL);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, LayoutParams.WRAP_CONTENT);
            params.setMargins(1, 1, 1, 1);

            TextView name = new TextView( context );
            name.setText( spinnerListItem.getName() );
            addView( name, params);

            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(200, LayoutParams.WRAP_CONTENT);
            params2.setMargins(1, 1, 1, 1);

            TextView pcode = new TextView(context);
            pcode.setText(spinnerListItem.getPcode());
            addView( pcode, params2);
            pcode.setVisibility(GONE);
        }
    }

}