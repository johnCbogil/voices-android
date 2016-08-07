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
import com.mobilonix.voices.base.util.GeneralUtil;
import com.mobilonix.voices.groups.GroupManager;
import com.mobilonix.voices.groups.model.Action;
import com.mobilonix.voices.groups.model.Group;
import com.mobilonix.voices.groups.model.Policy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ActionListRecylerAdapter extends RecyclerView.Adapter<ActionListRecylerAdapter.ActionListHolder> {

    private final static String TAG = ActionListRecylerAdapter.class.getCanonicalName();

    ArrayList<Action> actions;

    public ActionListRecylerAdapter(Context context, ArrayList<Action> actions) {
        this.actions = actions;
    }

    @Override
    public ActionListHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View actionCell = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_group, parent, false);

        ActionListHolder pvh = new ActionListHolder(actionCell, actions);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ActionListHolder holder, int position) {

        holder.setPosition(position);

        Log.wtf(TAG, "Adapter GET VIEW: " + actions.get(position).getTitle());

        holder.actionName.setText(actions.get(position).getTitle());
        holder.actionCategory.setText(actions.get(position).getSubject());
        holder.actionDescription.setText(actions.get(position).getBody());

        Picasso.with(holder.actionImage.getContext())
                .load(actions.get(position).getImageUrl())
                .placeholder(R.drawable.placeholder_spinner)
                .error(R.drawable.representatives_place_holder)
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

    public static class ActionListHolder extends RecyclerView.ViewHolder {

        Dialog groupDialog;

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
                    ArrayList<Policy> policyArray = new ArrayList<>();

                    GeneralUtil.toast("Finding policies for action: "
                            + actions.get(position).getTitle()
                            + " and groupkey: "
                            + actions.get(position).getGroupKey()
                            + "and policies "
                            +  GroupManager.INSTANCE.findGroupWithKey(actions.get(position).getGroupKey()));

                    Group associatedGroup = GroupManager.INSTANCE
                            .findGroupWithKey(actions.get(position).getGroupKey());

                    if(associatedGroup == null) {
                        return;
                    }

                    groupDialog = new Dialog(v.getContext());
                    groupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    groupDialog.setContentView(R.layout.dialog_groups);

                    TextView groupInfoDescriptionText = (TextView)groupDialog.findViewById(R.id.group_info_description_text);
                    TextView groupInfoPolicyText = (TextView)groupDialog.findViewById(R.id.group_info_policy_text);
                    ImageView groupInfoImage = (ImageView)groupDialog.findViewById(R.id.group_info_image);

                    groupInfoDescriptionText.setText(associatedGroup.getGroupDescription());
                    groupInfoPolicyText.setText(associatedGroup.getGroupCategory());

                    Picasso.with(groupInfoImage.getContext())
                            .load(associatedGroup.getGroupImageUrl())
                            .fit()
                            .error(R.drawable.representatives_place_holder)
                            .placeholder(R.drawable.placeholder_spinner)
                            .into(groupInfoImage);

                    policyArray.addAll(associatedGroup.getPolicies());
                    PolicyListAdapter policyAdapter
                            = new PolicyListAdapter(v.getContext(),
                            R.layout.policy_list_item,
                            policyArray);
                    ListView policyListView = (ListView)groupDialog.findViewById(R.id.policy_list);

                    groupDialog.show();
                }
            });

        }

        public void setPosition(int position) {
            this.position = position;
        }
    }

}
