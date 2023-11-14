package com.github.timheibel.intellijmigrationplugin.settings

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.ColorPicker
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.FormBuilder
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.event.TableModelEvent
import javax.swing.table.DefaultTableModel

class MigrationSettingsComponent {
    val panel: JPanel

    // Legacy Path
    private val legacyFolderTextField = TextFieldWithBrowseButton()
    private val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
    var legacyFolderPath: String
        get() = legacyFolderTextField.text
        set(value) {
            legacyFolderTextField.text = value
        }

    // Keyword Color Mapping
    var keywordColorMapping = mutableMapOf<String, JBColor>()
    private val colorLabels = mutableMapOf<String, JBLabel>()

    // FileType Comment Mapping
    var fileTypeCommentMapping = mutableMapOf<String, String>()
    private val tableModel = DefaultTableModel(arrayOf(arrayOf("", "")), arrayOf("Filetype", "Comment Type"))
    private val table = JBTable(tableModel)
    private val tableScrollPane = JBScrollPane(table)

    init {
        panel = FormBuilder.createFormBuilder()
            // Legacy Folder
            .addLabeledComponent(JBLabel("Legacy folder: "), legacyFolderTextField, 1, false).addSeparator(2)

            // Keyword Color Mapping
            .addLabeledComponent(createColorMappingPanel("MIGRATED"), JPanel(), 1, false)
            .addLabeledComponent(createColorMappingPanel("LATER"), JPanel(), 1, false)
            .addLabeledComponent(createColorMappingPanel("UNUSED"), JPanel(), 1, false).addSeparator(2)

            // Filetype-Comment Mapping
            .addLabeledComponentFillVertically("Filetype-Comment mapping", tableScrollPane)

            .panel

        configureLegacyFolderTextField()
        configureTableModelListener()
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

    // Keyword Color Mapping
    private fun createColorMappingPanel(keyword: String): JPanel {
        val colorMappingPanel = JPanel()
        val colorLabel = JBLabel(keyword)

        colorLabel.foreground = keywordColorMapping[keyword]
        colorLabels[keyword] = colorLabel

        val selectColorButton = JButton("Select Color")
        selectColorButton.addActionListener {
            val newColor = ColorPicker.showDialog(
                panel, "Choose Color for $keyword", keywordColorMapping[keyword], false, null, false
            )
            if (newColor != null) {
                keywordColorMapping[keyword] = JBColor(newColor, newColor)
                // Update the color label directly
                colorLabel.foreground = keywordColorMapping[keyword]
            }
        }

        colorMappingPanel.add(colorLabel)
        colorMappingPanel.add(selectColorButton)

        return colorMappingPanel
    }

    // Necessary to update the color labels when state is loaded
    fun updateColorLabels() {
        for (keyword in keywordColorMapping.keys) {
            val colorLabel = colorLabels[keyword]
            colorLabel?.foreground = keywordColorMapping[keyword]
            colorLabel?.repaint()
        }
    }

    // Filetype Comment Mapping
    private fun configureTableModelListener() {
        tableModel.addTableModelListener { event ->
            if (event.type == TableModelEvent.UPDATE) {
                handleTableModelUpdate()
            }
        }
    }

    private fun handleTableModelUpdate() {
        // Check if the last row is not empty, add a new empty row
        val lastRow = tableModel.rowCount - 1
        val lastFileType = tableModel.getValueAt(lastRow, 0) as? String
        val lastCommentType = tableModel.getValueAt(lastRow, 1) as? String
        if (!lastFileType.isNullOrBlank() || !lastCommentType.isNullOrBlank()) {
            tableModel.addRow(arrayOf("", ""))
        }

        // Remove empty rows and update fileTypeCommentMapping
        val rowsToRemove = mutableListOf<Int>()
        for (i in tableModel.rowCount - 2 downTo 0) {
            val fileType = tableModel.getValueAt(i, 0) as String
            val commentType = tableModel.getValueAt(i, 1) as String
            if (fileType.isEmpty() && commentType.isEmpty()) {
                tableModel.removeRow(i)
                rowsToRemove.add(i)
            } else {
                // Update fileTypeCommentMapping
                fileTypeCommentMapping[fileType] = commentType
            }
        }

        // Remove entries from fileTypeCommentMapping for the rows removed
        rowsToRemove.forEach {
            fileTypeCommentMapping.remove(tableModel.getValueAt(it, 0) as String)
        }
    }

}
