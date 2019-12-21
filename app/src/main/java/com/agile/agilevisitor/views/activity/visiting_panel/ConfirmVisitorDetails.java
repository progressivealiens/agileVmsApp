package com.agile.agilevisitor.views.activity.visiting_panel;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.agile.agilevisitor.R;
import com.agile.agilevisitor.customviews.MyButton;
import com.agile.agilevisitor.customviews.MyTextview;
import com.agile.agilevisitor.helper.PrefData;
import com.agile.agilevisitor.helper.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfirmVisitorDetails extends AppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    MyTextview tvTitle;
    @BindView(R.id.lin_toolbar)
    LinearLayout linToolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_dialog_visitor_name)
    TextInputEditText etDialogVisitorName;
    @BindView(R.id.et_visitor_company_name)
    TextInputEditText etVisitorCompanyName;
    @BindView(R.id.et_dialog_visitor_email)
    TextInputEditText etDialogVisitorEmail;
    @BindView(R.id.et_dialog_visitor_phone)
    TextInputEditText etDialogVisitorPhone;
    @BindView(R.id.et_dialog_visitor_category)
    TextInputEditText etDialogVisitorCategory;
    @BindView(R.id.iv_dialog_visitor_pic)
    ImageView ivDialogVisitorPic;
    @BindView(R.id.iv_dialog_visitor_aadhar)
    ImageView ivDialogVisitorAadhar;
    @BindView(R.id.btn_dialog_confirm)
    MyButton btnDialogConfirm;
    @BindView(R.id.btn_dialog_cancel)
    MyButton btnDialogCancel;

    public static String name = "", companyName = "", email = "", category = "", photo = "", aadhar = "",mobile="";

    public static String subUnitUniqueId = "", empId = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_visitor_details);
        ButterKnife.bind(this);

        if (getIntent() != null) {
            name = getIntent().getStringExtra("name");
            companyName = getIntent().getStringExtra("companyName");
            email = getIntent().getStringExtra("email");
            category = getIntent().getStringExtra("category");
            photo = getIntent().getStringExtra("photo");
            aadhar = getIntent().getStringExtra("aadhar");
            mobile = getIntent().getStringExtra("mobile");
        }

        initialize();

        btnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllDetailsActivity.isEditDetails = true;
                AllDetailsActivity.visitorName = name;
                AllDetailsActivity.visitorCompanyName = companyName;
                AllDetailsActivity.visitorEmail = email;
                AllDetailsActivity.categoryChoosen = category;
                AllDetailsActivity.selfie = photo;
                AllDetailsActivity.aadhar = aadhar;
                AllDetailsActivity.visitorMobile=mobile;
                startActivity(new Intent(ConfirmVisitorDetails.this, AllDetailsActivity.class));
            }
        });

        btnDialogConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(photo) && TextUtils.isEmpty(aadhar)){
                    Toast.makeText(ConfirmVisitorDetails.this, "Please complete/edit your visiting details to continue", Toast.LENGTH_LONG).show();
                }else{
                    AllDetailsActivity.visitorCompanyName = companyName;
                    startActivity(new Intent(ConfirmVisitorDetails.this, ConfirmVisitorRestDetailsActivity.class));
                }
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void initialize() {
        setSupportActionBar(toolbar);
        ivBack.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("Please Confirm Details");

        etDialogVisitorName.setText(name);
        etVisitorCompanyName.setText(companyName);
        etDialogVisitorEmail.setText(email);
        etDialogVisitorPhone.setText(mobile);
        etDialogVisitorCategory.setText(category);
        Picasso.get().load(Utils.BASE_VISITOR_PIC + photo).placeholder(R.drawable.profile_pic_demo).into(ivDialogVisitorPic);
        Picasso.get().load(Utils.BASE_VISITOR_AADHAR + aadhar).placeholder(R.drawable.aadhar_card_demo).into(ivDialogVisitorAadhar);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
