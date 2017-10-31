package com.mobilonix.voices.groups.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobilonix.voices.R;
import com.mobilonix.voices.VoicesMainActivity;
import com.mobilonix.voices.groups.GroupManager;
import com.mobilonix.voices.groups.model.Action;
import com.mobilonix.voices.representatives.ui.RoundedTransformation;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class ActionListRecyclerAdapter extends RecyclerView.Adapter<ActionListRecyclerAdapter.ActionListHolder> {
    VoicesMainActivity activity = new VoicesMainActivity();
    ArrayList<Action> actions;

    public ActionListRecyclerAdapter(Context context, ArrayList<Action> actions) {

        ArrayList<Action> modifiedActionsList = new ArrayList<>();

        //TODO: There is a bug here which requires this go between.  Fix it
        for(int i = 0; i < actions.size(); i++) {
            if(i % 2 == 0) {
                modifiedActionsList.add(actions.get(i));
            }
        }

        Collections.sort(modifiedActionsList);
        this.actions = modifiedActionsList;
    }

    @Override
    public ActionListHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View actionCell = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.groups_item, parent, false);

        ActionListHolder pvh = new ActionListHolder(actionCell, actions);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ActionListHolder holder, int position) {

        holder.setPosition(position);

        holder.actionName.setText(actions.get(position).getGroupName());
        holder.actionCategory.setText(actions.get(position).getSubject());
        holder.actionDescription.setText(actions.get(position).getTitle());

        Picasso.with(holder.actionImage.getContext())
                .load(actions.get(position).getImageUrl())
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .placeholder(R.drawable.spinner_moving)
                .error(R.drawable.voices_icon)
                .transform(new RoundedTransformation(10, 0))
                .fit()
                .into(holder.actionImage);

        holder.arrowImage.setVisibility(View.GONE);
        holder.actionDescription.setVisibility(View.VISIBLE);
        holder.learnMoreButton.setVisibility(View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return actions.size();
    }

    public class ActionListHolder extends RecyclerView.ViewHolder {

        ImageView actionImage;
        ImageView arrowImage;

        TextView actionDescription;
        TextView actionName;
        TextView actionCategory;
        Button learnMoreButton;

        int position;

        public ActionListHolder(View itemView, final ArrayList<Action> actions) {
            super(itemView);

            actionImage = (ImageView) itemView.findViewById(R.id.cell_group_image);
            arrowImage = (ImageView) itemView.findViewById(R.id.arrow_image);

            actionDescription = (TextView)itemView.findViewById(R.id.cell_group_description);
            actionName = (TextView)itemView.findViewById(R.id.cell_group_name);
            actionCategory = (TextView)itemView.findViewById(R.id.cell_group_category);

            learnMoreButton = (Button)itemView.findViewById(R.id.cell_group_learn_more_button);

            learnMoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupManager.INSTANCE.toggleGroups(GroupManager.GroupType.ACTION_DETAIL);
                    GroupManager.INSTANCE.toggleActionDetailView(activity, v.getContext(), actions.get(position));
                }
            });
        }
        public void setPosition(int position) {
            this.position = position;
        }
    }

}
