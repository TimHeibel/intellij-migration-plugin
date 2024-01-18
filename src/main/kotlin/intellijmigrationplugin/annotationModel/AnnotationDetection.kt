package intellijmigrationplugin.annotationModel

import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import kotlinx.coroutines.*

class AnnotationDetection {
    companion object {
        fun detectAnnotationInFile(document: Document, fileType: String?): ArrayList<AnnotationSnippet> {

            val outputList = ArrayList<AnnotationSnippet>()

            val commentType = AnnotationInformation.instance!!.commentTypeMapping[fileType]
                    ?: "//"
            val keywords = AnnotationInformation.instance!!.keywords

            val regexes = keywords.map { x -> Regex("//\\s*$x(\$|\\s)", RegexOption.IGNORE_CASE) }
            val regexEnd = Regex("//\\s*end(\$|\\s)", RegexOption.IGNORE_CASE)

            var startLine = -1;
            var currentAnnotationString = ""

            for (i in 0..document.lineCount - 1) {

                val startOffset = document.getLineStartOffset(i)
                val endOffset = document.getLineEndOffset(i)
                val line = document.getText(TextRange(startOffset, endOffset))

                var j = 0
                for (regex in regexes) {
                    if (line.contains(regex)) {
                        if (startLine != -1) {
                            val snippet = AnnotationSnippet(startLine, i - 1, currentAnnotationString)
                            outputList.add(snippet)
                        }
                        startLine = i
                        currentAnnotationString = keywords[j]
                        break
                    }
                    j++
                }

                if (line.contains(regexEnd)) {
                    val snippet = AnnotationSnippet(startLine, i, currentAnnotationString)
                    outputList.add(snippet)
                    startLine = -1
                }
            }
            if (startLine != -1) {
                val snippet = AnnotationSnippet(startLine, document.lineCount - 1, currentAnnotationString)
                outputList.add(snippet)
            }

            //outputList.forEach { x -> println(x) }

            return outputList
        }

        /**
         * Note line numbers from 0 to n-1 number of lines
         * @param lineStart: inclusive line index
         * @param lineEnd: exclusive line index
         */
        suspend fun detectAnnotationInFile(document: Document, fileType: String?, lineStart: Int, lineEnd: Int): MutableList<Pair<Int, String>> {
            val outputList = mutableListOf<Pair<Int, String>>()
            val commentType = AnnotationInformation.instance!!.commentTypeMapping[fileType]
                    ?: "//"
            val keywords = AnnotationInformation.instance!!.keywords

            val regexes = keywords.map { x -> Regex("//\\s*$x(\$|\\s)", RegexOption.IGNORE_CASE) }
            val regexEnd = Regex("//\\s*end(\$|\\s)", RegexOption.IGNORE_CASE)
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
    }

    private fun getAnnotationRegex(commentType: String, annotationType: String): Regex {
        return Regex("${Regex.escape(commentType)}(\\s)*${annotationType}($|\\s)")
    }

}