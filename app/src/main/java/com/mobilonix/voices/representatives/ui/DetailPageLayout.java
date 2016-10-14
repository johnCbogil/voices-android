package com.mobilonix.voices.representatives.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobilonix.voices.R;

public class DetailPageLayout extends LinearLayout {

    ImageView repsImage;
    TextView repsName;
    TextView repsParty;
    TextView repsDistrict;
    TextView repsElectionDate;
    ImageView mCallImage;
    ImageView mEmailImage;
    ImageView mTwitterImage;

    public DetailPageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        repsImage = (ImageView)findViewById(R.id.reps_image);
        repsName = (TextView)findViewById(R.id.reps_name);
        repsParty = (TextView)findViewById(R.id.reps_party);
        repsDistrict = (TextView)findViewById(R.id.reps_district);
        repsElectionDate = (TextView)findViewById(R.id.reps_election_date);
        mCallImage = (ImageView)findViewById(R.id.representatives_list_call_image);
        mEmailImage = (ImageView)findViewById(R.id.representatives_list_email_image);
        mTwitterImage = (ImageView)findViewById(R.id.representatives_list_twitter_image);
    }

    public void initViews(int position) {
    }
}
