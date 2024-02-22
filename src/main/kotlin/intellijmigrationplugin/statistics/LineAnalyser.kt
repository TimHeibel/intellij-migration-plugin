package intellijmigrationplugin.statistics

import com.intellij.openapi.application.ApplicationManager
import intellijmigrationplugin.annotationModel.AnnotationInformation
import intellijmigrationplugin.settings.MigrationSettingsState
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern

/// This class gets a file and counts the LOC (countLinesInFile) and
// lines which are tags within the Annotations (sortByLabels)
class LineAnalyser {



    private val settings: MigrationSettingsState
        get() {
            return ApplicationManager.getApplication().getService(MigrationSettingsState::class.java)
        }

    private var annotationInformation = AnnotationInformation.instance
    //search keywords
    private var keywordsList: MutableList<String> = settings.keywordColorMapping.map { it.first }.toMutableList()
    private var detectKeywordsList: MutableList<String> = mutableListOf<String>()
    private var regexPattern = "^(?!\\s*import)(?!\\s*\\/\\/).*[^\\s]$"
    private var pattern: Pattern = Pattern.compile(regexPattern, 8)
    private var multiCommentStart = "/*"
    private var multiCommentEnd = "*/"
    //safe numbers of
    val fileStatisticMap: MutableMap<String, Int> = mutableMapOf<String,Int>()

    /*init {
        // Initialize map with default values
        keywordsList.forEach { keyword ->
            fileStatisticMap[keyword] = 0
        }
    }*/
    fun pathToFile(filePath: String) {

        //TODO: Add pathname to StatisticInformation
        println(filePath)
        //resetFileStatisticMap()
        fileStatisticMap.clear()
        setFileFilters(filePath)
        //TODO: add this to Statistic Information
        val lOC = countLinesInFile(filePath)
        //TODO: add ...
        sortLOCbyLabel(filePath)

        println(fileStatisticMap.toString())

    }

    /*private fun resetFileStatisticMap() {
        fileStatisticMap.clear()
        keywordsList.forEach { keyword ->
            fileStatisticMap[keyword] = 0
        }
    }*/

    //updates regex and keywordsMap according to the file type
    private fun setFileFilters(path: String){
        //update RegexPattern
        val fileExtension = "." + path.substringAfterLast('.', "")

        val importMapping = annotationInformation?.importMapping
        val singleCommentMapping = annotationInformation?.singleCommentMapping
        var importStatement: String = "import"
        var singleLineComment: String = "//"

        if (importMapping!!.containsKey(fileExtension)) {
            importStatement = importMapping[fileExtension]!!
        }
        if(singleCommentMapping!!.containsKey(fileExtension)){
            singleLineComment = singleCommentMapping[fileExtension]!!
        }
        val transformedStr = singleLineComment.map { "\\$it" }.joinToString("")
        this.regexPattern = "^(?!\\s*$importStatement)(?!\\s*$transformedStr).*[^\\s]\$"
        this.pattern = Pattern.compile(regexPattern, 8)

        //getting the correct multiline comment markers
        val multiCommentMapping = annotationInformation?.multiCommentMapping
        val multiComments = multiCommentMapping?.getOrDefault(fileExtension, "/* */")
        multiComments?.split(" ")?.let { parts ->
            if (parts.size == 2) {
                multiCommentStart = parts[0]
                multiCommentEnd = parts[1]
            }
        }

        //adding singleLineComment to keyword List for SortByLabel function
        initialiseKeywords(transformedStr)

    }
    private fun initialiseKeywords(transformedStr: String){
        keywordsList.forEach { keyword ->
            detectKeywordsList.add(transformedStr + keyword)
            fileStatisticMap[keyword] = 0
        }
        detectKeywordsList.add(transformedStr + "End")

    }

    private fun sortLOCbyLabel(filePath: String){
        val segments = mutableMapOf<String, Pair<String, Int>>()
        var segmentIndex = 0

        try {
            File(filePath).useLines { lines ->
                var currentSegmentKey: String? = null
                val currentSegment = StringBuilder()

                lines.forEach { line ->
                    //TODO: what if //MIGRATED in comment or multi-line comment?
                    if (currentSegmentKey == null) {
                        val foundStartKeyword : String = detectKeywordsList.find { line.contains(it, ignoreCase = true) }.toString()

                        if (foundStartKeyword != "null") {
                            currentSegmentKey = foundStartKeyword
                            return@forEach
                        }
                    } else if (line.contains(detectKeywordsList.last(), ignoreCase = true)) {
                        val segmentKey = "${currentSegmentKey!!}-$segmentIndex"
                        val segmentContent = currentSegment.toString()

                        val linesOfCode = countLinesInSegment(segmentContent)
                        //TODO: list has // infromt fileStatisticMap nicht
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

    private fun countLinesInFile(filePath: String): Int {
        try {
            BufferedReader(FileReader(filePath)).use { br ->
                var line: String?
                var lOC = 0
                var isComment = false

                while (br.readLine().also { line = it } != null){
                    // Analyze each line using the regex pattern
                    val matcher: Matcher = pattern.matcher(line)

                    if(!isComment && line!!.contains(multiCommentStart)){
                        isComment = true
                        continue
                    }else if(isComment && line!!.contains(multiCommentEnd)){
                        isComment = false
                        continue
                    }else  if (!(isComment || !matcher.matches())) {
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


    private fun countLinesInSegment(segment: String): Int {
        var lOCSegment = 0
        var isComment = false

        segment.lines().forEach { line ->
            val matcher: Matcher = pattern.matcher(line)
            if(!isComment && line!!.contains(multiCommentStart)){
                isComment = true
                return@forEach
            }else if(isComment && line!!.contains(multiCommentEnd)){
                isComment = false
                return@forEach
            }else  if (!(isComment || !matcher.matches())) {
                lOCSegment++
            }
        }
        return lOCSegment
    }
}

