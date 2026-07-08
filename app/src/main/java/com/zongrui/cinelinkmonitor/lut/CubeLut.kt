package com.zongrui.cinelinkmonitor.lut

data class LutRgb(
    val red: Float,
    val green: Float,
    val blue: Float,
)

data class CubeLut(
    val title: String,
    val size: Int,
    val entries: List<LutRgb>,
)

class CubeLutParseException(message: String) : IllegalArgumentException(message)

