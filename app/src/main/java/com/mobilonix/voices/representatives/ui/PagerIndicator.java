package com.mobilonix.voices.representatives.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobilonix.voices.R;
import com.mobilonix.voices.base.util.GeneralUtil;
import com.mobilonix.voices.delegates.Callback;

import java.util.ArrayList;

public class PagerIndicator extends LinearLayout implements ViewPager.OnPageChangeListener{

    ArrayList<TextView> indicators = new ArrayList<TextView>();

    ArrayList<String> indicatorTags = new ArrayList<String>();

    String currentIndicatorTag;

    Callback callback;

    public PagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void addIndicator(String level, final String indicatorTag) {
        final TextView repsLevel = new TextView(getContext());
        repsLevel.setText(level);
        Typeface avenir = Typeface.createFromAsset(getContext().getAssets(), "fonts/AvenirNext-Regular.ttf");
        repsLevel.setTypeface(avenir);
        repsLevel.setTextColor(getResources().getColor(R.color.grey));
        repsLevel.setTextSize(25);
        repsLevel.setPadding(15,0,15,0);
        indicators.add(repsLevel);
        final int position = indicators.size() - 1;
        repsLevel.setTag(position + indicatorTag);
        addView(repsLevel);
        indicatorTags.add(indicatorTag);
        repsLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralUtil.toast("Tabs pressed");

                if(callback != null) {

                    for (int i = 0; i < indicators.size(); i++) {
                        if(indicators.get(i).getTag().equals(i + indicatorTag)) {
                            indicators.get(i).setTextColor(getResources().getColor(R.color.indicator_blue));
                            callback.onExecuted(i);
                        } else{
                            indicators.get(i).setTextColor(getResources().getColor(R.color.grey));
                        }
                    }
                }
            }
        });
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
                indicators.get(i).setTextColor(getResources().getColor(R.color.grey));
            }
        }

        currentIndicatorTag = indicatorTags.get(position);

        if(callback != null) {
            callback.onExecuted(null);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
