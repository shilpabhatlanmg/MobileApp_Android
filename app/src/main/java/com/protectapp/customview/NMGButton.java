package com.protectapp.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.protectapp.R;


public class NMGButton extends AppCompatButton {

	public NMGButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);


	}

	public NMGButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);

	}

	public NMGButton(Context context) {
		super(context);
		init(null);
	}
	
	private void init(AttributeSet attrs) {

		if (attrs!=null) {
			 TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TextInput);
			 String fontName = a.getString(R.styleable.TextInput_fontName);
			 if (fontName!=null) {
				 Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "font/" +fontName);
				 if(myTypeface!=null)
				 setTypeface(myTypeface);
			 }
			 a.recycle();
		}
	}

}