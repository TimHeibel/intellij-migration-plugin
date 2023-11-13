package com.github.timheibel.intellijmigrationplugin.settings

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.ColorPicker
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import javax.swing.JButton
import javax.swing.JPanel

/**
 * Component for configuring migration settings.
 * Allows users to select a legacy folder using a file chooser.
 * Includes UI for keyword to color mapping.
 */
class MigrationSettingsComponent {
    val panel: JPanel

    // Legacy Path
    private val legacyFolderTextField = TextFieldWithBrowseButton()
    private val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()

    // Keywords and their associated colors
    private val keywordColorMapping = mutableMapOf(
        "MIGRATED" to JBColor.GREEN, "LATER" to JBColor.RED, "UNUSED" to JBColor.GRAY
    )

    init {
        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Legacy folder: "), legacyFolderTextField, 1, false)

            // Color Mapping
            .addSeparator(2).addLabeledComponent(createColorMappingPanel("MIGRATED"), JPanel(), 1, false)
            .addLabeledComponent(createColorMappingPanel("LATER"), JPanel(), 1, false)
            .addLabeledComponent(createColorMappingPanel("UNUSED"), JPanel(), 1, false)

            .addComponentFillVertically(JPanel(), 0).panel

        configureLegacyFolderTextField()
    }

    // Legacy Path
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

    // Keyword Color Mapping Panel
    private fun createColorMappingPanel(keyword: String): JPanel {
        val colorMappingPanel = JPanel()
        val colorLabel = JBLabel(keyword)
        colorLabel.foreground = keywordColorMapping[keyword]
        val selectColorButton = JButton("Select Color")
        selectColorButton.addActionListener {
            // Open ColorPicker dialog and update the color
            val newColor = ColorPicker.showDialog(
                panel, "Choose Color for $keyword", keywordColorMapping[keyword], false, null, false
            )
            if (newColor != null) {
                keywordColorMapping[keyword] = JBColor(newColor, newColor.darker())
                colorLabel.foreground = keywordColorMapping[keyword]
            }
        }

        colorMappingPanel.add(colorLabel)
        colorMappingPanel.add(selectColorButton)
        return colorMappingPanel
    }

}
