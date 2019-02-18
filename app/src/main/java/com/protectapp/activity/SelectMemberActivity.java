package com.protectapp.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.protectapp.R;
import com.protectapp.customview.NMGRecyclerView;
import com.protectapp.databinding.ActivitySelectMemberBinding;
import com.protectapp.databinding.ContactListItemBinding;
import com.protectapp.model.Contact;
import com.protectapp.model.GenericResponseModel;
import com.protectapp.model.GetContactsData;
import com.protectapp.network.ProtectApiHelper;
import com.protectapp.util.AppCommons;
import com.protectapp.util.AppSession;
import com.protectapp.util.AppToastBuilder;
import com.protectapp.util.Constants;
import com.protectapp.util.GenericRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SelectMemberActivity extends BaseActivity implements View.OnClickListener,GenericRecyclerAdapter.GenericItemBinder, NMGRecyclerView.OnMoreListener {
    private ActivitySelectMemberBinding binding;
    private List<Contact> memberList = new ArrayList<>();
    private GenericRecyclerAdapter mAdapter;
    private static final int GET_CONTACTS_RQ = 201;
    int mPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_member);
        transparentStatusBar();
    }

    @Override
    public void getExtras() {

    }

    @Override
    public void initUI() {
    }

    @Override
    public void setUI() {
        binding.toolbar.setBackPressListener(this);
        binding.toolbar.setTitle(R.string.select_member);
        binding.memberList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new GenericRecyclerAdapter(R.layout.contact_list_item, memberList, this);
        binding.memberList.setAdapter(mAdapter);
        binding.memberList.setOnMoreListener(this);
        binding.memberList.setLoadMoreProgressView(binding.loadMoreProgressView);
        showProgress(true);
        hitAPI(GET_CONTACTS_RQ);
    }

    @Override
    public void hitAPI(int req_code) {
        switch (req_code) {
            case GET_CONTACTS_RQ:
                ProtectApiHelper.getInstance().getContacts(AppSession.getInstance().getAccessToken(), mPage,
                        new ApiCallback<GetContactsData>(GET_CONTACTS_RQ));
                break;
        }
    }

    @Override
    public <D> void onApiSuccess(GenericResponseModel<D> model, int req_code) {
        switch (req_code) {
            case GET_CONTACTS_RQ:
                if (model.getData() instanceof GetContactsData) {
                    binding.memberList.setTotalPages(((GetContactsData) model.getData()).getTotalPages());
                    addMemberList(((GetContactsData) model.getData()).getContacts());
                }
                binding.memberList.onLoadedMore();
                hideProgress();
                break;
        }
    }

    @Override
    public <D> void onApiFail(GenericResponseModel<D> model, int req_code) {
        switch (req_code) {
            case GET_CONTACTS_RQ:
                binding.memberList.onLoadedMoreFailed();
                hideProgress();
                AppCommons.showError(this,model!=null ? model.getMessage() : null);
                break;
        }
    }

    @Override
    public void bind(ViewDataBinding itemBinding, int position) {
        if (itemBinding instanceof ContactListItemBinding) {

            ImageLoader.getInstance().displayImage(memberList.get(position).getProfileImageURL(),
                    ((ContactListItemBinding) itemBinding).userImgIv,
                    AppCommons.getUserImageLoadingOptions());
            ((ContactListItemBinding) itemBinding).userNameTv.setText(memberList.get(position).getName());
            ((ContactListItemBinding) itemBinding).lastChatTv.setText(memberList.get(position).getLastMessage() != null ?
                    memberList.get(position).getLastMessage() : "");
            ((ContactListItemBinding) itemBinding).chatUnreadBadgeTv.setVisibility(View.GONE);
        }

    }

    @Override
    public void onMoreAsked(int page) {
        mPage=page;
        hitAPI(GET_CONTACTS_RQ);
    }

    public void addMemberList(List<Contact> incomingMemberList) {

        int start_index = this.memberList.size();
        memberList.addAll(incomingMemberList);
        mAdapter.notifyItemRangeInserted(start_index, memberList.size());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.back_btn:finish();
        }
    }

    @Override
    public void onItemClick(int position) {
        startActivityForResult(new Intent(this,ChatActivity.class).putExtra(Constants.EXTRA.CONTACT,memberList.get(position)),Constants.ACTIVITY_RQ.SELECT_MEMBER);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case Constants.ACTIVITY_RQ.SELECT_MEMBER:
                if(resultCode==RESULT_OK)
                {

                    setResult(data.getBooleanExtra(Constants.EXTRA.DID_SENT_MESSAGE,false) ?  RESULT_OK : RESULT_CANCELED,data);
                    finish();
                }
                break;
        }
    }
}
