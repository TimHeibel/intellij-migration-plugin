package intellijmigrationplugin.statistics

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

class CSVEditor {
    // filename, keyword1, keyword..., keyword, sum
    // name, 0, 6,4


    fun createCSVFile( keywords: List<String>, csvName: String, csvPath: String ): String{

        val filePath = "$csvPath/$csvName.csv"
        val file = File(filePath)
        val headers = mutableListOf("filename")

        keywords.forEach { keyword ->
            headers.add(keyword)
        }
        headers.add("UNMARKED")
        headers.add("sum")

        file.bufferedWriter().use { writer ->
            writer.write(headers.joinToString(","))
        }
        return filePath
    }

    fun addLine(values: MutableMap<String, Int>, csvPath: String, fileName: String): String {

        val line = StringBuilder("$fileName,")
        var sum = 0
        for ((keyword, value) in values) {
            sum += value
            line.append("$value,")
        }
        line.append("$sum")
        val lineStr = line.toString()

        try{
            val fileWriter = FileWriter(csvPath, true)
            val bufferedWriter = BufferedWriter(fileWriter)

            bufferedWriter.newLine()
            bufferedWriter.write(lineStr)

            bufferedWriter.close()
            fileWriter.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return lineStr
    }

    fun endLine(csvPath: String){

        val inputFile = File(csvPath)
        var sumRow = "Total,"

        inputFile.bufferedReader().use {
            val lines = inputFile.readLines()

            val columnSums = MutableList(lines[0].split(",").size) { 0 }
            for (line in lines) {
                val values = line.split(",")
                for ((index, value) in values.withIndex()) {
                    columnSums[index] += value.toIntOrNull() ?: 0
                }
            }
            columnSums.remove(0)
            sumRow += columnSums.joinToString(",")
        }
        try{
            val fileWriter = FileWriter(csvPath, true)
            val bufferedWriter = BufferedWriter(fileWriter)

            bufferedWriter.newLine()
            bufferedWriter.write(sumRow)

            bufferedWriter.close()
            fileWriter.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}