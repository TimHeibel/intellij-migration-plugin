
import intellijmigrationplugin.statistics.CSVEditor
import org.junit.jupiter.api.Test

class CsvTest {
    val csvEditor = CSVEditor()
    @Test
    fun createCSV(){

        val keywords = mutableListOf("MIGRATED", "LATER")

        val filePath = csvEditor.createCSVFile( keywords, "/home/finnika/Documents/Uni/5 Semester/SE Projekt/intellij-migration-plugin")
        
        val linesPerKeyword: MutableMap<String, Int> = mutableMapOf()
        linesPerKeyword["MIGRATED"] = 3
        linesPerKeyword["LATER"] = 1
        linesPerKeyword["Unmarked"] = 2
        val fileName = "Main.java"

        csvEditor.addLine(linesPerKeyword, filePath, fileName)
    }
    @Test
    fun createCSVEndLine(){
        csvEditor.endLine("/home/finnika/Documents/Uni/5 Semester/SE Projekt/intellij-migration-plugin/src/test/resources/newStatistics2024-03-14|15:39:44.csv")
    }
}