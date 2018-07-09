package com.example.q.project3ver3

import android.content.Intent.getIntent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.q.project3ver3.MainActivity
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap



class MyContact : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.contact, container, false)
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView)

        val userId = LoginActivity().myGetUserId()
        //DB와 연결하고 DB의 값을 가져옴
        loadData(userId)
        //DB의 모든 친구 정보를 contactList에 넣어줌

        return rootView
    }

    fun loadData(userId: String) {
        val data = HashMap<String, String>()

        data.put("userId", userId)
        data.put("tag", "load")

        postDataForLoad(LoginActivity().url, data)
    }

    fun postDataForLoad(url: String, data: HashMap<String, String>) {
        val requstQueue = Volley.newRequestQueue(this.context)
        var result : String
        var jsonObj = JSONObject(data)


        val jsonobj = object : JsonObjectRequest(Request.Method.POST, url, JSONObject(data),
                Response.Listener { response ->
                    result = response.toString()
                    var friendList = response.getJSONArray("friendList")

                    print("abc")
                },
                Response.ErrorListener { error ->
                    error.toString()
                    print("error")
                }
        ) {

            //here I want to post data to sever
        }
        requstQueue.add(jsonobj)
    }

}

