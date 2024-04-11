package intellijmigrationplugin.annotationModel.documents

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import intellijmigrationplugin.annotationModel.documents.AnnotationDocumentHandler

/**
 * This class should be created for every [AnnotationDocumentHandler] as it registers a document change listener.
 * All updates on a document call the functions in this class and are then transmitted to the Document Handler.
 */
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