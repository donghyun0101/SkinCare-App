package com.iconai.skincare.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.iconai.skincare.R
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private var btnStart: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    fun init() {
        btnStart = findViewById(R.id.btn_start)

        btnStart?.setOnClickListener {
            it.isSelected = true
            startActivity(Intent(this, SkinTypeActivity::class.java))
            finish()
        }
    }
}