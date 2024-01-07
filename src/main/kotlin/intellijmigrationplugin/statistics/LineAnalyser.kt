package intellijmigrationplugin.statistics

import com.intellij.openapi.application.ApplicationManager
import intellijmigrationplugin.annotationModel.AnnotationType
import intellijmigrationplugin.settings.MigrationSettingsState
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern

class LineAnalyser {



    private val settings: MigrationSettingsState
        get() {
            return ApplicationManager.getApplication().getService(MigrationSettingsState::class.java)
        }

    private var keywordsList: MutableList<String> = settings.keywordColorMapping.map { it.first }.toMutableList()
    private val regexPattern = ".*\\S|}"
    private val pattern: Pattern = Pattern.compile(regexPattern)
    private val fileStatisticMap: MutableMap<String, Int> = mutableMapOf<String,Int>()

    init {
        // Initialize map with default values
        keywordsList.forEach { keyword ->
            fileStatisticMap[keyword] = 0
        }
    }
    private fun resetFileStatisticMap() {
        fileStatisticMap.clear()
        keywordsList.forEach { keyword ->
            fileStatisticMap[keyword] = 0
        }
    }
    fun pathToFile(filePath: String) {
        //TODO: read files from Projects and analyse lines
        resetFileStatisticMap()
        val lOC = countLinesInFile(filePath)
        sortLOCbyLabel(filePath)

        //print statistic temporär
        val linesMigrated = fileStatisticMap[keywordsList[0]] ?: -1
        val linesLater = fileStatisticMap[keywordsList[1]] ?: -1
        val linesUnused = fileStatisticMap[keywordsList[2]] ?: -1
        val unmarked = lOC - (linesLater  + linesUnused + linesMigrated)
        val percent= 100 - ((unmarked/lOC).toDouble() * 100)
        println("lOC: $lOC $keywordsList[0] : $linesMigrated , $linesLater , $linesUnused, $unmarked")
        println("$percent % out of 100%")
    }

    //TODO: filter out comments and imports
    private fun countLinesInFile(filePath: String): Int {
        try {
            BufferedReader(FileReader(filePath)).use { br ->
                var line: String?
                var lOC = 0

                while (br.readLine().also { line = it } != null) {
                    // Analyze each line using the regex pattern
                    val matcher: Matcher = pattern.matcher(line!!)
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


    private fun sortLOCbyLabel(filePath: String){
        val segments = mutableMapOf<String, Pair<String, Int>>()
        var segmentIndex = 0

        try {
            File(filePath).useLines { lines ->
                var currentSegmentKey: String? = null
                val currentSegment = StringBuilder()

                lines.forEach { line ->
                    if (currentSegmentKey == null) {
                        //TODO: not annotation type anymore
                        val foundStartKeyword : String = keywordsList.find { line.contains(it) }.toString()

                        if (foundStartKeyword != "null") {
                            currentSegmentKey = foundStartKeyword
                            return@forEach
                        }
                    } else if (line.contains("END")) {
                        val segmentKey = "${currentSegmentKey!!}-$segmentIndex"
                        val segmentContent = currentSegment.toString()

                        val linesOfCode = countLinesInSegment(segmentContent)
                        val tmpInt: Int = fileStatisticMap[currentSegmentKey]!! + linesOfCode
                        fileStatisticMap[currentSegmentKey!!] = tmpInt

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
                    val segmentKey = "${currentSegmentKey!!}-$segmentIndex"
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


    private fun countLinesInSegment(segment: String): Int {
        var lOCSegment = 0

        segment.lines().forEach { line ->
            val matcher: Matcher = pattern.matcher(line)
            if (matcher.matches()) {
                lOCSegment++
            }
        }
        return lOCSegment
    }
}

