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
import com.mobilonix.voices.groups.model.Action;
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
                .placeholder(R.drawable.representatives_place_holder)
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

        public ActionListHolder(View itemView, ArrayList<Action> actions) {
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
