package com.fanhl.wallmoving.sample.ui.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fanhl.wallmoving.WallMovingApi
import com.fanhl.wallmoving.model.WallpaperConfig
import com.fanhl.wallmoving.sample.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initData()
    }

    private fun initData() {
        val wallpaperConfig = WallpaperConfig(
            "/storage/emulated/0/com.fanhl.kona/photos/269936.jpg"/* TODO add your image path here. */,
            0.5f,
            0.5f,
            0.8f,
            0.5f,
            0.5f
        )

        WallMovingApi.setWallPaper(this, wallpaperConfig)
    }
}
