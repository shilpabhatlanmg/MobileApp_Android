package com.protectapp.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.protectapp.R;
import com.protectapp.databinding.ActivityChatBinding;
import com.protectapp.databinding.ChatMessageLayoutBinding;
import com.protectapp.model.ChatModel;
import com.protectapp.model.Contact;
import com.protectapp.model.GenericResponseModel;
import com.protectapp.model.ProfileData;
import com.protectapp.network.ProtectApiHelper;
import com.protectapp.util.AppCommons;
import com.protectapp.util.AppSession;
import com.protectapp.util.Constants;

public class ChatActivity extends BaseActivity implements View.OnClickListener {
    private ActivityChatBinding binding;
    private Contact contact;
    private DatabaseReference mFirebaseRef;
    private FirebaseRecyclerAdapter mAdapter;
    private ProfileData profile;
    private String lastMessage="";
    private static final int UPDATE_LAST_MESSAGE_RQ=201;
    private static final int RESET_UNREAD_CHAT_RQ=202;
    boolean isLoaded=false;
    boolean sentAnyMessage=false;
    @Override
    public void setContentView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        transparentStatusBar();
    }

    @Override
    public void getExtras() {
        profile=AppSession.getInstance().getProfileData();
        contact = (Contact) getIntent().getSerializableExtra(Constants.EXTRA.CONTACT);
    }

    @Override
    public void initUI() {


    }

    @Override
    public void setUI() {
        if (contact == null) return;
        binding.toolbar.setMenuItemClickListener(this);
        binding.toolbar.addMenuItem(R.drawable.ic_phone);
        binding.toolbar.setBackPressListener(this);
        binding.toolbar.setTitle(contact.getName());
        binding.sendChatBtn.setOnClickListener(this);
        showProgress(true);
        setUpFirebaseChat();

    }

    private void setUpFirebaseChat() {
        mFirebaseRef = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_CHAT_KEY).child(contact.getNodeIdentifier());
        FirebaseRecyclerOptions<ChatModel> options =
                new FirebaseRecyclerOptions.Builder<ChatModel>()
                        .setQuery(mFirebaseRef.limitToLast(Constants.FIREBASE_CHAT_LIMIT), ChatModel.class)
                        .build();
        mAdapter = new FirebaseRecyclerAdapter<ChatModel, ChatHolder>(options)
        {
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if(!isLoaded)
                {
                    hideProgress();
                    resetUnreadChat();
                }
                isLoaded=true;
                int prevItemPosition =getItemCount()-2;
                if(prevItemPosition>=0)
                notifyItemChanged(prevItemPosition,false);
                if(mAdapter.getItemCount()>0)
                {
                    contact.setLastMessage(((ChatModel)mAdapter.getItem(getItemCount()-1)).getChatMessage());
                    contact.setTimestampOfLastMessage(AppCommons.toWebDate(((ChatModel)mAdapter.getItem(getItemCount()-1)).getTimeStampLong()));
                }
            }

            @NonNull
            @Override
            public ChatHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new ChatHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_message_layout, viewGroup,
                        false));
            }

            @Override
            protected void onBindViewHolder(@NonNull ChatHolder holder, int position, @NonNull ChatModel model) {
                holder.bind(model);
            }

            @Override
            public void onError(@NonNull DatabaseError error) {
                super.onError(error);
            }
        };

        binding.chatListRv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        ((SimpleItemAnimator)binding.chatListRv.getItemAnimator()).setSupportsChangeAnimations(false);
        binding.chatListRv.setAdapter(mAdapter);
        mAdapter.registerAdapterDataObserver(chatObserver);
    }

    private void resetUnreadChat() {
        int currentBadgeCount = AppSession.getInstance().getBadgeCountData().getChatBadgeCount();
        AppSession.getInstance().getBadgeCountData().setChatBadgeCount(currentBadgeCount-contact.getUnreadMessages());
        contact.setUnreadMessages(0);
        hitAPI(RESET_UNREAD_CHAT_RQ);
    }

    @Override
    public void hitAPI(int req_code) {
        switch (req_code)
        {
            case UPDATE_LAST_MESSAGE_RQ:
                ProtectApiHelper.getInstance().updateLastMessage(AppSession.getInstance().getAccessToken(),contact.getUserID(),lastMessage,null);break;
            case RESET_UNREAD_CHAT_RQ:
                ProtectApiHelper.getInstance().resetUnreadChat(AppSession.getInstance().getAccessToken(),contact.getUserID(),null);
                break;
        }
    }

    @Override
    public <D> void onApiSuccess(GenericResponseModel<D> model, int req_code) {

    }

    @Override
    public <D> void onApiFail(GenericResponseModel<D> model, int req_code) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.drawable.ic_phone:
                AppCommons.callUser(this,contact.getContactNumber());
                break;
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.send_chat_btn:
                sendMessage(binding.chatMsgEt.getText().toString());
                break;
        }
    }
    private void sendMessage(String msg)
    {
        if (msg.trim().isEmpty())return;
        binding.chatMsgEt.setText("");
        lastMessage=msg;
        ChatModel chat = new ChatModel(AppSession.getInstance().getProfileData().getUserID(),
                AppSession.getInstance().getProfileData().getName(),
                msg
                );
        sentAnyMessage=true;
        mFirebaseRef.push().setValue(chat);
        hitAPI(UPDATE_LAST_MESSAGE_RQ);
    }
    public class ChatHolder extends RecyclerView.ViewHolder {
    private ChatMessageLayoutBinding binding;
        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
        public void bind(ChatModel model)
        {
            boolean isCurrentUserChat = model.getSenderID()==profile.getUserID();
            boolean topNewChat =getAdapterPosition()==0 ||
                    ((ChatModel) mAdapter.getItem(getAdapterPosition()-1)).getSenderID()!=model.getSenderID();
            boolean bottomNewChat = getAdapterPosition()==mAdapter.getItemCount()-1
                    || ((ChatModel) mAdapter.getItem(getAdapterPosition()+1)).getSenderID()!=model.getSenderID();

            binding.chatMsgTv.setText(model.getChatMessage());
            binding.chatMsgTv.setBackgroundResource(
                    isCurrentUserChat ? (bottomNewChat ? R.drawable.chat_bg_green_sharp : R.drawable.chat_bg_green):
                            (bottomNewChat ? R.drawable.chat_bg_white_sharp : R.drawable.chat_bg_white));
            int topMargin = getResources().getDimensionPixelSize(topNewChat ? R.dimen.new_chat_item_margin : R.dimen.default_chat_item_margin);
            int bottomMargin = getResources().getDimensionPixelSize(bottomNewChat ? R.dimen.new_chat_item_margin : R.dimen.default_chat_item_margin);
            binding.chatMsgTv.setTextColor(ContextCompat.getColor(ChatActivity.this,isCurrentUserChat?
                    R.color.colorWhite : R.color.colorTxtBlack));
            ((ConstraintLayout.LayoutParams)binding.chatMsgContainer.getLayoutParams()).horizontalBias=isCurrentUserChat ? 1 :0;
            ((ViewGroup.MarginLayoutParams)binding.container.getLayoutParams()).setMargins(0,topMargin,0,bottomMargin);
            binding.chatDateTv.setText(AppCommons.getChatDateFlagWithTime(model.getTimeStampLong()));
            binding.chatDateTv.setTextColor(ContextCompat.getColor(ChatActivity.this,isCurrentUserChat?
                    R.color.colorTxtLLGray : R.color.colorTxtGray));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
        if(contact!=null)
        AppSession.getInstance().setCurrentChatUserId(contact.getUserID());
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
        hitAPI(RESET_UNREAD_CHAT_RQ);
        AppSession.getInstance().resetCurrentChatUserId();
    }
        private RecyclerView.AdapterDataObserver chatObserver = new RecyclerView.AdapterDataObserver()
    {
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            binding.chatListRv.post(new Runnable() {
                @Override
                public void run() {
                    binding.chatListRv.scrollToPosition(mAdapter.getItemCount()-1);
                }
            });


        }
    };

    @Override
    public void onBackPressed() {

        setResult(RESULT_OK,new Intent().putExtra(Constants.EXTRA.CONTACT,contact)
                .putExtra(Constants.EXTRA.DID_SENT_MESSAGE,sentAnyMessage));
        super.onBackPressed();
    }
}