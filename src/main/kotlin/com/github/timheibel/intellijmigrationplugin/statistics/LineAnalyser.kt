package com.github.timheibel.intellijmigrationplugin.statistics

import com.github.timheibel.intellijmigrationplugin.annotationModel.AnnotationType
import com.github.timheibel.intellijmigrationplugin.annotationModel.AnnotationType.MIGRATED
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern

class LineAnalyser {

    var fileStatisticMap = initializeEnumMap()
    fun initializeEnumMap(): HashMap<AnnotationType, Int> {
        val enumMap = AnnotationType.values().associateWith { 0 }
        return HashMap(enumMap) // Explicitly converting to HashMap
    }

    val regexPattern = ".*\\S|}"
    val pattern: Pattern = Pattern.compile(regexPattern)
    fun pathToFile(filePath: String) {
        //TODO: read files from Projekts and anylise lines
        fileStatisticMap.clear()
        fileStatisticMap = initializeEnumMap()
        val LoC = countLinesInFile(filePath)
        SortLOCbyLabels(filePath)

        //print statistic
        val linesMigrated = fileStatisticMap[MIGRATED] ?: -1
        val linesLater = fileStatisticMap[AnnotationType.LATER] ?: -1
        val linesUnused = fileStatisticMap[AnnotationType.UNUSED] ?: -1
        val unmarked = LoC - (linesLater  + linesUnused + linesMigrated)
        val percent= 100 - ((unmarked/LoC).toDouble() * 100)
        println("LoC: MIGRATED: LATER: UNUSED: UNMARKED: \n $LoC $linesMigrated $linesLater $linesUnused $unmarked")
        println("$percent % out of 100%")
    }

    /// Counts lines of code including comments and returns the lines as String
    fun countLinesInFile(filePath: String): Int {
        try {
            BufferedReader(FileReader(filePath)).use { br ->
                var line: String?
                var loCString = ""
                var lOC = 0

                while (br.readLine().also { line = it } != null) {
                    // Analyze each line using the regex pattern
                    val matcher: Matcher = pattern.matcher(line)
                    if (matcher.matches()) {
                        lOC++
                    }
                }

                return lOC
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return -1
    }

    //TODO: edit function
    fun SortLOCbyLabels(filePath: String){
        val segments = mutableMapOf<String, Pair<String, Int>>()
        var segmentIndex = 0

        try {
            File(filePath).useLines { lines ->
                var currentSegmentKey: AnnotationType? = null
                val currentSegment = StringBuilder()

                lines.forEach { line ->
                    if (currentSegmentKey == null) {
                        val foundStartKeyword = AnnotationType.values().find { line.contains(it.name) }

                        if (foundStartKeyword != null) {
                            currentSegmentKey = foundStartKeyword
                            return@forEach
                        }
                    } else if (line.contains("END")) {
                        val segmentKey = "${currentSegmentKey!!.name}-$segmentIndex"
                        val segmentContent = currentSegment.toString()

                        val linesOfCode = countLinesInSegment(segmentContent)
                        fileStatisticMap[currentSegmentKey!!] = (fileStatisticMap[currentSegmentKey]!! + linesOfCode)

                        segments[segmentKey] = Pair(segmentContent, linesOfCode)

                        currentSegmentKey = null
                        currentSegment.clear()
                        segmentIndex++
                    } else {
                        currentSegment.append(line).append("\n")
                    }
                }
                // Handle the last segment if it doesn't end with "END"
                if (currentSegmentKey != null && currentSegment.isNotEmpty()) {
                    val segmentKey = "${currentSegmentKey!!.name}-$segmentIndex"
                    val segmentContent = currentSegment.toString()
                    val linesOfCode = countLinesInSegment(segmentContent)
                    segments[segmentKey] = Pair(segmentContent, linesOfCode)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // Print each segment with its line count
        segments.forEach { (key, value) ->
            println("Segment [$key]:\n${value.first}\nLines of Code: ${value.second}\n")
        }
        return
    }


    fun countLinesInSegment(segment: String): Int {
        var lOCsegment = 0

        segment.lines().forEach { line ->
            val matcher: Matcher = pattern.matcher(line)
            if (matcher.matches()) {
                lOCsegment++
            }
        }
        return lOCsegment
    }
}

