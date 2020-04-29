package com.agile.agilevisitor.views.activity.user_panel;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.agile.agilevisitor.R;
import com.agile.agilevisitor.customviews.MyButton;
import com.agile.agilevisitor.customviews.MyTextview;
import com.agile.agilevisitor.helper.CheckNetworkConnection;
import com.agile.agilevisitor.helper.PrefData;
import com.agile.agilevisitor.helper.ProgressView;
import com.agile.agilevisitor.helper.Utils;
import com.agile.agilevisitor.views.activity.LoginUserTypeActivity;
import com.agile.agilevisitor.webapi.ApiClient;
import com.agile.agilevisitor.webapi.ApiInterface;
import com.agile.agilevisitor.webapi.ApiResponse;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    MyTextview tvTitle;
    @BindView(R.id.lin_toolbar)
    LinearLayout linToolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.btn_view_profile)
    MyButton btnViewProfile;
    @BindView(R.id.btn_logout)
    MyButton btnLogout;

    @BindView(R.id.iv_logo)
    CircleImageView ivLogo;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.tv_invites)
    MyTextview tvInvites;
    @BindView(R.id.tv_visitors)
    MyTextview tvVisitors;
    @BindView(R.id.btn_send_checkin_invite)
    MyButton btnSendCheckinInvite;
    /*@BindView(R.id.btn_visitor_history)
    MyButton btnVisitorHistory;*/

    ApiInterface apiInterface;
    ProgressView progressView;
    PrefData prefData;

    String username="",firstLetterOfUsername="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        ButterKnife.bind(this);

        initialize();

    }

    private void initialize() {
        setSupportActionBar(toolbar);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("HOME");

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(this);

        btnSendCheckinInvite.setOnClickListener(this);
        //btnVisitorHistory.setOnClickListener(this);
        btnViewProfile.setOnClickListener(this);
        btnLogout.setOnClickListener(this);

        apiInterface = ApiClient.getClient(UserHomeActivity.this, 0).create(ApiInterface.class);
        progressView = new ProgressView(UserHomeActivity.this);
        prefData = new PrefData(UserHomeActivity.this);

        tvUserName.setText(PrefData.readStringPref(PrefData.pref_user_name));
        tvInvites.setText(PrefData.readStringPref(PrefData.pref_user_invites));
        tvVisitors.setText(PrefData.readStringPref(PrefData.pref_user_visitors));
        Picasso.get().load(Utils.USER_IMAGE + PrefData.readStringPref(PrefData.pref_user_logo)).placeholder(R.drawable.progress_image).into(ivLogo);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(UserHomeActivity.this, UserHomeActivity.class));
        } else if (id == R.id.nav_send_invitation) {
            startActivity(new Intent(UserHomeActivity.this, UserCheckinInvitation.class));
        } /*else if (id == R.id.nav_visitor_history) {
            startActivity(new Intent(UserHomeActivity.this, VisitorHistoryActivity.class));
        }*/else if (id == R.id.nav_rate_us) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {
            logout();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        if (CheckNetworkConnection.isConnection1(UserHomeActivity.this, true)) {
            progressView.showLoader();

            Call<ApiResponse> call = apiInterface.userLogout(PrefData.readStringPref(PrefData.pref_fcm_token));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    progressView.hideLoader();

                    try {

                        if (response.body().getStatus().equalsIgnoreCase(getString(R.string.success))) {

                            Toast.makeText(UserHomeActivity.this, "Logout Successful", Toast.LENGTH_SHORT).show();


                            PrefData.writeBooleanPref(PrefData.PREF_LOGINSTATUS, false);

                            Intent intent = new Intent(UserHomeActivity.this, LoginUserTypeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(intent);
                            finish();

                        } else {
                            Utils.showSnackBar(drawerLayout, response.body().getMsg(), UserHomeActivity.this);
                        }
                    } catch (Exception e) {
                        if (response.code() == 400) {
                            Toast.makeText(UserHomeActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(UserHomeActivity.this, getString(R.string.network_busy), Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(UserHomeActivity.this, getString(R.string.resource_not_found), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserHomeActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_send_checkin_invite) {
            startActivity(new Intent(UserHomeActivity.this, UserCheckinInvitation.class));
        } /*else if (v.getId() == R.id.btn_visitor_history) {
            startActivity(new Intent(UserHomeActivity.this, VisitorHistoryActivity.class));
        } */else if (v.getId() == R.id.btn_view_profile) {
            startActivity(new Intent(UserHomeActivity.this, UserProfileActivity.class));
        } else if (v.getId() == R.id.btn_logout) {
            logout();
        }

    }
}
