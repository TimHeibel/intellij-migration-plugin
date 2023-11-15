package com.github.timheibel.intellijmigrationplugin.settings.converters

import com.intellij.util.xmlb.Converter
import org.jetbrains.annotations.NotNull

/**
 * Converter for serializing and deserializing a list of Pair objects.
 */
internal class PairListConverter : Converter<MutableList<Pair<String, String>>?>() {
    override fun fromString(@NotNull value: String): MutableList<Pair<String, String>> {
        val entries = value.split(";")
        val list = mutableListOf<Pair<String, String>>()

        for (entry in entries) {
            val parts = entry.split(":")
            val firstValue = parts[0]
            val secondValue = parts[1]

            list.add(Pair(firstValue, secondValue))
        }
        return list
    }

    override fun toString(value: MutableList<Pair<String, String>>): String {
        val entries = value.joinToString(";") { pair ->
            "${pair.first}:${pair.second}"
        }
        return entries
    }
}
