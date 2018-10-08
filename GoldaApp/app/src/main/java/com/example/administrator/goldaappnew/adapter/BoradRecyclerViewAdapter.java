package com.example.administrator.goldaappnew.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.goldaappnew.R;
import com.example.administrator.goldaappnew.bean.BoardBean;
import com.example.administrator.goldaappnew.utils.CommonTools;

import java.util.List;

public class BoradRecyclerViewAdapter extends RecyclerView.Adapter<BoradRecyclerViewAdapter.ViewHolder>{

    private LayoutInflater inflater;
    private List<BoardBean> list;

    private Activity activity;
    @SuppressLint("WrongConstant")
    public BoradRecyclerViewAdapter(Activity activity, List<BoardBean> paramList) {
        this.list = paramList;
        this.activity = activity;
        this.inflater = ((LayoutInflater) activity.getSystemService("layout_inflater"));
    }

    @Override
    public BoradRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fragment_board_listview_item,parent,false);
        BoradRecyclerViewAdapter.ViewHolder viewHolder = new BoradRecyclerViewAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BoradRecyclerViewAdapter.ViewHolder holder, final int position) {
        BoardBean board = list.get(position);

        holder.text_company.setText(board.getCompany());
        holder.text_address.setText(board.getAddress());
        holder.text_board_type.setText(board.getIcon_type()+" "+board.getIcon_class()+" "+board.getIcon_cnname());

        if ("".equals(list.get(position).getDateline()) || null == list.get(position).getDateline())
            holder.text_dateline.setText("0000/00/00");
        else
            holder.text_dateline.setText(CommonTools.timeStamp2Date(list.get(position).getDateline(), "yyyy/MM/dd"));

        /**
         * 黄色的代表“未上传、未受理、未审核”
            红色的表示“已退回”
            绿色的表示“已受理、已审核、已通过”
            处理用blue，查看用grey

         受理Status : 0未受理， 1已受理， 2已退回
         核查local_check : 0待处理， 1通过， 2未通过
         审核lead_idea : 0待审核， 1通过， 2未通过
         最后备案是由最后一步备案专员上传备案通知书就算备案完成b_attach_20这个有值就算“已备案”
         */
        // 受理状态
        if("0".equals(board.getStatus())){
            holder.board_state_sl.setText("未受理");
            holder.board_state_sl.setBackgroundResource(R.drawable.shape_board_button_state0);
        }else if("1".equals(board.getStatus())){
            holder.board_state_sl.setText("已受理");
            holder.board_state_sl.setBackgroundResource(R.drawable.shape_board_button_state1);
        }else if("2".equals(board.getStatus())){
            holder.board_state_sl.setText("已退回");
            holder.board_state_sl.setBackgroundResource(R.drawable.shape_board_button_state2);
        }

        // 核查状态
        if("0".equals(board.getLocal_check())){
            holder.board_state_sl.setText("待处理");
            holder.board_state_sl.setBackgroundResource(R.drawable.shape_board_button_state0);
        }else if("1".equals(board.getLocal_check())){
            holder.board_state_sl.setText(" 通过");
            holder.board_state_sl.setBackgroundResource(R.drawable.shape_board_button_state1);
        }else if("2".equals(board.getLocal_check())){
            holder.board_state_sl.setText("未通过");
            holder.board_state_sl.setBackgroundResource(R.drawable.shape_board_button_state2);
        }

        // 审核状态
        if("0".equals(board.getLead_idea())){
            holder.board_state_sh.setText("待处理");
            holder.board_state_sh.setBackgroundResource(R.drawable.shape_board_button_state0);
        }else if("1".equals(board.getLead_idea())){
            holder.board_state_sh.setText("通过");
            holder.board_state_sh.setBackgroundResource(R.drawable.shape_board_button_state1);
        }else if("2".equals(board.getLead_idea())){
            holder.board_state_sh.setText("未通过");
            holder.board_state_sh.setBackgroundResource(R.drawable.shape_board_button_state2);
        }

        // 备案状态
        if(null != board.getB_attach_20() && !"".equals(board.getB_attach_20())){
            holder.board_state_ba.setText("通过");
            holder.board_state_ba.setBackgroundResource(R.drawable.shape_board_button_state1);
        }else {
            holder.board_state_ba.setText("未备案");
            holder.board_state_ba.setBackgroundResource(R.drawable.shape_board_button_state2);
        }

//        Log.e("TAG", "getItemCount: "+list.size() );
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.i("","## xxx pos="+position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();

    }

    class ViewHolder extends RecyclerView.ViewHolder{
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
            super(itemView);

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
