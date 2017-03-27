package com.mobilonix.voices.util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class AvenirBoldTextView extends TextView {
    public AvenirBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/avenir-next-bold.ttf"));
    }
}
