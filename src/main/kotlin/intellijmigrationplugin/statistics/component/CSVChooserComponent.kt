package intellijmigrationplugin.statistics.component

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent

class CSVChooserComponent(private val project: Project) {
    private val fileChooserTextField = TextFieldWithBrowseButton()
    private val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()

    //TODO: add default file-ignore
    var fileIgnorePath: String
        get() = fileChooserTextField.text
        set(value) {
            fileChooserTextField.text = value
        }

    fun getComponent(): JComponent {
        configureLegacyFolderTextField()
        return FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Path to .file-ignore: "), fileChooserTextField, 1, false).panel
    }

    private fun configureLegacyFolderTextField() {
        fileChooserTextField.addActionListener { handleFolderSelection() }
    }

    private fun handleFolderSelection() {
        val chosenFiles = FileChooser.chooseFiles(descriptor, project, null)
        if (chosenFiles.isNotEmpty()) {
            fileIgnorePath = chosenFiles.first().path
        }
    }

}
