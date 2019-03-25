package com.protectapp.activity;

import android.animation.Animator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.protectapp.R;
import com.protectapp.model.GenericResponseModel;
import com.protectapp.util.AppSession;
import com.protectapp.util.Constants;
import com.protectapp.util.NavigationEventHandler;
import com.protectapp.util.NavigationEventHandlerImpl;
import com.protectapp.util.NetworkUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseActivity extends AppCompatActivity {
    private NavigationEventHandler navigationEventHandler;
    private View noInternetBar = null;
    private View progressBarLayout = null;
    public abstract void setContentView();

    public abstract void getExtras();

    public abstract void initUI();

    public abstract void setUI();

    public abstract void hitAPI(int req_code);

    public abstract <D> void onApiSuccess(GenericResponseModel<D> model, int req_code);

    public abstract <D> void onApiFail(GenericResponseModel<D> model, int req_code);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setContentView();
        getExtras();
        setBaseUI();
        initUI();
        setUI();
    }

    private void init() {
        navigationEventHandler = new NavigationEventHandlerImpl();

    }

    private void setBaseUI() {
        View view = findViewById(android.R.id.content);
        if (view instanceof FrameLayout) {
            noInternetBar = LayoutInflater.from(this).inflate(R.layout.no_internet_bar, (FrameLayout) view, false);
            ((FrameLayout) view).addView(noInternetBar);
            noInternetBar.setVisibility(View.GONE);
            progressBarLayout = LayoutInflater.from(this).inflate(R.layout.progress_bar_layout, (FrameLayout) view, false);
            ((FrameLayout) view).addView(progressBarLayout);
            progressBarLayout.setVisibility(View.GONE);
        }

    }

    public NavigationEventHandler getNavigation() {
        return navigationEventHandler;
    }

    public void transparentStatusBar() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void transparentStatusNavBar() {
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, true);
        }
    }

    private void setWindowFlag(final int bits, boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    public class ApiCallback<T> implements Callback<GenericResponseModel<T>> {
        private int reqCode;

        ApiCallback(int req_code) {
            this.reqCode = req_code;
        }

        @Override
        public void onResponse(Call<GenericResponseModel<T>> call, Response<GenericResponseModel<T>> response) {
            if (response.code() == Constants.API.SUCCESS) {
                if (response.body().getSuccess()==1)
                onApiSuccess(response.body(), reqCode);
                else
                onApiFail(response.body(), reqCode);
            }
            else
            if(response.code() == Constants.API.TOKEN_EXPIRE)
            {
                AppSession.getInstance().logout();
                getNavigation().afterLogout(BaseActivity.this, new Bundle());
            }
            else
            {
                onApiFail(null,reqCode);
            }
        }

        @Override
        public void onFailure(Call<GenericResponseModel<T>> call, Throwable t) {

            onApiFail(null,reqCode);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter networkChangeIntentFilter = new IntentFilter();
        networkChangeIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeIntentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        registerReceiver(networkChangeReceiver, networkChangeIntentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkChangeReceiver);
    }

    private NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();

    private class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {

            int status = NetworkUtil.getConnectivityStatusString(context);
            if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                   internetDisconnected();
                } else {
                    internetConnected();
                }
            }
        }
    }

    private void internetConnected()
    {
        if(noInternetBar!=null)
        {
            noInternetBar.animate().translationY(-getResources().getDimensionPixelSize(R.dimen.no_internet_bar_height)).setDuration(300)
                    .setListener(hideInternetErrorAnimListener);
        }
    }
    private void internetDisconnected()
    {
        if(noInternetBar!=null)
        {
            noInternetBar.setVisibility(View.VISIBLE);
            noInternetBar.setY(-getResources().getDimensionPixelSize(R.dimen.no_internet_bar_height));
            noInternetBar.animate().translationY(0).setDuration(300).setListener(null);
        }
    }
    private Animator.AnimatorListener hideInternetErrorAnimListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            noInternetBar.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };



    public void showProgress(boolean cancellable)
    {
        if (progressBarLayout!=null)
        {
            progressBarLayout.setVisibility(View.VISIBLE);
            progressBarLayout.setBackgroundColor(ContextCompat.getColor(this,cancellable ?
                    android.R.color.transparent : R.color.colorDimBlack));
            if(!cancellable)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }
    }
    public void hideProgress(){
        if (progressBarLayout!=null && progressBarLayout.getVisibility() == View.VISIBLE)
        {
            progressBarLayout.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hideProgress();
    }
}
