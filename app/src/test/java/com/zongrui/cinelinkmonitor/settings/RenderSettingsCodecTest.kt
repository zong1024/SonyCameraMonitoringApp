package com.zongrui.cinelinkmonitor.settings

import com.zongrui.cinelinkmonitor.render.DesqueezeFactor
import kotlin.test.Test
import kotlin.test.assertEquals

class RenderSettingsCodecTest {
    @Test
    fun encodesAndDecodesRenderSettings() {
        val settings = RenderSettings(
            monitorMode = MonitorMode.Photo,
            desqueezeFactor = DesqueezeFactor.custom(1.42f),
            lutEnabled = true,
            lutIntensity = 0.65f,
            lutName = "Kodak 2383",
        )

        val restored = RenderSettingsCodec.decode(RenderSettingsCodec.encode(settings))

        assertEquals(settings, restored)
    }

    @Test
    fun decodeFallsBackToDefaultsForCorruptInput() {
        val restored = RenderSettingsCodec.decode("not-json")

        assertEquals(RenderSettings.default(), restored)
    }

    @Test
    fun lutIntensityIsClamped() {
        val settings = RenderSettings(lutIntensity = 9f)

        assertEquals(1f, settings.lutIntensity)
    }
}

