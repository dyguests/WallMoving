package com.fanhl.wallmoving.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 壁纸信息
 */
@Parcelize
data class WallpaperConfig(
    val path: String,
    val centralX: Float,
    val centralY: Float,
    val scale: Float,
    val offsetX: Float,
    val offsetY: Float
) : Parcelable