package com.protectapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.protectapp.R;
import com.protectapp.customview.DrawerMenuView;
import com.protectapp.databinding.ActivityDashboardBinding;
import com.protectapp.fragment.AboutUsFragment;
import com.protectapp.fragment.AidHistoryFragment;
import com.protectapp.fragment.ChangePasswordFragment;
import com.protectapp.fragment.RecentChatsFragment;
import com.protectapp.fragment.HomeFragment;
import com.protectapp.fragment.MyProfileFragment;
import com.protectapp.fragment.PrivacyPolicyFragment;
import com.protectapp.listener.DashboardFragmentListener;
import com.protectapp.model.BeaconEvent;
import com.protectapp.model.GenericNotificationData;
import com.protectapp.model.GenericResponseModel;
import com.protectapp.model.GetBadgeCountData;
import com.protectapp.model.GetLocationData;
import com.protectapp.model.Incident;
import com.protectapp.model.ProfileData;
import com.protectapp.model.UUIDData;
import com.protectapp.network.ProtectApiHelper;
import com.protectapp.util.AppCommons;
import com.protectapp.util.AppAlertDialogHelper;
import com.protectapp.util.AppSession;
import com.protectapp.util.Constants;
import com.protectapp.util.IncidentDialog;
import com.protectapp.util.Prefs;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Dashboard extends BaseActivity implements View.OnClickListener, DrawerMenuView.DrawerMenuListener, DashboardFragmentListener, BeaconConsumer {
    private ActivityDashboardBinding binding;
    private BeaconManager beaconManager;
    private static final int GET_PROFILE_RQ = 201;
    private static final int GET_UUID_RQ = 202;
    private static final int REPORT_INCIDENT_RQ = 203;
    private static final int GET_BADGE_COUNT_RQ = 204;
    private static final int DO_LOGOUT_RQ = 205;
    private static final int RECORD_RESPONSE_RQ = 206;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 10001;
    private String UUID = null;
    private Beacon trackedBeacon = null;
    private int incidentType;
    private Incident extraIncident = null;
    private boolean incidentReminder=false;
    private int noBeaconCounter = 0;
    private GetLocationData currentLocation=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppSession.getInstance().updateFCMToken(getApplicationContext());
        handleIntent();
    }


    private void handleIntent() {
        if (extraIncident != null)
            if(incidentReminder)
            {
                AppAlertDialogHelper.showActionMessage(this, R.string.app_name, R.string.record_response_incident_msg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showProgress(false);
                        hitAPI(RECORD_RESPONSE_RQ);
                    }
                });
            }
            else
            {

                new IncidentDialog(this, extraIncident).show();
            }
    }

    @Override
    public void setContentView() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);
        transparentStatusBar();
    }

    @Override
    public void getExtras() {
        extraIncident = (Incident) getIntent().getSerializableExtra(Constants.EXTRA.INCIDENT);
        incidentReminder = getIntent().getBooleanExtra(Constants.EXTRA.REPORT_REMINDER,false);
    }

    @Override
    public void initUI() {
        initBeaconManager();

    }

    @Override
    public void setUI() {
        binding.drawerMenu.setDrawerMenuListener(this);
        binding.dashboardToolbar.setOnActionItemClickListener(this);
        binding.dashboardToolbar.adjustToolbarFor(HomeFragment.class);
        openFragment(HomeFragment.class,getHomeFragBundle());
        setUpDashboardControls();
        hitAPI(GET_UUID_RQ);
    }

    private void setUpDashboardControls() {
        if (AppCommons.isVisitor(this)) {
            binding.dashboardToolbar.setVisibility(View.GONE);
            binding.dashboardDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            hitAPI(GET_PROFILE_RQ);
            hitAPI(GET_BADGE_COUNT_RQ);
            binding.dashboardToolbar.setVisibility(View.VISIBLE);
            binding.dashboardDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    @Override
    public void hitAPI(int req_code) {
        switch (req_code) {
            case GET_PROFILE_RQ:
                ProtectApiHelper.getInstance().getProfile(AppSession.getInstance().getAccessToken(), new ApiCallback<ProfileData>(GET_PROFILE_RQ));
                break;
            case GET_UUID_RQ:
                ProtectApiHelper.getInstance().getUUID(new ApiCallback<UUIDData>(GET_UUID_RQ));
                break;
            case REPORT_INCIDENT_RQ:
                if (trackedBeacon != null) {
                    showProgress(false);
                    ProtectApiHelper.getInstance().reportIncident(AppSession.getInstance().getAccessToken(),
                            incidentType, trackedBeacon.getId2().toString(), trackedBeacon.getId3().toString(),
                            new ApiCallback<>(REPORT_INCIDENT_RQ));
                } else {
                    AppAlertDialogHelper.showMessage(this, R.string.no_beacon_title, R.string.no_beacon_message);
                }
                break;
            case GET_BADGE_COUNT_RQ:
                ProtectApiHelper.getInstance().getBadgeCount(AppSession.getInstance().getAccessToken(), new ApiCallback<GetBadgeCountData>(GET_BADGE_COUNT_RQ));
                break;
            case RECORD_RESPONSE_RQ:
                if(extraIncident!=null)
                ProtectApiHelper.getInstance()
                        .recordResponse(AppSession.getInstance().getAccessToken(),extraIncident.getReportID(),new ApiCallback<>(RECORD_RESPONSE_RQ));
                break;
            case DO_LOGOUT_RQ:
                ProtectApiHelper.getInstance().doLogout(AppSession.getInstance().getAccessToken(),
                        Prefs.getString(this, Constants.PREF.ACCESS_TOKEN, ""), new ApiCallback<>(DO_LOGOUT_RQ));
                break;
        }
    }

    @Override
    public <D> void onApiSuccess(GenericResponseModel<D> model, int req_code) {
        switch (req_code) {
            case GET_PROFILE_RQ:
                if (model.getData() instanceof ProfileData) {
                    AppSession.getInstance().setProfileData((ProfileData) model.getData());
                    onProfileUpdated();
                }
                break;
            case GET_UUID_RQ:
                if (model.getData() instanceof UUIDData) {
                    UUID = ((UUIDData) model.getData()).getUUID();
                    startBeaconRanging();
                }
                else
                {
                    hitAPI(GET_UUID_RQ);
                }
                break;
            case REPORT_INCIDENT_RQ:
                hideProgress();
                if (currentLocation!=null)
                AppAlertDialogHelper.showMessage(this, getString(R.string.reported_success_title), getString(R.string.reported_success_msg,currentLocation.getLocationName(),currentLocation.getPremise()));
                break;
            case GET_BADGE_COUNT_RQ:
                if (model.getData() instanceof GetBadgeCountData) {
                    AppSession.getInstance().setBadgeCountData((GetBadgeCountData) model.getData());
                    binding.dashboardToolbar.updateBadges();
                }
                break;
            case RECORD_RESPONSE_RQ:
                hideProgress();
                AppCommons.showToast(this,R.string.response_recorded_success_msg);
                break;
            case DO_LOGOUT_RQ:
                hideProgress();
                AppSession.getInstance().logout();
                getNavigation().afterLogout(this, new Bundle());
                break;
        }
    }


    @Override
    public <D> void onApiFail(GenericResponseModel<D> model, int req_code) {
        switch (req_code) {
            case GET_UUID_RQ:
                hitAPI(GET_UUID_RQ);
                break;
            case GET_PROFILE_RQ:
                break;
            case REPORT_INCIDENT_RQ:
                hideProgress();
                AppCommons.showError(this, model != null ? model.getMessage() : null);
                break;
            case RECORD_RESPONSE_RQ:
                hideProgress();
                AppCommons.showError(this, model != null ? model.getMessage() : null);
                break;
            case DO_LOGOUT_RQ:
                hideProgress();
                AppCommons.showError(this, model != null ? model.getMessage() : null);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_btn:
                binding.dashboardDrawer.openDrawer(Gravity.START);
                break;
            case R.id.chat_btn:
                openFragment(RecentChatsFragment.class,null);
                break;
            case R.id.calendar_btn:
                openFragment(AidHistoryFragment.class,null);
                break;
            case R.id.add_btn:
                startActivityForResult(new Intent(this, SelectMemberActivity.class),Constants.ACTIVITY_RQ.SELECT_MEMBER);
                break;

        }
    }

    private void closeDrawerMenu() {
        binding.dashboardDrawer.closeDrawer(Gravity.START);
    }

    @Override
    public void onDrawerMenuItemClick(int item_id) {

        closeDrawerMenuWithDelay();
        switch (item_id) {
            case R.id.close_menu_btn:
                break;
            case R.id.home_nav_btn:
                openFragment(HomeFragment.class,getHomeFragBundle());
                break;
            case R.id.my_profile_nav_btn:
                openFragment(MyProfileFragment.class,null);
                break;
            case R.id.change_password_nav_btn:
                openFragment(ChangePasswordFragment.class,null);
                break;
            case R.id.about_us_nav_btn:
                openFragment(AboutUsFragment.class,null);
                break;
            case R.id.privacy_policy_nav_btn:
                openFragment(PrivacyPolicyFragment.class,null);
                break;
            case R.id.logout_nav_btn:
                hitAPI(DO_LOGOUT_RQ);
                showProgress(false);
                break;

        }
    }

    private Bundle getHomeFragBundle() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.EXTRA.BEACON_EVENT,toBeaconEvent(trackedBeacon));
        return bundle;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        updateForegroundFragment();
    }

    private void updateForegroundFragment() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (currentFragment != null)
            binding.dashboardToolbar.adjustToolbarFor(currentFragment.getClass());
    }

    private void openFragment(Class fragmentClass,Bundle bundle) {
        try {
            binding.dashboardToolbar.adjustToolbarFor(fragmentClass);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm
                    .beginTransaction()
                    .setCustomAnimations(R.anim.fragment_enter, 0, 0, R.anim.fragment_exit);
            if (fragmentClass == HomeFragment.class) {
                if (fm.getBackStackEntryCount() > 0) {
                    FragmentManager.BackStackEntry entry = fm.getBackStackEntryAt(0);
                    fm.popBackStack(entry.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                Fragment fragment = null;
                if(fragmentClass.equals(HomeFragment.class))
                {
                    fragment = HomeFragment.newInstance(bundle);
                }
                else
                {
                    fragment= (Fragment) fragmentClass.newInstance();
                }
                ft.replace(R.id.container,fragment , fragmentClass.getName());
                ft.commit();
                return;
            }

            Fragment fragment = fm.findFragmentByTag(fragmentClass.getName());
            if (fragment == null) {

                ft.add(R.id.container, (Fragment) fragmentClass.newInstance(), fragmentClass.getName());
                ft.addToBackStack(fragmentClass.getName());
                ft.commit();
            } else {
                fm.popBackStack(fragmentClass.getName(), 0);
            }

        } catch (Exception e) {

        }

    }

    private void closeDrawerMenuWithDelay() {
        binding.container.postDelayed(new Runnable() {
            @Override
            public void run() {
                closeDrawerMenu();
            }
        }, 400);
    }

    private void startBeaconRanging() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            } else {
                bindBeaconManager();
            }
        } else {
            bindBeaconManager();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    bindBeaconManager();
                } else {
                    startBeaconRanging();
                }
                return;
            }
        }
    }

    private void initBeaconManager() {

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

    }

    private void bindBeaconManager() {
        if (beaconManager != null) beaconManager.unbind(this);
        beaconManager.bind(this);
    }
    private void unbindBeaconManager() {
        if (beaconManager != null) beaconManager.unbind(this);
    }


    @Override
    public void onBeaconServiceConnect() {
        if (UUID == null) return;

//        try {
//            beaconManager.setForegroundScanPeriod(Constants.BEACON_SCAN_INTERVAL);
//            beaconManager.updateScanPeriods();
//        } catch (Exception e) {
//
//        }
beaconManager.removeAllMonitorNotifiers();
beaconManager.addMonitorNotifier(new MonitorNotifier() {
    @Override
    public void didEnterRegion(Region region) {


    }

    @Override
    public void didExitRegion(Region region) {
        trackedBeacon = null;
        EventBus.getDefault().post(toBeaconEvent(null));
        beaconManager.removeAllRangeNotifiers();
        stopBeaconRangingInRegion();

    }


    @Override
    public void didDetermineStateForRegion(int i, Region region) {
        beaconManager.removeAllRangeNotifiers();
        beaconManager.addRangeNotifier(beaconRangeNotifier);
        startBeaconRangingInRegion();
    }
});
        startBeaconMonitoringInRegion();
    }


    private RangeNotifier beaconRangeNotifier=new RangeNotifier() {
        @Override
        public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
            if (beacons.size() > 0) {
                noBeaconCounter=0;
                Beacon beacon = selectValidBeacon((ArrayList<Beacon>) beacons);

                if (trackedBeacon == null || !beacon.equals(trackedBeacon)) {
                    trackedBeacon = beacon;
                    EventBus.getDefault().post(toBeaconEvent(beacon));
                }


            } else if (noBeaconCounter > 10) {
//                trackedBeacon = null;
//                EventBus.getDefault().post(toBeaconEvent(null));
            } else {
                noBeaconCounter++;
            }
        }
    };
private void startBeaconMonitoringInRegion()
{
    try {
        beaconManager.startMonitoringBeaconsInRegion(new Region("myRangingUniqueId", Identifier.parse(UUID), null, null));
    } catch (RemoteException e) {
        Log.d("Beacon", "beacon error");

    }
}
    private void startBeaconRangingInRegion()
    {
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", Identifier.parse(UUID), null, null));
        } catch (RemoteException e) {
            Log.d("Beacon", "beacon error");

        }
    }
    private void stopBeaconRangingInRegion()
    {
        try {
            beaconManager.stopRangingBeaconsInRegion(new Region("myRangingUniqueId", Identifier.parse(UUID), null, null));
        } catch (RemoteException e) {
            Log.d("Beacon", "beacon error");

        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (beaconManager != null)
            beaconManager.unbind(this);
    }

    @Override
    public void reportIncident(int incidentType) {
        this.incidentType = incidentType;
        AppAlertDialogHelper.showActionMessage(this, getString(R.string.confirm_report_title), AppCommons.generateConfirmReportMsg(this, incidentType), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hitAPI(REPORT_INCIDENT_RQ);
            }
        });
    }

    @Override
    public void incidentListLoaded() {
    if(AppSession.getInstance().getBadgeCountData()!=null)
    {
        AppSession.getInstance().getBadgeCountData().setIncidentBadgeCount(0);
        binding.dashboardToolbar.updateBadges();
    }

    }

    @Override
    public void onProfileUpdated() {
        binding.drawerMenu.updateHeader();
    }

    @Override
    public void onLocationLoaded(GetLocationData location) {
        if (location!=null)
        {
            this.currentLocation=location;
        }

    }

    private Beacon selectValidBeacon(ArrayList<Beacon> beaconList) {
        Collections.sort(beaconList, new Comparator<Beacon>() {
            @Override
            public int compare(Beacon o1, Beacon o2) {
                return o1.getDistance() > o2.getDistance() ? 1 : -1;
            }
        });
        int trackedBeaconIndex = trackedBeacon != null ? beaconList.indexOf(trackedBeacon) : -1;
        trackedBeacon = trackedBeaconIndex >= 0 ? beaconList.get(trackedBeaconIndex) : trackedBeacon;

        final Beacon newBeacon = beaconList.get(0);

        if (trackedBeacon != null && !newBeacon.equals(trackedBeacon) && trackedBeaconIndex != -1 && Math.abs(newBeacon.getDistance() - trackedBeacon.getDistance()) < Constants.BEACON_DISTANCE_BUFFER) {
            return trackedBeacon;
        } else {

            return newBeacon;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.dashboardToolbar.updateBadges();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppCommons.setBluetooth(true);
        EventBus.getDefault().register(this);
        bindBeaconManager();
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppCommons.setBluetooth(false);
        EventBus.getDefault().unregister(this);
        unbindBeaconManager();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotification(GenericNotificationData<Object> event) {
        binding.dashboardToolbar.updateBadges();
        if(event.getPayload() instanceof Incident)
        {
            new IncidentDialog(this, (Incident) event.getPayload()).show();
            trackedBeacon=null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment f : fragments) {
                if (f instanceof MyProfileFragment || f instanceof RecentChatsFragment) {
                    f.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }
private BeaconEvent  toBeaconEvent(Beacon beacon)
{
    BeaconEvent beaconEvent = new BeaconEvent();

    if(beacon==null)
    {
        beaconEvent.setMajorID(null);
        beaconEvent.setMinorID(null);
    }
    else
    {
        beaconEvent.setMajorID(beacon.getId2().toString());
        beaconEvent.setMinorID(beacon.getId3().toString());
        beaconEvent.setBeaconDistance(beacon.getDistance());
    }

    return beaconEvent;
}

}


