
import intellijmigrationplugin.statistics.CSVEditor
import org.junit.jupiter.api.Test

class CsvTest {
    private val csvEditor = CSVEditor()
    @Test
    fun createCSV(){

        val keywords = mutableListOf("MIGRATED", "LATER")

        val filePath = csvEditor.createCSVFile( keywords, "./testName.csv")
        
        val linesPerKeyword: MutableMap<String, Int> = mutableMapOf()
        linesPerKeyword["MIGRATED"] = 3
        linesPerKeyword["LATER"] = 1
        linesPerKeyword["Unmarked"] = 2
        val fileName = "Main.java"

        csvEditor.addLine(linesPerKeyword, filePath, fileName)
    }

}