package com.protectapp.customview;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.protectapp.R;
import com.protectapp.databinding.DefaultToolbarBinding;

public class DefaultToolbar extends FrameLayout {
    public DefaultToolbar( Context context) {
        super(context);
        init();
    }

    public DefaultToolbar( Context context,  AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DefaultToolbar( Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private DefaultToolbarBinding binding;
    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.default_toolbar, this, false);
        addView(view);
        binding = DataBindingUtil.bind(view);

    }

    public void setTitle(String title)
    {
        binding.toolbarTitleTv.setText(title);
    }
    public void setTitle(int title)
    {
        binding.toolbarTitleTv.setText(getContext().getString(title));
    }
    public void setBackPressListener(OnClickListener backPressListener)
    {
        binding.backBtn.setOnClickListener(backPressListener);
    }
    public void addMenuItem(int res_id)
    {
        ImageView menuItem = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.toolbar_menu_item,binding.toolbarMenuLay,false);
        menuItem.setId(res_id);
        menuItem.setImageResource(res_id);
        menuItem.setOnClickListener(menuItemClickListener);
        binding.toolbarMenuLay.addView(menuItem);
    }

    public OnClickListener getMenuItemClickListener() {
        return menuItemClickListener;
    }

    public void setMenuItemClickListener(OnClickListener menuItemClickListener) {
        this.menuItemClickListener = menuItemClickListener;
    }

    private View.OnClickListener menuItemClickListener=null;
}
