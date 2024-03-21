package intellijmigrationplugin.statistics.component

import intellijmigrationplugin.annotationModel.AnnotationInformation
import java.io.File
import javax.swing.JButton

class RunStatisticComponent(private val fileChooserComponent: FileChooserComponent, val annotationInformation: AnnotationInformation) {

    companion object{
        val includeFilesList = mutableListOf<String>()

    }

    fun runStatisticButton(): JButton{
        val statisticButton = JButton("run Statistic").apply {
            addActionListener {


                //TODO: exclude&include files after file-ignore

                val legacyFile = File(annotationInformation.legacyFolderPath)
                val fileConstrains = FileConstrains()
                getFileConstrains(fileConstrains)
                walkThoughFileTree(fileConstrains, legacyFile)
                //TODO: conformation Pop-up
                println("Processing complete.")
            }
        }
        return statisticButton
    }

    /**
     * This Methode reads the fileIgnore and safes all Constrains in the FileConstrains class
     * As well as the excludedFolderList from the settings.
     */
    private fun getFileConstrains(fileConstrains: FileConstrains){
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
                        fileConstrains.includedEndingsList.add(substring)
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
                    fileConstrains.excludedEndings.add(line)
                    continue
                } //file
                line.contains(".") -> {
                    fileConstrains.excludedFilesList.add(line)
                    continue
                } //folder
                else -> fileConstrains.excludedFolderNamesList.add(line)
            }
        }
    }

    /**
     * walks through FileTree and adds every file that should be analiesed into the @includeFilesListList
     */
    fun walkThoughFileTree(fileConstrains: FileConstrains, file: File){ 
        
        val dirName = file.path.substringAfterLast("/")
        
       if(file.isDirectory){
           
           if(fileConstrains.excludedFolderList.contains(file.path) || fileConstrains.excludedFolderNamesList.any { it.equals(dirName, ignoreCase = true) }){
               processExcludedDirectory(fileConstrains, file)
           }
           if(fileConstrains.includedFoldersList.contains(dirName)){
               //TODO: process included Folder
           }
           file.listFiles()?.forEach { subfile ->
               walkThoughFileTree(fileConstrains, subfile)
           }
       }
        if (file.isFile){
            val ending = dirName.substringAfterLast(".")
            //check is file is excluded via path, Name or ending
            if(fileConstrains.excludedFolderList.contains(file.path) || fileConstrains.excludedFilesList.any{it.equals(dirName, ignoreCase = true)} || fileConstrains.excludedEndings.contains(".$ending")){
                return
            }
            includeFilesList.add(file.path)
        }
    }
    
    fun processIncludedFolder(fileConstrains: FileConstrains, file: File){
        if(file.isFile){
            includeFilesList.add(file.path)
            return
        }
        file.listFiles()?.forEach { subfile ->
            includeFilesList.add(file.path)
        }
    }

    fun processExcludedDirectory(fileConstrains: FileConstrains, directory: File){

        val substring = directory.path.substringAfterLast("/")
        if(directory.isDirectory){
            if(fileConstrains.includedFoldersList.contains(substring)){
                //TODO: processe included Folder
            }
            directory.listFiles()?.forEach{subfile ->
                processExcludedDirectory(fileConstrains, subfile)
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

    /**
     * Safes all Constrains form the fileIgnore in different List, as well as the excludedFolderList from the settings
     */
    class FileConstrains(){
        var excludedFolderList: List<String> = mutableListOf()
        val excludedFolderNamesList: MutableList<String> = mutableListOf()
        val excludedFilesList: MutableList<String> = mutableListOf()
        val excludedEndings: MutableList<String> = mutableListOf()

        val includedFoldersList: MutableList<String> = mutableListOf()
        val includedFileList: MutableList<String> = mutableListOf()
        val includedEndingsList: MutableList<String> = mutableListOf()
    }
}
