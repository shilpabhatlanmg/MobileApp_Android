package com.protectapp.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.protectapp.R;
import com.protectapp.customview.TextInputView;
import com.protectapp.databinding.FragmentChangePasswordBinding;
import com.protectapp.listener.DashboardFragmentListener;
import com.protectapp.model.GenericResponseModel;
import com.protectapp.network.ProtectApiHelper;
import com.protectapp.util.AppCommons;
import com.protectapp.util.AppSession;
import com.protectapp.util.GenericRecyclerAdapter;

public class ChangePasswordFragment extends BaseFragment implements View.OnClickListener {

    private DashboardFragmentListener mListener;
    private static final int CHANGE_PASSWORD_RQ=201;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }
    public static ChangePasswordFragment newInstance() {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
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

    private FragmentChangePasswordBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }


    @Override
    public void initUI(View view) {

        binding=DataBindingUtil.bind(view);
    }

    @Override
    public void setUI() {
        binding.saveButton.setOnClickListener(this);
        binding.confirmPasswordInput.setOnDoneListener(new TextInputView.OnDoneListener() {
            @Override
            public void onDone() {
                changePassword();
            }
        });
    }

    @Override
    public void hitAPI(int req_code) {
    switch (req_code)
    {
        case CHANGE_PASSWORD_RQ:
            ProtectApiHelper.getInstance().changePassword(AppSession.getInstance().getAccessToken(),binding.oldPasswordInput.getValue(),
                    binding.newPasswordInput.getValue(),new ApiCallback<Object>(CHANGE_PASSWORD_RQ));
            break;
    }
    }

    @Override
    public <D> void onApiSuccess(GenericResponseModel<D> model, int req_code) {
        switch (req_code)
        {
            case CHANGE_PASSWORD_RQ:
                hideActivityProgress();
                AppCommons.showToast(getContext(),R.string.password_changed_success);
               break;
        }
    }

    @Override
    public <D> void onApiFail(GenericResponseModel<D> model, int req_code) {
        switch (req_code)
        {
            case CHANGE_PASSWORD_RQ:
                hideActivityProgress();
                AppCommons.showError(getContext(),model!=null ? model.getMessage() : null);
                break;
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
    private boolean validate()
    {
        if(binding.oldPasswordInput.getValue().isEmpty())
        {
            binding.oldPasswordInput.setError(getString(R.string.error_old_password_input));
            return false;
        }
        else
        if(binding.newPasswordInput.getValue().isEmpty())
        {
            binding.newPasswordInput.setError(getString(R.string.error_new_password_input));
            return false;
        }
        else
        if(binding.confirmPasswordInput.getValue().isEmpty())
        {
            binding.confirmPasswordInput.setError(getString(R.string.error_confirm_password_input));
            return false;
        }
        else
        if(binding.newPasswordInput.getValue().equals(binding.oldPasswordInput.getValue()))
        {
            binding.newPasswordInput.setError(getString(R.string.error_new_old_password_same));
            return false;
        }
        else
        if(!binding.newPasswordInput.getValue().equals(binding.confirmPasswordInput.getValue()))
        {
            binding.confirmPasswordInput.setError(getString(R.string.error_password_mismatch));
            return false;
        }

    return true;
    }

    @Override
    public void onClick(View v) {
     switch (v.getId())
     {
         case R.id.save_button:changePassword();break;
     }
    }

    private void changePassword()
    {
        if(validate())
        {
            showActivityProgress(false);
            hitAPI(CHANGE_PASSWORD_RQ);
        }
    }
}
