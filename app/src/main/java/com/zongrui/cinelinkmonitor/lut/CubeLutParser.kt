package com.zongrui.cinelinkmonitor.lut

import java.io.InputStream

object CubeLutParser {
    private const val MAX_SIZE = 65

    fun parse(input: InputStream): CubeLut {
        val lines = input.bufferedReader().use { it.readLines() }
        var title = "Imported LUT"
        var size: Int? = null
        val entries = mutableListOf<LutRgb>()

        for (rawLine in lines) {
            val line = rawLine.substringBefore("#").trim()
            if (line.isEmpty()) continue

            when {
                line.startsWith("TITLE", ignoreCase = true) -> {
                    title = line.removePrefixWord("TITLE").trim().trim('"').ifBlank { title }
                }
                line.startsWith("LUT_3D_SIZE", ignoreCase = true) -> {
                    val value = line.removePrefixWord("LUT_3D_SIZE").trim().toIntOrNull()
                        ?: throw CubeLutParseException("LUT_3D_SIZE must be an integer.")
                    if (value !in 1..MAX_SIZE) {
                        throw CubeLutParseException("LUT_3D_SIZE must be in the 1..$MAX_SIZE range.")
                    }
                    size = value
                }
                line.startsWith("DOMAIN_MIN", ignoreCase = true) ||
                    line.startsWith("DOMAIN_MAX", ignoreCase = true) -> Unit
                else -> entries += parseEntry(line)
            }
        }

        val resolvedSize = size ?: throw CubeLutParseException("Missing LUT_3D_SIZE.")
        val expectedEntries = resolvedSize * resolvedSize * resolvedSize
        if (entries.size != expectedEntries) {
            throw CubeLutParseException("Expected $expectedEntries LUT entries but found ${entries.size}.")
        }

        return CubeLut(title = title, size = resolvedSize, entries = entries)
    }

    private fun parseEntry(line: String): LutRgb {
        val parts = line.split(Regex("\\s+"))
        if (parts.size != 3) {
            throw CubeLutParseException("LUT entries must contain exactly three RGB values.")
        }
        val values = parts.map {
            it.toFloatOrNull() ?: throw CubeLutParseException("RGB values must be numeric.")
        }
        if (values.any { it !in 0.0f..1.0f }) {
            throw CubeLutParseException("RGB values must be in the 0.0..1.0 range.")
        }
        return LutRgb(values[0], values[1], values[2])
    }

    private fun String.removePrefixWord(prefix: String): String =
        replace(Regex("^$prefix\\s*", RegexOption.IGNORE_CASE), "")
}

