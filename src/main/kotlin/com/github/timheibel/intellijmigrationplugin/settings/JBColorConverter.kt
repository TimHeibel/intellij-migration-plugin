package com.github.timheibel.intellijmigrationplugin.settings

import com.intellij.ui.JBColor
import com.intellij.util.xmlb.Converter
import org.jetbrains.annotations.NotNull

/**
 * Converter for serializing and deserializing a map of string-JBColor pairs.
 */
internal class JBColorConverter : Converter<MutableMap<String, JBColor>?>() {
    override fun fromString(@NotNull value: String): MutableMap<String, JBColor> {
        val entries = value.split(";")
        val map = mutableMapOf<String, JBColor>()

        for (entry in entries) {
            val parts = entry.split(":")
            val key = parts[0]
            val colors = parts[1].split(",")
            val regularRGB = colors[0].toInt()
            val darkRGB = colors[1].toInt()

            map[key] = JBColor(regularRGB, darkRGB)
        }

        return map
    }

    override fun toString(value: MutableMap<String, JBColor>): String {
        val entries = value.entries.joinToString(";") { (key, jbColor) ->
            "$key:${jbColor.rgb},${jbColor.darkVariant.rgb}"
        }

        return entries
    }
}
