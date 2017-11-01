package com.mobilonix.voices.util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class AvenirTextView extends android.support.v7.widget.AppCompatTextView {
    public AvenirTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/avenir-next-regular.ttf"));
    }
}
