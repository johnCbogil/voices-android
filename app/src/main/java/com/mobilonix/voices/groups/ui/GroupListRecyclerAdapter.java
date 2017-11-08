package com.mobilonix.voices.groups.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobilonix.voices.BuildConfig;
import com.mobilonix.voices.R;
import com.mobilonix.voices.groups.GroupManager;
import com.mobilonix.voices.groups.model.Group;
import com.mobilonix.voices.representatives.ui.RoundedTransformation;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;

class GroupListRecyclerAdapter extends RecyclerView.Adapter<GroupListRecyclerAdapter.GroupListHolder> {
    private ArrayList<Group> groups;
    private GroupManager.GroupType groupType;

    GroupListRecyclerAdapter(ArrayList<Group> groups, GroupManager.GroupType groupType) {
        this.groups = groups;
        this.groupType = groupType;

        Iterator<Group> it = groups.iterator();
        while(it.hasNext()) {
            Group group = it.next();
            if(group.isDebug() && !BuildConfig.DEBUG) {
                it.remove();
            }
        }

    }

    @Override
    public GroupListHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View actionCell = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.groups_item, parent, false);

        return new GroupListHolder(actionCell, groups);
    }

    @Override
    public void onBindViewHolder(GroupListHolder holder, final int position) {
        final Group group = groups.get(position);
        holder.setPosition(position);
        holder.groupName.setText(group.getGroupName());
        holder.groupCategory.setText(group.getGroupCategory());
        holder.groupDescription.setText(group.getGroupDescription());

        if(group.getGroupImageUrl()==null){
            Picasso.with(holder.groupImage.getContext())
                    .load(R.drawable.voices_icon)
                    .fit()
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .placeholder(R.drawable.spinner_moving)
                    .error(R.drawable.voices_icon)
                    .transform(new RoundedTransformation(10, 0))
                    .into(holder.groupImage);
        } else {
            Picasso.with(holder.groupImage.getContext())
                    .load(group.getGroupImageUrl())
                    .fit()
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .placeholder(R.drawable.spinner_moving)
                    .error(R.drawable.voices_icon)
                    .transform(new RoundedTransformation(10, 0))
                    .into(holder.groupImage);
        }

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
        if((groupType == GroupManager.GroupType.ALL) || (groupType == GroupManager.GroupType.USER)) {
            holder.groupView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupManager.INSTANCE.goToGroupDetailPage( group);
                }
            });

        }

    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    static class GroupListHolder extends RecyclerView.ViewHolder {

        ImageView groupImage;
        ImageView arrowImage;

        View groupView;

        TextView groupDescription;
        TextView groupName;
        TextView groupCategory;

        Button learnMoreButton;

        int position;

        GroupListHolder(View itemView, ArrayList<Group> groups) {
            super(itemView);

            this.groupView = itemView;

            groupImage = (ImageView) itemView.findViewById(R.id.cell_group_image);
            arrowImage = (ImageView) itemView.findViewById(R.id.arrow_image);

            groupDescription = (TextView)itemView.findViewById(R.id.cell_group_description);
            groupName = (TextView)itemView.findViewById(R.id.cell_group_name);
            groupCategory = (TextView)itemView.findViewById(R.id.cell_group_category);

            learnMoreButton = (Button)itemView.findViewById(R.id.cell_group_learn_more_button);

        }

        void setPosition(int position) {
            this.position = position;
        }
    }

}
