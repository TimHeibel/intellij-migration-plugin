package intellijmigrationplugin.actions.annotation.utils

import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import intellijmigrationplugin.annotationModel.AnnotationSnippet

class AnnotationActionUtils {
    companion object {
        internal fun removeAnnotation(annotation: AnnotationSnippet, document: Document) {

            if(annotation.hasEnd) {
                removeLine(annotation.end, document)
            }

            removeLine(annotation.start, document)
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

        internal fun removeLine(line: Int, document: Document) {

            var endOffset = document.getLineEndOffset(line) + 1

            if (endOffset >= document.textLength) {
                endOffset -= 1
            }

            val range = TextRange(document.getLineStartOffset(line), endOffset)

            document.replaceString(range.startOffset, range.endOffset, "")
        }
    }
}