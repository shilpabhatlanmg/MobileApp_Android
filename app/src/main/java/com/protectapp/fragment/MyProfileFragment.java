package com.protectapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.myhexaville.smartimagepicker.ImagePicker;
import com.myhexaville.smartimagepicker.OnImagePickedListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.protectapp.R;
import com.protectapp.databinding.FragmentMyProfileBinding;
import com.protectapp.listener.DashboardFragmentListener;
import com.protectapp.model.GenericResponseModel;
import com.protectapp.model.ProfileData;
import com.protectapp.network.ProtectApiHelper;
import com.protectapp.util.AppCommons;
import com.protectapp.util.AppSession;
import com.protectapp.util.AppToastBuilder;

import java.io.InputStream;
import java.net.URI;

public class MyProfileFragment extends BaseFragment implements View.OnClickListener,OnImagePickedListener {
    private static final int UPDATE_PROFILE_RQ = 201;

    private DashboardFragmentListener mListener;
    private Uri selectedImageUri=null;
    public MyProfileFragment() {
        // Required empty public constructor
    }

    public static MyProfileFragment newInstance(String param1, String param2) {
        MyProfileFragment fragment = new MyProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private FragmentMyProfileBinding binding;
    private ImagePicker imagePicker;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }

    @Override
    public void initUI(View view) {
        binding = DataBindingUtil.bind(view);

        imagePicker = new ImagePicker(getActivity(),
                this,
                this).setWithImageCrop(1,1);

        }

    @Override
    public void setUI() {
        binding.imgPickerBtn.setOnClickListener(this);
        binding.saveButton.setOnClickListener(this);
        ProfileData profileData = AppSession.getInstance().getProfileData();
        binding.nameInputView.setText(profileData!=null ? profileData.getName() : "");
        binding.mobileInputView.setText(profileData!=null ? profileData.getContactNumber() : "");
        binding.emailInputView.setText(profileData!=null ? profileData.getEmailAddress() : "");
        ImageLoader.getInstance().displayImage(profileData!=null ? profileData.getProfileImageURL() : "",binding.userImageIv,AppCommons.getUserImageLoadingOptions());
    }

    @Override
    public void hitAPI(int req_code) {
    switch (req_code)
    {
        case UPDATE_PROFILE_RQ:
            ProtectApiHelper.getInstance().updateUserProfile(AppSession.getInstance().getAccessToken(),
                    binding.nameInputView.getValue(),
                    AppCommons.toBase64(getContext(),selectedImageUri),new ApiCallback<ProfileData>(UPDATE_PROFILE_RQ));
            break;
    }
    }

    @Override
    public <D> void onApiSuccess(GenericResponseModel<D> model, int req_code) {
        switch (req_code)
        {
            case UPDATE_PROFILE_RQ:
                hideActivityProgress();
                if (model.getData() instanceof ProfileData)
                {
                    selectedImageUri=null;
                    AppSession.getInstance().setProfileData((ProfileData) model.getData());
                    mListener.onProfileUpdated();
                    AppCommons.showToast(getContext(),R.string.profile_updated_success_msg);
                }
                break;
        }
    }

    @Override
    public <D> void onApiFail(GenericResponseModel<D> model, int req_code) {
        switch (req_code)
        {
            case UPDATE_PROFILE_RQ:
                hideActivityProgress();
                AppCommons.showError(getContext(),model != null ? model.getMessage() : null);
                break;
        }
    }

    private boolean validate() {
        boolean valid =true;

        if(binding.nameInputView.getValue().isEmpty())
        {
            binding.nameInputView.setError(getString(R.string.error_name_input));
            valid=false;
        }
        return valid;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DashboardFragmentListener) {
            mListener = (DashboardFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DashboardFragmentListener");
        }
    }

    @Override
    public void onDetach() {
          super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.img_picker_btn:imagePicker.choosePicture(true);break;
            case R.id.save_button:
                if(validate())
                {
                    hitAPI(UPDATE_PROFILE_RQ);
                    showActivityProgress(false);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePicker.handleActivityResult(resultCode, requestCode, data);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        imagePicker.handlePermission(requestCode, grantResults);
    }

    @Override
    public void onImagePicked(Uri imageUri) {
        selectedImageUri=imageUri;

        binding.userImageIv.setImageBitmap(AppCommons.getBitmap(getContext(),selectedImageUri));
    }
}
