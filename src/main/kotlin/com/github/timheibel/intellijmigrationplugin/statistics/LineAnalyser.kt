package com.github.timheibel.intellijmigrationplugin.statistics

import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern


class LineAnalyser {

    fun pathToFile() {
        //TODO: read files from Projekts and anylise lines
    }

    /// Counts lines of code including comments and returns the lines as String
    fun countLinesInFile(filePath: String): String? {
        try {
            BufferedReader(FileReader(filePath)).use { br ->
                var line: String?
                var loCString = ""
                var lOC = 0
                val regexPattern = ".*\\S|}"
                val pattern: Pattern = Pattern.compile(regexPattern)

                while (br.readLine().also { line = it } != null) {
                    // Analyze each line using the regex pattern
                    val matcher: Matcher = pattern.matcher(line)

                    if (matcher.matches()) {
                        lOC++
                        loCString += "$line\n"
                    } else {
                        // Optional: print or handle unmatched lines
                        // e.g., System.out.println("No match: " + line);
                    }
                }
                println(
                    """
                    Lines of Code: $lOC
                    $loCString
                    """.trimIndent()
                )
                return loCString
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}