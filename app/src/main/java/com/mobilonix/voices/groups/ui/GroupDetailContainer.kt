package com.mobilonix.voices.groups.ui


import android.content.Context
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ListAdapter
import com.mobilonix.voices.R
import com.mobilonix.voices.VoicesMainActivity
import com.mobilonix.voices.callbacks.Callback
import com.mobilonix.voices.groups.model.Action
import com.mobilonix.voices.groups.model.Group
import com.mobilonix.voices.session.SessionManager
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.group_detail.view.*


/**
 * Created by pc on 10/19/2017.
 */
class GroupDetailContainer(context: Context, attributes: AttributeSet) : FrameLayout(context, attributes), Callback<ArrayList<Action>> , TabLayout.OnTabSelectedListener {


    lateinit var group:Group
    private val actions:ArrayList<Action> = ArrayList()


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        actions_rv.layoutManager = LinearLayoutManager(context)
        actions_rv.adapter = ActionListRecyclerAdapter(context,actions)

        group_detail_tab_layout.setOnTabSelectedListener(this)
        SessionManager.INSTANCE.fetchAllActions(this)
        issues_list_view.adapter = PolicyListAdapter(context,group.policies)
        Picasso.with(context)
                .load(group.getGroupImageUrl())
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .placeholder(R.drawable.voices_icon)
                .error(R.drawable.voices_icon)
                .fit()
                .into(group_detail_group_image)


    }



    override fun onExecuted(data: ArrayList<Action>?): Boolean {
        if (data == null) {
            return false
        }
        actions.clear()
        for(a in data) {
            if (a.groupName.equals(group.groupName))actions.add(a)
        }
        return true;
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        if(tab == null)return
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        if(tab == null)return
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        if(tab == null)return
        toggleVisibility(actions_rv)
        toggleVisibility(issues_list_view)
    }

    fun toggleVisibility(v : View){
        if(v.visibility == View.GONE)v.visibility = View.VISIBLE
        else v.visibility = View.GONE
    }

}