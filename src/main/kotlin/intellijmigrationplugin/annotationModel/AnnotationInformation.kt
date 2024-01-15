package intellijmigrationplugin.annotationModel

import com.intellij.openapi.application.ApplicationManager
import intellijmigrationplugin.settings.MigrationSettingsState

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


    private var instance: AnnotationInformation? = null
    private val settings: MigrationSettingsState
        get() {
            return ApplicationManager.getApplication().getService(MigrationSettingsState::class.java)
        }


    val legacyFolderPath: String
        get() {
            return settings.legacyFolderPath
        }

    val markerColorMapping: HashMap<AnnotationType, String>
        get() {
            val colorMapping = settings.keywordColorMapping

            val colorHashMap = HashMap<AnnotationType, String>()
            for (pair in colorMapping) {
                colorHashMap[AnnotationType.valueOf(pair.first)] = pair.second
            }

            return colorHashMap
        }

    // TODO(David)
   /* val commentTypeMapping: HashMap<String, String>
        get() {
            val commentMapping = settings.fileTypeCommentMapping

            val commentHashMap = HashMap<String, String>()

            for (pair in commentMapping) {
                commentHashMap[pair.first] = pair.second
            }

            return commentHashMap
        }*/

}