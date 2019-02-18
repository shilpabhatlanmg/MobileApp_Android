package com.protectapp.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.protectapp.R;
import com.protectapp.databinding.BadgeImageViewBinding;

public class BadgeImageView extends FrameLayout {
    public BadgeImageView(Context context) {
        super(context);
        init(null);
    }

    public BadgeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BadgeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }
    private BadgeImageViewBinding binding;
    private void init(AttributeSet attrs) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.badge_image_view,this,false);
        addView(view);
        binding = DataBindingUtil.bind(view);
        int[] attrsArray = new int[] {
                android.R.attr.src
        };
        Drawable src=null;
        if(attrs!=null)
        {
            TypedArray a = getContext().obtainStyledAttributes(attrs, attrsArray);
            src = a.getDrawable(0);
        }
        if(src!=null)
        binding.iconIv.setImageDrawable(src);
    }

    public void setBadgeCount(int count)
    {
        binding.badgeTv.setText(String.valueOf(count));
        binding.badgeTv.setVisibility(count==0 ? GONE : VISIBLE);
    }
}
