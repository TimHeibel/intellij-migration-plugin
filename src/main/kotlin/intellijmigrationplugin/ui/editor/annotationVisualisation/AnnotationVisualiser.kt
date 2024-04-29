package intellijmigrationplugin.ui.editor.annotationVisualisation

import intellijmigrationplugin.annotationModel.AnnotationSnippet


interface AnnotationVisualiser {

    fun visualiseAnnotation(snippets: MutableList<AnnotationSnippet>) {}
    fun updateAnnotationVisualisation(snippets: MutableList<AnnotationSnippet>) {}
    fun turnVisualisationOff()
    fun turnVisualisationOn(snippets: MutableList<AnnotationSnippet>)

}