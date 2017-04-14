package com.mobilonix.voices.representatives.ui;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.mobilonix.voices.R;
import com.mobilonix.voices.VoicesMainActivity;

public class RepresentativesPageLayout extends LinearLayout {
    public String TAG = RepresentativesPageLayout.class.getCanonicalName();

    public RepresentativesPageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        hideKeyboard((VoicesMainActivity)getContext());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        findViewById(R.id.google_are_assholes_layout).requestFocus();

        hideKeyboard((VoicesMainActivity)getContext());

        initViews();
    }

    public void initViews() {
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
