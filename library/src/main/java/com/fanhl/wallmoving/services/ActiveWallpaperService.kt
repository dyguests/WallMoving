package com.fanhl.wallmoving.services

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.hardware.SensorManager
import android.os.Handler
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder

/**
 * 动态壁纸服务
 */
class ActiveWallpaperService : WallpaperService() {
    private val sensorManager by lazy { getSystemService(Context.SENSOR_SERVICE) as SensorManager }

    override fun onCreateEngine(): Engine {
        return ActiveWallpaper()
    }

    private inner class ActiveWallpaper : WallpaperService.Engine() {
        private val handler = Handler()
        private val runner = Runnable { draw() }

        private val paint = Paint()

        private var width: Int = 0
        private var height: Int = 0

        private var visible = true

        init {
            paint.color = Color.RED
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            this.visible = visible
            if (visible) {
                handler.post(runner)
            } else {
                handler.removeCallbacks(runner)
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            this.width = width
            this.height = height
        }

        private fun draw() {
            var canvas: Canvas? = null
            try {
                canvas = surfaceHolder.lockCanvas()
                if (canvas != null) {
                    draw(canvas)
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas)
                }
            }
        }

        private fun draw(canvas: Canvas) {
            canvas.drawCircle(width / 2f, height / 2f, 100f, paint)
        }
    }
}
