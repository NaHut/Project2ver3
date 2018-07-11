package com.example.q.project3ver3

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.q.project3ver3.model.Contact
import com.example.q.project3ver3.utils.RecyclerTouchListener
import org.json.JSONArray
import org.json.JSONObject
import java.util.HashMap
import kotlin.collections.ArrayList

var contact_list = ArrayList<Contact>()
const val ADD_FRIEND = 1
const val DEL_FRIEND = 2
const val EDIT_FRIEND = 3
const val DEL_IMAGE = 4

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

        /*어뎁터와 연결하여 contact_item layout에 넣어줌 */
        val layout_manager : RecyclerView.LayoutManager = LinearLayoutManager(this.context)
        recyclerView.setLayoutManager(layout_manager)

        //연락처 추가 버튼
        val fab = rootView.findViewById<View>(R.id.fab_add_contact) as FloatingActionButton
        fab.setOnClickListener { view ->
            showAddDialog()
        }

        //item 클릭시 연락처 수정 삭제

        recyclerView.addOnItemTouchListener(RecyclerTouchListener(this.requireContext(),
                recyclerView, object : RecyclerTouchListener.ClickListener {

            override fun onClick(view: View, position: Int) {}

            override fun onLongClick(view: View?, position: Int) {
                showActionsDialog(position);
            }
        }))

        return rootView
    }

    fun loadData(userId: String) {
        contact_list.clear()

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
    fun showAddDialog() {
        val layoutInflater = LayoutInflater.from(this.requireContext())
        val view = layoutInflater.inflate(R.layout.add_contact_dialog,null)

        val alertDialogBuilderUserInput = AlertDialog.Builder(this.requireContext())
        alertDialogBuilderUserInput.setView(view)

        val input_friend_name = view.findViewById<EditText>(R.id.add_friend_name)
        val input_friend_phoneN = view.findViewById<EditText>(R.id.add_friend_phoneN)

        alertDialogBuilderUserInput.setPositiveButton("Register"){
            dialog, which->
            val result = addFriend(input_friend_name.text.toString(),input_friend_phoneN.text.toString())
            if(result == ADD_FRIEND){
                Toast.makeText(this.requireContext(), "Register OK", Toast.LENGTH_SHORT).show()
                dialog.cancel()
            }
            else {
                Toast.makeText(this.requireContext(), "ID is existed", Toast.LENGTH_SHORT).show()
            }

        }

        alertDialogBuilderUserInput.setNeutralButton("Cancel"){_,_->
            Toast.makeText(this.requireContext(),"Canceled", Toast.LENGTH_SHORT).show()
        }

        val dialog: AlertDialog = alertDialogBuilderUserInput.create()

        dialog.show()
    }
    fun addFriend(input_friend_name: String, input_friend_phoneN: String) : Int {
        val data = HashMap<String,String>()

        data.put("userId", userId)
        data.put("name", input_friend_name)
        data.put("phoneNumber", input_friend_phoneN)
        data.put("tag", "add_contact")

        postDataForAdd(LoginActivity().url,data)

        return ADD_FRIEND
    }

    fun postDataForAdd(url: String, data: HashMap<String,String>) {
        val requstQueue = Volley.newRequestQueue(this.requireContext())
        var result : String

        val jsonobj = object : JsonObjectRequest(Request.Method.POST, url, JSONObject(data),
                Response.Listener { response ->
                    result = response.getString("result")

                    if(result == "add_complete"){
                        loadData(userId)
                    }

                },
                Response.ErrorListener { error ->
                    print("error")
                }
        ) {

            //here I want to post data to sever
        }
        requstQueue.add(jsonobj)
    }

    private fun showActionsDialog(position: Int) {
        val colors = arrayOf<CharSequence>("Edit", "Delete")
        var result : Int? = null
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle("Choose option")
        builder.setItems(colors) { dialog, which ->
            if (which == 0) {
                deleteContact(userId,position)
                showAddDialog()
                result = EDIT_FRIEND
            } else {
                result = deleteContact(userId, position)
            }
        }
        builder.show()

        if(result ==DEL_FRIEND){
            Toast.makeText(this.requireContext(),"Deleted ", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this.requireContext(),"Edit Completed ", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteContact(userId : String, position : Int) : Int?{
        val data = HashMap<String,String>()

        data.put("userId", userId)
        data.put("index",position.toString())
        data.put("tag", "delete_contact")

        postDataForDelete(LoginActivity().url,data)

        return DEL_FRIEND
    }
    fun postDataForDelete(url: String, data: HashMap<String,String>) {
        val requstQueue = Volley.newRequestQueue(this.requireContext())
        var result : String

        val jsonobj = object : JsonObjectRequest(Request.Method.POST, url, JSONObject(data),
                Response.Listener { response ->
                    result = response.getString("result")

                    if(result == "delete_complete"){
                        loadData(userId)
                    }

                },
                Response.ErrorListener { error ->
                    print("error")
                }
        ) {

            //here I want to post data to sever
        }
        requstQueue.add(jsonobj)
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

