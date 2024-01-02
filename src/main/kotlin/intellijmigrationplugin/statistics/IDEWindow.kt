package intellijmigrationplugin.statistics

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import java.io.File
import java.io.FileNotFoundException
import javax.swing.JButton
import javax.swing.JFileChooser

class IDEWindow : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, statisticsWindow: ToolWindow) {
        val myStatisticsWindow = MyStatisticsWindow(statisticsWindow)
        val content = ContentFactory.getInstance().createContent(myStatisticsWindow.getContent(), "", false)
        statisticsWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    class MyStatisticsWindow(private val statisticsWindow: ToolWindow) {

        private val lineAnalyserTest = LineAnalyser()

        fun getContent() = JBPanel<JBPanel<*>>().apply {
            val label = JBLabel("Select files or directories for analysis:")
            add(label)

            add(JButton("Select Files").apply {
                addActionListener {
                    val fileChooser = JFileChooser().apply {
                        fileSystemView = javax.swing.filechooser.FileSystemView.getFileSystemView()
                        //TODO: set to legacy path
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
            })
        }

        private fun processFileOrDirectory(file: File) {
            try {
                if (file.isFile) {
                    // Process the file
                    println(file.absolutePath)
                    lineAnalyserTest.pathToFile(file.absolutePath)
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
