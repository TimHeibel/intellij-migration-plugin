package intellijmigrationplugin.settings.converters
import com.intellij.ui.JBColor
import com.intellij.util.xmlb.Converter
import org.jetbrains.annotations.NotNull

/**
 * Converter for serializing and deserializing a list of pairs containing a string and JBColor.
 */
internal class JBColorConverter : Converter<MutableList<Pair<String, JBColor>>?>() {
    override fun fromString(@NotNull value: String): MutableList<Pair<String, JBColor>> {
        val entries = value.split(";")
        val list = mutableListOf<Pair<String, JBColor>>()

        for (entry in entries) {
            val parts = entry.split(":")
            val key = parts[0]
            val colors = parts[1].split(",")
            val regularRGB = colors[0].toInt()
            val darkRGB = colors[1].toInt()

            list.add(key to JBColor(regularRGB, darkRGB))
        }

        return list
    }

    override fun toString(value: MutableList<Pair<String, JBColor>>): String {
        val entries = value.joinToString(";") { (key, jbColor) ->
            "$key:${jbColor.rgb},${jbColor.darkVariant.rgb}"
        }

        return entries
    }
}
