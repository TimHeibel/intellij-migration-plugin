package intellijmigrationplugin.annotationModel

import com.intellij.openapi.application.ApplicationManager
import intellijmigrationplugin.annotationModel.documents.OpenDocumentManager
import intellijmigrationplugin.settings.MigrationSettingsState
import java.awt.Color
import intellijmigrationplugin.settings.components.FileTypeMapping

class AnnotationInformation private constructor() {

    companion object {
        var instance: AnnotationInformation? = null
            private set
            get() {
                if (field == null) {
                    field = AnnotationInformation()
                }
                return field!!
            }
    }

    lateinit var documentManager: OpenDocumentManager
    var lastCursorLine: Int? = null

    private val settings: MigrationSettingsState
        get() {
            return ApplicationManager.getApplication().getService(MigrationSettingsState::class.java)
        }


    val legacyFolderPath: String
        get() {
            return settings.legacyFolderPath
        }

    val excludedFolderList: List<String>
        get() {
            return settings.excludedFoldersList
        }

    val markerColorMapping: HashMap<String, String>
        get() {
            val colorMapping = settings.keywordColorMapping

            val colorHashMap = HashMap<String, String>()
            for (pair in colorMapping) {
                colorHashMap[pair.first] = pair.second
            }

            return colorHashMap
        }

    val markerRealColorMapping: HashMap<String, Color>
        get() {
            val colorMapping = markerColorMapping

            val colorHashMap = HashMap<String, Color>()
            for (pair in colorMapping) {

                val a = pair.value.substring(1, 3).toInt(16)
                val r = pair.value.substring(3, 5).toInt(16)
                val g = pair.value.substring(5, 7).toInt(16)
                val b = pair.value.substring(7, 9).toInt(16)
                colorHashMap[pair.key] = Color(r, g, b, a)
            }
            return colorHashMap
        }

    val keywords: List<String>
        get() {
            return settings.keywordColorMapping.map { x -> x.first }
        }

    val fileTypeMapping: HashMap<String, FileTypeMapping>
        get() {
            val typeMapping = settings.fileTypeCommentMapping

            val typeHashMap = HashMap<String, FileTypeMapping>()

            for (fileType in typeMapping) {

                //Returns only a shallow copy to ensure consistency
                val mapping = fileType.copy()
                typeHashMap[mapping.filetype] = mapping
            }

            return typeHashMap
        }

    var showMarker: Boolean = true
        set(value) {
            field = value
            if (!this::documentManager.isInitialized) return
            if (value) documentManager.turnVisualisationOn()
            else documentManager.turnVisualisationOff()
        }

    val singleCommentMapping: HashMap<String, String>
        get() {
            val typeMapping = fileTypeMapping
            val singleCommentMapping = HashMap<String, String>()

            for (key in typeMapping.keys) {
                singleCommentMapping[typeMapping[key]!!.filetype] = typeMapping[key]!!.singleLineComment
            }

            return singleCommentMapping
        }

    val defaultSingleComment: String
        get() {
            return singleCommentMapping[".*"] ?: "//"
        }

    val multiCommentMapping: HashMap<String, String>
        get() {
            val typeMapping = fileTypeMapping
            val multiCommentMapping = HashMap<String, String>()

            for (key in typeMapping.keys) {

                multiCommentMapping[typeMapping[key]!!.filetype] = typeMapping[key]!!.multiLineComment
            }

            return multiCommentMapping
        }

    val defaultMultiComment: String
        get() {
            return multiCommentMapping[".*"] ?: "/* */"
        }

    val importMapping: HashMap<String, String>
        get() {
            val typeMapping = fileTypeMapping
            val importMapping = HashMap<String, String>()

            for (key in typeMapping.keys) {

                importMapping[typeMapping[key]!!.filetype] = typeMapping[key]!!.importStatement
            }

            return importMapping
        }

    val defaultImport : String
        get() {
            return importMapping[".*"] ?: "import"
        }


}