package com.mobilonix.voices.representatives.ui;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobilonix.voices.R;
import com.mobilonix.voices.VoicesApplication;
import com.mobilonix.voices.analytics.AnalyticsManager;
import com.mobilonix.voices.representatives.RepresentativesManager;
import com.mobilonix.voices.representatives.model.Representative;
import com.mobilonix.voices.session.SessionManager;
import com.mobilonix.voices.util.AvenirTextView;
import com.mobilonix.voices.util.ViewUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RepresentativesListAdapter extends ArrayAdapter<Representative> {

    int resource;
    ArrayList<Representative> representatives;
    Dialog detailsDialog;

    public RepresentativesListAdapter(Context context, int resource, ArrayList<Representative> representatives) {
        super(context, resource, representatives);

        this.representatives = representatives;
        this.resource = resource;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder mViewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)
                    parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(resource, parent, false);
            mViewHolder = new ViewHolder();

            mViewHolder.mLinearLayout = (LinearLayout) convertView.findViewById(R.id.representatives_linear_layout);
            mViewHolder.mRepsImage = (ImageView) convertView.findViewById(R.id.representatives_list_image);
            mViewHolder.mRepsName = (TextView) convertView.findViewById(R.id.representatives_list_name_text);
            convertView.setTag(mViewHolder);
            mViewHolder.mCallImage = (ImageView) convertView.findViewById(R.id.representatives_list_call_image);
            mViewHolder.mEmailImage = (ImageView) convertView.findViewById(R.id.representatives_list_email_image);
            mViewHolder.mTwitterImage = (ImageView) convertView.findViewById(R.id.representatives_list_twitter_image);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        setImage(mViewHolder.mRepsImage, position);
        mViewHolder.mRepsName.setText(representatives.get(position).getName());

        mViewHolder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsDialog = new Dialog(getContext());
                detailsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                detailsDialog.setContentView(R.layout.reps_details);
                ImageView repsImage = (ImageView) detailsDialog.findViewById(R.id.reps_image);
                TextView repsName = (TextView) detailsDialog.findViewById(R.id.reps_name);
                TextView repsParty = (TextView) detailsDialog.findViewById(R.id.reps_party);
                TextView repsDistrict = (TextView) detailsDialog.findViewById(R.id.reps_district);
                TextView repsElectionDate = (TextView) detailsDialog.findViewById(R.id.reps_election_date);
                TextView repsUpcomingElection = (TextView) detailsDialog.findViewById(R.id.reps_election_upcoming);
                setImage(repsImage, position);
                repsName.setText(representatives.get(position).getName());

                if (representatives.get(position).getParty().equals("")) {
                    repsParty.setText(VoicesApplication.getContext().getResources().getString(R.string.party)
                            + VoicesApplication.getContext().getResources().getString(R.string.not_available));
                } else {
                    repsParty.setText(VoicesApplication.getContext().getResources().getString(R.string.party)
                            + representatives.get(position).getParty());
                }

                if (representatives.get(position).getDistrict().equals("")) {
                    repsDistrict.setText(VoicesApplication.getContext().getResources().getString(R.string.district)
                            + VoicesApplication.getContext().getResources().getString(R.string.not_available));
                } else {
                    repsDistrict.setText(VoicesApplication.getContext().getResources().getString(R.string.district)
                            + representatives.get(position).getDistrict());
                }

                if (representatives.get(position).getElectionDate().equals("")) {
                    repsElectionDate.setText(VoicesApplication.getContext().getResources().getString(R.string.next_election)
                            + VoicesApplication.getContext().getResources().getString(R.string.not_available));
                } else {
                    repsElectionDate.setText(VoicesApplication.getContext().getResources().getString(R.string.next_election)
                            + representatives.get(position).getElectionDate());
                }

               if(representatives.get(position).getElectionDate().contains("2017")){
                    repsUpcomingElection.setText(R.string.election_upcoming);
                } else {
                    repsUpcomingElection.setText(" ");
                }
            }
        });

        String check = representatives.get(position).getPhoneNumber();
        if (check == null || check.equals("")) {
            mViewHolder.mCallImage.setColorFilter(getContext().getResources().getColor(R.color.light_grey));
            mViewHolder.mCallImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String emailInstead = getContext().getResources().getString(R.string.email_instead);
                    toggleNoContactInfoDialog(emailInstead);
                }
            });
        } else {
            mViewHolder.mCallImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String bigTextString;
                    if(!RepresentativesManager.INSTANCE.getLastActionSelectedForContact().equals("<NOT COMING FROM GROUP>")){
                        bigTextString = RepresentativesManager.INSTANCE.getActionScript();
                    } else {
                        bigTextString = "Hello my name is [your name] and I am a constituent. I would like the representative to [support or oppose] [an issue that you care about] and I will be voting in the next election.";
                    }

                    AnalyticsManager.INSTANCE.trackEvent("CALL_REPRESENTATIVES_EVENT",
                            representatives.get(position).getName(),
                            SessionManager.INSTANCE.getCurrentUserToken(),
                            "ACTION=" + RepresentativesManager.INSTANCE.getLastActionSelectedForContact() +
                                    ";GROUP=" + RepresentativesManager.INSTANCE.getGroupForLastAction(), null);

                    Intent notificationIntent = new Intent();
                    PendingIntent contentIntent = PendingIntent.getActivity
                            (VoicesApplication.getContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationCompat.Builder b = new NotificationCompat.Builder(VoicesApplication.getContext());
                    NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
                    bigText.setBigContentTitle("What To Say");
                    bigText.bigText(bigTextString);
                    b.setDefaults(Notification.DEFAULT_ALL)
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle("What To Say")
                            .setContentText("Hello my name is [your name] and I am a constituent. I would like the representative to [support or oppose] [an issue that you care about] and I will be voting in the next election.")
                            .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
                            .setContentIntent(contentIntent);
                    b.setStyle(bigText);

                    NotificationManager notificationManager = (NotificationManager)VoicesApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(1, b.build());

                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                    phoneIntent.setData(Uri.parse("tel:" + representatives.get(position).getPhoneNumber()));
                    v.getContext().startActivity(phoneIntent);
                    RepresentativesManager.INSTANCE.setLastActionSelectedForContact("<NOT COMING FROM GROUP>","<NOT COMING FROM GROUP>","");
                }
            });
        }

        if (representatives.get(position).getLevel()!=null && representatives.get(position).getLevel().equals("Federal")) {
            check = representatives.get(position).getContactForm();
            if (check == null || check.equals("")) {
                mViewHolder.mEmailImage.setColorFilter(getContext().getResources().getColor(R.color.light_grey));
                mViewHolder.mEmailImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String callInstead = getContext().getResources().getString(R.string.call_instead);
                        toggleNoContactInfoDialog(callInstead);
                    }
                });
            } else {
                mViewHolder.mEmailImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog emailDialog;
                        emailDialog = new Dialog(getContext());
                        emailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        emailDialog.setContentView(R.layout.dialog_email);
                        AvenirTextView sureText = (AvenirTextView) emailDialog.findViewById(R.id.sure_text);
                        sureText.setText(VoicesApplication.getContext().getResources().getString(R.string.sure_text)
                                + representatives.get(position).getName() + "?");
                        Button yesButton = (Button) emailDialog.findViewById(R.id.yes_button);
                        Button noButton = (Button) emailDialog.findViewById(R.id.no_button);
                        yesButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AnalyticsManager.INSTANCE.trackEvent("" + "WEBFORM_REPRESENTATIVES_EVENT",
                                        representatives.get(position).getName(),
                                        SessionManager.INSTANCE.getCurrentUserToken(),
                                        "ACTION=" + RepresentativesManager.INSTANCE.getLastActionSelectedForContact() +
                                                ";GROUP=" + RepresentativesManager.INSTANCE.getGroupForLastAction(), null);
                                String url = representatives.get(position).getContactForm();
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                v.getContext().startActivity(i);
                            }
                        });
                        noButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                emailDialog.dismiss();
                            }
                        });
                        emailDialog.show();
                    }
                });
            }
            check = representatives.get(position).getTwitterHandle();
            if (check == null || check.equals("")) {
                mViewHolder.mTwitterImage.setColorFilter(getContext().getResources().getColor(R.color.light_grey));
                mViewHolder.mTwitterImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String callInstead = getContext().getResources().getString(R.string.call_instead);
                        toggleNoContactInfoDialog(callInstead);
                    }
                });
            } else {
                mViewHolder.mTwitterImage.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        AnalyticsManager.INSTANCE.trackEvent("" + "TWEET_REPRESENTATIVES_EVENT",
                                representatives.get(position).getName(),
                                SessionManager.INSTANCE.getCurrentUserToken(),
                                "ACTION=" + RepresentativesManager.INSTANCE.getLastActionSelectedForContact() +
                                        ";GROUP=" + RepresentativesManager.INSTANCE.getGroupForLastAction(), null);

                        String url = "https://twitter.com/intent/tweet?text="
                                + "@" + representatives.get(position).getTwitterHandle();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        v.getContext().startActivity(i);
                    }
                });
            }
        } else {
            check = representatives.get(position).getEmailAddress();
            if (check == null || check.equals("")) {
                mViewHolder.mEmailImage.setColorFilter(getContext().getResources().getColor(R.color.light_grey));
                mViewHolder.mEmailImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String callInstead = getContext().getResources().getString(R.string.call_instead);
                        toggleNoContactInfoDialog(callInstead);
                    }
                });
            } else {
                mViewHolder.mEmailImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog emailDialog;
                        emailDialog = new Dialog(getContext());
                        emailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        emailDialog.setContentView(R.layout.dialog_email);
                        AvenirTextView sureText = (AvenirTextView) emailDialog.findViewById(R.id.sure_text);
                        sureText.setText(VoicesApplication.getContext().getResources().getString(R.string.sure_text)
                                + representatives.get(position).getName() + "?");
                        Button yesButton = (Button) emailDialog.findViewById(R.id.yes_button);
                        Button noButton = (Button) emailDialog.findViewById(R.id.no_button);
                        yesButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AnalyticsManager.INSTANCE.trackEvent("" + "EMAIL_REPRESENTATIVES_EVENT",
                                        representatives.get(position).getName(),
                                        SessionManager.INSTANCE.getCurrentUserToken(),
                                        "ACTION=" + RepresentativesManager.INSTANCE.getLastActionSelectedForContact() +
                                                ";GROUP=" + RepresentativesManager.INSTANCE.getGroupForLastAction(), null);

                                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                        "mailto", representatives.get(position).getEmailAddress(), null));
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                                ArrayList<String> addresses = new ArrayList<>();
                                addresses.add(representatives.get(position).getEmailAddress());
                                emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses);
                                v.getContext().startActivity(Intent.createChooser(emailIntent, "Send Email"));
                            }
                        });
                        noButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                emailDialog.dismiss();
                            }
                        });
                        emailDialog.show();
                    }
                });
            }

            check = representatives.get(position).getTwitterHandle();
            if (check == null || check.equals("")) {
                mViewHolder.mTwitterImage.setColorFilter(getContext().getResources().getColor(R.color.light_grey));
                mViewHolder.mTwitterImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String callInstead = getContext().getResources().getString(R.string.call_instead);
                        toggleNoContactInfoDialog(callInstead);
                    }
                });
            } else {
                mViewHolder.mTwitterImage.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        AnalyticsManager.INSTANCE.trackEvent("" + "TWEET_REPRESENTATIVES_EVENT",
                                representatives.get(position).getName(),
                                SessionManager.INSTANCE.getCurrentUserToken(),
                                "ACTION=" + RepresentativesManager.INSTANCE.getLastActionSelectedForContact() +
                                        ";GROUP=" + RepresentativesManager.INSTANCE.getGroupForLastAction(), null);

                        String url = "https://twitter.com/intent/tweet?text="
                                + "@" + representatives.get(position).getTwitterHandle();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        v.getContext().startActivity(i);
                    }
                });
            }
        }
        return convertView;
    }

    public void setImage(final ImageView image, final int position) {
        int imageHeight = Math.round(ViewUtil.convertDpToPixel(100, VoicesApplication.getContext()));
        int imageWidth = Math.round(ViewUtil.convertDpToPixel(80, VoicesApplication.getContext()));

        if(representatives.get(position).getRepresentativeImageUrl()==null ||
                representatives.get(position).getRepresentativeImageUrl().trim().equals("")){
            Picasso.with(image.getContext())
                    .load(R.drawable.voices_icon)
                    .resize(imageWidth, imageHeight)
                    .centerCrop()
                    .placeholder(R.drawable.spinner_moving)
                    .error(R.drawable.voices_icon)
                    .transform(new RoundedTransformation(10, 4))
                    .into(image);
        } else {
            Picasso.with(image.getContext())
                    .load(representatives.get(position).getRepresentativeImageUrl())
                    .resize(imageWidth, imageHeight)
                    .centerCrop()
                    .placeholder(R.drawable.spinner_moving)
                    .error(R.drawable.voices_icon)
                    .transform(new RoundedTransformation(10, 4))
                    .into(image);
        }
    }

    public void toggleNoContactInfoDialog(String text) {

        final Dialog noContactInfoDialog;

        noContactInfoDialog = new Dialog(getContext());
        noContactInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        noContactInfoDialog.setContentView(R.layout.dialog_nocontactinfo);
        TextView considerText = (TextView)noContactInfoDialog.findViewById(R.id.consider_text);
        considerText.setText(text);
        Button gotItButton = (Button)noContactInfoDialog.findViewById(R.id.got_it_button_2);
        gotItButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noContactInfoDialog.dismiss();
            }
        });
        noContactInfoDialog.show();
    }

    private class ViewHolder {
        private LinearLayout mLinearLayout;
        private ImageView mRepsImage;
        private TextView mRepsName;
        private ImageView mMoreInfoImage;
        private ImageView mCallImage;
        private ImageView mEmailImage;
        private ImageView mTwitterImage;
    }
}
