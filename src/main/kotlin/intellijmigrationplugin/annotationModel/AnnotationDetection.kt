package intellijmigrationplugin.annotationModel

import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange


class AnnotationDetection {
    companion object {
        fun detectAnnotationInFile(document: Document, fileType: String?) : ArrayList<AnnotationSnippet> {

            val outputList = ArrayList<AnnotationSnippet>()


            val commentType = AnnotationInformation.instance!!.singleCommentMapping[".$fileType"]
                ?: "//"

            val annotations = AnnotationInformation.instance!!.markerColorMapping.keys

            val regexMapping =
                    annotations.map {
                        annotation -> getAnnotationRegex(commentType,annotation)
                    }

            val regexEnd      = getAnnotationRegex(commentType, "END")

            var line: String
            var currAnnotation = ""
            var annotationActive = false

            var annotationStartLine = 0
            var lineIndex = 0
            while(lineIndex < document.lineCount) {
                line = document
                    .getText(TextRange(document.getLineStartOffset(lineIndex),
                        document.getLineEndOffset(lineIndex)))
                if (!line.startsWith(commentType)) {
                    lineIndex++
                    continue
                }
                if(!annotationActive) {
                    for(regexPair in regexMapping) {
                        if(regexPair.second.matches(line)) {
                            annotationActive = true
                            currAnnotation = regexPair.first
                            annotationStartLine = lineIndex
                            break
                        }
                    }
                } else {
                    for(regexPair in regexMapping) {
                        if(regexPair.second.matches(line)) {
                            outputList.add(AnnotationSnippet(annotationStartLine, --lineIndex,
                                    false, currAnnotation))
                            annotationActive = false
                            break
                        }
                    }

                    if(annotationActive && regexEnd.second.matches(line)) {
                        outputList.add(AnnotationSnippet(annotationStartLine, lineIndex,
                                true, currAnnotation))
                        annotationActive = false
                    }
                }

                    lineIndex++
                }

                if (annotationActive) {
                    outputList.add(AnnotationSnippet(annotationStartLine, --lineIndex,
                        false, currAnnotation))
                }

            return outputList
        }

        private fun getAnnotationRegex(commentType: String, annotationType: String) : Pair<String, Regex> {
            return Pair(annotationType, Regex("${Regex.escape(commentType)}(\\s)*${annotationType}.*"))
        }
    }
}