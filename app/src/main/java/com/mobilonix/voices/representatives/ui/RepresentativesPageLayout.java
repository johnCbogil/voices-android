package com.mobilonix.voices.representatives.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobilonix.voices.R;
import com.mobilonix.voices.VoicesApplication;
import com.mobilonix.voices.VoicesMainActivity;
import com.mobilonix.voices.representatives.model.RepresentativesPage;

public class RepresentativesPageLayout extends LinearLayout {
    public String TAG = RepresentativesPageLayout.class.getCanonicalName();
    RepresentativesPage representativesPage;

    Dialog responseDialog;
    Dialog helpDialog;

    public RepresentativesPageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        hideKeyboard((VoicesMainActivity)getContext());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Toolbar toolbar = ((VoicesMainActivity)getContext()).getToolbar();

        ImageView infoIcon = (ImageView)toolbar.findViewById(R.id.representatives_info_icon);
        ImageView helpIcon = (ImageView)toolbar.findViewById(R.id.representatives_help_icon);

        infoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int voicesOrange = VoicesApplication.getContext().getResources().getColor(R.color.voices_orange);
                responseDialog = new Dialog(getContext());
                responseDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                responseDialog.setContentView(R.layout.dialog_info);
                responseDialog.setTitle(VoicesApplication.getContext().getString(R.string.response_title));
                TextView responseTextView = (TextView)responseDialog.findViewById(R.id.response);
                String response = VoicesApplication.getContext().getString(R.string.response);
                Spannable span = new SpannableString(response);
                span.setSpan(new ForegroundColorSpan(voicesOrange), 17, 28, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                span.setSpan(new ForegroundColorSpan(voicesOrange), 94, 149, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                responseTextView.setText(span);
                Button infoCloseButton = (Button) responseDialog.findViewById(R.id.info_close_button);
                infoCloseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        responseDialog.dismiss();
                    }
                });
                responseDialog.show();
            }
        });

        helpIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpDialog = new Dialog(getContext());
                helpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                helpDialog.setContentView(R.layout.dialog_instructions);
                helpDialog.setTitle(R.string.instructions_title);
                helpDialog.show();
                Button gotItButton = (Button)helpDialog.findViewById(R.id.got_it_button);
                gotItButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        helpDialog.dismiss();
                    }
                });
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
