package com.agile.agilevisitor.views.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.agile.agilevisitor.R;
import com.agile.agilevisitor.customviews.MyButton;
import com.agile.agilevisitor.customviews.MyTextview;
import com.agile.agilevisitor.helper.CameraActivity;
import com.agile.agilevisitor.helper.CheckNetworkConnection;
import com.agile.agilevisitor.helper.PrefData;
import com.agile.agilevisitor.helper.ProgressView;
import com.agile.agilevisitor.helper.Utils;
import com.agile.agilevisitor.views.activity.visiting_panel.AllDetailsActivity;
import com.agile.agilevisitor.views.activity.visiting_panel.ConfirmVisitorDetails;
import com.agile.agilevisitor.views.activity.visiting_panel.ConfirmVisitorRestDetailsActivity;
import com.agile.agilevisitor.views.activity.visiting_panel.GeneratePassActivity;
import com.agile.agilevisitor.webapi.ApiClient;
import com.agile.agilevisitor.webapi.ApiInterface;
import com.agile.agilevisitor.webapi.ApiResponse;
import com.kyanogen.signatureview.SignatureView;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DisclaimerFragment extends Fragment implements View.OnClickListener {

    View v;

    @BindView(R.id.signature_view)
    SignatureView signatureView;
    @BindView(R.id.btn_clear_sign)
    MyButton btnClearSign;
    @BindView(R.id.btn_submit_sign)
    MyButton btnSubmitSign;
    @BindView(R.id.root_disclaimer)
    LinearLayout rootDisclaimer;

    Bitmap bitmap = null;

    public static int aadharRequest = 105;
    String imagePath = "";

    ApiInterface apiInterface;
    ProgressView progressView;

    MultipartBody.Part selfie = null, aadharPic = null;
    RequestBody isSelfieSend = null, isAdhaarCardPhotoSend = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_disclaimer, container, false);
        ButterKnife.bind(this, v);

        initialize();

        return v;
    }

    private void initialize() {

        if (PrefData.readBooleanPref(PrefData.pref_called_from_confirm_fragment)) {
            ConfirmVisitorRestDetailsActivity.confirmTitle.setText("Disclaimer");
        } else {
            AllDetailsActivity.tvTitle.setText("Disclaimer");
        }
        btnClearSign.setOnClickListener(this);
        btnSubmitSign.setOnClickListener(this);

        apiInterface = ApiClient.getClient(getActivity(), 0).create(ApiInterface.class);
        progressView = new ProgressView(getActivity());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_clear_sign) {
            signatureView.clearCanvas();
        } else if (v.getId() == R.id.btn_submit_sign) {
            //bitmap = signatureView.getSignatureBitmap();

            if (PrefData.readBooleanPref(PrefData.pref_called_from_confirm_fragment)) {
                connectApiToSendVisitorPreviousDetails();
            } else {

                if (AllDetailsActivity.isEditDetails) {

                    openDialogToConfirmClickPic(getActivity());

                } else {
                    startActivityForResult(new Intent(getActivity(), CameraActivity.class).putExtra("toolbar_title", "disclaimer"), aadharRequest);
                }
            }

        }
    }


    public void openDialogToConfirmClickPic(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_confirm_to_capture);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = (int) (displaymetrics.widthPixels * 0.8);
        int height = (int) (displaymetrics.heightPixels * 0.5);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        MyButton btnRight, btnWrong;
        ImageView selfieImage;
        MyTextview dialogTitle;
        dialogTitle = dialog.findViewById(R.id.tv_dialog_title);
        selfieImage = dialog.findViewById(R.id.iv_selfie);
        btnRight = dialog.findViewById(R.id.btn_right);
        btnWrong = dialog.findViewById(R.id.btn_wrong);

        dialogTitle.setText("Please Verify Your Aadhar Pic");
        Picasso.get().load(Utils.BASE_VISITOR_AADHAR + AllDetailsActivity.aadhar).placeholder(R.drawable.aadhar_card_demo).into(selfieImage);

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(AllDetailsActivity.aadhar)){
                    Toast.makeText(context, "Please Click Your Aadhar Card Pic To Continue", Toast.LENGTH_LONG).show();
                }else{
                    dialog.dismiss();
                    AllDetailsActivity.isAadharWillBeUsed = true;

                    connectApiToSendVisitorEditedDetails();
                }
            }
        });

        btnWrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                AllDetailsActivity.isAadharWillBeUsed = false;

                startActivityForResult(new Intent(getActivity(), CameraActivity.class).putExtra("toolbar_title", "disclaimer"), aadharRequest);
            }
        });

        dialog.show();
    }

    private void connectApiToSendVisitorPreviousDetails() {
        if (CheckNetworkConnection.isConnection1(getActivity(), true)) {
            progressView.showLoader();

            String type = "";

            if (AllDetailsActivity.isInviteDetails) {
                type="Known";
                ConfirmVisitorDetails.subUnitUniqueId=AllDetailsActivity.subUnitId;
                ConfirmVisitorDetails.empId=AllDetailsActivity.employeeId;
            } else {
                type="Unknown";
            }

            Call<ApiResponse> call = apiInterface.previouslyVisitedNoEdit(
                    PrefData.readStringPref(PrefData.pref_fcm_token),
                    PrefData.readStringPref(PrefData.pref_visiter_id),
                    PrefData.readBooleanPref(PrefData.pref_wtm_Called),
                    ConfirmVisitorDetails.subUnitUniqueId,
                    ConfirmVisitorDetails.empId,
                    type,
                    PrefData.readStringPref(PrefData.pref_invite_id)
                    );
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    progressView.hideLoader();

                    try {

                        if (response.body().getStatus().equalsIgnoreCase(getString(R.string.success))) {

                            //Toast.makeText(getActivity(), "Successfully Submitted Data", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), GeneratePassActivity.class));
                            getActivity().finish();

                        } else {
                            Utils.showSnackBar(rootDisclaimer, response.body().getMsg(), getActivity());
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == aadharRequest) {
            if (resultCode == Activity.RESULT_OK) {

                imagePath = data.getStringExtra("result");
                AllDetailsActivity.visitorAadharFile = new File(imagePath);
                Log.e("Image", imagePath);

                if (AllDetailsActivity.isEditDetails) {
                    connectApiToSendVisitorEditedDetails();
                } else {
                    connectApiToSendVisitorAllDetails();
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Camera Closed Activity", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void connectApiToSendVisitorAllDetails() {
        if (CheckNetworkConnection.isConnection1(getActivity(), true)) {
            progressView.showLoader();

            MultipartBody.Part filePart = MultipartBody.Part.createFormData("photo", AllDetailsActivity.visitorImageFile.getName(), RequestBody.create(MediaType.parse("image/*"), AllDetailsActivity.visitorImageFile));
            MultipartBody.Part filePart1 = MultipartBody.Part.createFormData("adhaarCardPhoto", AllDetailsActivity.visitorAadharFile.getName(), RequestBody.create(MediaType.parse("image/*"), AllDetailsActivity.visitorAadharFile));
            RequestBody Token = RequestBody.create(MediaType.parse("text/plain"), PrefData.readStringPref(PrefData.pref_fcm_token));
            RequestBody VisitorId = RequestBody.create(MediaType.parse("text/plain"), PrefData.readStringPref(PrefData.pref_visiter_id));
            RequestBody MobileNumber = RequestBody.create(MediaType.parse("text/plain"), AllDetailsActivity.visitorMobile);
            RequestBody Name = RequestBody.create(MediaType.parse("text/plain"), AllDetailsActivity.visitorName);
            RequestBody companyName = RequestBody.create(MediaType.parse("text/plain"), AllDetailsActivity.visitorCompanyName);
            RequestBody Email = RequestBody.create(MediaType.parse("text/plain"), AllDetailsActivity.visitorEmail);
            RequestBody Category = RequestBody.create(MediaType.parse("text/plain"), AllDetailsActivity.categoryChoosen);
            RequestBody SubUnitId = RequestBody.create(MediaType.parse("text/plain"), AllDetailsActivity.subUnitId);
            RequestBody MeetToId = RequestBody.create(MediaType.parse("text/plain"), AllDetailsActivity.employeeId);
            RequestBody invitationId = RequestBody.create(MediaType.parse("text/plain"), PrefData.readStringPref(PrefData.pref_invite_id));

            RequestBody type = null;

            if (AllDetailsActivity.isInviteDetails) {
                type = RequestBody.create(MediaType.parse("text/plain"), "Known");
            } else {
                type = RequestBody.create(MediaType.parse("text/plain"), "Unknown");
            }

            Call<ApiResponse> call = apiInterface.completeProfileDetails(
                    Token,
                    VisitorId,
                    MobileNumber,
                    Name,
                    companyName,
                    Email,
                    Category,
                    filePart,
                    filePart1,
                    MeetToId,
                    SubUnitId,
                    type,
                    invitationId
            );
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    progressView.hideLoader();

                    try {

                        if (response.body().getStatus().equalsIgnoreCase(getString(R.string.success))) {

                            Toast.makeText(getActivity(), "Successfully Submitted Data", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), GeneratePassActivity.class));
                            getActivity().finish();

                        } else {

                            Utils.showSnackBar(rootDisclaimer, response.body().getMsg(), getActivity());

                        }

                    } catch (Exception e) {
                        if (response.code() == 400) {
                            Toast.makeText(getActivity(), "Bad Request!! Please retry.", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(getActivity(), "Network Busy.", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(getActivity(), "Resource Not Found.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Something went heywire!! please retry.", Toast.LENGTH_SHORT).show();
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

    private void connectApiToSendVisitorEditedDetails() {

        if (CheckNetworkConnection.isConnection1(getActivity(), true)) {
            progressView.showLoader();

            if (AllDetailsActivity.isSelfieWillBeUsed) {
                selfie = MultipartBody.Part.createFormData("photo", "", RequestBody.create(MediaType.parse("text/plain"), ""));
            } else {
                selfie = MultipartBody.Part.createFormData("photo", AllDetailsActivity.visitorImageFile.getName(), RequestBody.create(MediaType.parse("image/*"), AllDetailsActivity.visitorImageFile));
            }

            if (AllDetailsActivity.isAadharWillBeUsed) {
                aadharPic = MultipartBody.Part.createFormData("adhaarCardPhoto", "", RequestBody.create(MediaType.parse("text/plain"), ""));
            } else {
                aadharPic = MultipartBody.Part.createFormData("adhaarCardPhoto", AllDetailsActivity.visitorAadharFile.getName(), RequestBody.create(MediaType.parse("image/*"), AllDetailsActivity.visitorAadharFile));
            }

            RequestBody Token = RequestBody.create(MediaType.parse("text/plain"), PrefData.readStringPref(PrefData.pref_fcm_token));
            RequestBody VisitorId = RequestBody.create(MediaType.parse("text/plain"), PrefData.readStringPref(PrefData.pref_visiter_id));
            RequestBody MobileNumber = RequestBody.create(MediaType.parse("text/plain"), AllDetailsActivity.visitorMobile);
            RequestBody Name = RequestBody.create(MediaType.parse("text/plain"), AllDetailsActivity.visitorName);
            RequestBody companyName = RequestBody.create(MediaType.parse("text/plain"), AllDetailsActivity.visitorCompanyName);
            RequestBody Email = RequestBody.create(MediaType.parse("text/plain"), AllDetailsActivity.visitorEmail);
            RequestBody Category = RequestBody.create(MediaType.parse("text/plain"), AllDetailsActivity.categoryChoosen);
            RequestBody SubUnitId = RequestBody.create(MediaType.parse("text/plain"), AllDetailsActivity.subUnitId);
            RequestBody MeetToId = RequestBody.create(MediaType.parse("text/plain"), AllDetailsActivity.employeeId);
            RequestBody invitationId = RequestBody.create(MediaType.parse("text/plain"), PrefData.readStringPref(PrefData.pref_invite_id));

            if (AllDetailsActivity.isSelfieWillBeUsed) {
                isSelfieSend = RequestBody.create(MediaType.parse("text/plain"), "false");
            } else {
                isSelfieSend = RequestBody.create(MediaType.parse("text/plain"), "true");
            }


            if (AllDetailsActivity.isAadharWillBeUsed) {
                isAdhaarCardPhotoSend = RequestBody.create(MediaType.parse("text/plain"), "false");
            } else {
                isAdhaarCardPhotoSend = RequestBody.create(MediaType.parse("text/plain"), "true");
            }


            RequestBody type = null;

            if (AllDetailsActivity.isInviteDetails) {
                type = RequestBody.create(MediaType.parse("text/plain"), "Known");
            } else {
                type = RequestBody.create(MediaType.parse("text/plain"), "Unknown");
            }


            Call<ApiResponse> call = apiInterface.previouslyVisitedEdit(
                    Token,
                    VisitorId,
                    MobileNumber,
                    Name,
                    companyName,
                    Email,
                    Category,
                    selfie,
                    aadharPic,
                    MeetToId,
                    SubUnitId,
                    isSelfieSend,
                    isAdhaarCardPhotoSend,
                    type,
                    invitationId
            );
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    progressView.hideLoader();

                    try {

                        if (response.body().getStatus().equalsIgnoreCase(getString(R.string.success))) {

                            startActivity(new Intent(getActivity(), GeneratePassActivity.class));
                            getActivity().finish();

                        } else {

                            Utils.showSnackBar(rootDisclaimer, response.body().getMsg(), getActivity());

                        }

                    } catch (Exception e) {
                        if (response.code() == 400) {
                            Toast.makeText(getActivity(), "Bad Request!! Please retry.", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 500) {
                            Toast.makeText(getActivity(), "Network Busy.", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(getActivity(), "Resource Not Found.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Something went heywire!! please retry.", Toast.LENGTH_SHORT).show();
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
