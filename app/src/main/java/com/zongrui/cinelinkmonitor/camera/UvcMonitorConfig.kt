package com.zongrui.cinelinkmonitor.camera

object UvcMonitorConfig {
    const val DEFAULT_PREVIEW_WIDTH = 1280
    const val DEFAULT_PREVIEW_HEIGHT = 720

    private val bundledNativeUvcAbis = setOf("arm64-v8a", "armeabi-v7a")

    fun hasBundledNativeUvc(supportedAbis: Iterable<String>): Boolean =
        supportedAbis.firstOrNull() in bundledNativeUvcAbis
}
