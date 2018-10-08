package com.example.administrator.goldaappnew.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.goldaappnew.R;
import com.example.administrator.goldaappnew.bean.BoardBean;

import java.util.List;

public class BoradListViewAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<BoardBean> listData;

    @SuppressLint("WrongConstant")
    public BoradListViewAdapter(Activity activity,List<BoardBean> paramList) {
        this.listData = paramList;
        this.inflater = ((LayoutInflater) activity.getSystemService("layout_inflater"));
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

    public View getView(final int paramInt, View paramView, ViewGroup paramViewGroup) {
        if (paramView == null) {
            paramView = this.inflater.inflate(R.layout.fragment_board_listview_item, null);
        }
        ViewHolder holder = new ViewHolder(paramView);

        BoardBean board = listData.get(paramInt);

        holder.text_company.setText(board.getCompany());
        holder.text_address.setText(board.getAddress());
        holder.text_board_type.setText(board.getIcon_type()+" "+board.getIcon_class()+" "+board.getIcon_cnname());
        holder.text_dateline.setText(board.getDateline());



        paramView.setTag(holder);
        return paramView;
    }

    class ViewHolder {
        public CardView board_card;
        public TextView text_company;
        public TextView text_dateline;
        public TextView text_address;
        public TextView text_board_type;

        public TextView board_state_sl; // 受理状态
        public TextView board_state_hc; // 核查状态
        public TextView board_state_sh; // 审核状态
        public TextView board_state_ba; // 备案状态

        public Button btn_query;

        public ViewHolder(View itemView) {
            board_card = (CardView) itemView.findViewById(R.id.board_card);
            text_company = (TextView) itemView.findViewById(R.id.text_company);
            text_dateline = (TextView) itemView.findViewById(R.id.text_dateline);
            text_address = (TextView) itemView.findViewById(R.id.text_address);
            text_board_type = (TextView) itemView.findViewById(R.id.text_board_type);

            board_state_sl = (TextView) itemView.findViewById(R.id.board_state_sl);
            board_state_hc = (TextView) itemView.findViewById(R.id.board_state_hc);
            board_state_sh = (TextView) itemView.findViewById(R.id.board_state_sh);
            board_state_ba = (TextView) itemView.findViewById(R.id.board_state_ba);
            btn_query = (Button) itemView.findViewById(R.id.btn_query);
        }
    }


}
