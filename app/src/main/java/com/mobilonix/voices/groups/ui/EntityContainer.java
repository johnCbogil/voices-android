package com.mobilonix.voices.groups.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobilonix.voices.R;
import com.mobilonix.voices.VoicesApplication;
import com.mobilonix.voices.groups.GroupManager;
import com.mobilonix.voices.util.AvenirBoldTextView;

import java.util.ArrayList;

public class EntityContainer extends FrameLayout {
    RecyclerView entityList;

    TextView entityListTopText;
    TextView entityListBottomText;

    GroupManager.GroupType type;

    ImageView noFollowImage;



    AvenirBoldTextView actionSelectionButton;
    AvenirBoldTextView groupsSelectionButton;

    public EntityContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();

        entityList = (RecyclerView)findViewById(R.id.entity_list);

        entityListTopText = (TextView)findViewById(R.id.entity_no_follows_top_line);
        entityListBottomText = (TextView)findViewById(R.id.entity_no_follows_bottom_line);

        noFollowImage = (ImageView)findViewById(R.id.entity_no_follows_image);

        actionSelectionButton=(AvenirBoldTextView)findViewById(R.id.actions_button);
        groupsSelectionButton=(AvenirBoldTextView)findViewById(R.id.groups_button);

        actionSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupManager.INSTANCE.toggleGroups(GroupManager.GroupType.ACTION);
            }
        });

        groupsSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupManager.INSTANCE.toggleGroups(GroupManager.GroupType.USER);
            }
        });

    }


    public void setType(GroupManager.GroupType type) {
        this.type = type;
        if(type == GroupManager.GroupType.ACTION) {
            entityListTopText.setText(R.string.no_follow_actions_top);
            entityListBottomText.setText(R.string.no_follow_actions);
        } else if(type == GroupManager.GroupType.USER) {
            entityListTopText.setText(R.string.no_follow_groups_top);
            entityListBottomText.setText(R.string.no_follow_groups);
        } else if(type == GroupManager.GroupType.ALL) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)entityList.getLayoutParams();
            params.setMargins(0,0,0,0);
            entityList.setLayoutParams(params);
            entityListTopText.setText(VoicesApplication.EMPTY);
            entityListBottomText.setText(R.string.groups_fetch_error);
            noFollowImage.setImageResource(R.drawable.voices_error);
        }
    }

    /**
     * Add items to the entity list. If the list is empty, show a no items UX
     *
     * @param list
     * @param type
     */
    public void addItems(ArrayList list, GroupManager.GroupType type) {
        toggleNoFollowsLayout(list.size() > 0, type);

        entityList.setLayoutManager(new LinearLayoutManager(getContext()));

        if (type == (GroupManager.GroupType.ACTION)) {
            entityList.setAdapter(new ActionListRecyclerAdapter(getContext(), list));
        } else {
            entityList.setAdapter(new GroupListRecyclerAdapter(list, type));
        }
    }

    private void toggleNoFollowsLayout(boolean state, GroupManager.GroupType type) {

        setType(type);

        if(state) {
            entityList.setVisibility(View.VISIBLE);

            entityListTopText.setVisibility(View.GONE);
            entityListBottomText.setVisibility(View.GONE);

            noFollowImage.setVisibility(View.GONE);
        } else {
            entityList.setVisibility(View.GONE);

            entityListTopText.setVisibility(View.VISIBLE);
            entityListBottomText.setVisibility(View.VISIBLE);

            noFollowImage.setVisibility(View.VISIBLE);
        }
    }
}
