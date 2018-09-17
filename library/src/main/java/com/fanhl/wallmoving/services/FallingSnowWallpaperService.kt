package com.fanhl.wallmoving.services

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.preference.PreferenceManager
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import com.fanhl.wallmoving.model.MyPoint
import java.util.*


class FallingSnowWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine {
        return MyWallpaperEngine()
    }

//    inner class FallingSnowWallpaperEngine : WallpaperService.Engine() {
//
//        override fun onSurfaceCreated(holder: SurfaceHolder?) {
//            super.onSurfaceCreated(holder)
//
//            val surfaceConfig = RenderScriptGL.SurfaceConfig()
//            mRenderScriptGL = RenderScriptGL(this@FallingSnowWallpaperService, surfaceConfig)
//
//            // use low for wallpapers
//            mRenderScriptGL.setPriority(RenderScript.Priority.LOW)
//        }
//    }

    private inner class MyWallpaperEngine : WallpaperService.Engine() {
        private val handler = Handler()
        private val drawRunner = Runnable { draw() }
        private val circles: MutableList<MyPoint>
        private val paint = Paint()
        private var width: Int = 0
        internal var height: Int = 0
        private var visible = true
        private val maxNumber: Int
        private val touchEnabled: Boolean

        init {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this@FallingSnowWallpaperService)
            maxNumber = Integer.valueOf(prefs.getString("numberOfCircles", "4")!!)
            touchEnabled = prefs.getBoolean("touch", false)
            circles = ArrayList()
            paint.isAntiAlias = true
            paint.color = Color.WHITE
            paint.style = Paint.Style.STROKE
            paint.strokeJoin = Paint.Join.ROUND
            paint.strokeWidth = 10f
            handler.post(drawRunner)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible
            if (visible) {
                handler.post(drawRunner)
            } else {
                handler.removeCallbacks(drawRunner)
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            this.visible = false
            handler.removeCallbacks(drawRunner)
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder, format: Int,
            width: Int, height: Int
        ) {
            this.width = width
            this.height = height
            super.onSurfaceChanged(holder, format, width, height)
        }

        override fun onTouchEvent(event: MotionEvent) {
            if (touchEnabled) {

                val x = event.x
                val y = event.y
                val holder = surfaceHolder
                var canvas: Canvas? = null
                try {
                    canvas = holder.lockCanvas()
                    if (canvas != null) {
                        canvas.drawColor(Color.BLACK)
                        circles.clear()
                        circles.add(MyPoint((circles.size + 1).toString(), x, y))
                        drawCircles(canvas, circles)

                    }
                } finally {
                    if (canvas != null)
                        holder.unlockCanvasAndPost(canvas)
                }
                super.onTouchEvent(event)
            }
        }

        private fun draw() {
            val holder = surfaceHolder
            var canvas: Canvas? = null
            try {
                canvas = holder.lockCanvas()
                if (canvas != null) {
                    if (circles.size >= maxNumber) {
                        circles.clear()
                    }
                    val x = (width * Math.random()).toInt()
                    val y = (height * Math.random()).toInt()
                    circles.add(
                        MyPoint(
                            (circles.size + 1).toString(),
                            x.toFloat(),
                            y.toFloat()
                        )
                    )
                    drawCircles(canvas, circles)
                }
            } finally {
                if (canvas != null)
                    holder.unlockCanvasAndPost(canvas)
            }
            handler.removeCallbacks(drawRunner)
            if (visible) {
                handler.postDelayed(drawRunner, 5000)
            }
        }

        // Surface view requires that all elements are drawn completely
        private fun drawCircles(canvas: Canvas, circles: List<MyPoint>) {
            canvas.drawColor(Color.BLACK)
            for (point in circles) {
                canvas.drawCircle(point.x, point.y, 20.0f, paint)
            }
        }
    }
}
