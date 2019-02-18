package com.protectapp.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.protectapp.R;
import com.protectapp.databinding.FragmentAidHistoryBinding;
import com.protectapp.listener.DashboardFragmentListener;
import com.protectapp.model.GenericResponseModel;
import com.protectapp.util.Constants;
import com.protectapp.util.GenericRecyclerAdapter;

import java.util.Map;

public class AidHistoryFragment extends BaseFragment implements GenericRecyclerAdapter.GenericItemBinder {


    private DashboardFragmentListener mListener;;


    public AidHistoryFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AidHistoryFragment newInstance(String param1, String param2) {
        AidHistoryFragment fragment = new AidHistoryFragment();
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

    private FragmentAidHistoryBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_aid_history, container, false);
    }

    @Override
    public void initUI(View view) {
        binding = DataBindingUtil.bind(view);
    }

    @Override
    public void setUI() {
        binding.viewpager.setAdapter(new AidHistoryPagerAdapter(getChildFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.viewpager);

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

    @Override
    public void bind(ViewDataBinding itemBinding, int position) {

    }

    @Override
    public void onItemClick(int position) {

    }

    private class AidHistoryPagerAdapter extends FragmentStatePagerAdapter {
        public AidHistoryPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            String type;
            switch (i) {
                case 0:type=Constants.INCIDENT.TODAY;
                    break;
                case 1:type=Constants.INCIDENT.YESTERDAY;
                    break;
                case 2:type=Constants.INCIDENT.OTHERS;
                    break;
                    default:type=Constants.INCIDENT.TODAY;
            }
            return AidHistoryListFragment.newInstance(type);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.today);
                case 1:
                    return getString(R.string.yesterday);
                default:
                    return getString(R.string.previous);
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
