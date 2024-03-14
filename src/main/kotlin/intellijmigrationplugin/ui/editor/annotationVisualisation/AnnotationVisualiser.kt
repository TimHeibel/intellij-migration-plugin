import com.intellij.openapi.editor.event.*
import intellijmigrationplugin.annotationModel.markervisualisation.HighlightAnnotationSnippet


interface AnnotationVisualiser {

    fun visualiseAnnotation(snippets: MutableList<HighlightAnnotationSnippet>) {}
    fun updateAnnotationVisualisation(snippets: MutableList<HighlightAnnotationSnippet>) {}

    fun turnVisualisationOff()
    fun turnVisualisationOn(snippets: MutableList<HighlightAnnotationSnippet>)

}