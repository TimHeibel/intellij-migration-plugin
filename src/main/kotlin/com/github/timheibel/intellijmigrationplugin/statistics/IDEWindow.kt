package com.github.timheibel.intellijmigrationplugin.statistics

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import java.io.File
import javax.swing.JButton
import javax.swing.JFileChooser

class IDEWindow : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, statisticsWindow: ToolWindow) {
        val myStatisticsWindow = MyStatisticsWindow(statisticsWindow)
        val content = ContentFactory.getInstance().createContent(myStatisticsWindow.getContent(), null, false)
        statisticsWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    class MyStatisticsWindow(statisticsWindow: ToolWindow) {

        private val lineAnalyserTest  = LineAnalyser()

        //private val service = statisticsWindow.project.service<MyProjectService>()
        fun getContent() = JBPanel<JBPanel<*>>().apply {

            val label = JBLabel("This is a Button opens a file selector")
            val label2 = JBLabel("\n")
            var filePath = ""

            add(label)
            add(label2)
            //path in annotationfile
            add(JButton("Select File").apply {
                // button opens a File selector
                //TODO: multiple files
                addActionListener {
                    val fileChooser =  JFileChooser()
                    fileChooser.showOpenDialog(null)
                    val response = fileChooser.showSaveDialog(null)
                    // save filepath
                    if (response == JFileChooser.APPROVE_OPTION) {
                       filePath = File(fileChooser.selectedFile.absolutePath).toString()
                        println(filePath)
                        lineAnalyserTest.countLinesInFile(filePath)
                        println("done")
                    }

                }
            })
        }
    }
}
