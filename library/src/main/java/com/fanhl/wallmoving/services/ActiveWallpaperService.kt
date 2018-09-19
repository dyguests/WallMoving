package com.fanhl.wallmoving.services

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.preference.PreferenceManager
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.SurfaceHolder
import com.fanhl.wallmoving.model.Vector3
import com.fanhl.wallmoving.model.WallpaperConfig
import com.fanhl.wallmoving.util.ParcelableUtil

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

        /** 壁纸配置信息 */
        private var wallpaperConfig: WallpaperConfig? = null

        private val paint = Paint()

        private var width: Int = 0
        private var height: Int = 0

        private var visible = true

        private var rotation = Vector3()

        init {
            paint.color = Color.RED

            // 当 SP_KEY 数据变更时重新刷新数据
            PreferenceManager.getDefaultSharedPreferences(this@ActiveWallpaperService).apply {
                wallpaperConfig = readWallpaperConfig()

                registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
                    when (key) {
                        WallpaperConfig.SP_KEY -> {
                            wallpaperConfig = readWallpaperConfig()
                        }
                        else -> {
                        }
                    }
                }
            }
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
            Log.i(TAG, "draw: wallpaperConfig:${wallpaperConfig?.path}")

            canvas.drawColor(Color.BLACK)
            canvas.drawCircle(width / 2f + rotation.x * 100f, height / 2f + rotation.y * 100f, 100f, paint)
        }

        /**
         * 读取壁纸配置信息
         */
        private fun SharedPreferences.readWallpaperConfig(): WallpaperConfig? {
            return try {
                val source = getString(WallpaperConfig.SP_KEY, "")
                val parcel = ParcelableUtil.unmarshall(source.toByteArray())
                WallpaperConfig.CREATOR.createFromParcel(parcel)
            } catch (e: Exception) {
                null
            }
        }
    }
}
