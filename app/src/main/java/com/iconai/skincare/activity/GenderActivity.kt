package com.iconai.skincare.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.iconai.skincare.R
import com.iconai.skincare.util.ApiData

class GenderActivity : AppCompatActivity() {

    private var btnGender: ArrayList<Button> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gender)

        init()
    }

    fun init() {
        btnGender = arrayListOf(
            findViewById(R.id.btn_gender_woman),
            findViewById(R.id.btn_gender_man),
            findViewById(R.id.btn_gender_nonbinary),
            findViewById(R.id.btn_gender_prefernottosay)
        )

        for (i in 0 until btnGender.size)
            btnGender[i].setOnClickListener {
                it.isSelected = true
                ApiData.gender =
                    when (i) {
                        0 -> "female"
                        1 -> "male"
                        else -> btnGender[i].text.toString()
                    }
                startActivity(
                    Intent(
                        this,
                        AgeActivity::class.java
                    )
                )
                finish()
            }
    }
}