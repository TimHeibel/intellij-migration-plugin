package intellijmigrationplugin.annotationModel

import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import kotlinx.coroutines.*

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
                    if (regexEnd.containsMatchIn(line)) {
                        annotationSnippets.add(AnnotationSnippet(annotationStartLine, lineIndex, true, currAnnotation))
                        annotationActive = false
                        continue
                    }

                    // Check if a new annotation starts in the current line, thus terminating the old annotation.
                    markerRegexMapping.entries.find { it.value.containsMatchIn(line) }?.let { matched ->
                        annotationSnippets.add(AnnotationSnippet(annotationStartLine, lineIndex - 1, false, currAnnotation))
                        currAnnotation = matched.key
                        annotationStartLine = lineIndex
                    }
                }
            }

            // Add a final annotation snippet if an annotation block was left open
            if (annotationActive) {
                annotationSnippets.add(AnnotationSnippet(annotationStartLine, document.lineCount - 1, false, currAnnotation))
            }

            return annotationSnippets
        }

        /**
         * Note line numbers from 0 to n-1 number of lines
         * @param lineStart: inclusive line index
         * @param lineEnd: exclusive line index
         */
        suspend fun detectAnnotationInFile(document: Document, fileType: String?, lineStart: Int, lineEnd: Int): MutableList<Pair<Int, String>> {
            val outputList = mutableListOf<Pair<Int, String>>()
            val commentType = AnnotationInformation.instance!!.singleCommentMapping[fileType]
                    ?: "//"
            val keywords = AnnotationInformation.instance!!.keywords
            val regexes = keywords.map { x ->  getAnnotationRegex(commentType, x) }
            val regexEnd = getAnnotationRegex(commentType, "end")

            yield()
            for (i in lineStart..lineEnd - 1) {

                val startOffset = document.getLineStartOffset(i)
                val endOffset = document.getLineEndOffset(i)
                val line = document.getText(TextRange(startOffset, endOffset))

                if (line.contains(regexEnd)) {
                    outputList.add(Pair(i, "end"))
                    continue
                }
                var j = 0
                for (regex in regexes) {
                    if (line.contains(regex)) {
                        outputList.add(Pair(i, keywords[j]))
                        break
                    }
                    j++
                }
                yield()
            }
            return outputList

        }

        // Generates a regex pattern for a given annotation type and comment syntax
        fun getAnnotationRegex(commentType: String, annotationType: String): Regex {
            return Regex("^(\\h)*${Regex.escape(commentType)}(\\h)*${Regex.escape(annotationType)}($|\\s)", RegexOption.IGNORE_CASE)
        }

        private fun String.startsWithComment(commentType: String): Boolean {
            return this.trimStart().startsWith(commentType)
        }
    }
}
