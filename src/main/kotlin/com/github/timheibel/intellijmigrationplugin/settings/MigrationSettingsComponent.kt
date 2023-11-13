package com.github.timheibel.intellijmigrationplugin.settings

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import javax.swing.JPanel

/**
 * Component for configuring migration settings.
 * Allows users to select a legacy folder using a file chooser.
 */
class MigrationSettingsComponent {
    val panel: JPanel

    private val legacyFolderTextField = TextFieldWithBrowseButton()

    // Set up file chooser for legacy folder
    private val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()

    init {
        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Legacy folder: "), legacyFolderTextField, 1, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel

        configureLegacyFolderTextField()
    }

    private fun configureLegacyFolderTextField() {
        legacyFolderTextField.addActionListener { _ ->
            val chosenFiles = FileChooser.chooseFiles(descriptor, null, null)
            if (chosenFiles.isNotEmpty() && chosenFiles[0] != null) {
                legacyFolderTextField.text = chosenFiles[0].path
            }
        }
    }

    var legacyFolderPath: String
        get() = legacyFolderTextField.text
        set(value) {
            legacyFolderTextField.text = value
        }
}
