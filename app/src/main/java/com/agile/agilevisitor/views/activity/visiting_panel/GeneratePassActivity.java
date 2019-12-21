package com.agile.agilevisitor.views.activity.visiting_panel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.agile.agilevisitor.R;
import com.agile.agilevisitor.helper.PrefData;
import com.agile.agilevisitor.helper.Utils;
import com.agile.agilevisitor.views.activity.user_panel.UserCheckinInvitation;
import com.agile.agilevisitor.views.activity.user_panel.UserHomeActivity;
import com.agile.agilevisitor.views.fragment.VerifyOTPFragment;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.agile.agilevisitor.views.activity.visiting_panel.ConfirmVisitorDetails.photo;

public class GeneratePassActivity extends AppCompatActivity {

    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_mobile_number)
    TextView tvMobileNumber;
    @BindView(R.id.tv_whome_to_meet)
    TextView tvWhomeToMeet;
    @BindView(R.id.btn_print_pass)
    Button btnPrintPass;
    @BindView(R.id.iv_visitor_pic)
    CircleImageView ivVisitorPic;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_company_name)
    TextView tvCompanyName;

    long currentDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_pass);
        ButterKnife.bind(this);


        if (PrefData.readBooleanPref(PrefData.pref_wtm_Called)) {
            tvWhomeToMeet.setText("Meeting With : " + PrefData.readStringPref(PrefData.pref_employee_name));

        } else {
            tvWhomeToMeet.setVisibility(View.GONE);
        }

        tvCompanyName.setText("Company Name : "+AllDetailsActivity.visitorCompanyName);


        if (VerifyOTPFragment.isVisitorProfileAlreadyExist){

            if (AllDetailsActivity.isEditDetails) {
                tvName.setText("Name : "+AllDetailsActivity.visitorName);
            } else {
                tvName.setText("Name : "+ConfirmVisitorDetails.name);
            }

        }else{
            tvName.setText("Name : "+AllDetailsActivity.visitorName);
        }

        tvMobileNumber.setText("Mobile Number : " + AllDetailsActivity.visitorMobile);

        if (AllDetailsActivity.isEditDetails) {
            if (AllDetailsActivity.isSelfieWillBeUsed) {
                Picasso.get().load(Utils.BASE_VISITOR_PIC + AllDetailsActivity.selfie).placeholder(R.drawable.progress_animation).into(ivVisitorPic);
            } else {
                Picasso.get().load(AllDetailsActivity.visitorImageFile).placeholder(R.drawable.progress_animation).into(ivVisitorPic);
            }

        } else {

            if (VerifyOTPFragment.isVisitorProfileAlreadyExist) {
                Picasso.get().load(Utils.BASE_VISITOR_PIC + photo).placeholder(R.drawable.progress_animation).into(ivVisitorPic);
            } else {
                Picasso.get().load(AllDetailsActivity.visitorImageFile).placeholder(R.drawable.progress_animation).into(ivVisitorPic);
            }
        }


        currentDateTime = Long.valueOf(Utils.currentTimeStamp());
        tvDate.setText("Date : "+Utils.selectedDateFormat(currentDateTime, tvDate));
        tvTime.setText("Time : "+Utils.selectedDateAndTimeFormat(currentDateTime, tvTime));

        /*tvCheckinDateTime.setText("Checked in At :- " + Utils.selectedDateAndTimeFormat(currentDateTime, tvCheckinDateTime) + "\n Date :- " + Utils.selectedDateFormat(currentDateTime, tvCheckinDateTime));*/
        //tvCheckinDateTime.setText("");

        btnPrintPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PrefData.writeBooleanPref(PrefData.pref_called_from_confirm_fragment, false);
                PrefData.writeStringPref(PrefData.pref_invite_id,"");
                AllDetailsActivity.isEditDetails = false;
                AllDetailsActivity.isInviteDetails=false;
                AllDetailsActivity.visitorMobile="";
                AllDetailsActivity.categoryChoosen = "";
                AllDetailsActivity.visitorName="";
                AllDetailsActivity.visitorEmail="";
                AllDetailsActivity.visitorCompanyName="";
                AllDetailsActivity.subUnitId="";
                AllDetailsActivity.employeeId="";
                AllDetailsActivity.selfie="";
                AllDetailsActivity.aadhar="";
                AllDetailsActivity.inviteId="";

                Toast.makeText(GeneratePassActivity.this, "CheckIn Successfully", Toast.LENGTH_LONG).show();

                Intent intent=new Intent(GeneratePassActivity.this, VisitingLandingScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });
    }
}
