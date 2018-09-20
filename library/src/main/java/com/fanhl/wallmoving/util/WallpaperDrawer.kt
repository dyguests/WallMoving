package com.fanhl.wallmoving.util

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import com.fanhl.wallmoving.model.Coord
import com.fanhl.wallmoving.model.Vector3
import com.fanhl.wallmoving.model.Wallpaper

/**
 * 绘制帮助类
 */
class WallpaperDrawer {
    private val paint = Paint()

    var wallpaper: Wallpaper? = null
        set(value) {
            if (field == value) {
                return
            }

            field = value

            updateDefaultRect()
        }

    // 这里存放bitmap要显示的区域
    val srcRect = Rect()

    // 这里存放canvas用来显示的区域
    val dstRect = Rect()

    // 这里是为了防止重复产生实例
    private val bitmapSize = Coord()
    // bitmap的中心点
    private val bitmapCentral = Coord()
    // 要显示的区域的半径（width为中心到左右的距离，height是中心到上下的距离）
    private val visibleRadius = Coord()
    // 默认显示区域
    private val defaultRect = Rect()
    // 默认显示区域到总区域的边距
    private val visibleMargin = Coord()

    fun draw(canvas: Canvas, rotation: Vector3) {
        srcRect.apply {
            left = defaultRect.left + (visibleMargin.x * rotation.x).toInt()
            top = defaultRect.top + (visibleMargin.y * rotation.y).toInt()
            right = defaultRect.right + (visibleMargin.x * rotation.x).toInt()
            bottom = defaultRect.bottom + (visibleMargin.y * rotation.y).toInt()
        }
        canvas.drawBitmap(wallpaper?.bitmap ?: return, srcRect, dstRect, paint)
    }


    fun notifyDstRectChanged() {
        updateDefaultRect()
    }

    /**
     * 更新默认要显示的区域
     */
    private fun updateDefaultRect() {
        if (wallpaper == null) {
            return
        }

        bitmapSize.apply {
            x = wallpaper?.bitmap?.width ?: return
            y = wallpaper?.bitmap?.height ?: return
        }

        if (bitmapSize.x <= 0 || bitmapSize.y <= 0) {
            return
        }

        bitmapCentral.apply {
            x = bitmapSize.x / 2
            y = bitmapSize.y / 2
        }

        val scale = wallpaper?.scale ?: return

        if (dstRect.right <= 0 || dstRect.bottom <= 0) {
            return
        }

        // 屏幕的斜率
        val dstSlope = dstRect.height().toFloat() / dstRect.width()
        // 原图片可使用的边距的斜率
        val srcSlope = bitmapSize.x.toFloat() / bitmapSize.y

        // 生成显示区域的半径
        if (dstSlope > srcSlope) {
            val radiusHeight = bitmapSize.y * scale / 2
            val radiusWidth = radiusHeight / dstSlope
            visibleRadius.apply {
                x = radiusWidth.toInt()
                y = radiusHeight.toInt()
            }
            visibleMargin.apply {
                x = (bitmapCentral.x - radiusWidth).toInt()
                y = (bitmapCentral.y - radiusHeight).toInt()
            }
        } else {
            val radiusWidth = bitmapSize.x * scale / 2
            val radiusHeight = radiusWidth * dstSlope
            visibleRadius.apply {
                x = radiusWidth.toInt()
                y = radiusHeight.toInt()
            }
            visibleMargin.apply {
                x = (bitmapCentral.x - radiusWidth).toInt()
                y = (bitmapCentral.y - radiusHeight).toInt()
            }
        }

        //默认显示区域
        defaultRect.apply {
            left = bitmapCentral.x - visibleRadius.x
            top = bitmapCentral.y - visibleRadius.y
            right = bitmapCentral.x + visibleRadius.x
            bottom = bitmapCentral.y + visibleRadius.y
        }
    }

}