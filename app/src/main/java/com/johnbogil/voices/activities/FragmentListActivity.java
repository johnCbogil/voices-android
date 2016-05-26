package com.johnbogil.voices.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.johnbogil.voices.R;

public class FragmentListActivity extends FragmentActivity {

    private static final int NUM_PAGES = 3;

    public ViewPager mPager;
    public PagerAdapter mPagerAdapter;

    public ImageView info_button;
    public ImageView page_one_dot;
    public ImageView page_two_dot;
    public ImageView page_three_dot;

    public DataHandler mDataHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_list);

        mDataHandler = new DataHandler(this);
        mDataHandler.open();

        page_one_dot = (ImageView) findViewById(R.id.page_one_dot);
        page_two_dot = (ImageView) findViewById(R.id.page_two_dot);
        page_three_dot = (ImageView) findViewById(R.id.page_three_dot);

        info_button = (ImageView) findViewById(R.id.info_button);
        info_button.setColorFilter(Color.parseColor("#BABABA"));

        info_button.setOnClickListener(new View.OnClickListener() {
            //            @Override
            public void onClick(View v) {
                if (v.equals(info_button))
                    startActivity(new Intent(FragmentListActivity.this, FloatingActivity.class));
            }
        });

        mPager = (ViewPager) findViewById(R.id.listPage);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());

        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(2); //Is this necessary?

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0){
                    page_one_dot.setBackgroundResource(R.drawable.selected_dot);
                    page_two_dot.setBackgroundResource(R.drawable.unselected_dot);
                    page_three_dot.setBackgroundResource(R.drawable.unselected_dot);

                } else if (position == 1){
                    page_one_dot.setBackgroundResource(R.drawable.unselected_dot);
                    page_two_dot.setBackgroundResource(R.drawable.selected_dot);
                    page_three_dot.setBackgroundResource(R.drawable.unselected_dot);
                } else {
                    page_one_dot.setBackgroundResource(R.drawable.unselected_dot);
                    page_two_dot.setBackgroundResource(R.drawable.unselected_dot);
                    page_three_dot.setBackgroundResource(R.drawable.selected_dot);
                }

                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FragmentDisplay.create(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    @Override
    public void onBackPressed() {


    }
}
