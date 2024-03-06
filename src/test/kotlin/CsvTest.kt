
import intellijmigrationplugin.statistics.CSVEditor
import org.junit.jupiter.api.Test

class CsvTest {
    val csvEditor = CSVEditor()
    @Test
    fun createCSV(){

        val keywords = mutableListOf("UNMARKED", "MIGRATED", "LATER")

        val filePath = csvEditor.createCSVFile( keywords, "/home/finnika/Documents/Uni/5 Semester/SE Projekt/intellij-migration-plugin")
        
        val linesPerKeyword: MutableMap<String, Int> = mutableMapOf()
        linesPerKeyword["UNMARKED"] = 2
        linesPerKeyword["MIGRATED"] = 3
        linesPerKeyword["LATER"] = 1
        val fileName = "Main.java"

        csvEditor.addLine(linesPerKeyword, filePath, fileName)
    }
}