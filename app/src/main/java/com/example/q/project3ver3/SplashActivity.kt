package com.example.q.project3ver3

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
//            Thread.sleep(2000)
            Thread.sleep(2)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        val myIntent = Intent(this,LoginActivity::class.java)
        startActivity(myIntent)
        finish()
    }
}