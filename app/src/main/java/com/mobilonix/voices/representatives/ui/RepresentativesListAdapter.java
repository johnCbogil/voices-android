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
import android.widget.TextView;

import com.mobilonix.voices.R;
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
            representativesNameText.setText(representatives.get(position).getName());

            Picasso.with(listImage.getContext())
                    .load(representatives.get(position).getRepresentativeImageUrl())
                    .resize(400, 500)
                    .placeholder(R.drawable.placeholder_spinner)
                    .error(R.drawable.representatives_place_holder_male)
                    .transform(new RoundedTransformation(50, 4))
                    .into(listImage);

            final ImageView twitterImage = (ImageView)convertView.findViewById(R.id.representatives_list_twitter_image);
            final ImageView callImage = (ImageView)convertView.findViewById(R.id.representatives_list_call_image);
            final ImageView emailImage = (ImageView)convertView.findViewById(R.id.representatives_list_email_image);

            String check = representatives.get(position).getPhoneNumber();
            if(check == null || check.equals("")){
                callImage.setColorFilter(getContext().getResources().getColor(R.color.light_grey));
                callImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleNoContactInfoDialog();
                    }
                });
            } else {
                callImage.setOnClickListener(new View.OnClickListener() {
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
                emailImage.setColorFilter(getContext().getResources().getColor(R.color.light_grey));
                emailImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleNoContactInfoDialog();
                    }
                });
            } else {
                emailImage.setOnClickListener(new View.OnClickListener() {
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
                twitterImage.setColorFilter(getContext().getResources().getColor(R.color.light_grey));
                twitterImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleNoContactInfoDialog();
                    }
                });
            } else {
                twitterImage.setOnClickListener(new View.OnClickListener() {
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
        }

        return convertView;
    }
    public void toggleNoContactInfoDialog() {

        final Dialog noContactInfoDialog;

        noContactInfoDialog = new Dialog(getContext());
        noContactInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        noContactInfoDialog.setContentView(R.layout.dialog_nocontactinfo);
        Button gotItButton = (Button)noContactInfoDialog.findViewById(R.id.got_it_button_2);
        gotItButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noContactInfoDialog.dismiss();
            }
        });
        noContactInfoDialog.show();
    }
}
