package com.protectapp.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.protectapp.R;
import com.protectapp.databinding.AppTextInputBinding;
import com.protectapp.util.Constants;

public class TextInputView extends FrameLayout implements TextView.OnEditorActionListener {
    private String countryCode = "";
    private AppTextInputBinding binding;
    private boolean showingError = false;
    private int hintColor;
    private int txtColor;
    public TextInputView(Context context) {
        super(context);
        init(null);
    }

    public TextInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TextInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void init(AttributeSet attrs) {
        countryCode = Constants.COUNTRY_CODE+ " ";
        int inputType = InputType.TYPE_CLASS_TEXT;
        String fontName = null;
        String hint = null;
        Drawable background = null;
        boolean editable=true;
         hintColor = ContextCompat.getColor(getContext(), R.color.colorTxtGray);
         txtColor = ContextCompat.getColor(getContext(),R.color.colorTxtBlack);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TextInput);
            inputType = a.getInt(R.styleable.TextInput_inputType, InputType.TYPE_CLASS_TEXT);
            fontName = a.getString(R.styleable.TextInput_fontName);
            hint = a.getString(R.styleable.TextInput_hint);
            editable = a.getBoolean(R.styleable.TextInput_editable,true);
            background = a.getDrawable(R.styleable.TextInput_inputBackground);
            hintColor = a.getColor(R.styleable.TextInput_inputHintColor, ContextCompat.getColor(getContext(), R.color.colorTxtGray));
            txtColor = a.getColor(R.styleable.TextInput_inputColor, ContextCompat.getColor(getContext(), R.color.colorTxtBlack));
            a.recycle();
        }

        View view = LayoutInflater.from(getContext()).inflate(R.layout.app_text_input, this, false);
        addView(view);
        binding = DataBindingUtil.bind(view);

        //Set Editable
        binding.textInputEt.setEnabled(editable);
        //Set Next Focus
        //setNextFocus(focusSearch(FOCUS_DOWN));
        //Set InputType
        binding.textInputEt.setInputType(inputType);
        if (inputType == InputType.TYPE_CLASS_PHONE) {
            binding.textInputEt.setText(countryCode);
            Selection.setSelection(binding.textInputEt.getText(), binding.textInputEt.getText().length());
            binding.textInputEt.addTextChangedListener(new CountryCodeTextWatcher());
        } else if (inputType == InputType.TYPE_TEXT_VARIATION_PASSWORD) {
            binding.textInputEt.setTransformationMethod(new PasswordTransformationMethod());
            binding.textInputEt.addTextChangedListener(new DefaultTextWatcher());
        }
        else
        {
            binding.textInputEt.addTextChangedListener(new DefaultTextWatcher());
        }

        binding.togglePasswordVisibilityBtn.setVisibility(inputType == InputType.TYPE_TEXT_VARIATION_PASSWORD ?
                VISIBLE : GONE);
        binding.togglePasswordVisibilityBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.togglePasswordVisibilityBtn.setSelected(!binding.togglePasswordVisibilityBtn.isSelected());
                binding.textInputEt.setTransformationMethod(binding.togglePasswordVisibilityBtn.isSelected() ?
                        new SingleLineTransformationMethod() : new PasswordTransformationMethod());
                Selection.setSelection(binding.textInputEt.getText(), binding.textInputEt.getText().length());

            }
        });
        //Set Font
        if (fontName != null) {
            Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "font/" + fontName);
            if (myTypeface != null)
                binding.textInputEt.setTypeface(myTypeface);
        }
        //Set Hint
        binding.textInputHint.setText(hint != null ? hint : "");
        //Set Background Color
        if (background != null)
            binding.container.setBackgroundDrawable(background);
        else
            binding.container.setBackgroundResource(R.drawable.text_input_background);
        //Set hint color
        binding.textInputHint.setTextColor(hintColor);
        //Set text color
        binding.textInputEt.setTextColor(txtColor);
        //Set IME Options
        binding.textInputEt.setImeOptions(getNextFocusDownId()!=-1 ? EditorInfo.IME_ACTION_NEXT : EditorInfo.IME_ACTION_DONE);
        //Set Editor Action Listener
        binding.textInputEt.setOnEditorActionListener(this);
    }
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // call getParent() here
    }
    private  class CountryCodeTextWatcher extends DefaultTextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            super.beforeTextChanged(s, start, count, after);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            super.onTextChanged(s, start, before, count);
        }

        @Override
        public void afterTextChanged(Editable s) {
            super.afterTextChanged(s);
            if (!s.toString().startsWith(countryCode)) {
                binding.textInputEt.setText(countryCode);
                Selection.setSelection(binding.textInputEt.getText(), binding.textInputEt.getText().length());

            } else if (s.toString().length() > Constants.MOBILE_NUMBER_LENGTH + countryCode.length()) {
                binding.textInputEt.setText(s.toString().substring(0, s.length() - 1));
                Selection.setSelection(binding.textInputEt.getText(), binding.textInputEt.getText().length());
            }
        }
    }



    private  class DefaultTextWatcher implements TextWatcher
    {


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (showingError) hideError();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    }
    public void setError(String error) {
        showingError = true;
        binding.errorTv.setText(error);
        binding.errorTv.setVisibility(VISIBLE);
        binding.textInputEt.requestFocus();
        binding.textInputEt.setTextColor(ContextCompat.getColor(getContext(), R.color.colorErrorRed));

    }

    public void hideError() {
        showingError = false;
        binding.errorTv.setVisibility(GONE);
        binding.textInputEt.setTextColor(txtColor);

    }

    public String getValue() {
        if (binding.textInputEt.getInputType() == InputType.TYPE_CLASS_PHONE) {
            return binding.textInputEt.getText().toString().replace(countryCode, "").trim();
        } else {
            return binding.textInputEt.getText().toString().trim();
        }
    }

    public EditText getEditText() {
        return binding.textInputEt;
    }

    private TextInputView getNextFocus()
    {
       View nextFocus = ((View)getParent()).findViewById(getNextFocusDownId());
       if(nextFocus instanceof  TextInputView)
       {
           return (TextInputView) nextFocus;
       }
       else
       {
           throw new IllegalArgumentException("Next focus should be an instance of "+getClass());
       }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    switch (actionId)
    {
        case EditorInfo.IME_ACTION_NEXT:
            TextInputView nextFocus =getNextFocus();
            nextFocus.getEditText().setSelection(nextFocus.getEditText().getText().toString().length());
            nextFocus.getEditText().requestFocus(FOCUS_FORWARD);
            return true;
        case EditorInfo.IME_ACTION_DONE:
            if(onDoneListener!=null)onDoneListener.onDone();
            return false;
    }
        return false;
    }
private OnDoneListener onDoneListener=null;

    public OnDoneListener getOnDoneListener() {
        return onDoneListener;
    }

    public void setOnDoneListener(OnDoneListener onDoneListener) {
        this.onDoneListener = onDoneListener;
    }

    public interface OnDoneListener
{
    void onDone();
}
public void setText(String text)
{
    binding.textInputEt.setText(text);
}
}
