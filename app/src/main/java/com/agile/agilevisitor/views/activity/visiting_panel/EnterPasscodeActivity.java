package com.agile.agilevisitor.views.activity.visiting_panel;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.agile.agilevisitor.R;
import com.agile.agilevisitor.customviews.MyButton;
import com.agile.agilevisitor.customviews.MyTextview;
import com.agile.agilevisitor.helper.CheckNetworkConnection;
import com.agile.agilevisitor.helper.PinEntryEditText;
import com.agile.agilevisitor.helper.PrefData;
import com.agile.agilevisitor.helper.ProgressView;
import com.agile.agilevisitor.helper.Validation;
import com.agile.agilevisitor.views.fragment.VerifyOTPFragment;
import com.agile.agilevisitor.webapi.ApiClient;
import com.agile.agilevisitor.webapi.ApiInterface;
import com.agile.agilevisitor.webapi.ApiResponse;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnterPasscodeActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    MyTextview tvTitle;
    @BindView(R.id.lin_toolbar)
    LinearLayout linToolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_passcode)
    PinEntryEditText etPasscode;
    @BindView(R.id.btn_verify)
    MyButton btnVerify;
    @BindView(R.id.tv_scan_qr)
    MyButton tvScanQr;

    IntentIntegrator qrScan;

    ApiInterface apiInterface;
    ProgressView progressView;

    String passcode = "";

    int cameraId=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_passcode);
        ButterKnife.bind(this);

        initialize();

    }

    private void initialize() {
        setSupportActionBar(toolbar);
        ivBack.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        linToolbar.setVisibility(View.VISIBLE);

        tvTitle.setText("Invited Visitor");

        ivBack.setOnClickListener(this);
        tvScanQr.setOnClickListener(this);
        btnVerify.setOnClickListener(this);

        qrScan = new IntentIntegrator(EnterPasscodeActivity.this);
        apiInterface = ApiClient.getClient(EnterPasscodeActivity.this, 0).create(ApiInterface.class);
        progressView = new ProgressView(EnterPasscodeActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_verify:

                passcode = etPasscode.getText().toString();

                if (Validation.nullValidator(passcode)) {
                    Toast.makeText(this, "Please Fill The Passcode", Toast.LENGTH_SHORT).show();
                } else if (passcode.length() < 4) {
                    Toast.makeText(this, "Please Enter The Passcode Received", Toast.LENGTH_SHORT).show();
                } else if (passcode.length() == 4) {
                    connectApiToFetchInvitationDetails(passcode, true);
                }

                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_scan_qr:


                if (Camera.getNumberOfCameras() >= 2) {
                    cameraId =Camera.CameraInfo.CAMERA_FACING_FRONT;
                    qrScan.setPrompt("Scan QR Code");
                    qrScan.setCameraId(cameraId);  // Use a specific camera of the device
                    qrScan.setBeepEnabled(true);
                    qrScan.setOrientationLocked(false);
                    qrScan.initiateScan();
                }else if (Camera.getNumberOfCameras()==1){
                    cameraId=Camera.CameraInfo.CAMERA_FACING_BACK;
                    qrScan.setPrompt("Scan QR Code");
                    qrScan.setCameraId(cameraId);  // Use a specific camera of the device
                    qrScan.setBeepEnabled(true);
                    qrScan.setOrientationLocked(false);
                    qrScan.initiateScan();
                }else if (Camera.getNumberOfCameras()==0){
                    Toast.makeText(this, "No Camera Detected Try Entering Passcode", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    private void connectApiToFetchInvitationDetails(String passcode, boolean flag) {
        if (CheckNetworkConnection.isConnection1(EnterPasscodeActivity.this, true)) {
            progressView.showLoader();

            Call<ApiResponse> call = apiInterface.verifyInvitationPasscode(passcode);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    progressView.hideLoader();
                    try {

                        if (response.body().getStatus().equalsIgnoreCase("success")) {

                            PrefData.writeStringPref(PrefData.pref_visiter_id, String.valueOf(response.body().getVisitorId()));
                            PrefData.writeBooleanPref(PrefData.pref_wtm_Called, response.body().isWtmSection());
                            PrefData.writeStringPref(PrefData.pref_invite_id,String.valueOf(response.body().getInvitationId()));

                            if (response.body().isProfileAlreadyExists()) {

                                VerifyOTPFragment.isVisitorProfileAlreadyExist = true;
                                AllDetailsActivity.isInviteDetails=true;
                                AllDetailsActivity.subUnitId=String.valueOf(response.body().getSubUnitId());
                                AllDetailsActivity.employeeId=String.valueOf(response.body().getMeetToId());

                                Intent intent = new Intent(EnterPasscodeActivity.this, ConfirmVisitorDetails.class);
                                intent.putExtra("name", response.body().getName());
                                intent.putExtra("companyName", response.body().getCompanyName());
                                intent.putExtra("email", response.body().getEmail());
                                intent.putExtra("category", response.body().getCategory());
                                intent.putExtra("photo", response.body().getPhoto());
                                intent.putExtra("aadhar", response.body().getAdhharCard());
                                intent.putExtra("mobile", response.body().getMobile());
                                startActivity(intent);

                            } else {

                                VerifyOTPFragment.isVisitorProfileAlreadyExist = false;

                                AllDetailsActivity.isInviteDetails=true;
                                AllDetailsActivity.visitorMobile=response.body().getMobile();
                                AllDetailsActivity.visitorName=response.body().getName();
                                AllDetailsActivity.visitorEmail=response.body().getEmail();
                                AllDetailsActivity.categoryChoosen=response.body().getCategory();
                                AllDetailsActivity.subUnitId=String.valueOf(response.body().getSubUnitId());
                                AllDetailsActivity.employeeId=String.valueOf(response.body().getMeetToId());
                                startActivity(new Intent(EnterPasscodeActivity.this, AllDetailsActivity.class));
                            }

                        } else {
                            Toast.makeText(EnterPasscodeActivity.this, response.body().getMsg(), Toast.LENGTH_LONG).show();
                            etPasscode.setText("");
                        }

                    } catch (Exception e) {
                        if (response.code() == 400) {
                            Toast.makeText(EnterPasscodeActivity.this, "Bad Request!! Please retry.", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(EnterPasscodeActivity.this, "Network Busy.", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(EnterPasscodeActivity.this, "Resource Not Found.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EnterPasscodeActivity.this, "Something went heywire!! please retry.", Toast.LENGTH_SHORT).show();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                try {

                    passcode = result.getContents();
                    connectApiToFetchInvitationDetails(passcode, false);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(this, "Scan Unsuccessful", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
