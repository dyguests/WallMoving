package com.fanhl.wallmoving.model

import android.os.Parcel
import android.os.Parcelable

/**
 * 壁纸信息
 *
 * @param path 壁纸文件的路径
 * @param centralX 中心点x 取值[0,1]
 * @param centralY 中心点y 取值[0,1]
 * @param scale 缩放比率（CENTER_CROP 时为1）
 * @param offsetX 偏移最大值x 取值[0,1] (为0时在默认位置，为1/-1时在水平方向允许偏移在最大位置（左右允许偏移的最大像素是一样的）)
 * @param offsetY 偏移最大值x 取值[0,1] (同上)
 */
data class WallpaperConfig(
    val path: String,
    val centralX: Float,
    val centralY: Float,
    val scale: Float,
    val offsetX: Float,
    val offsetY: Float
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readFloat(),
        source.readFloat(),
        source.readFloat(),
        source.readFloat(),
        source.readFloat()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(path)
        writeFloat(centralX)
        writeFloat(centralY)
        writeFloat(scale)
        writeFloat(offsetX)
        writeFloat(offsetY)
    }

    companion object {
        const val SP_KEY = "WallpaperConfig"

        @JvmField
        val CREATOR: Parcelable.Creator<WallpaperConfig> = object : Parcelable.Creator<WallpaperConfig> {
            override fun createFromParcel(source: Parcel): WallpaperConfig = WallpaperConfig(source)
            override fun newArray(size: Int): Array<WallpaperConfig?> = arrayOfNulls(size)
        }
    }
}