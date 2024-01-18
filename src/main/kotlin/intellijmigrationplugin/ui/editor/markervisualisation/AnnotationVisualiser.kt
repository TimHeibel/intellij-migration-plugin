import com.intellij.openapi.editor.event.*


interface AnnotationVisualiser {

    fun visualiseAnnotation() {}
    fun updateAnnotationVisualisation(event: DocumentEvent) {}
}