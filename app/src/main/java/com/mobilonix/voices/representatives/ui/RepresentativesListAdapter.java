package com.mobilonix.voices.representatives.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobilonix.voices.R;
import com.mobilonix.voices.VoicesMainActivity;
import com.mobilonix.voices.representatives.model.Representative;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RepresentativesListAdapter extends ArrayAdapter<Representative> {

    int resource;
    ArrayList<Representative> representatives;

    public RepresentativesListAdapter(Context context, int resource, ArrayList<Representative> representatives) {
        super(context, resource, representatives);

        this.representatives = representatives;
        this.resource = resource;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater)
                    parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(resource, parent, false);

            ImageView listImage = (ImageView)convertView.findViewById(R.id.representatives_list_image);
            TextView representativesNameText = (TextView)convertView.findViewById(R.id.representatives_list_name_text);
            TextView representativesLocationText = (TextView)convertView.findViewById(R.id.representatives_list_location_text);

            representativesNameText.setText(representatives.get(position).getName());
            representativesLocationText.setText(representatives.get(position).getLocation());

            Picasso.with(listImage.getContext())
                    .load(representatives.get(position).getRepresentativeImageUrl())
                    .fit()
                    .placeholder(android.R.drawable.ic_menu_search)
                    .into(listImage);

            ImageView twitterImage = (ImageView)convertView.findViewById(R.id.representatives_list_twitter_image);
            final ImageView callImage = (ImageView)convertView.findViewById(R.id.representatives_list_call_image);
            final ImageView emailImage = (ImageView)convertView.findViewById(R.id.representatives_list_email_image);

            twitterImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "https://twitter.com/intent/tweet?text="
                            + representatives.get(position).getTwitterHandle();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    v.getContext().startActivity(i);
                }
            });

            callImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + representatives.get(position).getPhoneNumber()));
                    v.getContext().startActivity(intent);
                }
            });

            emailImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto","", null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");

                    ArrayList<String> addresses = new ArrayList<>();
                    addresses.add(representatives.get(position).getEmailAddress());
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses);

                    v.getContext().startActivity(Intent.createChooser(emailIntent, "Send email..."));
                }
            });

        }

        return convertView;
    }
}
