package com.protectapp.customview;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.protectapp.R;
import com.protectapp.databinding.DrawerMenuViewBinding;
import com.protectapp.model.ProfileData;
import com.protectapp.util.AppCommons;
import com.protectapp.util.AppSession;

public class DrawerMenuView extends FrameLayout implements View.OnClickListener {

    private DrawerMenuViewBinding binding;

    public DrawerMenuView(Context context) {
        super(context);
        init(null);
    }

    public DrawerMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DrawerMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.drawer_menu_view, this, false);
        addView(view);
        binding = DataBindingUtil.bind(view);
        binding.closeMenuBtn.setOnClickListener(this);
        binding.homeNavBtn.setOnClickListener(this);
        binding.myProfileNavBtn.setOnClickListener(this);
        binding.changePasswordNavBtn.setOnClickListener(this);
        binding.aboutUsNavBtn.setOnClickListener(this);
        binding.privacyPolicyNavBtn.setOnClickListener(this);
        binding.logoutNavBtn.setOnClickListener(this);
        clearInfo();
        updateHeader();
    }

    private void clearInfo() {
        binding.userNameTv.setText("");
        binding.userMobileTv.setText("");
        binding.userEmailTv.setText("");
    }

    public void updateHeader()
    {
        ProfileData profileData = AppSession.getInstance().getProfileData();
        if(profileData!=null)
        {

            binding.userNameTv.setText(profileData.getName()!=null
                    ? profileData.getName() : "");
            binding.userMobileTv.setText(profileData.getContactNumber()!=null ? profileData.getContactNumber() : "");
            binding.userEmailTv.setText(profileData.getEmailAddress()!=null ? profileData.getEmailAddress() : "");
            ImageLoader.getInstance().displayImage(profileData.getProfileImageURL(),binding.userImgIv,AppCommons.getUserImageLoadingOptions());
        }
    }
    private DrawerMenuListener drawerMenuListener;

    public DrawerMenuListener getDrawerMenuListener() {
        return drawerMenuListener;
    }

    public void setDrawerMenuListener(DrawerMenuListener drawerMenuListener) {
        this.drawerMenuListener = drawerMenuListener;
    }

    @Override
    public void onClick(View v) {
        drawerMenuListener.onDrawerMenuItemClick(v.getId());
    }

    public interface DrawerMenuListener {
        void onDrawerMenuItemClick(int item_id);

    }
}
