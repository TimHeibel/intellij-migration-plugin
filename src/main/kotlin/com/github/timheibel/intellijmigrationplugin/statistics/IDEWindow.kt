package com.github.timheibel.intellijmigrationplugin.statistics

import com.github.timheibel.intellijmigrationplugin.services.MyProjectService
import com.intellij.openapi.components.service
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


        private val service = statisticsWindow.project.service<MyProjectService>()
        fun getContent() = JBPanel<JBPanel<*>>().apply {

            var label = JBLabel("This is a Button that does nothing")
            val label2 = JBLabel("\n")
            add(label)
            add(label2)
            add(JButton("Select File").apply {
                // button opens a File selector
                //TODO: multiple files
                addActionListener {
                    val fileChooser =  JFileChooser()
                    fileChooser.showOpenDialog(null)
                    val response = fileChooser.showSaveDialog(null)
                    // save filepath
                    if (response == JFileChooser.APPROVE_OPTION) {
                       val file = File(fileChooser.selectedFile.absolutePath)
                        println(file)
                        label2.text = file.absolutePath
                    }

                }
            })
        }
    }
}