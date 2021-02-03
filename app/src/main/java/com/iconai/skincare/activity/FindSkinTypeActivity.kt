package com.iconai.skincare.activity

import android.content.Intent
import android.os.Bundle
import android.view.View.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.iconai.skincare.R

class FindSkinTypeActivity : AppCompatActivity() {
    private var tvFindQuestion: TextView? = null
    private var btnFindYes: Button? = null
    private var btnFindIdontknow: Button? = null
    private var btnFindNo: Button? = null

    private var strQuestion: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_findskintype)

        init()
    }

    private fun init() {
        strQuestion = arrayListOf(
            getString(R.string.page_findskin_question0),
            getString(R.string.page_findskin_question1),
            getString(R.string.page_findskin_question2),
            getString(R.string.page_findskin_question3),
            getString(R.string.page_findskin_question4),
            getString(R.string.page_findskin_question5),
            getString(R.string.page_findskin_question6),
            getString(R.string.page_findskin_question7),
            getString(R.string.page_findskin_question8),
            getString(R.string.page_findskin_question9),
            getString(R.string.page_findskin_question10),
            getString(R.string.page_findskin_question11),
            getString(R.string.page_findskin_question12),
            getString(R.string.page_findskin_question13),
            getString(R.string.page_findskin_question14),
            getString(R.string.page_findskin_question15),
            getString(R.string.page_findskin_question16),
            getString(R.string.page_findskin_question17),
            getString(R.string.page_findskin_question18),
            getString(R.string.page_findskin_question19),
            getString(R.string.page_findskin_question20),
            getString(R.string.page_findskin_question21),
            getString(R.string.page_findskin_question22),
            getString(R.string.page_findskin_question23),
            getString(R.string.page_findskin_question24)
        )

        tvFindQuestion = findViewById(R.id.tv_find_question)
        btnFindYes = findViewById(R.id.btn_find_yes)
        btnFindIdontknow = findViewById(R.id.btn_find_idontknow)
        btnFindNo = findViewById(R.id.btn_find_no)

        tvFindQuestion?.text = strQuestion[0]

        btnFindYes?.setOnClickListener {
            btnFindIdontknow?.visibility = VISIBLE
            when (tvFindQuestion?.text) {
                strQuestion[0] -> tvFindQuestion?.text = strQuestion[5]
                strQuestion[1] -> tvFindQuestion?.text = strQuestion[5]
                strQuestion[2] -> tvFindQuestion?.text = strQuestion[7]
                strQuestion[3] -> tvFindQuestion?.text = strQuestion[6]
                strQuestion[4] -> tvFindQuestion?.text = strQuestion[9]
                strQuestion[5] -> tvFindQuestion?.text = strQuestion[10]
                strQuestion[6] -> tvFindQuestion?.text = strQuestion[11]
                strQuestion[7] -> tvFindQuestion?.text = strQuestion[12]
                strQuestion[8] -> tvFindQuestion?.text = strQuestion[13]
                strQuestion[9] -> tvFindQuestion?.text = strQuestion[14]
                strQuestion[10] -> tvFindQuestion?.text = strQuestion[15]
                strQuestion[11] -> tvFindQuestion?.text = strQuestion[16]
                strQuestion[12] -> tvFindQuestion?.text = strQuestion[17]
                strQuestion[13] -> {
                    tvFindQuestion?.text = strQuestion[18]
                    btnFindIdontknow?.visibility = GONE
                }
                strQuestion[14] -> {
                    tvFindQuestion?.text = strQuestion[19]
                    btnFindIdontknow?.visibility = GONE
                }
                strQuestion[15] -> tvFindQuestion?.text = strQuestion[20]
                strQuestion[16] -> tvFindQuestion?.text = strQuestion[21]
                strQuestion[17] -> tvFindQuestion?.text = strQuestion[22]
                strQuestion[18] -> tvFindQuestion?.text = strQuestion[23]
                strQuestion[19] -> tvFindQuestion?.text = strQuestion[24]
                strQuestion[20] -> finishIntent(0)
                strQuestion[21] -> finishIntent(2)
                strQuestion[22] -> finishIntent(1)
                strQuestion[23] -> finishIntent(4)
                strQuestion[24] -> finishIntent(3)
            }
        }

        btnFindNo?.setOnClickListener {
            btnFindIdontknow?.visibility = VISIBLE
            when (tvFindQuestion?.text) {
                strQuestion[0] -> tvFindQuestion?.text = strQuestion[1]
                strQuestion[1] -> tvFindQuestion?.text = strQuestion[2]
                strQuestion[2] -> tvFindQuestion?.text = strQuestion[3]
                strQuestion[3] -> tvFindQuestion?.text = strQuestion[4]
                strQuestion[4] -> tvFindQuestion?.text = strQuestion[5]
                strQuestion[5] -> tvFindQuestion?.text = strQuestion[6]
                strQuestion[6] -> tvFindQuestion?.text = strQuestion[7]
                strQuestion[7] -> tvFindQuestion?.text = strQuestion[8]
                strQuestion[8] -> tvFindQuestion?.text = strQuestion[9]
                strQuestion[9] -> tvFindQuestion?.text = strQuestion[10]
                strQuestion[10] -> tvFindQuestion?.text = strQuestion[11]
                strQuestion[11] -> tvFindQuestion?.text = strQuestion[12]
                strQuestion[12] -> tvFindQuestion?.text = strQuestion[13]
                strQuestion[13] -> tvFindQuestion?.text = strQuestion[14]
                strQuestion[14] -> tvFindQuestion?.text = strQuestion[16]
                strQuestion[15] -> tvFindQuestion?.text = strQuestion[16]
                strQuestion[16] -> tvFindQuestion?.text = strQuestion[17]
                strQuestion[17] -> {
                    tvFindQuestion?.text = strQuestion[18]
                    btnFindIdontknow?.visibility = GONE
                }
                strQuestion[18] -> {
                    tvFindQuestion?.text = strQuestion[19]
                    btnFindIdontknow?.visibility = GONE
                }
                strQuestion[19] -> tvFindQuestion?.text = strQuestion[10]
                strQuestion[20] -> tvFindQuestion?.text = strQuestion[21]
                strQuestion[21] -> tvFindQuestion?.text = strQuestion[22]
                strQuestion[22] -> tvFindQuestion?.text = strQuestion[23]
                strQuestion[23] -> tvFindQuestion?.text = strQuestion[24]
                strQuestion[24] -> tvFindQuestion?.text = strQuestion[17]
            }
        }

        btnFindIdontknow?.setOnClickListener {
            btnFindIdontknow?.visibility = VISIBLE
            when (tvFindQuestion?.text) {
                strQuestion[0] -> tvFindQuestion?.text = strQuestion[6]
                strQuestion[1] -> tvFindQuestion?.text = strQuestion[7]
                strQuestion[2] -> tvFindQuestion?.text = strQuestion[8]
                strQuestion[3] -> tvFindQuestion?.text = strQuestion[5]
                strQuestion[4] -> tvFindQuestion?.text = strQuestion[8]
                strQuestion[5] -> tvFindQuestion?.text = strQuestion[11]
                strQuestion[6] -> tvFindQuestion?.text = strQuestion[12]
                strQuestion[7] -> tvFindQuestion?.text = strQuestion[13]
                strQuestion[8] -> tvFindQuestion?.text = strQuestion[14]
                strQuestion[9] -> tvFindQuestion?.text = strQuestion[10]
                strQuestion[10] -> tvFindQuestion?.text = strQuestion[17]
                strQuestion[11] -> tvFindQuestion?.text = strQuestion[15]
                strQuestion[12] -> tvFindQuestion?.text = strQuestion[16]
                strQuestion[13] -> tvFindQuestion?.text = strQuestion[17]
                strQuestion[14] -> {
                    tvFindQuestion?.text = strQuestion[18]
                    btnFindIdontknow?.visibility = GONE
                }
                strQuestion[15] -> tvFindQuestion?.text = strQuestion[21]
                strQuestion[16] -> tvFindQuestion?.text = strQuestion[23]
                strQuestion[17] -> tvFindQuestion?.text = strQuestion[23]
                strQuestion[20] -> finishIntent(2)
                strQuestion[21] -> finishIntent(0)
                strQuestion[22] -> finishIntent(4)
                strQuestion[23] -> finishIntent(1)
                strQuestion[24] -> finishIntent(4)
            }
        }
    }

    private fun finishIntent(selectSkinTypeInt: Int) {
        val intent: Intent = intent
        intent.putExtra("selectSkinTypeInt", selectSkinTypeInt)
        setResult(1, intent)
        finish()
    }
}