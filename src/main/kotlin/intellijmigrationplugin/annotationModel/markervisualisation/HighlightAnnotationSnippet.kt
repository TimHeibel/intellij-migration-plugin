package intellijmigrationplugin.annotationModel.`markervisualisation+`

import intellijmigrationplugin.annotationModel.AnnotationSnippet
import java.util.*

/**
 * Start line is inclusive
 * End line is inclusive
 */
class HighlightAnnotationSnippet: AnnotationSnippet {

    val id: UUID

    constructor(start: Int, end: Int, type: String): super(start, end, true, type) {
        id = UUID.randomUUID()
    }



}