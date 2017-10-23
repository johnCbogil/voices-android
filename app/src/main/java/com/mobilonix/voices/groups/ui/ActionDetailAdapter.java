package com.mobilonix.voices.groups.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobilonix.voices.R;
import com.mobilonix.voices.groups.model.ActionDetail;

import java.util.ArrayList;

public class ActionDetailAdapter extends ArrayAdapter<ActionDetail> {
    private final Context context;
    private final ArrayList<ActionDetail> actionDetails;
    boolean isExpanded = false;

    public ActionDetailAdapter(Context context, int resource, ArrayList<ActionDetail> actionDetails) {
        super(context, R.layout.view_expanding,actionDetails);
        this.context = context;
        this.actionDetails = actionDetails;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.view_expanding, parent, false);

            TextView expandingTitle = (TextView)convertView.findViewById(R.id.expanding_title);
            expandingTitle.setText(actionDetails.get(position).getActionDetailName());
            final ImageView expandingButton = (ImageView) convertView.findViewById(R.id.expanding_button);
            final Animation animShow = AnimationUtils.loadAnimation(context, R.anim.view_show);
            final Animation animHide = AnimationUtils.loadAnimation(context, R.anim.view_hide);
            expandingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isExpanded=false){
                        v.setVisibility(View.VISIBLE);
                        expandingButton.startAnimation(animShow);
                        isExpanded = true;
                    } else {
                        v.setVisibility(View.GONE);
                        expandingButton.startAnimation(animHide);
                        isExpanded=false;
                    }
                }
            });
        }
        return convertView;
    }
}
