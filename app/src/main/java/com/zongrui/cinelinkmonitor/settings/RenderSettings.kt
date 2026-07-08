package com.zongrui.cinelinkmonitor.settings

import com.zongrui.cinelinkmonitor.render.DesqueezeFactor

class RenderSettings(
    val monitorMode: MonitorMode = MonitorMode.Video,
    val desqueezeFactor: DesqueezeFactor = DesqueezeFactor.Cinema133,
    val lutEnabled: Boolean = false,
    lutIntensity: Float = 1f,
    val lutName: String? = null,
) {
    val lutIntensity: Float = lutIntensity.coerceIn(0f, 1f)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RenderSettings) return false
        return monitorMode == other.monitorMode &&
            desqueezeFactor == other.desqueezeFactor &&
            lutEnabled == other.lutEnabled &&
            lutIntensity == other.lutIntensity &&
            lutName == other.lutName
    }

    override fun hashCode(): Int {
        var result = monitorMode.hashCode()
        result = 31 * result + desqueezeFactor.hashCode()
        result = 31 * result + lutEnabled.hashCode()
        result = 31 * result + lutIntensity.hashCode()
        result = 31 * result + (lutName?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String =
        "RenderSettings(monitorMode=$monitorMode, desqueezeFactor=$desqueezeFactor, " +
            "lutEnabled=$lutEnabled, lutIntensity=$lutIntensity, lutName=$lutName)"

    companion object {
        fun default(): RenderSettings = RenderSettings()
    }
}

