package com.mobilonix.voices.groups.ui


import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.baoyz.actionsheet.ActionSheet
import com.mobilonix.voices.fragments.GroupWebsite
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


/**
 * Created by pc on 10/19/2017.
 */
class GroupDetailContainer(context: Context, attributes: AttributeSet) : FrameLayout(context, attributes), TabLayout.OnTabSelectedListener,
        ActionSheet.ActionSheetListener {


    //Handle back press from website visit
    lateinit var back: View
    //Records if person clicks follow button, whether or not it is to follow or unfollow
    private var isFollowing: Boolean = false
    //Takes all groups a user is following
    var userGroups = ArrayList<Group>()
    //Shows when API call is in progress to follow or unfollow
    private lateinit var pd: ProgressDialog
    //Group this page is showing
    lateinit var group: Group
    //Adapter shows list of actions belonging to group
    private lateinit var alAdapter: ActionListRecyclerAdapter
    //List holding all actions
    var actions: ArrayList<Action> = ArrayList()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        //set linear layout manager to RecyclerView
        actions_rv.layoutManager = LinearLayoutManager(context)
        //***WE NEED TO CREATE API CALL TO RETRIEVE ALL ACTIONS OF GROUP***//
        //Filters action list to remove all actions not belonging to this group.
        filterActionList()
        alAdapter = ActionListRecyclerAdapter(context, actions)
        //Setting adapter
        actions_rv.adapter = alAdapter
        //Getting all actions to refresh action list
        SessionManager.INSTANCE.fetchAllActions { /* this is the callback interface */refreshActionList(it) }
        //Takes action ListView and sets an adapter
        issues_list_view.adapter = PolicyListAdapter(context, group.policies, (context as VoicesMainActivity).supportFragmentManager)
        setUpViews()
        setListeners()
    }

    private fun setListeners() {
        //when person clicks to see more of description
        group_detail_less_button.setOnClickListener { seeMoreOrLess(false) }
        //when person clicks to see less of description
        group_detail_more_button.setOnClickListener { seeMoreOrLess(true) }
        group_detail_visit_site_button.setOnClickListener { visitWebsite() }
        group_detail_follow_group_button.setOnClickListener { if (isFollowing) buildUnFollowActionSheet() else follow() }
        group_detail_tab_layout.setOnTabSelectedListener(this)
    }

    private fun seeMoreOrLess(more: Boolean) {
        group_detail_description_tv.maxLines = if (more) 100 else 3
        toggleVisibility(group_detail_less_button)
        toggleVisibility(group_detail_more_button)
    }

    private fun visitWebsite() {

        val ft: FragmentManager = (context as VoicesMainActivity).supportFragmentManager
        val bundle = Bundle()
        val groupWebsite = GroupWebsite()
        bundle.putString("Website", group.groupWebsite)
        //when user goes back to this screen
        ft.addOnBackStackChangedListener { back.setOnClickListener { GroupManager.INSTANCE.onBackPress() } }

        groupWebsite.arguments = bundle
        groupWebsite.back = back
        ft.beginTransaction().add(R.id.group_detail_container, groupWebsite).addToBackStack(null).commit()
    }

    //New Actions List Arrives
    private fun refreshActionList(data: ArrayList<Action>?): Boolean {
        if (data == null || data.isEmpty()) {
            return false
        }
        GroupManager.INSTANCE.setAllActions(data)
        filterActionList()
        alAdapter.notifyDataSetChanged()
        return actions.isNotEmpty()
    }

    //Refreshes action list and sets image url for list to populate 
    private fun filterActionList() {
        val tempList = ArrayList<Action>()
        for (action in actions) {
            if (action.groupKey == group.groupKey) {
                action.imageUrl = group.groupImageUrl
                tempList.add(action)
            }
        }
        actions = tempList
    }


    override fun onTabReselected(tab: TabLayout.Tab?) {}
    override fun onTabUnselected(tab: TabLayout.Tab?) {}
    //If tab is selected to view actions or policies
    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (tab == null) return
        //changes recycler view visibility
        toggleVisibility(actions_rv)
        //changes list view visibility
        toggleVisibility(issues_list_view)
    }

    private fun toggleVisibility(v: View) {
        //takes a view's visibility and sets it to the opposite
        if (v.visibility == View.GONE) v.visibility = View.VISIBLE
        else v.visibility = View.GONE
    }

    private fun follow() {
        pd.setTitle("Following....")
        pd.show()
        GroupManager.INSTANCE.subscribeToGroup(group, true, { callBackFunction(it, true) })
    }

    private fun unFollow() {
        pd.setTitle("Unfollowing....")
        pd.show()
        GroupManager.INSTANCE.unSubscribeFromGroup(group, true, { callBackFunction(it, false) })
    }


    private fun setUpViews() {
        //Determines if user is following actions
        for (g in userGroups) {
            if (g.groupKey == group.groupKey) {
                //if so, set text of follow/un-follow button to its opposite
                group_detail_follow_group_button.setText(R.string.following_groups_text)
                isFollowing = true
            }
        }
        //set image view
        Picasso.with(context)
                .load(group.groupImageUrl)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .placeholder(R.drawable.voices_icon)
                .error(R.drawable.voices_icon)
                .fit()
                .into(group_detail_group_image)

        //sets the progress dialog to use while follow call is made
        pd = ProgressDialog(context)
        pd.isIndeterminate = true
        pd.setCancelable(false)

        group_detail_group_type_tv.text = group.groupCategory
        group_detail_description_tv.text = group.groupDescription
    }

    //Callback for follow/un-follow
    private fun callBackFunction(data: Boolean, subscribing: Boolean): Boolean {
        //if callback succeeded
        if (data) {
            //remove progress dialog
            pd.dismiss()
            //To track the event type
            val eventName: String = if (subscribing) "SUBSCRIBE_EVENT" else "UNSUBSCRIBE_EVENT"
            AnalyticsManager.INSTANCE.trackEvent(eventName, group.groupKey,
                    SessionManager.INSTANCE.currentUserToken, "none", null)
            //Takes the string based on whether the user is following or not and sets text accordingly
            group_detail_follow_group_button.setText(if (subscribing) R.string.following_groups_text else R.string.follow_group)
            //sets whether or not the user is following to the new status
            isFollowing = subscribing
        }
        return data
    }

    private fun buildUnFollowActionSheet() {
        //action sheet to un-follow, to make sure someone doesn't do it accidentally
        com.baoyz.actionsheet.ActionSheet.createBuilder(context, (context as VoicesMainActivity).supportFragmentManager)
                .setCancelButtonTitle(R.string.cancel)
                .setCancelableOnTouchOutside(true)
                .setListener(this)
                .setOtherButtonTitles("Unfollow")
                .show()
    }

    //If the (non cancel) button is clicked
    override fun onOtherButtonClick(actionSheet: ActionSheet?, index: Int) {
        unFollow()
    }

    override fun onDismiss(actionSheet: ActionSheet?, isCancel: Boolean) {}


}