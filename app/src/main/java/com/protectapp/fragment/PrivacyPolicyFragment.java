package com.protectapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.protectapp.R;
import com.protectapp.listener.DashboardFragmentListener;
import com.protectapp.model.GenericResponseModel;
import com.protectapp.util.Constants;
import com.protectapp.util.GenericRecyclerAdapter;

public class PrivacyPolicyFragment extends CMSBaseFragment  {

    private DashboardFragmentListener mListener;

    public PrivacyPolicyFragment() {
        // Required empty public constructor
        contentType=Constants.CMS.PRIVACY_POLICY;
    }

    public static PrivacyPolicyFragment newInstance() {
        PrivacyPolicyFragment fragment = new PrivacyPolicyFragment();
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
