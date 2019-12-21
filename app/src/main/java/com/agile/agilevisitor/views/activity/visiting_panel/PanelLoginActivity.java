package com.agile.agilevisitor.views.activity.visiting_panel;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.agile.agilevisitor.R;
import com.agile.agilevisitor.customviews.MyButton;
import com.agile.agilevisitor.helper.CheckNetworkConnection;
import com.agile.agilevisitor.helper.PrefData;
import com.agile.agilevisitor.helper.ProgressView;
import com.agile.agilevisitor.helper.Utils;
import com.agile.agilevisitor.helper.Validation;
import com.agile.agilevisitor.views.activity.LoginUserTypeActivity;
import com.agile.agilevisitor.webapi.ApiClient;
import com.agile.agilevisitor.webapi.ApiInterface;
import com.agile.agilevisitor.webapi.ApiResponse;
import com.google.android.material.textfield.TextInputEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PanelLoginActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.btn_panel_login)
    MyButton btnPanelLogin;
    @BindView(R.id.root_panel)
    RelativeLayout rootPanel;
    @BindView(R.id.et_company_email)
    TextInputEditText etCompanyEmail;
    @BindView(R.id.et_unit_code)
    TextInputEditText etUnitCode;
    @BindView(R.id.et_unit_password)
    TextInputEditText etUnitPassword;
    @BindView(R.id.et_panel_code)
    TextInputEditText etPanelCode;

    ApiInterface apiInterface;
    ProgressView progressView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel_login);
        ButterKnife.bind(this);

        initialize();
        setupUI(rootPanel);
    }

    private void initialize() {
        btnPanelLogin.setOnClickListener(this);

        apiInterface = ApiClient.getClient(PanelLoginActivity.this,0).create(ApiInterface.class);
        progressView = new ProgressView(PanelLoginActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_panel_login:

                if (Validation.nullValidator(etCompanyEmail.getText().toString())){
                    Utils.showSnackBar(rootPanel, "Email Id can't be null", etCompanyEmail, PanelLoginActivity.this);
                } else if (!Validation.emailValidator(etCompanyEmail.getText().toString())){
                    Utils.showSnackBar(rootPanel, "Please Enter the EmailId in proper format", etCompanyEmail, PanelLoginActivity.this);
                }else if (Validation.nullValidator(etUnitCode.getText().toString())){
                    Utils.showSnackBar(rootPanel, "Unit Code can't be null", etUnitCode, PanelLoginActivity.this);
                }else if (Validation.nullValidator(etUnitPassword.getText().toString())){
                    Utils.showSnackBar(rootPanel, "Unit Password can't be null", etUnitPassword, PanelLoginActivity.this);
                }else if (Validation.nullValidator(etPanelCode.getText().toString())){
                    Utils.showSnackBar(rootPanel, "Panel Code can't be null", etPanelCode, PanelLoginActivity.this);
                }else{
                    connectApiToLoginPanel(etCompanyEmail.getText().toString(),
                            etUnitCode.getText().toString(),
                            etUnitPassword.getText().toString(),
                            etPanelCode.getText().toString());
                }

                break;
        }
    }

    private void connectApiToLoginPanel(String Email,String unitCode,String unitPassword,String panelCode) {

        if (CheckNetworkConnection.isConnection1(PanelLoginActivity.this, true)) {
            progressView.showLoader();

            Call<ApiResponse> call = apiInterface.panelLogin(Email, unitCode, unitPassword,panelCode, PrefData.readStringPref(PrefData.pref_fcm_token));

            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    progressView.hideLoader();

                    try {

                        if (response.body().getStatus().equalsIgnoreCase(getString(R.string.success))) {

                            Toast.makeText(PanelLoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();

                            PrefData.writeBooleanPref(PrefData.PREF_LOGINSTATUS,true);
                            PrefData.writeStringPref(PrefData.user_type,"Visitor");
                            PrefData.writeStringPref(PrefData.pref_unit_name,response.body().getUnitName());

                            Intent i = new Intent(PanelLoginActivity.this, VisitingLandingScreen.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);

                        }else {
                            Utils.showSnackBar(rootPanel, response.body().getMsg(), PanelLoginActivity.this);
                        }
                    }catch (Exception e) {
                        if (response.code() == 400) {
                            Toast.makeText(PanelLoginActivity.this, getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(PanelLoginActivity.this, getString(R.string.network_busy), Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(PanelLoginActivity.this, getString(R.string.resource_not_found), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PanelLoginActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
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

    public void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    Utils.hideSoftKeyboard(PanelLoginActivity.this);
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
