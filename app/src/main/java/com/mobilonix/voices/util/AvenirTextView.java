package com.mobilonix.voices.util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class AvenirTextView extends TextView {
    public AvenirTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/avenir-next-regular.ttf"));
    }
}
