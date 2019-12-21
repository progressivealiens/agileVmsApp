package com.agile.agilevisitor.views.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.agile.agilevisitor.R;
import com.agile.agilevisitor.customviews.MyTextview;
import com.agile.agilevisitor.helper.PrefData;
import com.rd.PageIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IntroductoryScreen extends AppCompatActivity {

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.pageIndicatorView)
    PageIndicatorView pageIndicatorView;
    @BindView(R.id.tv_skip)
    TextView tvSkip;

    CustomViewPager customViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introductory_screen);
        ButterKnife.bind(this);


        customViewPager = new CustomViewPager(IntroductoryScreen.this);
        viewPager.setAdapter(customViewPager);

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PrefData.writeBooleanPref(PrefData.isFirstTimeRun,true);

                startActivity(new Intent(IntroductoryScreen.this, LoginUserTypeActivity.class));
                finish();
            }
        });

    }


    public class CustomViewPager extends PagerAdapter {

        Context context;

        public CustomViewPager(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }


        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View itemview = inflater.inflate(R.layout.view_pager_items, container, false);

            ImageView imageView = itemview.findViewById(R.id.iv_images);
            MyTextview heading = itemview.findViewById(R.id.tv_heading);
            MyTextview content = itemview.findViewById(R.id.tv_content);


            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {
                }

                @Override
                public void onPageSelected(int i) {

                    switch (i) {
                        case 0:
                            imageView.setImageDrawable(getResources().getDrawable(R.drawable.visiting_panel));
                            heading.setText(getResources().getString(R.string.visiting_panel_intro));
                            content.setText(getResources().getString(R.string.visiting_panel_subheading));
                            tvSkip.setText(getString(R.string.skip_introduction));
                            break;
                        case 1:
                            imageView.setImageDrawable(getResources().getDrawable(R.drawable.send_invitation));
                            heading.setText(R.string.send_invitation);
                            content.setText(R.string.send_invitation_subheading);
                            tvSkip.setText(getString(R.string.skip_introduction));
                            break;
                        case 2:
                            imageView.setImageDrawable(getResources().getDrawable(R.drawable.receive_notification));
                            heading.setText(R.string.receive_notification);
                            content.setText(R.string.receive_notification_subheading);
                            tvSkip.setText(getString(R.string.proceed));
                            break;
                    }
                }

                @Override
                public void onPageScrollStateChanged(int i) {
                }
            });

            container.addView(itemview);

            return itemview;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

}
