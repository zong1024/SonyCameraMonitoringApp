package com.zongrui.cinelinkmonitor.camera

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UvcMonitorConfigTest {
    @Test
    fun defaultPreviewRequestUsesUsbStreamingSafeAspectRatio() {
        assertEquals(1280, UvcMonitorConfig.DEFAULT_PREVIEW_WIDTH)
        assertEquals(720, UvcMonitorConfig.DEFAULT_PREVIEW_HEIGHT)
    }

    @Test
    fun bundledNativeUvcLibrariesSupportArmAndroidDevices() {
        assertTrue(UvcMonitorConfig.hasBundledNativeUvc(listOf("arm64-v8a")))
        assertTrue(UvcMonitorConfig.hasBundledNativeUvc(listOf("armeabi-v7a")))
    }

    @Test
    fun bundledNativeUvcLibrariesDoNotClaimX86EmulatorSupport() {
        assertFalse(UvcMonitorConfig.hasBundledNativeUvc(listOf("x86_64", "x86")))
        assertFalse(UvcMonitorConfig.hasBundledNativeUvc(listOf("x86_64", "arm64-v8a")))
    }
}
