package intellijmigrationplugin.actions.annotation.utils

import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import intellijmigrationplugin.annotationModel.AnnotationSnippet

class AnnotationActionUtils {
    companion object {
        internal fun Document.removeAnnotation(annotation: AnnotationSnippet) {

            if(annotation.hasEnd) {
                removeLine(annotation.end)
            }

            removeLine(annotation.start)
        }

        internal fun Document.placeAnnotation(annotationType: String, annotationComment : String, startLine: Int, endLine: Int,
                                              commentStart: String) {

            this.insertString(this.getLineEndOffset(endLine), "\n${commentStart}END")
            this.insertString(this.getLineStartOffset(startLine),
                    "$commentStart$annotationType $annotationComment\n")
        }

        internal fun Document.placeOneLineAnnotation(annotationType: String, annotationComment: String, line: Int,
                                            commentStart: String) {
            this.insertString(this.getLineStartOffset(line),
                    "$commentStart$annotationType $annotationComment\n")
            this.insertString(this.getLineEndOffset(line), "\n${commentStart}END")
        }

        internal fun Document.getLine(line: Int): String {
            return this.getText(TextRange(this.getLineStartOffset(line), this.getLineEndOffset(line)))
        }


        internal fun Document.removeLine(line: Int) {

            var endOffset = this.getLineEndOffset(line) + 1

            if (endOffset >= this.textLength) {
                endOffset -= 1
            }

            val range = TextRange(this.getLineStartOffset(line), endOffset)

            this.replaceString(range.startOffset, range.endOffset, "")
        }
    }
}