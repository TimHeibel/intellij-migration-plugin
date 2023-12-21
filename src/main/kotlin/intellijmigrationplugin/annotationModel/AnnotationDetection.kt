package intellijmigrationplugin.annotationModel

import io.ktor.util.*
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class AnnotationDetection {
    companion object {
        fun detectAnnotationInFile(path: Path) : ArrayList<Pair<AnnotationType, Pair<Int,Int>>> {

            val outputList = ArrayList<Pair<AnnotationType, Pair<Int,Int>>>()

            try {
                val commentType = AnnotationInformation.instance!!.commentTypeMapping[path.extension]
                    ?: "\\"
                val regexMigrated = getAnnotationRegex(commentType, AnnotationType.MIGRATED.name)
                val regexLater    = getAnnotationRegex(commentType, AnnotationType.LATER.name)
                val regexUnused   = getAnnotationRegex(commentType, AnnotationType.UNUSED.name)
                val regexEnd      = getAnnotationRegex(commentType, "END")

                val reader = Files.newBufferedReader(path)

                var line = reader.readLine()

                var currAnnotation = AnnotationType.UNMARKED
                var annotationStartLine = 0

                var lineIndex = 0
                while(line != null) {
                    if (!line.startsWith("\\")) {
                        line = reader.readLine()
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
                                outputList.add(Pair(currAnnotation, Pair(annotationStartLine, --lineIndex)))
                                currAnnotation = AnnotationType.UNMARKED
                            }

                            regexEnd.matches(line) -> {
                                outputList.add(Pair(currAnnotation, Pair(annotationStartLine, lineIndex)))
                                currAnnotation = AnnotationType.UNMARKED
                            }
                        }
                    }

                    lineIndex++
                    line = reader.readLine()
                }
            } catch (e : IOException) {
                return ArrayList()
            }

            return outputList
        }

        private fun getAnnotationRegex(commentType: String, annotationType: String) : Regex {
            return Regex("${Regex.escape(commentType)}(\\s)*${annotationType}.*")
        }
    }
}