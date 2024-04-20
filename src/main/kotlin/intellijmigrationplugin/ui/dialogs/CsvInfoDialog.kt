package intellijmigrationplugin.ui.dialogs

import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import intellijmigrationplugin.statistics.component.CSVChooserComponent
import intellijmigrationplugin.statistics.component.CSVNameInputField
import javax.swing.JComponent

class CsvInfoDialog: DialogWrapper(true) {

    init {
        title = "CSV Information"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val project = ProjectManager.getInstance().openProjects[0]
        val csvNameComponent = CSVNameInputField()
        val csvChooserComponent = CSVChooserComponent(project)

        return panel {
            group("CSV-Info") {
                row {
                    cell(csvChooserComponent.getComponent()).horizontalAlign(HorizontalAlign.FILL)
                }
                row {
                    cell(csvNameComponent.getComponent()).horizontalAlign(HorizontalAlign.FILL)
                }
            }
        }
    }
}



