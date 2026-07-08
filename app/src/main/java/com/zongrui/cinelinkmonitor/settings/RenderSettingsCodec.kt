package com.zongrui.cinelinkmonitor.settings

import com.zongrui.cinelinkmonitor.render.DesqueezeFactor
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object RenderSettingsCodec {
    fun encode(settings: RenderSettings): String {
        val pairs = listOf(
            "mode" to settings.monitorMode.name,
            "factor" to settings.desqueezeFactor.value.toString(),
            "lutEnabled" to settings.lutEnabled.toString(),
            "intensity" to settings.lutIntensity.toString(),
            "lutName" to settings.lutName.orEmpty(),
        )
        return pairs.joinToString("&") { (key, value) ->
            "${urlEncode(key)}=${urlEncode(value)}"
        }
    }

    fun decode(value: String): RenderSettings {
        return runCatching {
            val pairs = value.split("&").associate {
                val keyValue = it.split("=", limit = 2)
                if (keyValue.size != 2) error("Invalid settings entry.")
                urlDecode(keyValue[0]) to urlDecode(keyValue[1])
            }
            RenderSettings(
                monitorMode = MonitorMode.valueOf(pairs.getValue("mode")),
                desqueezeFactor = DesqueezeFactor.custom(pairs.getValue("factor").toFloat()),
                lutEnabled = pairs.getValue("lutEnabled").toBooleanStrict(),
                lutIntensity = pairs.getValue("intensity").toFloat(),
                lutName = pairs["lutName"]?.ifBlank { null },
            )
        }.getOrElse { RenderSettings.default() }
    }

    private fun urlEncode(value: String): String =
        URLEncoder.encode(value, StandardCharsets.UTF_8.name())

    private fun urlDecode(value: String): String =
        URLDecoder.decode(value, StandardCharsets.UTF_8.name())
}

