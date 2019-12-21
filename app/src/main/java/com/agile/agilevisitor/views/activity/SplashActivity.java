package com.agile.agilevisitor.views.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.agile.agilevisitor.R;
import com.agile.agilevisitor.helper.PrefData;
import com.agile.agilevisitor.views.activity.user_panel.UserHomeActivity;
import com.agile.agilevisitor.views.activity.visiting_panel.VisitingHomePanel;
import com.agile.agilevisitor.views.activity.visiting_panel.VisitingLandingScreen;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!PrefData.readBooleanPref(PrefData.isFirstTimeRun)){

                    startActivity(new Intent(SplashActivity.this,IntroductoryScreen.class));

                } else {
                    if (PrefData.readBooleanPref(PrefData.PREF_LOGINSTATUS)) {

                        if (PrefData.readStringPref(PrefData.user_type).equalsIgnoreCase("Visitor")) {
                            startActivity(new Intent(SplashActivity.this, VisitingLandingScreen.class));
                        } else {
                            startActivity(new Intent(SplashActivity.this, UserHomeActivity.class));
                        }

                    }else {
                        startActivity(new Intent(SplashActivity.this, LoginUserTypeActivity.class));
                    }
                }

                finish();

            }
        },3000);

    }

}