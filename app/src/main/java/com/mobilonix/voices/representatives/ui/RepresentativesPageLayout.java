package com.mobilonix.voices.representatives.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mobilonix.voices.R;
import com.mobilonix.voices.VoicesMainActivity;

public class RepresentativesPageLayout extends LinearLayout {
   Dialog responseDialog;

    public RepresentativesPageLayout(Context context, AttributeSet attrs) {

        super(context, attrs);

        hideKeyboard((VoicesMainActivity)getContext());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        ImageView infoIcon = (ImageView)findViewById(R.id.representatives_info_icon);
        infoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                responseDialog = new Dialog(getContext());
                responseDialog.setContentView(R.layout.info_dialog);
                responseDialog.setTitle(R.string.response_title);

                Button dialogCloseButton = (Button)responseDialog.findViewById(R.id.dialog_close_button);
                // if button is clicked, close the custom dialog
                dialogCloseButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        responseDialog.dismiss();
                    }
                });

                responseDialog.show();
            }
        });

        findViewById(R.id.google_are_assholes_layout).requestFocus();

        hideKeyboard((VoicesMainActivity)getContext());

        initViews();
    }

    public void initViews() {
        //getContext().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
