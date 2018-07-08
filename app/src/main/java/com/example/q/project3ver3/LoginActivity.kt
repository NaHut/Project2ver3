package com.example.q.project3ver3

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity);

        var inputUserId = findViewById(R.id.userId) as EditText
        var inputPassword = findViewById(R.id.password) as EditText
        var btnSignIn = findViewById(R.id.signIn) as Button
        var btnSignUp = findViewById(R.id.signUp) as Button

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
        return true;
    }
}