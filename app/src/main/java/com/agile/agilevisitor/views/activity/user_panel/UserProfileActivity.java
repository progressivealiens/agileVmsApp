package com.agile.agilevisitor.views.activity.user_panel;

import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.agile.agilevisitor.R;
import com.agile.agilevisitor.customviews.MyButton;
import com.agile.agilevisitor.customviews.MyTextview;
import com.agile.agilevisitor.helper.CheckNetworkConnection;
import com.agile.agilevisitor.helper.PrefData;
import com.agile.agilevisitor.helper.ProgressView;
import com.agile.agilevisitor.helper.Utils;
import com.agile.agilevisitor.webapi.ApiClient;
import com.agile.agilevisitor.webapi.ApiInterface;
import com.agile.agilevisitor.webapi.ApiResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    MyTextview tvTitle;
    @BindView(R.id.lin_toolbar)
    LinearLayout linToolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btn_reset_password)
    MyButton btnResetPassword;
    @BindView(R.id.root_profile)
    LinearLayout rootProfile;
    @BindView(R.id.iv_user_image)
    CircleImageView ivUserImage;
    @BindView(R.id.et_profile_mobile)
    TextInputEditText etProfileMobile;
    @BindView(R.id.et_unit_code)
    TextInputEditText etUnitCode;
    @BindView(R.id.et_unit_name)
    TextInputEditText etUnitName;
    @BindView(R.id.et_unit_address)
    TextInputEditText etUnitAddress;
    @BindView(R.id.et_subunit_name)
    TextInputEditText etSubunitName;
    @BindView(R.id.et_landmark)
    TextInputEditText etLandmark;
    @BindView(R.id.tv_profile_name)
    MyTextview tvProfileName;

    ApiInterface apiInterface;
    ProgressView progressView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);

        initialize();

        connectApiToFetchProfileData();

    }

    private void initialize() {
        setSupportActionBar(toolbar);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("Profile");
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setOnClickListener(this);
        btnResetPassword.setOnClickListener(this);

        apiInterface = ApiClient.getClient(UserProfileActivity.this, 0).create(ApiInterface.class);
        progressView = new ProgressView(UserProfileActivity.this);
    }

    private void connectApiToFetchProfileData() {

        if (CheckNetworkConnection.isConnection1(UserProfileActivity.this, true)) {
            progressView.showLoader();


            Call<ApiResponse> call = apiInterface.userProfile(PrefData.readStringPref(PrefData.pref_fcm_token));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    progressView.hideLoader();


                    try {

                        if (response.body().getStatus().equalsIgnoreCase(getString(R.string.success))) {

                            tvProfileName.setText(PrefData.readStringPref(PrefData.pref_user_name));
                            Picasso.get().load(Utils.USER_IMAGE + PrefData.readStringPref(PrefData.pref_user_logo)).placeholder(R.drawable.progress_image).into(ivUserImage);
                            etProfileMobile.setText(PrefData.readStringPref(PrefData.pref_user_mobile));
                            etUnitCode.setText(response.body().getUnitCode());
                            etUnitName.setText(response.body().getUnitName());
                            etUnitAddress.setText(response.body().getUnitAddress());
                            etSubunitName.setText(response.body().getSubUnitName());
                            etLandmark.setText(response.body().getLandmark());

                        } else {
                            Utils.showSnackBar(rootProfile, response.body().getMsg(), UserProfileActivity.this);
                        }
                    } catch (Exception e) {
                        if (response.code() == 400) {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.network_busy), Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.resource_not_found), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    progressView.hideLoader();
                }
            });
        }


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            onBackPressed();
        } else if (v.getId() == R.id.btn_reset_password) {
            openDialogToResetPassword();
        }
    }

    private void openDialogToResetPassword() {
        final Dialog dialog = new Dialog(UserProfileActivity.this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_reset_password);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = (int) (displaymetrics.widthPixels * 0.8);
        int height = (int) (displaymetrics.heightPixels * 0.5);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button resetPassword = dialog.findViewById(R.id.btn_reset_password);
        MyTextview tvDismiss = dialog.findViewById(R.id.tv_dismiss);

        tvDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
