package intellijmigrationplugin.settings.components

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.emptyText
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent

class LegacyFolderComponent(private val project: Project) {
    private val legacyFolderTextField = TextFieldWithBrowseButton()
    private val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()

    var legacyFolderPath: String
        get() = legacyFolderTextField.text
        set(value) {
            legacyFolderTextField.text = value
        }

    fun getComponent(): JComponent {
        configureLegacyFolderTextField()
        return FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Path to Legacy folder: "), legacyFolderTextField, 1, false).panel
    }

    private fun configureLegacyFolderTextField() {
        legacyFolderTextField.emptyText.setText("Optional")
        legacyFolderTextField.addActionListener { handleFolderSelection() }
    }

    private fun handleFolderSelection() {
        val chosenFiles = FileChooser.chooseFiles(descriptor, project, null)
        if (chosenFiles.isNotEmpty()) {
            legacyFolderPath = chosenFiles.first().path
        }
    }
}
