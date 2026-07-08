package com.zongrui.cinelinkmonitor.render

import java.util.Locale
import kotlin.math.roundToInt

data class PreviewScale(
    val scaleX: Float,
    val scaleY: Float,
)

data class DesqueezeFactor private constructor(
    val value: Float,
) {
    val label: String = if (value == 1f) {
        "1.00x"
    } else {
        String.format(Locale.US, "%.2fx", value)
    }

    fun previewScale(
        sourceWidth: Int,
        sourceHeight: Int,
        containerWidth: Int,
        containerHeight: Int,
    ): PreviewScale {
        require(sourceWidth > 0 && sourceHeight > 0) { "Source dimensions must be positive." }
        require(containerWidth > 0 && containerHeight > 0) { "Container dimensions must be positive." }
        return PreviewScale(scaleX = value, scaleY = 1f)
    }

    companion object {
        val Normal = DesqueezeFactor(1f)
        val Cinema133 = DesqueezeFactor(1.33f)

        fun custom(value: Float): DesqueezeFactor {
            val rounded = (value.coerceIn(1.0f, 2.0f) * 100f).roundToInt() / 100f
            return when (rounded) {
                1f -> Normal
                1.33f -> Cinema133
                else -> DesqueezeFactor(rounded)
            }
        }
    }
}

