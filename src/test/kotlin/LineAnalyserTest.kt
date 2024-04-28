
import intellijmigrationplugin.statistics.LineAnalyser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.regex.Pattern

class LineAnalyserTest {
    private var lineAnalyser = LineAnalyser()


    //test analiseLines()
    @Test
    fun analyseJavaFile(){
        val pathname = "src/test/resources/Main.java"

        val fileInformation = arrayOf("import", "//", "\\/\\/", "/*", "*/")
        val regex = Pattern.compile("^(?!\\s*import)(?!\\s*\\/\\/).*[^\\s]\$", 8)
        val keywords = mutableListOf("MIGRATED", "LATER")

        val result = lineAnalyser.analiseLines(pathname, regex, fileInformation, keywords)

        //expected result
        val linesPerKeyword: MutableMap<String, Int> = mutableMapOf()
        linesPerKeyword["UNMARKED"] = 2
        linesPerKeyword["MIGRATED"] = 3
        linesPerKeyword["LATER"] = 1

        for ((keyword, expected) in linesPerKeyword) {
            Assertions.assertEquals(expected, result[keyword])
            println("$keyword: $expected")
        }
    }
    @Test
    fun analysePhythonFile() {
        val pathname = "src/test/resources/PhythonTestCode.py"
        val fileInformation = arrayOf("import", "#", "\\#", "\"\"\"", "\"\"\"")
        val regex = Pattern.compile("^(?!\\s*import)(?!\\s*\\#).*[^\\s]\$", 8)
        val keywords = mutableListOf("MIGRATED", "LATER")

        val result = lineAnalyser.analiseLines(pathname, regex, fileInformation, keywords)

        //expected result
        val linesPerKeyword: MutableMap<String, Int> = mutableMapOf()
        linesPerKeyword["UNMARKED"] = 5
        linesPerKeyword["MIGRATED"] = 7
        linesPerKeyword["LATER"] = 0

        for ((keyword, expected) in linesPerKeyword) {
            Assertions.assertEquals(expected, result[keyword])
            println("$keyword: $expected")
        }

    }


    //test regex()
    @Test
    fun phythonRegex(){
        val informationArray = arrayOf("import", "#", "\\#", "\"\"\"", "\"\"\"")
        val result = lineAnalyser.setRegex(informationArray).toRegex().toString()
        //val expected = Pattern.compile("^(?!\\s*import)(?!\\s*\\#).*[^\\s]\$", 8)
        val expected = "^(?!\\s*import)(?!\\s*\\#).*[^\\s]\$"

        val resultBool = result == expected
        Assertions.assertEquals(true, resultBool)
    }

    //testing getFileInformation()
    @Test
    fun getFileInformationForPhython(){

        val pathname = "/src/test/resources/testFiles/PhythonTestCode.py"
        val importMapping: HashMap<String, String> = hashMapOf(
        ".*" to "import",
        ".py" to "import"
        )
        val singleCommentMapping: HashMap<String, String> = hashMapOf(
            ".*" to "//",
            ".py" to "#"
        )
        val multiCommentMapping: HashMap<String, String> = hashMapOf(
        ".*" to "/* */",
        ".py" to "\"\"\" \"\"\""
        )

        val fileInformation = lineAnalyser.getFileInformation(pathname, importMapping, singleCommentMapping, multiCommentMapping)
        val expected = arrayOf("import", "#", "\\#", "\"\"\"", "\"\"\"")

        for (i in 0..4) Assertions.assertEquals(expected[i], fileInformation[i])
    }

     @Test
     fun getDefaultFileInformation(){
         val lineAnalyser = LineAnalyser()

         val pathname = "src/test/resources/testFiles/PhythonTestCode.py"
         val importMapping: HashMap<String, String> = hashMapOf(
             ".*" to "import",
             ".kt" to "import"
         )
         val singleCommentMapping: HashMap<String, String> = hashMapOf(
             ".*" to "//",
             ".kt" to "#"
         )
         val multiCommentMapping: HashMap<String, String> = hashMapOf(
             ".*" to "/* */",
             ".kt" to "\"\"\" \"\"\""
         )

         val fileInformation = lineAnalyser.getFileInformation(pathname, importMapping, singleCommentMapping, multiCommentMapping)
         val expected = arrayOf("import", "//", "\\/\\/", "/*", "*/")

         for (i in 0..4) Assertions.assertEquals(expected[i], fileInformation[i])
     }

}