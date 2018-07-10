package com.example.q.project3ver3

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.q.project3ver3.model.Contact
import org.json.JSONArray
import org.json.JSONObject
import java.util.HashMap
import kotlin.collections.ArrayList

var contact_list = ArrayList<Contact>()

class MyContact : Fragment() {

    val userId = LoginActivity().myGetUserId()
    lateinit var requstQueue : RequestQueue
    lateinit var recyclerView : RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.contact, container, false)
        recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView)
        contact_list.clear()
        /*DB와 연결하고 DB의 값을 가져옴
        DB의 모든 친구 정보를 contactList에 넣어줌 */
        loadData(userId)
        //리스터 필요

//        val tmp = Contact("전형준","010-4830-0139","path")
//        contact_list.add(tmp)
//        contact_list.add(tmp)

        /*어뎁터와 연결하여 contact_item layout에 넣어줌 */
        val layout_manager : RecyclerView.LayoutManager = LinearLayoutManager(this.context)
        recyclerView.setLayoutManager(layout_manager)
//        val contact_adapter = contactAdapter(contact_list,this.context)
//        recyclerView.setAdapter(contact_adapter)

        return rootView
    }

    fun loadData(userId: String) {
        val data = HashMap<String, String>()

        data.put("userId", userId)
        data.put("tag", "load")

        postDataForLoad(LoginActivity().url, data)
    }

    fun postDataForLoad(url: String, data: HashMap<String, String>) {
        requstQueue = Volley.newRequestQueue(this.context)
//        var future = RequestFuture.newFuture<JSONObject>()
        val jsonRequest = object : JsonObjectRequest(Request.Method.POST, url, JSONObject(data),
                Response.Listener { response ->
                    var friend_list = response.getJSONArray("friendList")

                    jsonArrayParsing(friend_list)

                    val contact_adapter = contactAdapter(contact_list,this.context)
                    recyclerView.setAdapter(contact_adapter)

                },
                Response.ErrorListener { error ->
                    error.toString()
                    print("error")
                }
        ) {

            //here I want to post data to sever
        }

        MySingleton.getInstance(this.requireContext()).addToRequestQueue(jsonRequest)

    }
    fun jsonArrayParsing(json_array : JSONArray){
        for(i in 0 until json_array.length() ){
            var json_obj = json_array.getJSONObject(i)
            var name  = json_obj.getString("name")
            var phone_number = json_obj.getString("phoneNumber")
            var profile = json_obj.getString("profile")

            var contact = Contact(name, phone_number, profile)
            contact_list.add(contact)

        }
    }

     class contactAdapter(val items : ArrayList<Contact>, val context : Context?) : RecyclerView.Adapter<ViewHolder>() {

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = ViewHolder(LayoutInflater.from(context).inflate(R.layout.contact_items, parent, false))
            return view
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.friend_name.setText(contact_list.get(position).name)
            holder.friend_phone.setText(contact_list.get(position).phone_number)
        }
    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val friend_name = view.findViewById<TextView>(R.id.friendName)
        val friend_phone = view.findViewById<TextView>(R.id.friendPhone)
    }


}

