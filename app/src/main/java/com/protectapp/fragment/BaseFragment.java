package com.protectapp.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.protectapp.R;
import com.protectapp.activity.BaseActivity;
import com.protectapp.model.GenericResponseModel;
import com.protectapp.util.AppSession;
import com.protectapp.util.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseFragment extends Fragment {
    private View progressBarLayout = null;
    public abstract void initUI(View view);

    public abstract void setUI();

    public abstract void hitAPI(int req_code);

    public abstract <D> void onApiSuccess(GenericResponseModel<D> model, int req_code);

    public abstract <D> void onApiFail(GenericResponseModel<D> model, int req_code);

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
        setUI();
    }
    public void setUpProgressBar(ViewGroup container) {

            progressBarLayout = LayoutInflater.from(getContext()).inflate(R.layout.inner_progress_bar_layout, container, false);
            container.addView(progressBarLayout);
            progressBarLayout.setVisibility(View.GONE);

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

                if(getActivity() instanceof BaseActivity)
                {
                    AppSession.getInstance().logout();
                    ((BaseActivity) getActivity()).getNavigation().afterLogout(getActivity(), new Bundle());
                }
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

    public void showProgress()
    {
        if (progressBarLayout!=null)
        {
            progressBarLayout.removeCallbacks(showProgressRunnable);
            progressBarLayout.postDelayed(showProgressRunnable,1000);
        }
    }
    public void hideProgress(){
        if (progressBarLayout!=null)
        {
            progressBarLayout.removeCallbacks(showProgressRunnable);
            progressBarLayout.setVisibility(View.GONE);
        }
    }

private Runnable showProgressRunnable=new Runnable() {
    @Override
    public void run() {

        progressBarLayout.setVisibility(View.VISIBLE);
    }
};

    public void showActivityProgress(boolean cancellable)
    {
        if(getActivity() instanceof BaseActivity)
        {
            ((BaseActivity) getActivity()).showProgress(cancellable);
        }
    }
    public void hideActivityProgress()
    {
        if(getActivity() instanceof BaseActivity)
        {
            ((BaseActivity) getActivity()).hideProgress();
        }
    }
}
