package com.fanhl.wallmoving.services

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.fanhl.wallmoving.model.Vector3

/**
 * 动态壁纸服务
 */
class ActiveWallpaperService : WallpaperService() {
    private val sensorManager by lazy { getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private val gravitySensor by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }

    override fun onCreateEngine(): Engine {
        return ActiveWallpaper()
    }

    companion object {
        private val TAG = ActiveWallpaperService::class.java.simpleName
    }

    private inner class ActiveWallpaper : WallpaperService.Engine() {
        private val handler = Handler()
        private val runner = Runnable { draw() }

        private val gravitySensorEventListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }

            override fun onSensorChanged(event: SensorEvent?) {
                event ?: return
                rotation.apply {
                    //这里数值的最大值好像是10
                    x = event.values[0] / 10f
                    y = event.values[1] / 10f
                    z = event.values[2] / 10f
                }
                handler.post(runner)
            }
        }

        private val paint = Paint()

        private var width: Int = 0
        private var height: Int = 0

        private var visible = true

        private var rotation = Vector3()

        init {
            paint.color = Color.RED
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            this.visible = visible
            if (visible) {
                handler.post(runner)
                sensorManager.registerListener(gravitySensorEventListener, gravitySensor, SensorManager.SENSOR_DELAY_FASTEST)
            } else {
                handler.removeCallbacks(runner)
                sensorManager.unregisterListener(gravitySensorEventListener)
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
            canvas.drawColor(Color.BLACK)
            canvas.drawCircle(width / 2f + rotation.x * 100f, height / 2f + rotation.y * 100f, 100f, paint)
        }
    }
}
