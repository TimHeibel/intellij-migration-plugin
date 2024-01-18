package intellijmigrationplugin.annotationModel

import com.intellij.openapi.application.ApplicationManager
import intellijmigrationplugin.settings.MigrationSettingsState
import intellijmigrationplugin.ui.editor.DocumentChangeListener
import intellijmigrationplugin.ui.editor.FileSelectionChangeListener
import java.awt.Color

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

    private val settings: MigrationSettingsState
        get() {
            return ApplicationManager.getApplication().getService(MigrationSettingsState::class.java)
        }


    val legacyFolderPath: String
        get() {
            return settings.legacyFolderPath
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
                val r = pair.value.substring(1,3).toInt(16)
                val g = pair.value.substring(3,5).toInt(16)
                val b = pair.value.substring(5,7).toInt(16)
                //val a = pair.value.substring(1,3)
                colorHashMap[pair.key] = Color(r, g, b, 30)
            }
            return colorHashMap
        }

    val keywords: List<String>
        get() {
            return settings.keywordColorMapping.map { x -> x.first }
        }

    val commentTypeMapping: HashMap<String, String>
        get() {
            val commentMapping = settings.fileTypeCommentMapping

            val commentHashMap = HashMap<String, String>()

            for (pair in commentMapping) {
                commentHashMap[pair.first] = pair.second
            }

            return commentHashMap
        }

    var showMarker: Boolean = true
        set(value) {
            field = value
            if (!this::fileSelectionChangeManager.isInitialized) return
            if (value) fileSelectionChangeManager.turnVisualisationOn()
            else fileSelectionChangeManager.turnVisualisationOff()
        }

    lateinit var fileSelectionChangeManager: FileSelectionChangeListener

}