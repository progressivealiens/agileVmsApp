package com.agile.agilevisitor.views.activity.visiting_panel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.agile.agilevisitor.R;
import com.agile.agilevisitor.customviews.MyTextview;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VisitingLandingScreen extends AppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    MyTextview tvTitle;
    @BindView(R.id.lin_toolbar)
    LinearLayout linToolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_checkin)
    MyTextview tvCheckin;
    @BindView(R.id.tv_checkout)
    MyTextview tvCheckout;
    @BindView(R.id.tv_invited_visitor)
    TextView tvInvitedVisitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visiting_landing_screen);
        ButterKnife.bind(this);

        initialize();

        tvInvitedVisitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VisitingLandingScreen.this,EnterPasscodeActivity.class));
            }
        });

        tvCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(VisitingLandingScreen.this,VisitingHomePanel.class));
                startActivity(new Intent(VisitingLandingScreen.this, NDAActivity.class));
            }
        });

        tvCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VisitingLandingScreen.this, CheckoutActivity.class));
            }
        });
    }

    private void initialize() {
        setSupportActionBar(toolbar);
        ivBack.setVisibility(View.INVISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("Select Operation");
    }
}
