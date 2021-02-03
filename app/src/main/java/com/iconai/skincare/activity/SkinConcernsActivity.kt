package com.iconai.skincare.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.iconai.skincare.R
import com.iconai.skincare.util.ApiData

class SkinConcernsActivity : AppCompatActivity() {
    private var imgBtnSkinConcernsBack: ImageButton? = null
    private var btnSkinConcerns: ArrayList<Button> = arrayListOf()
    private var stringSkinConcernsList: ArrayList<String> = arrayListOf(
        "Wrinkles and find lines",
        "Eyebags",
        "Redness",
        "Dull skin",
        "Aging",
        "Dark Circles",
        "Visible Pores",
        "Sagging skin",
        "Hyperpigmentation",
        "Acne"
    )
    private var btnSkinConcernsNext: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_skinconcerns)

        init()
    }

    fun init() {
        imgBtnSkinConcernsBack = findViewById(R.id.imgbtn_skinconcerns_back)
        btnSkinConcerns = arrayListOf(
            findViewById(R.id.btn_skinconcerns_findlines),
            findViewById(R.id.btn_skinconcerns_eyebags),
            findViewById(R.id.btn_skinconcerns_redness),
            findViewById(R.id.btn_skinconcerns_dullskin),
            findViewById(R.id.btn_skinconcerns_aging),
            findViewById(R.id.btn_skinconcerns_darkcircles),
            findViewById(R.id.btn_skinconcerns_visiblepores),
            findViewById(R.id.btn_skinconcerns_saggingskin),
            findViewById(R.id.btn_skinconcerns_hyperpigmentation),
            findViewById(R.id.btn_skinconcerns_acne)
        )

        btnSkinConcernsNext = findViewById(R.id.btn_skinconcerns_next)

        imgBtnSkinConcernsBack?.setOnClickListener {
            ApiData.skinType = null
            startActivity(Intent(this, SkinTypeActivity::class.java))
            finish()
        }

        for (i in 0 until btnSkinConcerns.size)
            btnSkinConcerns[i].setOnClickListener {
                btnSkinConcerns[i].isSelected = !btnSkinConcerns[i].isSelected
            }

        btnSkinConcernsNext?.setOnClickListener {
            it.isSelected = true
            for (i in 0 until btnSkinConcerns.size)
                if (btnSkinConcerns[i].isSelected)
                    ApiData.skinConcerns.add(stringSkinConcernsList[i])

            startActivity(
                Intent(
                    this,
                    GenderActivity::class.java
                )
            )
            finish()
        }
    }
}