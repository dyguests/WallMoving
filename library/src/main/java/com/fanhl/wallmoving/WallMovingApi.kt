package com.fanhl.wallmoving

import android.content.Context
import android.preference.PreferenceManager
import com.fanhl.wallmoving.model.WallpaperConfig
import com.google.gson.Gson

object WallMovingApi {
    /**
     * 设置壁纸
     */
    fun setWallPaper(context: Context, wallpaperConfig: WallpaperConfig) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
            putString(
                WallpaperConfig.SP_KEY,
                Gson().toJson(wallpaperConfig)
            )
            apply()
        }
    }
}