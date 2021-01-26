package com.iconai.skincare.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.iconai.skincare.R
import com.iconai.skincare.util.ApiData

class DataCheckActivity : AppCompatActivity() {
    private var tvDataCheckSkinType: TextView? = null
    private var tvDataCheckSkinConcerns: TextView? = null
    private var tvDataCheckGender: TextView? = null
    private var tvDataCheckAge: TextView? = null
    private var btnDataCheckOk: Button? = null
    private var btnDataCheckNo: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datacheck)

        init()
    }

    @SuppressLint("SetTextI18n")
    fun init() {
        tvDataCheckSkinType = findViewById(R.id.tv_datacheck_skintype)
        tvDataCheckSkinConcerns = findViewById(R.id.tv_datacheck_skinconcerns)
        tvDataCheckGender = findViewById(R.id.tv_datacheck_gender)
        tvDataCheckAge = findViewById(R.id.tv_datacheck_age)

        btnDataCheckOk = findViewById(R.id.btn_datacheck_ok)
        btnDataCheckNo = findViewById(R.id.btn_datacheck_no)

        tvDataCheckSkinType?.text = ApiData.skinType.toString()
        tvDataCheckSkinConcerns?.text =
            when (ApiData.skinConcerns.size) {
                0 -> "Noting"
                else -> ApiData.skinConcerns.toString().replace("[", "").replace("]", "")
            }
        tvDataCheckGender?.text =
            when (ApiData.gender.toString()) {
                "female" -> "Woman"
                "male" -> "Man"
                else -> ApiData.gender.toString()
            }
        tvDataCheckAge?.text = ApiData.age.toString()

        btnDataCheckOk?.setOnClickListener {
            it.isSelected = true

            startActivity(Intent(this, CameraActivity::class.java))
            finish()
        }

        btnDataCheckNo?.setOnClickListener {
            it.isSelected = true

            ApiData.skinType = null
            ApiData.skinConcerns.clear()
            ApiData.gender = null
            ApiData.age = null
            ApiData.imgFaceBitmap = null
            ApiData.imgFaceFile = null
            ApiData.faceData = null

            startActivity(Intent(this@DataCheckActivity, MainActivity::class.java))
            finish()
        }
    }
}