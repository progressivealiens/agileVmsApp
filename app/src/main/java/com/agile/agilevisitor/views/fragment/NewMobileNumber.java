package com.agile.agilevisitor.views.fragment;

import android.os.Bundle;
import android.util.Log;
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
import com.agile.agilevisitor.helper.CheckNetworkConnection;
import com.agile.agilevisitor.helper.PrefData;
import com.agile.agilevisitor.helper.ProgressView;
import com.agile.agilevisitor.helper.Utils;
import com.agile.agilevisitor.helper.Validation;
import com.agile.agilevisitor.views.activity.visiting_panel.VisitingHomePanel;
import com.agile.agilevisitor.webapi.ApiClient;
import com.agile.agilevisitor.webapi.ApiInterface;
import com.agile.agilevisitor.webapi.OTPResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewMobileNumber extends Fragment {

    View v;

    @BindView(R.id.btn_send_otp)
    MyButton btnSendOtp;
    @BindView(R.id.root_frag_mobile)
    RelativeLayout rootFragMobile;
    @BindView(R.id.et_mobile_number)
    TextInputEditText etMobileNumber;

    ApiInterface apiInterface;
    ProgressView progressView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_new_number, container, false);
        ButterKnife.bind(this, v);

        Log.e("token",PrefData.readStringPref(PrefData.pref_fcm_token));

        initialize();

        btnSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Validation.nullValidator(etMobileNumber.getText().toString())){
                    Utils.showSnackBar(rootFragMobile, "Mobile Number can't be null", etMobileNumber, getActivity());
                }else if (!Validation.mobileValidator(etMobileNumber.getText().toString())){
                    Utils.showSnackBar(rootFragMobile, "Mobile Number must be 10 digits long", etMobileNumber, getActivity());
                }else{

                    connectApiToSendOtpToVisitorMobile(etMobileNumber.getText().toString());

                }
            }
        });
        return v;
    }

    private void initialize() {
        apiInterface = ApiClient.getOTPClient(getActivity()).create(ApiInterface.class);
        progressView = new ProgressView(getActivity());
    }

    private void connectApiToSendOtpToVisitorMobile(String mobileNum) {

        if (CheckNetworkConnection.isConnection1(getActivity(), true)) {
            progressView.showLoader();

            Call<OTPResponse> call = apiInterface.sendOTP(mobileNum);
            call.enqueue(new Callback<OTPResponse>() {
                @Override
                public void onResponse(Call<OTPResponse> call, Response<OTPResponse> response) {
                    progressView.hideLoader();

                    try {

                        if (response.body().getStatus().equalsIgnoreCase(getString(R.string.success))) {

                            PrefData.writeStringPref(PrefData.pref_visiting_mobile,mobileNum);
                            PrefData.writeStringPref(PrefData.pref_visitor_mobile_details,response.body().getDetails());
                            Utils.hideSoftKeyboard(getActivity());
                            Utils.changeFragmentHome(new VerifyOTPFragment(), "frag_verify_otp", VisitingHomePanel.activity);

                        } else {
                            Utils.showSnackBar(rootFragMobile, response.body().getDetails(), getActivity());
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


}
