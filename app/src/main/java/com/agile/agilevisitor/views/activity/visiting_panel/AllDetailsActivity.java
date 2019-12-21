package com.agile.agilevisitor.views.activity.visiting_panel;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.agile.agilevisitor.R;
import com.agile.agilevisitor.customviews.MyTextview;
import com.agile.agilevisitor.helper.PrefData;
import com.agile.agilevisitor.helper.Utils;
import com.agile.agilevisitor.views.fragment.BasicDetailsFragment;
import com.agile.agilevisitor.views.fragment.DisclaimerFragment;
import com.agile.agilevisitor.views.fragment.SelectCategoryFragment;
import com.agile.agilevisitor.views.fragment.WhomeToMeetFragment;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllDetailsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    public static MyTextview tvTitle;

    public static AppCompatActivity activity;

    public static String visitorMobile="",categoryChoosen = "",visitorName="",visitorEmail="",visitorCompanyName="",subUnitId="",employeeId="",selfie="",aadhar="",inviteId="";
    public static File visitorImageFile,visitorAadharFile;
    public static boolean isEditDetails=false,isInviteDetails=false;
    public static boolean isSelfieWillBeUsed =false, isAadharWillBeUsed =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_details);
        ButterKnife.bind(this);

        initialize();

        Utils.changeFragmentAllDetails(new SelectCategoryFragment(), "frag_select_category", activity);
    }

    private void initialize() {
        activity = this;
        tvTitle=findViewById(R.id.tv_title);
        setSupportActionBar(toolbar);
        ivBack.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_all_details);

        if (currentFragment instanceof SelectCategoryFragment) {
            finish();
        } else if (currentFragment instanceof BasicDetailsFragment) {
            currentFragment.getChildFragmentManager().popBackStack("frag_select_category", 0);
        } else if (currentFragment instanceof WhomeToMeetFragment) {
            currentFragment.getChildFragmentManager().popBackStack("frag_basic_details", 0);
        } else if (currentFragment instanceof DisclaimerFragment) {
            if (PrefData.readBooleanPref(PrefData.pref_wtm_Called)) {
                currentFragment.getChildFragmentManager().popBackStack("frag_wtm", 0);
            } else {
                currentFragment.getChildFragmentManager().popBackStack("frag_basic_details", 0);
            }
        }

        super.onBackPressed();
    }
}
