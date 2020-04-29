package com.agile.agilevisitor.views.activity.visiting_panel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.agile.agilevisitor.R;
import com.agile.agilevisitor.customviews.MyTextview;
import com.agile.agilevisitor.helper.CameraActivity;
import com.agile.agilevisitor.helper.CheckNetworkConnection;
import com.agile.agilevisitor.helper.PrefData;
import com.agile.agilevisitor.helper.ProgressView;
import com.agile.agilevisitor.helper.Utils;
import com.agile.agilevisitor.webapi.ApiClient;
import com.agile.agilevisitor.webapi.ApiInterface;
import com.agile.agilevisitor.webapi.ApiResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    MyTextview tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_unit_name)
    TextView tvUnitName;
    @BindView(R.id.btn_click_photo)
    Button btnClickPhoto;
    @BindView(R.id.et_mobile_number)
    TextInputEditText etMobileNumber;
    @BindView(R.id.root_checkout)
    LinearLayout rootCheckout;
    @BindView(R.id.ccp)
    CountryCodePicker countryCodePicker;

    String checkoutPhoto = "";

    public static int photoRequest = 105;
    File photoImageFile;

    ApiInterface apiInterface;
    ProgressView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        ButterKnife.bind(this);

        initialize();
        btnClickPhoto.setOnClickListener(this);

    }

    private void initialize() {
        setSupportActionBar(toolbar);
        ivBack.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tvTitle.setText("Checkout");
        tvUnitName.setText(PrefData.readStringPref(PrefData.pref_unit_name));

        apiInterface = ApiClient.getClient(CheckoutActivity.this, 0).create(ApiInterface.class);
        progressView = new ProgressView(CheckoutActivity.this);
        countryCodePicker.registerCarrierNumberEditText(etMobileNumber);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_click_photo) {

            countryCodePicker.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
                @Override
                public void onValidityChanged(boolean isValidNumber) {
                    if (!isValidNumber) {
                        Utils.showSnackBar(rootCheckout, "Please write the mobile number in proper format", etMobileNumber,CheckoutActivity.this);
                    } else {
                        startActivityForResult(new Intent(CheckoutActivity.this, CameraActivity.class).putExtra("toolbar_title", "basic"), photoRequest);
                    }
                }
            });
        }
    }

    private void connectApiToCheckout() {

        if (CheckNetworkConnection.isConnection1(CheckoutActivity.this, true)) {
            progressView.showLoader();

            MultipartBody.Part filePart = MultipartBody.Part.createFormData("checkOutPhoto", photoImageFile.getName(), RequestBody.create(MediaType.parse("image/*"), photoImageFile));
            RequestBody Token = RequestBody.create(MediaType.parse("text/plain"), PrefData.readStringPref(PrefData.pref_fcm_token));
            RequestBody MobileNumber = RequestBody.create(MediaType.parse("text/plain"), countryCodePicker.getFullNumberWithPlus());

            Call<ApiResponse> call = apiInterface.panelVisitorCheckOut(
                    Token,
                    MobileNumber,
                    filePart
            );
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    progressView.hideLoader();

                    try {

                        if (response.body().getStatus().equalsIgnoreCase(getString(R.string.success))) {

                            Toast.makeText(CheckoutActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(CheckoutActivity.this, VisitingLandingScreen.class));
                            finish();

                        } else {

                            Utils.showSnackBar(rootCheckout, response.body().getMsg(), CheckoutActivity.this);

                        }

                    } catch (Exception e) {
                        if (response.code() == 400) {
                            Toast.makeText(CheckoutActivity.this, "Bad Request!! Please retry.", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(CheckoutActivity.this, "Network Busy.", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(CheckoutActivity.this, "Resource Not Found.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CheckoutActivity.this, "Something went heywire!! please retry.", Toast.LENGTH_SHORT).show();
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
        if (requestCode == photoRequest) {
            if (resultCode == Activity.RESULT_OK) {
                checkoutPhoto = data.getStringExtra("result");
                photoImageFile = new File(checkoutPhoto);

                connectApiToCheckout();

            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(CheckoutActivity.this, "Camera Closed Activity", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
