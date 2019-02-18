package com.protectapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.protectapp.activity.ChatActivity;
import com.protectapp.customview.NMGRecyclerView;
import com.protectapp.databinding.ContactListItemBinding;
import com.protectapp.databinding.FragmentRecentChatsBinding;
import com.protectapp.listener.DashboardFragmentListener;
import com.protectapp.R;
import com.protectapp.model.Contact;
import com.protectapp.model.GenericNotificationData;
import com.protectapp.model.GenericResponseModel;
import com.protectapp.model.GetContactsData;
import com.protectapp.network.ProtectApiHelper;
import com.protectapp.util.AppCommons;
import com.protectapp.util.AppSession;
import com.protectapp.util.Constants;
import com.protectapp.util.GenericRecyclerAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class RecentChatsFragment extends BaseFragment implements GenericRecyclerAdapter.GenericItemBinder,NMGRecyclerView.OnMoreListener,SwipeRefreshLayout.OnRefreshListener {


    private DashboardFragmentListener mListener;
    private List<Contact> recentChats = new ArrayList<>();
    private GenericRecyclerAdapter mAdapter;
    private static final int GET_RECENT_CHAT_RQ = 201;
    private int mPage=0;
    public RecentChatsFragment() {
        // Required empty public constructor
    }

    public static RecentChatsFragment newInstance(String param1, String param2) {
        RecentChatsFragment fragment = new RecentChatsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private FragmentRecentChatsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recent_chats, container, false);
    }

    @Override
    public void initUI(View view) {
        binding = DataBindingUtil.bind(view);

    }

    @Override
    public void setUI() {
        setUpProgressBar(binding.container);
        binding.chatListRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mAdapter=new GenericRecyclerAdapter(R.layout.contact_list_item, recentChats, this);
        binding.chatListRv.setAdapter(mAdapter);
        binding.chatListRv.setLoadMoreProgressView(binding.loadMoreProgressView);
        binding.chatListRv.setOnMoreListener(this);
        binding.chatListRefreshLayout.setOnRefreshListener(this);
        showProgress();
        hitAPI(GET_RECENT_CHAT_RQ);
    }

    @Override
    public void hitAPI(int req_code) {
        switch (req_code) {
            case GET_RECENT_CHAT_RQ:
                ProtectApiHelper.getInstance().getRecentChats(AppSession.getInstance().getAccessToken(),mPage,new ApiCallback<GetContactsData>(GET_RECENT_CHAT_RQ));
                break;
        }
    }

    @Override
    public <D> void onApiSuccess(GenericResponseModel<D> model, int req_code) {
        switch (req_code) {
            case GET_RECENT_CHAT_RQ:
                binding.chatListRefreshLayout.setRefreshing(false);
                if (model.getData() instanceof GetContactsData)
                {
                    if(mPage==0)
                    {
                        recentChats.clear();
                        mAdapter.notifyDataSetChanged();
                        binding.chatListRv.reset();
                        binding.chatListRv.setTotalPages(((GetContactsData) model.getData()).getTotalPages());
                    }
                    addRecentChats(((GetContactsData) model.getData()).getContacts());
                }
                binding.chatListRv.onLoadedMore();
                hideProgress();
                break;
        }
    }

    @Override
    public <D> void onApiFail(GenericResponseModel<D> model, int req_code) {
        switch (req_code) {
            case GET_RECENT_CHAT_RQ:
                binding.chatListRefreshLayout.setRefreshing(false);
                binding.chatListRv.onLoadedMoreFailed();
                hideProgress();
                break;
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
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
        EventBus.getDefault().unregister(this);
        mListener = null;
    }


    @Override
    public void bind(ViewDataBinding itemBinding, int position) {
        if (itemBinding instanceof ContactListItemBinding) {

            ImageLoader.getInstance().displayImage(recentChats.get(position).getProfileImageURL(),
                    ((ContactListItemBinding) itemBinding).userImgIv,
                    AppCommons.getUserImageLoadingOptions());
            ((ContactListItemBinding) itemBinding).userNameTv.setText(recentChats.get(position).getName());
            ((ContactListItemBinding) itemBinding).lastChatTv.setText(recentChats.get(position).getLastMessage());
            ((ContactListItemBinding) itemBinding).chatUnreadBadgeTv.setText(String.valueOf(recentChats.get(position).getUnreadMessages()));
            ((ContactListItemBinding) itemBinding).chatUnreadBadgeTv.setVisibility(recentChats.get(position).getUnreadMessages() > 0 ? View.VISIBLE : View.GONE);
            ((ContactListItemBinding) itemBinding).lastChatTimeTv.setVisibility(recentChats.get(position).getTimestampOfLastMessage()!=null? View.VISIBLE : View.GONE);
            ((ContactListItemBinding) itemBinding).lastChatTimeTv.setText(AppCommons.getChatDateFlag(recentChats.get(position).getTimestampOfLastMessage()));

        }
    }

    @Override
    public void onMoreAsked(int page) {
        if(!this.binding.chatListRefreshLayout.isRefreshing())
        {
            mPage=page;
            hitAPI(GET_RECENT_CHAT_RQ);
        }
    }
    public void addRecentChats(List<Contact> incomingRecentChats) {

        int start_index = this.recentChats.size();
        recentChats.addAll(incomingRecentChats);
        mAdapter.notifyItemRangeInserted(start_index, recentChats.size());
    }

    @Override
    public void onItemClick(int position) {
        startActivityForResult(new Intent(getContext(),ChatActivity.class).putExtra(Constants.EXTRA.CONTACT,recentChats.get(position)),Constants.ACTIVITY_RQ.CHAT_UPDATE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case Constants.ACTIVITY_RQ.CHAT_UPDATE:
                if (resultCode==RESULT_OK && data!=null)
                {
                    Contact updatedContact = (Contact) data.getSerializableExtra(Constants.EXTRA.CONTACT);
                    updateChatList(updatedContact);
                }
                break;
            case Constants.ACTIVITY_RQ.SELECT_MEMBER:
                if (resultCode==RESULT_OK && data!=null)
                {
                    Contact incomingContact = (Contact) data.getSerializableExtra(Constants.EXTRA.CONTACT);
                    updateChatList(incomingContact);
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotification(GenericNotificationData<Object> event) {
        if(event.getPayload() instanceof Contact)
        {
            updateChatList((Contact) event.getPayload());
        }
    }


    private void updateChatList(Contact updatedContact) {
        int index = recentChats.indexOf(updatedContact);
        if(index>=0)
        {
            recentChats.set(index,updatedContact);
            mAdapter.notifyItemChanged(index);
        }
        else
        {
            recentChats.add(0,updatedContact);
            mAdapter.notifyItemInserted(0);
        }
    }

    @Override
    public void onRefresh() {
        mPage=0;
        hitAPI(GET_RECENT_CHAT_RQ);
    }
}
