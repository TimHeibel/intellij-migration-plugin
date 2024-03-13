package intellijmigrationplugin.actions.annotation.utils

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import intellijmigrationplugin.annotationModel.AnnotationSnippet

/**
 * Utility class for performing actions related to annotations within a document.
 */
class AnnotationActionUtils {
    companion object {

        /**
         * Removes the specified [annotation] from the [Document].
         *
         * @param annotation The annotation snippet to be removed.
         */
        internal fun Document.removeAnnotation(annotation: AnnotationSnippet) {

            if(annotation.hasEnd) {
                removeLine(annotation.end)
            }

            removeLine(annotation.start)
        }

        /**
         * Places a multi-line annotation in the [Document] starting from [startLine] to [endLine].
         *
         * @param annotationType The type of annotation.
         * @param annotationComment The comment associated with the annotation.
         * @param startLine The start line of the annotation.
         * @param endLine The end line of the annotation.
         * @param commentStart The string to start the annotation comment with.
         */
        internal fun Document.placeAnnotation(
                annotationType: String,
                annotationComment : String,
                startLine: Int, endLine: Int,
                commentStart: String) {

            this.insertString(this.getLineEndOffset(endLine), "\n${commentStart}END")
            this.insertString(this.getLineStartOffset(startLine),
                    "$commentStart$annotationType $annotationComment\n")
        }

        /**
         * Places a one-line annotation in the [Document] at the specified [line].
         *
         * @param annotationType The type of annotation.
         * @param annotationComment The comment associated with the annotation.
         * @param line The line where the annotation is placed.
         * @param commentStart The string to start the annotation comment with.
         */
        internal fun Document.placeOneLineAnnotation(
                annotationType: String,
                annotationComment: String,
                line: Int,
                commentStart: String) {
            this.insertString(this.getLineStartOffset(line),
                    "$commentStart$annotationType $annotationComment\n")
            this.insertString(this.getLineEndOffset(line), "\n${commentStart}END")
        }

        /**
         * Retrieves the text content of the specified [line] from the [Document].
         *
         * @param line The line number.
         * @return The text content of the specified line.
         */
        internal fun Document.getLine(line: Int): String {
            return this.getText(TextRange(this.getLineStartOffset(line), this.getLineEndOffset(line)))
        }

        /**
         * Removes the content of the specified [line] from the [Document].
         *
         * @param line The line number to be removed.
         */
        internal fun Document.removeLine(line: Int) {

            var endOffset = this.getLineEndOffset(line) + 1

            //Handle possible end of file
            if (endOffset >= this.textLength) {
                endOffset -= 1
            }

            val range = TextRange(this.getLineStartOffset(line), endOffset)

            this.replaceString(range.startOffset, range.endOffset, "")
        }

        /**
         * Merges two annotations represented by [first] and [second] into a single annotation in the document.
         * Note: This function does not check if the annotations are consecutive and similar.
         *
         * @param first The first annotation snippet.
         * @param second The second annotation snippet.
         */
        internal fun Document.mergeAnnotations(first: AnnotationSnippet, second: AnnotationSnippet) {
            var startAnnotation = first
            var endAnnotation = second

            if(first.start > second.start) {
                startAnnotation = second
                endAnnotation = first
            }

            if(startAnnotation.end > endAnnotation.start) {
                thisLogger().warn("Annotations overlap, which should not be possible. Abort merge")
                return
            }

            removeLine(endAnnotation.start)

            if(startAnnotation.hasEnd) {
                removeLine(startAnnotation.end)
            }
        }
    }
}