package com.mobilonix.voices.groups.ui


import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.baoyz.actionsheet.ActionSheet
import com.mobilonix.voices.Fragments.GroupWebsite
import com.mobilonix.voices.R
import com.mobilonix.voices.VoicesMainActivity
import com.mobilonix.voices.groups.model.Action
import com.mobilonix.voices.groups.model.Group
import com.mobilonix.voices.session.SessionManager
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.group_detail.view.*
import com.mobilonix.voices.analytics.AnalyticsManager
import com.mobilonix.voices.groups.GroupManager
import com.mobilonix.voices.util.AvenirBoldTextView
import com.mobilonix.voices.util.AvenirTextView


/**
 * Created by pc on 10/19/2017.
 */
class GroupDetailContainer(context: Context, attributes: AttributeSet) : FrameLayout(context, attributes), TabLayout.OnTabSelectedListener,
ActionSheet.ActionSheetListener{

    private var isFollowing: Boolean = false
    var userGroups = ArrayList<Group>()
    private lateinit var pd: ProgressDialog
    lateinit var group: Group
    private lateinit var alAdapter: ActionListRecyclerAdapter
    var actions: ArrayList<Action> = ArrayList()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        actions_rv.layoutManager = LinearLayoutManager(context)
        filterActionList()
        alAdapter = ActionListRecyclerAdapter(context, actions)
        actions_rv.adapter = alAdapter
        SessionManager.INSTANCE.fetchAllActions{refreshActionList(it)}
        issues_list_view.adapter = PolicyListAdapter(context, group.policies,(context as VoicesMainActivity).supportFragmentManager)
        setUpViews()
        setListeners()
    }

    private fun setListeners(){
        group_detail_collapsing_tb.addOnOffsetChangedListener{ _, verticalOffset ->  if (group_detail_less_button.visibility == View.VISIBLE && verticalOffset < -20) seeMoreOrLess(false)}
        group_detail_less_button.setOnClickListener {seeMoreOrLess(false) }
        group_detail_more_button.setOnClickListener{ seeMoreOrLess(true)}
        group_detail_visit_site_button.setOnClickListener { visitWebsite() }
        group_detail_follow_group_button.setOnClickListener { if (isFollowing) buildUnfollowActionSheet() else  follow() }
        group_detail_tab_layout.setOnTabSelectedListener(this)
    }

    private fun seeMoreOrLess(more: Boolean) {
        group_detail_description_tv.maxLines = if (more) 100 else 3
        toggleVisibility(group_detail_less_button)
        toggleVisibility(group_detail_more_button)
    }

    private fun visitWebsite() {

        val ft: FragmentTransaction = (context as VoicesMainActivity).supportFragmentManager.beginTransaction()
        val bundle = Bundle()
        bundle.putString("Website",group.groupWebsite)
        val groupWebsite:Fragment = GroupWebsite()
        groupWebsite.arguments = bundle
        groupWebsite.toolbar 
        ft.add(R.id.group_detail_container,groupWebsite).addToBackStack(null).commit()
    }

    private fun refreshActionList(data: ArrayList<Action>?): Boolean {
        if (data == null || data.isEmpty()) {
            return false
        }
        actions.clear()
        actions.addAll(data)
        filterActionList()
        alAdapter.notifyDataSetChanged()
        return actions.isNotEmpty()
    }
    private fun filterActionList() {
        actions
                .filter { it.groupName != group.groupName }
                .forEach { actions.remove(it) }
    }


    override fun onTabReselected(tab: TabLayout.Tab?) {}
    override fun onTabUnselected(tab: TabLayout.Tab?) {}
    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (tab == null) return
        toggleVisibility(actions_rv)
        toggleVisibility(issues_list_view)
    }

    private fun toggleVisibility(v: View) {
        if (v.visibility == View.GONE) v.visibility = View.VISIBLE
        else v.visibility = View.GONE
    }



    private fun follow() {
        pd.setTitle("Following....")
        pd.show()
        GroupManager.INSTANCE.subscribeToGroup(group, true, {callBackFunction(it,true)})
    }

    private fun unFollow() {
        pd.setTitle("Unfollowing....")
        pd.show()
        GroupManager.INSTANCE.unSubscribeFromGroup(group,true, {callBackFunction(it,false)})
    }


    private fun setUpViews() {
        for (g in userGroups) {
            if (g.groupKey == group.groupKey) {
                group_detail_follow_group_button.setText(R.string.following_groups_text)
                isFollowing = true
            }
        }
        Picasso.with(context)
                .load(group.groupImageUrl)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .placeholder(R.drawable.voices_icon)
                .error(R.drawable.voices_icon)
                .fit()
                .into(group_detail_group_image)

        pd = ProgressDialog(context)
        pd.setMessage("")
        pd.isIndeterminate = true
        pd.setCancelable(false)

        group_detail_group_name_tv.text = group.groupName
        group_detail_description_tv.text = group.groupDescription
    }

    private fun callBackFunction(data: Boolean, subscribing: Boolean):Boolean {
        if (data) {
            pd.dismiss()
            val eventName: String = if (subscribing) "SUBSCRIBE_EVENT" else "UNSUBSCRIBE_EVENT"
            AnalyticsManager.INSTANCE.trackEvent(eventName, group.groupKey,
                    SessionManager.INSTANCE.currentUserToken, "none", null)
            val following: Int = if (subscribing) R.string.following_groups_text else  R.string.follow_group
            group_detail_follow_group_button.setText(following)
            isFollowing = subscribing
        }
        return data
    }

    private fun buildUnfollowActionSheet(){
        com.baoyz.actionsheet.ActionSheet.createBuilder(context, (context as VoicesMainActivity).supportFragmentManager )
                .setCancelButtonTitle(R.string.cancel)
                .setCancelableOnTouchOutside(true)
                .setListener(this)
                .setOtherButtonTitles("Unfollow")
                .show()
    }

    override fun onOtherButtonClick(actionSheet: ActionSheet?, index: Int) {unFollow()}

    override fun onDismiss(actionSheet: ActionSheet?, isCancel: Boolean) {}



}