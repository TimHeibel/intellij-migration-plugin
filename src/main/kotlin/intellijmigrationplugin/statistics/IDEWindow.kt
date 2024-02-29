package intellijmigrationplugin.statistics

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.CollectionListModel
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import intellijmigrationplugin.annotationModel.AnnotationInformation
import java.io.File
import java.io.FileNotFoundException
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

/// This class is initializing a ToolWindow and adds the content from the MyStatisticsWindow class
class IDEWindow : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, statisticsWindow: ToolWindow) {
        val myStatisticsWindow = MyStatisticsWindow(statisticsWindow)
        val content = ContentFactory.getInstance().createContent(myStatisticsWindow.getContent(), "", false)
        statisticsWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    ///This class manages the content for myStatisticsWindow
    ///structure:
    class MyStatisticsWindow(private val statisticsWindow: ToolWindow) {

        private val lineAnalyser = LineAnalyser()
        private var annotationInformation = AnnotationInformation.instance
        private val legacyFolderPath = annotationInformation?.legacyFolderPath
        private val excludedLegacyFolders = annotationInformation?.excludedFolderList


        private val project = ProjectManager.getInstance().openProjects[0]
        private val fileAndFolderChooserComponent = FileAndFolderChooserComponent(project)
        private val includeFileAndFolderChooserComponent = FileAndFolderChooserComponent(project)

        private val csvTest = CSVEditor()

        data class DataModel(
            var fileEnding: String = "",
        )
        val data = DataModel()
        fun getContent(): JPanel {

            val contentPane: JPanel = panel {


                group("Exclude Folders") {

                    row("Enter File-Ending:") {
                        val textField = JTextField(15)
                        cell(textField)
                        val safeButton = JButton("safe").apply {
                            addActionListener{
                                filterFileByEnding(textField.text, fileAndFolderChooserComponent.excludedFoldersListModel, File(legacyFolderPath!!), true)
                            }
                        }
                        cell(safeButton)
                    }
                    row {
                        scrollCell(fileAndFolderChooserComponent.getComponent()).horizontalAlign(HorizontalAlign.FILL)
                            .comment("Specify folders to be excluded from statistics.")
                    }
                    row {
                        val excludeStatisticButton = JButton("run Statistic").apply {
                            addActionListener {
                                //List that should be excluded
                                val contentList = fileAndFolderChooserComponent.excludedFoldersListModel

                                if(executionPossible() != null){
                                    val keywords = annotationInformation?.keywords
                                    val csvPath = csvTest.createCSVFile(keywords!!)
                                    processFileOrDirectory(executionPossible()!!, contentList, true, csvPath)
                                }
                                updateStatistics()
                                println("Processing complete.")
                            }
                        }
                        cell(excludeStatisticButton)
                    }
                }

                group("Include Folders") {

                    row("Enter File-Ending:") {
                        val textField = JTextField(20)
                        cell(textField)
                        val safeButton = JButton("safe").apply {
                            addActionListener{
                                data.fileEnding = textField.text
                                filterFileByEnding(textField.text, includeFileAndFolderChooserComponent.excludedFoldersListModel, File(legacyFolderPath!!), false)
                            }
                        }
                        cell(safeButton)
                    }
                    row {
                        scrollCell(includeFileAndFolderChooserComponent.getComponent()).horizontalAlign(HorizontalAlign.FILL)
                            .comment("Specify folders to include in the Statistics.")
                    }
                    row {
                        val newButton = JButton("runStatistic").apply {
                            addActionListener {
                                // Code to execute when the button is clicked
                                val contentList = includeFileAndFolderChooserComponent.excludedFoldersListModel

                                val legacyPath = executionPossible()
                                if(legacyPath != null){
                                    //create CSV file
                                    val keywords = annotationInformation?.keywords
                                    val csvPath = csvTest.createCSVFile(keywords!!)
                                    processFileOrDirectory(legacyPath, contentList, false, csvPath)
                                }
                                //TODO: show CSV file in UI #38
                                updateStatistics()
                                println("Processing complete.")
                            }
                        }
                        cell(newButton)
                    }
                }
                group("Statistic"){
                    row {
                        //TODO: Issue 38
                        // Call the function initially to set the label text
                        updateStatistics()
                        cell(statisticLabel)
                    }
                }
            }
            return contentPane
        }
        private fun executionPossible(): File? {
            when (legacyFolderPath) {
                null -> {
                    //TODO: error pop-up
                    println("LEGACY PATH?????")
                    return null
                }
                else -> {
                    val legacyFolder = File(legacyFolderPath)
                    return legacyFolder
                }
            }
        }

        private fun filterFileByEnding(fileEnding: String, contentList: CollectionListModel<String>, file: File, excluded: Boolean) {
            try {
                if(excludedLegacyFolders!!.contains(file.path)) return
                if(contentList.contains(file.path)) return

                if (!excluded && file.isFile && file.path.endsWith(fileEnding))
                    includeFileAndFolderChooserComponent.excludedFoldersListModel.add(file.path)
                if(excluded && file.isFile && file.path.endsWith(fileEnding))
                    fileAndFolderChooserComponent.excludedFoldersListModel.add(file.path)

                if(file.isDirectory)
                    file.listFiles()?.forEach { subFile ->
                        filterFileByEnding(fileEnding, contentList, subFile, excluded)
                    }
                return
            } catch (e: FileNotFoundException) {
                println("File not found: ${e.message}")
            } catch (e: SecurityException) {
                println("Access denied: ${e.message}")
            } catch (e: Exception) {
                println("Error processing file or directory: ${e.message}")
            }

        }

        private val statisticLabel = JLabel("")
        private fun updateStatistics() {
            // Todo: #38
            statisticLabel.text = "update"
        }

        private fun processFileOrDirectory(file: File, excludedFolderFileList: CollectionListModel<String>, excluded: Boolean, csvPath: String) {
            try {

                if(excludedLegacyFolders!!.contains(file.path)) return

                if(!excluded && excludedFolderFileList.contains(file.path)){
                    processFolderFile(file, csvPath)
                }

                if(excluded && excludedFolderFileList.contains(file.path)) return


                if (file.isFile && excluded) lineAnalyser.getFileStatistic(file.absolutePath, csvPath)

                if(file.isDirectory)
                    file.listFiles()?.forEach { subFile ->
                        processFileOrDirectory(subFile, excludedFolderFileList, excluded, csvPath)
                    }
                return
            } catch (e: FileNotFoundException) {
                println("File not found: ${e.message}")
            } catch (e: SecurityException) {
                println("Access denied: ${e.message}")
            } catch (e: Exception) {
                println("Error processing file or directory: ${e.message}")
            }
        }
        private fun processFolderFile(file: File, csvPath: String) {


            if(excludedLegacyFolders!!.contains(file.path)) return

            if(file.isFile) lineAnalyser.getFileStatistic(file.absolutePath, csvPath)

            if (file.isDirectory )
                file.listFiles()?.forEach { subFile ->
                    processFolderFile(subFile, csvPath)
                }
        }
    }
}

