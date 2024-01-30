package intellijmigrationplugin.ui.editor

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener

class DocumentChangeListener : DocumentListener {

    private val documentHandler: AnnotationDocumentHandler

    constructor(documentHandler: AnnotationDocumentHandler) {
        this.documentHandler = documentHandler
    }

    override fun documentChanged(event: DocumentEvent) {
        documentHandler.documentChanged(event)
    }

    override fun bulkUpdateFinished(document: Document) {
        println("doc bulk update finished")
    }

    override fun bulkUpdateStarting(document: Document) {
        println("doc bulk update started")
    }

}