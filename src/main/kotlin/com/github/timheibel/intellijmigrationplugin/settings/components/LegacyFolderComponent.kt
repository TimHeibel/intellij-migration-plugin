package com.github.timheibel.intellijmigrationplugin.settings.components

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent

class LegacyFolderComponent() {

    val legacyFolderTextField = TextFieldWithBrowseButton()
    private val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
    var legacyFolderPath: String
        get() = legacyFolderTextField.text
        set(value) {
            legacyFolderTextField.text = value
        }
    fun getComponent(): JComponent {
        val component =  FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Legacy folder: "), legacyFolderTextField, 1, false)
            .panel
        configureLegacyFolderTextField()
        return component
    }

    private fun configureLegacyFolderTextField() {
        legacyFolderTextField.addActionListener { _ ->
            val chosenFiles = FileChooser.chooseFiles(descriptor, null, null)
            if (chosenFiles.isNotEmpty() && chosenFiles[0] != null) {
                legacyFolderTextField.text = chosenFiles[0].path
            }
        }
    }
}