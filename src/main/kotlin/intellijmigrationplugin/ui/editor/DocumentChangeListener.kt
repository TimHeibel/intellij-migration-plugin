package intellijmigrationplugin.ui.editor

import AnnotationVisualiser
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.markup.MarkupModel
import intellijmigrationplugin.ui.editor.visualiser.*

class DocumentChangeListener: DocumentListener {

        private val fs: AnnotationVisualiser

        constructor(path: String, mm: MarkupModel) {
            fs = SimpleAnnotationVisualiser(path, mm)
            fs.visualiseAnnotation()
        }

        override fun documentChanged(event: DocumentEvent) {
            fs.visualiseAnnotation()
        }

        override fun bulkUpdateFinished(document: Document) {
            println("doc bulk update finished")
        }

        override fun bulkUpdateStarting(document: Document) {
            println("doc bulk update started")
        }

}