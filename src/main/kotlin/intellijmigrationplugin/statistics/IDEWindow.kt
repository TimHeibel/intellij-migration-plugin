package intellijmigrationplugin.statistics

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.progress.ModalTaskOwner.component
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.sun.java.accessibility.util.AWTEventMonitor.addActionListener
import intellijmigrationplugin.annotationModel.AnnotationInformation
import intellijmigrationplugin.settings.components.ExcludedFoldersComponent
import org.jetbrains.plugins.notebooks.visualization.outputs.resetOutputInlayCustomHeight
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
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
        var annotationInformation = AnnotationInformation.instance
        val legacyFolderPath = annotationInformation?.legacyFolderPath
        val excludedLagacyFolders = annotationInformation?.excludedFolderList

        private val project = ProjectManager.getInstance().openProjects[0]
        private val FileAndFolderChooserComponent = FileAndFolderChooserComponent(project)
        private val IncludeFileAndFolderChooserComponent = FileAndFolderChooserComponent(project)
        private val excludedFoldersComponent = ExcludedFoldersComponent(project)
        fun getContent(): JPanel {

            val contentPane: JPanel = panel {

                group("Exclude Folders") {
                    row {
                        scrollCell(FileAndFolderChooserComponent.getComponent()).horizontalAlign(HorizontalAlign.FILL)
                            .comment("Specify folders to be excluded from statistics.")
                    }
                    row {
                        val excludeStatisticButton = JButton("run Statistic").apply {
                            addActionListener {
                                //execute when the button is clicked
                                val contentList = FileAndFolderChooserComponent.excludedFoldersListModel
                                excludedLagacyFolders?.forEach { filePath ->
                                    if(!contentList.contains(filePath)) {
                                        contentList.add(filePath)
                                    }
                                }
                                val legacyFolder = File(legacyFolderPath)

                                // Iterate over files and folders in specified directory
                                legacyFolder.listFiles()?.forEach { file ->
                                    // Check if the file or folder should be excluded
                                    if (!contentList.items.contains(file.absolutePath)) {
                                        processFileOrDirectory(file)
                                    }
                                }

                                println("Processing complete.")
                            }
                        }
                        cell(excludeStatisticButton)
                    }
                }

                group("Include Folders") {
                    row {
                        scrollCell(IncludeFileAndFolderChooserComponent.getComponent()).horizontalAlign(HorizontalAlign.FILL)
                            .comment("comment")
                    }
                    row {
                        val newButton = JButton("runStatistic").apply {
                            addActionListener {
                                // Code to execute when the button is clicked
                                val contentList = IncludeFileAndFolderChooserComponent.excludedFoldersListModel

                                contentList.items.forEach { filePath ->
                                    val file = File(filePath)
                                    processFileOrDirectory(file)
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

                        // Call the function initially to set the label text
                        updateStatistics()
                        cell(statisticLabel)
                    }
                }
            }
            return contentPane
        }

        // Use a dynamic label to display the current statistics
        val statisticLabel = JLabel("")

        // Define a function to update the label text
        fun updateStatistics() {
            statisticLabel.text = lineAnalyser.fileStatisticMap.toString()
        }
        private fun processFileOrDirectory(file: File) {
            try {
                if (file.isFile) {
                    // Process the file
                    println(file.absolutePath)
                    lineAnalyser.pathToFile(file.absolutePath)
                } else if (file.isDirectory) {
                    // Recursively process each file/directory within this directory
                    file.listFiles()?.forEach { subFile ->
                        processFileOrDirectory(subFile)
                    }
                }
            } catch (e: FileNotFoundException) {
                println("File not found: ${e.message}")
            } catch (e: SecurityException) {
                println("Access denied: ${e.message}")
            } catch (e: Exception) {
                println("Error processing file or directory: ${e.message}")
            }
        }
            /*TODO: Update printstatement

            val label = JBLabel("Select files or directories for analysis:")
            add(label)

            add(JButton("Select Files").apply {
                addActionListener {
                    val fileChooser = JFileChooser().apply {
                        fileSystemView = javax.swing.filechooser.FileSystemView.getFileSystemView()

                        currentDirectory = fileSystemView.homeDirectory
                        fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES
                        isMultiSelectionEnabled = true
                    }

                    val response = fileChooser.showOpenDialog(null)
                    if (response == JFileChooser.APPROVE_OPTION) {
                        fileChooser.selectedFiles.forEach { file ->
                            processFileOrDirectory(file)
                        }
                        println("File selection complete.")
                    }
                }
            })*/


    }
}

