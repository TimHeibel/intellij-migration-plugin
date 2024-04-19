package intellijmigrationplugin.statistics.component

import intellijmigrationplugin.annotationModel.AnnotationInformation
import intellijmigrationplugin.statistics.CSVEditor
import intellijmigrationplugin.statistics.LineAnalyser
import java.io.File
import javax.swing.JButton

class RunStatisticComponent(private val fileChooserComponent: FileChooserComponent, private val annotationInformation: AnnotationInformation, private val csvFileComponent: CSVFileComponent, private val csvChooserComponent: CSVChooserComponent, private val csvNameInputField: CSVNameInputField) {

    private val csvEditor = CSVEditor()
    private val lineAnalyser = LineAnalyser()
    private val includeFilesList = mutableListOf<String>()

    fun runStatisticButton(): JButton{
        val statisticButton = JButton("run Statistic").apply {
            addActionListener {

                val legacyFile = File(annotationInformation.legacyFolderPath)
                val fileConstraints = fileConstraints()
                getfileConstraints(fileConstraints)
                if(!checkSpecialCases(fileConstraints,legacyFile)){
                    walkThoughFileTree(fileConstraints, legacyFile)
                }
                //TODO: conformation Pop-up, determent fileName and path for saving
                //TODO: create pop-up
                val keywords = annotationInformation.keywords
                val csvPath = csvEditor.createCSVFile(keywords, csvNameInputField.csvName, csvChooserComponent.fileIgnorePath)
                for (filePath in includeFilesList){
                    lineAnalyser.getFileStatistic(filePath, csvPath)
                }
                csvFileComponent.addLink(csvPath)

                includeFilesList.clear()

            }
        }
        return statisticButton
    }

    /**
     * This Methode reads the fileIgnore and safes all Constrains in the fileConstraints class
     * As well as the excludedFolderList from the settings.
     */
    private fun getfileConstraints(fileConstraints: fileConstraints): fileConstraints{
        val fileIgnoreList = File(fileChooserComponent.fileIgnorePath).readLines()
        fileConstraints.excludedFolderList = annotationInformation.excludedFolderList
        for (line in fileIgnoreList) {
            if (line.startsWith("#") || line.isBlank()) continue // Ignore comments
            if (line.startsWith("!")) {

                val substring = line.substring(1)

                when {
                    //folder
                    !substring.contains(".") -> {
                        fileConstraints.includedFoldersList.add(substring)
                        continue
                    }
                    //Endings
                    substring.startsWith(".") -> {
                        fileConstraints.includedEndingsList.add(substring.substring(1))
                        continue
                    }
                    //Files
                    else -> {
                        fileConstraints.includedFileList.add(substring)
                        continue
                    }
                }
            }

            when {
                //ending
                line.startsWith(".") -> {
                    fileConstraints.excludedEndings.add(line.substring(1))
                    continue
                } //file
                line.contains(".") -> {
                    fileConstraints.excludedFilesList.add(line)
                    continue
                } //folder
                else -> fileConstraints.excludedFolderNamesList.add(line)
            }
        }
        return fileConstraints
    }

    private fun checkSpecialCases(fileConstraints: fileConstraints, legacyFile: File): Boolean{
        if(fileConstraints.includedEndingsList.contains("*") || fileConstraints.includedFileList.contains("*.") || fileConstraints.includedFoldersList.contains("*")){
            processIncludedFolder(fileConstraints, legacyFile)
            return true
        }
        if(fileConstraints.excludedFolderNamesList.contains("*") || fileConstraints.excludedFilesList.contains("*.") || fileConstraints.excludedEndings.contains("*")){
            processExcludedDirectory(fileConstraints, legacyFile)
            return true
        }
        return false
    }

    /**
     * walks through FileTree and adds every file that should be analysed into the @includeFilesListList
     */
    //TODO: add .*
    private fun walkThoughFileTree(fileConstraints: fileConstraints, file: File){

        if (fileConstraints.excludedFolderList.contains(file.path))  return

        val dirName = file.path.substringAfterLast("/")
        
       if(file.isDirectory){
           if( fileConstraints.excludedFolderNamesList.any { it.equals(dirName, ignoreCase = true) }){
               processExcludedDirectory(fileConstraints, file)
               return
           }
           if(fileConstraints.includedFoldersList.contains(dirName)){
               processIncludedFolder(fileConstraints, file)
               return
           }
           file.listFiles()?.forEach { subFile ->
               walkThoughFileTree(fileConstraints, subFile)
           }
       }
        if (file.isFile){
            val ending = dirName.substringAfterLast(".")
            //check is file is excluded via path, Name or ending
            if(fileConstraints.includedEndingsList.contains(dirName))
            if(fileConstraints.excludedFilesList.any{it.equals(dirName, ignoreCase = true)} || fileConstraints.excludedEndings.contains(ending)){
                return
            }
            includeFilesList.add(file.path)
        }
    }

    private fun processExcludedDirectory(fileConstraints: fileConstraints, directory: File){

        if (fileConstraints.excludedFolderList.contains(directory.path))  return

        val substring = directory.path.substringAfterLast("/")
        if(directory.isDirectory){
            if(fileConstraints.includedFoldersList.contains(substring)){
                processIncludedFolder(fileConstraints, directory)
                return
            }
            directory.listFiles()?.forEach{subFile ->
                processExcludedDirectory(fileConstraints, subFile)
            }
        }
        if(directory.isFile){
            //is included ending
            val ending = substring.substringAfterLast(".")
            if(fileConstraints.includedEndingsList.contains(ending)){
                includeFilesList.add(directory.path)
                return
            }
            //is included File
            if(fileConstraints.includedFileList.contains(substring)) includeFilesList.add(directory.path)
        }

    }

    private fun processIncludedFolder(fileConstraints: fileConstraints, directory: File){

        if (fileConstraints.excludedFolderList.contains(directory.path))  return

        if(directory.isFile){
            includeFilesList.add(directory.path)
            return
        }
        if(directory.isDirectory){
            directory.listFiles()?.forEach{subFile ->
                processIncludedFolder(fileConstraints, subFile)
            }
        }

    }

    /**
     * Safes all Constrains form the fileIgnore in different List, as well as the excludedFolderList from the settings
     */
    class fileConstraints{
        var excludedFolderList: List<String> = mutableListOf()
        val excludedFolderNamesList: MutableList<String> = mutableListOf()
        val excludedFilesList: MutableList<String> = mutableListOf()
        val excludedEndings: MutableList<String> = mutableListOf()

        val includedFoldersList: MutableList<String> = mutableListOf()
        val includedFileList: MutableList<String> = mutableListOf()
        val includedEndingsList: MutableList<String> = mutableListOf()
    }
}
