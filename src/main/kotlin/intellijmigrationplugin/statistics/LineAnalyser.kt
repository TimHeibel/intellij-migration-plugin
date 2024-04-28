package intellijmigrationplugin.statistics

import intellijmigrationplugin.annotationModel.AnnotationInformation
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern

class LineAnalyser {

    fun getFileStatistic(filePath: String, csvName: String): MutableMap<String,Int> {

        val csvEditor = CSVEditor()
        //get file specific information
        val annotationInformation = AnnotationInformation.instance!!
        val keywords = annotationInformation.keywords

        val fileInformation = getFileInformation(filePath, annotationInformation)

        val regex = setRegex(fileInformation)
        val statisticMap = analiseLines(filePath, regex, fileInformation, keywords)
        csvEditor.addLine(statisticMap, csvName, filePath)

        return statisticMap
    }

    fun getFileInformation(
        filePath: String,
        annotationInformation: AnnotationInformation): Array<String> {

        val fileInformationArray: Array<String> = Array(5){" "}
        //get String ending
        val fileExtension = "." + filePath.substringAfterLast('.', "")
        //0 = import
         fileInformationArray[0] = annotationInformation.importMapping[fileExtension] ?:annotationInformation.defaultImport


        //1 = single
        fileInformationArray[1] =
            annotationInformation.singleCommentMapping.getOrDefault(fileExtension, annotationInformation.defaultSingleComment)
        //is usable in strings
        fileInformationArray[2] = fileInformationArray[1].map { "\\$it" }.joinToString("")


        //2,3 multilineComments start and end
        val multiComments = annotationInformation.multiCommentMapping.getOrDefault(fileExtension, annotationInformation.defaultMultiComment)
        multiComments.split(" ").let { parts ->
            if (parts.size == 2) {
                fileInformationArray[3] = parts[0]
                fileInformationArray[4] = parts[1]
            }
        }

        return fileInformationArray
    }

    private fun editKeywordsList(keywordList: List<String>, fileInformation: Array<String>): MutableList<String>{
        val commentedKeywords = mutableListOf<String>()
        keywordList.forEach { keyword ->
            commentedKeywords.add(fileInformation[1] + keyword)
        }
        commentedKeywords.add(fileInformation[1] + "End")
        return commentedKeywords
    }

    fun setRegex(fileInformation: Array<String>): Pattern{

        // isLineOfCode?
        val transformedStr = fileInformation[2]
        val importStatement = fileInformation[0]
        val regexPattern = "^(?!\\s*$importStatement)(?!\\s*$transformedStr).*[^\\s]\$"
        val pattern= Pattern.compile(regexPattern, 8)

        return pattern
    }

    fun isValidKeyword(keyword: String, line: String, fileInformation: Array<String>): Boolean{
        val transformedKeyword = keyword.replace(fileInformation[1], fileInformation[2])
        val pattern = Pattern.compile("^\\s*$transformedKeyword\\b.*",2)
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
        val endKeyword = commentedKeywordsList.last()
        commentedKeywordsList.remove(endKeyword)
        val linesPerKeyword: MutableMap<String, Int> = mutableMapOf()
        val currentSegment = StringBuilder()

        for (keyword in keywords) {
            linesPerKeyword[keyword] = 0
        }
        linesPerKeyword["UNMARKED"] = 0

        try {
            BufferedReader(FileReader(filePath)).use { br ->
                var line: String?
                var isComment = false


                var currentSegmentKey: String? = null

                while (br.readLine().also { line = it } != null){

                    //split into StringSegments
                    //is multiline Comment

                    if (isComment){
                        if (line!!.contains(fileInformation[4])){
                            isComment = false
                        }
                        continue
                    }
                    if(line!!.contains(fileInformation[3])){
                        isComment = true
                        continue
                    }

                    //keyword detected
                    if (currentSegmentKey == null) {
                        val foundStartKeyword: String =
                            commentedKeywordsList.find { line!!.contains(it, ignoreCase = true) }.toString()
                        //check if keyword is in comment
                        val keyword = foundStartKeyword
                        if (isValidKeyword(keyword, line!!, fileInformation)) {
                            currentSegmentKey = foundStartKeyword

                            val segmentContent = currentSegment.toString()
                            val segmentLoC = countLinesInSegment(segmentContent, regex, fileInformation)
                            linesPerKeyword["UNMARKED"] = linesPerKeyword["UNMARKED"]!! + segmentLoC

                            currentSegment.clear()

                            continue
                        }
                    }
                    if (currentSegmentKey == null){
                        //append line to segment
                        currentSegment.append(line).append("\n")
                        continue
                    }
                    // //End detected and count line in Segment
                    if (line!!.contains(endKeyword, ignoreCase = true) && isValidKeyword(endKeyword, line!!, fileInformation)){
                        val currentKeyword = currentSegmentKey.removePrefix(fileInformation[1])
                        currentSegmentKey = null
                        val segmentContent = currentSegment.toString()
                        val segmentLoC = countLinesInSegment(segmentContent, regex, fileInformation)

                        linesPerKeyword[currentKeyword] = linesPerKeyword[currentKeyword]!! + segmentLoC

                        currentSegment.clear()

                    }
                    currentSegment.append(line).append("\n")

                }
                //last segment
                val segmentContent = currentSegment.toString()
                val segmentLoC = countLinesInSegment(segmentContent, regex, fileInformation)
                linesPerKeyword["UNMARKED"] = linesPerKeyword["UNMARKED"]!! + segmentLoC

            }

        }catch (e: IOException){
            e.printStackTrace()
        }

        return linesPerKeyword
    }

    fun countLinesInSegment(segment: String,regex: Pattern, fileInformation: Array<String>): Int {
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