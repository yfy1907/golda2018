package com.example.administrator.goldaappnew.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;

import com.example.administrator.goldaappnew.R;
import com.example.administrator.goldaappnew.jpush.LocalBroadcastManager;
import com.example.administrator.goldaappnew.utils.CommonTools;

public abstract class BaseFragment extends Fragment {

    protected Toolbar baseToolbar;
    protected LocalBroadcastManager localBroadcastManager ;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    protected void showSnackbarMessage(String message){
        if(baseToolbar != null){
            Snackbar sn = Snackbar.make(baseToolbar, message, BaseTransientBottomBar.LENGTH_SHORT);
            sn.show();
            CommonTools.setSnackbarMessageTextColor(sn, getResources().getColor(R.color.orange));
        }
    }
}