package com.zongrui.cinelinkmonitor.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LandscapeMonitorLayoutTest {
    @Test
    fun defaultLayoutKeepsControlsInRightLandscapeRail() {
        val layout = LandscapeMonitorLayout.Default

        assertEquals(184, layout.controlRailWidthDp)
        assertEquals(layout.controlRailWidthDp, layout.previewRightInsetDp)
        assertTrue(layout.overlayRightInsetDp > layout.controlRailWidthDp)
        assertTrue(layout.minLandscapeAspect > 1f)
    }
}
