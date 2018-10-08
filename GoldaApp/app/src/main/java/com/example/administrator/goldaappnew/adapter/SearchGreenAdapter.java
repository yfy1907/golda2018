package com.example.administrator.goldaappnew.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.goldaappnew.R;
import com.example.administrator.goldaappnew.activity.GreenMarkerDetail;
import com.example.administrator.goldaappnew.bean.AdGreenBean;
import com.example.administrator.goldaappnew.utils.CommonTools;
import com.example.administrator.goldaappnew.utils.DrawableTool;

import java.util.List;

/**
 * @name GoldaApp
 * @class name：com.example.administrator.goldaapp.adapter
 * @class describe
 * @anthor Administrator QQ:1032006226
 * @time 2017/9/25 10:50
 * @change
 * @chang time
 * @class describe
 */
public class SearchGreenAdapter extends RecyclerView.Adapter<SearchGreenAdapter.ViewHolder>{
    private List<AdGreenBean> list;
    private Context context;

    private LayoutInflater inflater;

    public SearchGreenAdapter(List<AdGreenBean> list, Context context) {
        this.list = list;
        this.context = context;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.cardview_search,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (list.get(position).getCompany().equals(""))
            holder.company.setText("未录入数据");
        else
            holder.company.setText(list.get(position).getAddress());
        if (list.get(position).getAddress().equals(""))
            holder.address.setText("未录入数据");
        else
            holder.address.setText(list.get(position).getAddress());

        holder.problem.setText("");

        holder.date.setText(CommonTools.timeStamp2Date(list.get(position).getDateline(), "yyyy/MM/dd"));
//            holder.date.setVisibility(View.INVISIBLE);
        holder.id.setText(list.get(position).getBoard_id());

        if (list.get(position).getStatus().equals("-1"))
            holder.icon.setImageResource(DrawableTool.getValue("pink_" + list.get(position).getIcon()));
        else
            holder.icon.setImageResource(DrawableTool.getValue("green_" + list.get(position).getIcon()));

//        Log.e("TAG", "getItemCount: "+list.size() );
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, GreenMarkerDetail.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("AdGreenBean",list.get(position));
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();

    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView company;
        public ImageView icon;
        public TextView address;
        public TextView date;
        public TextView id;
        public TextView problem;

        public ViewHolder(View itemView) {
            super(itemView);
            company = (TextView) itemView.findViewById(R.id.tv_company);
            icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            address = (TextView) itemView.findViewById(R.id.tv_address);
            date = (TextView) itemView.findViewById(R.id.tv_time);
            problem = (TextView) itemView.findViewById(R.id.tv_problem);
            id = (TextView) itemView.findViewById(R.id.tv_id);
        }
    }
}
