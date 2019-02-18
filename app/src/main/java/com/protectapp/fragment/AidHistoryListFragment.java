package com.protectapp.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.protectapp.R;
import com.protectapp.customview.AttendeeView;
import com.protectapp.customview.NMGRecyclerView;
import com.protectapp.databinding.DateHeaderItemBinding;
import com.protectapp.databinding.FragmentAidHistoryListingBinding;
import com.protectapp.databinding.IncidentListItemBinding;
import com.protectapp.listener.DashboardFragmentListener;
import com.protectapp.model.GenericResponseModel;
import com.protectapp.model.GetIncidentsData;
import com.protectapp.model.Incident;
import com.protectapp.model.User;
import com.protectapp.network.ProtectApiHelper;
import com.protectapp.util.AppCommons;
import com.protectapp.util.AppLinearLayoutManager;
import com.protectapp.util.AppSession;
import com.protectapp.util.Constants;
import com.protectapp.util.GenericRecyclerAdapter;
import com.protectapp.util.IncidentDialog;

import java.util.ArrayList;
import java.util.List;

public class AidHistoryListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,NMGRecyclerView.OnMoreListener {
    private static final String INCIDENT_LIST_TYPE = "incident_list_type";
    public static final int GET_INCIDENTS_RQ = 201;
    private AppLinearLayoutManager linearLayoutManager;
    private DashboardFragmentListener mListener;
    private AidHistoryListAdapter mAdapter;
    private String incidentListType;
    private List<Incident> incidentList = new ArrayList<>();
    private int mPage = 0;

    public AidHistoryListFragment() {
        // Required empty public constructor
    }

    public static AidHistoryListFragment newInstance(String IncidentListType) {
        AidHistoryListFragment fragment = new AidHistoryListFragment();
        Bundle args = new Bundle();
        args.putString(INCIDENT_LIST_TYPE, IncidentListType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            incidentListType = getArguments().getString(INCIDENT_LIST_TYPE, Constants.INCIDENT.TODAY);
        }
    }

    private FragmentAidHistoryListingBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_aid_history_listing, container, false);
    }

    @Override
    public void initUI(View view) {
        binding = DataBindingUtil.bind(view);
        setUpProgressBar(binding.container);
    }

    @Override
    public void setUI() {
        linearLayoutManager = new AppLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.incidentListRv.setLayoutManager(linearLayoutManager);
        ((SimpleItemAnimator) binding.incidentListRv.getItemAnimator()).setSupportsChangeAnimations(false);
        mAdapter = new AidHistoryListAdapter();
        binding.incidentListRv.setAdapter(mAdapter);
        binding.incidentListRv.setOnMoreListener(this);
        binding.incidentListRv.setLoadMoreProgressView(binding.loadMoreProgressView);
        binding.incidentListRefreshLayout.setOnRefreshListener(this);
        //mAdapter.setIncidentList(incidentList);
         binding.incidentListRv.showLoading();
        showProgress();
        hitAPI(GET_INCIDENTS_RQ);
    }

    @Override
    public void hitAPI(int req_code) {
        switch (req_code) {
            case GET_INCIDENTS_RQ:
                ProtectApiHelper.getInstance().getIncidents(AppSession.getInstance().getAccessToken(), incidentListType, 0, new ApiCallback<GetIncidentsData>(GET_INCIDENTS_RQ));
                break;
        }
    }

    @Override
    public <D> void onApiSuccess(GenericResponseModel<D> model, int req_code) {
        switch (req_code) {
            case GET_INCIDENTS_RQ:
                binding.incidentListRefreshLayout.setRefreshing(false);
                if (model.getData() instanceof GetIncidentsData) {
                    if (incidentListType.equals(Constants.INCIDENT.TODAY) && mListener != null)
                        mListener.incidentListLoaded();
                    if(mPage==0)
                    {
                        incidentList.clear();
                        mAdapter.notifyDataSetChanged();
                        binding.incidentListRv.reset();
                        binding.incidentListRv.setTotalPages(((GetIncidentsData) model.getData()).getTotalPages());
                    }
                    List<Incident> upcomingIncidentList = ((GetIncidentsData) model.getData()).getReports();
                    Incident lastIncident = incidentList.size() == 0 ? null : incidentList.get(incidentList.size() - 1);
                    addIncidentList(incidentListType.equals(Constants.INCIDENT.OTHERS) ?
                            AppCommons.processIncidentListWithDateHeader(lastIncident, upcomingIncidentList) :
                            AppCommons.processIncidentList(upcomingIncidentList)
                    );
                }
                hideProgress();
                binding.incidentListRv.onLoadedMore();

                break;
        }
    }

    @Override
    public <D> void onApiFail(GenericResponseModel<D> model, int req_code) {
        switch (req_code) {
            case GET_INCIDENTS_RQ:
                hideProgress();
                binding.incidentListRefreshLayout.setRefreshing(false);
                binding.incidentListRv.onLoadedMoreFailed();
                break;
        }
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
    public void onMoreAsked(int page) {
        if(!this.binding.incidentListRefreshLayout.isRefreshing())
        {
            mPage = page;
            hitAPI(GET_INCIDENTS_RQ);

        }
    }
    @Override
    public void onRefresh() {
        mPage=0;
        hitAPI(GET_INCIDENTS_RQ);
    }

    private class AidHistoryListAdapter extends RecyclerView.Adapter<AidHistoryListAdapter.BaseViewHolder> {
        private static final int DATE_HEADER_VIEW = 101;
        private static final int DEFAULT_ITEM_VIEW = 102;


        @NonNull
        @Override
        public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            return getViewHolder(viewGroup, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder viewHolder, int position) {
            viewHolder.bind();
        }

        @Override
        public int getItemViewType(int position) {
            return incidentList.get(position).isDateHeader() ? DATE_HEADER_VIEW : DEFAULT_ITEM_VIEW;
        }

        @Override
        public int getItemCount() {
            return incidentList.size();
        }

        private BaseViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            return
                    viewType == DEFAULT_ITEM_VIEW ?
                            new IncidentItemViewHolder(inflater.inflate(R.layout.incident_list_item, viewGroup, false)) :
                            new DateHeaderViewHolder(inflater.inflate(R.layout.date_header_item, viewGroup, false));
        }


        abstract class BaseViewHolder extends RecyclerView.ViewHolder {
            abstract void bind();

            public BaseViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }

        class IncidentItemViewHolder extends BaseViewHolder implements AttendeeView.AttendeeViewListener {
            private IncidentListItemBinding binding;

            public IncidentItemViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = DataBindingUtil.bind(itemView);
                binding.attendeeView.setAttendeeViewListener(this);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new IncidentDialog(getContext(), incidentList.get(getAdapterPosition())).show();
                    }
                });
            }

            @Override
            void bind() {
                int incidentType = incidentList.get(getAdapterPosition()).getType();
                String incidentLocation = incidentList.get(getAdapterPosition()).getLocation();
                String incidentTime = incidentList.get(getAdapterPosition()).getDisplayableTime();
                binding.incidentTypeTv.setText(AppCommons.getIncidentTypeName(getContext(), incidentType));
                if (incidentList.get(getAdapterPosition()).getReportID() != binding.attendeeView.getReportId())
                    binding.attendeeView.setAttendeeList(incidentList.get(getAdapterPosition()).getAttendedBy());
                binding.attendeeView.setReportId(incidentList.get(getAdapterPosition()).getReportID());
                itemView.setBackgroundResource(AppCommons.getIncidentColorBGRes(incidentType));
                binding.incidentLocationTv.setText(incidentLocation);
                binding.incidentDateTv.setText(incidentTime);
                binding.attendeeView.toggle(incidentList.get(getAdapterPosition()).isExpanded());

            }

            @Override
            public void onExpand() {
                incidentList.get(getAdapterPosition()).setExpanded(true);
                notifyItemChanged(getAdapterPosition());
            }

            @Override
            public void onCollapse() {
                incidentList.get(getAdapterPosition()).setExpanded(false);
                notifyItemChanged(getAdapterPosition());
            }
        }

        class DateHeaderViewHolder extends BaseViewHolder {
            private DateHeaderItemBinding binding;

            public DateHeaderViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = DataBindingUtil.bind(itemView);
            }

            @Override
            void bind() {
                binding.dateHeaderTv.setText(incidentList.get(getAdapterPosition()).getDisplayableDate());
            }
        }

    }

    public void addIncidentList(List<Incident> incomingIncidentList) {

        int start_index = incidentList.size();
        incidentList.addAll(incomingIncidentList);
        mAdapter.notifyItemRangeInserted(start_index, incidentList.size());
    }

    public interface AidHistoryListFragmentListener
    {

    }
}
