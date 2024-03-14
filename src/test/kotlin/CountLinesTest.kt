import intellijmigrationplugin.statistics.LineAnalyser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.util.regex.Pattern

class CountLinesTest {

    var lineAnalyser = LineAnalyser()

    @Test
    fun detectComments(){
        val pathname = "./src/test/resources/Main.java"
        var result = -1
        try {
            BufferedReader(FileReader(pathname)).use { br ->
                var line: String?
                val segment = StringBuilder()

                while (br.readLine().also { line = it } != null){

                    segment.append(line).append("\n")
                }
                val pattern = Pattern.compile("^(?!\\s*import)(?!\\s*\\/\\/).*[^\\s]$", 8)
                val fileInformation = arrayOf("import", "//", "\\/\\/", "/*", "*/")
                result = lineAnalyser.countLinesInSegment(segment.toString(), pattern, fileInformation)
            }
        }catch (e: IOException){
            e.printStackTrace()
        }
        val expected = 6
        Assertions.assertEquals(expected, result)


    }

    @Test
    fun lineEqualsCheck(){
        val pathname = "./src/test/resources/Main.java"
        val keyword = "//MIGRATED"
        val fileInformation = arrayOf("import", "//", "\\/\\/", "/*", "*/")
        var detectedKeywords = 0

        try {
            BufferedReader(FileReader(pathname)).use { br ->
                var line: String?



                while (br.readLine().also { line = it } != null){
                    val isKeyword = lineAnalyser.isValidKeyword(keyword,line!!, fileInformation)
                    if(isKeyword){
                        detectedKeywords++
                    }
                }

            }
        }catch (e: IOException){
            e.printStackTrace()
        }

        val expected = 2
        Assertions.assertEquals(expected, detectedKeywords)
    }

    @Test
    fun keywordDetections(){
        val segment = "//Migrated SE project 2023/24 "
        val keyword = "//MIGRATED"
        val fileInformation = arrayOf("import", "//", "\\/\\/", "/*", "*/")
        val isKeyword = lineAnalyser.isValidKeyword(keyword,segment, fileInformation)

        Assertions.assertEquals(true, isKeyword)

    }

    @Test
    fun wrongKeywords(){
        val segment = "// //Migrated SE project 2023/24"
        val keyword = "//MIGRATED"
        val fileInformation = arrayOf("import", "//", "\\/\\/", "/*", "*/")
        val isKeyword = lineAnalyser.isValidKeyword(keyword,segment, fileInformation)

        Assertions.assertEquals(false, isKeyword)
    }
}