package intellijmigrationplugin.statistics.component

import intellijmigrationplugin.annotationModel.AnnotationInformation
import intellijmigrationplugin.statistics.CSVEditor
import intellijmigrationplugin.statistics.LineAnalyser
import java.io.File
import javax.swing.JButton

class RunStatisticComponent(private val fileChooserComponent: FileChooserComponent, private val annotationInformation: AnnotationInformation, private val csvFileComponent: CSVFileComponent) {

    private val csvEditor = CSVEditor()
    private val lineAnalyser = LineAnalyser()
    private val includeFilesList = mutableListOf<String>()

    fun runStatisticButton(): JButton{
        val statisticButton = JButton("run Statistic").apply {
            addActionListener {

                val legacyFile = File(annotationInformation.legacyFolderPath)
                val fileConstrains = FileConstrains()
                getFileConstrains(fileConstrains)
                if(!checkSpecialCases(fileConstrains,legacyFile)){
                    walkThoughFileTree(fileConstrains, legacyFile)
                }
                //TODO: conformation Pop-up
                //TODO: analyse Lines with includeFilesList

                val keywords = annotationInformation.keywords
                val csvPath = csvEditor.createCSVFile(keywords, annotationInformation.legacyFolderPath)
                for (filePath in includeFilesList){
                    lineAnalyser.getFileStatistic(filePath, csvPath)
                }
                csvFileComponent.addLink(csvPath)

                includeFilesList.clear()
                println("Processing complete.")
            }
        }
        return statisticButton
    }

    /**
     * This Methode reads the fileIgnore and safes all Constrains in the FileConstrains class
     * As well as the excludedFolderList from the settings.
     */
    private fun getFileConstrains(fileConstrains: FileConstrains): FileConstrains{
        val fileIgnoreList = File(fileChooserComponent.fileIgnorePath).readLines()
        fileConstrains.excludedFolderList = annotationInformation.excludedFolderList
        for (line in fileIgnoreList) {
            if (line.startsWith("#") || line.isBlank()) continue // Ignore comments
            if (line.startsWith("!")) {

                val substring = line.substring(1)

                when {
                    //folder
                    !substring.contains(".") -> {
                        fileConstrains.includedFoldersList.add(substring)
                        continue
                    }
                    //Endings
                    substring.startsWith(".") -> {
                        fileConstrains.includedEndingsList.add(substring.substring(1))
                        continue
                    }
                    //Files
                    else -> {
                        fileConstrains.includedFileList.add(substring)
                        continue
                    }
                }
            }

            when {
                //ending
                line.startsWith(".") -> {
                    fileConstrains.excludedEndings.add(line.substring(1))
                    continue
                } //file
                line.contains(".") -> {
                    fileConstrains.excludedFilesList.add(line)
                    continue
                } //folder
                else -> fileConstrains.excludedFolderNamesList.add(line)
            }
        }
        return fileConstrains
    }

    private fun checkSpecialCases(fileConstrains: FileConstrains, legacyFile: File): Boolean{
        if(fileConstrains.includedEndingsList.contains("*") || fileConstrains.includedFileList.contains("*.") || fileConstrains.includedFoldersList.contains("*")){
            processIncludedFolder(fileConstrains, legacyFile)
            return true
        }
        if(fileConstrains.excludedFolderNamesList.contains("*") || fileConstrains.excludedFilesList.contains("*.") || fileConstrains.excludedEndings.contains("*")){
            processExcludedDirectory(fileConstrains, legacyFile)
            return true
        }
        return false
    }

    /**
     * walks through FileTree and adds every file that should be analysed into the @includeFilesListList
     */
    //TODO: add .*
    private fun walkThoughFileTree(fileConstrains: FileConstrains, file: File){

        if (fileConstrains.excludedFolderList.contains(file.path))  return

        val dirName = file.path.substringAfterLast("/")
        
       if(file.isDirectory){
           if( fileConstrains.excludedFolderNamesList.any { it.equals(dirName, ignoreCase = true) }){
               processExcludedDirectory(fileConstrains, file)
               return
           }
           if(fileConstrains.includedFoldersList.contains(dirName)){
               processIncludedFolder(fileConstrains, file)
               return
           }
           file.listFiles()?.forEach { subFile ->
               walkThoughFileTree(fileConstrains, subFile)
           }
       }
        if (file.isFile){
            val ending = dirName.substringAfterLast(".")
            //check is file is excluded via path, Name or ending
            if(fileConstrains.includedEndingsList.contains(dirName))
            if(fileConstrains.excludedFilesList.any{it.equals(dirName, ignoreCase = true)} || fileConstrains.excludedEndings.contains(ending)){
                return
            }
            includeFilesList.add(file.path)
        }
    }

    private fun processExcludedDirectory(fileConstrains: FileConstrains, directory: File){

        if (fileConstrains.excludedFolderList.contains(directory.path))  return

        val substring = directory.path.substringAfterLast("/")
        if(directory.isDirectory){
            if(fileConstrains.includedFoldersList.contains(substring)){
                processIncludedFolder(fileConstrains, directory)
                return
            }
            directory.listFiles()?.forEach{subFile ->
                processExcludedDirectory(fileConstrains, subFile)
            }
        }
        if(directory.isFile){
            //is included ending
            val ending = substring.substringAfterLast(".")
            if(fileConstrains.includedEndingsList.contains(ending)){
                includeFilesList.add(directory.path)
                return
            }
            //is included File
            if(fileConstrains.includedFileList.contains(substring)) includeFilesList.add(directory.path)
        }

    }

    private fun processIncludedFolder(fileConstrains: FileConstrains, directory: File){

        if (fileConstrains.excludedFolderList.contains(directory.path))  return

        if(directory.isFile){
            includeFilesList.add(directory.path)
            return
        }
        if(directory.isDirectory){
            directory.listFiles()?.forEach{subFile ->
                processIncludedFolder(fileConstrains, subFile)
            }
        }

    }

    /**
     * Safes all Constrains form the fileIgnore in different List, as well as the excludedFolderList from the settings
     */
    class FileConstrains{
        var excludedFolderList: List<String> = mutableListOf()
        val excludedFolderNamesList: MutableList<String> = mutableListOf()
        val excludedFilesList: MutableList<String> = mutableListOf()
        val excludedEndings: MutableList<String> = mutableListOf()

        val includedFoldersList: MutableList<String> = mutableListOf()
        val includedFileList: MutableList<String> = mutableListOf()
        val includedEndingsList: MutableList<String> = mutableListOf()
    }
}
