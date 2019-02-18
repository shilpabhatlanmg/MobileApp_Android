package com.protectapp.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.protectapp.R;
import com.protectapp.customview.TextInputView;
import com.protectapp.databinding.ActivityMobileVerificationBinding;
import com.protectapp.model.GenericResponseModel;
import com.protectapp.model.RequestOTPData;
import com.protectapp.network.ProtectApiHelper;
import com.protectapp.util.AppCommons;
import com.protectapp.util.Constants;

public class MobileVerificationActivity extends BaseActivity implements View.OnClickListener, TextInputView.OnDoneListener {
    private ActivityMobileVerificationBinding binding;
    private boolean fromForgotPassword;
    public static final int REQUEST_OTP_RQ = 201;

    @Override
    public void setContentView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mobile_verification);
        transparentStatusBar();
    }

    @Override
    public void getExtras() {
        fromForgotPassword = getIntent().getBooleanExtra(Constants.EXTRA.FROM_FORGOT_PASSWORD, false);
    }

    @Override
    public void initUI() {

    }

    @Override
    public void setUI() {

        binding.getOtpButton.setOnClickListener(this);
        binding.backBtn.setVisibility(isTaskRoot() ? View.GONE : View.VISIBLE);
        binding.backBtn.setOnClickListener(this);
        binding.phoneInput.setOnDoneListener(this);
    }

    @Override
    public void hitAPI(int req_code) {
        switch (req_code) {
            case REQUEST_OTP_RQ:
                showProgress(false);
                ProtectApiHelper.getInstance().requestOTP(Constants.COUNTRY_CODE + " " + binding.phoneInput.getValue(), fromForgotPassword,
                        AppCommons.isVisitor(this),new ApiCallback<RequestOTPData>(REQUEST_OTP_RQ));
                break;
        }
    }

    @Override
    public <D> void onApiSuccess(GenericResponseModel<D> model, int req_code) {
        switch (req_code) {
            case REQUEST_OTP_RQ:
                hideProgress();
                if (model.getData() instanceof RequestOTPData) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.EXTRA.MOBILE_NUMBER, binding.phoneInput.getValue());
                    bundle.putBoolean(Constants.EXTRA.FROM_FORGOT_PASSWORD, fromForgotPassword);
                    bundle.putString("temp_otp", ((RequestOTPData) model.getData()).getOTP());
                    String validity = ((RequestOTPData) model.getData()).getValidity()==null ? "0" :
                            ((RequestOTPData) model.getData()).getValidity();
                    bundle.putInt(Constants.EXTRA.OTP_VALIDITY, Integer.valueOf(validity));
                    getNavigation().afterGetOtpClick(this, bundle);

                }
                break;
        }
    }

    @Override
    public <D> void onApiFail(GenericResponseModel<D> model, int req_code) {
        switch (req_code) {
            case REQUEST_OTP_RQ:
                hideProgress();
                AppCommons.showError(this,model != null ? model.getMessage() : null);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_otp_button:
                getOtp();
                break;
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }

    private void getOtp() {
        if (validate()) {
            hitAPI(REQUEST_OTP_RQ);
        }
    }

    private boolean validate() {
        boolean valid = true;
        if (binding.phoneInput.getValue().length() < Constants.MOBILE_NUMBER_LENGTH) {
            valid = false;
            binding.phoneInput.setError("Mobile number is not valid");
        }
        return valid;
    }

    @Override
    public void onDone() {
        getOtp();
    }
}
