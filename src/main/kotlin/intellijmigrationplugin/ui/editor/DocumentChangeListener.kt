package intellijmigrationplugin.ui.editor

import AnnotationVisualiser
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.markup.MarkupModel
import intellijmigrationplugin.annotationModel.AnnotationInformation
import intellijmigrationplugin.ui.editor.markervisualisation.*

class DocumentChangeListener : DocumentListener {

    private val visualiser: AnnotationVisualiser

    constructor(path: String, mm: MarkupModel) {
        visualiser = SimpleAnnotationVisualiser(path, mm)
        if (!AnnotationInformation.instance!!.showMarker) return
        visualiser.visualiseAnnotation()
    }

    override fun documentChanged(event: DocumentEvent) {
        if (!AnnotationInformation.instance!!.showMarker) return
        visualiser.updateAnnotationVisualisation(event)
    }

    override fun bulkUpdateFinished(document: Document) {
        println("doc bulk update finished")
    }

    override fun bulkUpdateStarting(document: Document) {
        println("doc bulk update started")
    }

    fun turnVisualisationOn() {
        visualiser.turnVisualisationOn()
    }

    fun turnVisualisationOff() {
        visualiser.turnVisualisationOff()
    }
}