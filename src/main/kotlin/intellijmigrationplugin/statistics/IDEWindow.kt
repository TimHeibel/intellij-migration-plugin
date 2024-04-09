package intellijmigrationplugin.statistics

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import intellijmigrationplugin.annotationModel.AnnotationInformation
import intellijmigrationplugin.statistics.component.CSVFileCompoment
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

    ///This class manages the content for myStatisticsWindow
    ///structure:
    class MyStatisticsWindow() {

        private val lineAnalyser = LineAnalyser()
        private var annotationInformation = AnnotationInformation.instance
        private val legacyFolderPath = annotationInformation?.legacyFolderPath

        private val project = ProjectManager.getInstance().openProjects[0]

        private val fileChooserComponent = FileChooserComponent(project)
        private val runStatisticComponent = RunStatisticComponent(fileChooserComponent, annotationInformation!!)

        private val csvEditor = CSVEditor()
        private val csvFileComponent = CSVFileCompoment()

        fun getContent(): JPanel {

            val contentPane: JPanel = panel {

                group ("FileIgnore"){

                    row {
                        cell(fileChooserComponent.getComponent()).horizontalAlign(HorizontalAlign.FILL)
                            .comment("Select the ignore File")
                    }
                    row {
                        //TODO: analise a file ignore #37
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

