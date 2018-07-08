package com.example.q.project3ver3

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import com.android.volley.VolleyError
import com.android.volley.RequestQueue
import java.util.HashMap



class LoginActivity : AppCompatActivity() {

    val url = "http://143.248.36.215:8080"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity);

        var inputUserId = findViewById<EditText>(R.id.userId)
        var inputPassword = findViewById<EditText>(R.id.password)
        var btnSignIn = findViewById<Button>(R.id.signIn)
        var btnSignUp = findViewById<Button>(R.id.signUp)

        btnSignIn.setOnClickListener {
            var userId = inputUserId.text;
            var password = inputPassword.text;

            //check if he is memeber or not!!
            if(checkIsMember(userId.toString(), password.toString())){
                //He is Memeber
                val myIntent = Intent(this,MainActivity::class.java)
                startActivity(myIntent)
                finish()
            }
            else{
                //He is not member
            }
        }

        btnSignUp.setOnClickListener {

            //alert dialog
        }
    }

    fun checkIsMember (userId : String , password : String) : Boolean{
        val data = HashMap<String,String>()

        data.put("userId", userId)
        data.put("password", password)
        data.put("tag", "login")

        postData(url,data)
        return true
    }

    fun postData(url: String, data: HashMap<String,String>) {
        val requstQueue = Volley.newRequestQueue(this@LoginActivity)

        val jsonobj = object : JsonObjectRequest(Request.Method.POST, url, JSONObject(data),
                Response.Listener { response ->
                    print("success")
                },
                Response.ErrorListener { error ->
                    print("error")
                }
        ) {

            //here I want to post data to sever
        }
        requstQueue.add(jsonobj)
    }
}

