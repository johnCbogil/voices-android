package com.mobilonix.voices.navigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobilonix.voices.R;

import java.util.ArrayList;

public class NavigationAdapter extends ArrayAdapter<NavigationObject> {
    int resource;
    ArrayList<NavigationObject> navigationObjects = new ArrayList<NavigationObject>();

    ViewHolder topCell = null;

    public NavigationAdapter(Context context, int resource, ArrayList<NavigationObject> navigationObjects) {
        super(context, resource, navigationObjects);
        this.navigationObjects = navigationObjects;
        this.resource = resource;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder mViewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)
                    parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, parent, false);
            mViewHolder = new ViewHolder();

            mViewHolder.mNavigationObject = (LinearLayout) convertView.findViewById(R.id.navigation_object);
            mViewHolder.mTitle = (TextView) convertView.findViewById(R.id.navigation_title);
            mViewHolder.mDescription = (TextView) convertView.findViewById(R.id.navigation_description);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.mTitle.setText(navigationObjects.get(position).getTitle());
        mViewHolder.mDescription.setText(navigationObjects.get(position).getDescription());

        /* This is captured to we can get a reference to the top cell in the navigation list
        * after it's defined */
        if(position == 0) {
            topCell = mViewHolder;
        }


        return convertView;
    }

    private class ViewHolder {
        private LinearLayout mNavigationObject;
        private TextView mTitle;
        private TextView mDescription;
    }

    /**
     * Update the top cell of the navigation list adapter
     *
     * @param address
     */
    public void updateTopCell(String address) {
        if(topCell != null) {
            topCell.mDescription.setText(address);
        }
    }
}
