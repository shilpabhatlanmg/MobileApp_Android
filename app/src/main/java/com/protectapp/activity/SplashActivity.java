package com.protectapp.activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.protectapp.R;
import com.protectapp.model.GenericResponseModel;
import com.protectapp.util.AppSession;
import com.protectapp.util.Constants;
import com.protectapp.util.NavigationEventHandler;
import com.protectapp.util.NavigationEventHandlerImpl;

public class SplashActivity extends BaseActivity {
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_splash);
        transparentStatusNavBar();
    }

    @Override
    public void getExtras() {

    }

    @Override
    public void initUI() {
        //`AppSession.getInstance().init(this);
    }

    @Override
    public void setUI() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getNavigation().afterSplash(SplashActivity.this,getIntent().getExtras());
                finish();
            }
        },Constants.SPLASH_DELAY);
    }

    @Override
    public void hitAPI(int req_code) {

    }

    @Override
    public <D> void onApiSuccess(GenericResponseModel<D> model, int req_code) {

    }

    @Override
    public <D> void onApiFail(GenericResponseModel<D> model, int req_code) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
