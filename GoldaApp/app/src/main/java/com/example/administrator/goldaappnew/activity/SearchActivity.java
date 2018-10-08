package com.example.administrator.goldaappnew.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.goldaappnew.R;
import com.example.administrator.goldaappnew.adapter.SearchRedAdapter;
import com.example.administrator.goldaappnew.bean.AdRedBean;
import com.example.administrator.goldaappnew.staticClass.StaticMember;
import com.example.administrator.goldaappnew.utils.CommonTools;
import com.example.administrator.goldaappnew.utils.HttpTools;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/22.
 */
public class SearchActivity extends AppCompatActivity {
    @BindView(R.id.search_view)
    SearchView searchView;
    @BindView(R.id.tv_sure_cancel)
    TextView tvSureCancel;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycleView)
    RecyclerView recycleView;
    @BindView(R.id.search_src_text)
    SearchView.SearchAutoComplete textView;

    private String query;
    private List<AdRedBean> list;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        CommonTools.setStateBarColor(this);
        toolbar.setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        intData();
    }
    private void intData(){
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        textView.setTextColor(getResources().getColor(R.color.white));
        tvSureCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (query != null && !query.equals(""))
                    searchAndShow(query);
            }
        });
        query = getIntent().getStringExtra("query");
        textView.setText(query);
        searchAndShow(query);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchAndShow(query);
                Log.e("执行查询", "onQueryTextSubmit");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                query = newText;
                Log.e("查询内容改变", "onQueryTextChange");
                return false;
            }
        });
    }

    public void searchAndShow(final String query) {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    searchView.clearFocus();
                    Log.e("查到的广告数", list.size() + "个");
                    LinearLayoutManager llm = new LinearLayoutManager(SearchActivity.this);
                    recycleView.setLayoutManager(llm);
                    recycleView.setAdapter(new SearchRedAdapter(list, SearchActivity.this));
                    Snackbar sn = Snackbar.make(toolbar, "根据关键词-" + query + "-共查找到" + list.size() + "个巡查记录", BaseTransientBottomBar.LENGTH_SHORT);
                    CommonTools.setSnackbarMessageTextColor(sn, getResources().getColor(R.color.orange));
                    sn.show();
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                list = HttpTools.getJson(StaticMember.URL + "mob_search.php",
                        "query=" + query+"&use_permissions="+StaticMember.use_permissions+"&uid=" + StaticMember.USER.getUid(),
                        StaticMember.ADLIST_RED);
                handler.sendEmptyMessage(1);
            }
        }).start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
            finish();
        return super.onKeyDown(keyCode, event);
    }
}
