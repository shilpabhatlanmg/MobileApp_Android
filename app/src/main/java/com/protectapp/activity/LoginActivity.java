package com.protectapp.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.protectapp.R;
import com.protectapp.customview.TextInputView;
import com.protectapp.databinding.ActivityLoginBinding;
import com.protectapp.model.GenericResponseModel;
import com.protectapp.model.ProfileData;
import com.protectapp.network.ProtectApiHelper;
import com.protectapp.util.AppCommons;
import com.protectapp.util.Constants;

public class LoginActivity extends BaseActivity implements View.OnClickListener, TextInputView.OnDoneListener {
    private ActivityLoginBinding binding;
    public static final int LOGIN_API_REQ = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        transparentStatusBar();
    }

    @Override
    public void getExtras() {

    }

    @Override
    public void initUI() {

    }

    @Override
    public void setUI() {
        binding.loginButton.setOnClickListener(this);
        binding.forgotPasswordTv.setOnClickListener(this);
        binding.passwordInput.setOnDoneListener(this);
        binding.appLogoIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.passwordInput.getEditText().requestFocus(View.FOCUS_FORWARD);
            }
        });
    }

    @Override
    public void hitAPI(int req_code) {
        switch (req_code) {
            case LOGIN_API_REQ:
                ProtectApiHelper.getInstance().doSecurityLogin(Constants.COUNTRY_CODE + " " + binding.phoneInput.getValue(), binding.passwordInput.getValue(), new ApiCallback<ProfileData>(LOGIN_API_REQ));
                break;
        }
    }

    @Override
    public <D> void onApiSuccess(GenericResponseModel<D> model, int req_code) {
        hideProgress();
        switch (req_code) {
            case LOGIN_API_REQ:
                if (model.getData() instanceof ProfileData) {
                    Log.d("", "");
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.EXTRA.ACCESS_TOKEN,((ProfileData) model.getData()).getToken());
                    bundle.putSerializable(Constants.EXTRA.PROFILE_DATA,(ProfileData) model.getData());
                    bundle.putBoolean(Constants.EXTRA.CREATE_PASSWORD,((ProfileData) model.getData()).isShouldCreatePassword());
                    getNavigation().afterLogin(this,bundle);
                }
                break;
        }
    }

    @Override
    public <D> void onApiFail(GenericResponseModel<D> model, int req_code) {
        hideProgress();
        AppCommons.showError(this,model != null ? model.getMessage() : null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                doLogin();
                break;
            case R.id.forgot_password_tv:
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.EXTRA.FROM_FORGOT_PASSWORD,true);
                getNavigation().afterForgotPasswordClick(this,bundle);
                break;
        }
    }

    private boolean validate() {
        boolean valid = true;
        if (binding.phoneInput.getValue().length() != Constants.MOBILE_NUMBER_LENGTH) {
            valid = false;
            this.binding.phoneInput.setError(getString(R.string.error_mobile_input));
        } else if (this.binding.passwordInput.getValue().length() == 0) {
            valid = false;
            this.binding.phoneInput.setError(getString(R.string.error_password_input));
        }
        return valid;
    }

    @Override
    public void onDone() {
        doLogin();
    }

    private void doLogin() {
        if (validate())
            showProgress(false);
        hitAPI(LOGIN_API_REQ);
    }

}
