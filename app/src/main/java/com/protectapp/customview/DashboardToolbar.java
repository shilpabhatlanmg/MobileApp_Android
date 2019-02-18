package com.protectapp.customview;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.protectapp.R;
import com.protectapp.databinding.DashboardToolbarBinding;
import com.protectapp.fragment.AboutUsFragment;
import com.protectapp.fragment.AidHistoryFragment;
import com.protectapp.fragment.ChangePasswordFragment;
import com.protectapp.fragment.RecentChatsFragment;
import com.protectapp.fragment.HomeFragment;
import com.protectapp.fragment.MyProfileFragment;
import com.protectapp.fragment.PrivacyPolicyFragment;
import com.protectapp.model.GetBadgeCountData;
import com.protectapp.util.AppSession;

import java.util.HashMap;
import java.util.Map;

public class DashboardToolbar extends FrameLayout {
    private Map<Class,ToolbarModel> toolbarMap = new HashMap<>();
    public DashboardToolbar(Context context) {
        super(context);
        init();
    }

    public DashboardToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DashboardToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private DashboardToolbarBinding binding;

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dashboard_toolbar, this, false);
        addView(view);
        binding = DataBindingUtil.bind(view);
        toolbarMap.put(HomeFragment.class,new ToolbarModel(GONE,VISIBLE,VISIBLE,Color.TRANSPARENT,""));
        toolbarMap.put(RecentChatsFragment.class,new ToolbarModel(VISIBLE,GONE,GONE,Color.WHITE,getContext().getString(R.string.chat)));
        toolbarMap.put(AidHistoryFragment.class,new ToolbarModel(GONE,GONE,GONE,Color.WHITE,getContext().getString(R.string.aid_history)));
        toolbarMap.put(ChangePasswordFragment.class,new ToolbarModel(GONE,GONE,GONE,Color.WHITE,getContext().getString(R.string.change_password)));
        toolbarMap.put(MyProfileFragment.class,new ToolbarModel(GONE,GONE,GONE,Color.WHITE,getContext().getString(R.string.my_profile)));
        toolbarMap.put(PrivacyPolicyFragment.class,new ToolbarModel(GONE,GONE,GONE,Color.WHITE,getContext().getString(R.string.privacy_policy)));
        toolbarMap.put(AboutUsFragment.class,new ToolbarModel(GONE,GONE,GONE,Color.WHITE,getContext().getString(R.string.about_protect_app)));
    }

    public void setOnActionItemClickListener(View.OnClickListener actionItemClickListener) {

        binding.menuBtn.setOnClickListener(actionItemClickListener);
        binding.chatBtn.setOnClickListener(actionItemClickListener);
        binding.calendarBtn.setOnClickListener(actionItemClickListener);
        binding.addBtn.setOnClickListener(actionItemClickListener);
    }

    public void adjustToolbarFor(Class fragment) {
        if (fragment == null) return;
           ToolbarModel toolbarModel  = toolbarMap.get(fragment);
            if (toolbarModel!=null)
            {
                setBackgroundColor(toolbarModel.toolbarColor);
                binding.toolbarTitleTv.setText(toolbarModel.toolbarTitle);
                binding.addBtn.setVisibility(toolbarModel.addButtonVisibility);
                binding.chatBtn.setVisibility(toolbarModel.chatButtonVisibility);
                binding.calendarBtn.setVisibility(toolbarModel.calendarButtonVisibility);

            }
    }

    private class ToolbarModel
    {
        private int addButtonVisibility;
        private int chatButtonVisibility;
        private int calendarButtonVisibility;
        private int toolbarColor;
        private String toolbarTitle;

        public ToolbarModel(int addButtonVisibility, int chatButtonVisibility, int calendarButtonVisibility, int toolbarColor, String toolbarTitle) {
            this.addButtonVisibility = addButtonVisibility;
            this.chatButtonVisibility = chatButtonVisibility;
            this.calendarButtonVisibility = calendarButtonVisibility;
            this.toolbarColor = toolbarColor;
            this.toolbarTitle = toolbarTitle;
        }

    }

    public void updateBadges() {
        GetBadgeCountData badgeCountData = AppSession.getInstance().getBadgeCountData();
        if(badgeCountData!=null)
        {
            binding.chatBtn.setBadgeCount(badgeCountData.getChatBadgeCount());
            binding.calendarBtn.setBadgeCount(badgeCountData.getIncidentBadgeCount());
        }
    }
}
