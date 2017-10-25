package com.mobilonix.voices.groups.ui


import android.app.ProgressDialog
import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.TabLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.mobilonix.voices.R
import com.mobilonix.voices.callbacks.Callback
import com.mobilonix.voices.groups.model.Action
import com.mobilonix.voices.groups.model.Group
import com.mobilonix.voices.session.SessionManager
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.group_detail.view.*
import android.support.v4.content.ContextCompat
import com.mobilonix.voices.analytics.AnalyticsManager
import com.mobilonix.voices.callbacks.Callback2
import com.mobilonix.voices.groups.GroupManager
import kotlinx.android.synthetic.main.dialog_groups.view.*


/**
 * Created by pc on 10/19/2017.
 */
class GroupDetailContainer(context: Context, attributes: AttributeSet) : FrameLayout(context, attributes),
        Callback<ArrayList<Action>>, TabLayout.OnTabSelectedListener, AppBarLayout.OnOffsetChangedListener{


    var seeMoreClicked: Boolean = false
    var isFollowing: Boolean = false
    var userGroups = ArrayList<Group>()
    lateinit var pd: ProgressDialog
    lateinit var group: Group
    lateinit var alAdapter: ActionListRecyclerAdapter
    private val actions: ArrayList<Action> = ArrayList()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        actions_rv.layoutManager = LinearLayoutManager(context)
        alAdapter = ActionListRecyclerAdapter(context, actions)
        actions_rv.adapter = alAdapter
        group_detail_tab_layout.setOnTabSelectedListener(this)
        SessionManager.INSTANCE.fetchAllActions(this)
        issues_list_view.adapter = PolicyListAdapter(context, group.policies)
        setUpViews()
        group_detail_collapsing_tb.addOnOffsetChangedListener(this)
        group_detail_more_less_button.setOnClickListener { seeMore() }
        group_detail_visit_site_button.setOnClickListener { visitWebsite() }
        group_detail_follow_group_button.setOnClickListener { if (isFollowing) unFollow() else  follow() }
    }

    private fun seeMore() {
        if (seeMoreClicked) {
            group_detail_description_tv.maxLines = 3
            group_detail_more_less_button.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_keyboard_arrow_down_black_24dp))
        } else {
            group_detail_description_tv.maxLines = 100
            group_detail_more_less_button.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_keyboard_arrow_up_black_24dp))
        }
        seeMoreClicked = !seeMoreClicked
    }

    private fun visitWebsite() {}

    override fun onExecuted(data: ArrayList<Action>?): Boolean {
        if (data == null) {
            return false
        }
        actions.clear()
        for (a in data) {
            if (a.groupName.equals(group.groupName)) actions.add(a)
        }
        alAdapter.notifyDataSetChanged()
        return true;
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {}
    override fun onTabUnselected(tab: TabLayout.Tab?) {}
    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (tab == null) return
        toggleVisibility(actions_rv)
        toggleVisibility(issues_list_view)
    }

    fun toggleVisibility(v: View) {
        if (v.visibility == View.GONE) v.visibility = View.VISIBLE
        else v.visibility = View.GONE
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        if (seeMoreClicked) seeMore()
    }

    private fun follow() {
        pd.setTitle("Following....")
        pd.show();
        GroupManager.INSTANCE.subscribeToGroup(group, true,  Callback {callBackFunction(it,true)})
    }

    private fun unFollow() {
        pd.setTitle("Unfollowing....");
        pd.show()
        GroupManager.INSTANCE.unSubscribeFromGroup(group,true, Callback {callBackFunction(it,false)})
    }


    private fun setUpViews() {
        for (g in userGroups) {
            if (g.getGroupKey().equals(group.getGroupKey())) {
                group_detail_follow_group_button.setText(R.string.following_groups_text)
                isFollowing = true
            }
        }
        Picasso.with(context)
                .load(group.getGroupImageUrl())
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .placeholder(R.drawable.voices_icon)
                .error(R.drawable.voices_icon)
                .fit()
                .into(group_detail_group_image)

        pd = ProgressDialog(context)
        pd.setMessage("")
        pd.setIndeterminate(true)
        pd.setCancelable(false)


        group_detail_group_name_tv.text = group.groupName
        group_detail_description_tv.text = group.groupDescription
    }

    private fun callBackFunction(data: Boolean, subscribing: Boolean):Boolean {
        if (data) {
            pd.dismiss()
            val eventName: String = if (subscribing) "SUBSCRIBE_EVENT" else "UNSUBSCRIBE_EVENT"
            AnalyticsManager.INSTANCE.trackEvent(eventName, group.getGroupKey(),
                    SessionManager.INSTANCE.getCurrentUserToken(), "none", null)
            val following: Int = if (subscribing) R.string.following_groups_text else  R.string.follow_group
            group_detail_follow_group_button.setText(following)
            isFollowing = subscribing
            return true
        }
        return false
    }

}