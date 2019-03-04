package com.protectapp.fragment;

import android.animation.Animator;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.protectapp.R;
import com.protectapp.databinding.FragmentHomeBinding;
import com.protectapp.listener.DashboardFragmentListener;
import com.protectapp.model.BeaconEvent;
import com.protectapp.model.GenericResponseModel;
import com.protectapp.model.GetLocationData;
import com.protectapp.network.ProtectApiHelper;
import com.protectapp.util.AnimationListener;
import com.protectapp.util.AppCommons;
import com.protectapp.util.AppSession;
import com.protectapp.util.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class HomeFragment extends BaseFragment {
    private static final int GET_LOCATION_RQ=201;
    private DashboardFragmentListener mListener;
    private int beaconStatus=Constants.BEACON_STATUS.SEARCHING;
    private BeaconEvent beaconEvent=null;
    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(Bundle bundle) {
        HomeFragment fragment = new HomeFragment();;
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            beaconEvent = (BeaconEvent) getArguments().getSerializable(Constants.EXTRA.BEACON_EVENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    private FragmentHomeBinding binding;

    @Override
    public void initUI(View view) {
        binding = DataBindingUtil.bind(view);
    }

    @Override
    public void setUI() {
        binding.policeAlarmBtn.setOnTouchListener(buttonTouchListener);
        binding.fireAlarmBtn.setOnTouchListener(buttonTouchListener);
        binding.medicalAlarmBtn.setOnTouchListener(buttonTouchListener);
        binding.assistAlarmBtn.setOnTouchListener(buttonTouchListener);
        processBeaconEvent();
        binding.appVersionTv.setText("v"+AppCommons.getVersionName(getContext()));
    }

    @Override
    public void hitAPI(int req_code) {
        switch (req_code)
        {
            case GET_LOCATION_RQ:
                if(beaconEvent!=null)
                ProtectApiHelper.getInstance().getLocation(AppSession.getInstance().getAccessToken(),
                        beaconEvent.getMajorID(),beaconEvent.getMinorID(),
                        new ApiCallback<GetLocationData>(GET_LOCATION_RQ));
                break;
        }
    }

    @Override
    public <D> void onApiSuccess(GenericResponseModel<D> model, int req_code) {
        switch (req_code)
        {
            case GET_LOCATION_RQ:
                if(model.getData() instanceof GetLocationData)
                {

                    binding.currentLocationTv.setText(AppCommons.getLocationDisplayName((GetLocationData) model.getData()));
                    setBeaconStatus(Constants.BEACON_STATUS.UPDATED);
                    mListener.onLocationLoaded((GetLocationData) model.getData());
                }
                else
                {
                   hitAPI(GET_LOCATION_RQ);
                }
                break;
        }
    }

    @Override
    public <D> void onApiFail(GenericResponseModel<D> model, int req_code) {
        switch (req_code)
        {
            case GET_LOCATION_RQ:
                 hitAPI(GET_LOCATION_RQ);
            break;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
        if (context instanceof DashboardFragmentListener) {
            mListener = (DashboardFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
        mListener = null;
    }

    private View.OnTouchListener buttonTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).setListener(null).start();
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100).setListener(new AnimationListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        handleIncidentButtonClick(v);

                    }
                }).start();
                return true;
            }
            return false;
        }
    };

    private void  handleIncidentButtonClick(View view)
    {

        mListener.reportIncident(getIncidentType(view.getId()));

    }

    private int getIncidentType(int resId)
    {
        int type=0;
        switch (resId) {
            case R.id.fire_alarm_btn:
                type = Constants.INCIDENT.TYPE_FIRE;
                break;
            case R.id.assist_alarm_btn:
                type = Constants.INCIDENT.TYPE_ASSIST;
                break;
            case R.id.police_alarm_btn:
                type = Constants.INCIDENT.TYPE_POLICE;
                break;
            case R.id.medical_alarm_btn:
                type = Constants.INCIDENT.TYPE_MEDICAL;
                break;
        }
        return type;
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBeaconEvent(BeaconEvent event) {

        beaconEvent=event;
        processBeaconEvent();

         }

    private void processBeaconEvent() {

        if (beaconEvent!=null && beaconEvent.getMajorID()!=null)
        {
            //Beacon found, Updating
            setBeaconStatus(Constants.BEACON_STATUS.UPDATING);
            hitAPI(GET_LOCATION_RQ);
        }
        else
        {
            //Searching
            setBeaconStatus(Constants.BEACON_STATUS.SEARCHING);

        }
    }

    private void setBeaconStatus(int beaconStatus)
    {
        this.beaconStatus=beaconStatus;
        switch (beaconStatus)
        {
            case Constants.BEACON_STATUS.SEARCHING:
                binding.currentLocationTv.setText(getString(R.string.beacon_status_searching));
                binding.currentLocationTv.clearAnimation();
                binding.currentLocationTv.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.blinking));
                break;
            case Constants.BEACON_STATUS.UPDATING:
                binding.currentLocationTv.setText(getString(R.string.beacon_status_updating));
                binding.currentLocationTv.clearAnimation();
                binding.currentLocationTv.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.blinking));

                break;
            case Constants.BEACON_STATUS.UPDATED:
                binding.currentLocationTv.clearAnimation();
                break;
        }

    }


}
