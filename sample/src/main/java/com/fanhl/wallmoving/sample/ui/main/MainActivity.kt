package com.fanhl.wallmoving.sample.ui.main

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fanhl.wallmoving.sample.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initData()
    }

    private fun initData() {
        val preferences = getPreferences(Context.MODE_PRIVATE)
        preferences.edit().apply {
//            putString()
            apply()
        }
    }
}
