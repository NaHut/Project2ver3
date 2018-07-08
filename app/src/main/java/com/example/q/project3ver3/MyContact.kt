package com.example.q.project3ver3

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class MyContact : Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView =  inflater.inflate(R.layout.contact, container, false)
        val textView = rootView.findViewById<TextView>(R.id.textView)
        // Instantiate the RequestQueue.
//        val queue = Volley.newRequestQueue(activity)
//        val url = "http://www.google.com"
//
//        // Request a string response from the provided URL.
//        val stringRequest = StringRequest(Request.Method.GET, url,
//                Response.Listener<String> { response ->
//                    // Display the first 500 characters of the response string.
//                    textView.text = "Response is: ${response.substring(0, 500)}"
//                },
//                Response.ErrorListener { textView.text = "That didn't work!" })
//
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest)

        return rootView
    }
}