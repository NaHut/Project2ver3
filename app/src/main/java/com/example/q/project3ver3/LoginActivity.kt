package com.example.q.project3ver3

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.util.*

const val LOGIN = 0
const val WRONG_PW = 1
const val NOT_MEMBER = 2
const val ALREADY_MEMBER = 3
const val SIGN_UP = 4

var serverRes = NOT_MEMBER
var userID : String = ""

class LoginActivity : AppCompatActivity() {

    val url = "http://143.248.36.215:8080"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity);

        var inputUserId = findViewById<EditText>(R.id.userId)
        var inputPassword = findViewById<EditText>(R.id.password)
        var btnSignIn = findViewById<Button>(R.id.signIn)
        var btnSignUp = findViewById<Button>(R.id.signUp)

        //Sign In 버튼
        btnSignIn.setOnClickListener {
            var userId = inputUserId.text;
            var password = inputPassword.text;
//            var result = SignIn(userId.toString(), password.toString())
            SignIn(userId.toString(), password.toString())

            userID = userId.toString()

        }

        //Sign Up 버튼
        btnSignUp.setOnClickListener {
            showSignDialog()
        }


    }

    fun SignIn (userId : String , password : String){
        val data = HashMap<String,String>()

        data.put("userId", userId)
        data.put("password", password)
        data.put("tag", "login")

        postDataForSign(url,data)

    }

    fun postDataForSign(url: String, data: HashMap<String,String>) {
        val requstQueue = Volley.newRequestQueue(this@LoginActivity)
        var result : String

        val jsonobj = object : JsonObjectRequest(Request.Method.POST, url, JSONObject(data),
                Response.Listener { response ->
                    result = response.getString("result")
                    changeServerRes(result)
                    resultStatus(serverRes)
                   },
                Response.ErrorListener { error ->
                    print("error")
                }
        ) {

            //here I want to post data to sever
        }
        requstQueue.add(jsonobj)
    }
    fun showSignDialog() {
        val layoutInflater = LayoutInflater.from(this@LoginActivity)
        val view = layoutInflater.inflate(R.layout.sign_dialog,null)

        val alertDialogBuilderUserInput = AlertDialog.Builder(this@LoginActivity)
        alertDialogBuilderUserInput.setView(view)

        val inputId = view.findViewById<EditText>(R.id.signId)
        val inputPW = view.findViewById<EditText>(R.id.signPW)

        alertDialogBuilderUserInput.setPositiveButton("Register"){
            dialog, which->
            val result = signUp(inputId.text.toString(),inputPW.text.toString())
//            if(result == SIGN_UP){
//                Toast.makeText(applicationContext, "Register OK", Toast.LENGTH_SHORT).show()
//            }
//            else {
//                Toast.makeText(applicationContext, "ID is existed", Toast.LENGTH_SHORT).show()
//            }

        }

        alertDialogBuilderUserInput.setNeutralButton("Cancel"){_,_->
            Toast.makeText(applicationContext,"Canceled", Toast.LENGTH_SHORT).show()
        }

        val dialog: AlertDialog = alertDialogBuilderUserInput.create()

        dialog.show()
    }

    fun signUp(userId: String, password: String) : Int {
        val data = HashMap<String,String>()

        data.put("userId", userId)
        data.put("password", password)
        data.put("tag", "signUp")

        postDataForSign(url,data)

        return serverRes
    }

    fun changeServerRes(result : String){
        when(result){
            "LOGIN" -> serverRes = LOGIN
            "WRONG_PW" -> serverRes = WRONG_PW
            "NOT_MEMBER" -> serverRes = NOT_MEMBER
            "ALREADY_MEMBER" -> serverRes = ALREADY_MEMBER
            "SIGN_UP" -> serverRes = SIGN_UP
        }
    }

    fun myGetUserId() : String{
        return userID
    }

    fun resultStatus(result : Int){
        if(result == LOGIN){

            val myIntent = Intent(this,MainActivity::class.java)
            myIntent.putExtra("userId", userID)
            startActivity(myIntent)
            finish()
        }
        else if(result == WRONG_PW){
            Toast.makeText(this,"ERROR: Wrong PW", Toast.LENGTH_LONG).show()
        }
        else if(result == NOT_MEMBER){
            Toast.makeText(this,"ERROR: Please Register", Toast.LENGTH_LONG).show()
        }

        else if (result == SIGN_UP){
            Toast.makeText(applicationContext, "Register OK", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(applicationContext, "ID is existed", Toast.LENGTH_SHORT).show()
        }



    }
}


