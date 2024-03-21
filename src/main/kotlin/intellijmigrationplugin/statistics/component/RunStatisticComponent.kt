package intellijmigrationplugin.statistics.component

import intellijmigrationplugin.annotationModel.AnnotationInformation
import java.io.File
import javax.swing.JButton

class RunStatisticComponent(private val fileChooserComponent: FileChooserComponent, val annotationInformation: AnnotationInformation) {

    fun runStatisticButton(): JButton{
        val statisticButton = JButton("run Statistic").apply {
            addActionListener {


                //TODO: exclude&include files after file-ignore
                val includeFiles = mutableListOf<String>()
                val legacyFile = File(annotationInformation.legacyFolderPath)
                val fileConstrains = FileConstrains()
                getFileConstrains(fileConstrains)
                walkThoughFileTree(includeFiles, legacyFile)
                //TODO: conformation Pop-up
                println("Processing complete.")
            }
        }
        return statisticButton
    }


    /**
     * This Methode reads the fileIgnore and safes all Constrains in the fileConstrains class
     */
    private fun getFileConstrains(fileConstrains: FileConstrains){
        val fileIgnoreList = File(fileChooserComponent.fileIgnorePath).readLines()


        for (line in fileIgnoreList) {
            if (line.startsWith("#") || line.isBlank()) continue // Ignore comments

            if (line.startsWith("!")) {

                val substring = line.substring(1)
                //folder
                if(!substring.contains(".")){
                    fileConstrains.includedFoldersList.add(substring)
                    continue
                }
                //Endings
                if(substring.startsWith(".")){
                    fileConstrains.includedEndingsList.add(substring)
                    continue
                }
                //Files
                fileConstrains.includedFileList.add(substring)
                continue

            }
            //ending
            if(line.startsWith(".")){
                fileConstrains.excludedEndings.add(line)
                continue
            }
            //file
            if(line.contains(".")){
                fileConstrains.excludedFilesList.add(line)
                continue
            }
            //folder
           fileConstrains.excludedFolderNamesList.add(line)

        }




    }

    fun walkThoughFileTree(includeFiles: MutableList<String>, file: File){

       if(file.isDirectory){
           //check is directory excluded or included?
       }
        if (file.isFile){
            //check is file excluded?

            includeFiles.add(file.path)
        }

    }

    /**
     * Safes all Constrains form the fileIgnore in different List, as well as the excludedFolderList from the settings
     */
    companion object{

        val excludedFolderList: List<String> = mutableListOf()

    }

    class FileConstrains(){
        val excludedFolderNamesList: MutableList<String> = mutableListOf()
        val excludedFilesList: MutableList<String> = mutableListOf()
        val excludedEndings: MutableList<String> = mutableListOf()

        val includedFoldersList: MutableList<String> = mutableListOf()
        val includedFileList: MutableList<String> = mutableListOf()
        val includedEndingsList: MutableList<String> = mutableListOf()
    }

    fun processDirectory(directory: File){


    }
}
