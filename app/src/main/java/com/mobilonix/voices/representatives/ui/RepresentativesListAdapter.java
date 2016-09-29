package com.mobilonix.voices.representatives.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.mobilonix.voices.representatives.model.Representative;
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

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater)
                    parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(resource, parent, false);
            mViewHolder = new ViewHolder();

            mViewHolder.mLinearLayout = (LinearLayout)convertView.findViewById(R.id.representatives_linear_layout);
            mViewHolder.mRepsImage = (ImageView)convertView.findViewById(R.id.representatives_list_image);
            mViewHolder.mRepsName = (TextView)convertView.findViewById(R.id.representatives_list_name_text);
            convertView.setTag(mViewHolder);
            mViewHolder.mCallImage = (ImageView)convertView.findViewById(R.id.representatives_list_call_image);
            mViewHolder.mEmailImage = (ImageView)convertView.findViewById(R.id.representatives_list_email_image);
            mViewHolder.mTwitterImage = (ImageView)convertView.findViewById(R.id.representatives_list_twitter_image);
            } else {
                mViewHolder = (ViewHolder)convertView.getTag();
            }

        mViewHolder.mRepsName.setText(representatives.get(position).getName());

        if(representatives.get(position).getGender().equals("F")) {
            Picasso.with(mViewHolder.mRepsImage.getContext())
                    .load(representatives.get(position).getRepresentativeImageUrl())
                    .resize(400, 500)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_spinner)
                    .error(R.drawable.representatives_place_holder_female)
                    .transform(new RoundedTransformation(50, 4))
                    .into(mViewHolder.mRepsImage);
        } else if (representatives.get(position).getGender().equals("M")) {
            Picasso.with(mViewHolder.mRepsImage.getContext())
                    .load(representatives.get(position).getRepresentativeImageUrl())
                    .resize(400, 500)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_spinner)
                    .error(R.drawable.representatives_place_holder_male)
                    .transform(new RoundedTransformation(50, 4))
                    .into(mViewHolder.mRepsImage);
        } else {
            double random = Math.random();
            if(random < 0.5){
                Picasso.with(mViewHolder.mRepsImage.getContext())
                        .load(representatives.get(position).getRepresentativeImageUrl())
                        .resize(100, 125)
                        .centerCrop()
                        .placeholder(R.drawable.placeholder_spinner)
                        .error(R.drawable.representatives_place_holder_female)
                        .transform(new RoundedTransformation(10, 4))
                        .into(mViewHolder.mRepsImage);
            } else {
                Picasso.with(mViewHolder.mRepsImage.getContext())
                        .load(representatives.get(position).getRepresentativeImageUrl())
                        .resize(100, 125)
                        .centerCrop()
                        .placeholder(R.drawable.placeholder_spinner)
                        .error(R.drawable.representatives_place_holder_male)
                        .transform(new RoundedTransformation(10, 4))
                        .into(mViewHolder.mRepsImage);
            }
        }

            mViewHolder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    detailsDialog = new Dialog(getContext());
                    detailsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    detailsDialog.setContentView(R.layout.dialog_repsdetails);
                    ImageView repsImage = (ImageView)detailsDialog.findViewById(R.id.reps_image);
                    TextView repsName = (TextView)detailsDialog.findViewById(R.id.reps_name);
                    TextView repsParty = (TextView)detailsDialog.findViewById(R.id.reps_party);
                    TextView repsDistrict = (TextView)detailsDialog.findViewById(R.id.reps_district);
                    TextView repsElectionDate = (TextView)detailsDialog.findViewById(R.id.reps_election_date);
                    if(representatives.get(position).getGender()=="F") {
                        Picasso.with(repsImage.getContext())
                                .load(representatives.get(position).getRepresentativeImageUrl())
                                .resize(100, 125)
                                .centerCrop()
                                .placeholder(R.drawable.placeholder_spinner)
                                .error(R.drawable.representatives_place_holder_female)
                                .transform(new RoundedTransformation(10, 4))
                                .into(repsImage);
                    } else if(representatives.get(position).getGender()=="M"){
                        Picasso.with(repsImage.getContext())
                                .load(representatives.get(position).getRepresentativeImageUrl())
                                .resize(100, 125)
                                .centerCrop()
                                .placeholder(R.drawable.placeholder_spinner)
                                .error(R.drawable.representatives_place_holder_male)
                                .transform(new RoundedTransformation(10, 4))
                                .into(repsImage);
                    } else {
                        double random = Math.random();
                        if(random < 0.5){
                            Picasso.with(repsImage.getContext())
                                    .load(representatives.get(position).getRepresentativeImageUrl())
                                    .resize(100, 125)
                                    .centerCrop()
                                    .placeholder(R.drawable.placeholder_spinner)
                                    .error(R.drawable.representatives_place_holder_female)
                                    .transform(new RoundedTransformation(10, 4))
                                    .into(repsImage);
                        } else {
                            Picasso.with(repsImage.getContext())
                                    .load(representatives.get(position).getRepresentativeImageUrl())
                                    .resize(100, 125)
                                    .centerCrop()
                                    .placeholder(R.drawable.placeholder_spinner)
                                    .error(R.drawable.representatives_place_holder_male)
                                    .transform(new RoundedTransformation(10, 4))
                                    .into(repsImage);
                        }
                    }
                    repsName.setText(representatives.get(position).getName());

                    if(representatives.get(position).getParty().equals("")){
                        repsParty.setText(VoicesApplication.getContext().getResources().getString(R.string.party)
                                + VoicesApplication.getContext().getResources().getString(R.string.not_available));
                    } else {
                        repsParty.setText(VoicesApplication.getContext().getResources().getString(R.string.party)
                                + representatives.get(position).getParty());
                    }

                    if(representatives.get(position).getDistrict().equals("")){
                        repsDistrict.setText(VoicesApplication.getContext().getResources().getString(R.string.district)
                                + VoicesApplication.getContext().getResources().getString(R.string.not_available));
                    } else {
                        repsDistrict.setText(VoicesApplication.getContext().getResources().getString(R.string.district)
                                + representatives.get(position).getDistrict());
                    }

                    if(representatives.get(position).getElectionDate().equals("")){
                        repsElectionDate.setText(VoicesApplication.getContext().getResources().getString(R.string.next_election)
                                + VoicesApplication.getContext().getResources().getString(R.string.not_available));
                    } else {
                        repsElectionDate.setText(VoicesApplication.getContext().getResources().getString(R.string.next_election)
                                + representatives.get(position).getElectionDate());
                    }
                    detailsDialog.show();
                }
            });

            String check = representatives.get(position).getPhoneNumber();
            if(check == null || check.equals("")){
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
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + representatives.get(position).getPhoneNumber()));
                        v.getContext().startActivity(intent);
                    }
                });
            }

            check = representatives.get(position).getEmailAddress();
            if(check == null || check.equals("")){
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
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto",representatives.get(position).getEmailAddress(), null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                        ArrayList<String> addresses = new ArrayList<>();
                        addresses.add(representatives.get(position).getEmailAddress());
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses);

                        v.getContext().startActivity(Intent.createChooser(emailIntent, "Send Email"));
                    }
                });
            }

            check = representatives.get(position).getTwitterHandle();
            if(check == null || check.equals("")){
                mViewHolder.mTwitterImage .setColorFilter(getContext().getResources().getColor(R.color.light_grey));
                mViewHolder.mTwitterImage .setOnClickListener(new View.OnClickListener() {
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
                        String url = "https://twitter.com/intent/tweet?text="
                                + "@" + representatives.get(position).getTwitterHandle();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        v.getContext().startActivity(i);
                    }
                });
            }

        return convertView;
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
