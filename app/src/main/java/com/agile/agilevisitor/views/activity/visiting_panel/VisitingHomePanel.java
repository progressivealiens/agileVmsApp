package com.agile.agilevisitor.views.activity.visiting_panel;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.agile.agilevisitor.R;
import com.agile.agilevisitor.customviews.MyTextview;
import com.agile.agilevisitor.helper.CheckNetworkConnection;
import com.agile.agilevisitor.helper.PrefData;
import com.agile.agilevisitor.helper.ProgressView;
import com.agile.agilevisitor.helper.Utils;
import com.agile.agilevisitor.views.activity.LoginUserTypeActivity;
import com.agile.agilevisitor.views.fragment.NewMobileNumber;
import com.agile.agilevisitor.views.fragment.VerifyOTPFragment;
import com.agile.agilevisitor.webapi.ApiClient;
import com.agile.agilevisitor.webapi.ApiInterface;
import com.agile.agilevisitor.webapi.ApiResponse;
import com.google.android.material.navigation.NavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VisitingHomePanel extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.frame_container)
    FrameLayout frameContainer;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    MyTextview tvTitle;
    @BindView(R.id.lin_toolbar)
    LinearLayout linToolbar;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.tv_unit_name)
    MyTextview tvUnitName;

    ApiInterface apiInterface;
    ProgressView progressView;
    PrefData prefData;

    public static AppCompatActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visiting_home_handler);
        ButterKnife.bind(this);

        initialize();

        Utils.changeFragmentHome(new NewMobileNumber(), "frag_new_mobile", activity);

    }

    private void initialize() {
        activity = this;

        setSupportActionBar(toolbar);
        ivBack.setVisibility(View.GONE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("HOME");

        tvUnitName.setText(PrefData.readStringPref(PrefData.pref_unit_name));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navView.setNavigationItemSelectedListener(this);
        View headerView = navView.getHeaderView(0);

        apiInterface = ApiClient.getClient(VisitingHomePanel.this, 0).create(ApiInterface.class);
        progressView = new ProgressView(VisitingHomePanel.this);
        prefData = new PrefData(VisitingHomePanel.this);
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        if (currentFragment instanceof NewMobileNumber) {
            finish();
        } else if (currentFragment instanceof VerifyOTPFragment) {
            currentFragment.getChildFragmentManager().popBackStack("frag_new_mobile", 0);
        }
        super.onBackPressed();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();
        if (id == R.id.nav_home) {
            drawerLayout.closeDrawers();
            finish();
            startActivity(getIntent());
        } else if (id == R.id.nav_share) {
            //share();
        } else if (id == R.id.nav_rate_us) {
            //rateUs();
        } else if (id == R.id.nav_logout) {
            logout();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        if (CheckNetworkConnection.isConnection1(VisitingHomePanel.this, true)) {
            progressView.showLoader();

            Call<ApiResponse> call = apiInterface.panelLogout(PrefData.readStringPref(PrefData.pref_fcm_token));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    progressView.hideLoader();

                    try {

                        if (response.body().getStatus().equalsIgnoreCase(getString(R.string.success))) {

                            Toast.makeText(VisitingHomePanel.this, "Logout Successful", Toast.LENGTH_SHORT).show();

                            PrefData.writeBooleanPref(PrefData.PREF_LOGINSTATUS, false);

                            Intent intent = new Intent(VisitingHomePanel.this, LoginUserTypeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(intent);
                            finish();

                        } else {
                            Utils.showSnackBar(drawerLayout, response.body().getMsg(), VisitingHomePanel.this);
                        }
                    } catch (Exception e) {
                        if (response.code() == 400) {
                            Toast.makeText(VisitingHomePanel.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(VisitingHomePanel.this, getString(R.string.network_busy), Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(VisitingHomePanel.this, getString(R.string.resource_not_found), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(VisitingHomePanel.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                        e.printStackTrace();

                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    progressView.hideLoader();
                    t.printStackTrace();
                }
            });

        }
    }

}
