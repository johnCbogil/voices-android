package com.mobilonix.voices.groups.ui;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mobilonix.voices.R;
import com.mobilonix.voices.groups.GroupManager;
import com.mobilonix.voices.groups.model.Action;
import com.mobilonix.voices.groups.model.Group;
import com.mobilonix.voices.groups.model.Policy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GroupListRecylerAdapter extends RecyclerView.Adapter<GroupListRecylerAdapter.GroupListHolder> {

    private final static String TAG = GroupListRecylerAdapter.class.getCanonicalName();

    ArrayList<Group> groups;
    GroupManager.GroupType groupType;

    public GroupListRecylerAdapter(Context context, ArrayList<Group> groups, GroupManager.GroupType groupType) {
        this.groups = groups;
        this.groupType = groupType;
    }

    @Override
    public GroupListHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View actionCell = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_group, parent, false);

        GroupListHolder pvh = new GroupListHolder(actionCell, groups);
        return pvh;
    }

    @Override
    public void onBindViewHolder(GroupListHolder holder, final int position) {

        holder.setPosition(position);

        Log.wtf(TAG, "Adapter GET VIEW: " + groups.get(position).getGroupName());

        holder.groupName.setText(groups.get(position).getGroupName());
        holder.groupCategory.setText(groups.get(position).getGroupCategory());
        holder.groupDescription.setText(groups.get(position).getGroupDescription());

        Picasso.with(holder.groupImage.getContext())
                .load(groups.get(position).getGroupImageUrl())
                .placeholder(R.drawable.representatives_place_holder)
                .fit()
                .into(holder.groupImage);

        holder.arrowImage.setVisibility(View.GONE);
        holder.groupDescription.setVisibility(View.VISIBLE);
        holder.learnMoreButton.setVisibility(View.VISIBLE);

        if((groupType == GroupManager.GroupType.USER) || (groupType == GroupManager.GroupType.ALL)) {
            holder.groupDescription.setVisibility(View.GONE);
            holder.learnMoreButton.setVisibility(View.GONE);
            holder.arrowImage.setVisibility(View.VISIBLE);
        } else {
            holder.arrowImage.setVisibility(View.GONE);
            holder.groupDescription.setVisibility(View.VISIBLE);
            holder.learnMoreButton.setVisibility(View.VISIBLE);
        }

            /* For now we add debug behavior for group subscription */
        if(groupType == GroupManager.GroupType.ALL) {
            holder.groupView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupManager.INSTANCE.toggleSubscribeToGroupDialog(v.getContext(), groups.get(position));
                }
            });

        }


    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public static class GroupListHolder extends RecyclerView.ViewHolder {

        ImageView groupImage;
        ImageView arrowImage;

        View groupView;

        TextView groupDescription;
        TextView groupName;
        TextView groupCategory;

        Button learnMoreButton;

        Dialog groupDialog;

        int position;

        public GroupListHolder(View itemView, ArrayList<Group> groups) {
            super(itemView);

            this.groupView = itemView;

            groupImage = (ImageView) itemView.findViewById(R.id.cell_group_image);
            arrowImage = (ImageView) itemView.findViewById(R.id.arrow_image);

            groupDescription = (TextView)itemView.findViewById(R.id.cell_group_description);
            groupName = (TextView)itemView.findViewById(R.id.cell_group_name);
            groupCategory = (TextView)itemView.findViewById(R.id.cell_group_category);

            learnMoreButton = (Button)itemView.findViewById(R.id.cell_group_learn_more_button);

            learnMoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ArrayList<PolicyObject> policyArray = new ArrayList<>();

                    groupDialog = new Dialog(v.getContext());
                    groupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    groupDialog.setContentView(R.layout.groups_dialog);
                    Button dialogCloseButton = (Button)groupDialog.findViewById(R.id.dialog_close_button);
                    PolicyObject policy1 = new PolicyObject("Policy1");
                    PolicyObject policy2 = new PolicyObject("Policy2");
                    policyArray.add(policy1);
                    policyArray.add(policy2);
                    PolicyListAdapter policyAdapter
                            = new PolicyListAdapter(v.getContext(),
                            R.layout.policy_list_item,
                            policyArray);
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

        }

        public void setPosition(int position) {
            this.position = position;
        }
    }

}
