package intellijmigrationplugin.annotationModel

import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange

class AnnotationDetection {
    companion object {
        fun detectAnnotationInFile(document: Document, fileType: String?) : ArrayList<AnnotationSnippet> {

            val outputList = ArrayList<AnnotationSnippet>()


            val commentType = AnnotationInformation.instance!!.commentTypeMapping[fileType]
                ?: "//"
            val regexMigrated = getAnnotationRegex(commentType, AnnotationType.MIGRATED.name)
            val regexLater    = getAnnotationRegex(commentType, AnnotationType.LATER.name)
            val regexUnused   = getAnnotationRegex(commentType, AnnotationType.UNUSED.name)
            val regexEnd      = getAnnotationRegex(commentType, "END")
            var line: String
            var currAnnotation = AnnotationType.UNMARKED
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
                if(currAnnotation == AnnotationType.UNMARKED) {
                    when {
                        regexMigrated.matches(line) -> {
                            currAnnotation = AnnotationType.MIGRATED
                            annotationStartLine = lineIndex
                        }
                        regexLater.matches(line) -> {
                            currAnnotation = AnnotationType.LATER
                            annotationStartLine = lineIndex
                        }
                        regexUnused.matches(line) -> {
                            currAnnotation = AnnotationType.UNUSED
                            annotationStartLine = lineIndex
                        }
                    }
                } else {
                    when {
                        regexMigrated.matches(line) || regexLater.matches(line) || regexUnused.matches(line) -> {
                            outputList.add(AnnotationSnippet(annotationStartLine, --lineIndex,
                                false, currAnnotation))
                            currAnnotation = AnnotationType.UNMARKED
                        }
                        regexEnd.matches(line) -> {
                            outputList.add(AnnotationSnippet(annotationStartLine, lineIndex,
                                true, currAnnotation))
                            currAnnotation = AnnotationType.UNMARKED
                        }
                    }
                }

                    lineIndex++
                }

                if (currAnnotation != AnnotationType.UNMARKED) {
                    outputList.add(AnnotationSnippet(annotationStartLine, --lineIndex,
                        false, currAnnotation))
                }

            return outputList
        }

        private fun getAnnotationRegex(commentType: String, annotationType: String) : Regex {
            return Regex("${Regex.escape(commentType)}(\\s)*${annotationType}.*")
        }
    }
}