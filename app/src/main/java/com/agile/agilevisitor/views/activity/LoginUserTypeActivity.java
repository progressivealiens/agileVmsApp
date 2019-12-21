package com.agile.agilevisitor.views.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.agile.agilevisitor.R;
import com.agile.agilevisitor.customviews.MyButton;
import com.agile.agilevisitor.customviews.MyTextview;
import com.agile.agilevisitor.helper.CheckNetworkConnection;
import com.agile.agilevisitor.helper.PrefData;
import com.agile.agilevisitor.helper.ProgressView;
import com.agile.agilevisitor.helper.Utils;
import com.agile.agilevisitor.helper.Validation;
import com.agile.agilevisitor.views.activity.user_panel.UserHomeActivity;
import com.agile.agilevisitor.views.activity.visiting_panel.PanelLoginActivity;
import com.agile.agilevisitor.webapi.ApiClient;
import com.agile.agilevisitor.webapi.ApiInterface;
import com.agile.agilevisitor.webapi.ApiResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginUserTypeActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tv_visiting_panel)
    MyTextview tvVisitingPanel;
    @BindView(R.id.root_choose)
    RelativeLayout rootChoose;
    @BindView(R.id.btn_login)
    MyButton btnLogin;
    @BindView(R.id.et_login_mobile)
    TextInputEditText etLoginMobile;
    @BindView(R.id.et_login_password)
    TextInputEditText etLoginPassword;

    ApiInterface apiInterface;
    ProgressView progressView;

    String token = "";

    public static final int PERMISSIONS_REQUEST_CODE=1012;

    @Override
    protected void onStart() {
        super.onStart();
        if (!startRequestPermission()) {
            startRequestPermission();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user_type);
        ButterKnife.bind(this);

        initialize();
        getFirebaseToken();
        setupUI(rootChoose);
    }

    private void initialize() {
        tvVisitingPanel.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        apiInterface = ApiClient.getClient(LoginUserTypeActivity.this,0).create(ApiInterface.class);
        progressView = new ProgressView(LoginUserTypeActivity.this);
    }

    private boolean startRequestPermission() {
        if (ActivityCompat.checkSelfPermission(LoginUserTypeActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(LoginUserTypeActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED||
                ActivityCompat.checkSelfPermission(LoginUserTypeActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(LoginUserTypeActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_CODE);

            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_visiting_panel:
                startActivity(new Intent(LoginUserTypeActivity.this, PanelLoginActivity.class));
                //finish();
                break;
            case R.id.btn_login:

                if (Validation.nullValidator(etLoginMobile.getText().toString())){
                    Utils.showSnackBar(rootChoose, "Mobile Number can't be null", etLoginMobile, LoginUserTypeActivity.this);
                }else if (!Validation.mobileValidator(etLoginMobile.getText().toString())){
                    Utils.showSnackBar(rootChoose, "Mobile Number must be 10 digits long", etLoginMobile, LoginUserTypeActivity.this);
                }else if (Validation.nullValidator(etLoginPassword.getText().toString())){
                    Utils.showSnackBar(rootChoose, "Password can't be blank", etLoginMobile, LoginUserTypeActivity.this);
                }else if (!Validation.passValidator(etLoginPassword.getText().toString())){
                    Utils.showSnackBar(rootChoose, "Password must be 3 characters long", etLoginMobile, LoginUserTypeActivity.this);
                }else{
                    connectApiToLoginUser(etLoginMobile.getText().toString(),etLoginPassword.getText().toString());
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Log.e("PermissionProblem","PermissionProblem");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(LoginUserTypeActivity.this, R.string.permission_granted_success, Toast.LENGTH_LONG).show();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED || grantResults[2] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(LoginUserTypeActivity.this, R.string.sorry_cant_use, Toast.LENGTH_LONG).show();
                startRequestPermission();
            }
        }
    }

    private void connectApiToLoginUser(String mobile, String password) {


        if (CheckNetworkConnection.isConnection1(LoginUserTypeActivity.this, true)) {
            progressView.showLoader();

            Call<ApiResponse> call = apiInterface.userLogin(mobile,password,PrefData.readStringPref(PrefData.pref_fcm_token));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    progressView.hideLoader();

                    try {

                        if (response.body().getStatus().equalsIgnoreCase(getString(R.string.success))) {

                            PrefData.writeStringPref(PrefData.pref_user_mobile,mobile);
                            PrefData.writeBooleanPref(PrefData.PREF_LOGINSTATUS,true);
                            PrefData.writeStringPref(PrefData.user_type,"User");

                            String firstLetterOfUsername=response.body().getName().substring(0,1).toUpperCase();
                            String username=response.body().getName().substring(1);
                            username=firstLetterOfUsername+username;

                            PrefData.writeStringPref(PrefData.pref_user_name,username);
                            PrefData.writeStringPref(PrefData.pref_user_logo,response.body().getLogo());
                            PrefData.writeStringPref(PrefData.pref_user_invites,response.body().getInvites());
                            PrefData.writeStringPref(PrefData.pref_user_visitors,response.body().getVisitor());

                            startActivity(new Intent(LoginUserTypeActivity.this, UserHomeActivity.class));
                            finish();

                            Toast.makeText(LoginUserTypeActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();

                        }else {
                            Utils.showSnackBar(rootChoose, response.body().getMsg(), LoginUserTypeActivity.this);
                        }
                    }catch (Exception e) {
                        if (response.code() == 400) {
                            Toast.makeText(LoginUserTypeActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(LoginUserTypeActivity.this, getString(R.string.network_busy), Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(LoginUserTypeActivity.this, getString(R.string.resource_not_found), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginUserTypeActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
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

    public void getFirebaseToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e("FirebaseTokenFail", "getInstanceId failed", task.getException());
                            return;
                        }
                        token = task.getResult().getToken();
                        Log.e("Firebase Token : ", token);
                        PrefData.writeStringPref(PrefData.pref_fcm_token, token);
                    }
                });
    }

    public void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    Utils.hideSoftKeyboard(LoginUserTypeActivity.this);
                    return false;
                }
            });
        }
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

}
