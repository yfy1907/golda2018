package com.example.administrator.goldaappnew.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.goldaappnew.R;
import com.example.administrator.goldaappnew.bean.BoardBean;
import com.example.administrator.goldaappnew.utils.CommonTools;

import java.util.List;

/**
 * 上拉加载更多
 */

public class LoadMoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<BoardBean> dataList;
    private OnItemClickListener mClickListener;

    // 普通布局
    private final int TYPE_ITEM = 1;
    // 脚布局
    private final int TYPE_FOOTER = 2;
    // 当前加载状态，默认为加载完成
    private int loadState = 2;
    // 正在加载
    public final int LOADING = 1;
    // 加载完成
    public final int LOADING_COMPLETE = 2;
    // 加载到底
    public final int LOADING_END = 3;

    // 内部接口，点击查看或编辑
    public interface OnItemClickListener {
        void onItemClick(View view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mClickListener = listener;
    }

    public LoadMoreAdapter(Activity activity,List<BoardBean> dataList) {
        this.activity = activity;
        this.dataList = dataList;
    }

    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为FooterView
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 通过判断显示类型，来创建不同的View
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_board_listview_item, parent, false);
            RecyclerView.ViewHolder viewHolder = new RecyclerViewHolder(view);
//            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                @SuppressLint("WrongConstant")
//                @Override
//                public void onClick(View view) {
////                    Toast.makeText(activity, view.getTag() + "", 1000).show();
//                    if(null != mClickListener){
//                        mClickListener.onItemClick(view);
//                    }
//                }
//            });
            return viewHolder;

        } else if (viewType == TYPE_FOOTER) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_refresh_footer, parent, false);
            return new FootViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof RecyclerViewHolder) {

            final RecyclerViewHolder holder = (RecyclerViewHolder) viewHolder;

            BoardBean board = dataList.get(position);

            holder.text_company.setText(board.getCompany());
            holder.text_address.setText(board.getAddress());
            holder.text_board_type.setText(board.getIcon_type()+" "+board.getIcon_class()+" "+board.getIcon_cnname());

            if ("".equals(board.getDateline()) || null == board.getDateline())
                holder.text_dateline.setText("0000/00/00");
            else
                holder.text_dateline.setText(CommonTools.timeStamp2Date(board.getDateline(), "yyyy/MM/dd"));

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
                holder.board_state_hc.setText("待处理");
                holder.board_state_hc.setBackgroundResource(R.drawable.shape_board_button_state0);
            }else if("1".equals(board.getLocal_check())){
                holder.board_state_hc.setText(" 通过");
                holder.board_state_hc.setBackgroundResource(R.drawable.shape_board_button_state1);
            }else if("2".equals(board.getLocal_check())){
                holder.board_state_hc.setText("未通过");
                holder.board_state_hc.setBackgroundResource(R.drawable.shape_board_button_state2);
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

            final String confirm_status = board.getConfirm_status(); // 1只能查看， 0可以修改

            if("1".equals(confirm_status)){
                holder.btn_query.setText(" 查 看 ");
                holder.btn_query.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
            }else{
                holder.btn_query.setText(" 处 理 ");
                holder.btn_query.setBackgroundColor(Color.parseColor("#FF4F99C6"));
            }
            holder.btn_query.setTag(position);
            holder.btn_query.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("WrongConstant")
                @Override
                public void onClick(View view) {
//                    Toast.makeText(activity, view.getTag() + "", 1000).show();
                    if(null != mClickListener){
                        mClickListener.onItemClick(view);
                    }
                }
            });

//            Log.e("TAG", "getItemCount: "+dataList.size() );
            holder.itemView.setTag(position);

        } else if (viewHolder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) viewHolder;
            switch (loadState) {
                case LOADING: // 正在加载
                    footViewHolder.pbLoading.setVisibility(View.VISIBLE);
                    footViewHolder.tvLoading.setVisibility(View.VISIBLE);
                    footViewHolder.llEnd.setVisibility(View.GONE);
                    break;

                case LOADING_COMPLETE: // 加载完成
                    footViewHolder.pbLoading.setVisibility(View.INVISIBLE);
                    footViewHolder.tvLoading.setVisibility(View.INVISIBLE);
                    footViewHolder.llEnd.setVisibility(View.GONE);
                    break;

                case LOADING_END: // 加载到底
                    footViewHolder.pbLoading.setVisibility(View.GONE);
                    footViewHolder.tvLoading.setVisibility(View.GONE);
                    footViewHolder.llEnd.setVisibility(View.VISIBLE);
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size() + 1;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    // 如果当前是footer的位置，那么该item占据2个单元格，正常情况下占据1个单元格
                    return getItemViewType(position) == TYPE_FOOTER ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder {

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

        RecyclerViewHolder(View itemView) {
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

    private class FootViewHolder extends RecyclerView.ViewHolder {

        ProgressBar pbLoading;
        TextView tvLoading;
        LinearLayout llEnd;

        FootViewHolder(View itemView) {
            super(itemView);
            pbLoading = (ProgressBar) itemView.findViewById(R.id.pb_loading);
            tvLoading = (TextView) itemView.findViewById(R.id.tv_loading);
            llEnd = (LinearLayout) itemView.findViewById(R.id.ll_end);
        }
    }

    /**
     * 设置上拉加载状态
     *
     * @param loadState 0.正在加载 1.加载完成 2.加载到底
     */
    public void setLoadState(int loadState) {
        this.loadState = loadState;
        notifyDataSetChanged();
    }

}

