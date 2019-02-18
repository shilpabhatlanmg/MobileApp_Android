package com.protectapp.util;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.protectapp.R;
import com.protectapp.databinding.IncidentDialogLayoutBinding;
import com.protectapp.model.Incident;
import com.protectapp.model.User;

public class IncidentDialog {
    private Dialog dialog;
    private IncidentDialogLayoutBinding binding;
    public IncidentDialog(Context context,Incident incident) {
        dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        View view = LayoutInflater.from(context).inflate(R.layout.incident_dialog_layout,null,false);
        dialog.setContentView(view);
        binding = DataBindingUtil.bind(view);
        binding.incidentLocationTv.setText(incident.getLocation());
        binding.incidentTimeTv.setText(AppCommons.getDisplayableTime(incident.getTimestamp()));
        binding.incidentImage.setImageResource(getImageResourceByType(incident.getType()));
        User reportedBy = incident.getInitiatedBy();
        boolean isReporterSecurity = reportedBy!=null && reportedBy.getName()!=null;
        binding.reportedByName.setText(reportedBy!=null ? (reportedBy.getName()!=null ? reportedBy.getName() :reportedBy.getContactNumber()) : "");
        binding.reportedByImage.setVisibility(isReporterSecurity ? View.VISIBLE : View.GONE);
        ImageLoader.getInstance().displayImage(reportedBy!=null ? reportedBy.getImageURL() : "",binding.reportedByImage,AppCommons.getUserImageLoadingOptions());

        binding.closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private int getImageResourceByType(int type) {
    switch (type)
    {
        case Constants.INCIDENT.TYPE_ASSIST:return R.drawable.ic_assist_dash;
        case Constants.INCIDENT.TYPE_MEDICAL:return R.drawable.ic_med_dash;
        case Constants.INCIDENT.TYPE_FIRE:return R.drawable.ic_fire_dash;
        case Constants.INCIDENT.TYPE_POLICE:return R.drawable.ic_police_dash;
        default:return R.drawable.ic_assist_dash;
    }
    }

    public void show() {
        try {
            if (dialog == null) return;

            dialog.show();
        } catch (Exception e) {
        }

    }

    public void dismiss() {
        try {
            if (dialog == null) return;
            dialog.dismiss();
        } catch (Exception e) {

        }
    }

    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }
}
