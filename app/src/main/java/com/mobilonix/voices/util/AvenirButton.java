package com.mobilonix.voices.util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.mobilonix.voices.R;

public class AvenirButton extends Button {
    public AvenirButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/avenir-next-regular.ttf"));
        this.setBackgroundResource(R.drawable.rounded_button);
    }
}