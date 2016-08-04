package com.mobilonix.voices.groups.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobilonix.voices.R;
import com.mobilonix.voices.groups.GroupManager;
import com.mobilonix.voices.groups.model.Action;
import com.mobilonix.voices.groups.model.Group;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ActionListAdapter extends ArrayAdapter<Action> {

    Context context;
    int resource;
    ArrayList<Action> actions;

    public ActionListAdapter(Context context, int resource, ArrayList<Action> actions) {
        super(context, resource, actions);

        this.resource = resource;
        this.context = context;
        this.actions = actions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, parent, false);

            ImageView actionImage = (ImageView) convertView.findViewById(R.id.cell_group_image);
            ImageView arrowImage = (ImageView) convertView.findViewById(R.id.arrow_image);

            TextView actionDescription = (TextView)convertView.findViewById(R.id.cell_group_description);
            TextView actionName = (TextView)convertView.findViewById(R.id.cell_group_name);
            TextView actionCategory = (TextView)convertView.findViewById(R.id.cell_group_category);

            actionName.setText(actions.get(position).getTitle());
            actionCategory.setText(actions.get(position).getSubject());
            actionDescription.setText(actions.get(position).getBody());

            Button learnMoreButton = (Button)convertView.findViewById(R.id.cell_group_learn_more_button);

            Picasso.with(actionImage.getContext())
                    .load(actions.get(position).getImageUrl())
                    .placeholder(R.drawable.representatives_place_holder)
                    .fit()
                    .into(actionImage);


            arrowImage.setVisibility(View.GONE);
            actionDescription.setVisibility(View.VISIBLE);
            learnMoreButton.setVisibility(View.VISIBLE);

        }

        return convertView;
    }
}
