package com.zongrui.cinelinkmonitor.ui

data class LandscapeMonitorLayout(
    val controlRailWidthDp: Int,
    val previewRightInsetDp: Int,
    val overlayRightInsetDp: Int,
    val minLandscapeAspect: Float,
) {
    init {
        require(controlRailWidthDp > 0)
        require(previewRightInsetDp >= controlRailWidthDp)
        require(overlayRightInsetDp >= controlRailWidthDp)
        require(minLandscapeAspect > 1f)
    }

    companion object {
        val Default = LandscapeMonitorLayout(
            controlRailWidthDp = 184,
            previewRightInsetDp = 184,
            overlayRightInsetDp = 200,
            minLandscapeAspect = 1.2f,
        )
    }
}
