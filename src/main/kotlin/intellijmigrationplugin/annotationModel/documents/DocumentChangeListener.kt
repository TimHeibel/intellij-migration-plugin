package intellijmigrationplugin.annotationModel.documents

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener

/**
 * This class should be created for every [AnnotationDocumentHandler] as it registers a document change listener.
 * All updates on a document call the functions in this class and are then transmitted to the Document Handler.
 */
class DocumentChangeListener(private val documentHandler: AnnotationDocumentHandler) : DocumentListener {

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