package com.zongrui.cinelinkmonitor.camera

import kotlin.test.Test
import kotlin.test.assertEquals

class UvcMonitorConfigTest {
    @Test
    fun defaultPreviewRequestUsesUsbStreamingSafeAspectRatio() {
        assertEquals(1280, UvcMonitorConfig.DEFAULT_PREVIEW_WIDTH)
        assertEquals(720, UvcMonitorConfig.DEFAULT_PREVIEW_HEIGHT)
    }
}
