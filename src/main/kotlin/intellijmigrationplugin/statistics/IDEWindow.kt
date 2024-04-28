package intellijmigrationplugin.statistics

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import intellijmigrationplugin.annotationModel.AnnotationInformation
import intellijmigrationplugin.statistics.component.CSVFileComponent
import intellijmigrationplugin.statistics.component.FileChooserComponent
import intellijmigrationplugin.statistics.component.RunStatisticComponent
import javax.swing.JPanel


/// This class is initializing a ToolWindow and adds the content from the MyStatisticsWindow class
class IDEWindow : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, statisticsWindow: ToolWindow) {
        val myStatisticsWindow = MyStatisticsWindow()
        val content = ContentFactory.getInstance().createContent(myStatisticsWindow.getContent(), "", false)
        statisticsWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    /**
     * Manages the different UI Components for the Statistic window
     */
    class MyStatisticsWindow {

        private val project = ProjectManager.getInstance().openProjects[0]

        private var annotationInformation = AnnotationInformation.instance
        private val csvFileComponent = CSVFileComponent()
        private val fileChooserComponent = FileChooserComponent(project)
        private val runStatisticComponent = RunStatisticComponent(fileChooserComponent, annotationInformation!!,csvFileComponent)

        fun getContent(): JPanel {

            val contentPane: JPanel = panel {
                group ("FileIgnore"){

                    row {
                        cell(fileChooserComponent.getComponent()).horizontalAlign(HorizontalAlign.FILL)
                            .comment("Select the ignore File")
                    }
                    row {
                        cell(runStatisticComponent.runStatisticButton())
                    }
                }
                group("Statistic"){
                    row{
                        scrollCell(csvFileComponent.getComponent()).horizontalAlign(HorizontalAlign.FILL)
                            .comment("Statistic files will be shown here")
                    }
                }
            }
            return contentPane
        }
    }
}

