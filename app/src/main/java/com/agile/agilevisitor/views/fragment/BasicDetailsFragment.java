package com.agile.agilevisitor.views.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import com.agile.agilevisitor.helper.CameraActivity;
import com.agile.agilevisitor.helper.PrefData;
import com.agile.agilevisitor.helper.Utils;
import com.agile.agilevisitor.helper.Validation;
import com.agile.agilevisitor.views.activity.visiting_panel.AllDetailsActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BasicDetailsFragment extends Fragment {

    View v;

    @BindView(R.id.et_visitor_name)
    TextInputEditText etVisitorName;
    @BindView(R.id.et_visitor_email)
    TextInputEditText etVisitorEmail;
    @BindView(R.id.et_visitor_phone)
    TextInputEditText etVisitorPhone;
    @BindView(R.id.et_visitor_company_name)
    TextInputEditText etVisitorCompanyName;
    @BindView(R.id.root_basic_details)
    LinearLayout rootBasicDetails;
    @BindView(R.id.btn_basic_proceed)
    MyButton btnBasicProceed;

    public static int selfieRequest = 100;
    public static String imagePath = "";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_basic_details, container, false);
        ButterKnife.bind(this, v);

        initialize();

        btnBasicProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Validation.nullValidator(etVisitorName.getText().toString())) {
                    Utils.showSnackBar(rootBasicDetails, "Please fill your name", etVisitorName, getActivity());
                } else if (Validation.nullValidator(etVisitorCompanyName.getText().toString())) {
                    Utils.showSnackBar(rootBasicDetails, "Company Name can't be null", etVisitorEmail, getActivity());
                } else if (Validation.nullValidator(etVisitorEmail.getText().toString())) {
                    Utils.showSnackBar(rootBasicDetails, "Emailid can't be null", etVisitorEmail, getActivity());
                } else if (!Validation.emailValidator(etVisitorEmail.getText().toString())) {
                    Utils.showSnackBar(rootBasicDetails, "Please enter the email id in proper format", etVisitorEmail, getActivity());
                } else {

                    if (AllDetailsActivity.isEditDetails) {
                        openDialogToConfirmClickPic(getActivity());
                    } else {
                        AllDetailsActivity.visitorName = etVisitorName.getText().toString();
                        AllDetailsActivity.visitorEmail = etVisitorEmail.getText().toString();
                        AllDetailsActivity.visitorMobile = etVisitorPhone.getText().toString().trim();
                        AllDetailsActivity.visitorCompanyName = etVisitorCompanyName.getText().toString();

                        startActivityForResult(new Intent(getActivity(), CameraActivity.class).putExtra("toolbar_title", "basic"), selfieRequest);
                    }
                }
            }
        });

        return v;
    }

    private void initialize() {

        if (AllDetailsActivity.isEditDetails || AllDetailsActivity.isInviteDetails) {
            AllDetailsActivity.tvTitle.setText("Verify Basic Details");
        } else {
            AllDetailsActivity.tvTitle.setText("Add Basic Details");
        }

        if (AllDetailsActivity.isEditDetails || AllDetailsActivity.isInviteDetails) {
            etVisitorName.setText(AllDetailsActivity.visitorName);
            etVisitorEmail.setText(AllDetailsActivity.visitorEmail);
            etVisitorCompanyName.setText(AllDetailsActivity.visitorCompanyName);
            etVisitorPhone.setText(AllDetailsActivity.visitorMobile);
        } else {
            etVisitorPhone.setText(PrefData.readStringPref(PrefData.pref_visiting_mobile));
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
        selfieImage = dialog.findViewById(R.id.iv_selfie);
        btnRight = dialog.findViewById(R.id.btn_right);
        btnWrong = dialog.findViewById(R.id.btn_wrong);

        Picasso.get().load(Utils.BASE_VISITOR_PIC + AllDetailsActivity.selfie).placeholder(R.drawable.profile_pic_demo).into(selfieImage);

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(AllDetailsActivity.selfie)) {

                    Toast.makeText(context, "Please Click Your Picture To Continue", Toast.LENGTH_LONG).show();

                } else {

                    dialog.dismiss();
                    AllDetailsActivity.isSelfieWillBeUsed = true;

                    AllDetailsActivity.visitorName = etVisitorName.getText().toString();
                    AllDetailsActivity.visitorEmail = etVisitorEmail.getText().toString();
                    AllDetailsActivity.visitorCompanyName = etVisitorCompanyName.getText().toString();

                    if (PrefData.readBooleanPref(PrefData.pref_wtm_Called)) {
                        PrefData.writeBooleanPref(PrefData.pref_called_from_confirm_fragment, false);
                        Utils.changeFragmentAllDetails(new WhomeToMeetFragment(), "frag_wtm", AllDetailsActivity.activity);
                    } else {
                        PrefData.writeBooleanPref(PrefData.pref_called_from_confirm_fragment, false);
                        Utils.changeFragmentAllDetails(new DisclaimerFragment(), "frag_disclaimer", AllDetailsActivity.activity);
                    }
                }
            }
        });

        btnWrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                AllDetailsActivity.isSelfieWillBeUsed = false;

                AllDetailsActivity.visitorName = etVisitorName.getText().toString();
                AllDetailsActivity.visitorEmail = etVisitorEmail.getText().toString();
                AllDetailsActivity.visitorCompanyName = etVisitorCompanyName.getText().toString().trim();

                startActivityForResult(new Intent(getActivity(), CameraActivity.class).putExtra("toolbar_title", "basic"), selfieRequest);
            }
        });

        dialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == selfieRequest) {
            if (resultCode == Activity.RESULT_OK) {
                imagePath = data.getStringExtra("result");
                AllDetailsActivity.visitorImageFile = new File(imagePath);

                Log.e("Image", imagePath);

                if (PrefData.readBooleanPref(PrefData.pref_wtm_Called)) {
                    PrefData.writeBooleanPref(PrefData.pref_called_from_confirm_fragment, false);
                    Utils.changeFragmentAllDetails(new WhomeToMeetFragment(), "frag_wtm", AllDetailsActivity.activity);
                } else {
                    PrefData.writeBooleanPref(PrefData.pref_called_from_confirm_fragment, false);
                    Utils.changeFragmentAllDetails(new DisclaimerFragment(), "frag_disclaimer", AllDetailsActivity.activity);
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Camera Closed Activity", Toast.LENGTH_SHORT).show();
            }
        }
    }

}