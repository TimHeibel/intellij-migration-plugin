package intellijmigrationplugin.annotationModel.documents

import AnnotationVisualiser
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.DocumentEvent
import intellijmigrationplugin.annotationModel.AnnotationInformation
import intellijmigrationplugin.annotationModel.markervisualisation.HighlightAnnotationFile
import intellijmigrationplugin.annotationModel.markervisualisation.HighlightAnnotationSnippet
import intellijmigrationplugin.ui.editor.annotationVisualisation.SimpleAnnotationVisualiser
import kotlinx.coroutines.runBlocking

/**
 * Handles events and the state for a single document, which is opened as a tab at the top bar
 */
class AnnotationDocumentHandler {

    private lateinit var listener: DocumentChangeListener
    private lateinit var highlightAnnotationFile: HighlightAnnotationFile
    private var visualiser: AnnotationVisualiser
    private val editor: Editor
    val path: String

    constructor(path: String, editor: Editor) {
        this.path = path
        this.editor = editor
        visualiser = SimpleAnnotationVisualiser(path, editor.markupModel)
        highlightAnnotationFile = HighlightAnnotationFile(path, editor.document)

        runBlocking {
            val snippets = highlightAnnotationFile.computeSnippets()
            visualiser.visualiseAnnotation(snippets)
        }
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is AnnotationDocumentHandler && other.path == path
    }

    fun documentChanged(event: DocumentEvent) {
        //Change highlight annotation file aka model
        //var changes = highlightAnnotationFile.handleEvent(event)
        AnnotationInformation.instance!!.lastCursorLine = null;
        var snippets: MutableList<HighlightAnnotationSnippet>
        runBlocking {
            snippets = highlightAnnotationFile.computeSnippets()
        }

        if (!AnnotationInformation.instance!!.showMarker) return

        //Change visualisation maybe durch interface abstrahieren
        runBlocking {
            visualiser.updateAnnotationVisualisation(snippets)
        }
    }

    /**
     * Should be called in [OpenDocumentManager], when a document is added to the list of open documents
     */
    fun registerDocumentListener() {
        listener = DocumentChangeListener(this)
        editor.document.addDocumentListener(listener)
    }

    /**
     * Should be called in [OpenDocumentManager], when a document is removed from the list of open documents
     */
    fun deregisterDocumentListener() {
        editor.document.removeDocumentListener(listener)
    }

    fun turnVisualisationOn() {
        runBlocking {
            var snippets = highlightAnnotationFile.computeSnippets();
            visualiser.turnVisualisationOn(snippets)
        }
    }

    fun turnVisualisationOff() {
        visualiser.turnVisualisationOff()
    }

    fun getSnippets(): MutableList<HighlightAnnotationSnippet> {
        return highlightAnnotationFile.snippets;
    }

}