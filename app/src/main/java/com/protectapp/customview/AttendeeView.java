package com.protectapp.customview;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.protectapp.R;
import com.protectapp.databinding.AttendeeListItemBinding;
import com.protectapp.databinding.AttendeeViewBinding;
import com.protectapp.model.User;
import com.protectapp.util.AppCommons;
import com.protectapp.util.EnglishNumberToWords;
import com.protectapp.util.GenericRecyclerAdapter;

import java.util.List;

import static com.protectapp.util.Constants.MAX_ATTENDEE_IMAGES;

public class AttendeeView extends FrameLayout implements View.OnClickListener {
    private List<User> attendeeList;
    private GenericRecyclerAdapter attendeeListAdapter;
    private boolean isExpanded=false;
    private int reportId;
    public AttendeeView(Context context) {
        super(context);
        init();
    }

    public AttendeeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AttendeeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setAttendeeList(List<User> attendeeList) {
        if (attendeeList == null) return;
        this.attendeeList = attendeeList;
        this.attendeeListAdapter.setData(this.attendeeList);
        this.attendeeListAdapter.notifyDataSetChanged();
        binding.attendeeImagesLayout.removeAllViews();

        if (this.attendeeList.size() == 0) {
            setVisibility(GONE);
            return;
        } else {
            setVisibility(VISIBLE);
        }
        LayoutInflater inflater = LayoutInflater.from(getContext());
        String moreText = "";
        for (int i = 0; i < attendeeList.size(); i++) {
            if (i == MAX_ATTENDEE_IMAGES) break;

            FrameLayout attendeeImageFrame;

            if (i == MAX_ATTENDEE_IMAGES - 1 && attendeeList.size() > MAX_ATTENDEE_IMAGES) {
                attendeeImageFrame = (FrameLayout) inflater.inflate(R.layout.more_attendee_image_view, binding.attendeeImagesLayout, false);
                ((TextView) attendeeImageFrame.findViewById(R.id.more_attendee_count_tv)).setText(String.format("+%d", attendeeList.size() - MAX_ATTENDEE_IMAGES));

            } else {
                attendeeImageFrame = (FrameLayout) inflater.inflate(R.layout.attendee_image_view, binding.attendeeImagesLayout, false);
            }
            ImageLoader.getInstance().displayImage(attendeeList.get(i).getImageURL(),
                    (ImageView)attendeeImageFrame.findViewById(R.id.attendee_image_iv),AppCommons.getUserImageLoadingOptions());
            ((LinearLayout.LayoutParams) attendeeImageFrame.getLayoutParams())
                    .setMargins(i > 0 ? getResources().getDimensionPixelOffset(R.dimen.attendee_overlap_margin) : 0, 0, 0, 0);

            binding.attendeeImagesLayout.addView(attendeeImageFrame);

        }
        if (attendeeList.size() > 1)
            moreText = " and " + EnglishNumberToWords.convert(attendeeList.size() - 1) + " more";


        binding.attendeeNamesTv.setText(attendeeList.get(0).getName() + moreText);


    }

    private AttendeeViewBinding binding;

    private void init() {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.attendee_view, this, false);
        addView(view);
        binding = DataBindingUtil.bind(view);
        collapseAttendeeList();
        binding.attendeeShortList.setOnClickListener(this);
        binding.lessBtn.setOnClickListener(this);
        binding.attendeeListRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        attendeeListAdapter = new GenericRecyclerAdapter(R.layout.attendee_list_item, attendeeList, new GenericRecyclerAdapter.GenericItemBinder() {
            @Override
            public void bind(ViewDataBinding itemBinding, int position) {
                if(itemBinding instanceof AttendeeListItemBinding)
                {
                    ((AttendeeListItemBinding) itemBinding).attendeeNameTv.setText(attendeeList.get(position).getName());
                    ImageLoader.getInstance().displayImage(attendeeList.get(position).getImageURL(),
                            ((AttendeeListItemBinding) itemBinding).attendeeImageIv, AppCommons.getUserImageLoadingOptions());
                }

            }

            @Override
            public void onItemClick(int position) {

            }
        });
        binding.attendeeListRv.setAdapter(attendeeListAdapter);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.attendee_short_list:
                if (attendeeViewListener != null && attendeeList.size()>1)
                    attendeeViewListener.onExpand();
                break;
            case R.id.less_btn:
                if (attendeeViewListener != null)
                    attendeeViewListener.onCollapse();
                break;
        }
    }

    public void toggle(boolean doExpand) {
        if(isExpanded==doExpand)
            return;
        isExpanded=doExpand;
        if(doExpand)
            expandAttendeeList();
        else
            collapseAttendeeList();
    }


    private void expandAttendeeList() {

        binding.attendeeShortList.setVisibility(GONE);
        binding.attendeeListRv.setVisibility(VISIBLE);
        binding.lessBtn.setVisibility(VISIBLE);

    }

    private void collapseAttendeeList() {

        binding.attendeeListRv.setVisibility(GONE);
        binding.attendeeShortList.setVisibility(VISIBLE);
        binding.lessBtn.setVisibility(GONE);
    }

    private AttendeeViewListener attendeeViewListener;

    public AttendeeViewListener getAttendeeViewListener() {
        return attendeeViewListener;
    }

    public void setAttendeeViewListener(AttendeeViewListener attendeeViewListener) {
        this.attendeeViewListener = attendeeViewListener;
    }


    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public interface AttendeeViewListener {
        void onExpand();

        void onCollapse();
    }

}
