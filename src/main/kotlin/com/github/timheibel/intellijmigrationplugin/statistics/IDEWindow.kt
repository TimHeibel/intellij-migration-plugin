package com.github.timheibel.intellijmigrationplugin.statistics

import com.github.timheibel.intellijmigrationplugin.MyBundle
import com.github.timheibel.intellijmigrationplugin.services.MyProjectService
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import javax.swing.JButton

class IDEWindow : ToolWindowFactory {

    init {

    }
    override fun createToolWindowContent(project: Project, statisticsWindow: ToolWindow){
        val myStatisticsWindow = MyStatisticsWindow(statisticsWindow)
        val content = ContentFactory.getInstance().createContent(myStatisticsWindow.getContent(), null, false)
        statisticsWindow.contentManager.addContent(content)
    }
    override fun shouldBeAvailable(project: Project) = true

    class MyStatisticsWindow(statisticsWindow: ToolWindow) {

        private val service = statisticsWindow.project.service<MyProjectService>()

        fun getContent() = JBPanel<JBPanel<*>>().apply {
            val label = JBLabel("This is a Button that does nothing")

            add(label)
            add(JButton("Button")).apply {

            }
        }
    }
}