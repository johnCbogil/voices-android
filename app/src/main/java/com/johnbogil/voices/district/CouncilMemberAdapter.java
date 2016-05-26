package com.johnbogil.voices.district;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.johnbogil.voices.AnalyticsApplication;
import com.johnbogil.voices.R;
import com.johnbogil.voices.activities.FloatingActivity;
import com.johnbogil.voices.misc.CircleTransform;
import com.johnbogil.voices.state.StateLegislatorsClass;
import com.squareup.picasso.Picasso;

/**
 * Created by chrislinder1 on 2/2/16.
 */
public class CouncilMemberAdapter extends BaseAdapter {

    private Context mContext;
    private CouncilMembersClass[] mCouncilMembersClasses;
    private String full_name;
    public Activity mActivity;
    private Tracker mTracker;

    public CouncilMemberAdapter(Context context, CouncilMembersClass[] councilMembersClasses) {
        mContext = context;
        mCouncilMembersClasses = councilMembersClasses;
    }

    @Override
    public int getCount() {
        return mCouncilMembersClasses.length;
    }

    @Override
    public Object getItem(int position) {
        return mCouncilMembersClasses[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder mViewHolder;

        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_view_adapter_template, null);
            mViewHolder = new ViewHolder();
            mViewHolder.mStatePicture = (ImageView) convertView.findViewById(R.id.profile_image);
            mViewHolder.mStateName = (TextView) convertView.findViewById(R.id.full_name);
            mViewHolder.mStateDistrict = (TextView) convertView.findViewById(R.id.nextElection);
            mViewHolder.phone_call_button = (ImageView) convertView.findViewById(R.id.call_image);
            mViewHolder.email_button = (ImageView) convertView.findViewById(R.id.email_image);
            mViewHolder.twitter_image = (ImageView) convertView.findViewById(R.id.twitter_image);

            convertView.setTag(mViewHolder);

        }else{
            mViewHolder = (ViewHolder) convertView.getTag();
        }
//        AnalyticsApplication application = (AnalyticsApplication) mActivity.getApplication();
        AnalyticsApplication application = (AnalyticsApplication) mContext.getApplicationContext();

        mTracker = application.getDefaultTracker();

        //weird edit here
        final CouncilMembersClass mCouncilMembersClass = mCouncilMembersClasses[0];
        if (mViewHolder.mStatePicture != null) {
            Picasso.with(mContext).load(mCouncilMembersClass.getPhoto_url()).placeholder(R.drawable.template_holder)
                    .transform(new CircleTransform(25, 4)).fit().into(mViewHolder.mStatePicture);
        }else{
            Picasso.with(mContext).load(R.drawable.johnlennon23).placeholder(R.drawable.template_holder)
                    .transform(new CircleTransform(25, 4)).fit().into(mViewHolder.mStatePicture);
        }

        full_name = mCouncilMembersClass.getFull_name();
        mViewHolder.mStateName.setText(full_name);
        mViewHolder.mStateDistrict.setText(mCouncilMembersClass.getDistrict());
        mViewHolder.phone_call_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Intent call = new Intent(Intent.ACTION_CALL);
                call.setData(Uri.parse("tel:" + mCouncilMembersClass.getPhone()));
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                            .setTitle(mCouncilMembersClass.getFull_name())
                            .setMessage("You're about to call " + mCouncilMembersClass.getFull_name() + ", do you know what to say?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mContext.startActivity(call);
                                    mTracker.send(new HitBuilders.EventBuilder("ui", "open")
                                            .setLabel(full_name)
                                            .setAction("State Call")
                                            .build());
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mContext.startActivity(new Intent(mContext, FloatingActivity.class));
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } catch (android.content.ActivityNotFoundException e) {
                    e.printStackTrace();
                    Log.e("onClickContact", "no activity found");
                }
            }
        });
        mViewHolder.phone_call_button.setColorFilter(Color.parseColor(mContext.getString(R.string.app_color_theme)));
        mViewHolder.email_button.setColorFilter(Color.parseColor(mContext.getString(R.string.app_color_theme)));
        mViewHolder.email_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Uri uri = Uri.parse("mailto:" + mCouncilMembersClass.getEmail());
                Intent email = new Intent(Intent.ACTION_SENDTO,uri);
                mContext.startActivity(email);


            }
        });

        mViewHolder.twitter_image.setVisibility(View.GONE);

        return convertView;
    }

    private static class ViewHolder{
        ImageView phone_call_button;
        ImageView email_button;
        ImageView mStatePicture;
        ImageView twitter_image;

        TextView mStateName;
        TextView mStateDistrict;

    }
}
