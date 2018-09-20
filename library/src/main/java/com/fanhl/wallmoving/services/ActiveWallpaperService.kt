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
import android.view.SurfaceHolder
import com.fanhl.wallmoving.model.Coord
import com.fanhl.wallmoving.model.Vector3
import com.fanhl.wallmoving.model.Wallpaper
import com.fanhl.wallmoving.model.WallpaperConfig
import com.fanhl.wallmoving.util.WallpaperDrawer
import com.fanhl.wallmoving.util.WallpaperUtils
import com.google.gson.Gson
import org.jetbrains.anko.runOnUiThread

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

        private val gson by lazy { Gson() }

        /** 壁纸配置信息 */
        private var wallpaperConfig: WallpaperConfig? = null
            set(value) {
                if (field == value) {
                    return
                }

                field = value
                loadWallpaper(value ?: return)
            }

        private var wallpaper: Wallpaper? = null
            set(value) {
                if (field == value) {
                    return
                }

                field = value

                wallpaperDrawer.wallpaper = value
                if (value != null) {
                    handler.post(runner)
                }
            }

        private val wallpaperDrawer = WallpaperDrawer()

        private val paint = Paint().apply {
            color = Color.RED
        }

        // 屏幕尺寸
        private var screenSize = Coord()
            set(value) {
                if (field == value) {
                    return
                }

                field = value
                wallpaperDrawer.dstRect.apply {
                    right = value.x
                    bottom = value.y
                    wallpaperDrawer.notifyDstRectChanged()
                }
            }

        private var visible = true

        private var rotation = Vector3()

        init {
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
            screenSize = Coord(width, height)
            loadWallpaper(wallpaperConfig ?: return)
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

            wallpaperDrawer.draw(canvas, rotation)

            canvas.drawCircle(screenSize.x / 2f + rotation.x * 100f, screenSize.y / 2f + rotation.y * 100f, 100f, paint)
        }

        /**
         * 加载对应的图像
         */
        private fun loadWallpaper(config: WallpaperConfig) {
            WallpaperUtils.loadWallpaperAsync(config, screenSize) {
                runOnUiThread {
                    this@ActiveWallpaper.wallpaper = it
                }
            }
        }

        /**
         * 读取壁纸配置信息
         */
        private fun SharedPreferences.readWallpaperConfig(): WallpaperConfig? {
            return try {
                val source = getString(WallpaperConfig.SP_KEY, "")
                gson.fromJson(source, WallpaperConfig::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}

