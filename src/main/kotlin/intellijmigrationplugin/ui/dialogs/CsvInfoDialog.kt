package intellijmigrationplugin.ui.dialogs

import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import intellijmigrationplugin.statistics.component.CSVChooserComponent
import javax.swing.JComponent

class CsvInfoDialog: DialogWrapper(true) {

    private lateinit var data : CSVModel

    val project = ProjectManager.getInstance().openProjects[0]
    var csvChooserComponent = CSVChooserComponent(project)

    val nameComponent : String
        get() = data.nameComponent

    init {
        title = "CSV Information"
        init()
    }

    override fun createCenterPanel(): JComponent {


        val data = CSVModel()
        this.data = data

        return panel {
            group("CSV-Info") {
                row {
                    cell(csvChooserComponent.getComponent()).horizontalAlign(HorizontalAlign.FILL)
                }
                row("Enter csv Name:") {
                    textField().bindText(data :: nameComponent)
                }
            }
        }
    }
}
data class CSVModel(
    var nameComponent: String = ""
)

