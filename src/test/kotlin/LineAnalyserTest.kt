
import intellijmigrationplugin.statistics.LineAnalyser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class LineAnalyserTest {
    var lineAnalyser = LineAnalyser()


    //test analiseLines()
    @Test
    fun keywordsListPhython(){


    }
    @Test
    fun analysePhythonFile() {
        //for testing assertions

    }


    //test regex()
    @Test
    fun phythonRegex(){
        val informationArray = arrayOf("import", "#", "\\#", "\"\"\"", "\"\"\"")
        val result = lineAnalyser.setRegex(informationArray).toRegex().toString()
        //val expected = Pattern.compile("^(?!\\s*import)(?!\\s*\\#).*[^\\s]\$", 8)
        val expected = "^(?!\\s*import)(?!\\s*\\#).*[^\\s]\$"

        val resultBool = result.equals(expected)
        Assertions.assertEquals(true, resultBool)
    }

    //testing getFileInformation()
    @Test
    fun getFileInformationForPhython(){

        val pathname = "/home/finnika/Documents/Uni/5 Semester/SE Projekt/intellij-migration-plugin/src/test/resources/testFiles/PhythonTestCode.py"
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

         val pathname = "/home/finnika/Documents/Uni/5 Semester/SE Projekt/intellij-migration-plugin/src/test/resources/testFiles/PhythonTestCode.py"
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