package intellijmigrationplugin.ui.dialogs

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.*
import intellijmigrationplugin.annotationModel.AnnotationInformation
import javax.swing.JComponent

class AnnotationDialog : DialogWrapper(true) {

    private lateinit var data : DataModel

    val annotationType : String
        get() = data.annotationType
    val annotationComment : String
        get() = data.annotationComment

    init {
        title = "Annotation Menu"
        init()
    }

    override fun createCenterPanel(): JComponent {

        val data = DataModel()
        this.data = data

        val annotations = AnnotationInformation.instance!!.markerColorMapping.keys

        return panel {
            row("Select Annotation-Type:") {
                comboBox(annotations).bindItem(data :: annotationType.toNullableProperty())
            }
            row("Enter Annotation-Comment:") {
                textField().bindText(data :: annotationComment)
            }
        }

    }
}





data class DataModel(
    var annotationType : String = "",
    var annotationComment: String = "",
)