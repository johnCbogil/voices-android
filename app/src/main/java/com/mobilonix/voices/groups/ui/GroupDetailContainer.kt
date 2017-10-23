package com.mobilonix.voices.groups.ui


import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ListAdapter
import com.mobilonix.voices.groups.model.Action
import com.mobilonix.voices.groups.model.Group
import kotlinx.android.synthetic.main.group_detail.view.*


/**
 * Created by pc on 10/19/2017.
 */
class GroupDetailContainer(context: Context, attributes: AttributeSet) : FrameLayout(context, attributes) {


    var group:Group? = null
    val actions:ArrayList<Action> = ArrayList()

    constructor(context: Context,attributes: AttributeSet,group:Group) : this(context,attributes) {
        this.group = group
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        actions_rv.layoutManager = LinearLayoutManager(context)
        for(action in group!!.actions!!){

        }

    }
}