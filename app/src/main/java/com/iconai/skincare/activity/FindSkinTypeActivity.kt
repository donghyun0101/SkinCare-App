package com.iconai.skincare.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.iconai.skincare.R
import com.iconai.skincare.fragment.*

class FindSkinTypeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_findskintype)

        init()
    }

    fun init() {
        val fragment: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragment.beginTransaction()
        fragmentTransaction.replace(R.id.frame_find_result, FindSkinTypeFragment())
        fragmentTransaction.commit()
    }
}