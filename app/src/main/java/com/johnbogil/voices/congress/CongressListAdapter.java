package com.johnbogil.voices.congress;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
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
import com.squareup.picasso.Picasso;

import com.johnbogil.voices.R;
import com.johnbogil.voices.activities.FloatingActivity;
import com.johnbogil.voices.misc.CircleTransform;

/**
 * Created by chrislinder1 on 2/1/16.
 */
public class CongressListAdapter extends BaseAdapter {

    Context mContext;
    private CongressClass[] mCongressClasses;
    private String full_name;
    private String term_end;
    public Activity mActivity;
    private Tracker mTracker;
    public CongressListAdapter(Context context, CongressClass[] congressClasses) {
        mContext = context;
        mCongressClasses = congressClasses;
    }

    @Override
    public int getCount() {
        return mCongressClasses.length;
    }

    @Override
    public Object getItem(int position) {
        return mCongressClasses[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int SDK_INT = Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            final ViewHolder mViewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.list_view_adapter_template, null);
                mViewHolder = new ViewHolder();
                mViewHolder.congress_image = (ImageView) convertView.findViewById(R.id.profile_image);
                mViewHolder.congress_first_name = (TextView) convertView.findViewById(R.id.full_name);
                mViewHolder.congress_term_end = (TextView) convertView.findViewById(R.id.nextElection);
                mViewHolder.congress_twitter_id = (ImageView) convertView.findViewById(R.id.twitter_image);
                mViewHolder.phone_call_button = (ImageView) convertView.findViewById(R.id.call_image);
                mViewHolder.email_button = (ImageView) convertView.findViewById(R.id.email_image);
                mViewHolder.twitter_image = (ImageView) convertView.findViewById(R.id.twitter_image);

                convertView.setTag(mViewHolder);

            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }
            AnalyticsApplication application = (AnalyticsApplication) mContext.getApplicationContext();
            mTracker = application.getDefaultTracker();
            final CongressClass mCongressClass = mCongressClasses[position];
            if(mViewHolder.congress_image != null){
                Picasso.with(mContext).load(mCongressClass.getImage_url()).placeholder(R.drawable.template_holder)
                        .transform(new CircleTransform(50, 4)).into(mViewHolder.congress_image);
            }else{
                Picasso.with(mContext).load(mCongressClass.getImage_url()).placeholder(R.drawable.template_holder)
                        .transform(new CircleTransform(50, 4)).into(mViewHolder.congress_image);
            }
//            mViewHolder.congress_image.setBackgroundResource(R.drawable.round_corners);
            full_name = mCongressClass.getTitle() + ". " + mCongressClass.getFirst_name() + " " + mCongressClass.getLast_name();
            mViewHolder.congress_first_name.setText(full_name);

            term_end = mCongressClass.getTerm_end();
            mViewHolder.congress_term_end.setText(term_end);




            mViewHolder.phone_call_button.setColorFilter((Color.parseColor(mContext.getString(R.string.app_color_theme))));
            mViewHolder.phone_call_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent call = new Intent(Intent.ACTION_CALL);
                    call.setData(Uri.parse("tel:" + mCongressClass.getPhone()));
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                                .setTitle(mCongressClass.getTitle() + ". " + mCongressClass.getFirst_name() + " "
                                                                                +  mCongressClass.getLast_name())
                                .setMessage("You're about to call " + mCongressClass.getFirst_name() + ", do you know what to say?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mContext.startActivity(call);
                                        mTracker.send(new HitBuilders.EventBuilder("ui", "open")
                                                .setLabel(full_name)
                                                .setAction("Fed Call")
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

                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        Log.e("onClickContact", "no activity found");
                    }
                }
            });







            mViewHolder.email_button.setColorFilter(Color.parseColor(mContext.getString(R.string.app_color_theme)));
            mViewHolder.email_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Uri uri = Uri.parse("mailto:"+mCongressClass.getOc_email());
                    Intent email = new Intent(Intent.ACTION_SENDTO, uri);
                    mContext.startActivity(email);


//





                }
            });
            mViewHolder.twitter_image.setColorFilter(Color.parseColor(mContext.getString(R.string.app_color_theme)));
            mViewHolder.twitter_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://twitter.com/" + mCongressClass.getTwitter_id()));
                        mContext.startActivity(intent);

                    } catch (Exception e) {
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://twitter.com/#!/" + mCongressClass.getTwitter_id())));
                    }
                }
            });
        }

        return convertView;
    }
    private static class ViewHolder{
        ImageView congress_image;
        ImageView congress_twitter_id;
        ImageView phone_call_button;
        ImageView email_button;
        ImageView twitter_image;

        TextView congress_first_name;
        TextView congress_term_end;

    }
}
