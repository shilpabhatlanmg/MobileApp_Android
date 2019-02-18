package com.protectapp.fragment;

import android.content.Context;
import android.os.Bundle;

import com.protectapp.listener.DashboardFragmentListener;
import com.protectapp.util.Constants;

public class AboutUsFragment extends CMSBaseFragment {
    private DashboardFragmentListener mListener;
    public AboutUsFragment() {
        // Required empty public constructor
        contentType =Constants.CMS.ABOUT_US;
    }

    public static AboutUsFragment newInstance() {
        AboutUsFragment fragment = new AboutUsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DashboardFragmentListener) {
            mListener = (DashboardFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DashboardFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
