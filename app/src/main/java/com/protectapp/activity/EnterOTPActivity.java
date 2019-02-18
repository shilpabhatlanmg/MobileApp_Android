package com.protectapp.activity;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Toast;

import com.protectapp.R;
import com.protectapp.databinding.ActivityEnterOtpBinding;
import com.protectapp.model.GenericResponseModel;
import com.protectapp.model.RequestOTPData;
import com.protectapp.model.SpanData;
import com.protectapp.model.VerifyOTPData;
import com.protectapp.network.ProtectApiHelper;
import com.protectapp.util.AppCommons;
import com.protectapp.util.Constants;

public class EnterOTPActivity extends BaseActivity implements View.OnClickListener {
    private ActivityEnterOtpBinding binding;
    private String mobileNumber;
    private String countryCode;
    private int otpValidity;
    private static final int REQUEST_OTP_RQ = 201;
    private static final int VERIFY_OTP_RQ = 202;
    //remove after pilvo
    private String tempOTP;
    private boolean fromForgotPassword = false;

    @Override
    public void setContentView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enter_otp);
        transparentStatusBar();
    }

    @Override
    public void getExtras() {

        mobileNumber = getIntent().getExtras().getString(Constants.EXTRA.MOBILE_NUMBER);
        otpValidity = getIntent().getExtras().getInt(Constants.EXTRA.OTP_VALIDITY);
        fromForgotPassword = getIntent().getExtras().getBoolean(Constants.EXTRA.FROM_FORGOT_PASSWORD);
        tempOTP = getIntent().getExtras().getString("temp_otp");
        binding.backBtn.setVisibility(isTaskRoot() ? View.GONE : View.VISIBLE);
        binding.backBtn.setOnClickListener(this);
    }

    @Override
    public void initUI() {
        countryCode = Constants.COUNTRY_CODE;
    }

    @Override
    public void setUI() {
        binding.feedbackMsgTv.setHighlightColor(Color.TRANSPARENT);
        binding.feedbackMsgTv.setMovementMethod(LinkMovementMethod.getInstance());
        binding.verifyButton.setOnClickListener(this);
        binding.headerSubTitle.setText(getString(R.string.enter_the_otp_sent_to, countryCode + " " + mobileNumber));
       // binding.otpInput.setValue(tempOTP);
        setTimerFeedback();
    }

    @Override
    public void hitAPI(int req_code) {

        switch (req_code) {
            case REQUEST_OTP_RQ:
                ProtectApiHelper.getInstance().requestOTP(Constants.COUNTRY_CODE + " " + mobileNumber, fromForgotPassword,
                        AppCommons.isVisitor(this),new ApiCallback<RequestOTPData>(REQUEST_OTP_RQ));
                break;
            case VERIFY_OTP_RQ:
                ProtectApiHelper.getInstance().verifyOTP(Constants.COUNTRY_CODE + " " + mobileNumber, tempOTP, AppCommons.isVisitor(this), new ApiCallback<VerifyOTPData>(VERIFY_OTP_RQ));
                break;
        }

    }

    @Override
    public <D> void onApiSuccess(GenericResponseModel<D> model, int req_code) {
        switch (req_code) {
            case REQUEST_OTP_RQ:
                hideProgress();
                if (model.getData() instanceof RequestOTPData) {

                    otpValidity = Integer.parseInt(((RequestOTPData) model.getData()).getValidity());
                    tempOTP = ((RequestOTPData) model.getData()).getOTP();
                    setTimerFeedback();
                }
            case VERIFY_OTP_RQ:
                hideProgress();
                if (model.getData() instanceof VerifyOTPData) {
                    if (((VerifyOTPData) model.getData()).isOTPValid()) {
                        onVerificationDone((VerifyOTPData) model.getData());
                    }
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.verify_button:
                verifyOTP();
                break;
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }

    private void verifyOTP() {
        if (validate()) {
            showProgress(false);
            hitAPI(VERIFY_OTP_RQ);
        }
    }

    private CountDownTimer otpCountDown = null;

    private void setTimerFeedback() {
        if (otpCountDown != null) otpCountDown.cancel();
        otpCountDown = new CountDownTimer(otpValidity * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.feedbackMsgTv.setText(
                        AppCommons.getSpannedString(
                                new SpanData(getString(R.string.you_will_get_otp_in) + " ", ContextCompat.getColor(EnterOTPActivity.this, R.color.colorTxtGray), 1f, false),
                                new SpanData(AppCommons.convertMillistoMinutesSeconds(millisUntilFinished), ContextCompat.getColor(EnterOTPActivity.this, R.color.colorErrorRed), 1f, true))
                );

            }

            @Override
            public void onFinish() {
                binding.feedbackMsgTv.setText(
                        AppCommons.getSpannedString(
                                new SpanData(getString(R.string.do_not_receive_code) + " ", ContextCompat.getColor(EnterOTPActivity.this, R.color.colorTxtGray), 1f, false),
                                new SpanData(getString(R.string.resend_otp).toUpperCase(), ContextCompat.getColor(EnterOTPActivity.this, R.color.colorGreen), 1f, true, resendOTPListener))
                );
            }
        };
        otpCountDown.start();
    }

    private View.OnClickListener resendOTPListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showProgress(false);
            hitAPI(REQUEST_OTP_RQ);
        }
    };

    private boolean validate() {
        boolean valid = true;

        return valid;
    }

    private void onVerificationDone(VerifyOTPData data) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA.ACCESS_TOKEN, data.getAccessToken());
        bundle.putBoolean(Constants.EXTRA.FROM_FORGOT_PASSWORD, fromForgotPassword);
        getNavigation().afterOTPVerification(this, bundle);
    }
}
