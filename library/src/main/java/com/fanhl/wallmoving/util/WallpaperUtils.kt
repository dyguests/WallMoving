package com.fanhl.wallmoving.util

import android.graphics.BitmapFactory
import com.fanhl.wallmoving.model.Coord
import com.fanhl.wallmoving.model.Wallpaper
import com.fanhl.wallmoving.model.WallpaperConfig
import java.io.File
import java.io.FileInputStream

object WallpaperUtils {
    fun loadWallpaperAsync(config: WallpaperConfig, screenSize: Coord, onWallpaperGot: (Wallpaper?) -> Unit) {
        val file = File(config.path)
        if (!file.exists()) {
            onWallpaperGot(null)
            return
        }
        val fileInputStream = FileInputStream(file)

        val (sourceWidth, sourceHeight) = getBitmapSize(fileInputStream)
        val (screenWidth, screenHeight) = screenSize

        // 计算
        val centralPx = sourceWidth * config.centralX
        val centralPy = sourceHeight * config.centralY


    }

    private fun getBitmapSize(fileInputStream: FileInputStream): Pair<Int, Int> {
        val sizeOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(fileInputStream, null, sizeOptions)
        return Pair(sizeOptions.outWidth, sizeOptions.outHeight)
    }

}
