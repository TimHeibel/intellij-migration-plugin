package intellijmigrationplugin.annotationModel

import com.intellij.openapi.application.ApplicationManager
import intellijmigrationplugin.settings.MigrationSettingsState
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


    private var instance: AnnotationInformation? = null
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

   val singleCommentMapping: HashMap<String, String>
       get() {
           val typeMapping = fileTypeMapping
           val singleCommentMapping = HashMap<String, String>()

           for(key in typeMapping.keys) {

               singleCommentMapping[typeMapping[key]!!.filetype] = typeMapping[key]!!.singleLineComment
           }

           return singleCommentMapping
       }

   val multiCommentMapping: HashMap<String, String>
       get() {
           val typeMapping = fileTypeMapping
           val multiCommentMapping = HashMap<String, String>()

           for(key in typeMapping.keys) {

               multiCommentMapping[typeMapping[key]!!.filetype] = typeMapping[key]!!.multiLineComment
           }

           return multiCommentMapping
       }

   val importMapping: HashMap<String, String>
       get() {
           val typeMapping = fileTypeMapping
           val importMapping = HashMap<String, String>()

           for(key in typeMapping.keys) {

               importMapping[typeMapping[key]!!.filetype] = typeMapping[key]!!.importStatement
           }

           return importMapping
       }


}