package intellijmigrationplugin.annotationModel

import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange

class AnnotationDetection {

    companion object {
        // Detects annotations in a given document for a specific file type
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
            var currAnnotation = ""
            var annotationStartLine = 0

            for (lineIndex in 0 until document.lineCount) {
                val lineStartOffset = document.getLineStartOffset(lineIndex)
                val lineEndOffset = document.getLineEndOffset(lineIndex)
                val line = document.getText(TextRange(lineStartOffset, lineEndOffset))

                // Skip lines that don't start with a comment
                if (!line.startsWithComment(commentType)) continue

                if (!annotationActive) {
                    // Look for a line that starts a new annotation
                    markerRegexMapping.entries.find { it.value.containsMatchIn(line) }?.let { matched ->
                        annotationActive = true
                        currAnnotation = matched.key
                        annotationStartLine = lineIndex
                    }
                } else {
                    // Check if the current annotation ends in the current line.
                    if (regexEnd.containsMatchIn(line) || markerRegexMapping[currAnnotation]?.containsMatchIn(line) == true) {
                        annotationSnippets.add(AnnotationSnippet(annotationStartLine, lineIndex - if (regexEnd.containsMatchIn(line)) 0 else 1, regexEnd.containsMatchIn(line), currAnnotation))
                        annotationActive = false
                    }
                }
            }

            // Add a final annotation snippet if an annotation block was left open
            if (annotationActive) {
                annotationSnippets.add(AnnotationSnippet(annotationStartLine, document.lineCount - 1, false, currAnnotation))
            }

            return annotationSnippets
        }

        // Generates a regex pattern for a given annotation type and comment syntax
        private fun getAnnotationRegex(commentType: String, annotationType: String): Regex {
            return Regex("^(\\h)*${Regex.escape(commentType)}(\\h)*${Regex.escape(annotationType)}($|\\s)", RegexOption.IGNORE_CASE)
        }

        private fun String.startsWithComment(commentType: String): Boolean {
            return this.trimStart().startsWith(commentType)
        }
    }
}
