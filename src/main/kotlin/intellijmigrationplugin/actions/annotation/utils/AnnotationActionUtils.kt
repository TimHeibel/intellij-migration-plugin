package intellijmigrationplugin.actions.annotation.utils

import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import intellijmigrationplugin.annotationModel.AnnotationSnippet

class AnnotationActionUtils {
    companion object {
        internal fun removeAnnotation(annotation: AnnotationSnippet, document: Document) {

            if(annotation.hasEnd) {
                var offset = document.getLineEndOffset(annotation.end) + 1

                if(offset > document.textLength) {
                    offset -= 1
                }

                document.replaceString(document.getLineStartOffset(annotation.end),
                        offset, "")
            }

            var offset = document.getLineEndOffset(annotation.start) + 1
            if(offset > document.textLength) {
                offset -= 1
            }

            document.replaceString(document.getLineStartOffset(annotation.start),
                    offset, "")
        }

        internal fun placeAnnotation(annotationType: String, annotationComment : String, startLine: Int, endLine: Int,
                                     commentStart: String, document: Document) {

            document.insertString(document.getLineEndOffset(endLine), "\n${commentStart}END")
            document.insertString(document.getLineStartOffset(startLine),
                    "$commentStart$annotationType $annotationComment\n")
        }

        internal fun getLineFromDocument(line: Int, document: Document) : String {
            return document.getText(TextRange(document.getLineStartOffset(line), document.getLineEndOffset(line)))
        }
    }
}