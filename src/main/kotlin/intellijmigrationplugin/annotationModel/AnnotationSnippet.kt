package intellijmigrationplugin.annotationModel

import java.util.Date


class AnnotationSnippet(var start: Int, var end: Int, var hasEnd: Boolean, var type: AnnotationType) {

    var newFunctionName: String? = null

    fun getRange(): Int {
        return end - start
    }

    fun inRange(line: Int): Boolean {
        return line in start..end
    }

}