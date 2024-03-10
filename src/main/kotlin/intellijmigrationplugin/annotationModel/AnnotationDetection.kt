package intellijmigrationplugin.annotationModel

import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import intellijmigrationplugin.actions.annotation.utils.AnnotationActionUtils.Companion.getLine

/**
 * Utility class for detecting annotations in a given document for a specific file type.
 */
class AnnotationDetection {

    companion object {

        /**
         * Detects annotations in a given [document] for a specific [fileType].
         *
         * @param document The document to analyze for annotations.
         * @param fileType The type of the file associated with the document.
         * @return An [ArrayList] of [AnnotationSnippet] objects representing detected annotations.
         */
        fun detectAnnotationInFile(document: Document, fileType: String?): ArrayList<AnnotationSnippet> {
            val annotationSnippets = ArrayList<AnnotationSnippet>()
            val commentType = AnnotationInformation.instance?.singleCommentMapping?.get(".$fileType") ?: "//"
            val annotationMarkers = AnnotationInformation.instance?.markerColorMapping?.keys ?: emptySet()

            // Create a mapping of annotation markers to their corresponding regex patterns
            val markerRegexMapping = annotationMarkers.associateWith { getAnnotationRegex(commentType, it) }

            // Regex pattern to detect end of annotation block
            val regexEnd = getAnnotationRegex(commentType, "END")

            // Variables to track state of annotation parsing
            var annotationActive = false
            var annotationStartLine = 0

            for (lineIndex in 0 until document.lineCount) {
                val lineStartOffset = document.getLineStartOffset(lineIndex)
                val lineEndOffset = document.getLineEndOffset(lineIndex)
                val line = document.getText(TextRange(lineStartOffset, lineEndOffset))

                // Skip lines that don't start with a comment
                if (!line.startsWithComment(commentType)) continue

                if (!annotationActive) {
                    // Look for a line that starts a new annotation
                    markerRegexMapping.entries.find { it.value.containsMatchIn(line) }?.let { _ ->
                        annotationActive = true
                        annotationStartLine = lineIndex
                    }
                } else {
                    // Check if the current annotation ends in the current line.
                    if (regexEnd.containsMatchIn(line)) {

                        val annotation = AnnotationSnippet.fromStartLine(document.getLine(annotationStartLine), commentType, annotationStartLine, lineIndex, true)
                        annotationSnippets.add(annotation!!)
                        annotationActive = false
                        continue
                    }

                    // Check if a new annotation starts in the current line, thus terminating the old annotation.
                    markerRegexMapping.entries.find { it.value.containsMatchIn(line) }?.let { _ ->

                        val annotation = AnnotationSnippet.fromStartLine(document.getLine(annotationStartLine), commentType, annotationStartLine, lineIndex - 1, false)
                        annotationSnippets.add(annotation!!)
                        annotationStartLine = lineIndex
                    }
                }
            }

            // Add a final annotation snippet if an annotation block was left open
            if (annotationActive) {
                val annotation = AnnotationSnippet.fromStartLine(document.getLine(annotationStartLine), commentType, annotationStartLine, document.lineCount - 1, false)
                annotationSnippets.add(annotation!!)
            }

            return annotationSnippets
        }

        /**
         * Generates a regex pattern for a given [annotationType] and [commentType].
         *
         * @param commentType The comment syntax used in the document.
         * @param annotationType The type of annotation.
         * @return The generated [Regex] pattern for the annotation.
         */
        private fun getAnnotationRegex(commentType: String, annotationType: String): Regex {
            return Regex("^(\\h)*${Regex.escape(commentType)}(\\h)*${Regex.escape(annotationType)}($|\\s)", RegexOption.IGNORE_CASE)
        }

        /**
         * Checks if the [String] starts with the specified [commentType].
         *
         * @param commentType The comment syntax to check for at the beginning of the string.
         * @return `true` if the string starts with the comment syntax, `false` otherwise.
         */
        private fun String.startsWithComment(commentType: String): Boolean {
            return this.trimStart().startsWith(commentType)
        }
    }
}