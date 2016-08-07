package com.mobilonix.voices.groups.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mobilonix.voices.R;
import com.mobilonix.voices.groups.model.Policy;

import java.util.ArrayList;

public class PolicyListAdapter extends ArrayAdapter<Policy>{
    private final Context context;
    private final ArrayList<Policy> policies;

    public PolicyListAdapter(Context context, int resource, ArrayList<Policy> policies) {
        super(context, R.layout.policy_list_item, policies);
        this.context = context;
        this.policies = policies;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater)
                    parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.policy_list_item, parent, false);

            TextView policyText = (TextView)convertView.findViewById(R.id.policy_list_item_text);
            policyText.setText(policies.get(position).getPolicyName());
            ImageButton policyButton = (ImageButton)convertView.findViewById(R.id.policy_list_item_button);
            policyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        return convertView;
    }
}