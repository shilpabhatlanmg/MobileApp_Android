package com.protectapp.customview;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.protectapp.R;
import com.protectapp.databinding.OtpInputViewBinding;

public class OTPInputView extends FrameLayout implements View.OnTouchListener {
    private OtpInputViewBinding binding;
    public OTPInputView(Context context) {
        super(context);
        init(null);
    }

    public OTPInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public OTPInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
         View view = LayoutInflater.from(getContext()).inflate(R.layout.otp_input_view,this,false);
         addView(view);
         binding=DataBindingUtil.bind(view);
        setTextWatcher();

        binding.otpInputBox1.setOnTouchListener(this);
        binding.otpInputBox2.setOnTouchListener(this);
        binding.otpInputBox3.setOnTouchListener(this);
        binding.otpInputBox4.setOnTouchListener(this);
        binding.otpInputBox1.setCursorVisible(false);
        binding.otpInputBox2.setCursorVisible(false);
        binding.otpInputBox3.setCursorVisible(false);
        binding.otpInputBox4.setCursorVisible(false);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId())
        {
            case R.id.otp_input_box_1:
            case R.id.otp_input_box_2:
            case R.id.otp_input_box_3:
            case R.id.otp_input_box_4:
                removeTextWatcher();
                binding.otpInputBox1.setText("");
                binding.otpInputBox2.setText("");
                binding.otpInputBox3.setText("");
                binding.otpInputBox4.setText("");
                setTextWatcher();
                binding.otpInputBox1.requestFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(binding.otpInputBox1, InputMethodManager.SHOW_IMPLICIT);
                return true;
        }
        return true;
    }
    private abstract class OTPTextWatcher implements TextWatcher
    {
        public abstract void onNext();
        public abstract void onPrev();

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.toString().length()==1)
                onNext();
            else
            if(s.toString().length()==2)
            {
                s.delete(0,1);
                onNext();
            }
            else
            if(s.toString().length()==0)
                onPrev();
        }
    }
    private void setTextWatcher()
    {
        binding.otpInputBox1.addTextChangedListener(otp1TextWatcher);
        binding.otpInputBox2.addTextChangedListener(otp2TextWatcher);
        binding.otpInputBox3.addTextChangedListener(otp3TextWatcher);
        binding.otpInputBox4.addTextChangedListener(otp4TextWatcher);
    }
    private void removeTextWatcher()
    {
        binding.otpInputBox1.removeTextChangedListener(otp1TextWatcher);
        binding.otpInputBox2.removeTextChangedListener(otp2TextWatcher);
        binding.otpInputBox3.removeTextChangedListener(otp3TextWatcher);
        binding.otpInputBox4.removeTextChangedListener(otp4TextWatcher);
    }
    private OTPTextWatcher otp1TextWatcher =new OTPTextWatcher() {
    @Override
    public void onNext() {
        binding.otpInputBox2.requestFocus();
    }

    @Override
    public void onPrev() {

    }
    };
    private OTPTextWatcher otp2TextWatcher =new OTPTextWatcher() {
    @Override
    public void onNext() {
        binding.otpInputBox3.requestFocus();
    }

    @Override
    public void onPrev() {
        binding.otpInputBox1.requestFocus();
    }
    };
    private OTPTextWatcher otp3TextWatcher =new OTPTextWatcher() {
    @Override
    public void onNext() {
        binding.otpInputBox4.requestFocus();
    }

    @Override
    public void onPrev() {
        binding.otpInputBox2.requestFocus();
    }
    };
    private OTPTextWatcher otp4TextWatcher =new OTPTextWatcher() {
    @Override
    public void onNext() {
    }

    @Override
    public void onPrev() {
        binding.otpInputBox3.requestFocus();
    }
    };

    public void setValue(String value)
    {
        if(value==null || value.length()!=4)return;
        String[] values = value.split("");
        binding.otpInputBox1.setText(values[1]);
        binding.otpInputBox2.setText(values[2]);
        binding.otpInputBox3.setText(values[3]);
        binding.otpInputBox4.setText(values[4]);
    }
}
