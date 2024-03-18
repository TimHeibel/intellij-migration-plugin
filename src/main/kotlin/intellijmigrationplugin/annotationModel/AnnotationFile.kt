package intellijmigrationplugin.annotationModel

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.util.TextRange
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

open class AnnotationFile {

    var fileName: String
    var endLine: Int
    private var snippets: MutableList<AnnotationSnippet>
    protected var document: Document

    constructor(fileName: String, document: Document) {
        this.fileName = fileName
        snippets = mutableListOf()
        this.endLine = -1
        this.document = document
    }

}