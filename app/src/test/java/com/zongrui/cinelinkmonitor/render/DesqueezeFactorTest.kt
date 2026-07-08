package com.zongrui.cinelinkmonitor.render

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DesqueezeFactorTest {
    @Test
    fun providesCinemaDefaultForOnePointThreeThreeAnamorphicLens() {
        val factor = DesqueezeFactor.Cinema133

        assertEquals(1.33f, factor.value)
        assertEquals("1.33x", factor.label)
    }

    @Test
    fun customFactorIsClampedToMonitorSafeRange() {
        assertEquals(1.0f, DesqueezeFactor.custom(0.5f).value)
        assertEquals(2.0f, DesqueezeFactor.custom(2.5f).value)
        assertEquals(1.55f, DesqueezeFactor.custom(1.55f).value)
    }

    @Test
    fun displayScaleKeepsVerticalPixelsStable() {
        val scale = DesqueezeFactor.Cinema133.previewScale(
            sourceWidth = 1920,
            sourceHeight = 1080,
            containerWidth = 2560,
            containerHeight = 1440,
        )

        assertEquals(1.33f, scale.scaleX)
        assertEquals(1.0f, scale.scaleY)
    }

    @Test
    fun rejectsInvalidPreviewDimensions() {
        assertFailsWith<IllegalArgumentException> {
            DesqueezeFactor.Cinema133.previewScale(0, 1080, 1920, 1080)
        }
    }
}

