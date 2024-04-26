package intellijmigrationplugin.actions.annotation.utils

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import intellijmigrationplugin.annotationModel.AnnotationInformation
import intellijmigrationplugin.annotationModel.AnnotationSnippet
import intellijmigrationplugin.statistics.LineAnalyser

/**
 * Utility class for performing actions related to annotations within a document or editor.
 */
class AnnotationActionUtils {
    companion object {

        /**
         * Retrieves the comment syntax based on the event.
         *
         * @param event The action event triggered by the user.
         * @return The comment syntax used in the document, or the default if not found.
         */
        internal fun getCommentTypeByEvent(event: AnActionEvent) : String {

            val annotationInformation = AnnotationInformation.instance!!

            val default = annotationInformation.defaultSingleComment

            val fType = getFileTypeByEvent(event)
                    ?: return default

            annotationInformation.singleCommentMapping[".$fType"]?.let {
                return it
            }

            return default
        }

        /**
         * Retrieves the file type based on the given [event].
         *
         * @param event The action event triggered by the user.
         * @return The file extension representing the file type, or `null` if the file type cannot be determined.
         */
        internal fun getFileTypeByEvent(event: AnActionEvent) : String? {

            val vFile = event.getData(PlatformCoreDataKeys.VIRTUAL_FILE)
                    ?: return null

            return vFile.extension

        }


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

        internal fun Document.canMerge(first: AnnotationSnippet, second: AnnotationSnippet, filePath: String) : Boolean {
            if(!first.isSimilar(second)) {
                return false
            }

            var startAnnotation = first
            var endAnnotation = second

            if(first.start > second.start) {
                startAnnotation = second
                endAnnotation = first
            }

            val startLine = startAnnotation.end + 1
            val endLine = endAnnotation.start - 1

            return !containsCode(startLine, endLine, filePath)
        }

        internal fun Document.containsCode(startLine: Int, endLine: Int, filePath: String) : Boolean {

            val fileInformation = LineAnalyser().getFileInformation(filePath, AnnotationInformation.instance?.importMapping, AnnotationInformation.instance?.singleCommentMapping, AnnotationInformation.instance?.multiCommentMapping)

            var multiLineCommentActive = false

            for (lineNumber in startLine .. endLine) {
                val line = getLine(lineNumber)

                if(multiLineCommentActive) {
                    if(line.startsWith(fileInformation[4])) {
                        multiLineCommentActive = false
                    }
                    continue
                }

                if(line.isBlank()) {
                    continue
                }

                if(line.startsWith(fileInformation[0]) ||
                    line.startsWith(fileInformation[1])) {
                    continue
                }

                if(line.startsWith(fileInformation[3])) {
                    multiLineCommentActive = true
                    continue
                }

                return true
            }
            return false;
        }
    }
}