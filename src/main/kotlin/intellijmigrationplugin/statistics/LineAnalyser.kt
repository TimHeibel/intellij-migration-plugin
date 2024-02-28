package intellijmigrationplugin.statistics

import intellijmigrationplugin.annotationModel.AnnotationInformation
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern

class LineAnalyser {

    fun getFileStatistic(filePath: String): MutableMap<String,Int> {

        //get file specific information
        val annotationInformation = AnnotationInformation.instance
        val importMapping = annotationInformation!!.importMapping
        val singleCommentMapping = annotationInformation.singleCommentMapping
        val multiCommentMapping = annotationInformation.multiCommentMapping
        val keywords = annotationInformation.keywords

        val fileInformation = getFileInformation(filePath, importMapping, singleCommentMapping, multiCommentMapping)

        // set regex
        val regex = setRegex(fileInformation)

        //go through file
        val statisticMap = analiseLines(filePath, regex, fileInformation, keywords)
        println(statisticMap)

        return statisticMap
    }

    fun getFileInformation(
        filePath: String,
        importMapping: HashMap<String, String>?,
        singleCommentMapping: HashMap<String, String>?,
        multiCommentMapping: HashMap<String, String>?): Array<String> {

        //initialise Array
        val fileInformationArray: Array<String> = Array<String>(5){" "}
        //get String ending
        val fileExtension = "." + filePath.substringAfterLast('.', "")
        //0 = import
         fileInformationArray[0] = importMapping?.get(fileExtension) ?:"import"


        //1 = single
        fileInformationArray[1] = singleCommentMapping?.getOrDefault(fileExtension, "//")!!
        //is useble in strings
        fileInformationArray[2] = fileInformationArray[1].map { "\\$it" }.joinToString("")


        //2,3 multilineComments start and end
        val multiComments = multiCommentMapping?.getOrDefault(fileExtension, "/* */")
        multiComments?.split(" ")?.let { parts ->
            if (parts.size == 2) {
                fileInformationArray[3] = parts[0]
                fileInformationArray[4] = parts[1]
            }
        }

        return fileInformationArray
    }

    fun editKeywordsList(keywordList: List<String>, fileInformation: Array<String>): MutableList<String>{
        val commentedKeywords = mutableListOf<String>()
        keywordList.forEach { keyword ->
            commentedKeywords.add(fileInformation[1] + keyword)
        }
        commentedKeywords.add(fileInformation[1] + "End")
        return commentedKeywords
    }

    fun setRegex(fileInformation: Array<String>): Pattern{
        //TODO (implement function)

        // isLineOfCode?
        val transformedStr = fileInformation[2]
        val importStatement = fileInformation[0]
        val regexPattern = "^(?!\\s*$importStatement)(?!\\s*$transformedStr).*[^\\s]\$"
        val pattern= Pattern.compile(regexPattern, 8)

        return pattern
    }

    private fun isValidKeyword(keyword: String, line: String, fileInformation: Array<String>): Boolean{
        val transformedStr = fileInformation[2]
        val pattern = Pattern.compile("^\\s*$transformedStr$keyword\\b.*",2)
        val matcher: Matcher = pattern.matcher(line)
        return matcher.matches()
    }

    fun analiseLines(
        filePath: String,
        regex: Pattern,
        fileInformation: Array<String>,
        keywords: List<String>
    ): MutableMap<String, Int> {

        val commentedKeywordsList = editKeywordsList(keywords, fileInformation)
        val linesPerKeyword: MutableMap<String, Int> = mutableMapOf()

        for (keyword in keywords) {
            linesPerKeyword[keyword] = 0
        }
        linesPerKeyword["unmarked"] = 0

        try {
            BufferedReader(FileReader(filePath)).use { br ->
                var line: String?
                var isComment = false
                val currentSegment = StringBuilder()

                var currentSegmentKey: String? = null

                while (br.readLine().also { line = it } != null){

                    //split into StringSegments

                    //TODO: multiline-comments mix with code are not filtered out
                    //is multiline Comment

                    if (isComment){
                        if (line!!.contains(fileInformation[4])){
                            isComment = false
                        }
                        continue
                    }
                    if(!isComment && line!!.contains(fileInformation[3])){
                        isComment = true
                        continue
                    }

                    //keyword detected
                    if (currentSegmentKey == null) {
                        val foundStartKeyword: String =
                            commentedKeywordsList.find { line!!.contains(it, ignoreCase = true) }.toString()
                        //check if keyword is in comment
                        val keyword = foundStartKeyword.removePrefix("//")
                        if (isValidKeyword(keyword, line!!, fileInformation)) {
                            currentSegmentKey = foundStartKeyword

                            val segmentContent = currentSegment.toString()
                            val segmentLoC = countLinesInSegment(segmentContent, regex, fileInformation)
                            linesPerKeyword["unmarked"] = linesPerKeyword["unmarked"]!! + segmentLoC

                            currentSegment.clear()

                            continue
                        }
                    }  // //End detected and countline in Segment
                    if (line!!.contains(commentedKeywordsList.last(), ignoreCase = true) && isValidKeyword("End", line!!, fileInformation)){
                        val currentKeyword = currentSegmentKey!!.removePrefix("//")
                        currentSegmentKey = null
                        val segmentContent = currentSegment.toString()
                        val segmentLoC = countLinesInSegment(segmentContent, regex, fileInformation)

                        linesPerKeyword[currentKeyword] = linesPerKeyword[currentKeyword]!! + segmentLoC

                        currentSegment.clear()

                    }
                    //append line to segment
                    currentSegment.append(line).append("\n")

                }

            }

        }catch (e: IOException){
            e.printStackTrace()
        }

        return linesPerKeyword
    }

    private fun countLinesInSegment(segment: String,regex: Pattern, fileInformation: Array<String>): Int {
        var lOCSegment = 0
        var isComment = false

        segment.lines().forEach { line ->
            val matcher: Matcher = regex.matcher(line)
            if(!isComment && line.contains(fileInformation[3])){
                isComment = true
                return@forEach
            }else if(isComment && line.contains(fileInformation[4])){
                isComment = false
                return@forEach
            }else  if (!(isComment || !matcher.matches())) {
                lOCSegment++
            }
        }
        return lOCSegment
    }

}