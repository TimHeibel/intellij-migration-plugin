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
        fun getContent(): JPanel {

            val contentPane: JPanel = panel {

                group("File Chooser") {
                    row{
                        cell()
                    }
                }

                group("Exclude Folders") {
                    row {
                        scrollCell(fileAndFolderChooserComponent.getComponent()).horizontalAlign(HorizontalAlign.FILL)
                            .comment("Specify folders to be excluded from statistics.")
                    }
                    row {
                        val excludeStatisticButton = JButton("run Statistic").apply {
                            addActionListener {
                                //List that should be excluded
                                val contentList = fileAndFolderChooserComponent.excludedFoldersListModel
                                excludedLegacyFolders?.let { it1 -> contentList.add(it1) }

                                if(executionPossible() != null){
                                    processFileOrDirectory(executionPossible()!!, contentList, false)
                                }
                                updateStatistics()
                                println("Processing complete.")
                            }
                        }
                        cell(excludeStatisticButton)
                    }
                }

                group("Include Folders") {
                    row {
                        scrollCell(includeFileAndFolderChooserComponent.getComponent()).horizontalAlign(HorizontalAlign.FILL)
                            .comment("Specify folders to include in the Statistics.")
                    }
                    row {
                        val newButton = JButton("runStatistic").apply {
                            addActionListener {
                                // Code to execute when the button is clicked
                                val contentList = includeFileAndFolderChooserComponent.excludedFoldersListModel

                                if(executionPossible() != null){
                                    processFileOrDirectory(executionPossible()!!, contentList, true)
                                }

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

        private val statisticLabel = JLabel("")
        private fun updateStatistics() {
            statisticLabel.text = lineAnalyser.fileStatisticMap.toString()
        }

        private fun processFileOrDirectory(file: File, excludedFolderFileList: CollectionListModel<String>, included: Boolean) {
            try {
                if(excludedFolderFileList.contains(file.path) == included ){
                    if (file.isFile) {
                        println(file.absolutePath)
                        lineAnalyser.pathToFile(file.absolutePath)
                    } else if (file.isDirectory) {
                        // Recursively process each file/directory within this directory
                        file.listFiles()?.forEach { subFile ->
                            processFileOrDirectory(subFile, excludedFolderFileList, included)
                        }
                    }
                }else{
                    return
                }
            } catch (e: FileNotFoundException) {
                println("File not found: ${e.message}")
            } catch (e: SecurityException) {
                println("Access denied: ${e.message}")
            } catch (e: Exception) {
                println("Error processing file or directory: ${e.message}")
            }
        }
    }
}

