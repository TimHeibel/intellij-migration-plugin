package intellijmigrationplugin.annotationModel

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


    val annotationFiles: HashMap<String, AnnotationFile> = HashMap()
    var legacyFolerPath: String = ""
    val markerColorMapping: HashMap<AnnotationType, Color> = HashMap()
    val commentTypeMapping: HashMap<String, String> = HashMap()


    fun getAnnotationFile(name: String): AnnotationFile {
        return AnnotationFile()
    }

    fun computeAnnotationRanges(): HashMap<AnnotationType, Int> {
        return HashMap()
    }
}