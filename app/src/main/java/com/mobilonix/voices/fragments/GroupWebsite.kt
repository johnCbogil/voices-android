package com.mobilonix.voices.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobilonix.voices.R
import kotlinx.android.synthetic.main.group_website.*

/**
 * Created by Ari on 10/30/2017.
 */
class GroupWebsite : Fragment() {

    lateinit var back:View


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.group_website,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Sets website to group website
        website_web_view.loadUrl(arguments!!.getString("Website"))
        view.setOnClickListener{ fragmentManager!!.popBackStackImmediate()}
    }





}