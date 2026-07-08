package com.zongrui.cinelinkmonitor.render

import android.content.Context
import android.opengl.GLES20
import com.jiangdg.ausbc.render.effect.AbstractEffect
import com.jiangdg.ausbc.render.effect.bean.CameraEffect
import com.zongrui.cinelinkmonitor.R
import com.zongrui.cinelinkmonitor.lut.CubeLut
import kotlin.math.abs
import kotlin.math.max

class LutPreviewEffect(
    context: Context,
    lut: CubeLut,
    private var intensity: Float,
) : AbstractEffect(context) {
    private val lift = lut.previewLift()

    override fun getId(): Int = ID

    override fun getClassifyId(): Int = CameraEffect.CLASSIFY_ID_FILTER

    override fun getVertexSourceId(): Int = com.jiangdg.ausbc.R.raw.base_vertex

    override fun getFragmentSourceId(): Int = R.raw.lut_preview_fragment

    override fun beforeDraw() {
        super.beforeDraw()
        val program = mProgram
        val liftHandle = GLES20.glGetUniformLocation(program, "uLutLift")
        val intensityHandle = GLES20.glGetUniformLocation(program, "uIntensity")
        GLES20.glUniform3f(liftHandle, lift[0], lift[1], lift[2])
        GLES20.glUniform1f(intensityHandle, intensity.coerceIn(0f, 1f))
    }

    fun updateIntensity(value: Float) {
        intensity = value.coerceIn(0f, 1f)
    }

    private fun CubeLut.previewLift(): FloatArray {
        if (entries.isEmpty()) return floatArrayOf(0f, 0f, 0f)
        val avgRed = entries.sumOf { it.red.toDouble() }.toFloat() / entries.size
        val avgGreen = entries.sumOf { it.green.toDouble() }.toFloat() / entries.size
        val avgBlue = entries.sumOf { it.blue.toDouble() }.toFloat() / entries.size
        val lift = floatArrayOf(
            (avgRed - 0.5f) * 0.35f,
            (avgGreen - 0.5f) * 0.35f,
            (avgBlue - 0.5f) * 0.35f,
        )
        val maxAbs = max(abs(lift[0]), max(abs(lift[1]), abs(lift[2])))
        return if (maxAbs < 0.005f) floatArrayOf(0.02f, 0.01f, -0.01f) else lift
    }

    companion object {
        const val ID = 9101
    }
}
