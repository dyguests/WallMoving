package com.fanhl.wallmoving.util

import android.graphics.*
import com.fanhl.wallmoving.model.Coord
import com.fanhl.wallmoving.model.Vector2
import com.fanhl.wallmoving.model.Wallpaper
import com.fanhl.wallmoving.model.WallpaperConfig
import java.io.File
import java.io.FileInputStream

object WallpaperUtils {
    fun loadWallpaperAsync(config: WallpaperConfig, screenSize: Coord, onWallpaperGot: (Wallpaper?) -> Unit) {

    }

    fun loadWallpaper(config: WallpaperConfig, screenSize: Coord, onWallpaperGot: (Wallpaper?) -> Unit) {
        val file = File(config.path)
        if (!file.exists()) {
            onWallpaperGot(null)
            return
        }

        val fileInputStream = FileInputStream(file)

        val (sourceWidth, sourceHeight) = getBitmapSize(fileInputStream)
        val (screenWidth, screenHeight) = screenSize

        if (sourceWidth <= 0 || sourceHeight <= 0
                || screenWidth <= 0 || screenHeight <= 0) {
            onWallpaperGot(null)
            return
        }

        // 计算

        // 焦点的坐标点
        val centralPx = sourceWidth * config.centralX
        val centralPy = sourceHeight * config.centralY

        // 原图片焦点的边距（只算最小边距）
        val sourceMarginWidth = minOf(centralPx, sourceWidth - centralPx)
        val sourceMarginHeight = minOf(centralPy, sourceHeight - centralPy)

        // 屏幕的斜率
        val screenSlope = screenHeight.toFloat() / screenWidth

        // 原图片可使用的边距的斜率
        val sourceMarginSlope = sourceMarginHeight / sourceMarginWidth

        // 这里生成符合屏幕斜率的边距(焦点到边的距离)
        val (smmWidth, smmHeight) = if (screenSlope > sourceMarginSlope) {
            Vector2(sourceMarginHeight / screenSlope, sourceMarginHeight)
        } else {
            Vector2(sourceMarginWidth, sourceMarginWidth * screenSlope)
        }

        // 获取scale后默认在屏幕上居中显示的区域
        val centralScaledRect = RectF(
                centralPx - smmWidth * config.scale,
                centralPy - smmHeight * config.scale,
                centralPx + smmWidth * config.scale,
                centralPy + smmHeight * config.scale
        )

        // 获取默认显示的区域的边距（只算最小边距）
        val centralScaledMarginWidth = minOf(centralScaledRect.left, sourceWidth - centralScaledRect.right)
        val centralScaledMarginHeight = minOf(centralScaledRect.top, sourceHeight - centralScaledRect.bottom)

        // 获取允许偏移的px值
        val offsetPx = centralScaledMarginWidth * config.offsetX
        val offsetPy = centralScaledMarginHeight * config.offsetY

        // 生成总共需要用来的图片区域 (Float)（默认区域+偏移区域）
        val visibleRectF = RectF(
                centralScaledRect.left - offsetPx,
                centralScaledRect.top - offsetPy,
                centralScaledRect.right + offsetPx,
                centralScaledRect.bottom + offsetPy
        )

        // 生成总共需要用来的图片区域 (Int 这里用Int是为了之后从文件中读取对应区域)（默认区域+偏移区域）
        // 此处用 minOf、maxOf 是为了防止浮点运算时越界
        val visibleRect = Rect(
                maxOf(0, visibleRectF.left.toInt()),
                maxOf(0, visibleRectF.top.toInt()),
                minOf(sourceWidth, visibleRectF.right.toInt()),
                minOf(sourceHeight, visibleRectF.bottom.toInt())
        )

        // 生成默认显示区域相对比总显示区域的缩放比率（只算最小的）
        val scaleVisible = minOf(
                centralScaledRect.width() / visibleRectF.width(),
                centralScaledRect.height() / visibleRectF.height()
        )

        // 加载要用到的图片区域
        val bitmap = loadBitmap(fileInputStream, visibleRect)

        if (bitmap == null) {
            onWallpaperGot(null)
            return
        }

        // 生成Wallpaper信息
        val wallpaper = Wallpaper(
                bitmap,
                scaleVisible
        )

        onWallpaperGot(wallpaper)
    }

    /**
     * 加载图片文件的文件流fileInputStream中对应区域rect的Bitmap
     */
    private fun loadBitmap(fileInputStream: FileInputStream, rect: Rect): Bitmap? {
        val bitmapRegionDecoder = BitmapRegionDecoder.newInstance(fileInputStream, false)

        val bitmap = bitmapRegionDecoder.decodeRegion(rect, BitmapFactory.Options())

        bitmapRegionDecoder.recycle()
        return bitmap
    }

    private fun getBitmapSize(fileInputStream: FileInputStream): Pair<Int, Int> {
        val sizeOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(fileInputStream, null, sizeOptions)
        return Pair(sizeOptions.outWidth, sizeOptions.outHeight)
    }

}
