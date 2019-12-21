package com.agile.agilevisitor.views.activity.visiting_panel;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.agile.agilevisitor.R;
import com.agile.agilevisitor.customviews.MyTextview;
import com.agile.agilevisitor.helper.PrefData;
import com.agile.agilevisitor.helper.Utils;
import com.agile.agilevisitor.views.fragment.DisclaimerFragment;
import com.agile.agilevisitor.views.fragment.WhomeToMeetFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfirmVisitorRestDetailsActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.lin_toolbar)
    LinearLayout linToolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.frame_confirm_visitor_rest)
    FrameLayout frameConfirmVisitorRest;

    public static MyTextview confirmTitle;
    public static AppCompatActivity confirmActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_visitor_rest_details);
        ButterKnife.bind(this);

        initialize();

        if (PrefData.readBooleanPref(PrefData.pref_wtm_Called)) {

            PrefData.writeBooleanPref(PrefData.pref_called_from_confirm_fragment, true);

            Utils.changeFragmentConfirmVisitor(new WhomeToMeetFragment(), "wtm_confirm", ConfirmVisitorRestDetailsActivity.this);
        } else {

            PrefData.writeBooleanPref(PrefData.pref_called_from_confirm_fragment, true);

            Utils.changeFragmentConfirmVisitor(new DisclaimerFragment(), "disclaimer_confirm", ConfirmVisitorRestDetailsActivity.this);
        }

    }

    private void initialize() {
        confirmActivity = this;
        confirmTitle = findViewById(R.id.tv_title);
        setSupportActionBar(toolbar);
        ivBack.setVisibility(View.VISIBLE);
        confirmTitle.setVisibility(View.VISIBLE);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    @Override
    public void onBackPressed() {

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_confirm_visitor_rest);

        if (PrefData.readBooleanPref(PrefData.pref_wtm_Called)) {

            if (currentFragment instanceof WhomeToMeetFragment) {
                finish();
            } else if (currentFragment instanceof DisclaimerFragment) {
                currentFragment.getChildFragmentManager().popBackStack("wtm_confirm", 0);
            }

        } else {
            finish();
        }

        super.onBackPressed();
    }
}
