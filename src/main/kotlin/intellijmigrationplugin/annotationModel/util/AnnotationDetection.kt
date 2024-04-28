package intellijmigrationplugin.annotationModel.util

import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import groovy.lang.Tuple
import intellijmigrationplugin.annotationModel.AnnotationInformation
import intellijmigrationplugin.annotationModel.AnnotationSnippet
import intellijmigrationplugin.actions.annotation.utils.AnnotationActionUtils.Companion.getLine
import kotlinx.coroutines.*

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
                    // Look for END annotations without a starting annotation
                    if (regexEnd.matches(line)) {
                        val annotation = AnnotationSnippet(lineIndex, lineIndex, false, "END")
                        annotationSnippets.add(annotation)
                        continue
                    }

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
         * Note line numbers from 0 to n-1 number of lines
         * @param lineStart: inclusive line index
         * @param lineEnd: exclusive line index
         */
        suspend fun detectAnnotationInFile(document: Document, fileType: String?, lineStart: Int, lineEnd: Int): MutableList<Pair<Int, String>> {
            val outputList = mutableListOf<Pair<Int, String>>()
            val commentType = AnnotationInformation.instance!!.singleCommentMapping[fileType] ?: "//"
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


        /**
         * Detects an annotation in a string and returns the annotationtype
         */
        fun detectAnnotationInString(string: String, fileType: String?): String? {
            val commentType = AnnotationInformation.instance!!.singleCommentMapping[fileType] ?: "//"
            val keywords = AnnotationInformation.instance!!.keywords
            val regexes = keywords.map { x ->  getAnnotationRegex(commentType, x) }
            val regexEnd = getAnnotationRegex(commentType, "end")

            if (string.contains(regexEnd)) return "end"

            for ((j, regex) in regexes.withIndex()) {
                if (string.contains(regex)) {
                    return keywords[j]
                }
            }
            return null
        }

        fun detectNewProjectThing(string: String, fileType: String?): Pair<String, String> {
            val commentType = AnnotationInformation.instance!!.singleCommentMapping[fileType] ?: "//"
            val keywords = AnnotationInformation.instance!!.keywords
            val regexes = keywords.map { x ->  getFileJumpRegex(commentType, x) }

            for ((j, regex) in regexes.withIndex()) {
                val sequence: CharSequence = string
                val match = regex.find(sequence, 0)
                if (match == null) continue
                val split = match.value.split(":").toMutableList()
                split[1] = split[1].replace(" ", "")
                split[2] = split[2].replace(" ", "")
                return Pair(split[1], split[2])
            }
            return Pair("", "")
        }

        /**
         * Note line numbers from 0 to n-1 number of lines
         * @param document: document to be searched through
         * @param id: search string
         */
        suspend fun detectIdInFile(document: Document, id: String): Int {
            val regex = Regex(id, RegexOption.IGNORE_CASE)
            val lineStart = 0
            val lineEnd = document.lineCount-1
            yield()
            for (i in lineStart..lineEnd) {

                val startOffset = document.getLineStartOffset(i)
                val endOffset = document.getLineEndOffset(i)
                val line = document.getText(TextRange(startOffset, endOffset))

                if (line.contains(id, true)) {
                    return i
                }
                yield()
            }
            return -1
        }


        /**
         * Generates a regex pattern for a given [annotationType] and [commentType].
         * @param commentType The comment syntax used in the document.
         * @param annotationType The type of annotation.
         * @return The generated [Regex] pattern for the annotation.
         */
        private fun getAnnotationRegex(commentType: String, annotationType: String): Regex {
            return Regex("^(\\h)*${Regex.escape(commentType)}(\\h)*${Regex.escape(annotationType)}($|\\s)", RegexOption.IGNORE_CASE)
        }

        /**
         * Generates a jump target regex for a given [annotationType] and [commentType].
         * @param commentType The comment syntax used in the document.
         * @param annotationType The type of annotation.
         * @return The generated [Regex] pattern for the annotation.
         */
        private fun getFileJumpRegex(commentType: String, annotationType: String): Regex {
            return Regex("^(\\h)*${Regex.escape(commentType)}(\\h)*${Regex.escape(annotationType)}(\\h)+(:)(\\h)+(\\S)+(\\h)+(:)(\\h)+(\\S)+($|\\s)*", RegexOption.IGNORE_CASE)
        }

        /**
         * Checks if the [String] starts with the specified [commentType].
         * @param commentType The comment syntax to check for at the beginning of the string.
         * @return `true` if the string starts with the comment syntax, `false` otherwise.
         */
        private fun String.startsWithComment(commentType: String): Boolean {
            return this.trimStart().startsWith(commentType)
        }
    }
}

