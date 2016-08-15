package com.mobilonix.voices.representatives.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mobilonix.voices.R;
import com.mobilonix.voices.delegates.Callback;

import java.util.ArrayList;

public class PagerIndicator extends LinearLayout implements ViewPager.OnPageChangeListener{

    ArrayList<ImageView> indicators = new ArrayList<>();

    ArrayList<String> indicatorTags = new ArrayList<>();

    String currentIndicatorTag;

    Callback callback;

    public PagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void addIndicator(String indicatorTag) {
        ImageView circle = new ImageView(getContext());
        circle.setMaxHeight(15);
        circle.setMaxWidth(15);
        circle.setImageResource(R.drawable.empty_circle);
        circle.setPadding(5, 0, 5, 0);
        indicators.add(circle);
        addView(circle);

        indicatorTags.add(indicatorTag);

        /* First indicator will always be selected */
        if(indicators.size() == 1) {
            indicators.get(0).setImageResource(R.drawable.filled_circle);

            currentIndicatorTag = indicatorTags.get(0);
        }

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
                indicators.get(i).setImageResource(R.drawable.filled_circle);
            } else{
                indicators.get(i).setImageResource(R.drawable.empty_circle);
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
