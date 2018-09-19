package com.fanhl.wallmoving.model

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
) {
    companion object {
        const val SP_KEY = "WallpaperConfig"
    }
}