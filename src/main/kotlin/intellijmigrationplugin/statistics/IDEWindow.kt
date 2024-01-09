package intellijmigrationplugin.statistics

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsContexts.BorderTitle
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.ui.layout.migLayout.createLayoutConstraints
import intellijmigrationplugin.annotationModel.AnnotationInformation
import java.io.File
import java.io.FileNotFoundException
import javax.swing.JButton
import javax.swing.JFileChooser
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
        val legacyFolderPath = annotationInformation?.legacyFolderPath != null


        fun getContent() = panel {

            group("Exclude Folders") {
                row { cell() }
            }

            group("Include Folders"){
                row { cell() }
            }

        }
            /*TODO: two groups "exclude files" & "include files"

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
    }
}

