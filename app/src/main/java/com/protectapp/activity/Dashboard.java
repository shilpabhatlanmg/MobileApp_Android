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
import android.widget.Toast;

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
import com.protectapp.util.AppBeaconService;
import com.protectapp.util.AppCommons;
import com.protectapp.util.AppAlertDialogHelper;
import com.protectapp.util.AppSession;
import com.protectapp.util.Constants;
import com.protectapp.util.IncidentDialog;
import com.protectapp.util.Prefs;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import static com.protectapp.util.Constants.APP_BEACON_LAYOUT;

public class Dashboard extends BaseActivity implements View.OnClickListener, DrawerMenuView.DrawerMenuListener, DashboardFragmentListener {
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
    private BeaconEvent trackedBeacon = null;
    private int incidentType;
    private Incident extraIncident = null;
    private boolean incidentReminder = false;
    private int noBeaconCounter = 0;
    private GetLocationData currentLocation = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppSession.getInstance().updateFCMToken(getApplicationContext());
        handleIntent();
    }


    private void handleIntent() {
        if (extraIncident != null)
        {
            if (incidentReminder) {
                AppAlertDialogHelper.showActionMessage(this, R.string.app_name, R.string.record_response_incident_msg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showProgress(false);
                        hitAPI(RECORD_RESPONSE_RQ);
                    }
                });
            } else {

                new IncidentDialog(this, extraIncident).show();
            }

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
        incidentReminder = getIntent().getBooleanExtra(Constants.EXTRA.REPORT_REMINDER, false);
    }

    @Override
    public void initUI() {

    }

    @Override
    public void setUI() {
        binding.drawerMenu.setDrawerMenuListener(this);
        binding.dashboardToolbar.setOnActionItemClickListener(this);
        binding.dashboardToolbar.adjustToolbarFor(HomeFragment.class);
        openFragment(HomeFragment.class, getHomeFragBundle());
        setUpDashboardControls();
       // hitAPI(GET_UUID_RQ);
        startBeaconRanging();
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
                            incidentType, trackedBeacon.getMajorID(), trackedBeacon.getMinorID(),
                            new ApiCallback<>(REPORT_INCIDENT_RQ));
                } else {
                    AppAlertDialogHelper.showMessage(this, R.string.no_beacon_title, R.string.no_beacon_message);
                }
                break;
            case GET_BADGE_COUNT_RQ:
                ProtectApiHelper.getInstance().getBadgeCount(AppSession.getInstance().getAccessToken(), new ApiCallback<GetBadgeCountData>(GET_BADGE_COUNT_RQ));
                break;
            case RECORD_RESPONSE_RQ:
                if (extraIncident != null)
                    ProtectApiHelper.getInstance()
                            .recordResponse(AppSession.getInstance().getAccessToken(), extraIncident.getReportID(), new ApiCallback<>(RECORD_RESPONSE_RQ));
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
                } else {
                    hitAPI(GET_UUID_RQ);
                }
                break;
            case REPORT_INCIDENT_RQ:
                hideProgress();
                if (currentLocation != null)
                    AppAlertDialogHelper.showMessage(this, getString(R.string.reported_success_title), getString(R.string.reported_success_msg, currentLocation.getLocationName(), currentLocation.getPremise()));
                break;
            case GET_BADGE_COUNT_RQ:
                if (model.getData() instanceof GetBadgeCountData) {
                    AppSession.getInstance().setBadgeCountData((GetBadgeCountData) model.getData());
                    binding.dashboardToolbar.updateBadges();
                }
                break;
            case RECORD_RESPONSE_RQ:
                hideProgress();
                AppAlertDialogHelper.showMessage(this,R.string.app_name, R.string.response_recorded_success_msg);
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
                AppSession.getInstance().logout();
                getNavigation().afterLogout(this, new Bundle());
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
                openFragment(RecentChatsFragment.class, null);
                break;
            case R.id.calendar_btn:
                openFragment(AidHistoryFragment.class, null);
                break;
            case R.id.add_btn:
                startActivityForResult(new Intent(this, SelectMemberActivity.class), Constants.ACTIVITY_RQ.SELECT_MEMBER);
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
                openFragment(HomeFragment.class, getHomeFragBundle());
                break;
            case R.id.my_profile_nav_btn:
                openFragment(MyProfileFragment.class, null);
                break;
            case R.id.change_password_nav_btn:
                openFragment(ChangePasswordFragment.class, null);
                break;
            case R.id.about_us_nav_btn:
                openFragment(AboutUsFragment.class, null);
                break;
            case R.id.privacy_policy_nav_btn:
                openFragment(PrivacyPolicyFragment.class, null);
                break;
            case R.id.logout_nav_btn:
                hitAPI(DO_LOGOUT_RQ);
                showProgress(false);
                break;

        }
    }

    private Bundle getHomeFragBundle() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.EXTRA.BEACON_EVENT,trackedBeacon);
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

    private void openFragment(Class fragmentClass, Bundle bundle) {
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
                if (fragmentClass.equals(HomeFragment.class)) {
                    fragment = HomeFragment.newInstance(bundle);
                } else {
                    fragment = (Fragment) fragmentClass.newInstance();
                }
                ft.replace(R.id.container, fragment, fragmentClass.getName());
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
               // bindBeaconManager();
                startBeaconService();
            }
        } else {
           // bindBeaconManager();
            startBeaconService();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //bindBeaconManager();
                    startBeaconService();
                } else {
                    startBeaconRanging();
                }
                return;
            }
        }
    }

    private void startBeaconService() {
        try
        {
            startService(new Intent(this,AppBeaconService.class)
                    .putExtra(Constants.EXTRA.UUID,UUID));
        }
        catch (Exception e)
        {

        }
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();

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
        if (AppSession.getInstance().getBadgeCountData() != null) {
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
        if (location != null) {
            this.currentLocation = location;
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        extraIncident = (Incident) intent.getSerializableExtra(Constants.EXTRA.INCIDENT);
        incidentReminder =intent.getBooleanExtra(Constants.EXTRA.REPORT_REMINDER, false);
        handleIntent();

    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.dashboardToolbar.updateBadges();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        AppCommons.setBluetooth(true);
//        bindBeaconManager();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotification(GenericNotificationData<Object> event) {
        binding.dashboardToolbar.updateBadges();
        if (event.getPayload() instanceof Incident) {
            new IncidentDialog(this, (Incident) event.getPayload()).show();
           startBeaconService();
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
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBeaconEvent(BeaconEvent event) {

        trackedBeacon=event;

    }


}


