package com.agile.agilevisitor.views.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.agile.agilevisitor.R;
import com.agile.agilevisitor.customviews.MyButton;
import com.agile.agilevisitor.customviews.MyTextview;
import com.agile.agilevisitor.helper.CheckNetworkConnection;
import com.agile.agilevisitor.helper.PinEntryEditText;
import com.agile.agilevisitor.helper.PrefData;
import com.agile.agilevisitor.helper.ProgressView;
import com.agile.agilevisitor.helper.Utils;
import com.agile.agilevisitor.views.activity.visiting_panel.AllDetailsActivity;
import com.agile.agilevisitor.views.activity.visiting_panel.ConfirmVisitorDetails;
import com.agile.agilevisitor.views.activity.visiting_panel.VisitingHomePanel;
import com.agile.agilevisitor.webapi.ApiClient;
import com.agile.agilevisitor.webapi.ApiInterface;
import com.agile.agilevisitor.webapi.ApiResponse;
import com.agile.agilevisitor.webapi.OTPResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOTPFragment extends Fragment implements View.OnClickListener {

    View v;
    @BindView(R.id.otp_view)
    PinEntryEditText otpView;
    @BindView(R.id.tvTimer)
    MyTextview tvTimer;
    @BindView(R.id.tvResendOtp)
    MyTextview tvResendOtp;
    @BindView(R.id.btnOtpSubmit)
    MyButton btnOtpSubmit;
    @BindView(R.id.tv_change_number)
    MyTextview tvChangeNumber;
    @BindView(R.id.root_verify_otp)
    RelativeLayout rootVerifyOtp;
    @BindView(R.id.text)
    MyTextview text;

    CountDownTimer countDownTimer;

    ApiInterface apiInterface, apiInterfaceOtp;
    ProgressView progressView;
    PrefData prefData;

    public static boolean isVisitorProfileAlreadyExist=false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_verify_otp, container, false);
        ButterKnife.bind(this, v);


        initialize();
        startCountdownTimer();


        return v;
    }

    private void initialize() {
        tvResendOtp.setVisibility(View.GONE);
        tvTimer.setVisibility(View.VISIBLE);

        tvResendOtp.setOnClickListener(this);
        btnOtpSubmit.setOnClickListener(this);
        tvChangeNumber.setOnClickListener(this);

        apiInterface = ApiClient.getClient(getActivity(), 0).create(ApiInterface.class);
        apiInterfaceOtp = ApiClient.getOTPClient(getActivity()).create(ApiInterface.class);
        progressView = new ProgressView(getActivity());
        prefData = new PrefData(getActivity());

        text.setText("Enter Your Verification Code \nsent to " + PrefData.readStringPref(PrefData.pref_visiting_mobile));
    }

    private void startCountdownTimer() {
        countDownTimer = new CountDownTimer(1000 * 60, 1000) {
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("" + millisUntilFinished / 1000 + " Sec.");
            }

            public void onFinish() {
                tvTimer.setVisibility(View.INVISIBLE);
                tvResendOtp.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvResendOtp:

                connectApiToSendOtpToVisitorMobile(PrefData.readStringPref(PrefData.pref_visiting_mobile));

                break;
            case R.id.btnOtpSubmit:

                if (otpView.getText().toString().equalsIgnoreCase("")) {
                    Utils.showSnackBar(rootVerifyOtp, "Please enter OTP", otpView, getActivity());
                } else {

                    connectApiToVerifyOtp(otpView.getText().toString());

                }

                break;
            case R.id.tv_change_number:
                Utils.changeFragmentHome(new NewMobileNumber(), "frag_new_mobile", VisitingHomePanel.activity);
                break;

        }

    }

    private void connectApiToSendOtpToVisitorMobile(String mobileNum) {

        if (CheckNetworkConnection.isConnection1(getActivity(), true)) {
            progressView.showLoader();

            Call<OTPResponse> call = apiInterfaceOtp.sendOTP(mobileNum);
            call.enqueue(new Callback<OTPResponse>() {
                @Override
                public void onResponse(Call<OTPResponse> call, Response<OTPResponse> response) {
                    progressView.hideLoader();

                    try {

                        if (response.body().getStatus().equalsIgnoreCase(getString(R.string.success))) {

                            PrefData.writeStringPref(PrefData.pref_visitor_mobile_details,response.body().getDetails());

                            Toast.makeText(getActivity(), "Verification code has been send successfully", Toast.LENGTH_SHORT).show();

                            tvResendOtp.setVisibility(View.GONE);
                            tvTimer.setVisibility(View.VISIBLE);
                            startCountdownTimer();


                        } else {
                            Utils.showSnackBar(rootVerifyOtp, response.body().getDetails(), getActivity());
                        }
                    } catch (Exception e) {
                        if (response.code() == 400) {
                            Toast.makeText(getActivity(), getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(getActivity(), getString(R.string.network_busy), Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(getActivity(), getString(R.string.resource_not_found), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                        e.printStackTrace();

                    }
                }

                @Override
                public void onFailure(Call<OTPResponse> call, Throwable t) {
                    progressView.hideLoader();
                    t.printStackTrace();
                }
            });
        }
    }

    private void connectApiToVerifyOtp(String otp) {
        if (CheckNetworkConnection.isConnection1(getActivity(), true)) {
            progressView.showLoader();

            Call<OTPResponse> call = apiInterfaceOtp.verifyOTP(
                    PrefData.readStringPref(PrefData.pref_visitor_mobile_details),
                    otp
            );
            call.enqueue(new Callback<OTPResponse>() {
                @Override
                public void onResponse(Call<OTPResponse> call, Response<OTPResponse> response) {
                    progressView.hideLoader();

                    try {

                        if (response.body().getStatus().equalsIgnoreCase(getString(R.string.success))) {


                            connectApiToUpdateMobileNumber();


                        } else {
                            Utils.showSnackBar(rootVerifyOtp, response.body().getDetails(), getActivity());
                        }
                    } catch (Exception e) {
                        if (response.code() == 400) {
                            Toast.makeText(getActivity(), "OTP doesn't matched...", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(getActivity(), getString(R.string.network_busy), Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(getActivity(), getString(R.string.resource_not_found), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                        e.printStackTrace();

                    }
                }

                @Override
                public void onFailure(Call<OTPResponse> call, Throwable t) {
                    progressView.hideLoader();
                    t.printStackTrace();
                }
            });
        }
    }

    private void connectApiToUpdateMobileNumber() {
        if (CheckNetworkConnection.isConnection1(getActivity(), true)) {
            progressView.showLoader();

            Call<ApiResponse> call = apiInterface.registerVisitor(
                    PrefData.readStringPref(PrefData.pref_visiting_mobile),
                    PrefData.readStringPref(PrefData.pref_fcm_token));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    progressView.hideLoader();

                    try {

                        if (response.body().getStatus().equalsIgnoreCase(getString(R.string.success))) {

                            PrefData.writeStringPref(PrefData.pref_visiter_id, String.valueOf(response.body().getVisitorId()));
                            PrefData.writeBooleanPref(PrefData.pref_wtm_Called, response.body().isWtmSection());

                            if (response.body().isProfileAlreadyExists()) {

                                isVisitorProfileAlreadyExist=true;

                                Intent intent = new Intent(getActivity(), ConfirmVisitorDetails.class);
                                intent.putExtra("name", response.body().getName());
                                intent.putExtra("companyName",response.body().getCompanyName());
                                intent.putExtra("email", response.body().getEmail());
                                intent.putExtra("category", response.body().getCategory());
                                intent.putExtra("photo", response.body().getPhoto());
                                intent.putExtra("aadhar", response.body().getAdhharCard());
                                intent.putExtra("mobile", PrefData.readStringPref(PrefData.pref_visiting_mobile));
                                startActivity(intent);

                                Toast.makeText(getActivity(), "OTP Verified Successfully ", Toast.LENGTH_SHORT).show();

                            } else {

                                isVisitorProfileAlreadyExist=false;
                                startActivity(new Intent(getActivity(), AllDetailsActivity.class));
                                Toast.makeText(getActivity(), "OTP Verified Successfully ", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Utils.showSnackBar(rootVerifyOtp, response.body().getMsg(), getActivity());
                        }
                    } catch (Exception e) {
                        if (response.code() == 400) {
                            Toast.makeText(getActivity(), getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(getActivity(), getString(R.string.network_busy), Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(getActivity(), getString(R.string.resource_not_found), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
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
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

}
