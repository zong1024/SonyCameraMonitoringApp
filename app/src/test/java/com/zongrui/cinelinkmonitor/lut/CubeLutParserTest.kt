package com.zongrui.cinelinkmonitor.lut

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CubeLutParserTest {
    @Test
    fun parsesTitleSizeAndRgbTriples() {
        val cube = """
            # generated sample
            TITLE "Warm Lift"
            LUT_3D_SIZE 2
            DOMAIN_MIN 0.0 0.0 0.0
            DOMAIN_MAX 1.0 1.0 1.0
            0.0000 0.0000 0.0000
            1.0000 0.0000 0.0000
            0.0000 1.0000 0.0000
            1.0000 1.0000 0.0000
            0.0000 0.0000 1.0000
            1.0000 0.0000 1.0000
            0.0000 1.0000 1.0000
            1.0000 1.0000 1.0000
        """.trimIndent()

        val lut = CubeLutParser.parse(cube.byteInputStream())

        assertEquals("Warm Lift", lut.title)
        assertEquals(2, lut.size)
        assertEquals(8, lut.entries.size)
        assertEquals(LutRgb(1f, 1f, 1f), lut.entries.last())
    }

    @Test
    fun rejectsWrongEntryCount() {
        val cube = """
            TITLE "Broken"
            LUT_3D_SIZE 2
            0 0 0
        """.trimIndent()

        val error = assertFailsWith<CubeLutParseException> {
            CubeLutParser.parse(cube.byteInputStream())
        }

        assertEquals("Expected 8 LUT entries but found 1.", error.message)
    }

    @Test
    fun rejectsOutOfRangeRgbValues() {
        val cube = """
            LUT_3D_SIZE 1
            0.0 1.1 0.0
        """.trimIndent()

        val error = assertFailsWith<CubeLutParseException> {
            CubeLutParser.parse(cube.byteInputStream())
        }

        assertEquals("RGB values must be in the 0.0..1.0 range.", error.message)
    }
}

