package com.fanhl.wallmoving.sample.ui.main

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fanhl.wallmoving.model.WallpaperConfig
import com.fanhl.wallmoving.sample.R
import com.fanhl.wallmoving.util.ParcelableUtil

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initData()
    }

    private fun initData() {
        val wallpaperConfig = WallpaperConfig(
            "",
            0f,
            0f,
            0f,
            0f,
            0f
        )

        val preferences = getPreferences(Context.MODE_PRIVATE)
        preferences.edit().apply {
            putString(
                WallpaperConfig.SP_KEY,
                ParcelableUtil.marshall(wallpaperConfig).toString()
            )
            apply()
        }
    }
}
