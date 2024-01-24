package intellijmigrationplugin.settings.converters

import com.intellij.util.xmlb.Converter
import intellijmigrationplugin.settings.components.FileTypeMapping
import org.jetbrains.annotations.NotNull

/**
 * Converter for serializing and deserializing a list of FileTypeMapping objects.
 */
internal class FileTypeMappingListConverter : Converter<MutableList<FileTypeMapping>?>() {
    override fun fromString(@NotNull value: String): MutableList<FileTypeMapping> {
        val entries = value.split(";")
        val list = mutableListOf<FileTypeMapping>()

        for (entry in entries) {
            val parts = entry.split(":")
            val filetype = parts[0]
            val singleLineComment = parts[1]
            val multiLineComment = parts[2]
            val importStatement = parts[3]

            list.add(FileTypeMapping(filetype, singleLineComment, multiLineComment, importStatement))
        }
        return list
    }

    override fun toString(value: MutableList<FileTypeMapping>): String {
        val entries = value.joinToString(";") { mapping ->
            "${mapping.filetype}:${mapping.singleLineComment}:${mapping.multiLineComment}:${mapping.importStatement}"
        }
        return entries
    }
}
