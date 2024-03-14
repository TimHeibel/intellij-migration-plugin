package intellijmigrationplugin.annotationModel.markervisualisation

import intellijmigrationplugin.annotationModel.AnnotationSnippet
import java.util.*

/**
 * Start line is inclusive
 * End line is inclusive
 */
class HighlightAnnotationSnippet: AnnotationSnippet {

    val id: UUID

    constructor(start: Int, end: Int, type: String, hasEnd: Boolean): super(start, end, hasEnd, type) {
        id = UUID.randomUUID()
    }


    fun contains(line: Int): Boolean {
        return line in start..end-1
    }


}