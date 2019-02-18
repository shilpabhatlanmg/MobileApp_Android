package com.protectapp.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.protectapp.R;
import com.protectapp.customview.TextInputView;
import com.protectapp.databinding.ActivityCreatePasswordBinding;
import com.protectapp.model.GenericResponseModel;
import com.protectapp.network.ProtectApiHelper;
import com.protectapp.util.AppCommons;
import com.protectapp.util.Constants;

public class CreatePasswordActivity extends BaseActivity implements View.OnClickListener, TextInputView.OnDoneListener {
    private ActivityCreatePasswordBinding binding;
    private boolean fromForgotPassword;
    private String accessToken;
    private static final int CREATE_PASSWORD_RQ = 201;

    @Override
    public void setContentView() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_password);
        transparentStatusBar();
    }

    @Override
    public void getExtras() {
        accessToken = getIntent().getStringExtra(Constants.EXTRA.ACCESS_TOKEN);
        fromForgotPassword = getIntent().getBooleanExtra(Constants.EXTRA.FROM_FORGOT_PASSWORD, false);
    }

    @Override
    public void initUI() {

    }

    @Override
    public void setUI() {
        binding.headerTitle.setText(getString(fromForgotPassword ? R.string.reset_password : R.string.create_password));
        binding.createPasswordButton.setText(getString(fromForgotPassword ? R.string.reset_password : R.string.create_password));
        binding.createPasswordButton.setOnClickListener(this);
        binding.confirmPasswordInput.setOnDoneListener(this);
        binding.backBtn.setVisibility(isTaskRoot() ? View.GONE : View.VISIBLE);
        binding.backBtn.setOnClickListener(this);
    }

    @Override
    public void hitAPI(int req_code) {
        switch (req_code) {
            case CREATE_PASSWORD_RQ:
                    ProtectApiHelper.getInstance().createPassword(accessToken, binding.newPasswordInput.getValue(), new ApiCallback<Object>(CREATE_PASSWORD_RQ));
                break;

        }

    }

    @Override
    public <D> void onApiSuccess(GenericResponseModel<D> model, int req_code) {
        switch (req_code) {
            case CREATE_PASSWORD_RQ:
                hideProgress();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.EXTRA.ACCESS_TOKEN, accessToken);
                getNavigation().afterCreatePassword(this, bundle);
                break;

        }
    }

    @Override
    public <D> void onApiFail(GenericResponseModel<D> model, int req_code) {
        switch (req_code) {
            case CREATE_PASSWORD_RQ:
                hideProgress();
                AppCommons.showError(this,model != null ? model.getMessage() : null);
                break;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private boolean validate() {
        boolean valid = true;
        if (binding.newPasswordInput.getValue().length() == 0) {
            valid = false;
            binding.newPasswordInput.setError(getString(R.string.error_password_input));
        } else if (binding.newPasswordInput.getValue().length() < Constants.PASSWORD_MIN_LENGTH) {
            valid = false;
            binding.newPasswordInput.setError(getString(R.string.error_password_length,Constants.PASSWORD_MIN_LENGTH));
        } else if (!binding.newPasswordInput.getValue().equals(binding.confirmPasswordInput.getValue())) {
            valid = false;
            binding.confirmPasswordInput.setError(getString(R.string.error_password_mismatch));
        }

        return valid;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_password_button:
                createPassword();
                break;
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onDone() {
        createPassword();
    }

    private void createPassword() {
        if(validate())
        {
            showProgress(false);
            hitAPI(CREATE_PASSWORD_RQ);
        }
    }
}
