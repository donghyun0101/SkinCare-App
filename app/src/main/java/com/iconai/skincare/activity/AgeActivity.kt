package com.iconai.skincare.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.iconai.skincare.R
import com.iconai.skincare.util.ApiData

class AgeActivity : AppCompatActivity() {
    private var imgBtnAgeBack: ImageButton? = null
    private var btnAge: ArrayList<Button> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_age)

        init()
    }

    fun init() {
        imgBtnAgeBack = findViewById(R.id.imgbtn_age_back)
        btnAge = arrayListOf(
            findViewById(R.id.btn_age_1824),
            findViewById(R.id.btn_age_2534),
            findViewById(R.id.btn_age_3544),
            findViewById(R.id.btn_age_4554),
            findViewById(R.id.btn_age_55)
        )

        imgBtnAgeBack?.setOnClickListener {
            ApiData.gender = null
            startActivity(Intent(this, GenderActivity::class.java))
            finish()
        }
        for (i in 0 until btnAge.size)
            btnAge[i].setOnClickListener {
                it.isSelected = true
                ApiData.age = when (i) {
                    0 -> getString(R.string.page_age_18_24)
                    1 -> getString(R.string.page_age_25_34)
                    2 -> getString(R.string.page_age_35_44)
                    3 -> getString(R.string.page_age_45_54)
                    else -> getString(R.string.page_age_55)
                }
                startActivity(
                    Intent(
                        this,
                        DataCheckActivity::class.java
                    )
                )
                finish()
            }
    }
}