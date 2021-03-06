package com.mobilonix.voices.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobilonix.voices.R
import com.mobilonix.voices.groups.model.Policy
import com.mobilonix.voices.representatives.RepresentativesManager
import kotlinx.android.synthetic.main.policy_detail_page.*
import android.support.v7.app.AppCompatActivity



/**
 * Created by pc on 10/26/2017.
 */
class PolicyDetailPage:Fragment(){

    lateinit var policy:Policy


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.policy_detail_page,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        policies_title.text = policy.policyName
        policies_description.text = policy.policyDescription
        //Brings you  to contact rep page
        button_contact_representatives.setOnClickListener{RepresentativesManager.INSTANCE.selectRepresentativesTab()}
        //Removes this page
        back_from_policy_detail.setOnClickListener{ fragmentManager!!.popBackStackImmediate() }
    }

    override fun onResume() {
        super.onResume()
        //gets rid of action bar
        (activity as AppCompatActivity).supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        //when view is stopped, shows action bar again
        (activity as AppCompatActivity).supportActionBar!!.show()
    }

}