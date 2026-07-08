package com.zongrui.cinelinkmonitor.settings

import android.content.Context

class RenderSettingsStore(context: Context) {
    private val preferences = context.getSharedPreferences("render_settings", Context.MODE_PRIVATE)

    fun load(): RenderSettings =
        RenderSettingsCodec.decode(preferences.getString(KEY_SETTINGS, null).orEmpty())

    fun save(settings: RenderSettings) {
        preferences.edit()
            .putString(KEY_SETTINGS, RenderSettingsCodec.encode(settings))
            .apply()
    }

    companion object {
        private const val KEY_SETTINGS = "settings"
    }
}

