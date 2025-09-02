package com.curioskyestudios.lumonote

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.curioskyestudios.lumonote.ui.home.HomeViewActivity
import com.curioskyestudios.lumonote.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Start the home screen activity
        var intent = Intent(this, HomeViewActivity::class.java)
        startActivity(intent)
    }

}