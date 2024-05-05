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

            var startLine = startAnnotation.end
            if(!startAnnotation.hasEnd) {
                startLine+= 1
            }

            val endLine = endAnnotation.start

            return !containsCode(startLine, endLine, filePath)
        }

        internal fun Document.containsCode(startLine: Int, endLine: Int, filePath: String) : Boolean {

            val annotationInformation = AnnotationInformation.instance!!
            val fileInformationArray = LineAnalyser().getFileInformation(filePath, annotationInformation)

            val fileInformation = FileInformation(fileInformationArray[1], fileInformationArray[3], fileInformationArray[4])

            var multiLineCommentActive = false

            for (lineNumber in 0 .. lineCount - 1) {
                val line = getLine(lineNumber)

                val lineStatus = line.detectCode(fileInformation, multiLineCommentActive)

                if(lineStatus.containsCode && lineNumber in startLine .. endLine) {
                    return true
                }

                multiLineCommentActive = lineStatus.multiLineActive
            }
            return false
        }


        /**
         * Detects the code status of the current line based on the provided [fileInfo].
         *
         * @param fileInfo The information about the file's comment syntax.
         * @param multiActive Flag indicating if the multi-line comment is currently active.
         * @return The line status indicating whether the line contains code and if multi-line comment is active.
         */
        private fun String.detectCode(fileInfo: FileInformation, multiActive: Boolean = false): LineStatus {
            //Ensuring that each string has length > 0, to ensure termination of recursion
            if(fileInfo.single == "" || fileInfo.multiEnd == "" || fileInfo.multiStart == "") {
                thisLogger().warn("something went wrong, defining the comment syntax")
                return LineStatus(true, multiActive)
            }

            // Check if the line is blank
            if(isBlank()) {
                return LineStatus(false, multiActive)
            }

            // Check if multi-line comment is active
            if(multiActive) {
                if(contains(fileInfo.multiEnd)) {
                    return substringAfter(fileInfo.multiEnd).detectCode(fileInfo)
                }

                return LineStatus(containsCode = false, multiLineActive = true)
            }

            // Check if line starts with a single-line comment
            if(trim().startsWith(fileInfo.single)) {
                return LineStatus(containsCode = false, multiLineActive = false)
            }

            // Check if line starts a multi-line comment
            if(trim().startsWith(fileInfo.multiStart)) {
                return substringAfter(fileInfo.multiStart).detectCode(fileInfo, true)
            }

            // Code segment found
            val containsCode = true

            // Check if multi-line comment is opened after a code segment
            if(!contains(fileInfo.multiStart)) {
                return LineStatus(containsCode, false)
            }

            if(substringAfter(fileInfo.multiStart, "") <= substringAfter(fileInfo.single, "")) {
                return LineStatus(containsCode, false)
            }

            //detects a multiLineComment is opened after a code segment, short-circuiting if none is opened
            return LineStatus(containsCode, substringAfter(fileInfo.multiStart, fileInfo.multiEnd)
                .detectCode(fileInfo, true).multiLineActive)
        }

    }
}

/**
 * Data class representing information about the comment syntax of a file.
 *
 * @property single The single-line comment syntax.
 * @property multiStart The start delimiter of the multi-line comment syntax.
 * @property multiEnd The end delimiter of the multi-line comment syntax.
 */
private data class FileInformation(val single : String, val multiStart : String, val multiEnd : String)

/**
 * Data class representing the status of a line in terms of containing code and the state of multi-line comment.
 *
 * @property containsCode Indicates whether the line contains code.
 * @property multiLineActive Indicates whether a multi-line comment is active at the end of the string.
 */
private data class LineStatus(val containsCode : Boolean, val multiLineActive : Boolean)