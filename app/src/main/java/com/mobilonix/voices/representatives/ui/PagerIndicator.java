package com.mobilonix.voices.representatives.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobilonix.voices.R;
import com.mobilonix.voices.delegates.Callback;

import java.util.ArrayList;

public class PagerIndicator extends LinearLayout implements ViewPager.OnPageChangeListener{

    ArrayList<TextView> indicators = new ArrayList<>();

    ArrayList<String> indicatorTags = new ArrayList<>();

    String currentIndicatorTag;

    Callback callback;

    public PagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void addIndicator(String level, String indicatorTag) {
        TextView repsLevel = new TextView(getContext());
        repsLevel.setText(level);
        Typeface avenir = Typeface.createFromAsset(getContext().getAssets(), "fonts/AvenirNext-Regular.ttf");
        repsLevel.setTypeface(avenir, Typeface.BOLD);
        repsLevel.setTextColor(getResources().getColor(R.color.light_grey));
        repsLevel.setTextSize(25);
        repsLevel.setPadding(30,0,30,0);
        //formatting of the TextView goes here
        indicators.add(repsLevel);
        addView(repsLevel);
        indicatorTags.add(indicatorTag);

        //set style on first indicator
        indicators.get(0).setTextColor(getResources().getColor(R.color.indicator_blue));

            currentIndicatorTag = indicatorTags.get(0);
    }

    public String getCurrentIndicatorTag() {
        return currentIndicatorTag;
    }

    public void addIndicatorShiftCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < indicators.size(); i++) {
            if(i == position) {
                indicators.get(i).setTextColor(getResources().getColor(R.color.indicator_blue));
            } else{
                indicators.get(i).setTextColor(getResources().getColor(R.color.light_grey));
            }
        }

        currentIndicatorTag = indicatorTags.get(position);

        if(callback != null) {
            callback.onExecuted(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
