package com.protectapp.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.protectapp.R;
import com.protectapp.databinding.FragmentCmsBinding;
import com.protectapp.model.GenericResponseModel;
import com.protectapp.model.GetCMSPageData;
import com.protectapp.network.ProtectApiHelper;
import com.protectapp.util.AppCommons;
import com.protectapp.util.Constants;

public class CMSBaseFragment extends BaseFragment {
    String contentType = Constants.CMS.ABOUT_US;
    private FragmentCmsBinding binding;
    private static final int CMS_PAGE_RQ=201;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cms, container, false);
    }

    @Override
    public void initUI(View view) {
        binding = DataBindingUtil.bind(view);
    }

    @Override
    public void setUI() {
        setUpProgressBar(binding.container);
        hitAPI(CMS_PAGE_RQ);
        showProgress();
    }

    @Override
    public void hitAPI(int req_code) {
        switch (req_code)
        {
            case CMS_PAGE_RQ:
                ProtectApiHelper.getInstance().getCMSPage(contentType,new ApiCallback<GetCMSPageData>(CMS_PAGE_RQ));
                break;
        }

    }

    @Override
    public <D> void onApiSuccess(GenericResponseModel<D> model, int req_code) {
        switch (req_code)
        {
            case CMS_PAGE_RQ:
                hideProgress();
                if (model.getData() instanceof GetCMSPageData)
                {

                    binding.webView.loadDataWithBaseURL(Constants.PROTECT_API_BASE_URL,((GetCMSPageData) model.getData()).getPage(),"text/html","utf-8","");

                }
                break;
        }
    }

    @Override
    public <D> void onApiFail(GenericResponseModel<D> model, int req_code) {
        switch (req_code)
        {
            case CMS_PAGE_RQ:
                hideProgress();
                AppCommons.showError(getContext(),model!=null ? model.getMessage() : null);
                break;
        }
    }


}
