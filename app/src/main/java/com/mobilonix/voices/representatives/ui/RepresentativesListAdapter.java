package com.mobilonix.voices.representatives.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import com.mobilonix.voices.representatives.RepresentativesManager;
import com.mobilonix.voices.representatives.model.Representative;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.mRepsName.setText(representatives.get(position).getName());

        setImage(mViewHolder.mRepsImage, position);

        mViewHolder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailPageLayout detailPageLayout = RepresentativesManager.INSTANCE.getDetailPageLayout();

                detailPageLayout.repsName.setText(representatives.get(position).getName());

                setImage(detailPageLayout.repsImage, position);

                if (representatives.get(position).getParty().equals("")) {
                    detailPageLayout.repsParty.setText(VoicesApplication.getContext().getResources().getString(R.string.party)
                            + VoicesApplication.getContext().getResources().getString(R.string.not_available));
                } else {
                    detailPageLayout.repsParty.setText(VoicesApplication.getContext().getResources().getString(R.string.party)
                            + representatives.get(position).getParty());
                }

                if (representatives.get(position).getDistrict().equals("")) {
                    detailPageLayout.repsDistrict.setText(VoicesApplication.getContext().getResources().getString(R.string.district)
                            + VoicesApplication.getContext().getResources().getString(R.string.not_available));
                } else {
                    detailPageLayout.repsDistrict.setText(VoicesApplication.getContext().getResources().getString(R.string.district)
                            + representatives.get(position).getDistrict());
                }

                if (representatives.get(position).getElectionDate().equals("")) {
                    detailPageLayout.repsElectionDate.setText(VoicesApplication.getContext().getResources().getString(R.string.next_election)
                            + VoicesApplication.getContext().getResources().getString(R.string.not_available));
                } else {
                    detailPageLayout.repsElectionDate.setText(VoicesApplication.getContext().getResources().getString(R.string.next_election)
                            + representatives.get(position).getElectionDate());
                }

                if (representatives.get(position).getElectionDate().contains("2017")) {
                    detailPageLayout.electionUpcoming.setText(R.string.election_upcoming);
                } else {
                    detailPageLayout.electionUpcoming.setText("");
                }

                String check = representatives.get(position).getPhoneNumber();
                if (check == null || check.equals("")) {
                    detailPageLayout.mCallImage.setColorFilter(getContext().getResources().getColor(R.color.light_grey));
                    detailPageLayout.mCallImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String emailInstead = getContext().getResources().getString(R.string.email_instead);
                            toggleNoContactInfoDialog(emailInstead);
                        }
                    });
                } else {
                    detailPageLayout.mCallImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + representatives.get(position).getPhoneNumber()));
                            v.getContext().startActivity(intent);
                        }
                    });
                }

                check = representatives.get(position).getEmailAddress();
                if (check == null || check.equals("")) {
                    detailPageLayout.mEmailImage.setColorFilter(getContext().getResources().getColor(R.color.light_grey));
                    detailPageLayout.mEmailImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String callInstead = getContext().getResources().getString(R.string.call_instead);
                            toggleNoContactInfoDialog(callInstead);
                        }
                    });
                } else {
                    detailPageLayout.mEmailImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
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
                }

                check = representatives.get(position).getTwitterHandle();
                if (check == null || check.equals("")) {
                    detailPageLayout.mTwitterImage.setColorFilter(getContext().getResources().getColor(R.color.light_grey));
                    detailPageLayout.mTwitterImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String callInstead = getContext().getResources().getString(R.string.call_instead);
                            toggleNoContactInfoDialog(callInstead);
                        }
                    });
                } else {
                    detailPageLayout.mTwitterImage.setOnClickListener(new View.OnClickListener() {
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
                detailPageLayout.setVisibility(View.VISIBLE);
            }
        });

        return convertView;
    }

    public void toggleNoContactInfoDialog(String text) {

        final Dialog noContactInfoDialog;

        noContactInfoDialog = new Dialog(getContext());
        noContactInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        noContactInfoDialog.setContentView(R.layout.dialog_nocontactinfo);
        TextView considerText = (TextView) noContactInfoDialog.findViewById(R.id.consider_text);
        considerText.setText(text);
        Button gotItButton = (Button) noContactInfoDialog.findViewById(R.id.got_it_button_2);
        gotItButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noContactInfoDialog.dismiss();
            }
        });
        noContactInfoDialog.show();
    }

    public void setImage(final ImageView image, final int position) {

        String gender = representatives.get(position).getGender();

        int id;

        switch (gender) {
            case "M": {
                id = R.drawable.representatives_place_holder_male;
                break;
            }
            case "F": {
                id = R.drawable.representatives_place_holder_female;
                break;
            }
            default: {
                double random = Math.random();
                id = random > 0.5
                        ? R.drawable.representatives_place_holder_male
                        : R.drawable.representatives_place_holder_female;
                break;
            }
        }

        Picasso.with(image.getContext())
                .load(representatives.get(position).getRepresentativeImageUrl())
                .resize(110,110)
                .centerCrop()
                .placeholder(R.drawable.placeholder_spinner)
                .error(id)
                .transform(new RoundedTransformation(10, 4))
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        image.setImageBitmap(bitmap);
                    }
                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        try {
                            image.setImageBitmap(RepresentativesManager.INSTANCE.getBitmapFromMemCache(representatives.get(position).getName()));
                        } catch (Exception e) {
                            e.printStackTrace();
                       }
                  }

                   @Override
                   public void onPrepareLoad(Drawable placeHolderDrawable) {
                   }
               });
    }

    private class ViewHolder {
        private LinearLayout mLinearLayout;
        private ImageView mRepsImage;
        private TextView mRepsName;
    }
}