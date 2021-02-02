package com.iconai.skincare.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.iconai.skincare.R
import com.iconai.skincare.util.ApiData

class SkinTypeActivity : AppCompatActivity() {
    private var imgBtnSkinTypeBack: ImageButton? = null
    private var btnSkinType: ArrayList<Button> = arrayListOf()
    private var selectSkinTypeStr: ArrayList<String> = arrayListOf()
    private var snackBar: Snackbar? = null
    private var snackBarLayoutParams: RelativeLayout.LayoutParams? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_skintype)

        init()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1)
            if (requestCode == 1) {
                snackBar = Snackbar.make(window.decorView.rootView, "Your Skin Type is ${selectSkinTypeStr[data!!.getIntExtra("selectSkinTypeInt", 0)]}.\nPlease select ${selectSkinTypeStr[data!!.getIntExtra("selectSkinTypeInt", 0)]}", Snackbar.LENGTH_INDEFINITE)
                snackBarLayoutParams = RelativeLayout.LayoutParams(snackBar!!.view.layoutParams)
                snackBarLayoutParams?.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                snackBar!!.view.setPadding(0, 0, 0, 0)
                snackBar!!.view.setBackgroundColor(ContextCompat.getColor(this@SkinTypeActivity, R.color.alert_blue))
                snackBar!!.view.layoutParams = snackBarLayoutParams
                snackBar!!.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                snackBar!!.setAction("OK") {
                    snackBar!!.dismiss()
                }
                snackBar!!.setActionTextColor(getColor(R.color.white))
                snackBar!!.show()
            }
    }

    fun init() {
        imgBtnSkinTypeBack = findViewById(R.id.imgbtn_skintype_back)
        btnSkinType = arrayListOf(
            findViewById(R.id.btn_skintype_normal),
            findViewById(R.id.btn_skintype_dry),
            findViewById(R.id.btn_skintype_oily),
            findViewById(R.id.btn_skintype_sensitive),
            findViewById(R.id.btn_skintype_combination),
            findViewById(R.id.btn_skintype_null)
        )

        selectSkinTypeStr = arrayListOf(
            getString(R.string.page_skintype_dry),
            getString(R.string.page_skintype_oily),
            getString(R.string.page_skintype_normal),
            getString(R.string.page_skintype_sensitive),
            getString(R.string.page_skintype_combination)
        )

        imgBtnSkinTypeBack?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        for (i in 0 until btnSkinType.size) {
            btnSkinType[i].setOnClickListener {
                if (snackBar != null)
                    snackBar!!.dismiss()
                when (btnSkinType[i]) {
                    btnSkinType[5] -> {
                        startActivityForResult(
                            Intent(
                                this,
                                FindSkinTypeActivity::class.java
                            ), 1
                        )
                    }
                    else -> {
                        it.isSelected = true
                        ApiData.skinType = btnSkinType[i].text.toString()
                        startActivity(
                            Intent(
                                this,
                                SkinConcernsActivity::class.java
                            )
                        )
                        finish()
                    }
                }
            }
        }
    }
}