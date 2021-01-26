package com.iconai.skincare.fragment

import android.content.Intent
import android.content.Intent.getIntent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.iconai.skincare.R
import java.util.*


class FindSkinTypeFragment : Fragment() {

    var tvFindIndex: TextView? = null
    var tvFindTitle: TextView? = null
    var btnFindList: ArrayList<Button> = arrayListOf()
    var textFindList: ArrayList<ArrayList<String>> = arrayListOf()
    var selectInt: ArrayList<Int> = arrayListOf(0, 0, 0, 0, 0)
    var c: Int = 0
    var k: Int = 2

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_findskintype, container, false)

        tvFindIndex = rootView.findViewById(R.id.tv_find_index)
        tvFindTitle = rootView.findViewById(R.id.tv_find_title)

        btnFindList = arrayListOf(
            rootView.findViewById(R.id.btn_find_one),
            rootView.findViewById(R.id.btn_find_two),
            rootView.findViewById(R.id.btn_find_three),
            rootView.findViewById(R.id.btn_find_four),
            rootView.findViewById(R.id.btn_find_five)
        )

        textFindList = arrayListOf(
            arrayListOf(
                getString(R.string.page_findskin_index1),
                getString(R.string.page_findskin_one),
                getString(R.string.page_findskin_one_select1),
                getString(R.string.page_findskin_one_select2),
                getString(R.string.page_findskin_one_select3),
                getString(R.string.page_findskin_one_select4),
                getString(R.string.page_findskin_one_select5)
            ),
            arrayListOf(
                getString(R.string.page_findskin_index2),
                getString(R.string.page_findskin_two),
                getString(R.string.page_findskin_two_select1),
                getString(R.string.page_findskin_two_select2),
                getString(R.string.page_findskin_two_select3),
                getString(R.string.page_findskin_two_select4),
                getString(R.string.page_findskin_two_select5)
            ),
            arrayListOf(
                getString(R.string.page_findskin_index3),
                getString(R.string.page_findskin_three),
                getString(R.string.page_findskin_three_select1),
                getString(R.string.page_findskin_three_select2),
                getString(R.string.page_findskin_three_select3),
                getString(R.string.page_findskin_three_select4),
                getString(R.string.page_findskin_three_select5)
            ),
            arrayListOf(
                getString(R.string.page_findskin_index4),
                getString(R.string.page_findskin_four),
                getString(R.string.page_findskin_four_select1),
                getString(R.string.page_findskin_four_select2),
                getString(R.string.page_findskin_four_select3),
                getString(R.string.page_findskin_four_select4),
                getString(R.string.page_findskin_four_select5)
            ),
            arrayListOf(
                getString(R.string.page_findskin_index5),
                getString(R.string.page_findskin_five),
                getString(R.string.page_findskin_five_select1),
                getString(R.string.page_findskin_five_select2),
                getString(R.string.page_findskin_five_select3),
                getString(R.string.page_findskin_five_select4),
                getString(R.string.page_findskin_five_select5)
            ),
        )

        rootView.apply {
            for (i in 0 until btnFindList.size) {
                btnFindList[i].setOnClickListener {
                    c++
                    selectInt[i] = selectInt[i] + 1
                    when (c) {
                        5 -> {
                            var max = 0
                            var d = 0
                            for (j in 0 until selectInt.size)
                                if (selectInt[max] < selectInt[j]) {
                                    max = j
                                    d = j
                                }

                            val intent: Intent = activity!!.intent
                            intent.putExtra("selectSkinTypeInt", d)
                            activity?.setResult(1, intent)
                            activity?.finish()
                        }
                        else -> {
                            tvFindIndex?.text = textFindList[c][0]
                            tvFindTitle?.text = textFindList[c][1]
                            for (j in 0 until btnFindList.size) {
                                btnFindList[j].text = textFindList[c][k]
                                k++
                            }
                            k = 2
                        }
                    }
                }
            }
        }
        return rootView
    }
}