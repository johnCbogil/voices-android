package com.mobilonix.voices.groups.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mobilonix.voices.R;
import com.mobilonix.voices.groups.GroupManager;
import com.mobilonix.voices.groups.model.Group;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GroupListAdapter extends ArrayAdapter<Group> {

    Context context;
    int resource;
    Dialog groupDialog;
    ArrayList<Group> groups;
    GroupManager.GroupType groupType;

    ArrayList<PolicyObject> policyArray = new ArrayList<PolicyObject>();

    public GroupListAdapter(Context context, int resource, ArrayList<Group> groups, GroupManager.GroupType groupType) {
        super(context, resource, groups);

        this.resource = resource;
        this.context = context;
        this.groups = groups;
        this.groupType = groupType;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, parent, false);

            ImageView groupImage = (ImageView) convertView.findViewById(R.id.cell_group_image);
            ImageView arrowImage = (ImageView) convertView.findViewById(R.id.arrow_image);

            TextView groupDescription = (TextView)convertView.findViewById(R.id.cell_group_description);
            TextView groupName = (TextView)convertView.findViewById(R.id.cell_group_name);
            TextView groupCategory = (TextView)convertView.findViewById(R.id.cell_group_category);

            groupName.setText(groups.get(position).getGroupName());
            groupCategory.setText(groups.get(position).getGroupCategory());
            groupDescription.setText(groups.get(position).getGroupDescription());

            Button learnMoreButton = (Button)convertView.findViewById(R.id.cell_group_learn_more_button);
            learnMoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    groupDialog = new Dialog(getContext());
                    groupDialog.setContentView(R.layout.groups_dialog);
                    Button dialogCloseButton = (Button)groupDialog.findViewById(R.id.dialog_close_button);
                    PolicyObject policy1 = new PolicyObject("Policy1");
                    PolicyObject policy2 = new PolicyObject("Policy2");
                    policyArray.add(policy1);
                    policyArray.add(policy2);
                    PolicyListAdapter policyAdapter = new PolicyListAdapter(getContext(),R.layout.policy_list_item, policyArray);
                    ListView policyListView = (ListView)groupDialog.findViewById(R.id.policy_list);
                    policyListView.setAdapter(policyAdapter);
                    // if button is clicked, close the custom dialog
                    dialogCloseButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            groupDialog.dismiss();
                        }
                    });

                    groupDialog.show();
                }
            });

            Picasso.with(groupImage.getContext())
                    .load(groups.get(position).getGroupImageUrl())
                    .placeholder(R.drawable.representatives_place_holder)
                    .fit()
                    .into(groupImage);

            if((groupType == GroupManager.GroupType.USER) || (groupType == GroupManager.GroupType.ALL)) {
                groupDescription.setVisibility(View.GONE);
                learnMoreButton.setVisibility(View.GONE);
                arrowImage.setVisibility(View.VISIBLE);
            } else {
                arrowImage.setVisibility(View.GONE);
                groupDescription.setVisibility(View.VISIBLE);
                learnMoreButton.setVisibility(View.VISIBLE);
            }

        }

        return convertView;
    }
}
